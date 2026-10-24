package fateweavers.logic;

import java.util.List;

/**
 * What each stat does, in one place. Replaces the old ClassBonuses table: classes no longer give bonuses
 * directly, they shape stats (rankings, attunements, perks), and everything reads the stats.
 *
 * ALL NUMBERS ARE PLACEHOLDERS for tuning. They're scaled for stats running 0-80
 * (a Tier 1 blob tops out at 20 in a stat from practice).
 */
public final class StatRules {
    private StatRules() {}

    // ---- Body
    public static final float BASE_HP = 100f;
    public static final float HP_PER_VITALITY = 2.5f;          // VIT 20 = +50 health (the old Heavy bonus)
    public static final float HEAL_PER_VITALITY = 0.02f;       // +2% healing speed per point
    public static final float WALK_PER_DEXTERITY = 0.01f;      // +1% walk speed per point
    public static final float DODGE_PER_LUCK = 0.001f;         // +0.1% chance to dodge a melee hit per point

    // ---- Melee and holding ground
    public static final float MELEE_BASE_DPS = 5f;
    public static final float MELEE_DPS_PER_STRENGTH = 0.25f;  // STR 20 = 10 dps (the old Heavy value)
    public static final int STRENGTH_PER_GATE_POINT = 10;      // gate strength = 1 + STR / 10
    public static final int RESOLVE_PER_DOOR_LEVEL = 10;       // +1 door protection per 10 RES in the room

    // ---- Work
    public static final float REPAIR_PER_KNOWLEDGE = 0.05f;    // +5% repair speed per point
    public static final float CHARISMA_BOOST_PER_POINT = 0.005f; // others in the room work +0.5% faster per point

    // ---- Attunement numbers live in Attunements (bonus, burn, rarity, points)
    public static final float CRIT_MULTIPLIER = 2f;
    /** Psyche XP for the attacker per point of MENTAL damage that lands. */
    public static final float PSYCHE_XP_PER_MENTAL_DAMAGE = 0.5f;

    // ---- Devices: the three stats of the person on the station
    public static final List<Scaling> FIRE_GEM = List.of(
            Scaling.percent(Stat.ARCANA, 1f, "damage"),
            Scaling.percent(Stat.DEXTERITY, 0.5f, "load speed"),
            Scaling.percent(Stat.LUCK, 0.1f, "crit chance"));
    public static final List<Scaling> LOOM = List.of(
            Scaling.percent(Stat.ARCANA, 1.5f, "weave speed"),
            Scaling.percent(Stat.KNOWLEDGE, 0.5f, "weave speed"));
    public static final List<Scaling> SHIELD = List.of(
            Scaling.perTier(Stat.PSYCHE, 1, "shield layer"),
            Scaling.percent(Stat.ARCANA, 1f, "recharge speed"));

    public static float maxHp(CrewMember c) { return BASE_HP + HP_PER_VITALITY * c.stat(Stat.VITALITY); }

    public static float meleeDps(CrewMember c) { return MELEE_BASE_DPS + MELEE_DPS_PER_STRENGTH * c.stat(Stat.STRENGTH); }

    /** How much a unit counts for holding No Man's Land. */
    public static int gateStrength(CrewMember c) { return 1 + c.stat(Stat.STRENGTH) / STRENGTH_PER_GATE_POINT; }

    public static int doorProtection(CrewMember c) { return c.stat(Stat.RESOLVE) / RESOLVE_PER_DOOR_LEVEL; }

    public static float walkMultiplier(CrewMember c) { return 1f + WALK_PER_DEXTERITY * c.stat(Stat.DEXTERITY); }

    public static float repairMultiplier(CrewMember c) { return 1f + REPAIR_PER_KNOWLEDGE * c.stat(Stat.KNOWLEDGE); }

    public static float healMultiplier(CrewMember c) { return 1f + HEAL_PER_VITALITY * c.stat(Stat.VITALITY); }

    public static float dodgeChance(CrewMember c) { return DODGE_PER_LUCK * c.stat(Stat.LUCK); }

    /** One stat's bonus for a device, read from a person (0 if nobody). */
    public static float bonus(List<Scaling> scalings, Stat stat, String effect, CrewMember who) {
        if (who == null) return 0f;
        float total = 0;
        for (Scaling s : scalings) if (s.stat() == stat && s.effect().equals(effect)) total += s.bonus(who.stat(stat));
        return total;
    }

    /** Sum of every scaling for one effect (e.g. both Loom stats boost weave speed). */
    public static float effect(List<Scaling> scalings, String effect, CrewMember who) {
        if (who == null) return 0f;
        float total = 0;
        for (Scaling s : scalings) if (s.effect().equals(effect)) total += s.bonus(who.stat(s.stat()));
        return total;
    }

}
