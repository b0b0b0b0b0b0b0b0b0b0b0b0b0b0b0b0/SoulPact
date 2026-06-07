package bm.b0b0b0.SoulPact.land.model;

public enum BaseExpansionAxis {
    EAST,
    WEST,
    NORTH,
    SOUTH;

    public int resolvedExtent(ClanBaseRecord base, int defaultRadius) {
        return switch (this) {
            case EAST -> base.resolvedExtentXPos(defaultRadius);
            case WEST -> base.resolvedExtentXNeg(defaultRadius);
            case NORTH -> base.resolvedExtentZNeg(defaultRadius);
            case SOUTH -> base.resolvedExtentZPos(defaultRadius);
        };
    }

    public ClanBaseRecord withExtent(ClanBaseRecord base, int extent, int defaultRadius) {
        int extentXPos = base.resolvedExtentXPos(defaultRadius);
        int extentXNeg = base.resolvedExtentXNeg(defaultRadius);
        int extentZPos = base.resolvedExtentZPos(defaultRadius);
        int extentZNeg = base.resolvedExtentZNeg(defaultRadius);
        return switch (this) {
            case EAST -> base.withExtents(extent, extentXNeg, extentZPos, extentZNeg);
            case WEST -> base.withExtents(extentXPos, extent, extentZPos, extentZNeg);
            case NORTH -> base.withExtents(extentXPos, extentXNeg, extentZPos, extent);
            case SOUTH -> base.withExtents(extentXPos, extentXNeg, extent, extentZNeg);
        };
    }

    public BaseBounds expansionStrip(BaseBounds oldBounds, BaseBounds newBounds) {
        return switch (this) {
            case EAST -> new BaseBounds(
                    oldBounds.maxX() + 1,
                    newBounds.maxX(),
                    oldBounds.minY(),
                    oldBounds.maxY(),
                    oldBounds.minZ(),
                    oldBounds.maxZ()
            );
            case WEST -> new BaseBounds(
                    newBounds.minX(),
                    oldBounds.minX() - 1,
                    oldBounds.minY(),
                    oldBounds.maxY(),
                    oldBounds.minZ(),
                    oldBounds.maxZ()
            );
            case SOUTH -> new BaseBounds(
                    oldBounds.minX(),
                    oldBounds.maxX(),
                    oldBounds.minY(),
                    oldBounds.maxY(),
                    oldBounds.maxZ() + 1,
                    newBounds.maxZ()
            );
            case NORTH -> new BaseBounds(
                    oldBounds.minX(),
                    oldBounds.maxX(),
                    oldBounds.minY(),
                    oldBounds.maxY(),
                    newBounds.minZ(),
                    oldBounds.minZ() - 1
            );
        };
    }

    public String messageKey() {
        return switch (this) {
            case EAST -> "east";
            case WEST -> "west";
            case NORTH -> "north";
            case SOUTH -> "south";
        };
    }
}
