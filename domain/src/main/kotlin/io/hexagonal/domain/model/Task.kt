package io.hexagonal.domain.model

import arrow.core.Either
import java.util.*

// Maybe use inline class when stable to enforce compiler rules and avoid overhead wrappers
// (see https://kotlinlang.org/docs/inline-classes.html)
typealias Name = String
typealias Content = String

data class Task(val id: UUID, val name: Name, val content: Content, val state: TaskState = TaskState.TODO) {

    constructor(name: Name, content: Content) : this(UUID.randomUUID(), name, content)
    constructor(name: Name, content: Content, state: TaskState) : this(UUID.randomUUID(), name, content, state)

    fun move(to: TaskState): Either<DomainError, Task> =
        state.move(to)
            .map { this.copy(state = it) }
}