package io.hexagonal.domain.usecase.command

import arrow.core.left
import arrow.core.right
import io.hexagonal.domain.model.TaskError
import io.hexagonal.domain.model.Task
import io.hexagonal.domain.model.TaskState
import io.hexagonal.domain.ports.primary.command.MoveTaskRequest
import io.hexagonal.domain.ports.secondary.TaskPort
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class MoveTaskUseCaseTest: FunSpec() {

    init {
        test("'move' must return a Right when the task has been moved successfully to a new state") {
            // Given
            val taskPort = mockk<TaskPort>()
            val useCase = MoveTaskUseCase(taskPort)

            val id = UUID.randomUUID()
            val request = MoveTaskRequest(id.toString(), TaskState.CANCELLED.name)
            val task = Task(id, "name", "content", TaskState.TODO)
            val cancelled = task.copy(state = TaskState.CANCELLED)
            every { taskPort.get(id) } returns task.right()
            every { taskPort.move(cancelled) } returns cancelled.right()

            // When
            val result = useCase.move(request)

            // Then
            verify(exactly = 1) { taskPort.get(eq(id)) }
            verify(exactly = 1) { taskPort.move(eq(cancelled)) }
            result shouldBe cancelled.right()

        }

        test("'move' must return a Left when new state is invalid for the current task state") {
            // Given
            val taskPort = mockk<TaskPort>()
            val useCase = MoveTaskUseCase(taskPort)

            val id = UUID.randomUUID()
            val cancelled = Task(id, "name", "content", TaskState.CANCELLED)
            val request = MoveTaskRequest(id.toString(), TaskState.TODO.name)

            every { taskPort.get(id) } returns cancelled.right()

            // When
            val result = useCase.move(request)

            // Then
            verify(exactly = 1) { taskPort.get(eq(id)) }
            result shouldBe TaskError.InvalidState("Task has been cancelled and is no longer modifiable.").left()
        }

        test("'move' must return a Left when port failed to move the task to a new state") {
            // Given
            val taskPort = mockk<TaskPort>()
            val useCase = MoveTaskUseCase(taskPort)

            val id = UUID.randomUUID()
            val request = MoveTaskRequest(id.toString(), TaskState.CANCELLED.name)
            val task = Task(id, "name", "content", TaskState.TODO)
            val cancelled = task.copy(state = TaskState.CANCELLED)
            val domainError = TaskError.Unknown("Force an unknown error")
            every { taskPort.get(id) } returns task.right()
            every { taskPort.move(cancelled) } returns domainError.left()

            // When
            val result = useCase.move(request)

            // Then
            verify(exactly = 1) { taskPort.get(eq(id)) }
            verify(exactly = 1) { taskPort.move(eq(cancelled)) }
            result shouldBe domainError.left()
        }
    }
}
