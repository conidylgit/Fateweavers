# Fateweavers: start here (updated 26 Oct 2026, after save 81)

The game folder: the current build, the class tree data and the notes that go with them.

## ⚠️ Old note: the Gemini/Argon PDFs
The chat PDFs **cut off every code line that ran past the edge of the page**. Code copied straight out of the PDFs won't compile. Everything here has been handled:
- **Current code** was repaired and tested.
- **Reference code** is marked where lines were cut off.

The full source of Argon's early engines (`FateLoomEngine`, `FortressSubsystemEngine`, `BoardingAndPathfindingEngine`, `SiegeArtilleryEngine`, `FateEncounterEngine`, `ClassTreeDefinitions`…) was **never fully in the chat logs**, only small patches. So it can't be recovered from the PDFs. If you have those files saved anywhere, they're the only copy.

---

## 1. The game (`src/`, `test/`): the current build ✅ (updated 26 Oct 2026, save 81)
**Module 1: Fortress and character sandbox, plus the first slice of the map.** Run it with `run.bat` (Java 17+). `test.bat` compiles and runs `LogicTests` (1,320 checks) and renders every screenshot into `test/` (the `QolShots` harness also prints PASS / FAIL lines for the R1 map fixes and the R2 cards); `bots.bat` plays headless bot games. The full log of what changed (R1 saves 23–27, R2 saves 28–32, R4 saves 33–36, the T4 table save 37, personal weapons saves 38–39, the open items saves 40–41, ranged combat save 42, duos 43, A3 44–46, pause and dev tools 47, the blob mounts 48, the surge 49, then saves 50–81 below), every placeholder number and the open questions are in **`03_Latest_Handoff.md`**; the rules as built are `design/systems/stat_system.md` v8.3 in the project.

