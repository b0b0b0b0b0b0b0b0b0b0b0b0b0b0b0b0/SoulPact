package bm.b0b0b0.SoulPact.land.service;

import bm.b0b0b0.SoulPact.land.model.ClanBaseRecord;
import bm.b0b0b0.SoulPact.land.repository.ClanBaseRepository;
import java.util.List;

public final class BorderIndexBootstrap {

    private final ClanBaseRepository repository;
    private final BorderBlockIndex borderBlockIndex;

    public BorderIndexBootstrap(ClanBaseRepository repository, BorderBlockIndex borderBlockIndex) {
        this.repository = repository;
        this.borderBlockIndex = borderBlockIndex;
    }

    public void loadAll() {
        borderBlockIndex.clear();
        for (ClanBaseRecord base : repository.findAll()) {
            List<ClanBaseRepository.BorderBlock> blocks = repository.findBorderBlocks(base.id());
            borderBlockIndex.register(base.id(), base.world(), blocks);
        }
    }
}
