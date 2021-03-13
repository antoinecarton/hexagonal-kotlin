package io.hexagonal.domain.usecase.command

import arrow.core.flatMap
import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.Task
import io.hexagonal.domain.model.TaskState
import io.hexagonal.domain.ports.primary.command.MoveTask
import io.hexagonal.domain.ports.primary.command.MoveTaskRequest
import io.hexagonal.domain.ports.secondary.TaskPort
import java.util.*

class MoveTaskUseCase(private val taskPort: TaskPort) : MoveTask {
    override fun move(request: MoveTaskRequest): DResult<Task> =
        taskPort.get(UUID.fromString(request.id))
            .flatMap { it.move(TaskState.valueOf(request.state)) }
            .flatMap { taskPort.save(it) }
}