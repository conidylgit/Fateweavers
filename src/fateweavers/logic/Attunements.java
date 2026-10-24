package fateweavers.logic;

import java.util.*;

/**
 * Attunement rules: which attunements each class has, how a blob matches a weapon, and rarity rolls.
 *
 * Which class gets what:
 *  - Unclassed and Tier 1 classes are always NEUTRAL.
 *  - Deeper classes: OVERRIDES first (by class id or exact name), otherwise words in the class name
 *    (KEYWORDS, in priority order), otherwise inherited from the parent. At most MAX_PER_CLASS (2).
 *    PLACEHOLDER: the keyword guesses are a starting point; put real choices in OVERRIDES.
 *
 * Matching a weapon (or a hit the blob deals):
 *  - The blob's parts = its attunements, with duos split into their two parts (LAVA -> FIRE, EARTH).
 *  - The weapon's parts = its attunements (duos split too) + the craft attunement of its damage type
 *    (a slashing weapon counts as SLASHING).
 *  - All the blob's parts found on the weapon = full effect. Some = x2/3. None = nothing.
 *  - Balance (every Elementalist) matches any elemental weapon (Fire, Ice, Water, Earth, Stone, Air, and duos
 *    through their parts) at x1/2, unless something else of the blob matches properly.
 *  - Sub-attunements match only themselves (a Water blob gets nothing from an Ice weapon).
 *  - Effect power = that fraction x the rarity multiplier of the blob's matching attunement(s).
 */
public final class Attunements {
    private Attunements() {}

    public static final int MAX_PER_CLASS = 2;

    // ---- Effects (Minor level; the P7 ladder adds more). Multiplied by match power.
    public static final float DAMAGE_BONUS = 0.10f;      // +10% damage with a matching weapon (x rarity)
    public static final float BURN_DPS = 2f;             // Fire: burn 2 dps (x rarity: Legendary 6)
    public static final float BURN_SECONDS = 4f;
    public static final float PARTIAL_MATCH = 2f / 3f;   // duo blob with a weapon that has only one of its parts
    public static final float BALANCE_MATCH = 0.5f;      // Balance with any elemental weapon
    public static final float RESIST_MULTIPLIER = 0.5f;
    public static final float WEAK_MULTIPLIER = 1.5f;

    // ---- Attunement points (a plain counter for now; levels come in P7). PLACEHOLDER rates.
    public static final float POINTS_PER_USE = 1f;               // each shot or blow with a matching weapon
    public static final float PASSIVE_POINTS_PER_SECOND = 1f / 60f; // near something of the same attunement
    public static final float SWITCH_KEEP = 0.75f;               // points carried over when switching attunement

    // ---- Rarity roll odds by class tier (weights; anything above the tier's cap can't roll). PLACEHOLDER.
    private static final float[] BASE_ODDS = {40f, 25f, 18f, 10f, 5f, 2f};
    private static final float[] ODDS_PER_TIER = {-3f, 0f, 1f, 1f, 0.6f, 0.4f};   // slightly better each tier

    // ------------------------------------------------------------------ class -> attunements

    private static final Map<String, List<Attunement>> OVERRIDES = new HashMap<>();
    private static void o(String idOrName, Attunement... a) { OVERRIDES.put(idOrName.toLowerCase(Locale.ROOT), List.of(a)); }
    static {
        o("T3_ESOTERICIST", Attunement.KNOWLEDGE, Attunement.ESOTERIC);
        o("T4_SCHOLAR", Attunement.KNOWLEDGE);
        // From claude/class_connections.md section 5 (name words miss these)
        o("Infernal Fletcher", Attunement.FIRE, Attunement.ARCHERY);
        o("Arsonist", Attunement.FIRE, Attunement.EXPLOSIVES);
        o("Dragon Master", Attunement.FIRE, Attunement.BEAST);
        o("Dragonkin Alchemist", Attunement.FIRE, Attunement.BEAST);
        o("Furnace Lord", Attunement.FIRE, Attunement.METAL);
        o("Ice Monkey", Attunement.ICE, Attunement.BEAST);
        o("Tranquilizer", Attunement.SLOWNESS, Attunement.PIERCING);
        o("Thunder Fletcher", Attunement.LIGHTNING, Attunement.ARCHERY);
        o("Railgunner", Attunement.LIGHTNING, Attunement.FIREARMS);
        o("Overclocker", Attunement.LIGHTNING, Attunement.MACHINE);
        o("Iron Fist", Attunement.METAL, Attunement.KI);
        o("The Iron Gate", Attunement.METAL, Attunement.BASTION);
        o("Master Mason", Attunement.STONE, Attunement.BASTION);
        o("Teleportation Gridmaster", Attunement.SPACE);
        o("Matter Shifter", Attunement.SPACE);
        o("Shadowjumper", Attunement.SHADOW, Attunement.SPACE);
        o("Purificator", Attunement.RESTORATION, Attunement.HOLY);
        o("Berserker", Attunement.NORSE, Attunement.BLOOD);
        o("Einherjar Vanguard", Attunement.NORSE, Attunement.DEATH);
        o("Executioner", Attunement.CLEAVING, Attunement.DEATH);
        o("Blowgun Specialist", Attunement.POISON, Attunement.PIERCING);
        o("Drug Dealer", Attunement.OUTLAW, Attunement.POISON);
        o("Twitch Streamer", Attunement.PERFORMER, Attunement.CHAOS);
        o("Joker", Attunement.PERFORMER, Attunement.CHAOS);
        o("Trickshotter", Attunement.FIREARMS, Attunement.CHAOS);
        o("Spellsword", Attunement.ARCANE, Attunement.SLASHING);
        o("Spellblade", Attunement.ARCANE, Attunement.SLASHING);
        o("Bowling Bob", Attunement.BLUDGEONING, Attunement.THROWN);
        o("\"Big Red Button Guy\"", Attunement.EXPLOSIVES);
        o("Swashbuckler", Attunement.BLADEDANCE, Attunement.PIRATE);
        o("Harpooner", Attunement.IMPALING, Attunement.PIRATE);
        o("Reeler", Attunement.THROWN, Attunement.PIRATE);
        o("Good Ol' Dave");                                   // not Goo: inherit from the parent
    }

