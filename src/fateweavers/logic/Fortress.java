package fateweavers.logic;

import java.util.*;

/**
 * The whole game state and rules for Module 1: rooms, doors, crew, pathfinding and movement,
 * the Fire Gem, the Loom, the Shield Conduit, room damage and repairs, No Man's Land and its gate,
 * the Bandit Camp, and FTL-style auto-combat.
 * No drawing, no Swing, no java.awt, so this package can move to LibGDX unchanged.
 * Call update(dt) once per frame, then drainEvents() to react to what happened.
 *
 * World space (56 px tiles), left to right: your fortress (with its Entrance Hall), No Man's Land
 * (4x4 tiles, a gate down the middle), the camp entrance, and the camp's column of three rooms.
 */
public class Fortress {
    public static final float WALK_SPEED = 100f;      // world units per second (about 1.8 tiles/s)
    private static final float DOOR_SENSOR_RANGE = 40f;
    private static final float DOOR_PASSABLE = 0.6f;   // how open a door must be to walk through
    private static final float DOOR_STEP_OFFSET = 18f; // how far either side of a door the route passes
    private static final float DOOR_ROUTE_DROP = 6f;   // walk slightly below a side door's centre, at foot level
    /** Doors take this share of a unit's melee damage. */
    private static final float DOOR_DAMAGE_FACTOR = 0.4f;
    /** Fire Gem hits also hurt every bandit in the room: weapon damage x this. */
    public static final float GEM_UNIT_DAMAGE_MULTIPLIER = 5f;
    /** Seconds for a boarding bandit to knock one health bar out of an undefended room. */
    public static final float SABOTAGE_SECONDS = 5f;

    public static final int POWER_CAPACITY = 5;
    /** Below this much Aether, crew start losing health. */
    public static final float SUFFOCATE_BELOW = 0.3f;
    private static final float SUFFOCATE_DAMAGE_PER_SECOND = 6f;
    private static final float WARD_HEAL_PER_SECOND = 5f;
    private static final float AETHER_FLOW = 1.5f;      // how fast Aether evens out through an open door
    /** Practice XP per second of work, and per point of damage taken (Resolve and Vitality). */
    public static final float XP_PER_SECOND = 1f;
    public static final float XP_PER_DAMAGE = 0.5f;
    /** "Survived a battle on one health": at or below this share of max health when a fight ends. */
    public static final float LUCKY_SURVIVAL_SHARE = 0.05f;
    /** How far a shield bubble reaches beyond the rooms. */
    public static final float BUBBLE_MARGIN = 12f;

    private final Map<String, Room> rooms = new LinkedHashMap<>();
    private final List<Door> doors = new ArrayList<>();
    private final List<CrewMember> crew = new ArrayList<>();
    private final List<CrewMember> bandits = new ArrayList<>();
    private final List<GameEvent> events = new ArrayList<>();
    private final List<Weapon> weapons = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();
    private final List<EnemyShot> enemyShots = new ArrayList<>();
    private final Random rng;
    private final Loom loom;
    private final ShieldConduit shield;
    private final Permeator permeator;
    private final BanditCamp camp;
    private final Door gate;
    private ClassTree tree;                // set by the game after loading; promotions need it
    private Faction gateHolder = null;     // last known gate controller (for the "you control the gate" notice)
    private int day = 1;
    private boolean paused = false;
    private boolean testMode = false;      // dev console: fast learning

    public Fortress() { this(new Random()); }

    /** With a fixed random source, for repeatable tests. */
    public Fortress(Random rng) {
        this.rng = rng;
        // Your fortress. Most rooms are 2x2; the Chrysalis Ward is 3x3 with its crystal in the middle.
        addRoom("WEAPONS", "Turret", "Core room", true, true,
                "The Fire Gem floats on its pedestal here. It only charges while powered and someone is at the pedestal.", 2, 0, 2);
        addRoom("LOOM", "Fateweaver Loom", "Core room", true, true,
                "Charges while powered and manned. When full it unlocks Sleep, which moves you to the next node.", 2, 2, 2);
        addRoom("EAST", "East Room", "Neutral room", false, false,
                "An empty room. Rooms unlocked through classes can be built here later.", 4, 2, 2);
        addRoom("SHIELD", "Control Room", "Defence room", true, true,
                "Raise the shield just before a shot lands. Perfect timing parries it back; good timing blocks it. "
                        + "Ballista bolts go straight through it.", 0, 4, 2);
        rooms.put("CHRYSALIS", new Room("CHRYSALIS", "Chrysalis Ward", "Core room", true, false,
                "Heals crew and is where they change class. Stand someone here when a new class is unlocked.",
                2, 4, 3, 3, 1, 1, Room.Side.FORTRESS));
        addRoom("PERMEATOR", "Permeator", "Life support", true, false,
                "Fills the fortress with Aether, the mana-permeated air blobs need to live. Needs power, not crew.", 5, 4, 2);
        addRoom("ENTRANCE", "Entrance Hall", "Entrance", false, false,
                "Your way out to No Man's Land. Boarders break in here first; a Heavy Melee here doubles the door's health.",
                6, 2, 2);

        // No Man's Land: a 4x4 field in two halves with the gate between them
        String nml = "Open ground between the bases. Whichever side has more strength standing here controls the gate "
                + "(Heavy 3, Light 2, everyone else 1).";
        rooms.put("NML_WEST", new Room("NML_WEST", "No Man's Land (west)", "No Man's Land", false, false, nml,
                8, 1, 2, 4, -1, -1, Room.Side.NO_MANS_LAND));
        rooms.put("NML_EAST", new Room("NML_EAST", "No Man's Land (east)", "No Man's Land", false, false, nml,
                10, 1, 2, 4, -1, -1, Room.Side.NO_MANS_LAND));

        // The Bandit Camp: an entrance, then a column of shield, quarters and archer tower
        campRoom("CAMP_ENTRANCE", "Camp Entrance", "Bandit camp", false,
                "The bandits' gate into their camp. Break the door to raid them.", 12, 2);
        campRoom("CAMP_SHIELD", "Bandit Shield", "Bandit camp", true,
                "An open orb on a pedestal. Each layer stops one Fire Gem shot. A bandit with Psyche on the station adds layers.", 14, 0);
        campRoom("CAMP_QUARTERS", "Bandit Quarters", "Bandit camp", false,
                "Where the bandits sleep and gather before a raid.", 14, 2);
        campRoom("CAMP_TOWER", "Archer Tower", "Bandit camp", true,
                "A wooden tower with a ballista. Its bolts ignore shields. When it runs out of bolts, the bandits board.", 14, 4);

        // Each device takes one tile, which is also its station (FTL-style console): see Room.placeDevice
        // Flat bases (pedestals, the Loom's foot) stand on the tile's line; round ones sit in the middle of the room
        room("WEAPONS").placeDevice(0, 0, true, Room.Base.FLAT);
        room("SHIELD").placeDevice(0, 1, true, Room.Base.FLAT);
        room("LOOM").placeDevice(1, 1, true, Room.Base.FLAT);
        room("PERMEATOR").placeDevice(1, 1, false, Room.Base.ROUND);
        room("CAMP_SHIELD").placeDevice(0, 0, true, Room.Base.FLAT);
        room("CAMP_TOWER").placeDevice(0, 0, true, Room.Base.ROUND);

        connect("WEAPONS", "LOOM");
        connect("LOOM", "EAST");
        connect("SHIELD", "CHRYSALIS");
        connect("LOOM", "CHRYSALIS");
        connect("CHRYSALIS", "PERMEATOR");
        connect("EAST", "PERMEATOR");
        connect("EAST", "ENTRANCE");
        connect("ENTRANCE", "NML_WEST");
        gate = new Door(room("NML_WEST"), room("NML_EAST"), true);
        doors.add(gate);
        connect("NML_EAST", "CAMP_ENTRANCE");
        connect("CAMP_ENTRANCE", "CAMP_QUARTERS");
        connect("CAMP_QUARTERS", "CAMP_SHIELD");
        connect("CAMP_QUARTERS", "CAMP_TOWER");

        crew.add(new CrewMember("Aldric", CrewClass.UNCLASSED, room("LOOM"), 0, Faction.PLAYER));
        crew.add(new CrewMember("Brenna", CrewClass.UNCLASSED, room("LOOM"), 1, Faction.PLAYER));
        crew.add(new CrewMember("Cedric", CrewClass.UNCLASSED, room("WEAPONS"), 0, Faction.PLAYER));
        crew.add(new CrewMember("Daria", CrewClass.UNCLASSED, room("CHRYSALIS"), 0, Faction.PLAYER));
        for (int i = 0; i < crew.size(); i++) crew.get(i).headband = i;   // red, blue, green, yellow

        camp = new BanditCamp(room("CAMP_SHIELD"), room("CAMP_QUARTERS"), room("CAMP_TOWER"), room("CAMP_ENTRANCE"));
        spawnBandits();

        Room tower = room("WEAPONS");
        Weapon gem = new Weapon("Fire Gem", List.of(Attunement.FIRE), DamageType.MAGIC, StatRules.FIRE_GEM, 2, 5f, 3f, tower,
                new Pos(tower.devicePos.x(), tower.devicePos.y() - GEM_ABOVE_FOOT));   // the gem floats over its pedestal
        gem.target = camp.tower;
        weapons.add(gem);
        loom = new Loom(room("LOOM"));
        shield = new ShieldConduit(room("SHIELD"));
        permeator = new Permeator(room("PERMEATOR"));
    }

    private void addRoom(String id, String name, String kind, boolean core, boolean station, String desc,
                         int tx, int ty, int size) {
        rooms.put(id, new Room(id, name, kind, core, station, desc, tx, ty, size, size, -1, -1, Room.Side.FORTRESS));
    }

    private void campRoom(String id, String name, String kind, boolean station, String desc, int tx, int ty) {
        rooms.put(id, new Room(id, name, kind, false, station, desc, tx, ty, 2, 2, -1, -1, Room.Side.CAMP));
    }

    private void connect(String a, String b) {
        doors.add(new Door(room(a), room(b), false));
    }

    // ------------------------------------------------------------------ queries

