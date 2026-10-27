# Fateweavers — Info Packet

*Consolidated from the full Gemini planning history, then cleaned up with Claude on 23 Oct 2026 (all Section 7 conflicts and duplicates resolved). This is the current source of truth.*

---

## 1. The pitch

**Title: Fateweavers.** (Older Gemini/Argon code calls it "Bastion of Fate" and uses the `com.bastionoffate` package name. That was a working title and should be renamed when the code is rebuilt.)

A fantasy reimagining of **FTL: Faster Than Light**.

| FTL | Fateweavers |
|---|---|
| Spaceship & subsystems | **The Bastion**, a fortress of chambers (rooms) |
| Crew manning stations | Crew with **D&D-style classes** who man rooms, repair, fight boarders |
| FTL engine / jump | **The Fate Loom** charges over time. When full you can **sleep** to skip to the next day and the next node |
| Sector map & beacons | **The Loom-Web**: nodes linked by strings of fate. The string type decides the encounter |
| Rebel fleet chasing you | **The Unspoolers** consume nodes behind you |
| Sector boss → next sector | Boss node at the end of each tapestry, then escape onto a new tapestry |
| Final boss | **The Blind Weaver** on **The One True Fate** |

---

## 2. Lore

- **The Great Tapestry.** Reality is a huge, self-repairing cosmic fabric of fate-strings. By manipulating the strings to survive, your fortress causes structural **fraying**.
- **The Unspoolers / The Bleed.** The pursuers aren't an army. They're weeping, faceless weavers made of blinding white thread and jagged bronze needles. Any node they touch becomes **Necrotic/Frayed** and ceases to have ever existed, choking the map behind you. It's a race against a reality-collapse you accidentally started.
- **The One True Fate / Loom-Prime.** A timeline with no branches, untouchable. It's home to the cause of the infection.
- **The Blind Weaver (a.k.a. The Dead Architect).** The original entity that pinned all reality to one predetermined, unbending timeline.
- **The twist:** the "infection" isn't a corruption of fate. **Free will is the infection.** The One True Fate is a sterile, stagnant state where every choice was made eons ago. You woke a machine built to swallow unexpected variables. Beating it keeps reality chaotic, messy and free.
- **Blind Weaver boss phases** (working design proposed by Gemini, not yet revised by me): Phase 1 *Stagnation Pulses*; Phase 2 *Bronze Needle Swarm* (Unspooler boarders flood core rooms); Phase 3 *Predetermination Waves* (tries to revert a crew member's class back to Tier 0 unless countered by a fate-anchoring class).
- Sector boss designs: **not yet designed**.

---

## 3. Core mechanics (as planned)

**Fate Loom (replaces the engine)**
- Charges slowly over time while powered and manned. When full it unlocks **Sleep**, which skips to the next day and moves to the next node.
- Upgrades shorten the charge time.
- Random events can happen while it charges.

**Loom-Web map**
- Nodes connected by typed strings. Current string types: **Chaos** (enemy encounter), **Bounty** (treasure), **Shrine**, **Siege** (Mystery was also floated early on).
- The Unspoolers advance and fray the nodes behind you each day.

**Crew and classes**
- You start with **4 neutral, unclassed survivors**.
- Successful actions earn **XP → Class Points**, which you spend to promote up the tree.
- Promotion happens in the **Chrysalis Ward**, which merges the infirmary with the class station: it heals crew and is where they change class. **The Ward's upgrade level caps the highest tier you can unlock** (a Level 4 Ward unlocks Tier 5).
- Classes give room and operating bonuses, combat roles and FTL-style **blue options** in events.

**Rooms**
- **Core starting rooms (latest decision):** **Weapons Tower**, **Chrysalis Ward**, **The Fateweaver Loom**.
- **All other rooms are unlocked through classes** (e.g. an Alchemist Lab from the Alchemist line). Rooms mentioned so far: Mage Spire (barrier/shield wards, the FTL shields equivalent), Alchemist Lab, Artificer Workshop, Barracks, Granary, Gatehouse. These can wait.
- Hazards: fire, breaches, boarders. Doors can be opened, held or locked, and Heavy Melee classes can **body-block doorways**.

**The map, the camp and combat (built in Module 1, 24 Oct 2026; numbers are placeholders)**
- **Layout, left to right:** your fortress, your **Entrance Hall**, **No Man's Land**, the **Camp Entrance**, and the **Bandit Camp**.
- **Entrance Hall:** a 2 x 2 room off the East Room, your front door onto No Man's Land. The camp has a matching Camp Entrance.
- **No Man's Land:** two 2 x 4 halves with a **gate** between. Crew can walk out, hold the gate, fight and raid. Aether only exists inside your fortress.
- **The gate:** each blob standing in No Man's Land adds gate strength (1 + Strength / 10). The side with the higher total controls the gate; a tie means nobody does. Only the controller can open or close it, and the other side can break it (the gate has 3x a door's health).
- **Bandit Camp:** a green column of three 2 x 2 rooms: **Bandit Shield** (an orb on a pedestal), **Bandit Quarters** and **Archer Tower** (a ballista). It holds three **Level 1 Bandit-Wannabes**, who have no class until level 5 and a job each: shield, gunner, guard.
- **Ballista:** 10 bolts, one every 7 s, and it **ignores shields**. When it runs out of bolts (or nobody is free to man it for 3 s) the bandits **board** your fortress: they break doors, chase crew, and knock out a room bar every 5 s in rooms nobody defends.
- **Camp shield:** 1 layer, which blocks one Fire Gem shot. A Fire Gem hit on a camp room takes 1 room bar and does 5x damage to every bandit inside.
- **Camp cleared:** when every bandit dies a fresh camp moves in after 8 s, so the camp works as a repeatable test bench.
- **Combat:** FTL-style auto-fight. Hostile blobs in the same room trade a hit each second. Damage depends on Strength, melee is bludgeoning, and armour and attunement adjust it. A closed door only opens for its own side, and enemies stuck at one break it down.
- Full details: `README.md` in the game folder and the project doc `claude/stat_system.md`.

