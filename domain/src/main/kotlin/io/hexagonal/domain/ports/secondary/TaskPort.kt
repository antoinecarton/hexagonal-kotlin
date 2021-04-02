package io.hexagonal.domain.ports.secondary

import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.Task
import java.util.*

interface TaskPort {
    suspend fun create(task: Task): DResult<Task>
    suspend fun move(task: Task): DResult<Task>
    suspend fun all(): DResult<List<Task>>
    suspend fun get(id: UUID): DResult<Task>
    suspend fun delete(id: UUID): DResult<Unit>
}
