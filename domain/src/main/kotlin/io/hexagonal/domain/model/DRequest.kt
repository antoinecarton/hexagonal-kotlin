package io.hexagonal.domain.model

import arrow.core.Nel
import arrow.core.Validated

interface DRequest {
    fun validate(): Validated<Nel<RuleError>, Unit>
}
