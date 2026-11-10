package at.yedel.meowconfig.triggers;



import at.yedel.meowconfig.config.MeowConfigConfig;
import at.yedel.meowconfig.features.Meow;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.polyfrost.oneconfig.api.event.v1.events.ChatEvent;

import java.util.Objects;



public class MeowChatPatterns {
    private static final MeowChatPatterns INSTANCE = new MeowChatPatterns();

    public static MeowChatPatterns getInstance() {
        return INSTANCE;
    }

    private MeowChatPatterns() {
        EventManager.register(ChatEvent.Receive.class, (event) -> {
            if (MeowConfigConfig.getInstance().enabled && MeowConfigConfig.getInstance().meowOnChatPatterns) {
                String message = event.getFullyUnformattedMessage();
                for (String chatMessage: MeowConfigConfig.getInstance().chatMessages) {
                    if (Objects.equals(message, chatMessage)) {
                        Meow.getInstance().meow();
                        return;
                    }
                }
                for (String chatPattern: MeowConfigConfig.getInstance().chatPatterns) {
                    if (message.matches(chatPattern)) {
                        Meow.getInstance().meow();
                        return;
                    }
                }
            }
        });
    }
}
