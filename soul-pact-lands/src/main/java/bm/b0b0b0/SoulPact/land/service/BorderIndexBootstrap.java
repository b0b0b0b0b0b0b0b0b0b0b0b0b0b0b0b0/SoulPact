package bm.b0b0b0.SoulPact.land.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.land.model.ClanBaseRecord;
import bm.b0b0b0.SoulPact.land.repository.ClanBaseRepository;
import java.util.ArrayList;
import java.util.List;

public final class BorderIndexBootstrap {

    private final SoulPactApi api;
    private final ClanBaseRepository repository;
    private final BorderBlockIndex borderBlockIndex;
    private final BaseFlagIndex flagIndex;
    private final ClanBaseRecordIndex recordIndex;

    public BorderIndexBootstrap(
            SoulPactApi api,
            ClanBaseRepository repository,
            BorderBlockIndex borderBlockIndex,
            BaseFlagIndex flagIndex,
            ClanBaseRecordIndex recordIndex
    ) {
        this.api = api;
        this.repository = repository;
        this.borderBlockIndex = borderBlockIndex;
        this.flagIndex = flagIndex;
        this.recordIndex = recordIndex;
    }

    public void loadAll() {
        api.scheduler().supplyAsync(() -> {
            List<IndexedBase> indexed = new ArrayList<>();
            for (ClanBaseRecord base : repository.findAll()) {
                List<ClanBaseRepository.BorderBlock> blocks = repository.findBorderBlocks(base.id());
                indexed.add(new IndexedBase(base, blocks));
            }
            return indexed;
        }).thenAccept(indexed -> api.scheduler().runSync(() -> {
            borderBlockIndex.clear();
            flagIndex.clear();
            recordIndex.clear();
            for (IndexedBase entry : indexed) {
                flagIndex.register(entry.base());
                recordIndex.register(entry.base());
                borderBlockIndex.register(entry.base().id(), entry.base().world(), entry.blocks());
            }
        }));
    }

    private record IndexedBase(ClanBaseRecord base, List<ClanBaseRepository.BorderBlock> blocks) {
    }
}
