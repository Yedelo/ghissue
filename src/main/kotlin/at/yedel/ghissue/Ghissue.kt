package at.yedel.ghissue

import at.yedel.ghissue.config.GhissueConfig
import at.yedel.ghissue.features.GhissueCommand
import net.fabricmc.api.ClientModInitializer

object Ghissue : ClientModInitializer {
    override fun onInitializeClient() {
        GhissueConfig
        GhissueCommand.register()
    }
}