    /**
     * Name words -> attunement, in priority order (the first two found win). Words match at the start of a
     * word ("box" matches "Boxer"); "=ice" must be the whole word ("Ice Mage", not "Novice"); "!word" blocks it.
     */
    private static final String[][] KEYWORDS = {
            // Balance first: every Elementalist class
            {"BALANCE", "elementalist"},
            // elements
            {"LAVA", "lava", "magma", "molten"},
            {"LIGHTNING", "lightning", "storm", "thunder", "volt"},
            {"SLOWNESS", "slow"},
            {"METAL", "metal", "iron", "steel", "forge", "smith", "golem smith"},
            {"SPEED", "speed", "swift", "haste", "outrider"},
            {"SPACE", "space", "teleport", "gravity", "dimension"},
            {"FIRE", "fire", "flame", "pyro", "infern", "furnace", "ember"},
            {"ICE", "=ice", "frost", "glacier", "snow"},
            {"WATER", "water", "tide", "aqua"},
            {"STONE", "stone", "boulder", "granite"},
            {"EARTH", "earth", "dwarf", "mountain"},
            {"AIR", "=air", "wind", "gale", "=sky"},
            {"VOID", "void", "null", "nameless", "abyss", "oblivion", "anti-magic", "spell-eater", "world eater", "beyond"},
            // dark and light
            {"DARK", "unholy", "dark", "curse", "hex"},
            {"SHADOW", "shadow", "demon", "warlock", "stalker", "malign", "succubus", "oathbreaker", "dread"},
            {"SOUL", "soul"},
            {"DEATH", "death", "necro", "lich", "skeleton", "reaper", "undead", "boogeyman", "grave", "grim"},
            {"NIGHT", "night", "moon", "lunar", "dusk", "nocturn", "midnight", "twilight"},
            {"HOLY", "!unholy", "holy", "paladin", "priest", "saint", "exorcist", "bishop", "martyr", "crusader",
                    "templar", "hierophant", "angel"},
            {"LIGHT", "!lightning", "light", "sun", "solar", "radian", "dawn", "lumin", "prism", "beacon", "enlighten"},
            {"RESTORATION", "heal", "medic", "doctor", "restor", "life", "cleric", "mend", "surgeon", "nurse",
                    "vitality", "renew", "regen", "fountain", "purif", "sanitar"},
            {"BLOOD", "blood", "vampire"},
            {"POISON", "poison", "blight", "toxic", "venom", "envenom", "blowgun", "miasma", "vapour"},
            // magic and mind
            {"ARCANE", "arcane", "rune", "aura", "spellbreak", "imbuer", "enchant", "mana"},
            {"KNOWLEDGE", "scholar", "sage", "archaeolog", "librar"},
            {"ESOTERIC", "esoteric", "eldritch", "forbidden", "occult"},
            {"MIND", "mind", "telekin", "control", "hypno", "therap", "charm", "psion", "machiavelli", "psych"},
            {"SPIRIT", "spirit", "phantasm", "shaman", "haunt", "poltergeist", "ghost", "witch doctor"},
            {"TIME", "time", "clock", "chrono"},
            {"FATE", "fate", "oracle", "seer", "augur", "weaver", "star", "prophet", "doom", "spindle", "thread",
                    "tapestry", "watcher", "overseer", "lookout", "overwatch", "panopticon"},
            {"LUCK", "luck", "gambl", "dice"},
            {"CHAOS", "chaos", "anarch", "unshackled", "drunk"},
            // weapon crafts
            {"BLADEDANCE", "blade dancer", "swashbuck"},
            {"CLEAVING", "axe", "cleav", "scythe", "guillotine"},
            {"SLASHING", "sword", "blade", "saber", "sabre", "cutlass", "katana", "slash", "scimitar"},
            {"STABBING", "rapier", "fenc", "stab", "dagger", "assassin", "duel", "stiletto"},
            {"IMPALING", "spear", "lance", "pike", "harpoon", "trident", "javelin", "impal"},
            {"PIERCING", "pierc", "needle", "dart"},
            {"PUGILISM", "brawl", "box", "kickbox", "fist", "grappl", "punch", "bouncer"},
            {"CRUSHING", "crush", "juggernaut", "hammer", "flail", "maul", "mace"},
            {"BLUDGEONING", "club", "cudgel", "bludgeon", "cinder block"},
            {"DESTRUCTION", "wreck", "destr", "ruin", "rampage", "slaughter", "shatter", "raze", "annihil", "apex"},
            {"ARCHERY", "bow", "longbow", "crossbow", "fletch", "archer", "arrow", "bullseye", "marksman"},
            {"EXPLOSIVES", "bomb", "demoli", "rocket", "dynamite", "grenad", "kaboom", "atom", "explos"},
            {"FIREARMS", "gun", "gunner", "rifle", "pistol", "sniper", "musket", "cannon", "buckshot", "flintlock"},
            {"THROWN", "boomerang", "thrower", "sling", "throw", "juggl", "chakram", "reeler", "glaive", "hurler"},
            // families
            {"PIRATE", "pirate", "quartermaster", "the damned", "corsair", "buccaneer", "dread herald"},
            {"OUTLAW", "outlaw", "highwayman", "desperado", "criminal", "kingpin", "bandit", "bushranger", "thief",
                    "rogue", "smuggl", "hitman", "contract killer"},
            {"LAW", "sheriff", "judge", "justicar", "inquisitor", "marshal", "dawnguard"},
            {"NORSE", "valkyrie", "viking", "einherjar", "shield maiden", "rune warrior", "storm-herald"},
            {"KI", "martial", "monk", "daoist", "sensei", "guru", "drunken fist", "kung", "=chi", "=ki"},
            {"PERFORMER", "jester", "clown", "joker", "trickster", "illusion", "comedian", "bard", "actor", "siren",
                    "streamer", "circus"},
            // nature, machines, body, fortress
            {"BEAST", "beast", "wolf", "tamer", "hawk", "snake", "kangaroo", "=ape", "dinosaur", "dragon", "druid",
                    "menagerie", "monkey", "neanderthal", "primal", "ogre"},
            {"MACHINE", "machine", "artificer", "techno", "engineer", "automaton", "golem", "cyborg", "=bot", "bots",
                    "mechanic", "magitech", "robot", "exoskeleton"},
            {"GOO", "!good", "goo"},
            {"BASTION", "bastion", "fortress", "citadel", "bulwark", "pavise", "barrier", "ward", "sanctuary",
                    "mason", "castle", "gate"},
            {"ARMOUR", "armo", "plate", "mail", "shield", "knight", "tank", "guard"},
            {"HEAVY", "heavy", "giant", "titan", "colossus", "=big", "bigger", "throgg", "unmoveable", "immovable", "crog", "chuck"},
    };

