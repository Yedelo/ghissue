package at.yedel.meowconfig.utils;



import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.client.Minecraft;
// slime me
//~ if modern 'resource' -> 'resources'
import net.minecraft.resources.Identifier;
//~ if modern 'sound.instance' -> 'resources.sounds'
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
//? if modern {
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
//?}
import org.polyfrost.oneconfig.api.notifications.v1.Notifications;
import org.polyfrost.oneconfig.api.platform.v1.Platform;



public class MeowPlatform {
    private static final MeowPlatform INSTANCE = new MeowPlatform();

    public static MeowPlatform getInstance() {
        return INSTANCE;
    }

    //? if legacy {
    // public static final Component LOGO = Component.text("§d§l[MeowConfig]");
    //?} else {
    public static final Component LOGO = MiniMessage.miniMessage().deserialize("<gradient:#f05be1:#ffb3f7>[MeowConfig]");
    //?}

    public void sendChatMessage(String message) {
        if (Minecraft.getInstance().player != null) {
            //~ if modern 'sendChat' -> 'connection.sendChat'
            Minecraft.getInstance().player.connection.sendChat(message);
        }
    }

    public void receiveChatMessage(String message) {
        Platform.compatibility().displayChatMessage(message);
    }

    public void receiveChatMessageWithLogo(String message) {
        Platform.compatibility().displayChatMessage(LOGO.append(Component.space()).append(Component.text(message)));
    }

    public void receiveNotification(String message) {
        Notifications.info("MeowConfig", message);
    }

    public void playSound(String sound, float pitch, float volume) {
        //? if legacy {
      //SimpleSoundInstance soundInstance = SimpleSoundInstance.of(new Identifier(sound), pitch);
        //?} else {
        SoundEvent event = BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse(sound));
        if (event == null) return;
        SimpleSoundInstance soundInstance = SimpleSoundInstance.forUI(event, pitch, volume);
        //?}
        if (soundInstance == null) return;
        Minecraft.getInstance().getSoundManager().play(soundInstance);
    }

    public void schedule(Runnable runnable) {
        //~ if modern 'executeTask' -> 'schedule'
        Minecraft.getInstance().schedule(runnable);
    }
}
