package utils.presentation.flowMVI

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.FlowMVIDSL
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.plugins.JobManager
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


fun <K : Any> JobManager<K>.putOrIgnore(key: K, job: Job): Job? {
    val existing = get(key)
    if (existing?.isActive == true) {
        job.cancel()
        return existing
    }

    put(key, job)
    return null
}


@FlowMVIDSL
fun <K : Any> Job.registerOrIgnore(
    manager: JobManager<K>,
    key: K,
): Job = apply { manager.putOrIgnore(key, this) }

fun <K : Enum<K>> PipelineContext<*, *, *>.launchOrIgnore(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    jobs: JobManager<K>,
    key: K,
    block: suspend () -> Unit
) {
    launch(coroutineContext) {
        block()
    }.registerOrIgnore(manager = jobs, key = key)
}