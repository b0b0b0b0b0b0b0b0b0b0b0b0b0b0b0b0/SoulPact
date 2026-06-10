package bm.b0b0b0.SoulPact.quests.message;

public final class QuestsConsolePalette {

    public static final String PREFIX = "\u001B[37m[\u001B[90mSoulPact-Quests\u001B[37m]\u001B[0m ";

    private QuestsConsolePalette() {
    }

    public static String prefixLine(String message) {
        return PREFIX + message;
    }

    public static String green(String message) {
        return "\u001B[32m" + message + "\u001B[0m";
    }

    public static String yellow(String message) {
        return "\u001B[33m" + message + "\u001B[0m";
    }

    public static String red(String message) {
        return "\u001B[31m" + message + "\u001B[0m";
    }
}
