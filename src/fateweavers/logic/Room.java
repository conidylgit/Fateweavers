package fateweavers.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * A room on the FTL-style tile grid. Rooms are rectangles of whole tiles (most are 2x2, the Chrysalis
 * Ward is 3x3, each half of No Man's Land is 2x4). Each tile is one standing spot ("pad"); in rooms with
 * a station, pad 0 is the station (the device's tile, see placeDevice). Some tiles can be taken by a device (the ward's crystal).
 *
 * Every room belongs to a side: your fortress, No Man's Land (open ground between the bases) or the
 * bandit camp. Fortress and camp rooms also have:
 *   3 health bars (FTL-style): each hit knocks one out, a device can't use more power than its
 *     room's health, and anyone of the owning side standing in a damaged room repairs it.
 *   an Aether level (0..1): the mana-permeated air the blobs breathe (fortress only; outdoors is always 1).
 */
public class Room {
    /** Which part of the map a room belongs to. */
    public enum Side {
        FORTRESS(Faction.PLAYER), NO_MANS_LAND(null), CAMP(Faction.BANDITS);

        /** Whose room it is (null for No Man's Land). */
        public final Faction owner;
        Side(Faction owner) { this.owner = owner; }
    }

    public static final int TILE = 56;
    public static final int MAX_HEALTH = 3;
    public static final float REPAIR_SECONDS = 4f;    // per bar, for one normal crew member
    public static final float PAD_RADIUS = TILE / 2f;
    /** Feet sit a little below the tile centre, so the blob looks like it stands in the tile. */
    public static final float FEET_DROP = 12f;

    public final String id, name, kind, description;
    public final boolean core;          // white floor (a system room)
    public final boolean station;       // pad 0 is a station someone must stand on
    public final int tx, ty, cols, rows; // position and size in tiles
    public final Side side;
    public final Box bounds;
    public final Pos[] pads;            // standing spots (tile centres, at foot level)
    public final int[][] padTile;       // tile column/row of each pad, inside the room
    final CrewMember[] padOccupants;    // reserved by whoever is standing there or walking there

    int health = MAX_HEALTH;
    float repair = 0f;                  // 0..1 progress on the next bar
    float aether = 1f;                  // 0..1

    /** blockedCol/blockedRow = a tile taken by a device (-1 for none). */
    Room(String id, String name, String kind, boolean core, boolean station, String description,
         int tx, int ty, int cols, int rows, int blockedCol, int blockedRow, Side side) {
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.core = core;
        this.station = station;
        this.description = description;
        this.tx = tx;
        this.ty = ty;
        this.cols = cols;
        this.rows = rows;
        this.side = side;
        this.bounds = new Box(tx * TILE, ty * TILE, cols * TILE, rows * TILE);

        List<int[]> tiles = new ArrayList<>();
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (!(c == blockedCol && r == blockedRow)) tiles.add(new int[]{c, r});
        pads = new Pos[tiles.size()];
        padTile = new int[tiles.size()][];
        for (int i = 0; i < tiles.size(); i++) {
            int[] t = tiles.get(i);
            padTile[i] = t;
            pads[i] = new Pos(bounds.x() + (t[0] + 0.5f) * TILE, bounds.y() + (t[1] + 0.5f) * TILE + FEET_DROP);
        }
        padOccupants = new CrewMember[pads.length];
    }

    /** Where this room's device stands (its foot, at the tile's back wall), or null. */
    public Pos devicePos = null;
    /** Which tile the device is in (column, row), or null. */
    public int[] deviceTile = null;

    /** How far the device sits from the tile's side wall, and how far the operator steps aside for it. */
    public static final float DEVICE_INSET = 15f, OPERATOR_STEP = 9f;
    /** Rooms are drawn inset this much on each side (FortressView.WALL), so the bottom row's line is the wall. */
    public static final float WALL_INSET = 3f;

    /** A device's base: FLAT ones stand on the tile's bottom line, ROUND ones sit in the middle of the room. */
    public enum Base { FLAT, ROUND }

    /**
     * Put the room's device in one tile, on its outer side. A FLAT base stands on that tile's bottom line;
     * a ROUND base sits in the middle of the room (devicePos = the room centre). If 'isStation', that tile becomes
     * the station (pad 0); for a FLAT device whoever stands there steps a little aside so both stay visible.
     */
    void placeDevice(int col, int row, boolean isStation, Base base) {
        int idx = -1;
        for (int i = 0; i < padTile.length; i++) if (padTile[i][0] == col && padTile[i][1] == row) idx = i;
        if (idx < 0) throw new IllegalArgumentException(id + ": no tile " + col + "," + row);
        boolean left = col < cols / 2f - 0.01f || cols == 1;   // device toward the room's outer wall
        float tileX = bounds.x() + col * TILE, tileY = bounds.y() + row * TILE;
        deviceTile = new int[]{col, row};
        if (base == Base.ROUND) {
            devicePos = new Pos(bounds.centerX(), bounds.centerY());
        } else {
            float line = tileY + TILE - (row == rows - 1 ? WALL_INSET : 0f);   // the grid line, or the wall
            devicePos = new Pos(left ? tileX + DEVICE_INSET : tileX + TILE - DEVICE_INSET, line);
            Pos p = pads[idx];
            pads[idx] = new Pos(p.x() + (left ? OPERATOR_STEP : -OPERATOR_STEP), p.y());
        }
        if (isStation && idx != 0) {   // the device's tile is the station: make it pad 0
            Pos tp = pads[0]; pads[0] = pads[idx]; pads[idx] = tp;
            int[] tt = padTile[0]; padTile[0] = padTile[idx]; padTile[idx] = tt;
        }
    }

    public int padCount() { return pads.length; }
    public int health() { return health; }
    public boolean isDamaged() { return health < MAX_HEALTH; }
    public float repairProgress() { return repair; }
    public float aether() { return aether; }
    /** No Man's Land has no walls to break and no air to lose. */
    public boolean hasHealth() { return side != Side.NO_MANS_LAND; }
    public boolean isFortress() { return side == Side.FORTRESS; }
    public boolean isCamp() { return side == Side.CAMP; }
    public boolean isNoMansLand() { return side == Side.NO_MANS_LAND; }

    /** Centre of the room (world space). */
    public Pos center() { return new Pos(bounds.centerX(), bounds.centerY()); }

    int firstFreePad() {
        for (int i = 0; i < pads.length; i++) if (padOccupants[i] == null) return i;
        return -1;
    }

    /** The pad (tile) under a world point, or -1. */
    public int padAt(float x, float y) {
        if (!bounds.contains(x, y)) return -1;
        int c = (int) ((x - bounds.x()) / TILE), r = (int) ((y - bounds.y()) / TILE);
        for (int i = 0; i < pads.length; i++) if (padTile[i][0] == c && padTile[i][1] == r) return i;
        return -1;
    }

    /** Whoever is standing at the station (pad 0), or null. Rooms without a station have none. */
    public CrewMember stationOperator() {
        if (!station) return null;
        CrewMember c = padOccupants[0];
        return (c != null && !c.isMoving() && c.currentRoom == this) ? c : null;
    }
}
