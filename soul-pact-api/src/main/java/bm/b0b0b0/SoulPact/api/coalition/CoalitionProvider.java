package bm.b0b0b0.SoulPact.api.coalition;

import bm.b0b0b0.SoulPact.api.SoulPactGuiExtension;

public interface CoalitionProvider extends SoulPactGuiExtension {

    CoalitionDisplayBridge display();

    CoalitionWarBridge war();
}
