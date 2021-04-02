package io.hexagonal.domain.ports.primary.command

import arrow.core.Nel
import arrow.core.Validated
import io.hexagonal.domain.model.DRequest
import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.RuleError
import io.hexagonal.domain.model.validUuid

data class DeleteTaskRequest(val id: String) : DRequest {
    override fun validate(): Validated<Nel<RuleError>, Unit> =
        validUuid(id).map { Unit }
}

interface DeleteTask {
    fun delete(request: DeleteTaskRequest): DResult<Unit>
}
