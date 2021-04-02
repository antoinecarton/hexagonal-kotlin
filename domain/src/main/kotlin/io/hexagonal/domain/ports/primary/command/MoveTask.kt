package io.hexagonal.domain.ports.primary.command

import arrow.core.Nel
import arrow.core.Validated
import arrow.core.zip
import arrow.typeclasses.Semigroup
import io.hexagonal.domain.model.DRequest
import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.RuleError
import io.hexagonal.domain.model.Task
import io.hexagonal.domain.model.validTaskState
import io.hexagonal.domain.model.validUuid

data class MoveTaskRequest(val id: String, val state: String) : DRequest {
    override fun validate(): Validated<Nel<RuleError>, Unit> =
        validUuid(id)
            .zip(
                Semigroup.nonEmptyList(),
                validTaskState(state)
            ) { _, _ -> Unit }
}

interface MoveTask {
    suspend fun move(request: MoveTaskRequest): DResult<Task>
}
