package io.hexagonal.domain.ports.secondary

import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.Task
import java.util.*

interface TaskPort {
    fun create(task: Task): DResult<Task>
    fun save(task: Task): DResult<Task>
    fun all(): DResult<List<Task>>
    fun get(id: UUID): DResult<Task>
    fun delete(id: UUID): DResult<Unit>
}