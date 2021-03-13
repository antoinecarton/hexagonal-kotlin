package io.hexagonal.infra.web

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import io.hexagonal.domain.ports.secondary.TaskPort
import io.hexagonal.domain.usecase.command.CreateTaskUseCase
import io.hexagonal.domain.usecase.command.DeleteTaskUseCase
import io.hexagonal.domain.usecase.command.MoveTaskUseCase
import io.hexagonal.domain.usecase.query.GetTaskUseCase
import io.hexagonal.domain.usecase.query.GetTasksUseCase
import io.hexagonal.infra.db.InMemoryTaskDb
import io.hexagonal.infra.web.controller.TaskController
import io.hexagonal.infra.web.controller.tasks
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

    val taskPort: TaskPort = InMemoryTaskDb()
    val getTask = GetTaskUseCase(taskPort)
    val getTasks = GetTasksUseCase(taskPort)
    val moveTask = MoveTaskUseCase(taskPort)
    val createTask = CreateTaskUseCase(taskPort)
    val deleteTask = DeleteTaskUseCase(taskPort)

    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
            setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                indentObjectsWith(DefaultIndenter("  ", "\n"))
            })
        }
    }

    routing {
        tasks(TaskController(getTasks, getTask, createTask, moveTask, deleteTask))
    }
}