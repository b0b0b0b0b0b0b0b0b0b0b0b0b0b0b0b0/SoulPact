package bm.b0b0b0.SoulPact.land.service;

import bm.b0b0b0.SoulPact.land.repository.ClanBaseRepository;
import java.util.List;

public record BorderExpansionDelta(
        List<ClanBaseRepository.BorderBlock> removed,
        List<ClanBaseRepository.BorderBlock> added
) {
}
