package bm.b0b0b0.SoulPact.clan.gui;

public final class DeclareWarBlockGuiKeys {

    private DeclareWarBlockGuiKeys() {
    }

    public static String loreKey(String reasonId) {
        if (reasonId == null || reasonId.isBlank()) {
            return "clan.gui.info.item.declare-war-blocked.reason.generic";
        }
        return "clan.gui.info.item.declare-war-blocked.reason." + reasonId;
    }

    public static String chatKey(String reasonId) {
        if (reasonId == null || reasonId.isBlank()) {
            return "clan.gui.info.item.declare-war-blocked.chat.generic";
        }
        return "clan.gui.info.item.declare-war-blocked.chat." + reasonId;
    }
}
