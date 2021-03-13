package io.hexagonal.domain.ports.primary.command

import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.extensions.applicativeNel
import arrow.core.extensions.id.applicative.unit
import arrow.core.fix
import arrow.core.nel
import io.hexagonal.domain.model.Content
import io.hexagonal.domain.model.DRequest
import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.Name
import io.hexagonal.domain.model.RuleError
import io.hexagonal.domain.model.Task
import io.hexagonal.domain.model.notBlank

data class CreateTaskRequest(val name: Name, val content: Content) : DRequest {
    override fun validate(): Validated<NonEmptyList<RuleError>, Unit> =
        Validated.applicativeNel<RuleError>()
            .tupledN(
                notBlank(name) { RuleError.InvalidTaskName.nel() },
                notBlank(content) { RuleError.InvalidTaskContent.nel() },
            ).fix()
            .map { unit() }

}

interface CreateTask {
    fun create(request: CreateTaskRequest): DResult<Task>
}