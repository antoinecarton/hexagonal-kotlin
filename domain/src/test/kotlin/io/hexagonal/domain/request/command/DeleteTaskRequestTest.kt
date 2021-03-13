package io.hexagonal.domain.request.command

import arrow.core.Validated
import arrow.core.invalidNel
import io.hexagonal.domain.model.RuleError
import io.hexagonal.domain.ports.primary.command.DeleteTaskRequest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DeleteTaskRequestTest: FunSpec() {

    init {
        test("'validate' must return an invalid nel when id is blank") {
            // Given
            val request = DeleteTaskRequest("")

            // When
            val result = request.validate()

            // Then
            result shouldBe RuleError.InvalidTaskId.invalidNel()
        }

        test("'validate' must return an invalid nel when uuid is invalid") {
            // Given
            val request = DeleteTaskRequest("invalid-uuid")

            // When
            val result = request.validate()

            // Then
            result shouldBe RuleError.InvalidTaskId.invalidNel()
        }

        test("'validate' must return a valid when request is valid") {
            // Given
            val request = DeleteTaskRequest("0661ef75-3e57-47d1-b8b0-c90d7be6d653")

            // When
            val result = request.validate()

            // Then
            result shouldBe Validated.Valid(Unit)
        }
    }
}