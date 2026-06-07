package bm.b0b0b0.SoulPact.war.repository;

import bm.b0b0b0.SoulPact.war.model.ActiveWarRecord;
import bm.b0b0b0.SoulPact.war.model.WarCaptureRecord;
import bm.b0b0b0.SoulPact.war.model.WarDeclarationRecord;
import bm.b0b0b0.SoulPact.war.model.WarFlagSnapshot;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarRepository {

    Optional<WarDeclarationRecord> findPendingDeclaration(long attackerClanId, long defenderClanId);

    Optional<WarDeclarationRecord> findPendingForDefender(long defenderClanId);

    List<WarDeclarationRecord> listPendingForDefender(long defenderClanId);

    List<WarDeclarationRecord> listAllPendingDeclarations();

    long createDeclaration(long attackerClanId, long defenderClanId, UUID declaredBy, long createdAt);

    void updateDeclarationStatus(long declarationId, String status);

    Optional<ActiveWarRecord> findActiveWar(long clanId);

    Optional<ActiveWarRecord> findActiveWarBetween(long clanA, long clanB);

    List<ActiveWarRecord> listAllActiveWars();

    long createActiveWar(
            long attackerClanId,
            long defenderClanId,
            long startedAt,
            WarFlagSnapshot attackerFlag,
            WarFlagSnapshot defenderFlag
    );

    void finishWar(long warId, String status);

    void upsertCapture(long warId, long holderClanId, long targetClanId, long capturedAt, long deadlineAt);

    void clearCapture(long warId);

    java.util.List<WarCaptureRecord> listActiveCaptures();
}