    private static final List<Attunement> NEUTRAL = List.of(Attunement.NEUTRAL);

    public static List<Attunement> of(ClassNode n) {
        if (n == null || n.tier <= 1) return NEUTRAL;
        List<Attunement> own = OVERRIDES.get(n.id.toLowerCase(Locale.ROOT));
        if (own == null && n.name != null) own = OVERRIDES.get(n.name.toLowerCase(Locale.ROOT));
        if (own == null) own = fromName(n.name);
        if (!own.isEmpty()) return own;
        return of(n.parent);
    }

    static List<Attunement> fromName(String name) {
        if (name == null) return List.of();
        String lower = " " + name.toLowerCase(Locale.ROOT).replaceAll("[^a-z\\- ]+", " ").replace('-', ' ') + " ";
        List<Attunement> found = new ArrayList<>();
        for (String[] k : KEYWORDS) {
            if (found.size() >= MAX_PER_CLASS) break;
            boolean hit = false, blocked = false;
            for (int i = 1; i < k.length; i++) {
                String w = k[i];
                if (w.startsWith("!")) { if (startsWord(lower, w.substring(1))) blocked = true; continue; }
                if (startsWord(lower, w)) hit = true;
            }
            if (hit && !blocked) found.add(Attunement.valueOf(k[0]));
        }
        return found;
    }

