package io.hexagonal.domain.model

sealed class TaskError(open val message: String, open val e: Exception? = null) {
    data class InvalidState(override val message: String) : TaskError(message)
    data class NotFound(override val message: String) : TaskError(message)
    data class AlreadyExist(override val message: String) : TaskError(message)
    data class InvalidRules(override val message: String) : TaskError(message)
    data class Unknown(override val message: String, override val e: Exception? = null) : TaskError(message, e)
}
