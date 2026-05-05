package stats

import editors.models.Account
import kotlinx.datetime.LocalDate
import transactions.models.Transaction
import transactions.models.TransactionType
import utils.bigDecimalExtensions.div
import utils.bigDecimalExtensions.times
import utils.toLocalDate
import utils.types.BigDecimal
import kotlin.js.JsExport
import kotlin.time.Clock

@JsExport
data class StatsState(
    val accounts: List<StatAccount>,
    val selectedAccountId: String?,
    val period: StatPeriod,
    val metric: StatMetric,
    val summary: StatSummary,
    val dynamics: List<StatPoint>,
    val expenseDistribution: List<StatCategorySlice>,
)

@JsExport
data class StatAccount(
    val id: String,
    val title: String,
    val balance: BigDecimal,
)

@JsExport
enum class StatPresetPeriod {
    DAYS_30,
    MONTHS_3,
    MONTHS_6,
}

@JsExport
data class StatCustomPeriod(
    val from: LocalDate,
    val to: LocalDate,
)

@JsExport
data class StatPeriod(
    val preset: StatPresetPeriod?,
    val custom: StatCustomPeriod,
)

@JsExport
enum class StatMetric {
    INCOME,
    EXPENSE,
    BALANCE,
}

@JsExport
data class StatSummary(
    val income: BigDecimal,
    val expense: BigDecimal,
    val net: BigDecimal,
)

@JsExport
data class StatPoint(
    val timestamp: LocalDate,
    val value: BigDecimal,
)

@JsExport
data class StatCategorySlice(
    val categoryId: String,
    val categoryName: String,
    val amount: BigDecimal,
    val percent: Double,
    val colorHex: String?,
)

@JsExport
sealed class StatsIntent {
    data class SelectAccount(val accountId: String?) : StatsIntent()
    data class SelectPresetPeriod(val preset: StatPresetPeriod) : StatsIntent()
    data class SelectCustomPeriod(val fromIsoDate: String, val toIsoDate: String) : StatsIntent()
    data class SelectMetric(val metric: StatMetric) : StatsIntent()
}

internal object StatsCalculator {
    private val chartPalette = listOf(
        "#C3114F",
        "#3E7D68",
        "#5F9888",
        "#8E5AE8",
        "#F3A214",
        "#4F39F6",
        "#0EA5E9",
        "#EF4444",
    )

    fun initialState(now: LocalDate = Clock.System.now().toLocalDate()): StatsState {
        val customRange = StatCustomPeriod(
            from = shiftDays(now, -29),
            to = now
        )
        return StatsState(
            accounts = emptyList(),
            selectedAccountId = null,
            period = StatPeriod(
                preset = StatPresetPeriod.DAYS_30,
                custom = customRange
            ),
            metric = StatMetric.BALANCE,
            summary = StatSummary(
                income = BigDecimal.ZERO,
                expense = BigDecimal.ZERO,
                net = BigDecimal.ZERO
            ),
            dynamics = emptyList(),
            expenseDistribution = emptyList()
        )
    }

    fun parseCustomPeriod(
        from: String,
        to: String,
        fallback: StatCustomPeriod,
    ): StatCustomPeriod {
        val parsedFrom = runCatching { LocalDate.parse(from) }.getOrElse { fallback.from }
        val parsedTo = runCatching { LocalDate.parse(to) }.getOrElse { fallback.to }
        return if (parsedFrom <= parsedTo) {
            StatCustomPeriod(parsedFrom, parsedTo)
        } else {
            StatCustomPeriod(parsedTo, parsedFrom)
        }
    }

    fun buildState(
        selectedAccountId: String?,
        period: StatPeriod,
        metric: StatMetric,
        accounts: List<Account>,
        transactions: List<Transaction>,
        now: LocalDate = Clock.System.now().toLocalDate(),
    ): StatsState {
        val safeSelectedAccount = selectedAccountId.takeIf { id -> accounts.any { it.id == id } }
        val statAccounts = accounts.map { StatAccount(it.id, it.title, it.balance) }
        val activeRange = resolveRange(period, now)
        val filtered = transactions
            .asSequence()
            .filter { tx -> safeSelectedAccount == null || accountIdOf(tx) == safeSelectedAccount }
            .filter { tx -> tx.date in activeRange.from..activeRange.to }
            .toList()

        val income = filtered
            .asSequence()
            .filter { it.type is TransactionType.Income }
            .fold(BigDecimal.ZERO) { acc, tx -> acc + tx.value }
        val expense = filtered
            .asSequence()
            .filter { it.type is TransactionType.Outcome }
            .fold(BigDecimal.ZERO) { acc, tx -> acc + tx.value }
        val summary = StatSummary(
            income = income,
            expense = expense,
            net = income - expense
        )

        return StatsState(
            accounts = statAccounts,
            selectedAccountId = safeSelectedAccount,
            period = period,
            metric = metric,
            summary = summary,
            dynamics = buildDynamics(
                transactions = filtered,
                range = activeRange,
                metric = metric
            ),
            expenseDistribution = buildExpenseDistribution(filtered)
        )
    }

    private data class DateRange(
        val from: LocalDate,
        val to: LocalDate,
    )

