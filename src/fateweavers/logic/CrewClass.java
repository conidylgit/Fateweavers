package fateweavers.logic;

import static fateweavers.logic.Stat.*;

/**
 * The broad class family a crew member belongs to: Unclassed or one of the five paths.
 * Their exact class is a class-tree id (CrewMember.classId); this family decides their stat archetype
 * and their blob shape. Deeper classes keep their path's family (and its archetype) until they get their own.
 *
 * Stat archetype: the eight personal stats ranked strongest to weakest (Luck is shared, so it's left out).
 *  - Rank 1 (strongest): +2 every level and +25% learning speed.
 *  - The ranking also breaks ties for the red learning arrows: the stat ranked weaker counts as lower.
 *  - Rank 2: +1 every level.
 * Unclassed has no ranking: no level bonus and no learning bonus from class.
 * PLACEHOLDER rankings: change the order here.
 */
public enum CrewClass {
    UNCLASSED("Unclassed", "T0_UNCLASSED_SURVIVOR", ClassPath.NEUTRAL,
            "A plain blob with no specialisation yet. Every task it does trains its stats.", null),
    MAGIC("Magic User", "T1_MAGIC_USER", ClassPath.MAGIC,
            "Arcane-minded: grows Arcana fastest.",
            new Stat[]{ARCANA, PSYCHE, KNOWLEDGE, RESOLVE, CHARISMA, VITALITY, DEXTERITY, STRENGTH}),
    RANGED("Ranged User", "T1_RANGED_USER", ClassPath.RANGED,
            "Sharp-eyed: grows Dexterity fastest.",
            new Stat[]{DEXTERITY, STRENGTH, RESOLVE, VITALITY, KNOWLEDGE, CHARISMA, PSYCHE, ARCANA}),
    LIGHT_MELEE("Light Melee", "T1_LIGHT_MELEE", ClassPath.LIGHT_MELEE,
            "Quick on its feet: grows Dexterity fastest.",
            new Stat[]{DEXTERITY, STRENGTH, RESOLVE, CHARISMA, VITALITY, PSYCHE, KNOWLEDGE, ARCANA}),
    HEAVY_MELEE("Heavy Melee", "T1_HEAVY_MELEE", ClassPath.HEAVY_MELEE,
            "Built to hold doorways: grows Strength fastest.",
            new Stat[]{STRENGTH, VITALITY, RESOLVE, CHARISMA, DEXTERITY, KNOWLEDGE, PSYCHE, ARCANA}),
    SUPPORTER("Supporter", "T1_SUPPORTER", ClassPath.SUPPORTER,
            "Clever hands: grows Knowledge fastest.",
            new Stat[]{KNOWLEDGE, CHARISMA, PSYCHE, VITALITY, RESOLVE, DEXTERITY, STRENGTH, ARCANA});

    public final String title;
    public final String treeId;      // the matching class in the class tree
    public final ClassPath path;
    public final String description;
    /** Strongest to weakest (8 stats, no Luck), or null for Unclassed. */
    public final Stat[] ranking;

    CrewClass(String title, String treeId, ClassPath path, String description, Stat[] ranking) {
        this.title = title;
        this.treeId = treeId;
        this.path = path;
        this.description = description;
        this.ranking = ranking;
    }

    public Stat strongest() { return ranking == null ? null : ranking[0]; }
    public Stat secondStrongest() { return ranking == null ? null : ranking[1]; }

    public static CrewClass forPath(ClassPath p) {
        for (CrewClass c : values()) if (c.path == p) return c;
        return UNCLASSED;
    }
}
