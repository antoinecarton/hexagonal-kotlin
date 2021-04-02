package io.hexagonal.domain.request.command

import arrow.core.Invalid
import arrow.core.Validated
import arrow.core.invalidNel
import arrow.core.nonEmptyListOf
import io.hexagonal.domain.model.RuleError
import io.hexagonal.domain.ports.primary.command.MoveTaskRequest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MoveTaskRequestTest: FunSpec() {

    init {
        test("'validate' must return an invalid nel when task id is blank") {
            // Given
            val request = MoveTaskRequest("", "DONE")

            // When
            val result = request.validate()

            // Then
            result shouldBe RuleError.InvalidTaskId.invalidNel()
        }

        test("'validate' must return an invalid nel when task id is invalid") {
            // Given
            val request = MoveTaskRequest("invalid-uuid", "CANCELLED")

            // When
            val result = request.validate()

            // Then
            result shouldBe RuleError.InvalidTaskId.invalidNel()
        }

        test("'validate' must return an invalid nel when state is invalid") {
            // Given
            val request = MoveTaskRequest("0661ef75-3e57-47d1-b8b0-c90d7be6d653", "unknown_state")

            // When
            val result = request.validate()

            // Then
            result shouldBe RuleError.InvalidTaskState.invalidNel()
        }

        test("'validate' must return an invalid nel when both task id and state are invalid") {
            // Given
            val request = MoveTaskRequest("invalid-id", "unknown-state")

            // When
            val result = request.validate()

            // Then
            result shouldBe Invalid(nonEmptyListOf(RuleError.InvalidTaskId, RuleError.InvalidTaskState))
        }

        test("'validate' must return a valid when request is valid") {
            // Given
            val request = MoveTaskRequest("0661ef75-3e57-47d1-b8b0-c90d7be6d653", "DONE")

            // When
            val result = request.validate()

            // Then
            result shouldBe Validated.Valid(Unit)
        }
    }
}