    private fun resolveRange(period: StatPeriod, now: LocalDate): DateRange {
        return when (period.preset) {
            StatPresetPeriod.DAYS_30 -> DateRange(
                from = shiftDays(now, -29),
                to = now
            )

            StatPresetPeriod.MONTHS_3 -> {
                val currentMonthStart = LocalDate(now.year, now.monthNumber, 1)
                DateRange(
                    from = shiftMonths(currentMonthStart, -2),
                    to = now
                )
            }

            StatPresetPeriod.MONTHS_6 -> {
                val currentMonthStart = LocalDate(now.year, now.monthNumber, 1)
                DateRange(
                    from = shiftMonths(currentMonthStart, -5),
                    to = now
                )
            }

            null -> DateRange(
                from = minOf(period.custom.from, period.custom.to),
                to = maxOf(period.custom.from, period.custom.to)
            )
        }
    }

    private fun buildDynamics(
        transactions: List<Transaction>,
        range: DateRange,
        metric: StatMetric,
    ): List<StatPoint> {
        val useMonthlyBuckets = shouldUseMonthlyBuckets(range)
        val points = if (useMonthlyBuckets) {
            monthBuckets(range)
        } else {
            dayBuckets(range)
        }
        val grouped = transactions.groupBy { tx ->
            if (useMonthlyBuckets) LocalDate(tx.date.year, tx.date.monthNumber, 1) else tx.date
        }
        return points.map { bucketDate ->
            val bucketTransactions = grouped[bucketDate].orEmpty()
            val value = when (metric) {
                StatMetric.INCOME -> bucketTransactions
                    .asSequence()
                    .filter { it.type is TransactionType.Income }
                    .fold(BigDecimal.ZERO) { acc, tx -> acc + tx.value }

                StatMetric.EXPENSE -> bucketTransactions
                    .asSequence()
                    .filter { it.type is TransactionType.Outcome }
                    .fold(BigDecimal.ZERO) { acc, tx -> acc + tx.value }

                StatMetric.BALANCE -> {
                    val income = bucketTransactions
                        .asSequence()
                        .filter { it.type is TransactionType.Income }
                        .fold(BigDecimal.ZERO) { acc, tx -> acc + tx.value }
                    val expense = bucketTransactions
                        .asSequence()
                        .filter { it.type is TransactionType.Outcome }
                        .fold(BigDecimal.ZERO) { acc, tx -> acc + tx.value }
                    income - expense
                }
            }
            StatPoint(timestamp = bucketDate, value = value)
        }
    }

    private fun shouldUseMonthlyBuckets(range: DateRange): Boolean {
        val daySpan = (range.to.toEpochDays() - range.from.toEpochDays())
        return daySpan > 45
    }

    private fun dayBuckets(range: DateRange): List<LocalDate> {
        val count = range.to.toEpochDays() - range.from.toEpochDays()
        return (0..count).map { dayOffset ->
            shiftDays(range.from, dayOffset.toInt())
        }
    }

    private fun monthBuckets(range: DateRange): List<LocalDate> {
        val start = LocalDate(range.from.year, range.from.monthNumber, 1)
        val end = LocalDate(range.to.year, range.to.monthNumber, 1)
        val buckets = mutableListOf<LocalDate>()
        var cursor = start
        while (cursor <= end) {
            buckets += cursor
            cursor = shiftMonths(cursor, 1)
        }
        return buckets
    }

    private fun buildExpenseDistribution(transactions: List<Transaction>): List<StatCategorySlice> {
        val expensesByCategory = transactions
            .asSequence()
            .mapNotNull { tx ->
                val type = tx.type as? TransactionType.Outcome ?: return@mapNotNull null
                type.category to tx.value
            }
            .groupBy(
                keySelector = { (category, _) -> category.id },
                valueTransform = { (category, amount) -> category.name to amount }
            )
            .map { (categoryId, values) ->
                val categoryName = values.first().first
                val amount = values.fold(BigDecimal.ZERO) { acc, value -> acc + value.second }
                categoryId to (categoryName to amount)
            }
            .sortedByDescending { it.second.second }

        val totalExpense = expensesByCategory.fold(BigDecimal.ZERO) { acc, entry -> acc + entry.second.second }

        return expensesByCategory.mapIndexed { index, (categoryId, pair) ->
            val categoryName = pair.first
            val amount = pair.second
            val percent = if (totalExpense.isZero) {
                0.0
            } else {
                ((amount / totalExpense) * 100).toString().toDoubleOrNull() ?: 0.0
            }
            StatCategorySlice(
                categoryId = categoryId,
                categoryName = categoryName,
                amount = amount,
                percent = percent,
                colorHex = chartPalette[index % chartPalette.size]
            )
        }
    }

    private fun accountIdOf(transaction: Transaction): String? {
        return when (transaction.type) {
            is TransactionType.Income -> transaction.account.id
            is TransactionType.Outcome -> transaction.account.id
            is TransactionType.Transfer -> null
        }
    }

    private fun shiftDays(date: LocalDate, daysDelta: Int): LocalDate {
        return LocalDate.fromEpochDays(date.toEpochDays() + daysDelta)
    }

    private fun shiftMonths(date: LocalDate, monthsDelta: Int): LocalDate {
        val monthIndex = (date.year * 12) + (date.monthNumber - 1) + monthsDelta
        val targetYear = monthIndex.floorDiv(12)
        val targetMonth = monthIndex.mod(12) + 1
        val maxDay = daysInMonth(targetYear, targetMonth)
        val targetDay = minOf(date.dayOfMonth, maxDay)
        return LocalDate(targetYear, targetMonth, targetDay)
    }

    private fun daysInMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 30
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}
