package bm.b0b0b0.SoulPact.api.war;

import bm.b0b0b0.SoulPact.api.SoulPactGuiExtension;

public interface ClanWarProvider extends SoulPactGuiExtension {

    ClanWarUiBridge ui();

    WarFlagBreakGate flagBreak();
}
