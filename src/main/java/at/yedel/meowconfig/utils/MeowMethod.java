package at.yedel.meowconfig.utils;



import at.yedel.meowconfig.config.MeowConfigConfig;

import java.util.function.Consumer;



public enum MeowMethod {
    SEND((message) -> {
        String sendMessage = MeowConfigConfig.getInstance().sendPrefix + message + MeowConfigConfig.getInstance().sendSuffix;
        MeowPlatform.getInstance().sendChatMessage(sendMessage);
    }),
    RECEIVE((message) -> MeowPlatform.getInstance().receiveChatMessage(message)),
    NOTIFICATION((message) -> MeowPlatform.getInstance().receiveNotification(message));

    private final Consumer<String> meower;

    MeowMethod(Consumer<String> meower) {
        this.meower = meower;
    }

    public void meow(String message) {
        MeowPlatform.getInstance().schedule(() -> {
            meower.accept(message);
        });
    }
}
