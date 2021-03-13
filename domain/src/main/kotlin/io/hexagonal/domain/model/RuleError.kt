package io.hexagonal.domain.model

import arrow.core.Either
import arrow.core.Nel
import arrow.core.Validated
import arrow.core.nel
import tryingNel
import java.util.*

sealed class RuleError(val message: String) {
    object InvalidTaskId : RuleError("Task id is invalid")
    object InvalidTaskState : RuleError("Task state is invalid")
    object InvalidTaskName : RuleError("Task name is blank")
    object InvalidTaskContent : RuleError("Task content is blank")
}

fun notBlank(value: String, nel: () -> Nel<RuleError>): Validated<Nel<RuleError>, String> =
    Validated.fromEither(Either.conditionally(value.isNotBlank(), { nel() }, { value }))

fun validUuid(value: String): Validated<Nel<RuleError>, String> =
    Validated.fromEither(
        Either.tryingNel(
            { UUID.fromString(value); value },
            { RuleError.InvalidTaskId.nel() }
        )
    )

fun validTaskState(value: String): Validated<Nel<RuleError>, TaskState> =
    Validated.fromEither(
        Either.tryingNel({ TaskState.valueOf(value) }, { RuleError.InvalidTaskState.nel() })
    )