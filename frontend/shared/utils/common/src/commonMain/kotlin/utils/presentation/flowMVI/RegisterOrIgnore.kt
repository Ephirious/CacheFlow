package utils.presentation.flowMVI

import kotlinx.coroutines.Job
import pro.respawn.flowmvi.api.FlowMVIDSL
import pro.respawn.flowmvi.plugins.JobManager


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
