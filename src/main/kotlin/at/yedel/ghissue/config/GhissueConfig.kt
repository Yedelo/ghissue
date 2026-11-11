package at.yedel.ghissue.config

import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.Text

object GhissueConfig : Config( "ghissue.json", "assets/ghissue/ghissue.png", "ghissue", Category.QOL) {

}