package at.yedel.ghissue.features

import at.yedel.ghissue.config.GhissueConfig
import at.yedel.ghissue.data.Repo
import at.yedel.ghissue.data.RepoArgumentType
import org.polyfrost.oneconfig.api.commands.v1.CommandManager
import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.Command
import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.Handler
import org.polyfrost.oneconfig.api.platform.v1.Platform
import org.polyfrost.oneconfig.utils.v1.dsl.openUI


@Command("ghissue")
object GhissueCommand {
    fun register() {
        CommandManager.registerArgumentType(Repo::class.java, RepoArgumentType)
        CommandManager.register(this)
    }

    @Handler
    fun main() {
        GhissueConfig.openUI()
    }

    @Handler
    fun issue(repo: Repo, issue: String) {
        Platform.compatibility().displayChatMessage("Repo $repo issue $issue")
    }
}