package io.hexagonal.domain.usecase.query

import arrow.core.left
import arrow.core.right
import io.hexagonal.domain.model.Task
import io.hexagonal.domain.model.TaskError
import io.hexagonal.domain.model.TaskState
import io.hexagonal.domain.ports.primary.query.GetTaskRequest
import io.hexagonal.domain.ports.secondary.TaskPort
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.util.*

class GetTaskUseCaseTest: FunSpec() {

    init {
        test("'get' must return a Right with the task when it exists") {
            // Given
            val taskPort = mockk<TaskPort>()
            val useCase = GetTaskUseCase(taskPort)

            val id = UUID.randomUUID()
            val t1 = Task(id, "name", "content", state = TaskState.DONE)
            val request = GetTaskRequest(id.toString())
            coEvery { taskPort.get(id) } returns t1.right()

            // When
            val result = useCase.get(request)

            // Then
            coVerify(exactly = 1) { taskPort.get(eq(id)) }
            result shouldBe t1.right()
        }

        test("'get' must return a NotFound domain error when the task does not exist") {
            // Given
            val taskPort = mockk<TaskPort>()
            val useCase = GetTaskUseCase(taskPort)

            val id = UUID.randomUUID()
            val request = GetTaskRequest(id.toString())
            val domainError = TaskError.NotFound("Task '$id' not found")
            coEvery { taskPort.get(id) } returns domainError.left()

            // When
            val result = useCase.get(request)

            // Then
            coVerify(exactly = 1) { taskPort.get(id) }
            result shouldBe domainError.left()
        }
    }
}
