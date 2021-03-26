package io.hexagonal.domain.model

import arrow.core.left
import arrow.core.right
import io.hexagonal.domain.model.TaskError.InvalidState
import io.hexagonal.domain.model.TaskState.CANCELLED
import io.hexagonal.domain.model.TaskState.DONE
import io.hexagonal.domain.model.TaskState.IN_PROGRESS
import io.hexagonal.domain.model.TaskState.TODO
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TaskStateTest: FunSpec() {

    init {
        test("'move' must return an InvalidState domain error when moving from DONE to another state") {
            val invalidState = InvalidState("Task has been done and is no longer modifiable.")
            DONE.move(IN_PROGRESS) shouldBe invalidState.left()
            DONE.move(DONE) shouldBe invalidState.left()
            DONE.move(CANCELLED) shouldBe invalidState.left()
            DONE.move(TODO) shouldBe invalidState.left()
        }

        test("'move' must return an InvalidState domain error when moving from CANCELLED to another state") {
            val invalidState = InvalidState("Task has been cancelled and is no longer modifiable.")
            CANCELLED.move(IN_PROGRESS) shouldBe invalidState.left()
            CANCELLED.move(DONE) shouldBe invalidState.left()
            CANCELLED.move(TODO) shouldBe invalidState.left()
            CANCELLED.move(CANCELLED) shouldBe invalidState.left()
        }

        test("'move' must return an InvalidState domain error when moving from TODO to TODO") {
            TODO.move(TODO) shouldBe InvalidState("Task is already marked as TODO").left()
        }

        test("'move' must return a Right with the new state when moving from TODO to IN_PROGRESS") {
            TODO.move(IN_PROGRESS) shouldBe  IN_PROGRESS.right()
        }

        test("'move' must return a Right with the new state when moving from TODO to CANCELLED") {
            TODO.move(CANCELLED) shouldBe  CANCELLED.right()
        }

        test("'move' must return a Right with the new state when moving from TODO to DONE") {
            TODO.move(DONE) shouldBe  DONE.right()
        }

        test("'move' must return an InvalidState domain error when moving from IN_PROGRESS to IN_PROGRESS") {
            IN_PROGRESS.move(IN_PROGRESS) shouldBe InvalidState("Task is already in progress.").left()
        }

        test("'move' must return a Right with the new state when moving from IN_PROGRESS to TODO") {
            IN_PROGRESS.move(TODO) shouldBe  TODO.right()
        }

        test("'move' must return a Right with the new state when moving from IN_PROGRESS to CANCELLED") {
            IN_PROGRESS.move(CANCELLED) shouldBe  CANCELLED.right()
        }

        test("'move' must return a Right with the new state when moving from IN_PROGRESS to DONE") {
            IN_PROGRESS.move(DONE) shouldBe  DONE.right()
        }
    }
}
