package io.hexagonal.domain.usecase.command

import arrow.core.left
import arrow.core.right
import io.hexagonal.domain.model.TaskError.AlreadyExist
import io.hexagonal.domain.ports.primary.command.CreateTaskRequest
import io.hexagonal.domain.ports.secondary.TaskPort
import io.hexagonal.domain.usecase.command.CreateTaskUseCase.Companion.toTask
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.coVerify

class CreateTaskUseCaseTest: FunSpec() {

    init {
        test("'create' must return a Right when port created the task successfully") {
            // Given
            val taskPort = mockk<TaskPort>()
            val useCase = CreateTaskUseCase(taskPort)

            val request = CreateTaskRequest("name", "content")
            val task = request.toTask()
            coEvery { taskPort.create(any()) } returns task.right()

            // When
            val result = useCase.create(request)

            // Then
            coVerify(exactly = 1) { taskPort.create(any()) }
            result shouldBe task.right()
        }

        test("'create' must return a Left when port failed to create the task") {
            // Given
            val taskPort = mockk<TaskPort>()
            val useCase = CreateTaskUseCase(taskPort)

            val request = CreateTaskRequest("name", "content")
            val domainError = AlreadyExist("Task with name ${request.name} already exists")
            coEvery { taskPort.create(any()) } returns domainError.left()

            // When
            val result = useCase.create(request)

            // Then
            coVerify(exactly = 1) { taskPort.create(any()) }
            result shouldBe domainError.left()
        }
    }
}