    public Room room(String id) { return rooms.get(id); }
    public Collection<Room> rooms() { return Collections.unmodifiableCollection(rooms.values()); }
    public List<Room> fortressRooms() { return rooms.values().stream().filter(Room::isFortress).toList(); }
    public List<Door> doors() { return Collections.unmodifiableList(doors); }
    /** Your crew. */
    public List<CrewMember> crew() { return Collections.unmodifiableList(crew); }
    /** The living bandits. */
    public List<CrewMember> bandits() { return Collections.unmodifiableList(bandits); }
    /** Everyone: your crew, then the bandits. */
    public List<CrewMember> units() {
        List<CrewMember> all = new ArrayList<>(crew);
        all.addAll(bandits);
        return all;
    }
    public List<Weapon> weapons() { return Collections.unmodifiableList(weapons); }
    public List<Projectile> projectiles() { return Collections.unmodifiableList(projectiles); }
    public List<EnemyShot> enemyShots() { return Collections.unmodifiableList(enemyShots); }
    public BanditCamp camp() { return camp; }
    public Door gate() { return gate; }
    public Loom loom() { return loom; }
    public ShieldConduit shield() { return shield; }
    public Permeator permeator() { return permeator; }
    public ClassTree tree() { return tree; }
    public void setClassTree(ClassTree t) { tree = t; refreshClassNames(); }
    public int day() { return day; }
    public boolean isPaused() { return paused; }

    public List<Weapon> weaponsIn(Room r) {
        return weapons.stream().filter(w -> w.room == r).toList();
    }

    public int powerUsed() {
        int used = 0;
        for (Weapon w : weapons) if (w.powered) used += w.powerCost;
        if (loom.powered) used += loom.powerCost;
        if (shield.powered) used += shield.powerCost;
        if (permeator.powered) used += permeator.powerCost;
        return used;
    }

    public int powerFree() { return POWER_CAPACITY - powerUsed(); }

    /**
     * Charisma: everyone else of the same side standing in the room works faster.
     * The best Charisma there counts (+0.5% per point). 1.0 = no boost.
     */
    public float charismaBoost(Room r, CrewMember worker) {
        int best = 0;
        for (CrewMember c : unitsIn(r)) {
            if (c == worker || c.faction != (worker != null ? worker.faction : Faction.PLAYER)) continue;
            best = Math.max(best, c.stat(Stat.CHARISMA));
        }
        return 1f + StatRules.CHARISMA_BOOST_PER_POINT * best;
    }

    /** One device effect from the person on its station, e.g. effect(StatRules.FIRE_GEM, "damage", tower). */
    public float stationEffect(java.util.List<Scaling> scalings, String effect, Room r) {
        return StatRules.effect(scalings, effect, r.stationOperator());
    }

    public float damageOf(Weapon w) {
        CrewMember op = w.operator();
        float dmg = w.baseDamage * (1f + StatRules.effect(w.scalings, "damage", op));
        dmg *= 1f + Attunements.DAMAGE_BONUS * attunementMatch(op, w).power();
        return dmg;
    }

    /** How well a person matches a weapon (see Attunements.match). */
    public Attunements.Match attunementMatch(CrewMember c, Weapon w) {
        return Attunements.match(c, w.attunements, w.damageType);
    }

    /** Chance (0..1) a shot is a critical hit (double damage). */
    public float critChanceOf(Weapon w) {
        return Math.min(1f, StatRules.effect(w.scalings, "crit chance", w.operator()));
    }

    /** Seconds for a full charge, after Dexterity (load speed) and Charisma. */
    public float chargeSecondsOf(Weapon w) {
        float speed = (1f + StatRules.effect(w.scalings, "load speed", w.operator())) * charismaBoost(w.room, w.operator());
        return Math.max(0.5f, w.chargeSeconds / speed);
    }

    public boolean canFire(Weapon w) {
        return w.powered && w.isCharged() && w.target != null;
    }

    /** Short status line for a weapon's card. */
    public String statusOf(Weapon w) {
        if (!w.powered) return w.room.health < w.powerCost ? "Room too damaged" : "Unpowered";
        if (w.isCharged()) return "Ready";
        if (!w.isManned()) return "Needs someone on the station";
        return "Charging " + Math.round(w.charge * 100) + "%";
    }

    /** Why a weapon can't fire right now, or null if it can. */
    public String fireBlockedReason(Weapon w) {
        if (!w.powered) return w.room.health < w.powerCost ? w.room.name + " is too damaged" : "it's unpowered";
        if (w.target == null) return "it has no target";
        if (!w.isCharged()) return w.isManned() ? "still charging (" + Math.round(w.charge * 100) + "%)"
                : "nobody is on the station to charge it";
        return null;
    }

    /** Size of your fortress (including the Entrance Hall); the shield bubble wraps it. */
    public Box layoutBounds() { return boundsOf(rooms.values().stream().filter(Room::isFortress).toList()); }

    /** Size of the whole map: fortress, No Man's Land and camp. */
    public Box worldBounds() { return boundsOf(rooms.values()); }

    /** The camp's rooms. Its shield bubble wraps these. */
    public Box campBounds() { return boundsOf(rooms.values().stream().filter(Room::isCamp).toList()); }

    /** No Man's Land, both halves. */
    public Box noMansLandBounds() { return boundsOf(rooms.values().stream().filter(Room::isNoMansLand).toList()); }

    private static Box boundsOf(Collection<Room> rs) {
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        for (Room r : rs) {
            minX = Math.min(minX, r.bounds.x());
            minY = Math.min(minY, r.bounds.y());
            maxX = Math.max(maxX, r.bounds.x() + r.bounds.w());
            maxY = Math.max(maxY, r.bounds.y() + r.bounds.h());
        }
        return new Box(minX, minY, maxX - minX, maxY - minY);
    }

    /** Your shield bubble around the fortress: shots are parried or blocked where they cross it. */
    public Box bubble() {
        Box f = layoutBounds();
        return new Box(f.x() - BUBBLE_MARGIN, f.y() - BUBBLE_MARGIN, f.w() + BUBBLE_MARGIN * 2, f.h() + BUBBLE_MARGIN * 2);
    }

    /** The bandits' shield bubble around their camp. */
    public Box campBubble() {
        Box f = campBounds();
        return new Box(f.x() - BUBBLE_MARGIN, f.y() - BUBBLE_MARGIN, f.w() + BUBBLE_MARGIN * 2, f.h() + BUBBLE_MARGIN * 2);
    }

    public Room roomAt(float x, float y) {
        for (Room r : rooms.values()) if (r.bounds.contains(x, y)) return r;
        return null;
    }

    public Door doorAt(float x, float y) {
        for (Door d : doors) if (d.hitbox.contains(x, y)) return d;
        return null;
    }

    /** Your crew standing in a room (not counting anyone walking through). */
    public List<CrewMember> crewIn(Room r) {
        return crew.stream().filter(c -> c.currentRoom == r && !c.isMoving()).toList();
    }

    /** Everyone standing in a room, both sides. */
    public List<CrewMember> unitsIn(Room r) {
        return units().stream().filter(c -> c.currentRoom == r && !c.isMoving()).toList();
    }

    /** Living bandits anywhere in a room (standing or passing through). */
    public List<CrewMember> banditsIn(Room r) {
        return bandits.stream().filter(c -> c.currentRoom == r).toList();
    }

    private boolean hasEnemyIn(Room r, CrewMember me) {
        for (CrewMember u : me.isBandit() ? crew : bandits) if (u.alive && u.currentRoom == r) return true;
        return false;
    }

    /** Which side a door opens for (its owner; for the gate, whoever controls No Man's Land). */
    public Faction doorOpensFor(Door d) {
        return d.gate ? gateController() : d.owner;
    }

    /** A door's protection level: 1, +1 for each Heavy Melee of its side standing in either of its rooms. */
    public int doorProtection(Door d) {
        Faction f = doorOpensFor(d);
        int level = 1;
        if (f == null) return level;
        for (CrewMember u : f == Faction.PLAYER ? crew : bandits) {
            if (u.isMoving() || (u.currentRoom != d.roomA && u.currentRoom != d.roomB)) continue;
            level += StatRules.doorProtection(u);
        }
        return level;
    }

    public float doorMaxHealth(Door d) {
        return Door.maxHealthAt(doorProtection(d)) * (d.gate ? Door.GATE_HEALTH_MULTIPLIER : 1f);
    }
    public float doorHealth(Door d) { return d.healthFraction * doorMaxHealth(d); }

    /** Repair rate of a room in bars per second (0 if nobody of its side is there). */
    public float repairRate(Room r) {
        Faction f = r.side.owner;
        if (f == null) return 0f;
        float rate = 0;
        for (CrewMember c : f == Faction.PLAYER ? crew : bandits) {
            if (c.currentRoom != r || c.isMoving() || c.fighting) continue;
            rate += StatRules.repairMultiplier(c) * charismaBoost(r, c) / Room.REPAIR_SECONDS;
        }
        return rate;
    }

    /** Events since the last call. */
    public List<GameEvent> drainEvents() {
        List<GameEvent> out = new ArrayList<>(events);
        events.clear();
        return out;
    }

    // ------------------------------------------------------------------ No Man's Land and combat queries

    /** Strength a side has standing (or walking) in No Man's Land: Heavy 3, Light 2, everyone else 1. */
    public int strengthInNoMansLand(Faction f) {
        int s = 0;
        for (CrewMember u : f == Faction.PLAYER ? crew : bandits) {
            if (u.alive && u.currentRoom.isNoMansLand()) s += StatRules.gateStrength(u);
        }
        return s;
    }

    /** Who can open and close the gate: the side with more strength in No Man's Land. Null on a tie. */
    public Faction gateController() {
        int p = strengthInNoMansLand(Faction.PLAYER), b = strengthInNoMansLand(Faction.BANDITS);
        return p > b ? Faction.PLAYER : b > p ? Faction.BANDITS : null;
    }

    /** Melee damage per second this unit is taking from enemies in the same room. */
    public float incomingDps(CrewMember target) {
        float dps = 0;
        for (CrewMember u : target.isBandit() ? crew : bandits) {
            if (u.alive && u.currentRoom == target.currentRoom && !u.walking) dps += StatRules.meleeDps(u);
        }
        return dps;
    }

    /** Melee damage per second this unit is dealing right now (0 when not fighting). */
    public float outgoingDps(CrewMember u) {
        return u.fighting ? StatRules.meleeDps(u) : 0f;
    }

