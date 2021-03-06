package io.hexagonal.domain.model

import arrow.core.Either

typealias DResult<A> = Either<TaskError, A>
