package io.hexagonal.domain.model

import arrow.core.NonEmptyList
import arrow.core.Validated

interface DRequest {
    fun validate(): Validated<NonEmptyList<RuleError>, Unit>
}