    /** Seconds until this unit dies at the current incoming damage, or -1 if nobody is hurting it. */
    public float timeToKill(CrewMember target) {
        float dps = incomingDps(target);
        return dps <= 0 ? -1f : target.hp / dps;
    }

    // ------------------------------------------------------------------ shield queries

    /** Base layers + the station operator's Psyche (+1 per tier reached: 5, 20, 40, 60, 80, 100). */
    public int maxShieldLayers() {
        return ShieldConduit.BASE_LAYERS + Math.round(stationEffect(StatRules.SHIELD, "shield layer", shield.room));
    }

    /** Seconds per layer recharge, after the operator's Arcana (+1% per point) and Charisma around them. */
    public float shieldRechargeSeconds() {
        CrewMember op = shield.room.stationOperator();
        float speed = (1f + StatRules.effect(StatRules.SHIELD, "recharge speed", op)) * charismaBoost(shield.room, op);
        return ShieldConduit.LAYER_RECHARGE_SECONDS / speed;
    }

    /** The parry window. Fixed: Resolve no longer widens it (24 Oct decision). */
    public float parryWindow() {
        return Math.min(ShieldConduit.BLOCK_WINDOW, ShieldConduit.PARRY_WINDOW);
    }

    /** Why the shield can't be raised right now, or null if it can. */
    public String shieldBlockedReason() {
        if (!shield.powered) return shield.room.health < shield.powerCost ? "the Shield Room is too damaged" : "it's unpowered";
        if (shield.room.stationOperator() == null) return "nobody is on the station";
        if (shield.state == ShieldConduit.State.UP) return "it's already up";
        if (shield.state == ShieldConduit.State.COOLDOWN) return "it's cooling down after a miss";
        if (shield.layers == 0) return "no layers yet (recharging)";
        return null;
    }

    public boolean canRaiseShield() { return shieldBlockedReason() == null; }

    public String shieldStatus() {
        if (shield.queued) return "Armed: raises on unpause";
        if (shield.state == ShieldConduit.State.UP) return "Up";
        String why = shieldBlockedReason();
        if (why == null) return "Ready";
        return switch (why) {
            case "it's unpowered" -> "Unpowered";
            case "the Shield Room is too damaged" -> "Room too damaged";
            case "nobody is on the station" -> "Needs someone on the station";
            case "it's cooling down after a miss" -> "Missed, cooling down";
            default -> "Charging a layer " + Math.round(shield.recharge * 100) + "%";
        };
    }

    // ------------------------------------------------------------------ camp queries

    /** The bandit shield's layers: base 1, plus layers from the station bandit's Psyche; none if its room is wrecked. */
    public int campShieldMax() {
        if (camp.shieldRoom.health < 1 || camp.phase == BanditCamp.Phase.CLEARED) return 0;
        CrewMember op = camp.shieldRoom.stationOperator();
        int extra = op != null && op.isBandit() ? Math.round(StatRules.effect(StatRules.SHIELD, "shield layer", op)) : 0;
        return BanditCamp.BASE_LAYERS + extra;
    }

    /** Short status line for the ballista. */
    public String ballistaStatus() {
        if (camp.phase == BanditCamp.Phase.CLEARED) return "Abandoned";
        if (camp.bolts <= 0) return "Out of bolts";
        if (camp.tower.health < 1) return "Tower wrecked";
        CrewMember op = camp.tower.stationOperator();
        if (op == null || !op.isBandit()) return "Unmanned";
        if (camp.windup >= 0) return "Aiming at " + (camp.aim != null ? camp.aim.name : "you");
        return String.format("Next bolt in %.1f s", Math.max(0, camp.fireTimer));
    }

    // ------------------------------------------------------------------ commands

    public void setPaused(boolean p) { paused = p; }
    public void togglePause() { paused = !paused; }

    private boolean tryPower(String name, Room room, int cost, boolean currentlyOn) {
        if (currentlyOn) return true;
        if (room.health < cost) {
            events.add(GameEvent.notice(name + " can't be powered: " + room.name + " is too damaged (needs " + cost + " health)."));
            return false;
        }
        if (powerFree() < cost) {
            events.add(GameEvent.notice("Not enough power: " + name + " needs " + cost + ", " + powerFree() + " free."));
            return false;
        }
        return true;
    }

    /** Turn a weapon's power on or off. Fails if there isn't enough free power or the room is too damaged. */
    public boolean setWeaponPowered(Weapon w, boolean on) {
        if (on == w.powered) return true;
        if (on && !tryPower(w.name, w.room, w.powerCost, false)) return false;
        w.powered = on;
        if (!on) w.charge = 0f; // like FTL, unpowering loses the charge
        return true;
    }

    /** Point a weapon at a camp room. */
    public boolean setWeaponTarget(Weapon w, Room r) {
        if (r == null || !r.isCamp()) return false;
        w.target = r;
        return true;
    }

    /** Turn the Loom's power on or off. Progress is kept. */
    public boolean setLoomPowered(boolean on) {
        if (on == loom.powered) return true;
        if (on && !tryPower("The Loom", loom.room, loom.powerCost, false)) return false;
        loom.powered = on;
        return true;
    }

    /** Turn the Shield Conduit on or off. Layers start at 0 and charge up; turning it off loses them. */
    public boolean setShieldPowered(boolean on) {
        if (on == shield.powered) return true;
        if (on && !tryPower(shield.name, shield.room, shield.powerCost, false)) return false;
        shield.powered = on;
        shield.layers = 0;
        shield.recharge = 0f;
        shield.state = ShieldConduit.State.READY;
        shield.queued = false;
        return true;
    }

    /**
     * Raise the shield (the S key, the operator's Activate button, or Fire on the conduit).
     * While paused it's queued and raises the moment you unpause.
     */
    public boolean raiseShield() {
        if (paused) {   // armed: it raises the instant you unpause, timed from that moment
            if (!shield.queued) events.add(GameEvent.notice("Shield armed: it raises the instant you unpause."));
            shield.queued = true;
            events.add(GameEvent.at(GameEvent.Type.SHIELD_ARMED, shield.orb, 0f));
            return true;
        }
        String why = shieldBlockedReason();
        if (why != null) {
            events.add(GameEvent.notice("Can't raise the shield: " + why + "."));
            return false;
        }
        shield.state = ShieldConduit.State.UP;
        shield.stateTime = 0f;
        events.add(GameEvent.at(GameEvent.Type.SHIELD_RAISED, shield.orb, 0f));
        return true;
    }

    /** Loom weaving speed multiplier from the weaver's Arcana and Knowledge, and Charisma around them (1.0 = normal). */
    public float loomSpeed() {
        CrewMember op = loom.room.stationOperator();
        return (1f + StatRules.effect(StatRules.LOOM, "weave speed", op)) * charismaBoost(loom.room, op);
    }

    /** Seconds for a full weave at the current speed. */
    public float loomChargeSeconds() {
        return loom.chargeSeconds / loomSpeed();
    }

    public String loomStatus() {
        if (loom.isReady()) return "Ready to sleep";
        if (!loom.powered) return loom.room.health < loom.powerCost ? "Room too damaged" : "Unpowered";
        if (!loom.isManned()) return "Needs someone on the station";
        return "Weaving " + Math.round(loom.charge * 100) + "%";
    }

    /** End the day. Only works when the Loom is fully woven. Rest effects come later. */
    public boolean sleep() {
        if (!loom.isReady()) return false;
        day++;
        loom.charge = 0f;
        events.add(GameEvent.value(GameEvent.Type.SLEPT, day));
        return true;
    }

    public void setAutoFire(Weapon w, boolean on) {
        w.autoFire = on;
    }

    /** The player pressing Fire: fires if ready, otherwise says why ("not ready" feedback). */
    public boolean fireWeapon(Weapon w) {
        String why = fireBlockedReason(w);
        if (why != null) {
            events.add(GameEvent.notice(w.name + " isn't ready: " + why + "."));
            events.add(GameEvent.at(GameEvent.Type.FIRE_NOT_READY, w.muzzle, w.charge));
            return false;
        }
        launch(w);
        return true;
    }

    private void launch(Weapon w) {
        w.charge = 0f;
        float dmg = damageOf(w);
        boolean crit = rng.nextFloat() < critChanceOf(w);
        if (crit) {
            dmg *= StatRules.CRIT_MULTIPLIER;
            events.add(GameEvent.at(GameEvent.Type.CRIT, w.muzzle, dmg));
        }
        // A matching attunement unlocks the weapon's effects: Fire sets whoever it hits burning
        CrewMember op = w.operator();
        Attunements.Match m = attunementMatch(op, w);
        float burn = m.has(Attunement.FIRE) ? m.power() : 0f;
        for (Attunement a : m.matched()) useAttunement(op, a, Attunements.POINTS_PER_USE);
        shootAtCamp(new Hit(dmg, w.damageType, w.attunements, op, "the " + w.name), w.muzzle, w.target, dmg, burn);
        events.add(GameEvent.value(GameEvent.Type.WEAPON_FIRED, 0f));
    }

    /** A shot towards a camp room, stopping first at the edge of the camp's shield bubble. */
    private void shootAtCamp(Hit hit, Pos from, Room target, float damage) { shootAtCamp(hit, from, target, damage, 0f); }

    private void shootAtCamp(Hit hit, Pos from, Room target, float damage, float burnPower) {
        Pos to = target.center();
        float edgeX = campBubble().x();
        float t = (edgeX - from.x()) / Math.max(1f, to.x() - from.x());
        Pos contact = new Pos(edgeX, from.y() + (to.y() - from.y()) * Math.max(0f, Math.min(1f, t)));
        projectiles.add(new Projectile(hit, from, contact, damage, target, true, burnPower));
    }

    /** Set a door to Open or Closed. Camp doors aren't yours; the gate needs control of No Man's Land. */
    public boolean setDoorMode(Door d, Door.Mode mode) {
        if (d.gate) {
            if (gateController() != Faction.PLAYER) {
                events.add(GameEvent.notice("You need more strength in No Man's Land than the bandits to work the gate."));
                return false;
            }
        } else if (d.owner != Faction.PLAYER) {
            events.add(GameEvent.notice("That door belongs to the bandits. Break it down instead."));
            return false;
        }
        d.mode = mode;
        return true;
    }

