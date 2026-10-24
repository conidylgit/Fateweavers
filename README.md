# Fateweavers: start here (updated 24 Oct 2026)

The game folder: the current build, the class tree data and the notes that go with them.

## ⚠️ Old note: the Gemini/Argon PDFs
The chat PDFs **cut off every code line that ran past the edge of the page**. Code copied straight out of the PDFs won't compile. Everything here has been handled:
- **Current code** was repaired and tested.
- **Reference code** is marked where lines were cut off.

The full source of Argon's early engines (`FateLoomEngine`, `FortressSubsystemEngine`, `BoardingAndPathfindingEngine`, `SiegeArtilleryEngine`, `FateEncounterEngine`, `ClassTreeDefinitions`…) was **never fully in the chat logs**, only small patches. So it can't be recovered from the PDFs. If you have those files saved anywhere, they're the only copy.

---

## 1. The game (`src/`, `test/`): the current build ✅
**Module 1: Fortress and character sandbox.** Run it with `run.bat` (Java 17+). `test.bat` compiles and runs `LogicTests`, and also renders screenshots into `test/`.

- **Code layout:** `fateweavers.logic` is the rules and state (no Swing, no `java.awt`, so it moves to LibGDX unchanged). `fateweavers.swing` is drawing, the info panel and input.
- **The map, left to right:** your fortress, then your **Entrance Hall**, **No Man's Land** (two halves with a **gate** between), the **Camp Entrance**, and the **Bandit Camp**. Tiles are 56 px (`Room.TILE`); everything fits in 1440 x 760 with no scrolling.
- **Your fortress rooms:** Turret (Fire Gem pedestal), Control Room (Glass Orb shield), Fateweaver Loom, Permeator (makes the Aether), Chrysalis Ward (heals; class changes), East Room (empty), Entrance Hall. Every room has 4 pads; pad 0 is the station.
- **Controls:**
  - Left-click a blob, room or door to open its info panel. Click empty space (or **Esc**) to deselect.
  - Right-click a tile to send the selected blob there. Pathfinding goes through doors and re-routes if one is locked.
  - Keys **1-9** select crew. **Space** pauses. **S** shield. **Tab** opens the class tree. Backtick opens the dev console.
  - Door panel: Auto / Held open / Locked. There are **Open all** and **Close all** buttons under the map.
  - Mouse wheel scrolls long info panels.

### The Entrance Hall, No Man's Land and the gate
- The **Entrance Hall** (2 x 2) is off the East Room and is your front door. It borders **No Man's Land**.
- **No Man's Land** is two 2 x 4 halves with the **gate** between them. Your crew can walk out, hold the gate, fight there and raid the camp. Aether only exists inside your fortress; outdoors it is always 100%.
- **Gate control:** each blob standing in either half adds **gate strength = 1 + Strength / 10**. The side with the higher total controls the gate; a tie means nobody does. Only the controlling side can open or close it (the gate panel buttons do nothing otherwise). A closed gate opens only for its controller, and the other side can break it. The gate has **3x** a normal door's health. A pennant and a "You N : N Bandits" tally show who is ahead.

