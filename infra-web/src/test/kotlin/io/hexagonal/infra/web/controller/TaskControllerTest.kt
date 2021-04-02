package io.hexagonal.infra.web.controller

import arrow.core.left
import arrow.core.right
import io.hexagonal.domain.model.Task
import io.hexagonal.domain.model.TaskError.InvalidState
import io.hexagonal.domain.model.TaskError.NotFound
import io.hexagonal.domain.model.TaskError.Unknown
import io.hexagonal.domain.model.TaskState
import io.hexagonal.domain.ports.primary.command.CreateTaskRequest
import io.hexagonal.domain.ports.primary.command.DeleteTaskRequest
import io.hexagonal.domain.ports.primary.command.MoveTaskRequest
import io.hexagonal.domain.ports.primary.query.GetTaskRequest
import io.hexagonal.domain.usecase.command.CreateTaskUseCase
import io.hexagonal.domain.usecase.command.DeleteTaskUseCase
import io.hexagonal.domain.usecase.command.MoveTaskUseCase
import io.hexagonal.domain.usecase.query.GetTaskUseCase
import io.hexagonal.domain.usecase.query.GetTasksUseCase
import io.kotest.core.spec.style.FunSpec
import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NoContent
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import java.util.*

class TaskControllerTest : FunSpec() {

    private val getTask = mockk<GetTaskUseCase>()
    private val getTasks = mockk<GetTasksUseCase>()
    private val moveTask = mockk<MoveTaskUseCase>()
    private val createTask = mockk<CreateTaskUseCase>()
    private val deleteTask = mockk<DeleteTaskUseCase>()
    private val controller = TaskController(getTasks, getTask, createTask, moveTask, deleteTask)

