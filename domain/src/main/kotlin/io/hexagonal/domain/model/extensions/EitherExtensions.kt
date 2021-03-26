import arrow.core.Either
import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.right
import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.TaskError
import io.hexagonal.domain.model.RuleError

fun <T> Either.Companion.trying(block: () -> T): DResult<T> =
    try {
        block().right()
    } catch (e: Exception) {
        TaskError.Unknown(e.message ?: "", e).left()
    }

fun <T> Either.Companion.trying(block: () -> T, ifFail: (Exception) -> TaskError): DResult<T> =
    try {
        block().right()
    } catch (e: Exception) {
        ifFail.invoke(e).left()
    }

suspend fun <T> Either.Companion.sTrying(block: suspend () -> T, ifFail: (Exception) -> TaskError): DResult<T> =
    try {
        block().right()
    } catch (e: Exception) {
        ifFail.invoke(e).left()
    }

fun <T> Either.Companion.tryingNel(block: () -> T, ifFail: () -> Nel<RuleError>): Either<NonEmptyList<RuleError>, T> =
    try {
        block().right()
    } catch (e: Exception) {
        ifFail.invoke().left()
    }
