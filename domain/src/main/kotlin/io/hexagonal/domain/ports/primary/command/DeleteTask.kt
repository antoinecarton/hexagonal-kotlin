package io.hexagonal.domain.ports.primary.command

import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.extensions.validated.functor.unit
import io.hexagonal.domain.model.DRequest
import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.RuleError
import io.hexagonal.domain.model.validUuid

data class DeleteTaskRequest(val id: String) : DRequest {
    override fun validate(): Validated<NonEmptyList<RuleError>, Unit> =
        validUuid(id).unit()
}

interface DeleteTask {
    fun delete(request: DeleteTaskRequest): DResult<Unit>
}