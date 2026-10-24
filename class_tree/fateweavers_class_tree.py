#!/usr/bin/env python3
"""
FATEWEAVERS - Master Class Tree (latest names, matches 02_Info_Packet.md)

Run:  python fateweavers_class_tree.py
Writes (next to this file):
  fateweavers_class_tree.json   - every class as {id, name, tier, path, parent_id, status}
  fateweavers_class_tree.sql    - CREATE TABLE + INSERTs (SQLite), replaces the old seed_*.sql files
  fateweavers.db                - ready-made SQLite database

Structure: T0 1 -> T1 5 -> T2 25 -> T3 100 -> T4 300 -> T5 600 (T5 only filled in for MAGIC so far).
A T4 written as ("Name", None) has no Tier 5 classes decided yet.
status: "locked" = decided by me, "check" = flagged in the info packet (section 7.1) as unconfirmed.
"""
import json, re, sqlite3
from pathlib import Path

CHECK = {  # names still unconfirmed (info packet section 7.1). Decided since: Battlemage -> Spellsword,
           # Blowgunner -> Ice Monkey, Anvil-Dropper / Furnace-Brawler / Master Metallurgist ->
           # Artisanal Bladesmith / Furnace Lord / Forge Master. Info packet 7.1 renames applied 24 Oct 2026:
           # Shadowstalker -> Highwayman, Squire's Commander -> Strategist, Clown's Joker -> Chaos God,
           # Thrower -> Judoka, Primal / The Primal Hunter swapped.
    "Pruner", "Vessel of the Nameless Void",
}