### The Bandit Camp
- **Layout:** a green column of three 2 x 2 rooms plus the Camp Entrance: **Bandit Shield** (top), **Bandit Quarters** (middle, with a campfire) and **Archer Tower** (bottom, with the ballista). Camp rooms have 3 health bars like yours.
- **Bandits:** three **Level 1 "Bandit-Wannabe"** blobs with no class yet (red name and health bar, shown as "Level 1 Blob"). Each has a job: **SHIELD** mans the camp shield, **GUNNER** mans the ballista, **GUARD** defends the camp and leads boarding. Clicking one inspects it.
- **Ballista:** 10 bolts, one every 7 seconds after a 1-second wind-up with a red aim marker (the first shot comes after 5 seconds). Bolts **ignore your shield** completely, so all you can do is take the hit and repair. A bolt knocks out 1 bar of a room. It needs a bandit on its station and a standing tower. If the gunner dies, another free bandit takes over (gunner, then guard, then shield bandit); if nobody is free for 3 seconds they board anyway.
- **Camp shield:** 1 layer, 8-second recharge. Each layer stops one Fire Gem shot at the camp. It is 0 if its room is wrecked.
- **Your Fire Gem** shoots a camp room (default Archer Tower; choose another from that room's panel). A hit takes 1 bar off the room and does **5x weapon damage to every bandit inside**.
- **Boarding:** when the ballista is out of bolts the bandits walk through No Man's Land and the gate, break your doors and chase your crew. In rooms nobody is defending they knock out **1 bar every 5 seconds**. Your Heavy Melee still strengthens your doors.
- **Victory:** when every bandit is dead a "Camp cleared!" banner shows, and a fresh camp (rooms and doors mended, 10 bolts, three new bandits) moves in after 8 seconds. It is a repeatable test bench. If all your crew die, a "Fortress fallen" card shows with a Restart button.

### Combat
- **Auto-fight, FTL-style:** hostile blobs in the same room trade a blow **once a second**. Walking blobs do not swing but can be hit.
- **Damage:** melee dps = 5 + 0.25 per point of Strength, and all melee is **bludgeoning** for now. Every hit goes through one place (`Fortress.applyHit`): armour by damage type, then resist or weakness by attunement, then health. The Fire Gem is fire **magic**; the ballista bolt is neutral **piercing**.
- **Doors:** a door has an owner side. A closed door opens only for its side. Enemies stuck at one hit it for **40% of their melee dps**. A broken door stays open until someone of its side stands next to it (**8 seconds** to mend).
- **Reading a fight:** panels show "Taking N dmg/s, down in N s" on fighting blobs, and floating numbers use the hit's attunement colour.
- Fighting trains Strength; taking damage trains Resolve and Vitality. Numbers marked placeholder in `logic/StatRules.java` are still to be tuned.
- Full stat, attunement and damage rules: the project doc `claude/stat_system.md`.

## 2. `class_tree/`: the up-to-date class database ✅
This **replaces** the old `seed_locked_classes.sql`, `seed_remaining_143_classes.sql`, `seed_tier5_*.sql` files, which contain outdated draft names (e.g. "Giga-Crog", "Knuckle-Duster Danny").

| File | What it is |
|---|---|
| `fateweavers_class_tree.py` | The master tree as editable Python data. Run `python fateweavers_class_tree.py` to rebuild the other three files. It also checks the branching (5/5/4/3/2) and stops if a branch has the wrong number of children. |
| `fateweavers_class_tree.json` | Every class: `id, name, tier, path, parent_id, status` |
| `fateweavers_class_tree.sql` | SQLite table + inserts |
| `fateweavers.db` | Ready-made SQLite database |

- **Count:** 551 classes (1 + 5 + 25 + 100 + 300 + 120 Magic Tier 5s). The remaining 480 Tier 5s get added as you name them.
- **Matches the info packet exactly:** every name was checked against it.
- **`status: "check"`:** marks names still unconfirmed: now just Pruner and Vessel of the Nameless Void. Decided since: Battlemage → Spellsword, Blowgunner → Ice Monkey, Anvil-Dropper / Furnace-Brawler / Master Metallurgist → Artisanal Bladesmith / Furnace Lord / Forge Master.
- **Unzipped copy:** the game reads `class_tree/fateweavers_class_tree.json` from the project folder (not from the zip). After editing the `.py`, run it to rebuild the JSON and the tree screen picks up the change next launch.
- **Names only:** stats and mechanics aren't included, since those were never decided.

## Class tree screen (in the running game)
Press **Tab** or click **Class tree** in the fortress header: the tree opens on Unclassed and the five paths. Or select a crew member and click **View in class tree** in their panel: it opens on that person's class, lit up with its route to the centre. Tab, or the **Fortress** button, goes back. The fortress keeps running meanwhile.

- Unclassed sits in the middle. Each tier is a ring further out, and each path has its own wedge (Heavy Melee at the top, then Magic, Ranged, Supporter, Light Melee going clockwise).
- **Scroll** zooms toward the mouse. **Drag** moves the view. **F** (or the **Fit** button) shows the whole tree. **+ / -** zoom too.
- Names appear ring by ring as you zoom in. Hover a class to see its name and its route back to the centre.
- **Click** a class for its details: path, tier, status, the class it comes from, and the ones it leads to (click those to jump there).
- The 480 Tier 5 slots that don't have classes yet show as faint grey dots.
- **Class icons:** each class's icon is its path symbol, reshaped into a simple silhouette that hints at the class (Tier 2 so far; the rest show the plain path symbol until they're designed). Light Melee icons are thick outlines, like its hollow square. The style rules are in `class_tree/ICON_STYLE.md`.
- **Editing icons:** they live in `class_tree/class_glyphs.txt`: a class id, then a few simple shapes (polygons, circles, boxes, lines, SVG-style curves) on a 100 x 100 grid. The format is explained at the top of the file. Restart the game to see changes; mistakes are printed in the run.bat window.
- Code: `logic/ClassTree`, `ClassNode`, `ClassPath`, `ClassGlyphs`, `FlatJson` (loading, layout and icon data, no drawing). `swing/ClassTreeView`, `ClassTreePanel`, `ClassSymbols` (drawing and input).

## 3. `reference_old_argon_drafts/`: reference only ⚠️
Argon's last big LibGDX architecture output ("5 directives" prompt):
- `FortressCoreArchitecture` (3 core rooms + unlockable room sockets)
- `ClassTreeRepository`
- `CoreRoomSchematicRenderer` (procedural Weapons Tower / Chrysalis Ward / Fateweaver Loom art)
- `OptimizationEngine` (particle pooling, path cache, hazard tracking)
- `CrewStateMachineAndXpPipeline`
- `TravellingMerchantAndBlueOptionEngine`
- `BlindWeaverBossStateMachine` (3 phases)
- `bastion_notebook_db.py`

These are useful as **design ideas**, but they don't compile. They depend on the missing engine files, still use `com.bastionoffate` naming, and some lines were cut off (marked `LIKELY CUT OFF IN PDF`).

**Not included:** the earlier all-in-one Swing prototype (Loom map, siege combat, Chrysalis tree view). Module 1 replaced it, and it had the same cut-off problem.
