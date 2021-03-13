package io.hexagonal.domain.ports.primary.query

import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.extensions.validated.functor.unit
import io.hexagonal.domain.model.DRequest
import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.RuleError
import io.hexagonal.domain.model.Task
import io.hexagonal.domain.model.validUuid

data class GetTaskRequest(val id: String) : DRequest {
    override fun validate(): Validated<NonEmptyList<RuleError>, Unit> =
        validUuid(id).unit()
}

interface GetTask {
    fun get(request: GetTaskRequest): DResult<Task>
}