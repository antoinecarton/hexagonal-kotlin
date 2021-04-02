package io.hexagonal.infra.db

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.rightIfNotNull
import com.github.benmanes.caffeine.cache.Caffeine
import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.Task
import io.hexagonal.domain.model.TaskError
import io.hexagonal.domain.model.TaskState
import io.hexagonal.domain.model.extensions.trying
import io.hexagonal.domain.ports.secondary.TaskPort
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

    override suspend fun create(task: Task): DResult<Task> =
        get(task.id).fold(
            {
                when (it) {
                    is TaskError.NotFound -> {
                        either {
                            Either.trying { tasks.put(task.id, task) }.bind()
                            task
                        }
                    }
                    else -> it.left()
                }
            },
            { TaskError.AlreadyExist("Task with name ${task.name} already exists").left() }
        )

    override suspend fun move(task: Task): DResult<Task> =
        either {
            invalidateTask(task.id).bind()
            Either.trying { tasks.put(task.id, task) }.bind()
            task
        }

    override suspend fun all(): DResult<List<Task>> =
         Either.trying { tasks.asMap().values.toList() }

    override suspend fun get(id: UUID): DResult<Task> =
        tasks.getIfPresent(id)
            .rightIfNotNull { TaskError.NotFound("Task '$id' not found") }

    override suspend fun delete(id: UUID): DResult<Unit> = invalidateTask(id)

    private fun invalidateTask(id: UUID): DResult<Unit> = Either.trying { tasks.invalidate(id) }

}
