import dev.deftu.gradle.bloom.capitalize
import jdk.jfr.internal.JVM.exclude
import jdk.jfr.internal.JVM.include
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.invoke
import kotlin.reflect.KProperty
import net.ornithemc.ploceus.api.PloceusGradleExtensionApi
import me.modmuss50.mpp.ReleaseType

// in stonecutter.gradle.kts
class CommonProperty<T> {
	operator fun getValue(thisRef: Any?, property: KProperty<*>): T = (rootProject.extra[sc.current.project] as Map<String, Any?>)[property.name] as T
}
val ornithe by CommonProperty<Boolean>()
val environment by CommonProperty<String>()
val modName by CommonProperty<String>()
val modId by CommonProperty<String>()
val modDescription by CommonProperty<String>()
val modIcon by CommonProperty<String>()
val oneconfigVersion by CommonProperty<String>()
val rangedVersion by CommonProperty<Boolean>()
val maxMc by CommonProperty<String?>()
val finalFileName by CommonProperty<String>()
val modrinthReadme by CommonProperty<String>()
val versionType: String by project
val license: String by project
val modrinthId: String by project
val javaVersion = JavaVersion.VERSION_25
val fabricLoaderVersion = sc.properties.getAs<String>("versions.fabricloader")
val featherVersion = if (ornithe) sc.properties["versions.feather"] else null
val fabricApiVersion = if (!ornithe) sc.properties.getAs<String>("versions.fabricapi") else null
val oslCoreVersion = if (ornithe) sc.properties["versions.oslcore"] else null
val oslEntrypointsVersion = if (ornithe) sc.properties["versions.oslentrypoints"] else null

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
	id("ploceus") version "1.17.4" apply false
	id("dev.deftu.gradle.tools.bloom") version "2.73.0"
	id("me.modmuss50.mod-publish-plugin") version "2.1.1"
}

// taken directly from polyfrost polysprint https://github.com/Polyfrost/PolySprint/blob/legacy/build.gradle.kts
val ploceus = if (ornithe) {
	pluginManager.apply("ploceus")

	configurations.configureEach {
		exclude(group = "org.lwjgl.lwjgl")
	}

	extensions.getByType<PloceusGradleExtensionApi>().apply {
		setIntermediaryGeneration(2)
	}
} else {
	null
}

dependencies {
	minecraft("com.mojang:minecraft:${sc.current.version}")
	if (ornithe) mappings(ploceus!!.featherMappings(featherVersion))
	implementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
	implementation("org.polyfrost.oneconfig:${sc.current.version}-$environment:$oneconfigVersion")

	// oneconfig provides hypixel mod api for now
	if (ornithe) {
		implementation("net.ornithemc.osl-gen2:core:${oslCoreVersion}")
		implementation("net.ornithemc.osl-gen2:entrypoints:${oslEntrypointsVersion}")
	}
	else {
		implementation("net.fabricmc.fabric-api:fabric-api:${fabricApiVersion}")
	}
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

bloom {
	replacement("@MC_VERSION@", sc.current.version)
	replacement("@MOD_LOADER@", environment)
	// w deftu for capitalize
	replacement("@FORMATTED_MOD_LOADER@", environment.capitalize())
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
			val minecraftDependency =
				if (rangedVersion) ">=${sc.current.version} <=${maxMc}" else sc.current.version
			register("minecraft", minecraftDependency)
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

publishMods {
	val env = environment
	file.set(loomx.modJar.map { it.archiveFile.get() })
	changelog.set(rootProject.file("CHANGELOG.md").readText())
	type.set(ReleaseType.of(versionType))
	modLoaders.add(env)

	modrinth {
		displayName.set("${project.version.toString()} for ${env.capitalize()} ${sc.current.version}")
		accessToken = System.getenv("MODRINTH_TOKEN")
		projectId.set(modrinthId)
		environment = CLIENT_ONLY
		projectDescription = modrinthReadme
		if (rangedVersion) {
			minecraftVersionRange {
				start = sc.current.version
				end = maxMc
			}
		}
		else {
			minecraftVersions.add(sc.current.version)
		}

		requires("oneconfig")
	}
}

java {
	sourceCompatibility = javaVersion
	targetCompatibility = javaVersion
}