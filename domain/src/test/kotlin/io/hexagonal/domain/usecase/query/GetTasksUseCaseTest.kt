package io.hexagonal.domain.usecase.query

import arrow.core.left
import arrow.core.right
import io.hexagonal.domain.model.DomainError
import io.hexagonal.domain.model.Task
import io.hexagonal.domain.model.TaskState
import io.hexagonal.domain.ports.secondary.TaskPort
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class GetTasksUseCaseTest: FunSpec() {

    init {
        test("'all' must return a Right with all the tasks") {
            // Given
            val taskPort = mockk<TaskPort>()
            val useCase = GetTasksUseCase(taskPort)

            val t1 = Task(name = "name1", content = "content1", state = TaskState.CANCELLED)
            val t2 = Task(name = "name2", content = "content2")

            val tasks = listOf(t1, t2)

            every { taskPort.all() } returns tasks.right()

            // When
            val result = useCase.all()

            // Then
            verify(exactly = 1) { taskPort.all() }
            result shouldBe tasks.right()
        }

        test("'all' must return a Left when an error occurs") {
            // Given
            val taskPort = mockk<TaskPort>()
            val useCase = GetTasksUseCase(taskPort)

            val domainError = DomainError.Unknown("Force an unknown error")
            every { taskPort.all() } returns domainError.left()

            // When
            val result = useCase.all()

            // Then
            verify(exactly = 1) { taskPort.all() }
            result shouldBe domainError.left()
        }
    }
}