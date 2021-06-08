import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    id("org.jetbrains.compose") version "0.4.0"
//    id("com.squareup.sqldelight") version "1.5.0"
}

group = "com.falconer.utils"
version = "0.1"

repositories {
    jcenter()
    google()
    mavenCentral()
//    maven { url= uri("https://www.jetbrains.com/intellij-repository/releases") }
//    maven { url= uri( "https://jetbrains.bintray.com/intellij-third-party-dependencies") }
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

val koinVersion = "3.0.2"
val sqlDelightVersion="1.5.0"

dependencies {
    implementation(compose.desktop.currentOs)

    // https://mvnrepository.com/artifact/commons-io/commons-io
    implementation("commons-io:commons-io:2.9.0")

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")


//    implementation("com.squareup.sqldelight:gradle-plugin:$sqlDelightVersion")
//    implementation("com.squareup.sqldelight:sqlite-driver:$sqlDelightVersion")
//    implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
//    implementation("com.squareup.sqldelight:coroutines-extensions:$sqlDelightVersion")

//    // Koin for Kotlin Multiplatform
//    implementation("io.insert-koin:koin-core:$koinVersion")
//    // Koin Test for Kotlin Multiplatform
//    testImplementation("io.insert-koin:koin-test:$koinVersion")
}

//
//sqldelight {
//    database("AppDatabase") {
//        packageName = "com.falconer.utils.forrest.db"
//    }
//}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "15"
}

compose.desktop {
    application {
        mainClass = "com.falconer.utils.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Forrest"
            packageVersion = "1.0.0"
        }
    }
}