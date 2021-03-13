package io.hexagonal.domain.model

import arrow.core.left
import arrow.core.right
import io.hexagonal.domain.model.DomainError.InvalidState
import io.hexagonal.domain.model.TaskState.CANCELLED
import io.hexagonal.domain.model.TaskState.DONE
import io.hexagonal.domain.model.TaskState.IN_PROGRESS
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TaskTest: FunSpec() {

    init {
        test("'move' must return a Right with the task updated to the new state when valid move") {
            val task = Task("name", "content", IN_PROGRESS)
            task.move(DONE) shouldBe task.copy(state = DONE).right()
        }

        test("'move' must return a Left domain error when invalid move") {
            val task = Task("name", "content", CANCELLED)
            task.move(DONE) shouldBe InvalidState("Task has been cancelled and is no longer modifiable.").left()
        }
    }
}