plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(Deps.kotlinStdLib)
    implementation(project(":domain"))
    implementation(Deps.arrowCore)
    implementation(Deps.caffeine)
}
