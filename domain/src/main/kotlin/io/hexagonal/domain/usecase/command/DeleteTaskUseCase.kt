package io.hexagonal.domain.usecase.command

import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.ports.primary.command.DeleteTask
import io.hexagonal.domain.ports.primary.command.DeleteTaskRequest
import io.hexagonal.domain.ports.secondary.TaskPort
import java.util.*

class DeleteTaskUseCase(private val taskPort: TaskPort) : DeleteTask {
    override suspend fun delete(request: DeleteTaskRequest): DResult<Unit> =
        taskPort.delete(UUID.fromString(request.id))
}

