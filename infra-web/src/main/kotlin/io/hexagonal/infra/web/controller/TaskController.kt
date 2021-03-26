package io.hexagonal.infra.web.controller

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import io.hexagonal.domain.model.DRequest
import io.hexagonal.domain.model.DResult
import io.hexagonal.domain.model.TaskError
import io.hexagonal.domain.ports.primary.command.CreateTask
import io.hexagonal.domain.ports.primary.command.CreateTaskRequest
import io.hexagonal.domain.ports.primary.command.DeleteTask
import io.hexagonal.domain.ports.primary.command.DeleteTaskRequest
import io.hexagonal.domain.ports.primary.command.MoveTask
import io.hexagonal.domain.ports.primary.command.MoveTaskRequest
import io.hexagonal.domain.ports.primary.query.GetTask
import io.hexagonal.domain.ports.primary.query.GetTaskRequest
import io.hexagonal.domain.ports.primary.query.GetTasks
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.NoContent
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import sTrying

private fun <R : DRequest, T> withValidRequest(request: R, ifValid: (R) -> DResult<T>): DResult<T> =
    request.validate()
        .fold(
            { nel -> TaskError.InvalidRules(nel.all.joinToString(separator = ", ") { it.message }).left() },
            { ifValid(request) }
        )

private suspend inline fun <reified T : Any> withValidBody(call: ApplicationCall): DResult<T> =
    Either.sTrying({ call.receive() }, { TaskError.InvalidRules("Invalid body") })

private fun toHttpError(error: TaskError): Pair<HttpStatusCode, String> {
    return when (error) {
        is TaskError.InvalidRules -> HttpStatusCode.BadRequest to error.message
        is TaskError.AlreadyExist -> HttpStatusCode.Conflict to error.message
        is TaskError.InvalidState -> HttpStatusCode.BadRequest to error.message
        is TaskError.NotFound -> HttpStatusCode.NotFound to error.message
        is TaskError.Unknown -> HttpStatusCode.InternalServerError to error.message
    }
}

data class TaskController(
    val getTasks: GetTasks,
    val getTask: GetTask,
    val createTask: CreateTask,
    val moveTask: MoveTask,
    val deleteTask: DeleteTask
) {
    suspend fun all(call: ApplicationCall) {
        getTasks.all().fold(
            { toHttpError(it).let { (status, message) -> call.respond(status, message) } },
            { call.respond(OK, it) }
        )
    }

    suspend fun get(call: ApplicationCall) {
        val getTaskRequest = GetTaskRequest(call.parameters["id"] ?: "")
        withValidRequest(getTaskRequest) {
            getTask.get(it)
        }.fold(
            { toHttpError(it).let { (status, message) -> call.respond(status, message) } },
            { call.respond(OK, it) }
        )
    }

    suspend fun create(call: ApplicationCall) {
        val createTaskRequest = call.receive<CreateTaskRequest>()
        withValidRequest(createTaskRequest) {
            createTask.create(it)
        }.fold(
            { toHttpError(it).let { (status, message) -> call.respond(status, message) } },
            { call.respond(Created, it) }
        )
    }

    suspend fun move(call: ApplicationCall) {
        val id = call.parameters["id"] ?: ""
        withValidBody<StateBody>(call).flatMap { body ->
            val moveTaskRequest = MoveTaskRequest(id, body.state)
            withValidRequest(moveTaskRequest) {
                moveTask.move(it)
            }
        }.fold(
            { toHttpError(it).let { (status, message) -> call.respond(status, message) } },
            { call.respond(OK, it) }
        )
    }

    suspend fun delete(call: ApplicationCall) {
        val deleteTaskRequest = DeleteTaskRequest(call.parameters["id"] ?: "")
        withValidRequest(deleteTaskRequest) {
            deleteTask.delete(it)
        }.fold(
            { toHttpError(it).let { (status, message) -> call.respond(status, message) } },
            { call.respond(NoContent) }
        )
    }

    companion object {
        internal data class StateBody(val state: String)
    }
}

fun Routing.tasks(controller: TaskController) {
    route("/tasks") {
        get { controller.all(call) }

        get("/{id}") { controller.get(call) }

        put { controller.create(call) }

        post("/move/{id}") { controller.move(call) }

        delete("/{id}") { controller.delete(call) }
    }
}
