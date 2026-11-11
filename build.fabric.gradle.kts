import dev.deftu.gradle.bloom.capitalize
import jdk.jfr.internal.JVM.exclude
import jdk.jfr.internal.JVM.include
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.invoke
import kotlin.reflect.KProperty
import org.gradle.internal.Transformers.type

// in stonecutter.gradle.kts
class CommonProperty<T> {
	operator fun getValue(thisRef: Any?, property: KProperty<*>): T = (rootProject.extra[sc.current.project] as Map<String, Any?>)[property.name] as T
}
val modName by CommonProperty<String>()
val modId by CommonProperty<String>()
val modDescription by CommonProperty<String>()
val modIcon by CommonProperty<String>()
val oneconfigVersion by CommonProperty<String>()
val finalFileName by CommonProperty<String>()
val modrinthReadme by CommonProperty<String>()
val versionType: String by project
val license: String by project
val modrinthId: String by project
val javaVersion = JavaVersion.VERSION_25
val fabricLoaderVersion = sc.properties.getAs<String>("versions.fabricloader")

repositories {
	fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
		forRepository { maven(url) { name = alias } }
		filter { groups.forEach(::includeGroup) }
	}

	mavenCentral()
	gradlePluginPortal()
	google()
	maven("https://repo.polyfrost.cc/releases")
	maven("https://repo.polyfrost.org/releases")
	maven("https://repo.polyfrost.org/snapshots")
	maven("https://maven.terraformersmc.com/releases")
	maven("https://repo.hypixel.net/repository/Hypixel/")
	maven("https://maven.fabricmc.net/releases")
	maven("https://maven.ornithemc.net/releases")
	maven("https://maven.ornithemc.net/snapshots")
	maven("https://central.sonatype.com/repository/maven-snapshots") {
		name = "Sonatype Snapshots"
		content { includeGroup("net.kyori") }
	}
	maven("https://maven.cloverclient.com/releases") {
		content { includeGroup("pl.tomgirl") }
	}
	strictMaven("https://maven.deftu.dev/releases", "Deftu", "dev.deftu")
	strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
	strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

plugins {
	id("dev.kikugie.loom-back-compat") version "0.4.2"
	id("net.fabricmc.fabric-loom-remap") version "1.17.4" apply false
	id("dev.deftu.gradle.tools.bloom") version "2.73.0"
	id("me.modmuss50.mod-publish-plugin") version "2.1.1"
	id("org.jetbrains.kotlin.jvm") version "2.4.10"
}

dependencies {
	minecraft("com.mojang:minecraft:${sc.current.version}")
	implementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
	implementation("org.polyfrost.oneconfig:${sc.current.version}-fabric:$oneconfigVersion")
}

loom {
	runConfigs.remove(runConfigs["server"])

	runConfigs.all {
		runDir = "../../run"
		val resourcePackDir: String? = System.getenv("minecraft.resourcePackDir")
		if (!resourcePackDir.isNullOrBlank()) {
			println("Using resource pack directory $resourcePackDir from environment variable minecraft.resourcePackDir")
			programArgs("--resourcePackDir", resourcePackDir)
		}
	}
}

tasks {
	processResources {
		fun MutableMap<String, String>.register(key: String, value: String) {
			inputs.property(key, value)
			set(key, value)
		}
		fun target(version: String?) = ">=$version"

		val props = buildMap {
			register("modName", modName)
			register("modId", modId)
			register("modDescription", modDescription)
			register("modIcon", modIcon)
			register("license", license)
			register("version", version.toString())
			register("java", target(javaVersion.majorVersion))
			register("fabricLoader", target(fabricLoaderVersion))
			register("oneconfig", target(oneconfigVersion))
		}
		filesMatching(listOf("fabric.mod.json")) { expand(props) }

		outputs.upToDateWhen { false }
	}

	register<Copy>("buildAndCollect") {
		group = "build"

		from(loomx.modJar.map { it.archiveFile })
		into(rootProject.layout.buildDirectory.file("libs"))
		dependsOn("build")
	}

	loomx.modJar {
		archiveFileName = finalFileName
		// manifest.attributes(mapOf())
	}
}

java {
	sourceCompatibility = javaVersion
	targetCompatibility = javaVersion
}