### New since save 80 (save 81, the design thread's bottom bar)
- **The bottom bar** (concept 1, FTL reactors): the **mana reactor** counts **free** mana ("0 free of 4", blue pips for what's free), then each system (Permeator, Loom, Pedestals, Weapons) has its pips over an **icon tile** that lights blue while it's powered. **Click a tile** to power the next one, right-click to switch the last off (the pips still work). Weapons fades back when no staff is wielded. The **stamina reactor** (amber) and **Mounts** follow. A damaged system shows its integrity on its tile as a fraction, **"2/3"** (red "0/3" when broken).
- **The buttons on the right** are grouped and labelled: **CREW POSTS** (Save posts, Return) and **DOORS** (Open all, Close all). The control hints moved behind the round **"?"** at the far right (hover it, or click to keep it open; Esc closes it). Notices show in the middle of the bar.
- The icons on the bar are the mock-up's sketches (PLACEHOLDER until the design thread picks them).

### New since save 69 (saves 70–80, the design-thread build)
- **Blob renderer parity:** the game draws every model in `class_tree/class_blobs.txt` exactly as `tools/BlobPreview` does (check with `blobcheck.bat`). The crew card and class card draw the class's own model.
- **The approved device and item art:** Furnace A, Training stand A and the cel-shaded item icons (`swing/SvgArt`, `swing/ItemSvgDefs`), with attuned items tinted in their colour.
- **Hands free:** a blob's equipped weapon shows in its hand as a black silhouette with a steel or gem accent (it floats beside blobs with no hands; `swing/EquipArt`). A `hand x y [angle]` line in `class_blobs.txt` moves it. Armour tints the torso and shoulders: Plate steel with a shine, Leather brown, Mana violet.
- **Your notes on it (save 73):** daggers are held blade-down, swords tilted higher, and blobs with hip items wear a **belt** instead of a headband (the nearest headband or belt in a model's lineage wins). A one-sided hip item hangs on the back hip, away from the weapon hand (save 74).
- **The Ward, the class card and the rerolls (save 75):** the class card keeps its colours in promotion mode; no more rerolls (a promotion may raise each attunement one rarity, Luck sets the chance); the Ward room card is back; the side card takes clicks before the HUD buttons under it; the Ward is Restoration-attuned on its card.
- **The lineage strip (saves 76–77):** every next class shows, "?" while it's still in the fog; hover a ring for its name.
- **The stats panel (save 78):** the crew card's attributes are grouped value bars in four families (value out of the cap, green XP line, MAX, Spend N points → Done), and the stats book opens with a profile shape chart.
- **Levelling (save 79):** the level takes half of every attribute XP (the same curve), so taking two attributes to the cap = the level cap; the crew start at level 1.
- **Hands (save 80):** every model holds its weapon where you placed it on the Hand Placer (`hand x y angle`, `hand none`, `hand bow x y` for one weapon); Throgg and the Pugilist fight with their fists; the hammer is a small war hammer; the Swashbuckler lost his parrot.

### New since save 64 (saves 65–69, 26 Oct night)
- **System integrity:** a mount's bolt that takes a bar also takes a point off a system in the room (−33% of every number; 3 points = broken). Repairs mend the bars, then one point at a time; a destroyed system's first point takes twice as long. A damaged system wears an amber "67%" tag on the map, and "2/3" on its tile on the bottom bar (save 81).
- **A stolen Permeator** costs a mana and runs only in one of your Utility Rooms. **The Mana Blade** scales Arcana + Dexterity.
- **The picked icons** (52 attunements, 11 attributes, the stats book) are drawn from SVG (`swing/SvgIcons`, `swing/IconSvgDefs`).
- **The event screen:** an event (the Sage) is a dialogue bar over the dimmed map (`swing/EventBar`). The selected blob talks; choices show their attribute icon and chance; below the recommended 20 a choice is a long shot (chance × attribute ÷ 20, never below 1%).
- **The class card:** the class tree's side card is the dark C2 card (blob on its attunement's glow, attribute chips, perks, lineage, the gate as two bars, NEEDS / PROMOTE! / OPEN CLASS PAGE).

### New since save 48 (saves 49–63, 26 Oct)
Each line's details are in `03_Latest_Handoff.md` under its save.
- **49 The Permeator's surge:** a blob on its post spends a spare mana on a 10 s surge (Arcana).
- **50–51 Small changes, device slots:** every pedestal, station and Anchorstone card has a slot row: drag between the device and its operator's hands (no Unslot or WORK IT buttons). Mounts power themselves when someone climbs on.
- **52 Stealing:** in a cleared room, a Thief drags an enemy device into its hands (anyone can take a crate); a charge bar (Dexterity + Luck), a hit resets it, the nearest enemy comes for the thief.
- **53–54 Rooms:** the Workshop (2x3: Workbench + Furnace), the Training Room (3x2: the training stand + an empty Anchorstone), a 5x4 storage rack, the quiver's own slot. Every Anchorstone device can be lifted and set down on any empty Anchorstone; a stolen campfire heals at home.
- **55 Crafting:** stacks of 20, a 3-input + tool Workbench, 21+ recipes (the Workbench's book page lists them), raws at the shop and in chests, essence (on deaths and level-up rerolls), the level-up reroll pop-up (both rerolls removed in save 75), the Protection Amulet.
- **56 The shop page:** a full-screen goblin market with tabs (Weapons, Devices, Ammo, Materials, Services); it pauses the game.
- **57–58 Morale, Madness, alignment:** two thin bars and an alignment icon on the crew card; inspired / shaken; desertion at 0 (TALK DOWN); madness episodes at 100; classes start Good or Evil by path. Posts (right-click a device or its slot to send the selected blob to its post), duo damage types, the Axe, gem staffs, the Furnace trains Strength + Knowledge.
- **59 The Loom-Web:** a generated tapestry of 10 columns; SLEEP opens the Loom-Web page, click a knot to weave there; the Unspoolers fray a column a sleep after 2; Treasure Caches, Healing Springs, Faith and Attunement Shrines; Deeper or Home after the miniboss.
- **60 The evening answers:** the Mana Blade (the gem's attunement + Finesse, Magic + Piercing); any Quintessence + a Glass Orb makes that orb (a Mana shield carrying it); RESTART starts a fresh run.
- **61 The camp's devices** are now the same objects as yours (stealing takes the very ballista engine or campfire).
- **62 System damage:** a mount's bolt that takes a bar also breaks a system in the room (both sides). BROKEN shows under it; whoever repairs the room mends the bars, then the system.
- **63 The Siege fortress:** a Siege knot is an enemy fortress behind walls: Entrance Hall (their gate), Mount Room (a Ranger Platform and their shooter with a Longbow), Pedestal Room (a Stone shield), Utility Room (a Permeator: break or steal it and their rooms vent), Treasure Room. Three crew; kill them all to take it.

- **Code layout:** `fateweavers.logic` is the rules and state (no Swing, no `java.awt`, so it moves to LibGDX unchanged). `fateweavers.swing` is drawing, the info panel and input.
- **The map, left to right:** your fortress, your **Entrance Hall**, **No Man's Land** (two halves with a **gate**), the **Camp Entrance** and the **Bandit Camp**. Tiles are 56 px (`Room.TILE`).
- **Your rooms:** three **Utility Rooms** (the storage rack, the Workbench, the Permeator: each room card's sub-line says which), two Pedestal Rooms (the Fire Gem's and the shield orb's), Fateweaver Loom, Chrysalis Ward (heals, class changes), Mount Room (the ballista mount), **Battlement** (above the Mount Room, two stations: the Scorpion and the Ranger Platform), Entrance Hall. Every device stands **in the middle of its room on a round base** (one slot per 2x2 room, two per 2x3, the Ward's in the middle of its 3x3). Conduits sit on **pedestals**, siege engines on **mounts**, utility devices on **Anchorstones**. **A station is a spot beside its device:** the operator stands left of it on our side, right of it on the camp's, facing it.
- **The Loom test route:** four nodes east of the gate, Bandit Camp → Goblin Shop → The Sage → Goblin Boss. Sleep at a full Loom to weave to the next one; the node you leave is erased. **The camp starts neutral** (R4, 26 Oct): it does nothing until it sees aggression (you powering a weapon or mount, firing, or a blob of yours entering No Man's Land; a shield doesn't count), then it fights as before. **You can't sleep while hostiles remain** (a hostile band, the shop's guards once robbed, the boss); a neutral camp counts as none. **Units** are the money (header, top; they were "Fate shards" until 26 Oct). Shop: buy / repair / hire / rob. Sage: select a blob, pick one of five choices with the chance shown. Boss: rocks, a belly slam with goblins, a rage; win by killing, feeding (Food from the shop) or the sage's hint.
- **Start:** all four crew start on a station (both Pedestal Rooms, Loom, Workshop) and the Fire Gem, Loom, shield and Permeator start powered.
- **Every attribute starts at 1.** Players read "Enemies" (proper names like Bandit Camp stay).
- **The power system (R2, 26 Oct):** eleven attributes (Strength, Vitality, Dexterity, Perception, Arcana, Faith, Psyche, Knowledge, Resolve, Charisma, Luck); attribute point n costs 20 + 2n XP, and since save 79 a level costs the same and fills from half of every attribute XP (two capped attributes = the level cap; the crew start at level 1); the tier cap holds gifted points too (overflow is banked until promotion); a class needs its favourite attribute at the gate as well as the level; an Unclassed blob picks its own star (the empty star slots on its card) until level 5; the green ▲ follows the most practised attribute; mana and stamina pools follow a square-root curve of the crew's attributes. Tier 1 classes carry their path's attunement (Arcane / Heavy / Martial / Range / Knowledge); a duo's part is worth 60%; damage types (Slashing, Piercing, Bludgeoning, Neutral, Magic, Mental, Soul, True) never match attunements; rarity rolls are for blobs only; Resolve softens Mental and Soul damage and breaks doors faster, Charisma lowers the shop's prices, Luck crits in melee.

### Controls
- Left-click a blob, room, door or device to open its card. Hover outlines hug what you'll click (a blob's drawn body, a device and its base; the rack, the crystal and the Permeator too). Clicking on the left half of the screen opens the card on the **right**. Click empty space (or **Esc**) to deselect.
- A weapon's fire button or AUTO hides the card so you can pick the room; the card stays closed after the pick (double-click the weapon image to toggle its power, which keeps the card).
- Right-click a tile to send the selected blob there. Sending a blob into a device room puts them on the free station.
- **Your crew don't attack on their own** (26 Oct): right-click an enemy to attack it (the target flashes red; they follow it), right-click an enemy device to wreck its room. A move order cancels the attack.
- **1-9** select crew. **Space** pauses. **S** raises the Fire shield. **Tab** opens the class tree. Backtick (or the Dev button) opens the dev console: Test Mode, **free class picks** (any class at once, no requirements), +1 / +5 levels or to the tier cap for the selected blob (or the whole crew), free points, Luck, test shots. **The game pauses while a page covers the fortress** (a book page or the class tree).
- **The bottom bar** (save 81): the mana reactor (free mana), then the Permeator and Loom with their own columns; everything else shares a column per kind (**Pedestals**, **Weapons** for staffs; **Mounts** after the stamina reactor). **Click a system's icon tile** (or its pips) to power the next member, right-click to switch the last one off; hover to see the members. Capacity = round(3.11 + 0.256 × √(everyone's Arcana + Faith + Psyche)); stamina the same from Strength + Vitality + Dexterity + Resolve.
- **CREW POSTS: Save posts / Return** sends everyone back to their saved spots; **DOORS: Open all / Close all**. The round **"?"** at the far right shows these controls (hover, or click to keep it open).
- **Mount:** the Mount Room's ballista draws **stamina** (the amber reactor on the bottom bar) and uses the bolts of the crates in its room (no tick row any more). Someone on it climbs on for 2 s before it charges (**14 s**), and takes 2 s to climb off when ordered away. AUTO / single shots like the Fire Gem. **The Scorpion** (Goblin Shop, 35 Units): half the damage, twice the rate, no stamina; with it on the rack, the mount card offers "Fit the Scorpion" (a swap).
- **The Battlement and blob mounts (26 Oct, save 48):** every mount sits on a **station**, and **any mount fits any station** ("Fit …" on its card): the Ballista, the Scorpion, the **Ranger Platform** and the **Stage** (Goblin Shop, 30 Units). Both blob mounts draw 1 stamina and take 2 s to climb on or off.
  - **Ranger Platform:** anyone can climb on. The blob shoots its own ranged weapon (arrows, a staff's mana) at an enemy in **any room, indoor or out, within its range + 2 tiles** for the height. It's the only way to shoot out of the fortress, on a high lob. Shield rules apply: the camp's Mana shield stops a staff's spark, arrows pass. Without a ranged weapon or ammo it stands by. Select the shooter to see the ring.
  - **Stage:** the performer only performs. Enemies within **1 + Charisma/20 doors** (at most **1 + Charisma/10** at once) walk to the Stage and attack the performer. Every 3 s each rolls to break free (1% per Resolve point, max 60%). The rooms in reach light up gold while it's selected; taunted enemies wear a gold star.
  - The Guard Post is scrapped for now (parked as a door upgrade).
- **Items:** each blob carries one item. Drag items between a blob's hands slot, the Storeroom rack, the workbench and pedestals in the info panel, or click a slot for the obvious move. **Unslot** puts a conduit in the operator's hands (swapping with what they hold if it fits the pedestal).

### Shields (the shield Pedestal Room; the orb decides the shield)
- **Glass Orb = Mana shield:** automatic bars that stop magic and mental shots.
- **Stone Orb = Stone shield:** charges 20 s, then stands until broken. Piercing and slashing bounce off; blunt hits it ×3; while it stands, enemies can't come through your outer doors.
- **Fire Orb = Fire parry:** press **S** as the shrinking ring reaches the target ring ("S!" shows in the parry window). A perfect parry destroys the shot and sets the room that fired it on fire; a late one blocks for a bar. The Fire and Stone orbs start on the Storeroom rack.

### The Bandit Camp
- Four Level 1 enemies with jobs: **Shield**, **Gunner**, **Caster**, **Guard**. Bandit Shield (top), a Utility Room with a **campfire** where enemies heal (middle), the Battlement with a **ballista** and a **Glass Gem** mana gun, each with its own operator (bottom).
- The gunner shoots the ballista at your shield pedestal while your shield is up, and swaps to the faster mana gun once it's down. **Bolts meet your shield** like any shot: Mana lets them through, Stone bounces them, Fire can parry them. Out of both ammo types, the enemies board.
- Your Fire Gem shoots a camp room; click a room while it charges to arm the shot. Rarity is rolled for every conduit, device and door (with a rolling animation).
- **Treasure Room** (right of the campfire): its door is padlocked until every enemy is dead. Its chest works like the storage rack and is refilled with each new band (2 rolls: conduits or a weapon).
- **Victory:** kill every enemy; a fresh camp moves in after 8 s. If your crew all die, Restart.

### Doors and combat
- Doors are **Open** or **Closed**; closed doors open for their own side. Base health 20; each blob of that side standing on a door pad adds +10% (+1% per Strength), and a Heavy Melee in the room reinforces it without a pad. The gate has 3× health and the side with more strength in No Man's Land (Heavy 3, Light 2, others 1) controls it.
- Hostile blobs in the same room trade blows. Fists are **neutral** damage, one blow a second; a weapon sets its own rate. Every hit goes through `Fortress.applyHit` (armour, attunement, health). Full rules: project doc `claude/stat_system.md`.

### Personal weapons (A1, 26 Oct)
- Every blob has four slots: **Hands, Weapon, Armour, Accessory** (Armour and Accessory stay empty for now). The crew card shows them, with the weapon's name, dps and type and a book icon for its page.
- Eight weapons (numbers PLACEHOLDER): Sword, Rapier, Hammer, Wrecking ball, Longbow, Rapid crossbow, Spellsword, Glass Magic Staff. Each has a damage type, an attunement, a rate and two scaling attributes (+1% damage per point each). Ranged weapons fight anyone in their own room, and **from an outdoor room (No Man's Land, the camp, the boss arena) they shoot into other outdoor rooms within range**: 2 + 0.8 × √(Perception − 1) tiles. Select a ranged blob outdoors to see its dashed range ring; ordered at a far enemy, it walks to the first spot in range and stops. Your fortress is indoor: no shooting out.
- **Start:** Aldric Sword, Brenna Longbow, Cedric Hammer, Daria Glass Magic Staff; the other four weapons wait on the storage rack.
- **Changing weapons only in the Storeroom:** with the blob there, drag a weapon from the rack onto its Weapon slot (a swap), or drag it back to unequip.
- The Goblin Shop sells two random weapons; the treasure chest can roll one (20% a roll).
- **Finesse** (a new attunement on the Rapier and Rapid crossbow): +10% crit chance. The Spellsword is Slashing.

### Ammo, quivers and the Staff (26 Oct, A3)
- **Ammo lives in rooms:** a bolt crate (5 bolts, shop 5 Units) set down in a room feeds any mount there (at most 2 crates). Take a crate in a blob's hands (Storeroom card) and **drag it from the hands slot onto a room on the map**: the blob carries it there. The Mount Room starts with 1 crate.
- **Bows shoot arrows from a quiver** in the Accessory slot (16). Empty = fists until you buy a quiver (refills the emptiest). Brenna starts with one; a spare is on the rack. Put quivers on in the Storeroom.
- **The Glass Magic Staff needs a mana bar** (the Weapons column); unpowered, Daria uses fists. The bar is full at the start, so switch something off to power it.
- **The camp** now loads its ballista in 14 s too, its crew's stats scale its devices like yours, its Glass Gem never runs dry, and it boards when its 10 bolts are gone.

### Training and attunements (26 Oct)
- **Using a thing trains every attribute it scales with:** a gem trains Arcana + Dexterity, the mount Strength + Dexterity, the Loom Arcana + Knowledge, the Ward Vitality + Faith, a weapon its two attributes.
- No Man's Land trains Perception (lookout) as well as Resolve; the blob haggling in the shop learns Charisma from buying; every node woven to gives the whole crew +1 Luck. Healing (the Ward, the camp's campfire) reads Vitality and Faith.
- **A class with two attunements carries them as one duo:** one card, one rarity, one points pool; each part matched gives ×0.6, both ×1.2.
- The class tree says why a next class is locked ("Needs level 5", "Needs Arcana 5"); attunement pages list the classes that carry them.

### Info cards
Every card uses the v7 skeleton (header, image box with a meter, icon cards, STATS + book icon, AIM FOR, buttons): Player, Enemy blob, Pedestal (gem / each orb / empty), Mount, Loom, Door, Enemy door, Gate, No Man's Land, Enemy Pedestal, Enemy Mount, Storage rack, Workbench, Chrysalis Ward, Permeator, Campfire; all matched to the "Info Cards: Pedestal" canvas. Room cards are the Floor Plan design; a device row on them opens the device's card.

**Info pages (26 Oct):** every card with STATS has the book icon, and one builder (`logic/InfoSpec`) makes the page for anything with stats: gems, orbs, the Loom, the mount, doors and the gate, the enemies' three devices, the Workbench, the Ward, the Permeator, the campfire, blobs (the stats ledger) and classes. `swing/BookPage` draws them all with the same layout.

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
Press **Tab** or click **Class tree** in the fortress header: the tree opens on Unclassed (the root class is simply "Unclassed" since 26 Oct; its id is still `T0_UNCLASSED_SURVIVOR`) and the five paths. Or select a crew member and click **View in class tree** in their panel: it opens on that person's class, lit up with its route to the centre. Tab, or the **Fortress** button, goes back. The fortress keeps running meanwhile.

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