TREE = {
 "MAGIC": ("Magic User", {
  "Novice Elementalist": {
   "Fire Mage":  [("Lightning Mage", ["Lightning Master", "Lightning Elemental"]), ("Lava Mage", ["Lava Master", "Lava Elemental"]), ("Fire Master", ["Fire Paragon", "Fire Incarnation"])],
   "Ice Mage":   [("Water Mage", ["Water Master", "Water Elemental"]), ("Slowness Mage", ["Slowness Master", "Slowness Elemental"]), ("Ice Master", ["Ice Paragon", "Ice Incarnation"])],
   "Earth Mage": [("Metal Mage", ["Metal Master", "Metal Elemental"]), ("Stone Warden", ["Stone Master", "Stone Elemental"]), ("Earth Master", ["Earth Paragon", "Earth Incarnation"])],
   "Air Mage":   [("Speed Mage", ["Speed Master", "Speed Elemental"]), ("Space Mage", ["Space Master", "Space Elemental"]), ("Air Master", ["Air Paragon", "Air Incarnation"])],
  },
  "Novice Support Mage": {
   "Priest":        [("Bishop", ["Pope", "Unholy Archbishop"]), ("Inquisitor", ["Vampire Hunter", "Exorcist"]), ("Martyr", ["Saint", "Revenger"])],
   "Druid":         [("Gardener", ["Apothecary", "World Tree"]), ("Beastkin", ["Beastkin Chieftain", "Great White Wolf"]), ("Rejuvenator", ["Revitaliser", "Invigorator"])],
   "Vitality Mage": [("Growth Mage", ["Unyielding Growth", "Size Manipulator"]), ("Unkillable Joe", ["Unmoveable Object", "Indomitable Joe"]), ("Lifebinder", ["Soulbinder", "Fountain of Vitality"])],
   "Barrier Mage":  [("Bastion Ward Mage", ["The Last Fortress Mage", "Sanctuary Mage"]), ("Bubble Mage", ["Aegis Custodian", "Atmospheric Dome"]), ("Telekinesis Mage", ["Editor Privileges Enabled", "True Telekinetic"])],
  },
  "Novice Dark Mage": {
   "Necromancer":        [("Bone Crafter", ["Skeleton Lord", "Lord of Bone"]), ("Soul Reaper", ["Grim Reaper", "Soul Harvester"]), ("Untethered Soul", ["Lich", "Abomination"])],
   "Blood Mage":         [("Vampire", ["Royal Pureblood Vampire", "Sanguine Horror"]), ("Hemomancer", ["Sanguine Elemental", "Blood-bender"]), ("Warlock", ['"The Devilbound"', "Demonic Master"])],
   "Curse Mage":         [("Death Mage", ["The Forgotten One", "SoulShattered Mage"]), ("Pain Mage", ["Vivacious Torturer", '"He Who Knows Pain"']), ("Control Mage", ["Machiavelli", "Slaver"])],
   "Advanced Dark Mage": [("Shade Elemental", ["Shadow From Beyond", "Legion of Shades"]), ("Dark Master", ["Eternal Night", "Lord of Stars"]), ("Sealed Paragon", ["The Unshackled Sovereign", "The Shattered Apex"])],
  },
  "Novice Wizard": {
   "Spellblade":    [("Spellsword", ["Sword of Fire", "Sword of Ice"]), ("Rune-Blade", ["Zero Point Edge", "Cursed Blade"]), ("Spell-Eater", ["Magic Devourer", "The Anti-Magic Blade"])],
   "Arcane Wizard": [("Grandmaster Wizard", ["Archmage", "Arcane Elemental"]), ("Mana Sage", ["Well of Mana", "Mana Elemental"]), ("Ritualist", ["Grand Ritualist", "Shaman"])],
   "Spirit Mage":   [("Spirit Elementalist", ["Spirit Conduit", "Spirit Caller"]), ("Phantasm", ["Ethereal Specter", "Poltergeist"]), ('"The Spiritbound"', ["Ultimate Spirit Avatar", "Spirit-Gorged Vessel"])],
   "Esotericist":   [("Time Mage", ["The Clockmaker", "The Watcher"]), ("Transcendent", ["Descended Deity", "Peacewalker"]), ("Eldritch Scholar", ["Vessel of the Nameless Void", "Inhabitant of the Impossible"])],
  },
  "Novice Artificer": {
   "Golem Smith":          [("Forgesmith Ascendant", ["Armour Lord", "Power Exoskeleton"]), ("Automaton Lord", ["Clockwork King", "Factory Designer"]), ("Swarm-Smith", ["Grey Goo Guy", "Nanite Paragon"])],
   "Advanced Artificer":   [("Master Artificer", ["Unshackled Creator", "Leader of Creation"]), ("Gunsmith", ["Artisanal Cannoneer", "Hyperfixated Gun Nerd"]), ("Magitech Architect", ["Teleportation Gridmaster", "The Master of the Citadel"])],
   "Runesmith / Armornaut":[('"The Runebound"', ["Living Master Rune", "Monolith of Balance"]), ('"Research & Development"', ["Atom Splitter", '"Experimental Weapons Department"']), ("Scholar", ["Absolute True Genius", "Mind Shattered Savant of Ruin"])],
   "Technomancer":         [("Cyborg", ["Fully Chromed Sovereign", "Technological Terror"]), ("Watchdog", ["Overseer", "Firewall"]), ("Hacker", ["Black Hat", "White Hat"])],
  },
 }),
 "RANGED": ("Ranged User", {
  "Bowman": {
   "Longbowman":    ["Sharpshooter", "Fletcher", "Lookout"],
   "Deadeye":       ["Ambusher", "Trickshotter", "Bullseye"],
   "Crossbowman":   ["Arbalist", "Semi-Automatic Crossbowman", "Pavise Bearer"],
   "Highwayman":    ["Hitman", "Stalker", "Drifter"],
  },
  "Novice Hunter": {
   "Tamer":       ["Beast Master", "Shape Shifter", "Communicator"],
   "Trapper":     ["Sweeper", "Webweaver", '"Big Red Button Guy"'],
   "Tracker":     ["Analyst", "Tribesman", "Hiker"],
   "Survivalist": ["Stoic", "Scavenger", "The Primal Hunter"],
  },
  "Gunslinger": {
   "Pistolero":        ["Sheriff", "Desperado", '"Gungho Garry"'],
   "Soldier":          ["Commander", "Machine Gunner", "Combat Medic"],
   "Sniper":           ["Reconnaissance Sniper", "Railgunner", "Ghillie Suit Sniper"],
   '"Buckshot Barry"': ["Flamethrower Frankie", "Rocket Launcher Randy", "Buckshot Barry (with another shotgun!)"],
  },
  "Hurler": {
   "Spear Thrower": ["Trident Master", "Harpooner", "Impaler"],
   "Bomber":        ["Arsonist", "Incisionist", "Anarchist"],
   "Big Chuck":     ["Bowling Bob", "Cinder Block Cindy", "Bigger Chuck"],
   "Boomeranger":   ["Glaive Thrower", "Reeler", "The Bushranger"],
  },
  "Slingshotter": {
   "Sling Master":       ["Hunter of Giants", "Sling Savant", "Flail Lord"],
   "Blowgun Specialist": ["Tranquilizer", "Witch Doctor", "Blowgun Sniper"],
   "Netcaster":          ["Bola Master", "Goo Gunner", "Ice Monkey"],
   "Trickster":          ["Joker", "Illusionist", "Ninja"],
  },
 }),
 "LIGHT_MELEE": ("Light Melee", {
  "Thief": {
   "Goblin":   ["Goblin Chieftain", "Hobgoblin", "Goblin Shaman"],
   "Burglar":  ["Heister", "Cat Burglar", "Archaeologist"],
   "Criminal": ["Drug Dealer", "Kingpin", "Psycho"],
   "Outlaw":   ["Rebel", "Rioter", "Lone Wolf"],
  },
  "Scout": {
   "Pathfinder":     ["Beacon", "Treasure Hunter", "Cartographer"],
   "Outrider":       ["Athlete", "Messenger", "Stridewalker"],
   "Vanguard Scout": ["Spy", "Shadow", "Elite Scout"],
   "Surveyor":       ["Observer", "Seer", "Panopticon"],
  },
  "Assassin": {
   "Shadowdancer":    ["Shadowjumper", "Haunter", "Malignance"],
   "Poisoner":        ["Snake Kin", "Blighter", "Envenomer"],
   "Executioner":     ["Judge", "Disintegrator", "Oblivion"],
   "Master Assassin": ["Excommunicated Contract Killer", "Grandmaster Assassin", "Marksman"],
  },
  "Brawler": {
   "Pugilist":       ["Puncher", "Boxer", "Iron Fist"],
   "Grappler":       ["Wrestler", "Bouncer", "Judoka"],
   "Kickboxer":      ["Kangaroo", "Nut Kicker", "MMA Fighter"],
   "Martial Artist": ["Daoist", "Sensei", "Monk"],
  },
  "Duelist": {
   "Fencer":       ["Bloodletter", "Skirmisher", "Skypiercer"],
   "Blade Dancer": ["Dervish", "Blade Demon", "Song of Steel"],
   "Swashbuckler": ["Pirate Captain", "Quartermaster", '"The Damned"'],
   "Swordsman":    ["Grandmaster Swordsman", "Jedi", "Kensai"],
  },
 }),
 "HEAVY_MELEE": ("Heavy Melee", {
  "Crog": {
   "Neanderthal": ["Ape", "Dinosaur Tamer", "Primal"],
   "Ogre-Kin":    ["Ogre Brawler", "Ogre Chieftain", '"Small" Ogre'],
   "Stick Master":["ThrustLord", "SwingLord", "SwipeLord"],
   "Throgg":      ["Giant-kin", "Heracles", "Thraxx"],
  },
  "Squire": {
   "Knight":      ["Aurablade Knight", "Lord", "Prince Charming"],
   "Spearman":    ["Phalanx", "Dragoon", "Gladiator"],
   "Strategist":  ["Augur", "Tactician", "Leader"],
   "Flag Bearer": ["Well of Hope", "Warcaller", "Dread Herald"],
  },
  "Initiate Paladin": {
   "Valkyrie":        ["Storm Maiden", "Einherjar Vanguard", "Shield Maiden"],
   "Crusader":        ["Holy Knight", "Justicar", "Zealot"],
   "Templar":         ["Spellbreaker", "Hierophant", "Dawnguard"],
   '"The Oathbound"': ["Oathbreaker", "Angelic Warrior", "Demonic Warrior"],
  },
  "Hammerman": {
   "Viking":  ["Berserker", "Storm-Herald", "Rune Warrior"],
   "Dwarf":   ["Dwarven Warrior", "Dwarven Engineer", "Dwarven Blacksmith"],
   "Smith":   ["Artisanal Bladesmith", "Furnace Lord", "Forge Master"],
   "Wrecker": ["Destructor", "Crusher", "Obliterator"],
  },
  "Shieldbearer": {
   "Bulwark":    ["The Iron Gate", "Fortress Lord", "Rook"],
   "Defender":   ["The Castle Doctrinarian", "Home Alone Kid", "Knockoff Waluigi"],
   "Bastion":    ["Juggernaut", "Fortress Incarnate", "Titan-Shield"],
   "Evade Tank": ["Parry Lord", "Guru", "Matador"],
  },
 }),
 "SUPPORTER": ("Supporter", {
  "Medic": {
   "Doctor":           ["Surgeon", "Nurse", "Mad Doctor"],
   "Sanitarian":       ["Hazmat Hank", "Purificator", "Janice"],
   "Genetic Engineer": ["Mutagenist", "Cloner", "The Menagerie"],
   "Therapist":        ["Hypnotist", "Serene Presence", "Dark Mango Psychology Expert"],
  },
  "Handyman": {
   "Repairman":     ["Repair-O-Bots", "Master Repairman", "Good Ol' Dave"],
   "Builder":       ["Foreman", "Master Mason", "MacGyver"],
   "Demolitionist": ["Razer", "World Eater", "Pruner"],
   "Engineer":      ["Chief Engineer", "Overclocker", "Automaton Mechanic"],
  },
  "Novice Mystic": {
   "Oracle": ["Doomsayer", "Star-Gazer", "Prophet"],
   "Weaver": ["Thread-Spinner", "Spindle-Guard", "Tapestry-Cutter"],
   "Witch":  ["Cauldron Hag", "Hex-Weaver", "Coven Matriarch"],
   "Sage":   ["Savant", "Forbidden Scholar", '"Enlightened"'],
  },
  "Novice Alchemist": {
   "Advanced Alchemist": ["Chemist", "Elixir Specialist", "Vapour Addict"],
   "Toxicologist":       ["Miasma Lord", "Dragonkin Alchemist", '"The Goobound"'],
   "Transmuter":         ["Matter Shifter", "Philosopher's Alchemist", "Unstable Transformer"],
   "Imbuer":             ["Attuner", "Enchanter", "Awakener"],
  },
  "Jester": {
   "Clown":    ["Chaos God", "Comedian", "Twitch Streamer"],
   "Drunkard": ["Tavern Brawler", "The Drunken Fist", "Alcohol Misuse Disorder"],
   "Harlot":   ["Siren", "Succubus", "Charmer"],
   "Artist":   ["Bard", "Painter", "Actor"],
  },
 }),
}


