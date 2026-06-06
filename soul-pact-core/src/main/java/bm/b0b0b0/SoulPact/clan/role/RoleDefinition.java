package bm.b0b0b0.SoulPact.clan.role;

public final class RoleDefinition {

    private final String key;
    private final String title;
    private final boolean listNames;

    public RoleDefinition(String key, String title, boolean listNames) {
        this.key = key;
        this.title = title;
        this.listNames = listNames;
    }

    public String key() {
        return key;
    }

    public String title() {
        return title;
    }

    public boolean listNames() {
        return listNames;
    }
}