**Weapons and economy**
- Fortress weapons replace FTL ship weapons (e.g. the **Magical Fire Orb Turret**).
- Weapons can always be **bought from the Travelling Merchant**. The Runesmith / Gunsmith (artificer) route lets you **craft** them.
- Resources so far: Mana/Scrap, Stone/Masonry, Provisions (an early prototype collapsed these into one "ManaScrap" currency).

---

## 4. Tech and development status

> **Current build status (25 Oct 2026):** Module 1 is the Java Swing sandbox in this folder (`src/`, run with `run.bat`). For what the game does today read `README.md`; for the latest changes, placeholder numbers and open questions read `03_Latest_Handoff.md`. The bullets below are the original 23 Oct plan and are kept for history.

- **Stack:** Java 17+. The long-term target is **LibGDX** (chosen over Python, which felt too small-scale, and over C++/custom engine). Current runnable prototypes are **single-file Java Swing** programs.
- **My setup:** Windows; the prototype is at `C:\Users\Coni\Downloads\FateWeavers\Fateweavers.java` and runs with `java Fateweavers.java` (Java had to be installed/added to PATH).
- **Art direction:** clean, readable, FTL-style "blueprint/schematic" UI with procedural graphics. Characters are simple pixel-outlined sprites (faceless two-tone head, tunic, swinging arms, boots, based on a reference sprite I generated). I want smooth animation polish like in *Furious Fowl*.
- **Latest direction: build modularly.** For now keep the class system to **the unclassed + 5 basic classes**, and don't code encounters or the Weave of Fate yet.

**Module roadmap**
1. **Living Bastion Sandbox** *(current)*: a fortress "house" you can move around in, smooth walking animation, animated doors, clickable rooms **and** clickable people (each person separately clickable even when sharing a room; rooms have 4 sub-slots), unclassed + 5 basic class outfits.
2. Room subsystems, power grid, fire and breach hazards.
3. Boarders and doorway combat (body-blocking, damage numbers, XP).
4. Siege artillery, turrets, Mage Spire barriers.
5. Fate Loom and Loom-Web map (charge/sleep, strings, Unspooler creep).
6. Full class tree database hookup (Tiers 2–5) and bosses.

