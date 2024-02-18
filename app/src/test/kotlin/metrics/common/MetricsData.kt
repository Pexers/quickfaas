/*
 * Copyright (c) 5/16/2022, Pexers (https://github.com/Pexers)
 */

package metrics.common

import kotlin.time.Duration

data class MetricsData(
    var executionTime: Duration? = null,
    var memoryUsage: Float? = null,
    var internalExecTime: Duration? = null
)