    init {
        beforeEach {
            clearAllMocks()
            mockkStatic("io.ktor.request.ApplicationReceiveFunctionsKt")
            mockkStatic("io.ktor.response.ApplicationResponseFunctionsKt")
        }

        test("'get' must return a 200 OK when task exists") {
            // Given
            val id = "0661ef75-3e57-47d1-b8b0-c90d7be6d653"
            val task = Task(UUID.fromString(id), "name", "content", TaskState.IN_PROGRESS)

            val call = spyk<ApplicationCall>()
            coEvery { call.parameters["id"] } returns id
            coEvery { getTask.get(GetTaskRequest(id)) } returns task.right()
            coEvery { call.response.status(any()) } just Runs
            coEvery { call.respond(any(), any()) } just Runs

            // When
            controller.get(call)

            // Then
            coVerify { call.respond(OK, task) }
        }

        test("'get' must return a 404 Not Found when task does not exist") {
            // Given
            val id = "0661ef75-3e57-47d1-b8b0-c90d7be6d653"
            val call = spyk<ApplicationCall>()
            coEvery { call.parameters["id"] } returns id
            coEvery { getTask.get(GetTaskRequest(id)) } returns NotFound("Force a not found").left()
            coEvery { call.response.status(any()) } just Runs
            coEvery { call.respond(any(), any()) } just Runs

            // When
            controller.get(call)

            // Then
            coVerify { call.respond(NotFound, "Force a not found") }
        }

        test("'all' must return a 200 OK with existing tasks") {
            // Given
            val getTask = mockk<GetTaskUseCase>()
            val getTasks = mockk<GetTasksUseCase>()
            val moveTask = mockk<MoveTaskUseCase>()
            val createTask = mockk<CreateTaskUseCase>()
            val deleteTask = mockk<DeleteTaskUseCase>()
            val controller = TaskController(getTasks, getTask, createTask, moveTask, deleteTask)

            val t1 = Task(UUID.randomUUID(), "name1", "content1", TaskState.IN_PROGRESS)
            val t2 = Task(UUID.randomUUID(), "name2", "content2", TaskState.DONE)
            val tasks = listOf(t1, t2)

            val call = spyk<ApplicationCall>()
            coEvery { getTasks.all() } returns tasks.right()
            coEvery { call.response.status(any()) } just Runs
            coEvery { call.respond(any(), any()) } just Runs

            // When
            controller.all(call)

            // Then
            coVerify { call.respond(OK, tasks) }
        }

        test("'all' must return a 500 when an error occurs") {
            // Given
            val call = spyk<ApplicationCall>()
            coEvery { getTasks.all() } returns Unknown("Force an unknown error").left()
            coEvery { call.response.status(any()) } just Runs
            coEvery { call.respond(any(), any()) } just Runs

            // When
            controller.all(call)

            // Then
            coVerify { call.respond(InternalServerError, "Force an unknown error") }
        }

        test("'create' must return a 201 Created when task successfully created") {
            // Given
            val task = Task(UUID.randomUUID(), "name", "content", TaskState.TODO)
            val request = CreateTaskRequest("name", "content")

            val call = spyk<ApplicationCall>()
            coEvery { createTask.create(request) } returns task.right()
            coEvery { call.receive<CreateTaskRequest>() } returns request
            coEvery { call.response.status(any()) } just Runs
            coEvery { call.respond(any(), any()) } just Runs

            // When
            controller.create(call)

            // Then
            coVerify { call.respond(Created, task) }
        }

        test("'create' must return a 400 Bad Request when request is invalid") {
            // Given
            val request = CreateTaskRequest("", "content")

            val call = spyk<ApplicationCall>()
            coEvery { call.receive<CreateTaskRequest>() } returns request
            coEvery { call.response.status(any()) } just Runs
            coEvery { call.respond(any(), any()) } just Runs

            // When
            controller.create(call)

            // Then
            coVerify { call.respond(BadRequest, "Task name is blank") }
        }


        test("'delete' must return a 400 Bad Request when request is invalid") {
            // Given
            val call = spyk<ApplicationCall>()
            coEvery { call.parameters["id"] } returns "invalid-id"
            coEvery { call.response.status(any()) } just Runs
            coEvery { call.respond(any(), any()) } just Runs

            // When
            controller.delete(call)

            // Then
            coVerify { call.respond(BadRequest, "Task id is invalid") }
        }

        test("'delete' must return a 204 No Content when request is valid and task successfully deleted'") {
            // Given
            val call = spyk<ApplicationCall>()
            val id = "0661ef75-3e57-47d1-b8b0-c90d7be6d653"
            val request = DeleteTaskRequest(id)
            coEvery { call.parameters["id"] } returns id
            coEvery { deleteTask.delete(request) } returns Unit.right()
            coEvery { call.response.status(any()) } just Runs
            coEvery { call.respond(any()) } just Runs

            // When
            controller.delete(call)

            // Then
            coVerify { call.respond(NoContent) }
        }

        test("'move' must return a 400 Bad Request when request is invalid") {
            // Given
            val call = spyk<ApplicationCall>()
            val stateBody = TaskController.Companion.StateBody("unknown_state")
            coEvery { call.parameters["id"] } returns "invalid-id"
            coEvery { call.response.status(any()) } just Runs
            coEvery { call.respond(any(), any()) } just Runs
            coEvery { call.receive<TaskController.Companion.StateBody>() } returns stateBody

            // When
            controller.move(call)

            // Then
            coVerify { call.respond(BadRequest, "Task id is invalid, Task state is invalid") }
        }

        test("'move' must return a 400 Bad Request when requested state is invalid") {
            // Given
            val call = spyk<ApplicationCall>()
            val id = "0661ef75-3e57-47d1-b8b0-c90d7be6d653"
            val stateBody = TaskController.Companion.StateBody("DONE")
            val request = MoveTaskRequest(id, "DONE")
            coEvery { call.parameters["id"] } returns id
            coEvery { moveTask.move(request) } returns InvalidState("Task has been done and is no longer modifiable.").left()
            coEvery { call.response.status(any()) } just Runs
            coEvery { call.respond(any(), any()) } just Runs
            coEvery { call.receive<TaskController.Companion.StateBody>() } returns stateBody

            // When
            controller.move(call)

            // Then
            coVerify { call.respond(BadRequest, "Task has been done and is no longer modifiable.") }
        }

        test("'move' must return a 200 OK when request is valid and move successfully done'") {
            // Given
            val call = spyk<ApplicationCall>()
            val id = "0661ef75-3e57-47d1-b8b0-c90d7be6d653"
            val task = Task(UUID.fromString(id), "name", "content", TaskState.IN_PROGRESS)
            val stateBody = TaskController.Companion.StateBody("CANCELLED")
            val request = MoveTaskRequest(id, "CANCELLED")
            val cancelled = task.copy(state = TaskState.CANCELLED)
            coEvery { call.parameters["id"] } returns id
            coEvery { moveTask.move(request) } returns cancelled.right()
            coEvery { call.response.status(any()) } just Runs
            coEvery { call.respond(any(), any()) } just Runs
            coEvery { call.receive<TaskController.Companion.StateBody>() } returns stateBody

            // When
            controller.move(call)

            // Then
            coVerify { call.respond(OK, cancelled) }
        }
    }
}
