package io.hexagonal.infra.db

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.core.rightIfNotNull
import com.github.benmanes.caffeine.cache.Caffeine
import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.DomainError
import io.hexagonal.domain.model.Task
import io.hexagonal.domain.model.TaskState
import io.hexagonal.domain.ports.secondary.TaskPort
import trying
import java.util.*

/**
 * A basic implementation to simulate some data (~ in memory adapter) with a caffeine cache
 */
class InMemoryTaskDb : TaskPort {

    private val tasks = Caffeine
        .newBuilder()
        .maximumSize(200) // that's enough for our example :)
        .build<UUID, Task>()

    init {
        val doneTask = Task(
            UUID.fromString("682c1b16-532e-4164-afc3-ae5fd00671af"),
            "Read Batman #1",
            "The joker war #1",
            state = TaskState.DONE
        )
        val cancelledTask = Task(
            UUID.fromString("4b1de79b-51f3-4825-a55a-29f7d82db92e"),
            "Read Batman #2",
            "The joker war #2",
            state = TaskState.CANCELLED
        )
        tasks.putAll(mapOf(doneTask.id to doneTask, cancelledTask.id to cancelledTask))
    }

    override fun create(task: Task): DResult<Task> =
        get(task.id).fold(
            {
                when (it) {
                    is DomainError.NotFound -> {
                        tasks.put(task.id, task)
                        task.right()
                    }
                    else -> it.left()
                }
            },
            {
                DomainError.AlreadyExist("Task with name ${task.name} already exists").left()
            }
        )

    override fun save(task: Task): DResult<Task> =
        Either.trying {
            tasks.invalidate(task.id)
            tasks.put(task.id, task)
        }.map { task }

    override fun all(): DResult<List<Task>> = tasks.asMap().values.toList().right()

    override fun get(id: UUID): DResult<Task> =
        tasks.getIfPresent(id)
            .rightIfNotNull { DomainError.NotFound("Task '$id' not found") }

    override fun delete(id: UUID): DResult<Unit> = Either.trying { tasks.invalidate(id) }

}