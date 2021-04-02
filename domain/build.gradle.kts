plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(Deps.kotlinStdLib)
    implementation(Deps.arrowCore)

    testImplementation(TestDeps.kotestRunnerJunit5)
    testImplementation(TestDeps.kotestAssertions)
    testImplementation(TestDeps.kotestProperty)
    testImplementation(TestDeps.junitJupiterApi)
    testRuntimeOnly(TestDeps.junitJupiterEngine)
    testImplementation(TestDeps.mockk)
}
