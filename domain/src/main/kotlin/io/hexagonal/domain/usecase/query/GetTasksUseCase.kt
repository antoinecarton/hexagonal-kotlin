package io.hexagonal.domain.usecase.query

import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.Task
import io.hexagonal.domain.ports.primary.query.GetTasks
import io.hexagonal.domain.ports.secondary.TaskPort

class GetTasksUseCase(private val taskPort: TaskPort) : GetTasks {
    override suspend fun all(): DResult<List<Task>> =
        taskPort.all()
}