    /** "=word" must be the whole word; otherwise the word just has to start there. */
    private static boolean startsWord(String lower, String word) {
        if (word.startsWith("=")) return lower.contains(" " + word.substring(1) + " ");
        return lower.contains(" " + word.replace('-', ' '));
    }

    /** "Fire", "Arcane + Slashing" */
    public static String describe(List<Attunement> list) {
        return String.join(" + ", list.stream().map(a -> a.label).toList());
    }

    // ------------------------------------------------------------------ matching

    /** How well a blob matches a weapon or hit: fraction 0 / 2/3 / 1, and power = fraction x rarity. */
    public record Match(float fraction, float power, Set<Attunement> shared, List<Attunement> matched) {
        public boolean any() { return fraction > 0; }
        public boolean has(Attunement part) { return shared.contains(part); }
    }

    public static final Match NONE = new Match(0f, 0f, Set.of(), List.of());

    /** All parts something counts as: duos split, plus the craft attunement of its damage type. */
    public static Set<Attunement> partsOf(List<Attunement> attunements, DamageType type) {
        Set<Attunement> out = EnumSet.noneOf(Attunement.class);
        if (attunements != null) for (Attunement a : attunements) if (!a.isNeutral()) out.addAll(a.parts());
        Attunement craft = Attunement.craftOf(type);
        if (craft != null) out.add(craft);
        return out;
    }

    public static Match match(CrewMember c, List<Attunement> weapon, DamageType type) {
        if (c == null) return NONE;
        Set<Attunement> blob = partsOf(c.attunements(), null);
        if (blob.isEmpty()) return NONE;
        Set<Attunement> w = partsOf(weapon, type);
        Set<Attunement> shared = EnumSet.noneOf(Attunement.class);
        for (Attunement p : blob) if (w.contains(p)) shared.add(p);
        // Balance (the Elementalists) half-matches any elemental weapon, and shares its elements (so Fire still burns)
        boolean balanceHalf = false;
        if (blob.contains(Attunement.BALANCE) && !w.contains(Attunement.BALANCE)) {
            for (Attunement p : w) if (Attunement.ELEMENTAL.contains(p)) { shared.add(p); balanceHalf = true; }
            if (balanceHalf) shared.add(Attunement.BALANCE);
        }
        if (shared.isEmpty()) return NONE;
        int full = 0;
        for (Attunement p : blob) if (w.contains(p)) full++;
        float fraction = full == blob.size() ? 1f : full > 0 ? PARTIAL_MATCH : BALANCE_MATCH;
        List<Attunement> matched = new ArrayList<>();
        float rarity = 0f;
        for (Attunement a : c.attunements()) {
            if (a.isNeutral()) continue;
            boolean m = false;
            for (Attunement p : a.parts()) if (shared.contains(p)) m = true;
            if (m) { matched.add(a); rarity += c.rarityOf(a).multiplier(); }
        }
        rarity /= Math.max(1, matched.size());
        return new Match(fraction, fraction * rarity, shared, matched);
    }

    /** Resist / weakness multiplier for a hit carrying these attunements on this unit. */
    public static float taken(CrewMember target, List<Attunement> hit) {
        float m = 1f;
        if (hit == null) return m;
        for (Attunement h : hit) {
            for (Attunement a : target.attunements()) {
                if (a.resists == h) m *= RESIST_MULTIPLIER;
                if (a.weakTo == h) m *= WEAK_MULTIPLIER;
            }
        }
        return m;
    }

    // ------------------------------------------------------------------ rarity

    /** Chance (0..1) of each rarity for a class of this tier. */
    public static float[] odds(int tier) {
        Rarity cap = Rarity.capFor(tier);
        float[] w = new float[Rarity.values().length];
        float sum = 0;
        for (int i = 0; i <= cap.ordinal(); i++) {
            w[i] = Math.max(0f, BASE_ODDS[i] + ODDS_PER_TIER[i] * Math.max(0, tier));
            sum += w[i];
        }
        for (int i = 0; i < w.length; i++) w[i] = sum <= 0 ? (i == 0 ? 1f : 0f) : w[i] / sum;
        return w;
    }

    public static Rarity rollRarity(int tier, Random rng) {
        float[] p = odds(tier);
        float r = rng.nextFloat(), acc = 0f;
        for (int i = 0; i < p.length; i++) {
            acc += p[i];
            if (r < acc && p[i] > 0) return Rarity.values()[i];
        }
        return Rarity.capFor(tier);
    }
}
