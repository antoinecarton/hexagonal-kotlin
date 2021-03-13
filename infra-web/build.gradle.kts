plugins {
    kotlin("jvm")
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(Deps.kotlinStdLib)
    implementation(project(":domain"))
    implementation(project(":infra-db"))
    implementation(Deps.arrowCore)
    implementation(Deps.arrowCoreData)
    implementation(Deps.arrowSyntax)
    implementation(Deps.ktorServerCore)
    implementation(Deps.ktorServerNetty)
    implementation(Deps.ktorJackson)
    implementation(Deps.logbackClassic)

    testImplementation(TestDeps.kotestRunnerJunit5)
    testImplementation(TestDeps.kotestAssertions)
    testImplementation(TestDeps.kotestProperty)
    testImplementation(TestDeps.mockk)
    testImplementation(TestDeps.junitJupiterApi)
    testRuntimeOnly(TestDeps.junitJupiterEngine)
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}