def build_nodes():
    nodes, used = [], set()

    def add(tier, name, path, parent):
        base = f"T{tier}_" + re.sub(r"[^A-Za-z0-9]+", "_", name.replace("&", "and").replace("'", "")).strip("_").upper()
        nid, n = base, 2
        while nid in used:              # duplicate names (e.g. Joker, Commander) get a numbered id
            nid, n = f"{base}_{n}", n + 1
        used.add(nid)
        nodes.append({"id": nid, "name": name, "tier": tier, "path": path, "parent_id": parent,
                      "status": "check" if name in CHECK else "locked"})
        return nid

    root = add(0, "Unclassed Survivor", "NEUTRAL", None)
    for path, (t1_name, t2s) in TREE.items():
        t1 = add(1, t1_name, path, root)
        for t2_name, t3s in t2s.items():
            t2 = add(2, t2_name, path, t1)
            for t3_name, t4s in t3s.items():
                t3 = add(3, t3_name, path, t2)
                for t4 in t4s:
                    t4_name, t5s = t4 if isinstance(t4, tuple) else (t4, None)
                    t4_id = add(4, t4_name, path, t3)
                    for t5_name in (t5s or []):
                        add(5, t5_name, path, t4_id)
    return nodes


def verify(nodes):
    from collections import Counter
    kids = Counter(n["parent_id"] for n in nodes)
    expected = {0: 5, 1: 5, 2: 4, 3: 3}
    for n in nodes:
        want = expected.get(n["tier"])
        if want is not None and kids[n["id"]] != want:
            raise SystemExit(f"Branching error: {n['name']} (T{n['tier']}) has {kids[n['id']]} children, expected {want}")
        if n["tier"] == 4 and kids[n["id"]] not in (0, 2):
            raise SystemExit(f"Branching error: {n['name']} (T4) has {kids[n['id']]} T5s, expected 0 or 2")
    return Counter(n["tier"] for n in nodes)


