package utils.presentation.flowMVI

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.MVIState
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.plugins.JobManager


/**
 * @param flow Поток данных (например, UseCase)
 * @param jobs Менеджер для управления жизненным циклом джобы
 * @param key Ключ джобы
 * @param update Логика обновления: берет текущий стейт [S] и новое значение [T], возвращает новый стейт [S]
 */
inline fun <S : MVIState, T, K : Enum<K>> PipelineContext<S, *, *>.observe(
    flow: Flow<T>,
    jobs: JobManager<K>,
    key: K,
    crossinline onError: (Throwable) -> Unit = { throw it },
    crossinline update: suspend (T) -> Unit,
) {
    launch {
        try {
            flow.collect { newValue ->
                update(newValue)
            }
        } catch (t: Throwable) {
            onError(t)
        }
    }.registerOrIgnore(jobs, key)
}