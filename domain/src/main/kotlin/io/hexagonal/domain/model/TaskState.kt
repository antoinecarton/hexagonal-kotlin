package io.hexagonal.domain.model

import arrow.core.Either
import arrow.core.left
import arrow.core.right

// Purpose is not to create a Finite State Machine but to showcase use of some business rules
enum class TaskState {

    TODO {
        override fun move(to: TaskState): Either<DomainError, TaskState> =
            when (to) {
                IN_PROGRESS, CANCELLED, DONE -> to.right()
                TODO -> DomainError.InvalidState("Task is already marked as TODO").left()
            }
    },

    IN_PROGRESS {
        override fun move(to: TaskState): Either<DomainError, TaskState> =
            when (to) {
                CANCELLED, DONE, TODO -> to.right()
                IN_PROGRESS -> DomainError.InvalidState("Task is already in progress.").left()
            }
    },

    DONE {
        override fun move(to: TaskState): Either<DomainError, TaskState> =
            DomainError.InvalidState("Task has been done and is no longer modifiable.").left()
    },

    CANCELLED {
        override fun move(to: TaskState): Either<DomainError, TaskState> =
            DomainError.InvalidState("Task has been cancelled and is no longer modifiable.").left()
    };

    abstract fun move(to: TaskState): Either<DomainError, TaskState>

}
