package at.yedel.meowconfig.features;



import at.yedel.meowconfig.config.MeowConfigConfig;
import at.yedel.meowconfig.utils.MeowMethod;
import at.yedel.meowconfig.utils.MeowPlatform;



public class Meow {
    private static final Meow INSTANCE = new Meow();

    public static Meow getInstance() {
        return INSTANCE;
    }

    public void meow() {
        String message = getFullMessage();
        boolean meowed = false;
        for (int i = 0; i < MeowMethod.values().length; i ++) {
            if (MeowConfigConfig.getInstance().meowMethods[i]) {
                MeowMethod meowMethod = MeowMethod.values()[i];
                meowMethod.meow(message);
                meowed = true;
            }
        }
        if (meowed) {
            if (MeowConfigConfig.getInstance().playSoundOnMeow) {
                String[] meowSounds = MeowConfigConfig.getInstance().meowSounds;
                if (meowSounds.length > 0) {
                    String meowSound = meowSounds[(int) (Math.random() * meowSounds.length)];
                    MeowPlatform.getInstance().playSound(meowSound, MeowConfigConfig.getInstance().meowPitch, MeowConfigConfig.getInstance().meowVolume);
                }
            }
            MeowTracking.getInstance().incrementMeows();
            MeowTracking.getInstance().incrementCharacters(message.length());
        }
    }

    private String getFullMessage() {
        StringBuilder builder = new StringBuilder();
        if (!MeowConfigConfig.getInstance().prefix.isEmpty()) {
            append(builder, MeowConfigConfig.getInstance().prefix);
            append(builder, " ");
        }
        append(builder, MeowConfigConfig.getInstance().immediatePrefix);
        append(builder, MeowConfigConfig.getInstance().meowMessage);
        append(builder, MeowConfigConfig.getInstance().immediateSuffix);
        if (!MeowConfigConfig.getInstance().suffix.isEmpty()) {
            append(builder, " ");
            append(builder, MeowConfigConfig.getInstance().suffix);
        }
        return builder.toString();
    }

    private void append(StringBuilder builder, String string) {
        builder.append(
            string
                .replace("${meows}", String.valueOf(MeowTracking.getInstance().getMeows()))
                .replace("${characters}", String.valueOf(MeowTracking.getInstance().getCharacters()))
                .replace("${summary}", MeowTracking.getInstance().getSummary())
        );
    }
}
