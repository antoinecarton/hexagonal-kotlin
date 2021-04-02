package io.hexagonal.domain.ports.primary.command

import arrow.core.Nel
import arrow.core.Validated
import arrow.core.nel
import arrow.core.zip
import arrow.typeclasses.Semigroup
import io.hexagonal.domain.model.Content
import io.hexagonal.domain.model.DRequest
import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.Name
import io.hexagonal.domain.model.RuleError
import io.hexagonal.domain.model.Task
import io.hexagonal.domain.model.notBlank

data class CreateTaskRequest(val name: Name, val content: Content) : DRequest {
    override fun validate(): Validated<Nel<RuleError>, Unit> =
        notBlank(name) { RuleError.InvalidTaskName.nel() }
            .zip(
                Semigroup.nonEmptyList(),
                notBlank(content) { RuleError.InvalidTaskContent.nel() }
            ) { _, _ -> Unit }
}

interface CreateTask {
    fun create(request: CreateTaskRequest): DResult<Task>
}
