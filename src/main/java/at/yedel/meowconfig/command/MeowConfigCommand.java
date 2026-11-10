package at.yedel.meowconfig.command;



import at.yedel.meowconfig.config.MeowConfigConfig;
import at.yedel.meowconfig.features.Meow;
import at.yedel.meowconfig.features.MeowTracking;
import at.yedel.meowconfig.utils.MeowPlatform;
import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.Command;
import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.Handler;
import org.polyfrost.oneconfig.utils.v1.dsl.ScreensKt;



@Command(value = "meowconfig", description = "The main command of MeowConfig")
public class MeowConfigCommand {
    private static final MeowConfigCommand INSTANCE = new MeowConfigCommand();

    public static MeowConfigCommand getInstance() {
        return INSTANCE;
    }

    @Handler(
        description = "The main command, hosting all subcommands. When used with no arguments, opens the config screen."
    )
    public void main() {
        ScreensKt.openUI(MeowConfigConfig.getInstance());
    }

    @Handler(
        description = "Meow"
    )
    public void meow() {
        Meow.getInstance().meow();
    }

    @Handler(
        description = "Shows tracked meowing stats."
    )
    public void stats() {
        MeowPlatform.getInstance().receiveChatMessageWithLogo(MeowTracking.getInstance().getSummary());
    }
}
