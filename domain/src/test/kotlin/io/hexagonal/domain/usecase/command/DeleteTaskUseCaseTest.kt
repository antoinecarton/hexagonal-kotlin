package io.hexagonal.domain.usecase.command

import arrow.core.left
import arrow.core.right
import io.hexagonal.domain.model.DomainError
import io.hexagonal.domain.ports.primary.command.DeleteTaskRequest
import io.hexagonal.domain.ports.secondary.TaskPort
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class DeleteTaskUseCaseTest: FunSpec() {

    init {
        test("'delete' must return a Right when port deleted the task successfully") {
            // Given
            val taskPort = mockk<TaskPort>()
            val useCase = DeleteTaskUseCase(taskPort)

            val id = UUID.randomUUID()
            val request = DeleteTaskRequest(id.toString())
            every { taskPort.delete(id) } returns Unit.right()

            // When
            val result = useCase.delete(request)

            // Then
            verify(exactly = 1) { taskPort.delete(eq(id)) }
            result shouldBe Unit.right()
        }

        test("'delete' must return a Left when port failed to delete the task") {
            // Given
            val taskPort = mockk<TaskPort>()
            val useCase = DeleteTaskUseCase(taskPort)

            val id = UUID.randomUUID()
            val request = DeleteTaskRequest(id.toString())
            val domainError = DomainError.Unknown("Force an unknown error")
            every { taskPort.delete(id) } returns domainError.left()


            // When
            val result = useCase.delete(request)

            // Then
            verify(exactly = 1) { taskPort.delete(eq(id)) }
            result shouldBe domainError.left()
        }
    }
}