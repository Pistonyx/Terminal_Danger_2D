package MainGame;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
/** This class wasn't made by me, I copied it off of the internet as I didn't know how to do it */
public final class Texts {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("messages");

    private Texts() {}

    public static String t(String key) {
        try {
            return BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return "??" + key + "??";
        }
    }

    public static String tf(String key, Object... args) {
        return MessageFormat.format(t(key), args);
    }
}