def write_outputs(nodes, here: Path):
    (here / "fateweavers_class_tree.json").write_text(json.dumps(nodes, indent=1, ensure_ascii=False), encoding="utf-8")

    def q(v):
        return "NULL" if v is None else ("'" + str(v).replace("'", "''") + "'") if isinstance(v, str) else str(v)

    sql = ["-- FATEWEAVERS class tree (generated by fateweavers_class_tree.py). Replaces the old seed_*.sql files.",
           "CREATE TABLE IF NOT EXISTS character_classes (",
           "  id TEXT PRIMARY KEY, name TEXT NOT NULL, tier INTEGER NOT NULL CHECK (tier BETWEEN 0 AND 5),",
           "  path TEXT NOT NULL, parent_id TEXT REFERENCES character_classes(id), status TEXT NOT NULL);",
           "CREATE INDEX IF NOT EXISTS idx_classes_parent ON character_classes(parent_id, tier);",
           "DELETE FROM character_classes;"]
    for n in nodes:
        sql.append("INSERT INTO character_classes VALUES (" + ", ".join(q(n[k]) for k in ("id", "name", "tier", "path", "parent_id", "status")) + ");")
    (here / "fateweavers_class_tree.sql").write_text("\n".join(sql) + "\n", encoding="utf-8")

    db = here / "fateweavers.db"
    if db.exists():
        db.unlink()
    with sqlite3.connect(db) as conn:
        conn.executescript("\n".join(sql))


if __name__ == "__main__":
    nodes = build_nodes()
    counts = verify(nodes)
    write_outputs(nodes, Path(__file__).resolve().parent)
    print("Tier counts:", dict(sorted(counts.items())), "| total:", len(nodes))
    print("Tier 5 still to name: Ranged, Light Melee, Heavy Melee, Supporter (", 600 - counts.get(5, 0), "classes )")
