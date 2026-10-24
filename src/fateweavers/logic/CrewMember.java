package fateweavers.logic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.List;
import java.util.Set;

/** One blob: your crew or a bandit. Position is in world space at their feet. */
public class CrewMember {
    public static final float ATTACK_INTERVAL = 1f;   // seconds between melee hits

    /** A point to walk to. If 'door' is set, reaching this point means walking through that door. */
    record Step(Pos p, Door door) {}

    public final String name;
    public final Faction faction;
    CrewClass crewClass;          // family: decides stat archetype and blob shape
    String classId;               // exact class in the class tree
    String className;             // its display name ("Magic User", "Novice Elementalist")
    int tier;                     // 0 = Unclassed
    List<Attunement> attunements = List.of(Attunement.NEUTRAL);   // from the class (Attunements.of), max 2
    final Map<Attunement, Rarity> rarity = new EnumMap<>(Attunement.class);   // rolled when gained
    final Map<Attunement, Float> attunementPoints = new EnumMap<>(Attunement.class);
    int rarityRerolls = 0;        // one optional reroll per promotion
    Armour armour = Armour.NONE;  // bandits only for now
    BanditRole role = null;       // bandits: their job in the camp
    String descriptor = null;     // bandits: shown instead of a class ("Bandit-Wannabe")
    int headband = -1;            // your starting crew: 0 red, 1 blue, 2 green, 3 yellow (-1 = none)
    Room currentRoom;
    float x, y;
    boolean facingRight = true;
    boolean walking = false;
    boolean waitingAtDoor = false;
    float distanceWalked = 0f;
    float hp;
    boolean alive = true;

    // Combat
    boolean fighting = false;     // an enemy is in the same room and they're trading hits
    boolean wasFighting = false;  // last frame (to spot a fight ending)
    boolean breakingDoor = false; // hitting a door that won't open for them
    float attackTimer = 0f;
    float sabotage = 0f;          // bandits: seconds spent wrecking an undefended room
    float burnTime = 0f;          // seconds of burning left
    float burnTick = 0f;
    float burnDps = 0f;           // set by whoever lit them (Fire attunement x rarity)
    String lastHurtBy = null;
    Hit lastHit = null;           // the last hit that landed (for the panel's "Last hit" row)
    float lastHitDamage = 0f;

    // Stats (see Progression and StatRules)
    final int[] trained = new int[Stat.COUNT];   // from practice: limited by the tier cap
    final int[] gifted = new int[Stat.COUNT];    // free points, class level bonuses, crew-wide Luck: ignore the cap
    final float[] statXp = new float[Stat.COUNT];
    int level = 0;
    float levelXp = 0f;
    int freePoints = 0;
    final Set<Stat> training = EnumSet.noneOf(Stat.class);   // what they're practising right now

    final Deque<Step> steps = new ArrayDeque<>();
    Door crossing = null; // door currently being walked through
    Room destinationRoom;
    int destinationPad;

    CrewMember(String name, CrewClass cls, Room startRoom, int pad, Faction faction) {
        this.name = name;
        this.faction = faction;
        this.crewClass = cls;
        this.classId = cls.treeId;
        this.className = cls.title;
        this.tier = cls == CrewClass.UNCLASSED ? 0 : 1;
        this.currentRoom = startRoom;
        this.destinationRoom = startRoom;
        this.destinationPad = pad;
        startRoom.padOccupants[pad] = this;
        this.x = startRoom.pads[pad].x();
        this.y = startRoom.pads[pad].y();
        this.facingRight = faction == Faction.PLAYER;
        this.hp = maxHp();
    }

    public CrewClass crewClass() { return crewClass; }
    public String classId() { return classId; }
    public String className() { return className; }
    public int tier() { return tier; }
    public List<Attunement> attunements() { return attunements; }
    /** Rarity of one of this blob's attunements (Common if it was never rolled). */
    public Rarity rarityOf(Attunement a) { return rarity.getOrDefault(a, Rarity.COMMON); }
    public float attunementPoints(Attunement a) { return attunementPoints.getOrDefault(a, 0f); }
    public int rarityRerolls() { return rarityRerolls; }
    public Armour armour() { return armour; }
    public BanditRole role() { return role; }
    public String descriptor() { return descriptor; }
    public int headband() { return headband; }
    public Hit lastHit() { return lastHit; }
    public float lastHitDamage() { return lastHitDamage; }
    public boolean isNeutral() { return attunements.stream().allMatch(Attunement::isNeutral); }
    public Room currentRoom() { return currentRoom; }
    public Room destinationRoom() { return destinationRoom; }
    public float x() { return x; }
    public float y() { return y; }
    public boolean facingRight() { return facingRight; }
    public boolean isWalking() { return walking; }
    public boolean isWaitingAtDoor() { return waitingAtDoor; }
    public boolean isMoving() { return !steps.isEmpty(); }
    public float hp() { return hp; }
    public float maxHp() { return StatRules.maxHp(this); }
    public boolean isAlive() { return alive; }
    public boolean isBandit() { return faction == Faction.BANDITS; }
    public boolean isFighting() { return fighting; }
    public boolean isBreakingDoor() { return breakingDoor; }
    public boolean isBurning() { return burnTime > 0; }
    /** Total distance walked; drawing code uses it to time the walk cycle. */
    public float distanceWalked() { return distanceWalked; }

