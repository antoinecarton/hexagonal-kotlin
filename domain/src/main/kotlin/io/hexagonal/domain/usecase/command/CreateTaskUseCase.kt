package io.hexagonal.domain.usecase.command

import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.Task
import io.hexagonal.domain.ports.primary.command.CreateTask
import io.hexagonal.domain.ports.primary.command.CreateTaskRequest
import io.hexagonal.domain.ports.secondary.TaskPort
import java.util.*

class CreateTaskUseCase(private val taskPort: TaskPort) : CreateTask {
    override fun create(request: CreateTaskRequest): DResult<Task> =
        taskPort.create(request.toTask())

    companion object {
        internal fun CreateTaskRequest.toTask() =
            Task(UUID.randomUUID(), this.name, this.content)
    }
}

