package io.hexagonal.domain.ports.primary.query

import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.Task

interface GetTasks {
    suspend fun all(): DResult<List<Task>>
}
