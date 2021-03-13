package io.hexagonal.domain.ports.primary.command

import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.extensions.applicativeNel
import arrow.core.extensions.id.applicative.unit
import arrow.core.fix
import io.hexagonal.domain.model.DRequest
import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.RuleError
import io.hexagonal.domain.model.Task
import io.hexagonal.domain.model.validTaskState
import io.hexagonal.domain.model.validUuid

data class MoveTaskRequest(val id: String, val state: String) : DRequest {
    override fun validate(): Validated<NonEmptyList<RuleError>, Unit> =
        Validated.applicativeNel<RuleError>()
            .tupledN(
                validUuid(id),
                validTaskState(state)
            ).fix()
            .map { unit() }
}

interface MoveTask {
    fun move(request: MoveTaskRequest): DResult<Task>
}