    /** Opens or closes all of your own doors (not the gate or the camp's). */
    public void openAllDoors() { for (Door d : doors) if (d.owner == Faction.PLAYER) d.mode = Door.Mode.OPEN; }
    public void closeAllDoors() { for (Door d : doors) if (d.owner == Faction.PLAYER) d.mode = Door.Mode.CLOSED; }

    /** Turn the Permeator on or off. */
    public boolean setPermeatorPowered(boolean on) {
        if (on == permeator.powered) return true;
        if (on && !tryPower(permeator.name, permeator.room, permeator.powerCost, false)) return false;
        permeator.powered = on;
        return true;
    }

    /** Average Aether across the fortress (for the bar at the bottom-left). */
    public float averageAether() {
        float t = 0;
        int n = 0;
        for (Room r : rooms.values()) if (r.isFortress()) { t += r.aether; n++; }
        return n == 0 ? 1f : t / n;
    }

    public void setCrewClass(CrewMember c, CrewClass cls) {
        c.crewClass = cls;
        c.classId = cls.treeId;
        c.className = cls.title;
        c.tier = cls == CrewClass.UNCLASSED ? 0 : 1;
        c.attunements = List.of(Attunement.NEUTRAL);
        events.add(GameEvent.of(GameEvent.Type.CLASS_CHANGED, c));
    }

    /** Send one of your crew to a room. preferredPad = -1 means any free pad. Returns false if impossible. */
    public boolean orderMove(CrewMember unit, Room target, int preferredPad) {
        if (unit.isBandit()) return false;
        if (preferredPad >= 0 && preferredPad < target.padOccupants.length) {
            CrewMember other = target.padOccupants[preferredPad];
            if (other != null && other != unit && !other.isBandit() && other.alive) {   // swap the two
                Room oldRoom = unit.destinationRoom;
                int oldPad = unit.destinationPad;
                target.padOccupants[preferredPad] = null;
                if (!tryMove(unit, target, preferredPad, true)) { target.padOccupants[preferredPad] = other; return false; }
                if (oldRoom.padOccupants[oldPad] == null) tryMove(other, oldRoom, oldPad, false);
                else tryMove(other, oldRoom, -1, false);
                return true;
            }
        }
        return tryMove(unit, target, preferredPad, true);
    }

    private boolean tryMove(CrewMember unit, Room target, int preferredPad, boolean reportFailure) {
        int pad = preferredPad;
        if (pad < 0 || (target.padOccupants[pad] != null && target.padOccupants[pad] != unit)) {
            pad = target.firstFreePad();
            if (unit.destinationRoom == target && pad < 0) pad = unit.destinationPad;
        }
        if (pad < 0) {
            if (reportFailure) events.add(GameEvent.notice(target.name + " is full."));
            return false;
        }

        // Rerouting mid-doorway: finish walking through first, and plan the new route from the far side.
        // (Dropping the crossing step used to leave the door held open and currentRoom stale.)
        Room from = unit.currentRoom;
        CrewMember.Step keep = null;
        if (unit.crossing != null && !unit.steps.isEmpty() && unit.steps.peekFirst().door() == unit.crossing) {
            keep = unit.steps.peekFirst();
            from = unit.crossing.neighborOf(unit.currentRoom);
        }

        List<Room> path = findRoomPath(from, target);
        if (path.isEmpty()) {
            if (reportFailure) events.add(GameEvent.notice("No route to " + target.name + "."));
            return false;
        }

        if (unit.destinationRoom.padOccupants[unit.destinationPad] == unit)
            unit.destinationRoom.padOccupants[unit.destinationPad] = null;
        target.padOccupants[pad] = unit;
        unit.destinationRoom = target;
        unit.destinationPad = pad;
        unit.steps.clear();
        unit.waitingAtDoor = false;
        unit.sabotage = 0f;
        if (keep != null) unit.steps.add(keep);

        for (int i = 0; i < path.size() - 1; i++) {
            Room a = path.get(i), b = path.get(i + 1);
            Door door = doorBetween(a, b);
            if (door == null) continue;
            float cx = door.center.x(), cy = door.center.y();
            if (door.verticalWall) {
                int dir = b.bounds.centerX() > a.bounds.centerX() ? 1 : -1;
                unit.steps.add(new CrewMember.Step(new Pos(cx - dir * DOOR_STEP_OFFSET, cy + DOOR_ROUTE_DROP), null));
                unit.steps.add(new CrewMember.Step(new Pos(cx + dir * DOOR_STEP_OFFSET, cy + DOOR_ROUTE_DROP), door));
            } else {
                int dir = b.bounds.centerY() > a.bounds.centerY() ? 1 : -1;
                unit.steps.add(new CrewMember.Step(new Pos(cx, cy - dir * DOOR_STEP_OFFSET), null));
                unit.steps.add(new CrewMember.Step(new Pos(cx, cy + dir * DOOR_STEP_OFFSET), door));
            }
        }
        unit.steps.add(new CrewMember.Step(target.pads[pad], null));
        events.add(GameEvent.of(GameEvent.Type.STARTED_WALKING, unit));
        return true;
    }

    // ------------------------------------------------------------------ pathfinding

