import kotlin.reflect.KProperty
import kotlin.text.replace

plugins {
    id("dev.kikugie.stonecutter")
}

val modName: String by project
val modId: String by project
val modIcon: String by project
val modrinthLogoLink: String by project

stonecutter active "26.3-fabric"

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

    val modName by Declare(extra["mod.name"])
    val modId by Declare(extra["mod.id"])
    val modDescription by Declare(extra["mod.description"])
    val modIcon by Declare(extra["modIcon"])
    val oneconfigVersion by Declare(properties.getAs<String>("versions.oneconfig"))
    val finalFileName by Declare("$modName-$version+${current.project}.jar")

    val modrinthReadme by Declare(rootProject.file("README.md").readText()
        .replace("src/main/resources/$modIcon", modrinthLogoLink)
    )
}