**What exists from the Gemini/Argon sessions** (useful for reference, but don't trust it blindly):
- A big LibGDX backend draft (`com.bastionoffate.*` packages: core, loom, fortress, combat, db, ui, events, boss), a JUnit integration test, a Python notebook/DB builder (`bastion_notebook_db.py`) and SQLite seed files (`seed_locked_classes.sql`, `seed_remaining_143_classes.sql`, `seed_tier5_magic_locked.sql`, `seed_tier5_artificer_locked.sql`).
- An all-in-one Swing prototype, followed by the **Module 1** Swing sandbox (`Fateweavers.java`).
- ⚠️ **The seed files and prototypes contain outdated class names.** In particular `seed_remaining_143_classes.sql` holds Argon's first drafts for Brawler, Duelist, Heavy Melee and Supporter (e.g. "Knuckle-Duster Danny", "Giga-Crog", "Duct-Tape Dave") that I later **replaced**. Starting crew names also differ between files (Alden/Bryn/Corin/Dara vs Aldric/Brenna/Cedric/Daria). None of these are decided. **The class tree below is correct; the seeds need regenerating from it.**
- I used Google **Antigravity** as a possible workspace and image prompts for a Path-of-Exile-style class tree UI.

---

## 5. Class tree (latest versions)

**Structure:** T0 (1) → T1 (5) → T2 (25) → T3 (100) → T4 (300) → T5 (600) = 1,031 classes.
**Reading this list:** `T3 Class → T4 children` and, where done, each T4 is followed by its two T5s in brackets.
**Status:** T0–T4 fully named. **T5 is done for the Magic path only.** Ranged, Light Melee, Heavy Melee and Supporter T5s are still to do.

**T0:** Unclassed Survivor (neutral)
**T1:** Magic User · Ranged User · Light Melee · Heavy Melee · Supporter (bonus to operating rooms and machinery, and to repairs)

### 5.1 MAGIC

**Novice Elementalist**
- **Fire Mage** → Lightning Mage *(Lightning Master / Lightning Elemental)* · Lava Mage *(Lava Master / Lava Elemental)* · Fire Master *(Fire Paragon / Fire Incarnation)*
- **Ice Mage** → Water Mage *(Water Master / Water Elemental)* · Slowness Mage *(Slowness Master / Slowness Elemental)* · Ice Master *(Ice Paragon / Ice Incarnation)*
- **Earth Mage** → Metal Mage *(Metal Master / Metal Elemental)* · Stone Warden *(Stone Master / Stone Elemental)* · Earth Master *(tectonic shaper; Earth Paragon / Earth Incarnation)*
- **Air Mage** → Speed Mage *(Speed Master / Speed Elemental)* · Space Mage *(Space Master / Space Elemental)* · Air Master *(Air Paragon / Air Incarnation)*
- *T5 naming rule for this branch:* sub-elements → **[Element] Master / [Element] Elemental**. The base element's Master → **[Element] Paragon / [Element] Incarnation**. Never change the element word.

**Novice Support Mage** *(renamed from Novice Healer)*
- **Priest** → Bishop *(Pope / Unholy Archbishop)* · Inquisitor *(Vampire Hunter / Exorcist)* · Martyr *(Saint / Revenger)*
- **Druid** → Gardener *(Apothecary / World Tree)* · Beastkin *(Beastkin Chieftain / Great White Wolf)* · Rejuvenator *(Revitaliser / Invigorator)*
- **Vitality Mage** → Growth Mage *(Unyielding Growth / Size Manipulator)* · Unkillable Joe *(Unmoveable Object / Indomitable Joe)* · Lifebinder *(Soulbinder / Fountain of Vitality)*
- **Barrier Mage** → Bastion Ward Mage *(The Last Fortress Mage / Sanctuary Mage)* · Bubble Mage *(Aegis Custodian: guaranteed single-target protection / Atmospheric Dome: weaker whole-team protection)* · Telekinesis Mage *(Editor Privileges Enabled / True Telekinetic: psionic, telepathy, mental attacks, Jean Grey style)*

**Novice Dark Mage**
- **Necromancer** → Bone Crafter *(Skeleton Lord / Lord of Bone)* · Soul Reaper *(Grim Reaper / Soul Harvester)* · Untethered Soul *(Lich / Abomination)*
- **Blood Mage** → Vampire *(Royal Pureblood Vampire / Sanguine Horror)* · Hemomancer *(Sanguine Elemental / Blood-bender)* · Warlock *("The Devilbound" / Demonic Master)*
- **Curse Mage** → Death Mage *(The Forgotten One / SoulShattered Mage)* · Pain Mage *(Vivacious Torturer / "He Who Knows Pain")* · Control Mage *(Machiavelli / Slaver)*
- **Advanced Dark Mage** → Shade Elemental *(Shadow From Beyond / Legion of Shades)* · Dark Master *(Eternal Night / Lord of Stars)* · Sealed Paragon *(evil being who wants to be good and has sealed their powers, which break out under extreme threat. T5: The Unshackled Sovereign, redeemed with powers restored / The Shattered Apex, pushed too far, snapped and kills everything)*

**Novice Wizard**
- **Spellblade** → Spellsword *(Sword of Fire / Sword of Ice)* · Rune-Blade *(Zero Point Edge / Cursed Blade)* · Spell-Eater *(Magic Devourer / The Anti-Magic Blade)*
- **Arcane Wizard** *(renamed from Arcane Mage)* → Grandmaster Wizard *(Archmage / Arcane Elemental)* · Mana Sage *(Well of Mana / Mana Elemental)* · Ritualist *(Grand Ritualist / Shaman)*
- **Spirit Mage** → Spirit Elementalist *(Spirit Conduit: stole spirit magic and casts all elements / Spirit Caller)* · Phantasm *(Ethereal Specter / Poltergeist)* · "The Spiritbound" *(Ultimate Spirit Avatar / Spirit-Gorged Vessel: consumes lesser spirits to take their attributes into their body)*
- **Esotericist** → Time Mage *(The Clockmaker / The Watcher)* · Transcendent *(Descended Deity / Peacewalker)* · Eldritch Scholar *(Vessel of the Nameless Void / Inhabitant of the Impossible)*

**Novice Artificer**
- **Golem Smith** → Forgesmith Ascendant *(fantasy Iron Man; Armour Lord / Power Exoskeleton)* · Automaton Lord *(Clockwork King / Factory Designer)* · Swarm-Smith *(nanobots; Grey Goo Guy / Nanite Paragon)*
- **Advanced Artificer** → Master Artificer *(Unshackled Creator / Leader of Creation)* · Gunsmith *(Artisanal Cannoneer / Hyperfixated Gun Nerd)* · Magitech Architect *(upgrades rooms with magitech; Teleportation Gridmaster / The Master of the Citadel)*
- **Runesmith / Armornaut** *(crafts the fortress weapons)* → "The Runebound" *(Living Master Rune / Monolith of Balance)* · "Research & Development" *(a nod to real-world weapons-of-mass-destruction engineers; Atom Splitter / "Experimental Weapons Department")* · Scholar *(Absolute True Genius / Mind Shattered Savant of Ruin)*
- **Technomancer** → Cyborg *(Fully Chromed Sovereign / Technological Terror)* · Watchdog *(Overseer / Firewall)* · Hacker *(Black Hat / White Hat)*

### 5.2 RANGED *(T5 not started)*

**Bowman**
- **Longbowman** → Sharpshooter · Fletcher · Lookout
- **Deadeye** → Ambusher · Trickshotter · Bullseye
- **Crossbowman** → Arbalist · Semi-Automatic Crossbowman · Pavise Bearer
- **Highwayman** *(renamed from Shadowstalker; thief/bowman hybrid who robs travellers from cover)* → Hitman · Stalker · Drifter

**Novice Hunter**
- **Tamer** → Beast Master · Shape Shifter · Communicator
- **Trapper** → Sweeper · Webweaver · "Big Red Button Guy"
- **Tracker** → Analyst · Tribesman · Hiker
- **Survivalist** → Stoic · Scavenger · The Primal Hunter *(swapped with Neanderthal's Primal)*

**Gunslinger**
- **Pistolero** → Sheriff · Desperado *(renamed from Outlaw)* · "Gungho Garry"
- **Soldier** → Commander · Machine Gunner · Combat Medic
- **Sniper** → Reconnaissance Sniper · Railgunner · Ghillie Suit Sniper
- **"Buckshot Barry"** → Flamethrower Frankie · Rocket Launcher Randy · Buckshot Barry (with another shotgun!) *(same name as parent on purpose)*

**Hurler** *(T2; Big Chuck was moved down to T3)*
- **Spear Thrower** → Trident Master · Harpooner · Impaler
- **Bomber** → Arsonist · Incisionist · Anarchist
- **Big Chuck** → Bowling Bob · Cinder Block Cindy · Bigger Chuck
- **Boomeranger** → Glaive Thrower · Reeler *(renamed from Hooker)* · The Bushranger *(an Australian survivalist who uses boomerangs; description tentative)*

**Slingshotter** *(T2; replaced Bomber's old slot)*
- **Sling Master** → Hunter of Giants · Sling Savant · Flail Lord
- **Blowgun Specialist** → Tranquilizer · Witch Doctor *(tiki voodoo, distinct from the broomstick-and-cauldron Witch)* · Blowgun Sniper
- **Netcaster** → Bola Master · Goo Gunner · Ice Monkey *(a subtle nod to Bloons TD)*
- **Trickster** (a clown who throws cards) → Joker · Illusionist · Ninja

### 5.3 LIGHT MELEE *(T5 not started)*

**Thief**
- **Goblin** → Goblin Chieftain · Hobgoblin · Goblin Shaman
- **Burglar** → Heister · Cat Burglar · Archaeologist
- **Criminal** → Drug Dealer · Kingpin · Psycho
- **Outlaw** (Robin Hood type) → Rebel · Rioter · Lone Wolf

**Scout**
- **Pathfinder** → Beacon · Treasure Hunter · Cartographer
- **Outrider** → Athlete · Messenger · Stridewalker
- **Vanguard Scout** → Spy · Shadow *(elite assassin)* · Elite Scout
- **Surveyor** → Observer · Seer · Panopticon

**Assassin**
- **Shadowdancer** → Shadowjumper · Haunter · Malignance
- **Poisoner** → Snake Kin · Blighter · Envenomer
- **Executioner** → Judge · Disintegrator · Oblivion
- **Master Assassin** → Excommunicated Contract Killer · Grandmaster Assassin · Marksman

**Brawler**
- **Pugilist** → Puncher · Boxer · Iron Fist
- **Grappler** → Wrestler · Bouncer · Judoka *(renamed from Thrower; judo-style throws)*
- **Kickboxer** *(renamed from Boxer)* → Kangaroo · Nut Kicker · MMA Fighter
- **Martial Artist** → Daoist · Sensei · Monk

**Duelist**
- **Fencer** → Bloodletter · Skirmisher · Skypiercer
- **Blade Dancer** → Dervish · Blade Demon · Song of Steel
- **Swashbuckler** → Pirate Captain · Quartermaster · "The Damned"
- **Swordsman** → Grandmaster Swordsman · Jedi · Kensai

### 5.4 HEAVY MELEE *(T5 not started)*

**Crog** (big dumb guy with a big stick)
- **Neanderthal** → Ape · Dinosaur Tamer · Primal *(swapped with Survivalist's The Primal Hunter)*
- **Ogre-Kin** → Ogre Brawler · Ogre Chieftain · "Small" Ogre
- **Stick Master** *(renamed from Club-Master)* → ThrustLord · SwingLord · SwipeLord
- **Throgg** → Giant-kin · Heracles · Thraxx

**Squire**
- **Knight** → Aurablade Knight · Lord · Prince Charming
- **Spearman** → Phalanx · Dragoon · Gladiator
- **Strategist** *(renamed from Commander)* → Augur · Tactician · Leader
- **Flag Bearer** → Well of Hope · Warcaller · Dread Herald (Jolly Roger, debuffs enemies)

**Initiate Paladin**
- **Valkyrie** → Storm Maiden · Einherjar Vanguard · Shield Maiden
- **Crusader** → Holy Knight · Justicar · Zealot
- **Templar** → Spellbreaker · Hierophant · Dawnguard
- **"The Oathbound"** → Oathbreaker · Angelic Warrior · Demonic Warrior

**Hammerman** *(replaced the T2 Spearman; Spearman now lives under Squire)*
- **Viking** → Berserker · Storm-Herald · Rune Warrior
- **Dwarf** → Dwarven Warrior · Dwarven Engineer · Dwarven Blacksmith
- **Smith** → Artisanal Bladesmith · Furnace Lord · Forge Master
- **Wrecker** → Destructor · Crusher · Obliterator

**Shieldbearer**
- **Bulwark** → The Iron Gate · Fortress Lord · Rook
- **Defender** → The Castle Doctrinarian (the guy who *really* wants a home invasion) · Home Alone Kid · Knockoff Waluigi
- **Bastion** → Juggernaut · Fortress Incarnate · Titan-Shield
- **Evade Tank** → Parry Lord · Guru (Oogway style: pauses momentum and reflects it back) · Matador

### 5.5 SUPPORTER *(T5 not started)*

**Medic**
- **Doctor** *(renamed from Field Surgeon)* → Surgeon · Nurse · Mad Doctor
- **Sanitarian** → Hazmat Hank · Purificator · Janice (a random cleaning lady)
- **Genetic Engineer** → Mutagenist · Cloner · The Menagerie (breeds crazy plants and animals with special effects)
- **Therapist** → Hypnotist · Serene Presence · Dark Mango Psychology Expert

**Handyman**
- **Repairman** → Repair-O-Bots · Master Repairman · Good Ol' Dave
- **Builder** → Foreman · Master Mason · MacGyver
- **Demolitionist** → Razer · World Eater · Pruner
- **Engineer** → Chief Engineer · Overclocker · Automaton Mechanic

**Novice Mystic**
- **Oracle** → Doomsayer · Star-Gazer · Prophet
- **Weaver** → Thread-Spinner · Spindle-Guard · Tapestry-Cutter
- **Witch** → Cauldron Hag · Hex-Weaver · Coven Matriarch
- **Sage** → Savant · Forbidden Scholar · "Enlightened"

**Novice Alchemist**
- **Advanced Alchemist** → Chemist · Elixir Specialist · Vapour Addict
- **Toxicologist** → Miasma Lord · Dragonkin Alchemist · "The Goobound"
- **Transmuter** → Matter Shifter · Philosopher's Alchemist · Unstable Transformer (Jekyll and Hyde)
- **Imbuer** → Attuner · Enchanter · Awakener

**Jester**
- **Clown** → Chaos God *(replaced Joker)* · Comedian · Twitch Streamer
- **Drunkard** → Tavern Brawler · The Drunken Fist · Alcohol Misuse Disorder
- **Harlot** → Siren · Succubus · Charmer
- **Artist** → Bard · Painter · Actor

---

## 6. What's next

> **Updated 25 Oct 2026 (evening):** Module 1 now has items, three shields, the enemy camp with a ballista and mana gun, doors with pads, mana and stamina bars, every info card, **the Loom test route (camp → Goblin Shop → Sage → Goblin Boss)** and **our ballista mount**. Next: the rest of P4 (personal weapons, towers), P5–P9. Details in `03_Latest_Handoff.md`.

1. **Module 1 (Living Bastion Sandbox):** keep polishing or rebuilding the movable fortress, character animation, doors and click handling with the 5 basic classes. Core rooms: Weapons Tower, Chrysalis Ward, Fateweaver Loom.
2. **Tier 5 naming:** Ranged (120), Light Melee (120), Heavy Melee (120), Supporter (120). Next up: **Bowman** (Sharpshooter, Fletcher, Lookout, Ambusher…).
3. **Regenerate the class database/seed files** from Section 5, replacing the outdated Argon drafts.
4. Later: sector bosses, string/encounter design, the remaining modules.

---

## 7. Decisions log and notes

### 7.1 Resolved on 23 Oct 2026
- Spellblade's first T4 is **Spellsword** (not Battlemage).
- Netcaster's third T4 is **Ice Monkey**. Blowgunner is gone (it clashed with Blowgun Specialist).
- Demolitionist keeps **Pruner**.
- Smith's T4s are **Artisanal Bladesmith · Furnace Lord · Forge Master**. Gemini's suggestions were dropped.
- Eldritch Scholar T5 **Vessel of the Nameless Void** is approved.
- Lifebinder (Vitality Mage) is accepted. Sawbones (Doctor) is dropped.
- **Shadowstalker → Highwayman**, with no "Shadow" nickname.
- Tamer loses its "Beast Master" alias, so Beast Master is only its T4.
- **Primal** (now Neanderthal) and **The Primal Hunter** (now Survivalist) were swapped.
- Clown's **Joker → Chaos God**, so Joker exists only under Trickster.
- Squire's **Commander → Strategist**, so Commander exists only under Soldier.
- Grappler's **Thrower → Judoka**.
- The Fate Loom's **Strain** feature (instant fill at a cost) is **dropped**.

### 7.2 Kept on purpose
- **Buckshot Barry** has the same name as its parent T4 (it's the joke).
- **Bastion** (Shieldbearer T3) shares its name with the fortress.
- **Witch** vs **Witch Doctor**: different concepts (broomstick/cauldron vs tiki voodoo).
- **Kangaroo** vs **The Bushranger**: different concepts (animal vs Australian boomerang survivalist).

### 7.3 Minor notes (no action needed unless I change my mind)
- **Chaos God** shares the word "Chaos" with the Chaos string type on the Loom-Web.
- **Strategist** and its child **Tactician** have similar meanings.

### 7.4 Not decided yet
- Starting crew names.
- Sector bosses, full string/encounter list, resource set.
