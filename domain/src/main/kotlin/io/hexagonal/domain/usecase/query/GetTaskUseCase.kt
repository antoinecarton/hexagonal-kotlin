package io.hexagonal.domain.usecase.query

import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.Task
import io.hexagonal.domain.ports.primary.query.GetTask
import io.hexagonal.domain.ports.primary.query.GetTaskRequest
import io.hexagonal.domain.ports.secondary.TaskPort
import java.util.*

class GetTaskUseCase(private val taskPort: TaskPort) : GetTask {
    override suspend fun get(request: GetTaskRequest): DResult<Task> =
        taskPort.get(UUID.fromString(request.id))
}
