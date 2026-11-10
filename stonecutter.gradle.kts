import kotlin.reflect.KProperty
import kotlin.text.replace

plugins {
    id("dev.kikugie.stonecutter")
    id("me.modmuss50.mod-publish-plugin") version "2.1.1"
}

val modName: String by project
val modId: String by project
val modIcon: String by project
val modrinthLogoLink: String by project

stonecutter active "26.1-fabric"

stonecutter parameters {
    val shared = mutableMapOf<String, Any?>()
    extra[current.project] = shared

    class Declare<T>(private val value: T) {
        operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): Declare<T> {
            shared[property.name] = value
            return this
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value
    }

    val loader by Declare(current.project.split("-")[1])
    val ornithe by Declare(current.version == "1.8.9")
    val environment by Declare(if (ornithe) "ornithe" else "fabric")
    val legacy by Declare(current.parsed <= "1.8.9")
    val modern by Declare(!legacy)

    constants {
        // alrighty
        match(loader, "fabric")
        this["legacy"] = legacy
        this["modern"] = modern
    }

    val modName by Declare(extra["mod.name"])
    val modId by Declare(extra["mod.id"])
    val modDescription by Declare(extra["mod.description"])
    val modIcon by Declare(extra["modIcon"])
    val oneconfigVersion by Declare(properties.getAs<String>("versions.oneconfig"))
    val rangedVersion by Declare(properties.getAs<String>("versioning") == "range")
    val maxMc by Declare(if (rangedVersion) properties.getAs<String>("mc.max") else null)
    val minecraftTarget by Declare(if (rangedVersion) "${current.version}-$maxMc" else current.version)
    val finalFileName by Declare("$modName-$version+$minecraftTarget-$environment.jar")

    val modrinthReadme by Declare(rootProject.file("README.md").readText()
        .replace("src/main/resources/$modIcon", modrinthLogoLink)
    )
}