    /** The remaining route as points (for drawing a path preview). */
    public List<Pos> remainingRoute() {
        return steps.stream().map(Step::p).toList();
    }

    // ---------------------------------------------------------------- stats

    /** A stat's value: practice + free points + bonuses. */
    public int stat(Stat s) { return Math.min(Progression.MAX_STAT, trained[s.ordinal()] + gifted[s.ordinal()]); }
    /** The part of a stat that came from practice (this is what the tier cap limits). */
    public int trained(Stat s) { return trained[s.ordinal()]; }
    public int gifted(Stat s) { return gifted[s.ordinal()]; }
    public int level() { return level; }
    public int freePoints() { return freePoints; }
    public int levelCap() { return Progression.levelCap(tier); }
    public boolean atLevelCap() { return level >= levelCap(); }
    /** 0..1 towards the next level (1 at the cap). */
    public float levelProgress() { return atLevelCap() ? 1f : levelXp / Progression.levelXpFor(level); }
    public float levelXp() { return levelXp; }
    /** 0..1 towards a stat's next practised point (1 when capped). */
    public float statProgress(Stat s) {
        return statCapped(s) ? 1f : statXp[s.ordinal()] / Progression.statXpFor(trained[s.ordinal()]);
    }
    /** True when practice can't raise this stat any further at this tier. */
    public boolean statCapped(Stat s) {
        return trained[s.ordinal()] >= levelCap() || stat(s) >= Progression.MAX_STAT;
    }
    /** Stats being practised right now. */
    public Set<Stat> training() { return training; }

    /**
     * Learning arrows, indexed by Stat.ordinal(): 0 = none, 1 = green up arrow (highest stat),
     * -1 / -2 / -3 = red down arrow on the lowest / 2nd lowest / 3rd lowest stat.
     * Only from Tier 1; Luck never gets one. Ties: the earlier stat in Stat order wins the up arrow;
     * for down arrows the stat ranked weaker in the class ranking counts as lower.
     */
    public int[] learningArrows() {
        int[] out = new int[Stat.COUNT];
        if (tier < Progression.ARROWS_FROM_TIER) return out;
        Stat top = null;
        for (Stat s : Stat.PERSONAL) if (top == null || stat(s) > stat(top)) top = s;
        out[top.ordinal()] = 1;
        List<Stat> rest = new ArrayList<>(List.of(Stat.PERSONAL));
        rest.remove(top);
        rest.sort(Comparator.comparingInt(this::stat).thenComparing(s -> -classRank(s)));
        for (int i = 0; i < Progression.ARROW_DOWN.length; i++) out[rest.get(i).ordinal()] = -(i + 1);
        return out;
    }

    public int learningArrow(Stat s) { return learningArrows()[s.ordinal()]; }

    /** Where a stat sits in the class ranking (0 = strongest). Without a ranking, later stats count as weaker. */
    int classRank(Stat s) {
        Stat[] r = crewClass.ranking;
        if (r != null) for (int i = 0; i < r.length; i++) if (r[i] == s) return i;
        return s.ordinal();
    }

    /** Learning bonus as a fraction (0.25 = +25%, -0.5 = -50%): arrow plus the class's strongest stat. */
    public float learningBonus(Stat s) {
        if (s == Stat.LUCK || tier < Progression.ARROWS_FROM_TIER) return 0f;
        int a = learningArrow(s);
        float b = a > 0 ? Progression.ARROW_UP : a < 0 ? -Progression.ARROW_DOWN[-a - 1] : 0f;
        // The class's star stat gives +2 a level only, no learning bonus (v4.1).
        return b;
    }

    public String status() {
        if (!alive) return "Dead";
        if (fighting) return "Fighting";
        if (breakingDoor) return steps.isEmpty() || steps.peekFirst().door() == null || !steps.peekFirst().door().gate
                ? "Breaking a door" : "Breaking the gate";
        if (waitingAtDoor) return "Waiting for a door";
        if (isMoving()) return "Walking to " + destinationRoom.name;
        if (burnTime > 0) return "Burning!";
        if (currentRoom.aether < Fortress.SUFFOCATE_BELOW) return "Suffocating";
        if (sabotage > 0) return "Wrecking the room";
        if (training.contains(Stat.KNOWLEDGE)) return "Repairing";
        if (training.contains(Stat.STRENGTH)) return "Sparring";
        if (destinationPad == 0 && currentRoom.station) return "On the station";
        if (training.contains(Stat.VITALITY)) return "Resting";
        return "Idle";
    }
}
