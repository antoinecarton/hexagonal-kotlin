package io.hexagonal.domain.usecase.query

import arrow.core.left
import arrow.core.right
import io.hexagonal.domain.model.DomainError
import io.hexagonal.domain.model.Task
import io.hexagonal.domain.model.TaskState
import io.hexagonal.domain.ports.primary.query.GetTaskRequest
import io.hexagonal.domain.ports.secondary.TaskPort
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
            every { taskPort.get(id) } returns t1.right()

            // When
            val result = useCase.get(request)

            // Then
            verify(exactly = 1) { taskPort.get(eq(id)) }
            result shouldBe t1.right()
        }

        test("'get' must return a NotFound domain error when the task does not exist") {
            // Given
            val taskPort = mockk<TaskPort>()
            val useCase = GetTaskUseCase(taskPort)

            val id = UUID.randomUUID()
            val request = GetTaskRequest(id.toString())
            val domainError = DomainError.NotFound("Task '$id' not found")
            every { taskPort.get(id) } returns domainError.left()

            // When
            val result = useCase.get(request)

            // Then
            verify(exactly = 1) { taskPort.get(id) }
            result shouldBe domainError.left()
        }
    }
}