    private List<Room> findRoomPath(Room start, Room goal) {
        if (start == goal) return List.of(start);
        Deque<Room> queue = new ArrayDeque<>();
        Map<Room, Room> parent = new HashMap<>();
        Set<Room> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Room curr = queue.poll();
            if (curr == goal) {
                LinkedList<Room> path = new LinkedList<>();
                for (Room at = goal; at != null; at = parent.get(at)) path.addFirst(at);
                return path;
            }
            for (Door d : doors) {
                Room nb = d.neighborOf(curr);
                if (nb != null && visited.add(nb)) {
                    parent.put(nb, curr);
                    queue.add(nb);
                }
            }
        }
        return Collections.emptyList();
    }

    private Door doorBetween(Room a, Room b) {
        for (Door d : doors) {
            if ((d.roomA == a && d.roomB == b) || (d.roomA == b && d.roomB == a)) return d;
        }
        return null;
    }

    // ------------------------------------------------------------------ simulation

    public void update(float dt) {
        if (paused || fallen) return;
        timeSurvived += dt;
        updateShield(dt);          // first, so a shield armed during pause is up before anything moves
        updateGate();
        updateDoors(dt);
        for (CrewMember u : units()) updateCrew(u, dt);
        updateCombat(dt);
        checkLuckySurvival();
        updateDoorAttacks(dt);
        updateRepairs(dt);
        updateDoorRepairs(dt);
        enforceRoomPower();
        updateWeapons(dt);
        updateLoom(dt);
        updateCamp(dt);
        updateBandits(dt);
        updateEnemyShots(dt);
        updateAether(dt);
        updateHealth(dt);
        removeDead();
        updateTraining(dt);
        updateAttunementPoints(dt);
        for (CrewMember c : crew) highestLevel = Math.max(highestLevel, c.level);
        if (!fallen && crew.stream().noneMatch(c -> c.alive)) {
            fallen = true;
            events.add(GameEvent.of(GameEvent.Type.FORTRESS_FALLEN, null));
            events.add(GameEvent.notice("The fortress has fallen."));
        }
    }

    // ------------------------------------------------------------------ game over
    boolean fallen = false;
    float timeSurvived = 0f;
    int highestLevel = 0;
    /** Every crew member is dead: the world stops and the game-over card shows. */
    public boolean isFallen() { return fallen; }
    public float timeSurvived() { return timeSurvived; }
    public int campsCleared() { return camp.cleared; }
    public int highestLevel() {
        int h = highestLevel;
        for (CrewMember c : crew) h = Math.max(h, c.level);
        return h;
    }

    private void updateLoom(float dt) {
        if (!loom.isWeaving()) return;
        loom.charge = Math.min(1f, loom.charge + dt * loomSpeed() / loom.chargeSeconds);
        if (loom.isReady()) events.add(GameEvent.value(GameEvent.Type.LOOM_READY, 0f));
    }

    private void updateWeapons(float dt) {
        for (Weapon w : weapons) {
            if (w.powered && w.isManned() && w.charge < 1f) {
                w.charge = Math.min(1f, w.charge + dt / chargeSecondsOf(w));
            }
            if (w.autoFire && canFire(w)) launch(w);
        }

        List<Projectile> next = new ArrayList<>();
        Iterator<Projectile> it = projectiles.iterator();
        while (it.hasNext()) {
            Projectile p = it.next();
            if (!p.update(dt)) continue;
            it.remove();
            if (p.checkShield) {   // at the camp's shield bubble
                if (camp.layers > 0) {
                    camp.layers--;
                    camp.recharge = 0f;
                    events.add(GameEvent.at(GameEvent.Type.ENEMY_SHIELD_BLOCK, p.to, camp.layers));
                } else {
                    next.add(new Projectile(p.hit, p.to, p.target.center(), p.damage, p.target, false, p.burnPower));
                }
            } else {
                hitCampRoom(p);
            }
        }
        projectiles.addAll(next);
    }

    /** A shot landing on a camp room: one health bar, and weapon damage x5 to every bandit inside. */
    private void hitCampRoom(Projectile p) {
        Room r = p.target;
        r.health = Math.max(0, r.health - 1);
        float unitDamage = p.damage * GEM_UNIT_DAMAGE_MULTIPLIER;
        Hit h = new Hit(unitDamage, p.hit.type(), p.hit.attunements(), p.hit.source(), p.hit.label());
        for (CrewMember b : banditsIn(r)) {
            applyHit(b, h);
            if (p.burnPower > 0) setBurning(b, p.burnPower);
        }
        events.add(new GameEvent(GameEvent.Type.TARGET_HIT, null, null, unitDamage, r.center(), h.type(), h.tint()));
    }

    /** Fire's burn: BURN_DPS x power for BURN_SECONDS (a stronger burn replaces a weaker one). */
    private void setBurning(CrewMember u, float power) {
        float dps = Attunements.BURN_DPS * power;
        u.burnDps = u.burnTime > 0 ? Math.max(u.burnDps, dps) : dps;
        u.burnTime = Attunements.BURN_SECONDS;
    }

    /** A device can't use more power than its room's health: switch off anything that no longer fits. */
    private void enforceRoomPower() {
        for (Weapon w : weapons) {
            if (w.powered && w.room.health < w.powerCost) {
                w.powered = false;
                w.charge = 0f;
                events.add(GameEvent.notice(w.name + " lost power: " + w.room.name + " is too damaged."));
            }
        }
        if (loom.powered && loom.room.health < loom.powerCost) {
            loom.powered = false;
            events.add(GameEvent.notice("The Loom lost power: " + loom.room.name + " is too damaged."));
        }
        boolean running = permeator.isRunning();   // never switched off: it's "down" while its room is wrecked
        if (permeator.wasRunning && !running && permeator.powered)
            events.add(GameEvent.notice("The Permeator is down: its room is too damaged. Aether is draining! Repair it to restart."));
        if (!permeator.wasRunning && running)
            events.add(GameEvent.notice("The Permeator is running again."));
        permeator.wasRunning = running;
        if (shield.powered && shield.room.health < shield.powerCost) {
            setShieldPowered(false);
            events.add(GameEvent.notice(shield.name + " lost power: " + shield.room.name + " is too damaged."));
        }
    }

    private void updateRepairs(float dt) {
        for (Room r : rooms.values()) {
            if (!r.hasHealth()) continue;
            if (!r.isDamaged()) { r.repair = 0f; continue; }
            float rate = repairRate(r);
            if (rate <= 0) continue;           // progress is kept until someone comes back
            r.repair += dt * rate;
            if (r.repair >= 1f) {
                r.repair = 0f;
                r.health++;
                events.add(GameEvent.at(GameEvent.Type.ROOM_REPAIRED, r.center(), r.health));
            }
        }
    }

    private void updateShield(float dt) {
        if (shield.queued) {         // pressed while paused
            shield.queued = false;
            raiseShield();
        }
        int max = maxShieldLayers();
        if (!shield.powered) {
            shield.layers = 0;
            shield.recharge = 0f;
        } else {
            if (shield.layers > max) shield.layers = max;
            if (shield.layers < max) {
                shield.recharge += dt / shieldRechargeSeconds();
                if (shield.recharge >= 1f) { shield.recharge = 0f; shield.layers++; }
            } else {
                shield.recharge = 0f;
            }
        }
        switch (shield.state) {
            case UP -> {
                shield.stateTime += dt;
                if (shield.stateTime > ShieldConduit.BLOCK_WINDOW) {   // nothing arrived in time
                    shield.state = ShieldConduit.State.COOLDOWN;
                    shield.stateTime = ShieldConduit.MISS_COOLDOWN;
                    events.add(GameEvent.at(GameEvent.Type.SHIELD_MISS, shield.orb, 0f));
                }
            }
            case COOLDOWN -> {
                shield.stateTime -= dt;
                if (shield.stateTime <= 0) { shield.state = ShieldConduit.State.READY; shield.stateTime = 0f; }
            }
            case READY -> { }
        }
    }

    private void updateEnemyShots(float dt) {
        Iterator<EnemyShot> it = enemyShots.iterator();
        while (it.hasNext()) {
            EnemyShot s = it.next();
            s.elapsed += dt;
            if (!s.ignoresShields && !s.pastShield && s.elapsed >= EnemyShot.APPROACH_SECONDS) {
                if (shield.state == ShieldConduit.State.UP) {
                    boolean parry = shield.stateTime <= parryWindow();
                    shield.state = ShieldConduit.State.READY;
                    shield.stateTime = 0f;
                    it.remove();
                    if (parry) {
                        float dmg = s.damage * ShieldConduit.PARRY_MULTIPLIER;
                        shootAtCamp(new Hit(dmg, DamageType.MAGIC, List.of(Attunement.ARCANE), null, "a parried shot"), s.contact, camp.tower, dmg);
                        events.add(GameEvent.at(GameEvent.Type.SHIELD_PARRY, s.contact, dmg));
                    } else {
                        shield.layers = Math.max(0, shield.layers - 1);
                        events.add(GameEvent.at(GameEvent.Type.SHIELD_BLOCK, s.contact, shield.layers));
                    }
                    continue;
                }
                s.pastShield = true;
            }
            if (s.hasLanded()) {
                it.remove();
                Room r = s.target;
                r.health = Math.max(0, r.health - 1);
                events.add(GameEvent.at(GameEvent.Type.ROOM_HIT, s.to, r.health));
            }
        }
    }

    private void updateDoors(float dt) {
        for (Door d : doors) {
            boolean open = d.broken || d.mode == Door.Mode.OPEN;
            if (!open) {
                for (CrewMember u : units()) {
                    if (u.crossing == d) { open = true; break; } // never close on someone mid-doorway
                }
            }
            if (!open) {      // closed doors open for their own side walking by
                Faction f = doorOpensFor(d);
                if (f != null) {
                    for (CrewMember u : f == Faction.PLAYER ? crew : bandits) {
                        if (u.isMoving() && d.center.distanceTo(u.x, u.y) < DOOR_SENSOR_RANGE) { open = true; break; }
                    }
                }
            }
            float target = open ? 1f : 0f;
            d.openAmount += (target - d.openAmount) * Math.min(1f, dt * 10f);
        }
    }

    /** Tells the player when control of the gate changes hands. */
    private void updateGate() {
        Faction c = gateController();
        if (c == gateHolder) return;
        gateHolder = c;
        String text = c == Faction.PLAYER ? "You control the gate in No Man's Land."
                : c == Faction.BANDITS ? "The bandits control the gate!"
                : "Nobody controls the gate.";
        events.add(new GameEvent(GameEvent.Type.GATE_CONTROL, null, text, c == null ? 0 : c.ordinal() + 1, gate.center));
        events.add(GameEvent.notice(text));
    }

    // ------------------------------------------------------------------ combat (FTL-style auto-fight)

    /** Hostile units in the same room hit each other automatically. Walking units don't swing (but can be hit). */
    private void updateCombat(float dt) {
        List<CrewMember> all = units();
        for (CrewMember u : all) { u.wasFighting = u.fighting; u.fighting = false; }
        for (CrewMember u : all) {
            if (!u.alive || u.walking) { u.attackTimer = 0f; continue; }
            CrewMember target = null;
            for (CrewMember e : u.isBandit() ? crew : bandits) {
                if (!e.alive || e.hp <= 0 || e.currentRoom != u.currentRoom) continue;
                if (target == null || e.hp < target.hp) target = e;   // focus the weakest
            }
            if (target == null) { u.attackTimer = 0f; continue; }
            u.fighting = true;
            if (Math.abs(target.x - u.x) > 1f) u.facingRight = target.x > u.x;
            u.attackTimer += dt;
            if (u.attackTimer >= CrewMember.ATTACK_INTERVAL) {
                u.attackTimer -= CrewMember.ATTACK_INTERVAL;
                if (rng.nextFloat() < StatRules.dodgeChance(target)) {   // Luck: a dodge
                    events.add(new GameEvent(GameEvent.Type.DODGE, target, null, 0f, new Pos(target.x, target.y)));
                    continue;
                }
                // All melee is BLUDGEONING for now (class and weapon change it in P4).
                // A Bludgeoning-attuned blob matches it like any bludgeoning weapon.
                Attunements.Match m = Attunements.match(u, List.of(), MELEE_TYPE);
                float dmg = StatRules.meleeDps(u) * CrewMember.ATTACK_INTERVAL * (1f + Attunements.DAMAGE_BONUS * m.power());
                for (Attunement a : m.matched()) useAttunement(u, a, Attunements.POINTS_PER_USE);
                float dealt = applyHit(target, new Hit(dmg, MELEE_TYPE, List.of(), u, u.name));
                events.add(new GameEvent(GameEvent.Type.ATTACK, u, null, dealt, new Pos(u.x, u.y)));
            }
        }
    }

    /** Pedestal art (DeviceArt): the gem floats this far above the foot, the orb sits this far above it. In-map scale. */
    public static final float DEVICE_SCALE = 0.85f, GEM_ABOVE_FOOT = 32f * DEVICE_SCALE, ORB_ABOVE_FOOT = 28f * DEVICE_SCALE;

    /** Every melee blow's damage type until personal weapons (P4). */
    public static final DamageType MELEE_TYPE = DamageType.BLUDGEONING;

    /**
     * The one place a unit takes damage: armour (by damage type), then resist/weakness (by attunement).
     * MENTAL hits that land train the attacker's Psyche. Returns the damage dealt.
     */
    float applyHit(CrewMember target, Hit h) {
        if (target == null || !target.alive || h.amount() <= 0) return 0f;
        float dmg = h.amount() * target.armour.taken(h.type()) * Attunements.taken(target, h.attunements());
        target.hp -= dmg;
        target.lastHit = h;
        target.lastHitDamage = dmg;
        target.lastHurtBy = h.label() != null ? h.label() : h.source() != null ? h.source().name : null;
        hurtXp(target, dmg);
        if (h.type() == DamageType.MENTAL && h.source() != null && !h.source().isBandit() && dmg > 0)
            gainStatXp(h.source(), Stat.PSYCHE, dmg * StatRules.PSYCHE_XP_PER_MENTAL_DAMAGE);
        String by = h.source() != null ? h.source().name : "burning".equals(h.label()) ? "burn" : h.label();
        events.add(new GameEvent(GameEvent.Type.UNIT_HIT, target, by, dmg, new Pos(target.x, target.y), h.type(), h.tint()));
        return dmg;
    }

    /** A fight just ended and one of your crew is still standing on (nearly) one health: the whole crew gets lucky. */
    private void checkLuckySurvival() {
        for (CrewMember c : crew) {
            if (!c.alive || !c.wasFighting || c.fighting || c.hp <= 0) continue;
            if (c.hp > Math.max(1f, c.maxHp() * LUCKY_SURVIVAL_SHARE)) continue;
            crewLuck(c.name + " survived a fight on " + Math.round(Math.ceil(c.hp)) + " health!", new Pos(c.x, c.y));
        }
    }

    /** Something super lucky happened: every living crew member gains 1 Luck. */
    public void crewLuck(String why, Pos where) {
        for (CrewMember c : crew) if (c.alive) c.gifted[Stat.LUCK.ordinal()]++;
        events.add(GameEvent.at(GameEvent.Type.LUCKY, where, 1f));
        events.add(GameEvent.notice("Lucky! " + why + " The whole crew gains +1 Luck."));
    }

    /** Units stuck at a door that won't open for them hit it until it breaks. */
    private void updateDoorAttacks(float dt) {
        for (Door d : doors) d.underAttack = false;
        for (CrewMember u : units()) {
            u.breakingDoor = false;
            if (!u.alive || !u.waitingAtDoor || u.fighting || u.steps.isEmpty()) continue;
            Door d = u.steps.peekFirst().door();
            if (d == null || d.broken || d.mode == Door.Mode.OPEN || doorOpensFor(d) == u.faction) continue;
            u.breakingDoor = true;
            d.underAttack = true;
            float dmg = StatRules.meleeDps(u) * DOOR_DAMAGE_FACTOR * dt;
            d.healthFraction -= dmg / doorMaxHealth(d);
            if (d.healthFraction <= 0) {
                d.healthFraction = 0f;
                d.broken = true;
                events.add(GameEvent.at(GameEvent.Type.DOOR_BROKEN, d.center, 0f));
                String which = d.gate ? "The gate" : "The door between " + d.roomA.name + " and " + d.roomB.name;
                events.add(GameEvent.notice(which + " was broken down by " + u.name + "."));
            }
        }
    }

    /** Damaged and broken doors mend while someone of their side stands next to them (and nobody is hitting them). */
    private void updateDoorRepairs(float dt) {
        for (Door d : doors) {
            if ((!d.broken && d.healthFraction >= 1f) || d.underAttack) continue;
            Faction f = doorOpensFor(d);
            if (f == null) continue;
            boolean someone = false;
            for (CrewMember u : f == Faction.PLAYER ? crew : bandits) {
                if (!u.isMoving() && !u.fighting && (u.currentRoom == d.roomA || u.currentRoom == d.roomB)) { someone = true; break; }
            }
            if (!someone) continue;
            d.healthFraction = Math.min(1f, d.healthFraction + dt / Door.REPAIR_SECONDS);
            if (d.healthFraction >= 1f && d.broken) d.broken = false;
        }
    }

    // ------------------------------------------------------------------ the Bandit Camp

    private void spawnBandits() {
        // Level 1 "Bandit-Wannabe" blobs: no class until level 5, all stats 0, so the camp shield has 1 layer
        spawnBandit("Blob", BanditRole.SHIELD, camp.shieldRoom);
        spawnBandit("Blob", BanditRole.GUNNER, camp.tower);
        spawnBandit("Blob", BanditRole.GUARD, camp.quarters);
    }

    /**
     * For classed bandits (none spawn yet: the starting Bandit-Wannabes are Unclassed). Bandits don't
     * train: they arrive as Tier 1 with a fixed sheet built from their class ranking
     * (strongest 20, second 10, third 10, the rest 2). PLACEHOLDER values; they match the old balance
     * (Heavy Melee 150 health and 10 dps, Ranged 7.5 dps, Magic +1 shield layer).
     */
    private void giveBanditStats(CrewMember b) {
        Stat[] rank = b.crewClass.ranking;
        b.hp = b.maxHp();
        if (rank == null) return;
        int[] values = {20, 10, 10, 2, 2, 2, 2, 2};
        for (int i = 0; i < rank.length; i++) b.gifted[rank[i].ordinal()] = values[i];
        if (b.crewClass == CrewClass.HEAVY_MELEE) b.gifted[Stat.VITALITY.ordinal()] = 20;   // the old +50% health
        // Armour (PLACEHOLDER): Heavy Melee plate, Ranged leather, Magic mana
        b.armour = switch (b.crewClass) {
            case HEAVY_MELEE -> Armour.PLATE;
            case RANGED -> Armour.LEATHER;
            case MAGIC -> Armour.MANA;
            default -> Armour.NONE;
        };
        b.hp = b.maxHp();
    }

    /** Descriptor shown (grey italics) instead of a class for the starting bandits. */
    public static final String WANNABE = "Bandit-Wannabe";

    private void spawnBandit(String name, BanditRole role, Room preferred) {
        List<Room> options = new ArrayList<>(List.of(preferred, camp.quarters, camp.shieldRoom, camp.tower, camp.entrance));
        for (Room r : options) {
            int pad = r.padOccupants[0] == null ? 0 : r.firstFreePad();
            if (pad < 0) continue;
            CrewMember b = new CrewMember(name, CrewClass.UNCLASSED, r, pad, Faction.BANDITS);
            b.role = role;
            b.descriptor = WANNABE;
            b.level = 1;
            giveBanditStats(b);   // Unclassed: no sheet, no armour
            bandits.add(b);
            return;
        }
    }

    private void updateCamp(float dt) {
        // Shield layers recharge one at a time, like yours
        int max = campShieldMax();
        if (camp.layers > max) camp.layers = max;
        if (camp.layers < max) {
            camp.recharge += dt / BanditCamp.LAYER_RECHARGE_SECONDS;
            if (camp.recharge >= 1f) { camp.recharge = 0f; camp.layers++; }
        } else {
            camp.recharge = 0f;
        }

        if (camp.phase == BanditCamp.Phase.CLEARED) {
            camp.resetTimer -= dt;
            if (camp.resetTimer <= 0) resetCamp();
            return;
        }
        if (bandits.isEmpty()) {
            camp.phase = BanditCamp.Phase.CLEARED;
            camp.resetTimer = BanditCamp.RESET_SECONDS;
            camp.cleared++;
            camp.windup = -1f;
            camp.aim = null;
            events.add(GameEvent.at(GameEvent.Type.CAMP_CLEARED, camp.quarters.center(), camp.cleared));
            events.add(GameEvent.notice("Camp cleared! A new band moves in in " + Math.round(BanditCamp.RESET_SECONDS) + " s."));
            return;
        }

        // Whose job the ballista is: the GUNNER bandit at first; if the gunner dies, a free bandit takes over.
        // Nobody free for a few seconds (all busy fighting) -> they board instead (no stalemate).
        if (camp.phase == BanditCamp.Phase.HOLDING && camp.bolts > 0) {
            if (camp.gunner == null || !camp.gunner.alive || camp.gunner.hp <= 0) camp.gunner = pickGunner();
            if (camp.gunner == null) {
                camp.unmannedTime += dt;
                if (camp.unmannedTime >= BanditCamp.UNMANNED_BOARD_SECONDS) {
                    camp.phase = BanditCamp.Phase.BOARDING;
                    camp.windup = -1f;
                    camp.aim = null;
                    events.add(GameEvent.at(GameEvent.Type.BOARDING, camp.quarters.center(), 0f));
                    events.add(GameEvent.notice("Nobody is manning the ballista. The bandits are coming to board you!"));
                    return;
                }
            } else {
                camp.unmannedTime = 0f;
            }
        }

        // The ballista: needs a bandit on its station, a standing tower and bolts left
        CrewMember op = camp.tower.stationOperator();
        boolean manned = op != null && op.isBandit() && camp.tower.health >= 1 && camp.bolts > 0;
        if (camp.windup >= 0) {
            if (!manned) { camp.windup = -1f; camp.aim = null; return; }
            camp.windup += dt;
            if (camp.windup >= BanditCamp.WINDUP_SECONDS) fireBallista();
        } else if (manned) {
            camp.fireTimer -= dt;
            if (camp.fireTimer <= 0) {
                camp.fireTimer = BanditCamp.BALLISTA_INTERVAL;
                camp.windup = 0f;
                List<Room> targets = fortressRooms();
                camp.aim = targets.get(rng.nextInt(targets.size()));
            }
        }
    }

    /** A free bandit (alive, not fighting) for the ballista: the GUNNER first, then the GUARD, then the SHIELD bandit. */
    private CrewMember pickGunner() {
        CrewMember best = null;
        int bestRank = 99;
        for (CrewMember b : bandits) {
            if (!b.alive || b.hp <= 0 || b.fighting) continue;
            int rank = b.role == BanditRole.GUNNER ? 0 : b.role == BanditRole.SHIELD ? 2 : 1;
            if (rank < bestRank) { best = b; bestRank = rank; }
        }
        return best;
    }

    private void fireBallista() {
        camp.windup = -1f;
        Room target = camp.aim;
        camp.aim = null;
        if (target == null) return;
        camp.bolts--;
        enemyShots.add(EnemyShot.bolt(camp.ballista, target));
        events.add(GameEvent.at(GameEvent.Type.ENEMY_FIRED, camp.ballista, camp.bolts));
        if (camp.bolts <= 0) {
            camp.phase = BanditCamp.Phase.BOARDING;
            events.add(GameEvent.at(GameEvent.Type.BOARDING, camp.quarters.center(), 0f));
            events.add(GameEvent.notice("The ballista is out of bolts. The bandits are coming to board you!"));
        }
    }

    /** A fresh camp: rooms and doors mended, 10 bolts, three new bandits. */
    private void resetCamp() {
        for (Room r : rooms.values()) if (r.isCamp()) { r.health = Room.MAX_HEALTH; r.repair = 0f; }
        for (Door d : doors) if (d.owner == Faction.BANDITS) { d.healthFraction = 1f; d.broken = false; d.mode = Door.Mode.CLOSED; }
        camp.bolts = BanditCamp.MAX_BOLTS;
        camp.fireTimer = BanditCamp.FIRST_SHOT_DELAY;
        camp.windup = -1f;
        camp.aim = null;
        camp.layers = BanditCamp.BASE_LAYERS;
        camp.recharge = 0f;
        camp.phase = BanditCamp.Phase.HOLDING;
        camp.gunner = null;
        camp.unmannedTime = 0f;
        spawnBandits();
        events.add(GameEvent.at(GameEvent.Type.CAMP_RESET, camp.quarters.center(), 0f));
        events.add(GameEvent.notice("A new band of bandits has taken over the camp."));
    }

    /** Bandit brains: stop and fight when enemies are in the room, otherwise go where their role says. */
    private void updateBandits(float dt) {
        for (CrewMember b : bandits) {
            if (!b.alive) continue;
            Room here = b.currentRoom;
            if (hasEnemyIn(here, b)) {
                b.sabotage = 0f;
                // Stand and fight (unless halfway through a doorway, which finishes first)
                if (b.isMoving() && b.crossing == null && b.destinationRoom != here) tryMove(b, here, -1, false);
                continue;
            }
            // Boarders wreck undefended fortress rooms
            if (camp.phase == BanditCamp.Phase.BOARDING && here.isFortress() && !b.isMoving()
                    && b.destinationRoom == here && here.health > 0) {
                b.sabotage += dt;
                if (b.sabotage >= SABOTAGE_SECONDS) {
                    b.sabotage = 0f;
                    here.health--;
                    events.add(GameEvent.at(GameEvent.Type.ROOM_HIT, here.center(), here.health));
                    events.add(new GameEvent(GameEvent.Type.ROOM_SABOTAGED, b, here.name, here.health, here.center()));
                }
                if (here.health > 0) continue;
            } else {
                b.sabotage = 0f;
            }
            Room goal = goalFor(b);
            boolean offStation = b == camp.gunner && goal == camp.tower && b.destinationPad != 0
                    && camp.tower.padOccupants[0] == null;   // the gunner steps onto the ballista once it's free
            if (goal == null || (goal == b.destinationRoom && !offStation)) continue;
            boolean wantsStation = goal.station && (goal == camp.shieldRoom || goal == camp.tower);
            tryMove(b, goal, wantsStation ? 0 : -1, false);
        }
    }

    private Room goalFor(CrewMember b) {
        if (camp.phase == BanditCamp.Phase.BOARDING) {
            Room chase = nearest(b, rooms.values().stream().filter(r -> hasPlayerIn(r)).toList());
            if (chase != null) return chase;
            Room wreck = nearest(b, rooms.values().stream().filter(r -> r.isFortress() && r.health > 0).toList());
            return wreck != null ? wreck : b.destinationRoom;
        }
        // Holding the camp
        if (b == camp.gunner) return camp.tower;
        if (b.role == BanditRole.SHIELD) return camp.shieldRoom;
        if (b.role == BanditRole.GUNNER) return camp.tower;
        Room defend = nearest(b, rooms.values().stream()
                .filter(r -> (r.isCamp() || r == room("NML_EAST")) && hasPlayerIn(r)).toList());
        return defend != null ? defend : camp.quarters;
    }

    private boolean hasPlayerIn(Room r) {
        for (CrewMember c : crew) if (c.alive && c.currentRoom == r) return true;
        return false;
    }

    /** The room in the list that's the fewest doors away (ties: list order). */
    private Room nearest(CrewMember b, List<Room> options) {
        Room best = null;
        int bestLen = Integer.MAX_VALUE;
        for (Room r : options) {
            int len = findRoomPath(b.currentRoom, r).size();
            if (len > 0 && len < bestLen) { best = r; bestLen = len; }
        }
        return best;
    }

    // ------------------------------------------------------------------ Aether and health

    private void updateAether(float dt) {
        Room source = permeator.room;
        boolean running = permeator.isRunning();
        for (Room r : rooms.values()) {
            if (!r.isFortress()) { r.aether = 1f; continue; }   // outdoors: open air
            if (running) {
                if (r == source) r.aether = clamp01(r.aether + Permeator.FILL_PER_SECOND * dt);
            } else if (r == source) {
                r.aether = clamp01(r.aether - r.aether * Permeator.VENT_PER_SECOND * dt);   // vents, slower as it empties
            } else {
                r.aether = clamp01(r.aether - Permeator.LEAK_PER_SECOND * dt);
            }
        }
        // Aether spreads through doors: open (or held open) = full flow, shut = a trickle
        for (Door d : doors) {
            if (!d.roomA.isFortress() || !d.roomB.isFortress()) continue;
            float flow = AETHER_FLOW * (d.isOpen() ? 1f : Permeator.SHUT_DOOR_FLOW);
            float diff = d.roomA.aether - d.roomB.aether;
            float move = diff * Math.min(0.5f, flow * dt);
            d.roomA.aether -= move;
            d.roomB.aether += move;
        }
    }

    private void updateHealth(float dt) {
        Room ward = room("CHRYSALIS");
        for (CrewMember c : units()) {
            float max = c.maxHp();
            if (c.currentRoom.aether < SUFFOCATE_BELOW) {
                c.hp -= SUFFOCATE_DAMAGE_PER_SECOND * dt;
                c.lastHurtBy = "Aether";
                hurtXp(c, SUFFOCATE_DAMAGE_PER_SECOND * dt);
            } else if (!c.isBandit() && c.currentRoom == ward && !c.isMoving() && !c.fighting) {
                c.hp = Math.min(max, c.hp + WARD_HEAL_PER_SECOND * StatRules.healMultiplier(c) * dt);
            }
            if (c.burnTime > 0) {   // Fire attunement's burn: ticks once a second
                c.burnTime -= dt;
                c.burnTick += dt;
                if (c.burnTick >= 1f) {
                    c.burnTick -= 1f;
                    // status damage: no damage type, so armour doesn't touch it (Fire resist/weakness still does)
                    applyHit(c, new Hit(c.burnDps > 0 ? c.burnDps : Attunements.BURN_DPS, null,
                            List.of(Attunement.FIRE), null, "burning"));
                }
                if (c.burnTime <= 0) { c.burnTime = 0f; c.burnTick = 0f; }
            }
            if (c.hp > max) c.hp = max;
        }
    }

    private void removeDead() {
        for (List<CrewMember> list : List.of(crew, bandits)) {
            Iterator<CrewMember> it = list.iterator();
            while (it.hasNext()) {
                CrewMember c = it.next();
                if (c.hp > 0) continue;
                c.hp = 0;
                c.alive = false;
                c.fighting = false;
                c.crossing = null;
                if (c.destinationRoom.padOccupants[c.destinationPad] == c) c.destinationRoom.padOccupants[c.destinationPad] = null;
                it.remove();
                String how = "Aether".equals(c.lastHurtBy) ? " ran out of Aether and died."
                        : c.lastHurtBy != null ? " was killed by " + c.lastHurtBy + "." : " died.";
                events.add(new GameEvent(GameEvent.Type.CREW_DIED, c, c.name + how, 0f, new Pos(c.x, c.y)));
                events.add(GameEvent.notice(c.name + how));
            }
        }
    }

    private static float clamp01(float v) { return Math.max(0f, Math.min(1f, v)); }

    // ------------------------------------------------------------------ training (stats and levels)

    /**
     * Which stats this blob is practising right now (several can train at once):
     *   Weapons Tower station -> Dexterity     Loom station -> Arcana     Shield station -> Arcana + Psyche
     *   Repairing a damaged room -> Knowledge  Fighting -> Strength
     *   In No Man's Land -> Resolve            Resting / healing in the Chrysalis Ward -> Vitality
     * Damage taken also trains Resolve and Vitality (see hurtXp). Charisma waits for Morale and Madness.
     */
    public Set<Stat> practising(CrewMember c) {
        Set<Stat> out = EnumSet.noneOf(Stat.class);
        if (c.isMoving() || !c.alive || c.isBandit()) return out;
        Room r = c.currentRoom;
        if (c.fighting) out.add(Stat.STRENGTH);
        if (r.isNoMansLand()) out.add(Stat.RESOLVE);
        if (c.fighting) return out;
        if (r.isDamaged() && r.side.owner == c.faction) out.add(Stat.KNOWLEDGE);
        if (r.id.equals("CHRYSALIS")) out.add(Stat.VITALITY);
        if (r.stationOperator() == c) {
            if (r == weapons.get(0).room && weapons.get(0).powered) out.add(Stat.DEXTERITY);
            if (r == loom.room && loom.powered) out.add(Stat.ARCANA);
            if (r == shield.room && shield.powered) { out.add(Stat.ARCANA); out.add(Stat.PSYCHE); }
        }
        return out;
    }

    private void updateTraining(float dt) {
        for (CrewMember c : crew) {
            Set<Stat> now = practising(c);
            c.training.clear();
            c.training.addAll(now);
            if (now.isEmpty()) continue;
            for (Stat s : now) gainStatXp(c, s, XP_PER_SECOND * dt);
            // Resting doesn't count as work for the total level; everything else does
            if (!(now.size() == 1 && now.contains(Stat.VITALITY))) gainLevelXp(c, XP_PER_SECOND * dt);
        }
    }

    /** Taking damage trains Resolve and Vitality (your crew only). */
    private void hurtXp(CrewMember c, float damage) {
        if (c.isBandit() || !c.alive || damage <= 0) return;
        gainStatXp(c, Stat.RESOLVE, damage * XP_PER_DAMAGE);
        gainStatXp(c, Stat.VITALITY, damage * XP_PER_DAMAGE);
    }

    /** How fast this blob learns a stat right now (learning arrows + class stat, Knowledge, Test Mode). */
    public float learningRate(CrewMember c, Stat s) {
        float m = 1f + c.learningBonus(s);
        m *= 1f + Progression.KNOWLEDGE_LEARNING_PER_POINT * c.stat(Stat.KNOWLEDGE);
        if (testMode) m *= Progression.TEST_MODE_MULTIPLIER;
        return m;
    }

    private void gainStatXp(CrewMember c, Stat s, float xp) {
        int i = s.ordinal();
        if (c.statCapped(s)) { c.statXp[i] = 0f; return; }
        c.statXp[i] += xp * learningRate(c, s);
        while (!c.statCapped(s) && c.statXp[i] >= Progression.statXpFor(c.trained[i])) {
            c.statXp[i] -= Progression.statXpFor(c.trained[i]);
            c.trained[i]++;
            events.add(new GameEvent(GameEvent.Type.STAT_UP, c, s.shortLabel, c.stat(s), new Pos(c.x, c.y)));
        }
        if (c.statCapped(s)) c.statXp[i] = 0f;
    }

    private void gainLevelXp(CrewMember c, float xp) {
        if (c.atLevelCap()) { c.levelXp = 0f; return; }
        c.levelXp += xp * (testMode ? Progression.TEST_MODE_MULTIPLIER : 1f);
        while (!c.atLevelCap() && c.levelXp >= Progression.levelXpFor(c.level)) {
            c.levelXp -= Progression.levelXpFor(c.level);
            levelUp(c);
        }
        if (c.atLevelCap()) c.levelXp = 0f;
    }

    /** +1 level: free points (tier + 1) and the class bonus (+2 strongest, +1 second strongest). */
    private void levelUp(CrewMember c) {
        List<ClassNode> before = unlockedNext(c);
        c.level++;
        c.freePoints += Progression.freePointsPerLevel(c.tier);
        Stat first = c.crewClass.strongest(), second = c.crewClass.secondStrongest();
        if (first != null) c.gifted[first.ordinal()] += 2;
        if (second != null) c.gifted[second.ordinal()] += 1;
        events.add(new GameEvent(GameEvent.Type.LEVEL_UP, c, null, c.level, new Pos(c.x, c.y)));
        for (ClassNode n : unlockedNext(c)) {
            if (!before.contains(n)) {
                events.add(new GameEvent(GameEvent.Type.CLASS_UNLOCKED, c, n.name, 0f, new Pos(c.x, c.y)));
                events.add(GameEvent.notice(c.name + " reached level " + c.level + " and can change class! "
                        + "Send them to the Chrysalis Ward."));
                break;
            }
        }
    }

    /** Spend one free point on a stat. Free points ignore the tier cap (up to the hard max). */
    public boolean spendFreePoint(CrewMember c, Stat s) {
        if (c.isBandit() || c.freePoints <= 0 || c.stat(s) >= Progression.MAX_STAT) return false;
        c.freePoints--;
        c.gifted[s.ordinal()]++;
        return true;
    }

    // ------------------------------------------------------------------ dev console

    public boolean isTestMode() { return testMode; }
    public void setTestMode(boolean on) {
        testMode = on;
        events.add(GameEvent.notice(on ? "Test Mode on: everyone learns " + Math.round(Progression.TEST_MODE_MULTIPLIER) + "x faster."
                : "Test Mode off: back to real pace."));
    }

    /** Dev: jump straight to the level cap (free points and class bonuses included). */
    public void devLevelToCap(CrewMember c) {
        if (c == null || c.isBandit()) return;
        while (!c.atLevelCap()) levelUp(c);
        c.levelXp = 0f;
    }

    /** Dev: hand out free points. */
    public void devGivePoints(CrewMember c, int n) {
        if (c == null || c.isBandit()) return;
        c.freePoints += n;
    }

    // ------------------------------------------------------------------ classes and promotion

    /** The class-tree node a crew member currently is (null if the tree isn't loaded). */
    public ClassNode classNodeOf(CrewMember c) {
        return tree == null ? null : tree.byId(c.classId);
    }

    /** Level needed to become this class (the cap of the tier below it), or -1 if it can't be unlocked. */
    public int levelRequirementFor(ClassNode n) {
        if (n == null || n.placeholder || n.tier < 1) return -1;
        return Progression.levelCap(n.tier - 1);
    }

    /** True if this class comes straight after the person's current class. */
    public boolean isNextStep(CrewMember c, ClassNode n) {
        return n != null && n.parent != null && n.parent.id.equals(c.classId);
    }

    /** Next-step class whose level requirement is met (they've hit their tier's level cap). */
    public boolean isUnlocked(CrewMember c, ClassNode n) {
        if (!isNextStep(c, n)) return false;
        int req = levelRequirementFor(n);
        return req >= 0 && c.level >= req;
    }

    /** All classes this person has unlocked as their next step. */
    public List<ClassNode> unlockedNext(CrewMember c) {
        ClassNode cur = classNodeOf(c);
        if (cur == null) return List.of();
        List<ClassNode> out = new ArrayList<>();
        for (ClassNode n : cur.children()) if (isUnlocked(c, n)) out.add(n);
        return out;
    }

    public boolean isInWard(CrewMember c) {
        return c.currentRoom.id.equals("CHRYSALIS") && !c.isMoving();
    }

    /** Can promote right now: has an unlocked class and is standing in the Chrysalis Ward. */
    public boolean canPromoteNow(CrewMember c) {
        return !c.isBandit() && isInWard(c) && !unlockedNext(c).isEmpty();
    }

    /** Anyone in the ward with a class waiting (drives the crystal's hum). */
    public List<CrewMember> readyToPromote() {
        return crew.stream().filter(this::canPromoteNow).toList();
    }

    /** Promote into a class. Must be unlocked, and they must be in the Chrysalis Ward. Stats carry over. */
    public boolean promote(CrewMember c, ClassNode n) {
        if (!isUnlocked(c, n) || !isInWard(c)) return false;
        float frac = c.hp / c.maxHp();
        c.classId = n.id;
        c.className = n.name;
        c.tier = n.tier;
        c.crewClass = CrewClass.forPath(n.path);
        changeAttunements(c, Attunements.of(n));
        c.rarityRerolls = c.isNeutral() ? 0 : 1;   // one optional reroll per promotion
        c.hp = frac * c.maxHp();
        events.add(new GameEvent(GameEvent.Type.PROMOTED, c, n.name, n.tier, new Pos(c.x, c.y)));
        return true;
    }

    /** Your crew with an attunement (for Loom encounters later: burn a barrier, talk to a scholar). */
    public List<CrewMember> crewWith(Attunement a) {
        return crew.stream().filter(c -> c.alive && c.attunements.contains(a)).toList();
    }

    // ------------------------------------------------------------------ attunement points and rarity

    /** What the Shield Conduit counts as for passive attunement points. PLACEHOLDER (the orb is drawn Arcane). */
    public static final List<Attunement> SHIELD_ATTUNEMENTS = List.of(Attunement.ARCANE);

    /**
     * New class, new attunements. Kept attunements keep their rarity and points. Dropped ones pass
     * SWITCH_KEEP (x0.75) of their points, split evenly, to the new ones; each new one rolls its rarity
     * (capped by the class tier).
     */
    void changeAttunements(CrewMember c, List<Attunement> next) {
        List<Attunement> old = c.attunements;
        float moved = 0f;
        for (Attunement a : old) if (!a.isNeutral() && !next.contains(a)) {
            moved += c.attunementPoints(a);
            c.attunementPoints.remove(a);
            c.rarity.remove(a);
        }
        List<Attunement> added = new ArrayList<>();
        for (Attunement a : next) if (!a.isNeutral() && !old.contains(a)) added.add(a);
        for (Attunement a : added) {
            c.rarity.put(a, Attunements.rollRarity(c.tier, rng));
            c.attunementPoints.put(a, moved * Attunements.SWITCH_KEEP / added.size());
        }
        c.attunements = List.copyOf(next);
    }

    /** Spend the promotion's reroll on one attunement: a fresh roll replaces the old rarity (it can go down). */
    public boolean rerollRarity(CrewMember c, Attunement a) {
        if (c.rarityRerolls <= 0 || a == null || a.isNeutral() || !c.attunements.contains(a)) return false;
        c.rarityRerolls--;
        Rarity before = c.rarityOf(a);
        Rarity after = Attunements.rollRarity(c.tier, rng);
        c.rarity.put(a, after);
        events.add(GameEvent.notice(c.name + "'s " + a.label + ": " + before.label + " -> " + after.label + "."));
        return true;
    }

    /** Any time an attunement is used or needed (firing a matching weapon, a chat option, burning wood...). */
    public void useAttunement(CrewMember c, Attunement a, float points) {
        if (c == null || c.isBandit() || !c.alive || a == null || a.isNeutral() || !c.attunements.contains(a)) return;
        c.attunementPoints.merge(a, points * (testMode ? Progression.TEST_MODE_MULTIPLIER : 1f), Float::sum);
    }

    /** True if this room holds something tuned to 'a': a device, or another crew member. */
    public boolean roomHolds(Room r, Attunement a, CrewMember besides) {
        for (Weapon w : weapons) if (w.room == r && w.attunements.contains(a)) return true;
        if (shield.room == r && SHIELD_ATTUNEMENTS.contains(a)) return true;
        for (CrewMember o : crew) if (o != besides && o.alive && o.currentRoom == r && o.attunements.contains(a)) return true;
        return false;
    }

    /** Like meditation: being in (or next door to) a room that holds something of your attunement. */
    public boolean nearSameAttunement(CrewMember c, Attunement a) {
        Room here = c.currentRoom;
        if (roomHolds(here, a, c)) return true;
        for (Door d : doors) {
            Room other = d.roomA == here ? d.roomB : d.roomB == here ? d.roomA : null;
            if (other != null && roomHolds(other, a, c)) return true;
        }
        return false;
    }

    private void updateAttunementPoints(float dt) {
        for (CrewMember c : crew) {
            if (!c.alive || c.isNeutral()) continue;
            for (Attunement a : c.attunements)
                if (!a.isNeutral() && nearSameAttunement(c, a)) useAttunement(c, a, Attunements.PASSIVE_POINTS_PER_SECOND * dt);
        }
    }

    private void refreshClassNames() {
        if (tree == null) return;
        for (CrewMember c : crew) {
            ClassNode n = tree.byId(c.classId);
            if (n != null) { c.className = n.name; c.tier = n.tier; changeAttunements(c, Attunements.of(n)); }
        }
    }

    /** Walking speed for a person: +1% per Dexterity. */
    public float walkSpeedOf(CrewMember u) {
        return WALK_SPEED * StatRules.walkMultiplier(u);
    }

    private void updateCrew(CrewMember u, float dt) {
        u.waitingAtDoor = false;
        if (u.steps.isEmpty()) {
            u.walking = false;
            return;
        }

        CrewMember.Step next = u.steps.peekFirst();
        if (next.door() != null && u.crossing != next.door()) {
            // Standing at the doorway, about to walk through
            if (next.door().openAmount < DOOR_PASSABLE) {
                u.walking = false;
                u.waitingAtDoor = true;
                return;
            }
            u.crossing = next.door();
        }

        u.walking = true;
        float dx = next.p().x() - u.x, dy = next.p().y() - u.y;
        float dist = (float) Math.hypot(dx, dy);
        if (Math.abs(dx) > 1f) u.facingRight = dx > 0;
        float step = walkSpeedOf(u) * dt;

        if (dist <= step) {
            u.x = next.p().x();
            u.y = next.p().y();
            u.distanceWalked += dist;
            u.steps.pollFirst();
            if (next.door() != null) {
                u.crossing = null;
                u.currentRoom = next.door().neighborOf(u.currentRoom);
            }
            if (u.steps.isEmpty()) {
                u.walking = false;
                events.add(GameEvent.of(GameEvent.Type.ARRIVED, u));
            }
        } else {
            u.x += dx / dist * step;
            u.y += dy / dist * step;
            u.distanceWalked += step;
        }
    }
}
