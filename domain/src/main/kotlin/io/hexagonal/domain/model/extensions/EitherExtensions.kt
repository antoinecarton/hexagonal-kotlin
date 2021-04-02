package io.hexagonal.domain.model.extensions

import arrow.core.Either
import arrow.core.Nel
import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.RuleError
import io.hexagonal.domain.model.TaskError

fun <T> Either.Companion.trying(block: () -> T): DResult<T> =
    catch { block() }
        .mapLeft { TaskError.Unknown(it.message ?: "", it) }

suspend fun <T> Either.Companion.sTrying(block: suspend () -> T, ifFail: (Throwable) -> TaskError): DResult<T> =
    catch { block() }
        .mapLeft { ifFail.invoke(it) }

fun <T> Either.Companion.tryingNel(block: () -> T, ifFail: () -> Nel<RuleError>): Either<Nel<RuleError>, T> =
    catch { block() }
        .mapLeft { ifFail.invoke() }
