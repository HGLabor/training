import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val jvmVersion = JavaVersion.VERSION_11
val jvmVersionString = jvmVersion.majorVersion.let { if (it.toInt() <= 10) "1.$it" else it }

group = "de.hglabor.plugins"
version = "0.0.1"

plugins {
    kotlin("jvm") version "1.5.0"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    kotlin("plugin.serialization") version "1.5.0"
}

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://raw.githubusercontent.com/JorelAli/CommandAPI/mvn-repo/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    // Paper
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    // Craftbukkit
    compileOnly("org.bukkit:craftbukkit:1.16.5-R0.1-SNAPSHOT")
    // World Edit
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.0")
    // CommandAPI
    implementation("dev.jorel:commandapi-shade:5.8")
    // HGLaborUtils
    implementation("de.hglabor:hglabor-utils:0.0.6")
    // KSpigot
    implementation("net.axay:kspigot:1.16.26")
    // BlueUtils
    compileOnly("net.axay", "BlueUtils", "1.0.2")
    // KMongo
    compileOnly("org.litote.kmongo", "kmongo-core", "4.2.3")
    compileOnly("org.litote.kmongo", "kmongo-serialization-mapping", "4.2.3")
}

java.sourceCompatibility = jvmVersion
java.targetCompatibility = jvmVersion

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = jvmVersionString
}

tasks {
    shadowJar {
        dependencies {
            //exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib.*"))
        }
        minimize()
        simpleRelocate("net.axay.kspigot")
    }
}

fun ShadowJar.simpleRelocate(pattern: String) {
    relocate(pattern, "${project.group}.${project.name.toLowerCase()}.shadow.$pattern")
}