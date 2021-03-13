object Versions {
    val kotlin = "1.4.31"
    val arrow = "0.11.0"
    val ktor = "1.5.2"
    val logback = "1.2.3"
    val caffeine = "2.9.0"
}

object TestVersions {
    val kotest = "4.4.1"
//    val kotestRunnerConsoleJvm = "4.1.3.2"
    val junit = "5.5.1"
    val mockk = "1.10.6"
}

object Deps {
    val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    val arrowCore = "io.arrow-kt:arrow-core:${Versions.arrow}"
    val arrowCoreData = "io.arrow-kt:arrow-core-data:${Versions.arrow}"
    val arrowSyntax = "io.arrow-kt:arrow-syntax:${Versions.arrow}"
    val ktorServerCore = "io.ktor:ktor-server-core:${Versions.ktor}"
    val ktorServerNetty = "io.ktor:ktor-server-netty:${Versions.ktor}"
    val ktorJackson = "io.ktor:ktor-jackson:${Versions.ktor}"
    val logbackClassic = "ch.qos.logback:logback-classic:${Versions.logback}"
    val caffeine = "com.github.ben-manes.caffeine:caffeine:${Versions.caffeine}"
}

object TestDeps {
    val ktorServerTests = "io.ktor:ktor-server-tests:${Versions.ktor}"
    val kotestRunnerJunit5 = "io.kotest:kotest-runner-junit5-jvm:${TestVersions.kotest}"
    val kotestAssertions = "io.kotest:kotest-assertions-core-jvm:${TestVersions.kotest}"
    val kotestProperty = "io.kotest:kotest-property-jvm:${TestVersions.kotest}"
    val junitJupiterApi = "org.junit.jupiter:junit-jupiter-api:${TestVersions.junit}"
    val junitJupiterEngine = "org.junit.jupiter:junit-jupiter-engine:${TestVersions.junit}"
    val mockk = "io.mockk:mockk:${TestVersions.mockk}"
}