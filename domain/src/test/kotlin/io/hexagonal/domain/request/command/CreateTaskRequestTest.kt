package io.hexagonal.domain.request.command

import arrow.core.Invalid
import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.invalidNel
import io.hexagonal.domain.model.RuleError
import io.hexagonal.domain.ports.primary.command.CreateTaskRequest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CreateTaskRequestTest: FunSpec() {

    init {
        test("'validate' must return an invalid nel when task name is blank") {
            // Given
            val request = CreateTaskRequest("", "content")

            // When
            val result = request.validate()

            // Then
            result shouldBe RuleError.InvalidTaskName.invalidNel()
        }

        test("'validate' must return an invalid nel when task content is blank") {
            // Given
            val request = CreateTaskRequest("name", "")

            // When
            val result = request.validate()

            // Then
            result shouldBe RuleError.InvalidTaskContent.invalidNel()
        }

        test("'validate' must return an invalid nel when both task name and content are blank") {
            // Given
            val request = CreateTaskRequest("", "")

            // When
            val result = request.validate()

            // Then
            result shouldBe Invalid(NonEmptyList(RuleError.InvalidTaskName, RuleError.InvalidTaskContent))
        }

        test("'validate' must return a valid when request is valid") {
            // Given
            val request = CreateTaskRequest("name", "content")

            // When
            val result = request.validate()

            // Then
            result shouldBe Validated.Valid(Unit)
        }
    }
}