package bm.b0b0b0.SoulPact.quests.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.quests.model.QuestDefinition;
import bm.b0b0b0.SoulPact.quests.repository.SqlClanPointsRepository;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class QuestRewardService {

    private static final String NOTE_PREFIX = "quest:";

    private final SoulPactApi api;
    private final SqlClanPointsRepository pointsRepository;
    private final QuestTreasuryBridge treasuryBridge;

    public QuestRewardService(
            SoulPactApi api,
            SqlClanPointsRepository pointsRepository,
            QuestTreasuryBridge treasuryBridge
    ) {
        this.api = api;
        this.pointsRepository = pointsRepository;
        this.treasuryBridge = treasuryBridge;
    }

    public void award(long clanId, QuestDefinition definition, UUID finisherId, String clanTag) {
        if (definition.rewardPoints() != 0) {
            pointsRepository.addPoints(clanId, definition.rewardPoints());
        }
        if (definition.rewardTreasury() > 0D) {
            treasuryBridge.resolve().ifPresent(treasury ->
                    treasury.credit(clanId, finisherId, definition.rewardTreasury(), NOTE_PREFIX + definition.id()));
        }
        if (!definition.rewardCommands().isEmpty()) {
            dispatchCommands(definition, finisherId, clanTag);
        }
    }

    private void dispatchCommands(QuestDefinition definition, UUID finisherId, String clanTag) {
        api.scheduler().runSync(() -> {
            Player finisher = Bukkit.getPlayer(finisherId);
            String playerName = finisher == null ? null : finisher.getName();
            for (String command : definition.rewardCommands()) {
                if (command.contains("{player}") && playerName == null) {
                    continue;
                }
                String prepared = command
                        .replace("{player}", playerName == null ? "" : playerName)
                        .replace("{tag}", clanTag);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), prepared);
            }
        });
    }
}
