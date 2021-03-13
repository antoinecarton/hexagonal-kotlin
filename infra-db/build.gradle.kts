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
    implementation(Deps.arrowCoreData)
    implementation(Deps.arrowSyntax)
    implementation(Deps.caffeine)
}
