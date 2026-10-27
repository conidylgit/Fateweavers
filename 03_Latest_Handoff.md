# Fable handoff 3 (25 Oct 2026, running log: session 3)

Session 3 of the Fable plan, run unattended while the user is at work. Earlier sessions: `claude/fable_handoff_1_2026-10-25.md` (saves 1–7). Build state = the Desktop (`C:\Users\Coni\Desktop\Fateweavers`).
Key: ✅ user decision · 💡 Claude's call, change if wrong · ❓ open · PLACEHOLDER = a number to tune

## Baseline (09:40)
Save 7 compiles, 397 tests pass, all screenshots render. Leftovers from sessions 1–2: none that an answer allows (Q37's wand is 💡, not decided: not built).

## Saves 10–11 (10:30): the Loom test route (P1.5, Q29–Q35)
- **`LoomRoute`**: four nodes in a line, `TEST_ROUTE` = Bandit Camp → Goblin Shop → The Sage → Goblin Boss. `Fortress.route()`, `node()`, `nodeType()`. **Sleep at a full Loom weaves to the next node**; the Unspoolers erase the node you left (`eraseNode`: its rooms, doors, enemies and shots go; crew out there scramble back to the Entrance Hall; posts there are forgotten). At the last node, Sleep says the route is complete. Your fortress and No Man's Land stay (Q31 A).
- **Fate shards** (Q30): `Fortress.shards()`. +30 for clearing the camp (`LoomRoute.CAMP_CLEARED_SHARDS`), +10 on arriving at each later node, +50 for the boss. All PLACEHOLDER. Shown in the header.
- **Goblin Shop** (`Shop`, Q33): a 2x2 "visit" room (`Room.Side.VISIT`, nobody's door, always open) at tiles (12,2). Stock: Fire Gem 30, Glass Orb 20, Stone Orb 25, Food 5 ×2 (PLACEHOLDER prices). `buy(ware)` puts the item on the Storeroom's rack (refuses when the rack is full); `repairAll()` 5 shards a bar; `hire()` 25 shards for an Unclassed blob in the Entrance Hall (names Edric, Fenna, …, PLACEHOLDER); `rob()` spawns 2 goblin guards (Strength 10) who board; kill them and everything is free. The card lists it all with prices; the red Rob button is the aggressive option.
- **The Sage** (`EventScene`, `Choice`, `SkillCheck`, Q34): a 2x2 hut at (12,2). Data-driven scene: five choices from the roadmap draft. **You pick a blob** (the selected one) and **one choice**; the roll happens (`SkillCheck.chance` = 75/50/25 base + attribute/2 + Luck×0.5, cap 95; the chance shows on each button); the sage leaves. Locks: Debate needs KNO 40+, Read thoughts needs PSY 40+, "Show him the fate-thread" needs an attunement level and **stays greyed until levels exist** (Q34 d). Outcomes (PLACEHOLDER): Listen = lore + the boss hint / dozes off; Grab his staff (aggressive) = a Fire Gem into your hands or the rack / zapped for 20; Debate = +3 Knowledge; Read = a Glass Orb; failures are text only (no morale/madness yet).
- **Goblin Boss** (`GoblinBoss`, Q35): a 6x6 grass arena (`Room.Side.ARENA`) at (12,0) with a door from No Man's Land. The boss is a bandit `CrewMember` with `isBoss()`: takes the middle 2x2, never moves, drawn 2.2× big, Strength 20 / Vitality 100 (10 dps, 350 health, PLACEHOLDER). Throws **rocks** (blunt `EnemyShot.rock`, 15 to a shield, 1 bar off a room; the Stone shield hates them) every 8 s (first after 5 s) at a random fortress room, not while fighting. **Phase 2 (≤60%)**: belly slam (two random rooms lose a bar, `BOSS_SLAM` event) and 2 goblins (Level 1) who chase you. **Phase 3 (≤30%)**: rocks every 4 s; eats a goblin once to heal 20%. **Wins**: kill him; **feed him** (a blob carrying Food standing on the grass, the card's button); **the sage's hint** (known from the Listen success: a blob on the grass "talks him down"). Any win pays 50 shards and finishes the route; his goblins scatter.
- The Fire Gem's target follows the node (the shop / the hut / the arena); the AUTO crosshair accepts any east-side room.
- Map: per-node label and "Node n of 4 › next" under the east side; a halo in the node's colour; the goblin trader at his stall with his wares on the counter; the sage with his staff; rocks drawn as tumbling boulders; `floorOf` warm boards for visit rooms, grass for the arena. The camera refits on every Sleep. Pedestals are drawn once each (a second gem on the rack no longer draws a ghost pedestal).
- Cards: the room card gets a shop / sage / boss section; a nobody's door shows as an amber "Passage". The boss himself opens the enemy blob card.
- Tests: 442 pass (route block: erase/arrive, shards, buy/repair/hire/rob, locks and chances, choose, boss phases, feed/hint/kill). Shots: `route_shop.png`, `route_sage.png`, `route_sage_done.png`, `route_boss.png`.

Placeholders this session so far: every price and reward above; `GoblinBoss` constants; guard/goblin Strength; the sage outcomes' numbers; the hut and shop positions.

## Save 12 (11:10): our ballista mount (P4, Q36)
- **`Mount extends Weapon`** in the **East Room** (now a station room, "Siege room", device in the middle on a round base). Piercing bolts, `BASE_DAMAGE` 6, `CHARGE_SECONDS` 7 (PLACEHOLDER, the enemy ballista's rate), scalings `StatRules.MOUNT`: Strength +1% damage, Dexterity +0.5% load speed, Luck +0.1% crit (PLACEHOLDER, Q36).
- **Stamina, not mana** (Q7 note): the mount costs 1 stamina slot; `Fortress.staminaUsed()/staminaFree()`; `powerUsed()` skips mounts; the bar sheds mounts when stamina shrinks. UI: a **STAMINA** bar (grey-blue pips) right of the mana bar with the Ballista's column (left-click on, right-click off).
- **Ammo**: `Mount.MAX_BOLTS` 10 (PLACEHOLDER). Out of bolts it can't charge or fire. ❓ No resupply exists yet (💡 the shop could sell bolts).
- **Climbing on / off ~2 s** (`Mount.MOUNT_SECONDS`): the operator on the station climbs on over 2 s (`mounted` 0..1) before it charges; a move order given while mounted waits 2 s (`CrewMember.isDismounting()`, the pending order runs after), the blob standing exposed meanwhile.
- Firing uses the Fire Gem's machinery: single shot / armed shot / AUTO with the crosshair at any east-side room; bolts drawn as darts along the arc; the projectile's piercing hit damages the room and anyone in it. The Fire Gem's book/attunement pages work for it too.
- **Mount card** (💡 layout: the v7 canvas never drew one, so it uses the gem card's skeleton): ballista art with the charge fill, ON THE MOUNT card (Climbing on %, Mounted, Climbing off), Piercing attunement card with its rolled rarity, rows Damage / Load time / Crit chance (operator ▲▼), Bolts, Stamina, Climb on/off; AIM FOR STR DEX LCK; buttons AUTO and WORK IT / Stop. Open it by clicking the mount or from the East Room card.
- Personal weapons: not built (Q37 A = fists; the wand is 💡, undecided).
- Tests: 454 pass. Shot: `card_mount.png`.

## Saves 13–14 (11:40): QA bots, fixes, polish
- **`test/fateweavers/logic/BotPlaytest.java`** (`bots.bat`): four scripted bots play the whole route headless (AFK, turtle = everyone on stations + doors shut + AUTO gem, rush = everyone into the enemy side + rob + grab the staff, Test Mode = spend points and promote), then N random-action games (every public action: moves, power, shield, fire/AUTO, doors, pedestal insert/unslot, rack and bench, buy/repair/hire/rob, sage choices, feed/hint, sleep, posts, points, promotions, hand-overs). Every frame checks: no exception, every living unit on the map and headed for a room that exists, no two units on one pad, health in range, shards ≥ 0, mana and stamina within capacity, nobody stuck walking 12 s (door-breaking, fighting and dismounting excused).
- Result: **154 games, 0 problems** after two fixes: (1) `enforceRoomPower` now runs again after deaths, so the mana/stamina bars never sit over capacity for a frame; (2) the Fire Gem's shots at the shop / hut / arena flew into a zero-length second leg behind a ghost camp-shield bubble and never landed: non-camp nodes now get straight shots, and the camp's shield layers are dropped when the camp is erased (a test covers the gem hitting the boss).
- AFK falls at ~220 s (the enemies board after their ammo); turtle and rush reach node 4 in 240 s; ~20% of fuzz games lose one blob, nearly always to the boss.
- Polish from packet §7: all six items were already done or not applicable; the `Armour` javadoc's old Brute/Archer/Mage names fixed. `test.bat` now also compiles the bots and renders `QolShots`.
- Tests: **457 pass.**

## Design problems found (for you, not decided)
1. **The boss hits hard for a Level 0 crew:** 10 dps plus two goblins; a blob on the grass lives ~10 s. Feeding him or using the hint is quick, fighting him needs levels or the Fire Gem / ballista from home.
2. **No bolt resupply** for our ballista (10 bolts, then it's done). 💡 The shop could sell bolts.
3. **The route is short on money:** 30 shards from the camp + 10 a node; a Fire Gem costs 30. Prices and rewards are all placeholders.
4. **Fuzz bots can't win the camp** (no bot shoots it properly); humans can. Not a bug.
5. The Sage's fifth choice can never be picked until attunement levels exist (your Q34 d).
6. Robbing the shop with the whole crew away leaves the fortress empty: the guards wreck rooms if nobody is home.

## START HERE for the next session
- **Run:** `run.bat`. **Test:** `test.bat` (457 checks + every screenshot into `test/`). **Bots:** `bots.bat`.
- **What the game is now:** `Fateweavers/README.md` (Desktop `README.md`). **Rules and numbers:** `claude/stat_system.md` (v6). **Plan:** `claude/roadmap_2026-10-24.md`. **Decisions:** `claude/fable_questionnaire_2026-10-25.md`. **Build logs:** `claude/fable_handoff_1_2026-10-25.md` (saves 1–7), this file (saves 10–14).
- **Open ❓ (collected):** Q8 magic vs Stone (kept: wears its health) · Q10 mana-gun rate / room damage readings · Stone shield vs your own crew leaving · bolt resupply · shop prices, shard rewards, boss numbers · the wand / crude ranged weapon for starters (💡, Q37) · Workbench recipes beyond the one · the hut/shop/arena positions (💡).
- **Next phases (roadmap):** P4 rest (personal weapons, weapon towers, gatepost, stage, mount upgrades), P5 fortress shell, P6 conduits (crystal, ring), P7 attunement levels (unlocks the sage's fifth choice), P8 mind, P9 events (reuse `EventScene`).
- Dead files to delete by hand on the Desktop: the `# TO DELETE (1–15)` files.

## Play-test checklist (what to click)
1. `run.bat`. Header: "Day 1 · Node 1/4 Bandit Camp › Goblin Shop · 0 Fate shards". The East Room now holds the ballista.
2. **Mount:** click the ballista → card. Daria starts on it; after 2 s the tag says Mounted and it charges (7 s). AUTO at the Archer Tower, or click the image then a camp room. Watch a dart fly. Right-click Daria elsewhere: she stands 2 s climbing off first. The STAMINA bar (bottom left, right of MANA) has the Ballista's column.
3. **Clear the camp** (gem + ballista on the tower, or board): "+30 Fate shards". Sleep when the Loom is full → the night fades to **Node 2: Goblin Shop** (the camp is gone; the camera refits).
4. **Shop:** click the stall → Buy Food (5), Buy Glass Orb (20)… items land on the Storeroom rack. "Repair n bars" when something's broken. "Hire" puts a blob in the Entrance Hall. Red **Rob the shop** → two guards march at you; kill them → everything "free".
5. Sleep → **Node 3: The Sage.** Select a blob (its chances show on the five buttons), click "Sit and listen" (76% at Level 0): on success the boss hint is yours. "Grab his staff" is the red aggressive one. He leaves after one choice.
6. Sleep → **Node 4: Goblin Boss.** Rocks fly at your rooms every 8 s (a Stone shield bounces them; blunt hurts it ×3). Send a blob carrying Food onto the grass (through the gate) and press "Feed him"; or with the hint, "Use the sage's hint"; or shoot him with the gem / ballista and board. Below 60% he slams (two rooms lose a bar, two goblins) and below 30% he rages and eats one. Any win: "+50 Fate shards. The test route is complete." Sleep then says so.

## Save 16 (11:30, with the user): cards matched to the canvas, Pedestal Rooms
Decisions: the canvas "Info Cards: Pedestal" (17 boards, 02:30) is final as it is; cards follow the boards except the Storeroom and Workbench keep the Minecraft-style slots; both the Turret and the Control Room are now called **Pedestal Room** (identical labels; the card sub-line says "Gem pedestal" / "Shield pedestal"); the enemy device cards are titled **Enemy Pedestal** (Shield Conduit) and **Enemy Mount** (Ballista).
- **Storeroom card** (own card: click the rack or the room's button): "Room · 9 slots", rack art with "n / 9 slots" fill, the 3×3 rack, a HANDS row of the blobs standing there (drag targets), the board's hint.
- **Workbench card** (own card: click the bench): "Station · Workshop", bench art with the craft fill "4.0 / 10 s", SUPPORTER card (Class Synergy / Not a Supporter), rows Craft time and Makes, AIM FOR KNO, then the bench slots and a HANDS row. The board's "Load time 5 s per item" has no mechanic behind it: left out (❓).
- **Permeator**: "Device · Permeator room", fill and corner = **Durability** (its room's health %), Aether as the small hint, NEARBY card, one row: Status. The Power button went (power is on the mana bar; the room card still has the small toggle).
- **Chrysalis Ward**: "Device · Ward room", corner "Tier n / 5" (the board's "Level 2 / 4": read as the highest tier in the ward ❓), IN THE WARD card, rows Highest tier / Healing ▲ (the best patient's Vitality bonus) / Patients "n / 8 spots"; no AIM FOR; View in class tree.
- **Campfire**: corner "Heals 3 /s"; rows Healing, Resting "n / 4 spots".
- **Enemy Mount**: ballista alone, reload fill with "x.x / 7 s", GUNNER card tagged Aiming / Reloading / On the Glass Gem, rows Bolts left, Reload time, Hits "Rooms · Piercing", Shields "Mana lets bolts through" (the board said "Ignored"; bolts meet shields since save 7), plus a Glass Gem blasts row (kept: real state, 💡).
- **Enemy Pedestal**: sub-line "Shield Conduit · Pedestal · Enemy"; otherwise as before.
- Renames: `WEAPONS` and `SHIELD` rooms both "Pedestal Room"; every "Turret" / "Control Room" in text updated; room ids unchanged.
- 457 tests pass; shots re-rendered.

## Saves 18–20 (12:00–13:30, with the user): enemy devices, station animation, Loom/Ward bases
Decisions: the enemy Mount card's Shields row reads **"Blocks magical attacks"**; the camp's shield is a **Pedestal holding a Glass Orb** (a Mana shield) and the Archer Tower is a **Mount holding a Ballista**; a working station gets an animation; the Loom and Ward bases are centred; the room cards get a redesign (drafts on the canvas "Room Cards: Redesign Drafts", waiting for a pick; station cards are not to be redesigned).
- **Camp shield rule (follows from the Glass Orb):** it now stops only magic and mental shots (`Fortress.campShieldBlocks`): your Fire Gem still loses a shot per bar, **your ballista's bolts pass through**. Camp cards count "blocked" shots per weapon. New test (458 pass). Bots: 0 problems.
- Text: every "bolts ignore your shield" line is gone (BanditRole, Archer Tower and Bandit Shield descriptions and cards). Enemy Pedestal card: "Glass Orb · Mana shield · Enemy", Neutral attunement, Blocks magic + mental. Camp room cards: PEDESTAL → Glass Orb, MOUNT → Ballista buttons.
- **Station animation** (map): a dashed ring turning round the device's base plus rising motes while someone works the station (blue ours, red enemies); nothing when unmanned or the operator is still walking. Freezes while paused.
- **Loom and Ward** now stand on the shared round dais (`DeviceArt.loomOnBase`, `chrysalisOnBase`) dead centre of their rooms, like every other device.
- Room-card drafts: A Station-card sibling, B Crew spots first, C Floor plan; each shown on Pedestal Room (manned), East Room (empty), Storeroom (no station), Archer Tower (enemy).

## Save 22 (14:30–15:30, with the user): Floor Plan room cards, the Battlement, the Glass Gem, the station rule
Decisions (user, 25 Oct afternoon): design C "Floor plan" for every room card, with the user's own edits on the canvas; health = three glowing #1BD6D2 orbs that pop when hit and grow back while repairing; Aether shows Infused / Refilling / Leaking with a glowing border; counts as tally marks; the Mount Room gets an AMMO chip; descriptions removed from room cards; no controlling your weapons from enemy room cards ("We don't need to be able to control the enemy weapons"). The Archer Tower is now the **Battlement**: 2 wide × 3 tall, a mount (ballista) and a pedestal (Glass Gem) on the grid's two inner vertices, the same dais under each, **two operators** (Gunner, Caster: four bandits per camp). **A station left empty powers off and loses all built charge** (ours and theirs). The **Glass Gem** is a real Neutral magic pedestal weapon (weaker than the Fire Gem, Dexterity fire-rate scaling ×2) and the shop sells it. The East Room is the **Mount Room**.
- Logic: `Room` gets two-station support (`placeTwinDevices`, `stationOperator(i)`, `stationCount()`), `aetherState()` (smoothed rate) and `isRepairing()`. `BanditCamp` has a separate Glass Gem slot (`gemTimer`, `gemWindup`, `gemAim`, `caster`), no more gun swapping; `BanditRole.CASTER`. `Fortress.updateStationsLeft()` applies the station rule (weapons, mount, shield, Loom; camp mount, gem and shield). `Fortress.newGem(glass)`, `GLASS_GEM_*`, `FIRE_GEM_*`, `campGemSeconds()`, `StatRules.GLASS_GEM`, `Shop.Ware.GLASS_GEM` (25 shards). PLACEHOLDER numbers.
- Map: Battlement art (both slots on vertices, bolt and mana ticks along the room's bottom), the clear Glass Gem (`DeviceArt.glassGem`), click targets for the enemy mount / gem / shield pedestal, the camp's name moved above the camp (the Battlement grew), station rings for both Battlement stations.
- Cards: `InfoPanel.drawRoom` rebuilt (plan via `FortressView.paintRoomPlan`, labels, chips, device rows, station cards); `InfoCard.healthOrbs / chip / pill / tally / pulseDot`; new Enemy Pedestal card for the Glass Gem. The old camp section (YOUR FIRE GEM / AUTO fire here) is gone.
- Spec: `04_Room_Cards_Spec.md` (design-handoff format).
- 483 tests pass; bots: 0 problems; QolShots has room_* shots.
- ❓ The Loom also loses its weave when Aldric leaves (the rule says every station): say if the Loom should be the exception. ❓ The camp's Glass Gem keeps the 10-blast mana reserve so the camp still boards when it's out.

# R1 session (25 Oct 2026, late evening → 26 Oct): Misc, QoL, polish
Builds `roadmaps/R1_misc_qol_polish.md` in the order of `handoffs/2026-10-25_R1_handoff_and_prompt.md` §2. Save 22 was the baseline (483 checks, 40 bot games, 0 problems; re-run here before starting). Screenshots: Desktop `handoff_images\2026-10-26_R1\` (`_INDEX.md`). No shell on the PC this session: the code is staged into the workspace, built there, and written back after each item.

## Save 23: items 1–6, the map fixes E1–E6
- **E1** no green tile outline (and no blob or device outline) while a crosshair is up: the aimed room's outline is the only highlight. `FortressView.drawRooms` / `mouseMoved` check `aiming`.
- **E2 + Q44 B** a fire button (the weapon image's single click, AUTO) hides the inspector; after the pick or a right-click cancel the card **stays closed**. The card is only hidden (not closed) for one OS multi-click interval so a double-click on the weapon still toggles power and keeps the card; then `inspected` is dropped (`cardHideStart`, `InfoPanel.hideNow()`).
- **E3** tight blob hitbox: `CrewRenderer.hitbox` is the drawn body's box (it used to be a 26×54 box that ignored the 0.7 blob scale); hover and click test the drawn shape itself (`CrewRenderer.hits`, `BlobArt.shape`) within `HIT_MARGIN` 2 px (PLACEHOLDER, so thin arms still click).
- **E4** device hitboxes hug device + base, ours and the camp's: `DeviceArt.BOX_*` (measured from each drawing's opaque pixels), placed on the map by `FortressView.deviceTargets()` from the same feet the devices are drawn with. One list feeds hover, click and the outline (`Target`, `deviceAt`, `deviceBox`); the old `stationBox` and the hand-written click rectangles are gone. The camp's devices now outline on hover too (dark green on the grass), since a hitbox is hover + click.
- **E5** rack, Chrysalis crystal and Permeator light up on hover and open their card on click (they're in the same list).
- **E6** Loom drawn `LOOM_DROP` 6 px (PLACEHOLDER) below its room's centre; its station ring and hitbox follow.
- Harness: `QolShots` has R1 checks driven by real mouse events (63 PASS lines: E1 pixel check for the green tile, E2 hide / stays closed / double-click, E3 off-body vs on-body hover, E4/E5 hover + click + outside for all 12 devices, E6). Shots `r1_aim_auto`, `r1_hover_blob`, `r1_hover_{loom,rack,crystal,permeator,camp_mount}`.
- Tests: 483 pass (no logic changed), bots 0 problems. Files: `BlobArt`, `CrewRenderer`, `DeviceArt`, `FortressView`, `InfoPanel`, `QolShots`.
- Placeholders: `CrewRenderer.HIT_MARGIN` 2 px, `FortressView.LOOM_DROP` 6 px.

## Save 24: items 7–8, station spots (E7) and the placement rule
- **E7** a station is its own spot beside its device: `Room.stationSpot` puts pad 0 (and the Battlement's pad 1) `STATION_GAP` 30 px left of the device on our side, right of it on the enemy side, `STATION_DROP` 4 px below the device's foot line (both PLACEHOLDER). `Room.placeDevice` (ROUND + station) and `placeTwinDevices` use it; pathing just walks to the pad, so nothing else changed. Whoever reaches a station turns to face its device (`Fortress.faceStation`). The Battlement follows the rule as written: both bandits right of their own device, one above the other (handoff §4's ❓; say if you want one each side).
- **Placement rule** `Room.deviceSlots()` / `isDeviceSlot(p)`: one slot per 2×2 (its centre vertex), two per 2×3 or 3×2 (the inner vertices), the Ward's one in the middle of its 3×3, none for other sizes. No device moved: every one already stood on a slot; the test now asserts it.
- Tests: **495 pass** (+12: spots beside devices on the right side and inside the room, the Battlement pair, facing after walking in from either side, the gunner facing left; slot counts and positions per room size, every device on a slot). Bots 0 problems. Files: `Room`, `Fortress`, `LogicTests`.
- Placeholders: `Room.STATION_GAP` 30, `Room.STATION_DROP` 4.

## Save 25: items 9–10, Anchorstone / Utility Rooms (Q38 B, Q39 A, E11) and device rows (E10)
- **`Anchorstone`** (new, `fateweavers.logic`): the slot a utility device sits on, sibling of `Pedestal` / `Mount`. `Kind` PERMEATOR / WORKBENCH / STORAGE / CAMPFIRE with the label the room card's sub-line shows; `device` = the Permeator / Workbench / Storeroom (null for the campfire). `Fortress.anchorstones()`, `anchorstoneIn(room)`. The campfire now has a real `devicePos` on the quarters' slot (`placeDevice`), and the map and hitbox draw it from there (same spot as before).
- **E11 / Q39 A**: PERMEATOR, WORKSHOP, STOREROOM and CAMP_QUARTERS are named `Anchorstone.ROOM_NAME` = **"Utility Room"** on the map (ids unchanged; `kind` "Utility room" for ours). Room card sub-line: **Permeator / Workbench / Storage / Campfire** (+ "No station" where there is none). Device rows read "Anchorstone · Permeator / Workbench / Storage rack / Campfire" (the caption drops out when the name and status don't fit, as before: the Permeator's "Running" row). Device cards: Storage rack ("Anchorstone · Utility Room · 9 slots"), Workbench ("Station · Anchorstone · Utility Room"), Permeator ("Device · Anchorstone · Utility Room"), Campfire ("Resting spot · Anchorstone · Enemy"). Notices say "the storage rack" instead of "the Storeroom's rack".
- **E10** device rows on room cards open the device card: the save-22 code already did this (`InfoPanel.deviceRow` registers the row as a button); your shot 09 was most likely from a build compiled before save 22 (`out\` was recompiled after the shots were taken). Now covered by harness checks that click every device row on all 11 room cards with devices (ours and the camp's) and check the right card opens.
- Tests: **504 pass** (+9: four Anchorstones with the right kind/device/room, every Anchorstone room labelled Utility Room and its device on the room's slot, no other room so named, Pedestal Rooms untouched, the sub-line labels). Bots 0 problems. Harness: +25 PASS lines (E10 rows, E11 label count). Files: `Anchorstone` (new), `Fortress`, `EventScene`, `Shop`, `InfoPanel`, `FortressView`, `LogicTests`, `QolShots`.
- No new numbers.

## Save 26: item 11, one info-page system (E12, E13, E14)
- **`InfoSpec`** (new, `fateweavers.logic`): the page of anything with stats as plain data: name, kind line, attunement, rarity, operator (+ the word for them: operator / reinforcer / weaver / caster / gunner / patient / crafter / rester), five `Tile`s (label, value, sub-line, `Accent`), scalings, match, boost / active / inactive lines, `Art`, extra `rows` (`StatSheet.Row`) and `notes`. **`InfoSpec.of(thing, fortress)`** builds it for: gems and the Glass Gem, our mount, the three orbs, the Loom, doors and the gate, the enemies' Glass Orb / Glass Gem / ballista, the Workbench, the Permeator, the Ward (pass its Room), the campfire (its Anchorstone; the other Anchorstones hand over to their device), blobs (the whole `StatSheet` as rows) and classes (`ClassNode` or `CrewClass`: path, tier, star, second, leads-to, the 8-attribute ranking as rows). Null for things without stats (the rack, plain rooms).
- **`BookPage.open(thing)`** draws any spec with the one Forge layout (insignia by `Art`, tiles by `Accent`, "WHAT THE <word> ADDS" with "Nothing scales it yet" when nothing does, the attunement panel, then the notes and rows underneath). The old `weaponSpec / orbSpec / loomSpec` moved into `InfoSpec` unchanged in content; `DeviceSpec` is gone. The blob ledger (`openStats`) reads its rows through the same builder. `openClass(node)` exists for the class page (no card opens it yet: the class card is R5).
- **E12** the Door card (and the Gate) has the book; **E14** Enemy Mount / Enemy Pedestal / Enemy Glass Gem cards have the book and pages; also Workbench, Ward, Permeator and Campfire. Cards call one action, `openBook(thing)` (replaces openWeaponBook / openOrbBook / openLoomBook). The camp's pages show only what really scales (the bandit's Psyche for bars, the caster's Dexterity for the gem; the camp's ballista: nothing).
- Tests: **518 pass** (+14: every listed thing has a page, pages well formed, none for the rack / a plain room / junk, an Anchorstone's page is its device's, the Door page counts reinforcers and shows the reinforced max, the gate's x3, the three enemy pages' numbers, a blob's rows = its StatSheet, the Magic User's / Heavy Melee's / Unclassed's class pages). Bots 0 problems. Harness: +13 PASS (every card with STATS opens its page from the book icon; a class page opens); shots `r1_page_{door,enemy_mount,enemy_pedestal,workbench,ward,class}`.
- Files: `InfoSpec` (new), `StatRules` (`DOOR`, `WARD` scalings, presentation only), `Fortress` (`classTree()`), `BookPage`, `InfoPanel`, `FortressView`, `LogicTests`, `QolShots`.
- No new numbers (the pages read existing constants).

## Save 27: items 12–14, the Unclassed rename (E18), the T5 fog check (E19), text drift
- **E18** "Unclassed Survivor" → **"Unclassed"** in the master `class_tree/fateweavers_class_tree.py` (`add(..., keep_id=...)`); JSON, SQL and DB regenerated (only the root's name changed, 551 nodes). The id stays `T0_UNCLASSED_SURVIVOR` as the plan said, so `CrewClass.UNCLASSED.treeId`, `class_glyphs.txt`, `class_blobs.txt` and every id-keyed file are untouched. A fresh blob's class now reads "Unclassed" everywhere the tree supplies the name (the crew card, the tree card).
- **E19** checked, no fix needed: a capped Lightning Mage's revealed T5s sit in a clear fog hole at max (5x), home (1.25x) and the smallest (0.12x) zoom. The harness renders all three and samples the pixels round the T5 node (`r1_tree_t5_fog_{max,home,min}.png`). I tried a minimum hole size and took it out again: the hole was already open. (Shot 20's overlapping T5 nodes are E24, the class tree segment.)
- **Text drift**: `Stat.java` Dexterity trains "working a weapon: the gem's pedestal or the ballista's mount" (was "the Weapons Tower"), Psyche trains "working the shield pedestal, and landing mental hits" (was "not decided yet"), Resolve does "Willpower (nothing yet: doors are reinforced by Strength since 25 Oct)" (was "Holding doors"); `Progression`'s comment and a `Fortress` comment now say 30 × n / the real station names. These are the rules as built (`stat_system.md` §1); R2 rewrites them again with the new attribute list.
- Tests: 518 pass, bots 0 problems; harness +7 PASS (root named Unclassed with the old id, a fresh blob reads Unclassed, a capped T4's two T5s unlocked and clear at three zooms); shots `r1_tree_card_unclassed`, `r1_tree_t5_fog_*`.
- Files: `class_tree/fateweavers_class_tree.{py,json,sql}`, `class_tree/fateweavers.db`, `Stat`, `Progression`, `Fortress` (comment), `QolShots`.
- No new numbers.

## R1 done (save 27): where things stand
- **Build:** Desktop save 27. `test.bat`: **518 checks**, every shot renders, the harness prints "R1 checks: all passed" (≈110 PASS lines). `bots.bat`: 40 games, 0 problems. Shots: `handoff_images\2026-10-26_R1\` (19 files + `_INDEX.md`).
- **Items 1–15 of the R1 plan are built** (E1–E7, the placement rule, Anchorstone / Utility Rooms, E10–E14, E18, E19 checked, text drift, docs). Not touched: R1 §6 attribute icons (own thread), the class card (R5), anything in R2. The stretch (R4 §6 step 1, the neutral camp) was not started.
- **Placeholders added this session** (all `// PLACEHOLDER`): `CrewRenderer.HIT_MARGIN` 2 px (how far outside a blob's body a click still counts); `FortressView.LOOM_DROP` 6 px (the Loom drawn below its room's centre); `Room.STATION_GAP` 30 px and `Room.STATION_DROP` 4 px (the station spot beside its device).
- **Calls I made that you may want to change:** the Battlement's two bandits both stand right of their device (the rule as written; §4 of the R1 handoff asked); camp devices outline on hover like ours (a hitbox is hover + click); device rows on room cards say "Anchorstone · …" and the device cards' sub-lines say "Anchorstone · Utility Room"; the Permeator's device row drops the caption because "Running" leaves no room (the existing rule); the enemy mount's page shows no scalings because nothing scales the camp's ballista.
- **Open ❓ (unchanged from save 22):** the Loom losing its weave when the weaver leaves; the camp's Glass Gem mana reserve; bolt resupply; shop prices, shard rewards, boss numbers; the wand (💡 Q37).
- **Not done on purpose:** the project's `Fateweavers/...` code copy is still save 22 (70 files; say if you want it synced). The `# TO DELETE (1–15)` files are still yours to delete by hand.

# R2 session (26 Oct 2026, late): the power system
Builds `roadmaps/R2_power_system.md` in the order of its §5, after R1 (save 27). You were away for this session: everything that needs your input is in `roadmaps/R2_attunement_table_T2-T3_DRAFT.md` (the hand table) and the "Questions for you" block at the end. Shots: Desktop `handoff_images\2026-10-26_R2\`.

## Save 28: R2 step 1, attributes, the curve, the cap, the pools
- **Eleven attributes** (F8 order): Strength, Vitality, Dexterity, **Perception**, Arcana, **Faith**, Psyche, Knowledge, Resolve, Charisma, Luck. `Stat.PERSONAL` is ten (no Luck). Every ranking has ten entries: PER / FAI placed as F10's placeholders (Magic FAI 3rd / PER 9th, Ranged PER 1st / DEX 2nd, Supporter FAI 4th, Heavy and Light PER 5th / FAI 9th; PLACEHOLDER). `CrewClass.RANKING_OVERRIDES` + `rankingFor(node)` exist for the bridge classes (filled in step 3).
- **The curve** (F1 pace, 💡 formula as PLACEHOLDER): level n costs `24 + 2n` XP (level 5 at 150 XP = 2.5 min, 20 at 900 = 15 min, 40 at 2,600 = 43 min at 1 XP/s); attribute point n costs `20 + 2n` XP. `Progression.levelXpTotal`.
- **The cap is the cap** (Q47 A): `CrewMember.stat()` never passes the tier cap for your crew (enemies uncapped). Free points can't be spent past it (the point stays); class-bonus points that don't fit are **banked** (`CrewMember.banked`) and land on promotion (`Fortress.releaseBanked`). Practice stops at the cap counting gifted points.
- **Pools** (Q52 B, F11): one square-root curve for both bars, `POOL_BASE 3.11 + POOL_PER_SQRT 0.256 × √points` (PLACEHOLDER): fresh crew 4 / 4, 120 points → 6, 280 → 7, 1,200 → 12. Mana points = Arcana + Faith + Psyche, stamina points = Strength + Vitality + Dexterity + Resolve. `Fortress.manaContribution(c)` / `staminaContribution(c)` for the stats book.
- **▲ follows the most practised attribute** (Q49 A): `learningArrows` ranks by `trained`, not the total.
- **Unclassed picks its star** (F2 / G12): `Fortress.chooseStar(c, s)` any time before level 5 (never Luck); the chosen attribute gets the +2 a level; locked from level 5; forgotten on promotion (the class decides). No UI for it yet (step 5).
- **Attribute gate** (F2 / Q50 A): `Fortress.isUnlocked` also needs the class's favourite attribute (`favouriteOf(node)` = its ranking's star) at the cap of the tier below (`attributeGateFor`: 5 / 20 / 40 / 60 / 80); `lockReason(c, n)` says "Needs Arcana 5" etc. for the cards. The Ward has no tier cap (it never had one in code).
- **Luck in the rarity odds**: `Attunements.odds(tier, luck)`: each point over 1 adds `ODDS_PER_LUCK` 0.2 weight to every step above Common (PLACEHOLDER); blob rolls and rerolls pass the blob's Luck.
- **Floors** hook: `Fortress.setFloor(n)` / `xpMultiplier()` = `XP_PER_FLOOR_MULTIPLIER`^floor (1.0 PLACEHOLDER until R6); all XP goes through it.
- Tests: **564 pass** (old-rule tests rewritten to the decisions: 11 attributes, the ten-entry rankings, arrows by practice, cap-aware helpers; +22 new: cap and banking, enemies uncapped, the star pick and lock, the gate both ways with `lockReason`, banked points landing on promotion, the pool curve and contributions, the arrow on practice, Luck in the odds, the floor hook, the curve's first costs). Bots 0 problems; harness all PASS (its gifted-point arrays grew to ten).
- Files: `Stat`, `CrewClass`, `Progression`, `CrewMember`, `Fortress`, `Attunements`, `LogicTests`, `QolShots`, `Shots`.
- Placeholders: `Progression.LEVEL_XP_BASE/PER_LEVEL` 24/2, `STAT_XP_BASE/PER_POINT` 20/2, `XP_PER_FLOOR_MULTIPLIER` 1.0, `STAR_PICK_UNTIL_LEVEL` 5 (decided); `Fortress.POOL_BASE` 3.11 / `POOL_PER_SQRT` 0.256; `Attunements.ODDS_PER_LUCK` 0.2; the PER / FAI ranking positions.

## Save 29: R2 step 2, attunements (the enum, 60% duos, Tier 1 paths, the hand table draft)
- **The enum** (R2 §4): **new** Martial, Range, Command, Primal, Craft, Repair, Transformation, Hex, Brawl, Blade, Bow; **Dancing** (was Bladedance, still a kind of Blade, PLACEHOLDER); **dropped** Thrown, Slashing, Piercing, Bludgeoning, Stabbing, Impaling, Crushing, Cleaving, Pugilism, Archery. Kept: Destruction, Firearms (→ Explosives), Heavy, Armour, Machine and everything else. The duo list is unchanged (Lava, Lightning, Slowness, Metal, Speed, Space, Loom, Door).
- **Damage types and affinities are apart**: `Attunement.craftOf` is gone; a weapon's damage type never matches an attunement any more (a Blade blob needs a Blade-tuned sword, a plain sword gives nothing). `Attunements.partsOf` ignores the type.
- **Duos at 60% a part** (G1): `Attunements.DUO_PART = 0.6` replaces `PARTIAL_MATCH` (kept as a deprecated alias). A duo with only one of its two parts on the other side counts 0.6, **on either side** (Lava blob + Fire gem 0.6; Fire blob + Lava weapon 0.6, was full; Lava blob + a Fire-and-Earth weapon full). The duo book page says x0.6.
- **Tier 1 carries its path's attunement** (Q55 A): `Attunements.PATH_ATTUNEMENT`: Magic **Arcane**, Heavy Melee **Heavy**, Light Melee **Martial**, Ranged **Range**, Supporter **Knowledge**. Unclassed stays Neutral. Because `of()` inherits from the parent when nothing of its own fits, **no class past Unclassed is Neutral any more** (the 47 fall-throughs are gone: the "genes" rule). The Arcane rule needs no code: an element word wins outright, so Fire Mage = Fire, Arcane Wizard = Arcane.
- **The keyword fallback** was remapped so it compiles and makes sense (all PLACEHOLDER, listed in the draft doc): Slashing / Stabbing / Cleaving words → Blade, Pugilism → Brawl, Crushing / Bludgeoning → Heavy, Archery → Bow, Thrown → Range, Impaling / Piercing words → no row (inherit); new rows for Hex (curse and hex left Dark), Dancing, Martial, Command, Primal (primal and neanderthal left Beast), Craft, Repair, Transformation. Overrides re-pointed: the Fletchers → Bow; Tranquilizer, Blowgun Specialist, Harpooner, Reeler → Range; Executioner → Blade + Death; Spellsword / Spellblade → Arcane + Blade; Bowling Bob → Range + Heavy; Swashbuckler → Dancing + Pirate.
- **The hand table** for Tier 1–3 (125 rows, the rule, the ❓s): `roadmaps/R2_attunement_table_T2-T3_DRAFT.md` in the project. **Not built** (it needs your OK first); the game runs on the fallback until then.
- **The ballista** was Piercing-attuned: Piercing is a damage type only now, so our `Mount` and the camp's (`BanditCamp.MOUNT_ATTUNEMENT`) carry **no attunement** for now (PLACEHOLDER ❓, question 4 in the draft). The stamina pips and the mount's charge bar used Piercing's colour; they keep the exact colour as `Theme.STAMINA`.
- UI: `Theme.BASES` colours for the new attunements (PLACEHOLDER; Blade / Brawl / Range / Bow / Command took the dropped ones' colours); new attunements draw as their first letter in the icon slot (the existing fallback) until R1 §6's icon thread gives them one. The "ALSO matches any slashing weapon" book lines are gone.
- Tests: **570 pass** (the matching block rewritten to the new rules: 60% both ways, damage types ignored, Blade / Brawl in place of the crafts; the tree block now checks Unclassed is Neutral, every T1 carries its path's attunement, nothing deeper is Neutral, Sword of Fire = Fire + Blade; Dancing / Explosives parents; Arcane not in Balance's element set). Bots 0 problems; harness all PASS. Shot `02_step2_crew_card_fire_blade.png`.
- Files: `Attunement`, `Attunements`, `Mount`, `BanditCamp`, `Theme`, `AttunementIcons` (none: the letter fallback already covered it), `BookPage`, `FortressView`, `LogicTests`, `Shots`.
- Placeholders: `Attunements.DUO_PART` 0.6 (decided); the keyword remaps; `Theme.BASES` colours for Martial / Command / Primal / Craft / Repair / Transformation / Hex; Dancing as a kind of Blade; the Mount's missing attunement.

## Save 30: R2 step 3, the bridge classes' own rankings (Q53 A)
- **`CrewClass.RANKING_OVERRIDES`** (one class) and **`RANKING_LINES`** (a class and everything below it) hold the 31 classes + 3 lines of `class_connections.md` §6. **Shape (PLACEHOLDER):** the path's star stays the star (the attribute gate reads it; "Spellsword needs Strength" is a second gate for later, ❓ R2 §6), the attributes §6 says the class wants come straight after it in the order §6 lists them, the rest keep the path order (`CrewClass.lift`). The Marksman "behaves like Ranged" and takes the whole Ranged ranking. Lines: the Spellblade line (STR, DEX: Spellblade, Spellsword, Rune-Blade, Spell-Eater, the Swords), the Evade Tank line (DEX: Evade Tank, Guru, Parry Lord, Matador), the Demolitionist line (STR: Demolitionist, Razer, World Eater, Pruner). §6's "luck classes" are a 💡 and are not built (Luck stays out of every ranking).
- **`CrewClass.rankingFor(node)`**: its own override, else the nearest line above it, else the path's. `hasOwnRanking(node)` for the cards.
- **A blob carries its ranking** (`CrewMember.ranking`, set by the constructor, `setCrewClass` and `promote`): `star()` and the new `second()` read it, so the level-up bonus (+2 / +1), the gold star on the crew card and the learning-arrow tie-break all follow the override. The class page (`InfoSpec`) shows the class's own STAR / SECOND / ranking rows and says "A bridge class: its own ranking, not Magic's." `CrewClass.strongest()` / `secondStrongest()` stay for the path families.
- **Test Mode's `setCrewClass`** now gives a Tier 1 blob its path's attunement (it set Neutral).
- Tests: **583 pass** (+13: the Spellblade line's order and inheritance, Leader, Marksman, Fire Mage unchanged, the two other lines, the entry count, the gate still reads the star, a promoted Spellsword gifts +1 Strength a level for 20 levels and nothing to Psyche, the tie-break; a Test-Mode Magic User is Arcane). Bots 0 problems; harness +2 PASS (the Spellsword page's tiles and note); shot `03_step3_class_page_spellsword_bridge_ranking.png`.
- Files: `CrewClass`, `CrewMember`, `Fortress`, `InfoSpec`, `InfoPanel`, `LogicTests`, `QolShots`, `Shots`.
- Drift seen, not changed: the class page's STAR tile and ranking row say "+2 a level, +25% learning", but the star itself gives no learning bonus (v4.1: the +25% is the green arrow's, which the gold star only marks when it sits on the same attribute). Say which wording you want.

## Save 31: R2 step 4, Soul and True damage, Resolve / Charisma / Luck jobs
- **`DamageType.SOUL` and `TRUE`** (R2 §4): `isMental()` = Mental or Soul; `ignoresArmour()` = True. **`Armour`** has Soul and True columns: Soul reads like Mental (Mana armour halves it, PLACEHOLDER), True is 1.0 everywhere. **`Fortress.applyHit`**: True damage skips armour, resist / weakness and mental defence (✅ G9); Mental and Soul hits are softened by the target's **Resolve** (`StatRules.mentalDefence`: -0.5% a point, at most -50%, PLACEHOLDER) and, landing, train the attacker's Psyche (Soul now too). **Every shield blocks True** (`ShieldConduit.blocks`: "shields still block it"); **no shield blocks Soul** yet (❓ question 9 below). The orb cards' Blocks row shows the new chips (Soul an eye, True a cross; PLACEHOLDER icons). No weapon deals Soul or True yet: that is R3's crafted weapons (Soul scales with Psyche + Perception, True with Faith when they exist).
- **Resolve breaks doors faster** (`StatRules.doorBreakSpeed`: +2% a point over the first, PLACEHOLDER) in `updateDoorAttacks`; fear and taunt resistance wait for fear and taunts (nothing to resist yet).
- **Charisma talks the shop down** (`StatRules.shopDiscount`: the crew's best Charisma, -1% a point over the first, at most -50%, PLACEHOLDER): wares and the hire cost, not repairs (`Shop.discount`, refreshed every update and before a sale; the card's prices read it). "Who talks" is the crew's best (PLACEHOLDER: nobody is at the counter yet).
- **Luck crits in melee** (`StatRules.meleeCritChance`: +0.1% a point like weapons, PLACEHOLDER; x2, a CRIT event at the blob). Dodge, weapon crits, rarity odds and the skill rolls (+0.5% a point) were already there.
- Not touched (nothing to hook yet): Perception's range on blob-to-blob ranged weapons (P4), Faith's healing (❓ the Ward is Vitality's today: question 10), Madness / Morale, the Stage taunt, Luck on shop stock.
- Tests: **605 pass** (+22: the two types' flags, armour columns, True through armour + resistance + Resolve, mental defence and its cap, Soul softened, Magic untouched by Resolve, shields and True / Soul, door-breaking speed, melee crit chance, the discount at Charisma 1 / 21 / 100 with the cap, repairs full price). Bots 0 problems; harness all PASS. Shot `04_step4_glass_orb_blocks_true.png`.
- Files: `DamageType`, `Armour`, `Hit` (comment), `StatRules`, `ShieldConduit`, `Shop`, `Fortress`, `InfoCard`, `LogicTests`.
- Placeholders: `StatRules.MENTAL_DEFENCE_PER_RESOLVE` 0.005 / `MENTAL_DEFENCE_MAX` 0.5, `DOOR_BREAK_PER_RESOLVE` 0.02, `SHOP_DISCOUNT_PER_CHARISMA` 0.01 / `SHOP_DISCOUNT_MAX` 0.5, `MELEE_CRIT_PER_LUCK` 0.001, the Armour Soul column, the two chip icons.

## Save 32: R2 step 5, rarity on blobs only, the stats book's new rows, the Unclassed star pick
- **Rarity rolls are for blobs only** (Q57 A, G16): the Fire Gem, the ballista, the three orbs, every door and the gate, the Loom, bought and crafted devices, and the camp's shield / gem / mount / campfire roll nothing any more. Their `rarity` fields stay (always null; the cards' and pages' rarity branches were already null-safe), the weapon / Loom / door rarity boosts to a match are gone (the blob's own rarity sets the power: Rare blob on the Fire Gem = x2, was x3.5 with an Epic gem), the "Attunement Synergy" tags and the boost lines on the pages say so. The game's seed stream is unchanged (the old rarity seed draw is still taken, so every seeded game plays the same). The crafted-orb notice drops the "(Rare)".
- **Stats book** (`StatSheet`): new group **Mind** (Mental and Soul damage taken: Resolve; mana pool points; stamina pool points), Melee **Crit chance** (Luck), **Door-breaking speed** (Resolve), Work **Shop discount** (Charisma). Lower-is-better rows count a drop as a buff.
- **The Unclassed star pick** (F2 / G12) on the crew card: while a blob is Unclassed and under level 5, every attribute but Luck shows an empty star slot after its name; hovering fills it gold, clicking it calls `Fortress.chooseStar`; the picked attribute wears the star (black, or gold when it is also the most practised, the existing rule); the caption above the rows says "Pick a star: +2 a level" and then "Star set, free until Lv 5". The slots vanish at level 5 (locked) and on promotion (the class decides).
- `Stat.java`: Psyche trains from "landing Mental or Soul hits"; Luck also "a small bonus on every skill roll" (both already true in code).
- Tests: **613 pass** (+8: no device rolls anywhere, the match without a weapon boost, the crafted orb, the new sheet rows and the Resolve row's direction). Bots 0 problems; harness +4 PASS (fresh blob has no star; ten slots; a click picks Arcana; locked at level 5); shot `05_step5_unclassed_star_pick.png`.
- Files: `Fortress`, `Attunements`, `Weapon`, `Door`, `ShieldConduit`, `Loom`, `BanditCamp`, `InfoSpec`, `StatSheet`, `Stat`, `InfoPanel`, `LogicTests`, `QolShots`.
- No new numbers.

## R2 done (save 32): where things stand
- **Build:** Desktop save 32. `test.bat`: **613 checks**, every shot renders, the harness prints "R1 checks: all passed" (its R2 lines are PASS too). `bots.bat`: 40 games, 0 problems. Shots: `handoff_images\2026-10-26_R2\` (5 files + `_INDEX.md`).
- **Built (R2 §5 steps 1–6):** eleven attributes, the curve, the cap holds gifted points (banking), pools with diminishing returns, Luck in the rarity odds, the floors × XP hook (save 28); the attunement enum rebuilt, 60% duos, Tier 1 path attunements, damage types apart from affinities, the keyword fallback remapped (save 29); the bridge classes' rankings and blobs carrying their ranking (save 30); Soul and True damage, armour columns, Resolve's mental defence and door-breaking speed, Charisma's shop discount, Luck's melee crit (save 31); rarity on blobs only, the stats book's new rows, the Unclassed star pick, `Stat.java` strings (save 32); `design/systems/stat_system.md` **v7** in the project.
- **Not built, by design:** the **T2–T3 attunement hand table** (drafted in `roadmaps/R2_attunement_table_T2-T3_DRAFT.md`, needs your OK, then it goes into `Attunements.OVERRIDES` by id); the reroll-on-every-level-up with essence (R3 §5); the second gated attribute (R2 §6 ❓); class perks beyond the two that exist; anything that deals Soul or True damage (R3's weapons); Perception's range (P4); Faith's healing (❓ below).
- **Placeholders added in R2** (all `// PLACEHOLDER`): `Progression.LEVEL_XP_BASE/PER_LEVEL` 24/2, `STAT_XP_BASE/PER_POINT` 20/2, `XP_PER_FLOOR_MULTIPLIER` 1.0; `Fortress.POOL_BASE` 3.11 / `POOL_PER_SQRT` 0.256; `Attunements.ODDS_PER_LUCK` 0.2, `DUO_PART` 0.6 (decided); the PER / FAI ranking positions; the keyword remaps; `Theme.BASES` colours for the new attunements; Dancing as a kind of Blade; the bridge rankings' shape (star stays, wanted attributes next); `StatRules.MENTAL_DEFENCE_PER_RESOLVE` 0.005 / `MENTAL_DEFENCE_MAX` 0.5, `DOOR_BREAK_PER_RESOLVE` 0.02, `SHOP_DISCOUNT_PER_CHARISMA` 0.01 / `SHOP_DISCOUNT_MAX` 0.5, `MELEE_CRIT_PER_LUCK` 0.001; the Armour Soul column; the Soul / True chip icons; the Mount's missing attunement.
- **Calls I made that you may want to change:** a duo's "each part at 60%" is built as *one part across = 0.6, both = 1.0, on either side*; the keyword fallback's remaps (Blade for every sword / axe / dagger word, Heavy for hammers and clubs, Range for thrown and darts, no row for spears); Novice Elementalist stays Balance in the fallback until the table says Arcane; the Mount carries no attunement; the bridge rankings keep the path's star; the shop discount reads the crew's best Charisma; no shield blocks Soul; the Unclassed star slots show on every attribute but Luck.

## Questions for you (R2)
Answer these with the draft table's own ❓ list (`roadmaps/R2_attunement_table_T2-T3_DRAFT.md`, 8 questions: Dark / Holy and the Arcane rule, Novice Elementalist, the five unsure rows, the ballista's attunement, Dancing's parent, Arcane and Balance, the 60% reading, the Neutral easter egg).
9. **Soul damage and shields:** no shield blocks Soul today. Should the Mana shield (it blocks Mental) block Soul too?
10. **Faith and healing:** R2 §1 gives Faith "healing" and keeps Vitality's "Ward healing (as now)". Is Faith's healing only healing *others* (not built) or should the Ward read Faith as well as Vitality?
11. **Who haggles:** the shop discount reads the crew's best Charisma wherever they stand. Should it be the blob you send to the shop instead (R4's dialogue)?
12. **Rarity fields:** devices keep a `rarity` field that is always null (the cards' rim code reads it). Say if you want the fields and the rim code removed outright.
13. **The class page's STAR tile** says "+2 a level, +25% learning"; the +25% belongs to the green arrow, which the gold star only marks when it sits on the same attribute (v4.1). Keep the wording or change it to "+2 a level"?

# R4 session (26 Oct 2026, same night): enemies and encounters
Builds `roadmaps/R4_enemies_encounters.md` in the order of its §6, after R2 (save 32). Shots: Desktop `handoff_images\2026-10-26_R4\`.

## Save 33: R4 §6 step 1, the neutral camp, the aggression triggers, the sleep rule (Q58 A, F14, F15)
- **The camp starts neutral** (`BanditCamp.hostile` false; the header and the camp card say "Neutral"). The band mans its stations (gunner on the mount, caster on the gem) and watches: nothing fires, the unmanned-boarding timer doesn't run.
- **Aggression** (`Fortress.provokeCamp(why)`, once): **you powering a weapon or mount** (`setWeaponPowered(w, true)` as an act: the Fire Gem being powered from the start is the game's setup, not an act, so the camp stays neutral until you touch something), **firing** (`launch`), **a blob of yours entering No Man's Land** (the door crossing into either half). Raising a shield doesn't count (nothing a shield does deals damage). The banner "THE CAMP TURNS HOSTILE!" (`GameEvent.Type.CAMP_HOSTILE`), a notice naming the cause ("The bandits saw you powering the Fire Gem."), then the old behaviour: first bolt 5 s later, boarding when nobody can shoot, the lot. `camp.provokedBy()` keeps the cause for cards.
- **A new band is neutral too** (`resetCamp`): PLACEHOLDER call, §1 only says "the first camp". A Fire Gem left on AUTO provokes the new band with its next shot, which reads right.
- **The sleep rule** (`Fortress.hostilesRemain()`, `sleepBlockedReason()`): `sleep()` refuses while hostiles remain (a notice says so). At a camp node the band counts only once hostile (a neutral camp is no hostiles: you can sleep at once, F15); everywhere else every living enemy counts (the shop's guards once you rob it, the Goblin Boss and his goblins). The Loom's status reads "Woven, but hostiles remain"; both Loom cards grey the SLEEP button and say why above it. The Loom already lost its weave when the weaver left (C11 was built with the station rule).
- **Bots:** the AFK bot now survives untouched (the camp never fires at a fortress that does nothing); turtle / rush / test-mode bots still clear camps (they power and fire). 40 games, 0 problems.
- Tests: **629 pass** (+16: starts neutral and counts as no hostiles; 12 s with the start-powered gem = still neutral, no bolt, no boarding; the shield is not aggression; unpowering isn't; powering is, with the cause; a woven Loom can't sleep until every enemy is dead; walking into No Man's Land provokes; aiming doesn't, firing does; the new band is neutral; the shop's guards block sleep; the Battlement test now checks "manned but silent" before provoking; the boarding test provokes first). Harness +3 PASS; shots `01–03` in `handoff_images\2026-10-26_R4\`.
- Files: `BanditCamp`, `Fortress`, `GameEvent`, `FortressView`, `InfoPanel`, `LogicTests`, `QolShots`.
- Placeholders: none numeric; two calls flagged (the start-powered gem isn't an act; new bands start neutral).

# Your answers (26 Oct, ~01:50) and what they built
All recorded in `roadmaps/archive/2026-10-26_answers_R2_table_R4_controls.md`. Every R2 question and the R4 controls are answered; save 34 builds them.

## Save 34: the answers, and R4 §6 step 2 (attack orders)
- **The T2–T3 hand table is in code** (`Attunements.OVERRIDES`, all 125 rows by class id, approved with your changes): every school counts for the Arcane rule (Novice Dark Mage = Dark, Vitality Mage = Restoration, Spirit Mage = Spirit; Arcane Wizard keeps Arcane), Novice Elementalist = Arcane + Balance, Evade Tank = **Deflection** + Dancing, Pathfinder = Fate + Martial, Vanguard Scout = **Speed** + Command, Goblin = Outlaw + Primal, Soldier = Firearms + **Order**. Justicar = Holy + Order (by name). The keyword fallback still covers T4–T5.
- **New attunements:** **Order** (replaces Law; its keywords too) and **Deflection** (parrying and evading; keywords parry / evade / deflect / riposte / dodge; Matador reads Dancing). **Order and Chaos are opposites** (x0.5 from their own, x1.5 from the other; PLACEHOLDER like the other pairs). **Speed** and **Dancing** are plain now (not a duo / not a kind of Blade). PLACEHOLDER colours for Speed, Dancing, Deflection.
- **Duos:** each matched part adds 0.6, so **both parts = x1.2** (Lava blob + Lava weapon, a Weaver on the Loom). One part = x0.6; a plain partial match stays x0.6; a plain full match x1.
- **Both ballistas are Range.** **The Mana shield blocks Soul.** **The Ward reads Faith** (+2% healing a point, `StatRules.HEAL_PER_FAITH`, PLACEHOLDER; the Ward page and the stats book say so). **The shop's haggler is the crew member standing in the shop** with the most Charisma (`Fortress.haggler()`); nobody in = full price; the shop card says who haggles and the discount. **STAR tile:** "+2 a level". **Device rarity fields removed** (weapons, orbs, doors, the Loom, the camp's devices; rims fall back to the device's own colour; the bought / crafted item names drop the rarity word).
- **R4 §6 step 2 (G11), the half that doesn't wait on weapons:** **your crew no longer attack on their own.** With a blob selected, **right-click an enemy = attack it** (it walks to the enemy's room, fights only that enemy, follows it if it leaves; the target flashes red for a second), **right-click an enemy device = wreck its room** (a bar every 5 s like boarders, and it provokes the camp), **right-click a tile = move** (cancels the order). Left-click still inspects. Enemies still attack whoever is in their room. **The single-tile green outline under the mouse is gone** (`FortressView.SHOW_HOVER_TILE` false); room, station, device and blob outlines stay.
- Not built from step 2: ranged blobs attacking from anywhere in the room and outdoor cross-room fire (they need blob ranged weapons, R3, and the Ranger Platform); the melee-engagement ❓ stays open.
- **Bots** give attack orders now (a `fightBack` helper; fuzz has attack and wreck actions): AFK 4 alive, turtle clears the camp, rush and test mode reach node 2. 40 games, 0 problems.
- Tests: **648 pass** (+19: the table rows, Order / Chaos, plain Speed / Dancing, x1.2 duos, Range ballistas, Faith in the Ward, Soul on the Mana shield, the haggler in the shop, no auto-attack, attack orders and their flash, a move cancelling them, wrecking a camp room). Harness +4 PASS; shots `04–05` in `handoff_images\2026-10-26_R4\`.
- Files: `Attunement`, `Attunements`, `Theme`, `StatRules`, `StatSheet`, `InfoSpec`, `ShieldConduit`, `Mount`, `BanditCamp`, `Weapon`, `Door`, `Loom`, `Fortress`, `CrewMember`, `GameEvent`, `FortressView`, `InfoPanel`, `BookPage`, `ItemSlots`, `LogicTests`, `BotPlaytest`, `QolShots`.
- Placeholders: `StatRules.HEAL_PER_FAITH` 0.02; `FortressView.TARGET_FLASH` 1 s (decided: "a second"); the Order / Chaos x0.5 / x1.5; the new colours.

## Save 35: R4 §6 step 3, the gate and the guard (E17, Q43 B)
- **The gate must be broken to pass if you don't control it** (✅ R4 §3), even when it stands open: `Fortress.gateBars(u, gate)` blocks anyone whose side doesn't control No Man's Land; they stop at it and hit it like any door (Resolve speeds them up). A tie (equal strength, or nobody there) means nobody controls it: it stays shut and bars everyone. Standing in No Man's Land counts toward control, so a blob that walks into the west half usually controls it and walks straight through. The gate, No Man's Land and its card texts say so.
- **The camp's guard** (`Fortress.guardGoal`, the `GUARD` role at the camp node): **while the camp is neutral he stays home** (a neutral camp does nothing, so this follows from §1; flagged in case you meant otherwise). **Once hostile** he walks out to the east half of No Man's Land to reinforce it. **Outnumbered where he stands** (more of your blobs there than enemies, him included) he breaks off the fight and falls back to the Camp Entrance, onto the camp door's pad (reinforcing the door from behind), with a notice; he goes out again once he wouldn't be outnumbered. Raiders inside the camp come first: he goes to defend that room. The gunner / caster takeover can still pull him onto the Battlement.
- **Bots:** AFK and test mode as before, rush reaches node 2; **the turtle now loses its fortress at 175 s** (the guard in No Man's Land holds the gate against it, and turtle never leaves its stations). That's the game being harder for a bot that never attacks, not a bug; 40 games, 0 problems.
- Tests: **658 pass** (+10: nobody controls an empty No Man's Land, standing there gives you control, the enemies are barred, an open gate still bars the side that doesn't control it, boarders hit an open gate; the guard stays home while neutral, goes out when hostile, falls back 3-to-1 onto the door pad, goes out again). Harness +2 PASS; shots `06–07`.
- Files: `Fortress`, `CrewMember`, `InfoPanel`, `LogicTests`, `QolShots`.
- No new numbers.

## Save 36: R4 §6 step 4, the Treasure Room (decided 26 Oct)
- **The room:** `CAMP_TREASURE`, "Treasure Room", a 2x2 right of the campfire's Utility Room, its door from that room (`Fortress.treasureDoor()`). The chest stands on its Anchorstone slot (`Anchorstone.Kind.CHEST`); the room keeps the name "Treasure Room" (your answer: an exception to "every Anchorstone room is a Utility Room"). It's part of the camp node, so it goes when you weave on.
- **The door is locked until every enemy is dead** (`Door.locked`): your blobs stop at it and can't break it (a gold padlock on the map and on the door card, "Locked: kill every enemy"). When the camp is cleared it unlocks and stands open; a new band locks it again.
- **The chest works like the storage rack** (`fortress.chest()`, a second `Storeroom`): take, store, drag and drop, click for the obvious move; the rack code now finds the rack in the blob's room (`storageIn(room)`, `rearrange(rack, from, to)`). Its card: "Treasure chest · Treasure Room · locked / open", the chest art (lid shut with a lock, or open with the loot glinting), the 3x3 slots once open; the room card has a chest row.
- **Loot:** conduits only, **2 rolls**, each **25% Glass Gem, 25% Glass Orb, 10% Fire Gem, 10% Stone Orb, else nothing** (`Fortress.CHEST_ROLLS`, `CHEST_*`, PLACEHOLDER; Luck doesn't change them). **Refilled with each new band**; what you didn't take leaves with it (and with the camp when you weave on). What a blob carries out is yours. The chest's items don't count as your weapons or orbs until taken. Its rolls use their own stream, drawn after the rest of the setup, so every seeded game starts the same as before.
- **Right-clicking the chest** moves the selected blob there (it isn't something to wreck).
- Tests: **724 pass** (+66: the room and its place, the chest on its Anchorstone, the door locked and unbreakable while enemies live, nobody reaches the chest, it opens when they're all dead, a blob walks in, takes and stores like the rack, a new band locks and refills it, carried loot stays yours, the odds per roll and over 20,000 rolls, at most 2 per chest over 40 games, the Anchorstone count / names / node size updated). Bots 0 problems; harness +3 PASS; shots `08–10`.
- Files: `Fortress`, `Door`, `Anchorstone`, `InfoSpec`, `DeviceArt`, `FortressView`, `InfoPanel`, `LogicTests`, `QolShots`.
- Placeholders: `CHEST_ROLLS` 2 and the four odds (decided as placeholders); `DeviceArt.BOX_CHEST`.

## Save 37: the T4 attunement table (approved 26 Oct) and four new attunements
- **All 300 T4 classes** now come from the hand table (`Attunements.OVERRIDES` by id, after the T2–T3 block). Rule and rows: `roadmaps/R2_attunement_table_T4_DRAFT.md`. You approved 88 unsure rows through the questionnaire, with fixes. Known mis-hits from review §5.4–§5.5 are fixed: Dwarven Engineer = Earth + Machine, Inquisitor = Holy + Order, Combat Medic = Restoration + Firearms, Matador and Parry Lord carry Deflection, Gardener / Rejuvenator are healers, Mutagenist and Cloner are Transformation, Swarm-Smith = Machine + Craft, Blowgun Sniper = Poison + Range, Ninja = Stealth + Range. The §6 bridge classes are noted in the doc.
- **New attunements:**
  - **Forerunner**, the initiators. Flag Bearer line: Flag Bearer = Forerunner + Command, Warcaller = Command + Forerunner, Dread Herald = Forerunner + Shadow, Well of Hope = Forerunner + Holy. Scout line, T2–T3 only: Scout = Forerunner + Martial, Outrider = Speed + Forerunner, Pathfinder = Fate + Forerunner, Surveyor = Knowledge + Forerunner, Vanguard Scout = Speed + Forerunner.
  - **Charm**: Prince Charming = Charm + Armour, Charmer = Charm + Performer, Harlot = Performer + Charm, Siren = Charm + Water, Succubus = Shadow + Charm.
  - **Stealth**: Ghillie Suit Sniper = Firearms + Stealth, Ninja = Stealth + Range, Spy = Stealth + Mind, Ambusher = Bow + Stealth, Cat Burglar = Outlaw + Stealth.
  - **Growth**: Gardener = Growth + Primal, Growth Mage = Growth + Restoration, Druid = Beast + Growth, and the T5s Unyielding Growth and Size Manipulator = Growth.
  - Each has PLACEHOLDER colours and keyword rows for the fallback; "charm" moved from Mind to Charm.
- **Changed at T2–T3 by these answers:** Scout, Outrider, Pathfinder, Surveyor, Vanguard Scout, Flag Bearer, Harlot and Druid (updated in the T2–T3 block).
- **Two calls I made** (in the T4 doc):
  - Dread Herald: you wrote "shadow, pirate" and later "swap out command for Forerunner". It's built as Forerunner + Shadow.
  - Vanguard Scout: Forerunner replaced Command.
- Tests: **732 pass**. 8 new checks:
  - all 300 T4 classes come from the table;
  - the new rows: Charming, Ghillie, Gardener, Dread Herald;
  - Forerunner, Growth and Charm sit on their T2–T3 classes;
  - the Scout line's T4s keep their own pairs;
  - each new attunement has at least 3 carriers;
  - Vanguard Scout updated.
- Bots: 0 problems; the harness passes.
- Files: `Attunement`, `Attunements`, `Theme`, `LogicTests`.
- Placeholders: the four new colours; the keyword rows.

# A1 session (26 Oct 2026, ~03:10): personal weapons and blob slots
Answers in `roadmaps/archive/2026-10-26_answers_R2_table_R4_controls.md` (the A1 and Finesse blocks).

## Save 38: A1 logic (personal weapons, slots, Finesse)
- **`PersonalWeapon`** (new) with eight kinds. Each has base dps, attack interval, damage type, attunement, two scaling attributes and a shop price, all PLACEHOLDER:

| Weapon | dps | Interval | Type | Attunement | Scaling | Price |
|---|---|---|---|---|---|---|
| Sword | 10 | 1.0 s | Slashing | Blade | STR | 20 |
| Rapier | 8 | 0.6 s | Piercing | **Finesse** + Blade | STR + DEX | 25 |
| Hammer | 12 | 1.4 s | Bludgeoning | Heavy | STR + VIT | 25 |
| Wrecking ball | 16 | 2.0 s | Bludgeoning | Heavy | STR + VIT | 40 |
| Longbow | 8 | 1.2 s | Piercing, ranged | Bow | DEX + STR | 25 |
| Rapid crossbow | 7 | 0.6 s | Piercing, ranged | **Finesse** + Bow | DEX + RES | 30 |
| Spellsword | 10 | 1.0 s | Magic | Arcane + Blade | ARC + STR | 40 |
| Glass Magic Staff | 8 | 1.2 s | Magic, ranged | none (Neutral) | ARC + PSY | 30 |

- **Damage:** dps = base × (1 + 1% per point of each scaling attribute). That is about 2× fists (fists are 5 + 0.25 per STR).
- **Ranged weapons:** they fight from anywhere in their room. Their range (2 tiles + 1 per 10 Perception) is stored and shown but does nothing until cross-room fire exists.
- **Spellsword:** I made it **Magic** damage (the roadmap said "slashing / magic"). Say if you want Slashing.
- **Blob slots:** **Weapon** (`CrewMember.weapon`) sits next to Hands. **Armour and Accessory** slots exist but stay empty for now.
- **Combat:** a blow uses the weapon's type, attunement (for the match bonus), rate and dps. The hit's label is "Cedric's Hammer". Fighting trains the weapon's scaling attributes (fists train Strength).
- **Door breaking:** it uses the weapon's dps, still × 0.4.
- **Enemies:** still use fists.
- **Starting kit:** Aldric Sword, Brenna Longbow, Cedric Hammer, Daria Glass Magic Staff. The Rapier, Rapid crossbow, Spellsword and Wrecking ball start on the storage rack, after the two orbs.
- **Equipping:** only standing in the Storeroom (`canEquip`).
  - `equipFromRack` swaps the weapon with the one in that rack slot.
  - `equipFromHands` takes a weapon from the blob's hands.
  - `unequip` puts it on the rack, and the blob is back to fists.
- **Pedestal spares:** `spares()` now lists conduits only, so weapons on the rack aren't offered to pedestals.
- **Shop:** 2 random weapons in stock, drawn from their own random stream. `buyWeapon` sends the weapon to the rack. Prices are per kind, and Charisma's discount applies.
- **Chest:** each roll is 20% Glass Gem, 20% Glass Orb, 8% Fire Gem, 8% Stone Orb, **20% a random weapon**, 24% nothing.
- **Finesse** (your mid-session request), a new **attunement**:
  - the Rapier and Rapid crossbow are tuned to it (daggers and stilettos too, once crafting adds them);
  - a Finesse weapon adds **+10% crit chance** on top of Luck (`PersonalWeapon.FINESSE_CRIT`, PLACEHOLDER);
  - **Carriers**: Assassin = Finesse + Blade, Master Assassin = Finesse + Blade, Grandmaster Assassin = Finesse + Shadow, Fencer = Finesse + Blade, Bloodletter = Blood + Finesse, Skirmisher = Finesse + Speed, Skypiercer = Finesse + Air, Deadeye = Finesse + Bow, Bullseye = Finesse + Bow, Sniper = Firearms + Finesse;
  - which slot Finesse replaced in each pair is my call;
  - it has keyword rows and a PLACEHOLDER colour.
- **Stats book:** the melee group becomes "Weapon" or "Weapon (ranged)", with the weapon's dps, damage per attack, attack rate and range. The base column is the weapon at every attribute 1.
- Tests: **755 pass**. 23 new checks:
  - the starting kit and the rack;
  - empty slots;
  - the numbers and scaling;
  - the Staff;
  - about 2× fists;
  - rates and range;
  - Finesse weapons and crit;
  - a hammer landing Bludgeoning / Heavy;
  - training;
  - equipping only in the Storeroom, swapping and unequipping;
  - the shop's two weapons, price and purchase;
  - the Finesse carriers.
- Bots: 0 problems. The turtle bot now clears the camp again.
- Files: `PersonalWeapon` (new), `CrewMember`, `StatRules`, `StatSheet`, `Fortress`, `Shop`, `Attunement`, `Attunements`, `Theme`, `LogicTests`.
- **Note:** during this save I accidentally overwrote the workspace copy of `Fortress.java` with a bad shell command. I restored it from save 36's copy and re-applied this session's edits. Every test passes, and the Desktop copy was never touched.

## Save 39: A1 UI (slots, weapon pages, shop) and bots
- **Crew card:** four slots in a row: Hands, Weapon, Armour, Accessory. Under them is a line with the weapon's name, dps and type, plus a book icon that opens its page. A hint says "Change weapons in the Storeroom".
- **Storage rack card:** with blobs in the Storeroom, a "WEAPONS" row shows each blob's weapon slot under its hands.
  - Drag a weapon from the rack onto it to equip (a swap if the blob already holds one).
  - Drag it back onto the rack to unequip.
  - Clicking a slot does the obvious move.
- **Weapon pages:** each weapon has a book page (`InfoSpec.personal`) with DPS, per blow, crit, scales with, range or reach, what the wielder adds, its attunement and notes. Each kind has its own icon (`WeaponArt.drawPersonal`), also used in rack and hand slots.
- **Shop card:** the two weapons in stock appear as "Buy a Sword" and so on, with their price. The dps sits on the weapon's page; it didn't fit on the row.
- **Bots:** the turtle starts with one blob bare-handed, and `armUp` walks it to the Storeroom and equips the first weapon on the rack. If it never gets armed, that counts as a problem. Turtle and testmode arm up; fuzz also equips and unequips at random.
- **Small fix:** the book pages now say "1 point" instead of "1 points".
- **Screenshots:** `handoff_images\2026-10-26_A1\` (4 shots, `_INDEX.md`).
- Tests: 755 pass, "R1 checks: all passed", bots 0 problems.
- Files: `InfoSpec`, `WeaponArt`, `ItemSlots`, `BookPage`, `InfoPanel`, `QolShots`, `BotPlaytest`.


## Save 40: open items, part 1 (your answers of 26 Oct, ~04:00)
Your answers are in `roadmaps/archive/2026-10-26_answers_R2_table_R4_controls.md` (the "Open items" block).
- **Spellsword:** now **Slashing**. Arcane stays in its attunement.
- **Money is "Units"** on screen everywhere: the header, the shop, prices and notices (`Theme.MONEY`, `Theme.money(n)`). The code still calls it `shards`.
- **XP per floor:** × **1.5** (`XP_PER_FLOOR_MULTIPLIER`, PLACEHOLDER). Floors don't exist yet, so this changes nothing in play today.
- **The Ballista** loads in **14 s** (was 7, PLACEHOLDER). The camp's ballista keeps 7 s (`BanditCamp.BALLISTA_INTERVAL`); say if it should slow too.
- **The Scorpion** (`SiegeEngine`, new):
  - 3 damage, 7 s, 0 stamina, the same 2 s climb (PLACEHOLDER).
  - The Goblin Shop sells one for **35** (PLACEHOLDER).
  - When an engine is on the rack (or in the hands of a blob in the Mount Room), the mount card shows **"Fit the Scorpion"**. Fitting it swaps engines: the old one goes where the new one came from, the mount powers off and its charge is lost.
  - Both engines share the mount's bolts.
  - Its art is a small iron-armed thrower with a curled tail; it also draws on the rack.
- **The training hard rule:** using a thing trains every attribute it scales with (Luck never trains).
  - A gem's station now trains Arcana + Dexterity.
  - The mount trains Strength + Dexterity.
  - The Loom trains Arcana + Knowledge.
  - The Ward trains Vitality + Faith (resting still gives no level XP).
  - A weapon in a fight trains its scalings (as before).
- **New training sources:**
  - **Perception** from lookout in No Man's Land (with Resolve).
  - **Charisma** for the haggler, 0.5 XP per Unit spent on wares or hiring (PLACEHOLDER).
  - **Luck: +1 to the whole crew** on every node woven to.
- **Healing devices scale with Faith:** the campfire now heals at 3/s × the enemy's Vitality + Faith multiplier, like the Ward.
- **Attribute descriptions** (`Stat.java`) list the new training sources.
- Tests: **769 pass**, with new checks for:
  - the Scorpion's numbers, fitting and swapping back;
  - the shop's Scorpion;
  - the training rule (gem, Loom, Ward, No Man's Land);
  - Charisma from buying, and Luck per node.
  - Two Sage tests now set Luck back to 1, because the +1 per node moved their odds.
- Shots: `test/open_mount_fit_scorpion.png`, `test/open_mount_scorpion_fitted.png`.
- Files: `SiegeEngine` (new), `Mount`, `Weapon`, `Shop`, `Fortress`, `StatRules`, `PersonalWeapon`, `Progression`, `Stat`, `InfoSpec`, `Theme`, `FortressView`, `InfoPanel`, `DeviceArt`, `ItemSlots`, `BookPage`, `LogicTests`, `QolShots`.

## Save 41: open items, part 2 (automatic duos, visuals)
- **Automatic duos** (your answer): a class with two plain attunements carries them as **one duo** (`CrewMember.carriesDuo()`).
  - **One rarity and one points pool,** both kept under the first attunement (`poolKey`). On promotion the two pools are summed, and the rarity of the one you kept is used (otherwise the first one's roll).
  - **Matching:** each part found on the weapon adds 0.6, so both parts give **×1.2** (it was ×1.0) and one part still gives ×0.6. Balance still half-matches elements as before.
  - **The crew card** shows one **"DUO ATTUNEMENT"** card: a yin-yang of the two and the name "Fire + Blade" (`AttunementIcons.drawDuo(a, b)`).
  - Fixed duos (Lava and the rest) are unchanged. A class that already holds a fixed duo plus another attunement doesn't fuse them.
- **Ward flair:** each patient still mending gets a breathing green ring under its feet and green "+" crosses rising off it. This sits on top of the existing room glow.
- **Level-up flair:** a white-gold flash and an eight-point star burst at the start, on top of the rings, light column, sparks, chevrons and the "Level n!" label.
- **Gate text:** on the class tree, each locked next class says why under its ring ("Needs level 5", "Needs Arcana 5"). The class panel shows a second progress bar for the favourite attribute's gate, and says what's missing.
- **Attunement pages** gain a **CARRIED BY** row: how many classes carry it and their names (the first 14, then "and n more"). The "What it does" card grows to fit wrapped rows.
- Tests: **772 pass** (new checks: duo matching ×1.2 / ×0.6, the shared rarity and pool). The old "Fire + Blade = full" check now expects ×1.2.
- Screenshots: `handoff_images\2026-10-26_open_items\` (see `_INDEX.md`).
- Files: `CrewMember`, `Attunements`, `Fortress`, `AttunementIcons`, `InfoPanel`, `FortressView`, `ClassTreeView`, `ClassTreePanel`, `BookPage`, `LogicTests`, `QolShots`.

## Save 42: A2, ranged combat across rooms (R4 §3's ranged half)
Your answers (26 Oct, ~04:50) are in the answers doc ("A2" block).
- **Outdoor rooms** (`Room.isOutdoor`): No Man's Land, the camp and the boss arena. Your fortress, the shop and the Sage's hut are indoor.
- **Shooting across rooms** (`Fortress.canShoot`):
  - It needs a ranged weapon (Longbow, Rapid crossbow, Glass Magic Staff).
  - The target must be in the same room, or both rooms must be outdoor and the target within range. Range is the straight-line distance in tiles, from shooter to target.
  - Indoor rooms can't shoot out or in. Melee weapons never cross rooms.
- **Range:** **2 + 0.8 × √(Perception − 1)** tiles, so diminishing returns (`PersonalWeapon.rangeFor`, PLACEHOLDER): Perception 1 gives 2 tiles, 5 gives 3.6, 20 gives 5.5, 40 gives 7 and 100 gives 10.
- **No misses** beyond the target's usual Luck dodge. Shots never stray onto another blob.
- **Orders:**
  - Right-click an enemy with a ranged blob selected. If it can shoot from where it stands, it stays put and shoots.
  - Otherwise it walks toward the target and **stops at the first free spot in range** (`shootingPad`) in an outdoor room. If there isn't one, it keeps closing in, as melee does.
  - It never walks into the camp just to shoot.
- **Enemies** keep their fists (your answer), so ranged blobs can pick off a camp from No Man's Land.
- **On screen:**
  - **A selected ranged blob standing outdoors shows a dashed range ring**, labelled "2.0 tiles".
  - **Shots fly per weapon:** a red-fletched arrow (Longbow), a short dark bolt (Rapid crossbow) or a glass-blue spark with a trail (Staff), each on a slight lob (`RANGED_SHOT` event).
  - The weapon page's RANGE tile and notes explain the rule.
- **Stats book:** the Range row uses the new curve.
- **Bots:** rush-bot ranged blobs now shoot from No Man's Land (16 shots across rooms in the rush game; it reached node 4). The bot log prints the count.
- Tests: **784 pass**, with new checks for:
  - which rooms are outdoor;
  - in range / out of range;
  - a sword can't cross rooms; indoors can't shoot out;
  - ordered in range she stays put, and the arrow lands across rooms (a RANGED_SHOT event);
  - out of range she sets off and stops in No Man's Land;
  - the range curve (2, 3.6, 10).
- Shots: `handoff_images\2026-10-26_A2\` (2, `_INDEX.md`).
- Files: `Room`, `GameEvent`, `Fortress`, `PersonalWeapon`, `StatSheet`, `InfoSpec`, `FortressView`, `LogicTests`, `QolShots`, `BotPlaytest`.

## Save 43: one attunement per thing (duos everywhere)
Your rule (26 Oct, ~05:00): **anything has exactly one attunement; if it has two, that is one duo attunement.**
- **Classes:** every class with two attunements carries them as one duo (one rarity, one points pool, one card). That now includes the **14 classes that pair a fixed duo with another attunement**. Each becomes a duo of the two (your answer), with the fixed duo as one half:
  - Smith and Forgesmith Ascendant = Metal + Craft;
  - Railgunner = Lightning + Firearms;
  - Tranquilizer = Slowness + Range;
  - Stridewalker = Speed + Space;
  - Shadowjumper = Shadow + Space;
  - Iron Fist = Metal + Ki;
  - Storm Maiden and Storm-Herald = Lightning + Norse;
  - Dwarven Blacksmith = Earth + Metal;
  - Furnace Lord and Forge Master = Fire + Metal;
  - The Iron Gate = Metal + Bastion;
  - Overclocker = Lightning + Machine.
- **Weapons and devices** with two attunements are duos too: Rapier = Finesse + Blade, Rapid crossbow = Finesse + Bow, Spellsword = Arcane + Blade. Their pages say **DUO ATTUNEMENT · "Finesse + Blade"**, and the "if matched" line reads "each half ×0.6, both ×1.2".
- **Matching** (`Attunements.halves` / `covered`):
  - A duo's halves are its two attunements; a lone fixed duo (Lava) has its two parts as halves.
  - Each half the other side covers adds ×0.6 (both = ×1.2).
  - A fixed-duo half (Metal) is covered by Metal, or by both Earth and Machine, but **not by Earth alone**.
  - When the blob is a duo, its halves decide (a Metal weapon covers only Smith's Metal half: ×0.6). A single-attunement blob is scored against the weapon's duo: a Blade blob with the Rapier is now **×0.6** (it was ×1.0).
  - Balance's ×1/2 on elements is unchanged.
- **Duo attunement pages** (your request, ~05:10): a duo's page shows **one half at a time**.
  - The other half waits in a **small orb** beside the big insignia, labelled with its name and "click to switch".
  - **Click it and it floats up into the big insignia** (0.45 s) while the current half shrinks into its place. The whole page turns to that half: its colours, background, tiles, rules and level effects.
  - A **DUO** row says "Half of the Fire + Blade duo: each half x0.6, both x1.2, one rarity and one points pool".
  - The header reads "ALDRIC · … · DUO ATTUNEMENT · FIRE + BLADE"; the name stays fixed while the halves swap.
  - It works for a class's duo, a lone fixed duo (Lava shows Fire and Earth), and a device's duo (the Loom's Esotericism + Fate, the doors' Heavy + Bastion).
  - Rarity and points are read from the blob's own attunement, which holds both halves.
- Tests: **790 pass** (new: the Rapier ×0.6, Metal + Craft as one duo, the Metal half's coverage, both sides ×1.2). The harness clicks the small orb and checks that the page turns to Blade.
- Shots: `handoff_images\2026-10-26_duo_pages\` (3: Fire, mid-float, Blade).
- Files: `Attunements`, `CrewMember`, `InfoSpec`, `BookPage`, `LogicTests`, `QolShots`.

## Save 44: shared mana columns, the Staff needs mana, Ward Restoration, Permeator Arcane
Your answers (26 Oct, ~05:35) are in the answers doc ("A3 and the mana bar" block).
- **The mana bar groups by kind (FTL-style pips):**
  - Key devices keep their own column: the **Permeator** and the **Loom** (the Ward draws nothing yet).
  - Everything else shares a column per kind: **Pedestals** (Fire Gem, Glass Orb, any empty pedestal as grey pips), **Weapons** (every staff your crew wields), **Mounts** on the stamina bar, and **Devices** (things on Anchorstones; none draws mana yet, so that column only appears once one does).
  - A column is as tall as all its members' pips, with a small gap between members. **Left-click powers the next member in order; right-click switches off the last one that's on.**
  - Under a shared column, "n/m on". Hovering lists the members with ● on / ○ off, bottom to top like the pips.
- **The Glass Magic Staff fires only while powered with 1 mana bar** (`PersonalWeapon.STAFF_MANA`, PLACEHOLDER).
  - Unpowered, its wielder fights with fists (`CrewMember.activeWeapon()`; combat, range, crit, the stats book and training all read it).
  - The crew card says "Glass Magic Staff · no mana" and "Fists until powered: mana bar, Weapons column".
  - Taking a staff off frees its bar. When the mana bar shrinks, staffs lose power first.
  - **At the start the bar is full (4/4: Permeator, Loom, Fire Gem, shield), so Daria's staff starts unpowered.** Switch something off to power it. The bots power staffs when mana is free.
- **The Chrysalis Ward is Restoration-attuned:** a matching patient heals +10% × match power (`Fortress.wardHealMultiplier`). Its page shows Restoration.
- **The Permeator is Arcane** (`Permeator.ATTUNEMENT`), for its station later.
- Tests: **798 pass** (new: the staff unpowered = fists, powering needs a free bar, the Weapons column lists the wielders, unequipping frees the bar, the Ward's Restoration bonus, the Permeator's Arcane).
- Shots: `test/mana_columns_hover.png`, `test/mana_staff_unpowered_card.png` (to the handoff folder with A3).
- Files: `PersonalWeapon`, `CrewMember`, `StatRules`, `StatSheet`, `Fortress`, `Permeator`, `InfoSpec`, `FortressView`, `InfoPanel`, `LogicTests`, `QolShots`, `BotPlaytest`.

## Save 45: A3 part 1, ammo in rooms (crates) and quivers
- **Ammo lives in rooms** (`Room.bolts`, at most `Room.MAX_BOLTS` = 2 crates = 10).
  - A mount has no bolts of its own: it uses its room's (`Mount.bolts()` and `hasAmmo()` read the room).
  - **Our Mount Room starts with 1 crate (5 bolts)**; it used to be 10 built in.
  - Rooms with ammo show their crates (one per 5 bolts) in the bottom-left corner. The mount card's row reads "Bolts (in its room)".
- **Crates** (`Item.boltCrate()`, 5 bolts, PLACEHOLDER):
  - The shop sells two for 5 Units each (PLACEHOLDER).
  - **Drag a crate from a blob's hands slot and drop it on a room on the map:** the blob walks there and sets it down (`Fortress.deliverCrate`), and the room gains 5 bolts.
  - A full room refuses it (a notice), and the crate stays in the blob's hands.
  - A plain move order cancels the delivery.
- **Quivers** (`Quiver`, 16 arrows, PLACEHOLDER) go in the **Accessory** slot. The Longbow and the Rapid crossbow both shoot arrows; every shot uses one.
  - **Empty or no quiver = fists** (`activeWeapon()`). A notice says "quiver is empty".
  - The crew card says "Longbow · no arrows" and "Fists until a quiver with arrows is worn".
  - **Brenna starts with a full quiver; a spare waits on the rack.**
  - **Buying a quiver (5 Units) refills the emptiest quiver you own to 16**; only if every quiver is full does a new one go on the rack.
  - Quivers go on in the **Storeroom only**: the storage card now shows each blob's **Accessory** slot under its Weapon slot. Drag from the rack, click to take off, or click a rack quiver with a blob selected.
  - The quiver icon shows fewer arrows as it empties, with a grey slash when empty. Crates have their own icon.
- Tests: **808 pass** (new: the Mount Room's first crate, carrying and setting down a crate, a full room refusing one, Brenna's quiver, an arrow per shot, empty = fists, the emptiest quiver first, putting a quiver on and off). The harness drags a real crate from the Storeroom card onto the Mount Room.
- Shots: `test/ammo_storeroom_quivers.png`, `test/ammo_crate_set_down.png`.
- Files: `Quiver` (new), `Item`, `Room`, `Mount`, `PersonalWeapon`, `CrewMember`, `Fortress`, `Shop`, `InfoPanel`, `ItemSlots`, `FortressView`, `LogicTests`, `QolShots`.

## Save 46: A3 part 2, the camp's devices on our mechanics
- **The camp's ballista loads in 14 s, like ours.** Its gunner's stats scale it with our mount's scalings (`StatRules.MOUNT`, `Fortress.campBallistaSeconds`): Dexterity speeds the load, Strength adds damage, and a **Luck crit knocks off a second bar**.
- **The camp's Glass Gem has no reserve** (magic never runs dry). Its caster scales it with our Glass Gem's scalings: Dexterity for the rate (as before), **Arcana for blast damage**, and **a Luck crit always knocks a bar off**.
- **The camp's shield**: its operator's Psyche adds bars (as before); **Arcana now speeds its recharge** (`campShieldRechargeSeconds`).
- **The camp's bolts live in the Battlement: 2 crates (10)**, refilled with each new band. The crates show in the room.
- **Boarding:** when the Battlement runs out of bolts, **everyone boards (as before)**. The Gem no longer counts as ammo.
- **Cards and pages:**
  - Enemy Mount: "Bolts (in its room) 7 / 10", the live reload time (13.9 s at Dexterity 1), our mount's scalings.
  - Enemy Glass Gem: "Ammo: none", its blast damage, our Glass Gem's scalings.
  - Enemy shield: the live recharge time.
- **A call I made, to confirm:** the camp's devices now run **our formulas and numbers**, but they are still the camp's own code, not the same `Mount` / `Pedestal` objects as yours. Making them literally ours (with unslotting) fits best with stealing (A6), where taking a device off a pedestal is the point. Say if you want the full object rewrite now.
- `BanditCamp.holdFire` (new) silences the camp's guns while its crew still mans them. Tests use it (they used to empty the bolts and mana); a truce could later.
- **Bots:** the rush bot no longer clears the camp. Brenna's 16 arrows run out, Daria's staff has no free mana, and the camp boards later (14 s reloads). That's balance, not a fault: 0 problems.
- Tests: **812 pass** (new: the camp's 14 s reload sped by Dexterity, the shield's Arcana recharge, 2 crates in the Battlement, out of bolts = everyone boards; the Enemy Glass Gem page shows no ammo and our scalings).
- Shots: `handoff_images\2026-10-26_A3\` (`_INDEX.md`).
- Files: `BanditCamp`, `EnemyShot`, `Fortress`, `InfoSpec`, `FortressView`, `InfoPanel`, `LogicTests`, `QolShots`.

## Save 47: pause on pages, dev level tools, free class picks
- **The game pauses while any page covers the fortress:** a book page (a stats book, a weapon or device page, an attunement page) and the class tree screen. `FortressView.tick` skips the world's steps while it's covered (`setCoveredByScreen`, set by the main window when it switches to the tree). Nothing catches up when you come back. The pages say "Game paused" (the book's top bar, the tree's footer).
- **Dev console (the Dev button or `):**
  - **Free class picks: ON / OFF.** While on, **any designed class of any tier can be picked straight away, from anywhere**: no level, no attribute gate, no next-step rule, no Chrysalis Ward (`Fortress.setFreeClasses`). Open the class tree for a blob, click any class, then PROMOTE.
  - **Selected: +1 level**, **Selected: +5 levels** (real level-ups with free points and class bonuses, stopping at the tier cap; `devLevelUp`).
  - **Selected: level up to tier cap**, and **Whole crew: level up to tier cap**.
  - The rest as before: Test Mode, +5 free points, +1 Luck, the test shots.
- Tests: **816 pass** (new: +1 level is a real level-up, level-ups stop at the cap, a Tier 4 class shut normally, free picks promote an Unclassed blob straight to it outside the Ward). The harness checks the pause: nothing moves under a book page or the tree screen, and the game runs again afterwards.
- Shot: `handoff_images\2026-10-26_A3\07_dev_console.png`.
- Files: `Fortress`, `FortressView`, `ClassTreePanel`, `ClassTreeView`, `BookPage`, `Fateweavers`, `LogicTests`, `QolShots`.

## Save 48: the blob mounts (R3 §8 step 3, second half): the Battlement, the Ranger Platform, the Stage
Your answers (26 Oct, ~06:25–06:40) are in `roadmaps/archive/2026-10-26_answers_blob_mounts.md`.
- **Our Battlement** (`BATTLEMENT`): a new 3x2 room above the Mount Room, with doors to the Pedestal Room and the Mount Room and **two stations** on its inner vertices.
  - It starts with the **Scorpion** on the first station and the **Ranger Platform** on the second, plus 1 bolt crate (5 bolts).
  - **Any mount fits any station** (`Fortress.fitEngine(mount, engine)`; the "Fit …" rows on every station's card): the Ballista, the Scorpion, the Ranger Platform, the Stage.
  - Cards and notices now say "station" (your word). `Fortress.mounts()` lists the three; `Weapon.station` says which station works each one.
  - Sending a blob into a room with two stations: the clicked station first, else the first free one.
- **The Ranger Platform** (`SiegeEngine.Kind.RANGER_PLATFORM`, a blob mount; 1 stamina):
  - **Anyone can climb on** (~2 s on and off, as before).
  - Powered and climbed on, the blob shoots **its own ranged weapon** (quiver arrows, a staff's mana) at an enemy **in any room, indoor or out, within its weapon's range + 2 tiles for the height** (`Mount.PLATFORM_HEIGHT_BONUS`, PLACEHOLDER). It's the only way to shoot out of an indoor room.
  - **Without a ranged weapon or ammo it stands there doing nothing** (no melee either).
  - Ordered to attack, the shooter **stays on the Platform**; out of range it waits.
  - **Shield rules:** a Platform shot into the camp meets the camp's Mana shield. A staff's spark costs the shield a bar and does no damage; arrows pass.
  - On the map the blob **climbs up onto the deck** (drawn raised; clicks find it there). Shots fly on a **high lob** (`RANGED_SHOT` text ends ":PLATFORM").
  - Working it trains **Perception** (the hard rule).
- **The Stage** (`SiegeEngine.Kind.STAGE`, a blob mount; the shop sells it for 30 Units, PLACEHOLDER; 1 stamina):
  - The performer **only performs**: no attacks, no resistance.
  - **The taunt pulls enemies:** enemies in reach walk to the Stage's room and attack the performer (`Fortress.updateTaunts`, `CrewMember.tauntedBy`).
  - **Reach, both ways (Charisma):** up to **1 + Charisma / 20 doors** away, at most **1 + Charisma / 10 enemies** at once (`StatRules.stageDoors / stageEnemies`, PLACEHOLDER).
  - **Steady + break free:** every 3 s each taunted enemy rolls to break free, 1% per point of its Resolve (max 60%). A freed enemy can't be taunted again for 5 s (`StatRules.TAUNT_*`, PLACEHOLDER).
  - **Not taunted:** a neutral camp's band (in its camp) and the Goblin Boss.
  - Leaving the Stage, or it losing power, drops every taunt.
  - Performing trains **Charisma**.
  - **Looks** like your reference image: a round three-step dark blue-grey podium with a warm glowing top. A spotlight beam falls over the performer while they perform. Taunted enemies wear a small gold star and float "taunted!" / "broke free".
  - While the Stage or its performer is selected, the rooms in reach are lit gold, with a tag like "Taunt: 2 doors, 3 enemies".
- **The Guard Post: scrapped for now** (your answer). It's parked as a possible door upgrade (`notes/ideas_parked.md`).
- **No more bolt tick rows** (your screenshot): neither our mounts nor the camp's Battlement draw a row of ticks. The crates in the room show the ammo. Our mounts keep a thin charge bar under them while loading.
- **Cards and pages:**
  - The Platform's and the Stage's own card (who's on it, the numbers that matter, "Fit …", WORK IT / Stop) and book page (Range / Weapon / Ammo, or Reach / Enemies / Holding / Break free).
  - The siege card's header says "Station"; its Bolts tile says "the crates in its room".
  - The stamina bar's Mounts column lists all three stations.
- **`Scaling.sqrtCurve`** (new): a square-root scaling, so the Platform's page can list Perception's range curve.
- **Calls I made (to confirm, listed in R3's "Pressing: do first"):**
  - Powering the **Stage doesn't provoke** a neutral camp (it isn't a weapon); powering the Platform does, like any weapon.
  - Only shots **from the Platform** meet the camp's shield; the older A2 shots across outdoor rooms still don't (unchanged).
  - Platform shooters keep the "attack by order" rule: they shoot only the enemy you right-click (the bots pick the nearest in range).
- **Bots:** the turtle and Test Mode bots put the bow blob on the Platform and power it. A new **"stage" game** (the turtle with a Stage on the rack) fits it on the Scorpion's station and sends its toughest blob on when boarders get in. The fuzzer fits random engines and blob mounts on random stations. The log prints Platform shots and taunts. 45 games, **0 problems** (turtle: 15 shots from the Platform; stage: 14 Platform shots, 2 taunts).
- Tests: **856 pass** (+40: the Battlement, the stations, the Platform from indoors, the high lob, arrows used, holding the spot out of range, a sword standing by, the camp shield stopping a spark, the Stage fitting, reach by Charisma, taunts pulling and attacking, break free, the performer not attacking, training, getting off). The harness adds 10 PASS checks (raised blob, clicking it, the boxes, the Mounts column…): **R1 checks: all passed**.
- Shots: `handoff_images\2026-10-26_blob_mounts\` (9, `_INDEX.md`).
- New PLACEHOLDERs: Platform +2 tiles height, 1 stamina; Stage 1 stamina, 30 Units; reach 1 + CHA/20 doors, 1 + CHA/10 enemies; break-free roll every 3 s at 1%/Resolve (max 60%), 5 s free; the Battlement's 1 crate.
- Files: `SiegeEngine`, `Mount`, `Weapon`, `CrewMember`, `Fortress`, `StatRules`, `Scaling`, `Shop`, `GameEvent`, `InfoSpec`, `DeviceArt`, `FortressView`, `InfoPanel`, `BookPage`, `LogicTests`, `QolShots`, `BotPlaytest`.

## Save 49: the Permeator's station and its surge (R3 §2, A4); the Workbench opens to anyone; the Supporter bonus
Built unattended from the ✅ in R3 §1–2 (no new questions). The curve is a PLACEHOLDER; it's in "Pressing" for your OK.
- **The Permeator's room has a station** (beside the crystal). The Permeator still needs only power to run.
- **The surge** (`Fortress.startSurge`, `Permeator.surgeLeft`):
  - While someone is on the station, the mana bar's Permeator column shows a second pip, **Surge**. Left-click the column, or press **SURGE** on the Permeator card, to spend a spare mana on it.
  - **That mana and the operator are locked for 10 s.** A move order is refused with "locked in the Permeator's surge for n s more"; the pip can't be switched off. The crystal and the pip crackle light blue.
  - **The refill is supercharged**, slow at first and rising exponentially. The Permeator room's fill rate and the Aether flow through every fortress door are multiplied by **1 + peak × (e^(3t) − 1) / (e^3 − 1)** (t = 0..1 through the pulse).
  - **peak = (2 + 0.1 × Arcana) × 1.2 for a Supporter × (1 + 10% × the Arcane match)** (all PLACEHOLDER). At Arcana 1 a surge ends at ×3.1; at Arcana 40, ×7.
  - **Then the pip frees:** spend again for another pulse. No spare mana = no surge (the bar is full at the start; switch something off).
  - It stops early if the operator is gone, the Permeator goes down, or the bar shrinks (the surge is shed before the Permeator itself).
  - **The surge trains Arcana** (the hard rule; `StatRules.PERMEATOR`).
- **Cards and pages:**
  - The Permeator card: On the station, Surge / Surge peak, Surge cost, AIM FOR Arcana, and the **SURGE** button (or why not).
  - Its page: a SURGE tile (the peak, or ×now and seconds left) and what the operator adds.
- **The Workbench opens to anyone** (✅ R3 §1, G24); **a Supporter works it 20% faster** (`StatRules.anchorstoneBonus`, ✅ +20% on every Anchorstone device; the Permeator's surge uses it too).
- **Fix:** the Chrysalis Ward's page had lost its crystal picture and its "What the patient adds" panel since save 44 (the lines sat behind a `//` comment). Back now.
- **Mana bar:** the columns start clear of the "left: on · right: off" line (the Permeator's "2/2 on" used to overlap it).
- **Bots:** the fuzzer sends blobs to the Permeator's station and starts surges. 45 games, 0 problems.
- Tests: **871 pass** (+15: the station; no surge without an operator or a spare mana; the locked mana and operator; it starts at ×1 and rises exponentially; it ends after 10 s and can go again; +0.1 peak per Arcana; a Supporter +20%; a drained fortress refills faster with a surge; anyone crafts, a Supporter 20% faster). Harness +3 PASS (the card's SURGE button, the Surge pip), **R1 checks: all passed**.
- Shots: `handoff_images\2026-10-26_permeator_surge\` (4, `_INDEX.md`).
- New PLACEHOLDERs: `Permeator.SURGE_BASE` 2, `SURGE_PER_ARCANA` 0.1, `SURGE_CURVE` 3 (the exponent). ✅ numbers: 10 s, 1 mana, Supporter +20%.
- Files: `Permeator`, `Workbench`, `StatRules`, `Fortress`, `GameEvent`, `InfoSpec`, `DeviceArt`, `FortressView`, `InfoPanel`, `LogicTests`, `QolShots`, `BotPlaytest`.

## Save 50: your "Pressing" answers, part 1 (the small changes)
Your answers (26 Oct, ~12:00–12:45) are in `roadmaps/archive/2026-10-26_answers_pressing.md`.
- **Mounts power themselves:** once a blob has climbed on (~2 s), the mount draws its stamina by itself (a notice if there isn't enough); getting off powers it down. **The WORK IT / Stop buttons are gone** from every mount card (the siege card keeps AUTO, full width).
- **Powering a mount never provokes a neutral camp** (the Platform, the Stage, and the ballista too, since it now powers itself when someone climbs on: a call, see below). Firing still does.
- **Every ranged shot into the camp from outside it meets its shield** (not only the Platform's): a staff's spark from No Man's Land costs the camp's Mana shield a bar and does no damage; arrows pass.
- **The Ranger Platform picks its own target:** the nearest enemy in range, unless you right-clicked one that's in range. It never starts a fight with a neutral camp.
- **Attunements:** the Platform is **Range**, the Stage **Performer**; their cards and pages show the attunement card (engines carry their own attunement, `SiegeEngine.Kind.attunement`).
- **Bigger Stage reach:** **1 + CHA/10 doors, 2 + CHA/10 enemies** (PLACEHOLDER).
- **The Goblin Boss can be taunted:** he can't walk, so taunted he throws his rocks at the Stage's room. **Resolve 55** (PLACEHOLDER): a 55% break-free roll every 3 s.
- **No camp respawn:** a cleared camp stays empty; its treasure room stays open with whatever you left. The notice says "take what's in the treasure room and weave on".
- **Balance (A7):**
  - **Pools:** bars = round(3.49 + 0.0925 × points^0.69): 4 fresh, 6 at 120 points, 8 at 280, 12 at 700.
  - **Weapons:** +2% per point per scaling attribute (was +1%).
  - **Rapid crossbow:** DEX + PER (was DEX + RES).
- **The flatter surge:** peak = (1.2 + 0.06 × Arcana) × Supporter × match, curve exponent 1.5 (was 2 / 0.1 / 3). At Arcana 1 it ends at ×2.3 (was ×3.1); at Arcana 40, ×3.6.
- **A call I made:** since mounts now power themselves when climbed on, powering the **ballista** no longer provokes either (otherwise just stationing Daria would turn the camp hostile). Only firing it does. Say if you want powering the ballista to count again.
- **Bots:** the Stage performer steps off below 40% health. The Test Mode bot no longer turns the camp hostile (nothing it does provokes now), so its camp stays neutral. 45 games, 0 problems.
- Tests: **884 pass** (+13: mounts power themselves and power down, no provoking, Range / Performer, the Platform's own target, a spark from No Man's Land meets the shield, the boss's Resolve, the new pool curve, +2%, DEX + PER, the flatter surge, the camp stays empty, the treasure room stays open). **R1 checks: all passed.**
- Files: `SiegeEngine`, `Mount`, `Weapon`, `StatRules`, `PersonalWeapon`, `Permeator`, `GoblinBoss`, `Fortress`, `InfoSpec`, `InfoPanel`, `LogicTests`, `BotPlaytest`.

## Save 51: device slots (drag between a device and its operator's hands; no Unslot)
Your answer (26 Oct, ~12:25): every pedestal and station card shows the device's slot next to the operator's hands slot.
- **The row** sits under the operator on every pedestal card (gem, orb, empty) and every station card (siege engines, the Platform, the Stage).
  - On the left, **ON THE PEDESTAL / ON THE STATION** and what's on it.
  - In the middle, two arrows ⇄.
  - On the right, **<NAME>'S HANDS**: the operator, else the selected blob if it stands in the room, else the first blob there, else a grey "Nobody here".
- **Moving items:**
  - Drag from the device to the hands to take the item out. If the hands hold something that fits, the two swap; if they hold something that doesn't fit, a notice says the hands are full.
  - Drag from the hands to the device to put it on (a swap if the device holds something).
  - A click on either slot does the same move.
- **The Unslot button is gone** (gem and orb cards). The gem card's AUTO is full width.
- **Stations can be empty:** take an engine (or the Platform, or the Stage) into a blob's hands and the station shows "Empty station". It can't be powered or fire until an engine goes back on. Any engine fits any station, from the hands or with the card's "Fit … from the rack" rows.
- Logic: `Fortress.handsFor / itemIn / fitsDevice / takeFromDevice / putIntoDevice`; `Mount.isEmpty / kind`. `unslot` stays for older tests and the fuzzer.
- **Stealing** comes next and uses the same row on the enemy's device cards (dragging from their device into your thief's hands starts the charge bar).
- Bots: the fuzzer drags items in and out of random devices. 45 games, 0 problems.
- Tests: **895 pass** (+11: the hands slot belongs to the operator; out, back, a swap; full hands refused; an empty station can't power or fire; engines back on; a Stage swaps with the Scorpion; an engine doesn't fit a pedestal). The harness does a real drag on the gem card, out and back (+3 PASS). **R1 checks: all passed.**
- Shots: `handoff_images\2026-10-26_device_slots\` (4, `_INDEX.md`).
- Files: `Mount`, `Fortress`, `InfoSpec`, `InfoPanel`, `ItemSlots`, `DeviceArt`, `FortressView`, `BookPage`, `LogicTests`, `QolShots`, `BotPlaytest`.

## Save 52: stealing (A6, R3 §7) and the camp's devices as takeable things
Your answers (26 Oct, ~12:25–12:35) are in `roadmaps/archive/2026-10-26_answers_pressing.md` (P4).
- **How:** every enemy device card (their shield pedestal, their Battlement's ballista and Glass Gem, their campfire) shows the same slot row as yours, with "ON THEIR …". Drag from their device into your blob's hands slot to start the steal. Their Battlement card also has a **THEIR CRATES** row. A click on their slot works too.
- **Who's stealing:** the hands slot is the selected blob if it stands in that room, else the first of yours there. Under the row the card says what's blocking it, or "Drag it into …'s hands to steal it".
- **The rules (✅):**
  - **The room must be cleared** (no living enemy in it).
  - **Only Thieves take devices:** the Thief (T2) and everything below it (Goblin, Burglar, Criminal, Outlaw and their T4s; `Fortress.isThief` walks up the tree). **Anyone can take a crate.**
  - **Hands empty.**
  - **The charge:** 8 s ÷ (1 + 2% × (Dexterity + Luck)) for a device, half for a crate (PLACEHOLDER). **A hit resets it.** Moving off cancels it.
  - **Stealth:** a **1% roll once per attempt**. If it hits, nobody notices ("unseen" by the bar). Otherwise **the nearest enemy drops everything** and goes for the thief (`CrewMember.hunting`), as with a taunt.
  - On the map a small gold bar fills over the thief; the card shows it too ("Stealing 40% · a hit starts it over").
- **What you get:**
  - their ballista → a **Ballista engine** (fits any station);
  - their Glass Gem → a **Glass Gem** for your gem pedestal;
  - their orb → a **Glass Orb**;
  - their **campfire** → a **Campfire** item (where it goes in your fortress is open: "Pressing");
  - a crate → a **bolt crate** (5 bolts off their Battlement).
  - The chest can't be taken.
- **The camp without them:**
  - no ballista → it only casts the Gem;
  - no Gem and no ballista → it boards;
  - with its ballista, out of bolts still means everyone boards (as before);
  - no orb → no shield;
  - no campfire → no healing in the quarters.
  - The map shows empty pedestals where things were taken. **A new band brings new gear** (`resetCamp`; later nodes).
- **The camp-object merge:** the camp's devices are now things that sit in slots and can be taken out (the slot row and the steal), and they run our formulas (save 46). I did **not** rewrite their firing to run through our `Mount` / `Pedestal` update loop: it would change every camp timing for no visible difference. Say if you want that too.
- **Bots:** a new **"thief" game**: the turtle clears the camp, then Aldric (made a Thief) strips it (4 devices stolen) and weaves on. The rush bot takes a crate from a cleared Battlement; the fuzzer tries steals. 46 games, 0 problems.
- Tests: **921 pass** (+26: cleared first; Thieves only, crates for anyone; a Burglar counts; the charge time; the drag starts it; a hunter comes; the charge rises; a hit resets it; the ballista becomes an engine; the Gem, orb, campfire and a crate; their effects on the camp). Harness: a real drag from their ballista into Aldric's hands (+2 PASS). **R1 checks: all passed.**
- Shots: `handoff_images\2026-10-26_stealing\` (3, `_INDEX.md`).
- New PLACEHOLDERs: `StatRules.STEAL_SECONDS` 8, `STEAL_PER_POINT` 0.02, `STEAL_CRATE_FACTOR` 0.5; ✅ `STEALTH_CHANCE` 1%.
- Files: `BanditCamp`, `Item`, `CrewMember`, `StatRules`, `GameEvent`, `Fortress`, `InfoPanel`, `ItemSlots`, `FortressView`, `LogicTests`, `QolShots`, `BotPlaytest`.

## Save 53: the quiver's own slot, the 2x3 Workshop with the Furnace, the Training Room, the 5x4 rack
Your answers: P5 (26 Oct, ~12:35) and the midday ones (~13:15–13:30, `roadmaps/archive/2026-10-26_answers_midday.md`).
- **The quiver has its own slot.** The crew card now has five slots: HANDS · WEAPON · **AMMO** · ARMOUR · ACCESS. Brenna's quiver sits in AMMO, so Accessory is free for amulets. The Storeroom's card shows an AMMO row under WEAPONS (drag a quiver on or off there). `CrewMember.ammo`.
- **The Workshop is 2x3** with two Anchorstones, each with its own station: the **Workbench** on top, the **Furnace** below. Anyone works either; a Supporter is 20% faster.
- **The Shield room moved down a row** (it still opens to the Workshop and the Ward).
- **The Furnace:**
  - Metal → Molten Metal, Glass → Molten Glass;
  - 1 : 1, no fuel, 6 s (PLACEHOLDER);
  - an input slot and an output slot on its card, with an arrow that fills;
  - on the map, its mouth glows and its chimney smokes while it works.
  - It trains nothing (it scales with nothing); say if it should train something.
- **The Training Room** (3x2, under the Shield room, door to it) holds two slots:
  - **The training stand** starts with the **worn notebook** (Knowledge). Whoever is on its station trains every attribute the thing on it scales with, at the flat station rate (1 XP/s each, never Luck, no mana or stamina). The fortress can't use what's on the stand.
  - It takes any conduit, mount engine, device, weapon or book. Raw materials don't fit: they scale with nothing.
  - The card has the same slot row as the pedestals (ON THE STAND ⇄ hands).
  - **An empty Anchorstone** (your midday answer): the first free slot for a device. Moving devices onto it comes in save 54.
- **The empty-slot looks** (your "stone base + glyph"):
  - an **empty Station** is a round iron-banded plinth with rivets and four bolt holes (you see it when you lift an engine off);
  - an **empty Anchorstone** is a flat rune-carved stone with a faint, pulsing blue glow (in the Training Room).
- **The rack is 5x4 = 20 slots** (your midday answer). The camp's chest stays 3x3.
- **Raw materials exist as items** with their own art: Wood, Metal, Twine, Glass, Crystal, Rock, Leather, Molten Metal, Molten Glass. Plus the notebook's art.
  - **A call:** the rack starts with **one Metal and one Glass** so the Furnace can be tried. That's a stand-in until raws come from the shop and chests (save 55); say if you'd rather start empty.
  - Stacks of 20 come with crafting (save 55).
- **Small fixes:**
  - The Workbench card no longer says "Needs a Supporter" (anyone works it since save 49). Its operator is a CRAFTER.
  - Empty Anchorstone stations say "Empty: it waits for someone" instead of "its charge is lost".
- **Bots:** a new **"crafter" game**. The turtle runs while Daria carries the Metal and the Glass to the Furnace, smelts both, puts them back on the rack, then trains on the stand (2 smelted, 210 s on the stand). The fuzzer also uses the Furnace and the stand. 47 games, 0 problems.
- **Tests: 953 pass** (+32):
  - the layout and doors; the two stations; the 5x4 rack and the 3x3 chest; the quiver in Ammo;
  - the Furnace takes Metal and runs only with someone on its station; Metal → Molten Metal in 6 s; molten doesn't go back in; Glass and Wood;
  - the notebook trains Knowledge and gives XP; the Rapier swaps onto the stand; raws don't fit; a mount trains Strength; drag it off.
  - The harness checks the Workshop's and the Training Room's device rows (+5 PASS) and the room shots (+5 PASS). **R1 checks: all passed.**
- **Shots:** `handoff_images\2026-10-26_rooms_ammo\` (8, `_INDEX.md`).
- **New PLACEHOLDERs:** `Furnace.SMELT_SECONDS` 6; the starting Metal and Glass on the rack.
- **Files:**
  - new: `Furnace`, `TrainingStand`;
  - changed: `Item`, `Anchorstone`, `Storeroom`, `CrewMember`, `Fortress`, `InfoSpec`, `InfoPanel`, `FortressView`, `DeviceArt`, `ItemSlots`, `BookPage`, `LogicTests`, `QolShots`, `BotPlaytest`.

## Save 54: every Anchorstone device moves like a conduit; the stolen campfire heals at home
Your midday answers: devices move like conduits; every one is movable; the Training Room has the first empty Anchorstone.
- **How:** every device card (Workbench, Furnace, Permeator, storage rack, campfire) now has the slot row: **ANCHORSTONE** ⇄ the operator's hands, the same as pedestals and stations.
  - Drag the device into the hands to lift it off. The blob carries it (its art rides in the hands slot).
  - Drag it onto any empty Anchorstone to set it down. A click on a blob's hands slot in a room with an empty Anchorstone does it too.
  - Carrying a device and dragging another off swaps them.
- **An empty Anchorstone** has its own card (click the rune stone on the map, or its row on the room card): who's on its station, the slot row, what fits.
- **Where a device stands decides its station:** each Anchorstone is one of the room's device slots, and its station is that slot's. The Furnace on the Training Room's free stone is worked from that room's second station.
- **While a device is carried it does nothing:**
  - **Workbench and Furnace:** a craft or smelt starts over; nothing goes in.
  - **Permeator:** it powers off and its mana frees; any surge ends; its old room starts to vent. Set back down, it powers on again if there's a spare mana.
  - **Storage rack:** the Storeroom has no storage until it's back (everything stays on it). The rack can't go on itself.
- **The stolen campfire is a device now (✅ midday), not an item.** Set down on an Anchorstone, whoever of yours rests in that room heals at the camp's rate (3/s × the Vitality + Faith heal multiplier). It needs no operator or mana. The camp's own chest and campfire still can't be lifted (the campfire is stolen instead).
- **On the training stand:** a Workbench trains Knowledge, the Permeator Arcana, a campfire Vitality + Faith. The Furnace and the rack scale with nothing, so they don't fit.
- **Calls I made (say if you want them different):**
  - The Permeator and the rack are movable too ("all movable"). Lifting the Permeator stops the Aether, so it's a risk you choose.
  - The Storeroom's slot has no station (as before), so a Workbench or Furnace set down there can't be worked.
- **Bots:**
  - The thief game now carries the stolen campfire home and sets it on the Training Room's stone.
  - The fuzzer lifts and sets down random devices.
  - 47 games, 0 problems.
- **Tests: 982 pass** (+29):
  - lift the Furnace (no operator, nothing goes in), set it down in the Training Room (its station there, it smelts);
  - the Workbench moves to the Workshop's second slot;
  - the Permeator stops, can't power while carried, its room vents, and it runs again when set back;
  - the rack (no storage while carried, everything still on it, not on itself);
  - the campfire on a free stone heals Brenna;
  - the camp's chest stays.
  - The harness does a real drag of the Furnace into Daria's hands (+5 PASS). **R1 checks: all passed.**
- **Shots:** `handoff_images\2026-10-26_anchorstones\` (6, `_INDEX.md`).
- **New PLACEHOLDERs:** none (the campfire uses the camp's 3/s).
- **Files:**
  - new: `AnchorDevice`, `Campfire`;
  - changed: `Anchorstone`, `Workbench`, `Furnace`, `Permeator`, `Storeroom`, `Item` (the campfire item is gone), `TrainingStand`, `Fortress`, `InfoSpec`, `InfoPanel`, `FortressView`, `ItemSlots`, `LogicTests`, `QolShots`, `BotPlaytest`.

## Save 55: crafting (R3 §4), stacks, essence (R3 §5), the level-up reroll pop-up, the Protection Amulet, raws to buy
Your answers: P6 (26 Oct) and the midday ones (the reroll **pays** essence; skipping is free).
- **Stacks (✅):** raws, crafted parts and the essences stack, **up to 20 per slot**.
  - Putting a stack on the rack piles it onto a matching stack first, and the rest starts a new one.
  - The count shows in the slot's corner, Minecraft-style ("Wood ×20" in names).
  - Hands carry a whole stack; dropping one onto a matching Workbench input piles it on.
- **The Workbench (✅ 3 inputs + a tool slot):**
  - three input slots, a **TOOL** slot under them (the tool stays), an arrow and the output;
  - a recipe takes what it needs and leaves the rest of a stack, and keeps going while the output stack has room;
  - anything extra in the inputs means no recipe; a tool in its slot makes its recipes win (Wood + a small blade makes a Handle, not Sticks);
  - a click on a hands slot in the Workshop puts a hammer or small blade in the tool slot, a stack onto its match, else the first free input.
- **The R3 §4 recipes, as built (21):**
  - Wood → 2 Sticks;
  - 3 Twine → String;
  - 2 Molten Glass → Glass Orb;
  - Stick + Metal + Twine → Hammer;
  - Molten Metal + (hammer) → Small Blade;
  - Wood + (small blade) → Handle;
  - Handle + Small Blade → **Dagger** (new weapon: Finesse ✅; Piercing, DEX + PER, 6 dps / 0.5 s, 15 Units, all PLACEHOLDER);
  - Small Blade + Molten Metal + (hammer) → Blade;
  - Blade + Handle → Sword;
  - 2 Sticks + String → Bow (the Longbow);
  - 2 Sticks + Rock → 4 Arrows;
  - Leather + Arrows → a Quiver (takes up to 16);
  - Molten Glass + Crystal → Glass Gem;
  - Wood + Glass Gem → Glass Magic Staff;
  - Orb + Metal → **Protection Amulet**;
  - Fire Gem + Glass Orb → Fire Orb (kept);
  - Gem + (hammer) → 2–3 shards of its attunement (a Glass Gem gives glass shards);
  - Shard of Y + Essence → Essence of Y;
  - 6 Essence of Y → Quintessence of Y;
  - Quintessence of Fire or Stone + Glass Orb → Fire / Stone Orb;
  - Quintessence of Y + Glass Gem → a Y Gem (Glass Gem numbers, PLACEHOLDER; Fire makes the real Fire Gem).
  - "Hammer" tools: the Hammer and the Wrecking ball. "Small blade" tools: a Small Blade or a Dagger.
  - All 21 lines are on the Workbench's book page.
- **Not built, waiting on you ("Pressing"):**
  - the **Axe** (its attunement and numbers; and "Molten Metal + (Hammer)" also makes the Small Blade, so the two recipes clash);
  - the **Mana Blade** (Glass Gem + Handle: its numbers);
  - **staffs from other gems**;
  - **orbs for attunements other than Fire and Stone**.
  - **Re-attuning any item with Quintessence** is left for the shrines (your P8 answer puts it there).
- **Essence (✅ R3 §5):**
  - **1–3 on any blob's death** (yours or theirs);
  - **every rarity roll after a blob's first pays 1–5** by the rarity rolled (Common 1, Uncommon 2, Rare 3, Epic 4, Legendary and Mythical 5, PLACEHOLDER).
  - **A call:** essence goes straight onto the storage rack (there's no floor loot yet); a full rack loses it.
- **The level-up reroll pop-up (✅):** every level-up of a blob with an attunement brings a card at the top: "<Name> reached level N", a **Reroll <attunement> · <rarity>** button for each attunement (a duo shares one), and **Keep**.
  - Use it or lose it: Keep drops it, and so does the next level-up.
  - A reroll can go down and pays essence.
  - It doesn't pause the game; with several waiting it shows one at a time ("+2 more").
  - The promotion's old reroll button stays as it was.
- **The Protection Amulet (✅):**
  - it goes in the **Accessory** slot (drag in the Storeroom, like weapons; a click on it on the rack puts it on the selected blob);
  - **5% less damage** from everything but True damage;
  - the Storeroom's card now has an **ACCESS.** row under AMMO.
- **Raws at the shop (✅ 1–2 Units):**
  - Wood 1, Metal 2, Twine 1, Glass 2, Crystal 2, Rock 1, Leather 2;
  - one per click, onto the matching stack; the shop doesn't run out.
- **Raws in the chest (✅):** a roll can be a stack of one raw (20% a roll, 3–8 of it, PLACEHOLDER). "Nothing" drops to 4%.
- **Bots:**
  - The **crafter** game now also turns 3 Wood into 6 Sticks at the Workbench (3 crafted), then trains.
  - The fuzzer uses the inputs and tool slot, the reroll pop-up and the amulet.
  - 47 games, 0 problems.
- **Tests: 1060 pass** (+78):
  - stacks on the rack (15 + 8 = 20 + 3; the name shows the count);
  - Wood → 2 Sticks leaves the rest, and keeps going while the output has room;
  - all 20 recipes one by one (the tool stays; a Finesse Dagger; a quiver takes 16 of 20 arrows; real orbs and gems; 2–3 Fire shards; six Essence of Fire → a Quintessence; a Stone Orb; an Ice Gem);
  - anything extra means no recipe, and a missing tool means none either; a full output waits;
  - the tool slot in and out; stacks pile into an input;
  - the amulet on, 5% off a hit, off again;
  - 1–3 essence on a death;
  - no reroll for the unclassed; the pop-up after a level-up; skipping is free; a reroll pays by rarity; one per level-up;
  - raw prices; the chest's new odds.
  - The harness: +3 PASS. **R1 checks: all passed.**
- **Shots:** `handoff_images\2026-10-26_crafting\` (4, `_INDEX.md`).
- **New PLACEHOLDERs:**
  - the Dagger's numbers;
  - `Crafting.SHARDS_MIN/MAX` 2–3;
  - `Rarity.essence()` 1–5;
  - `Fortress.ESSENCE_ON_DEATH_MIN/MAX` ✅ 1–3;
  - `Fortress.CHEST_RAWS` 20%, `CHEST_RAW_MIN/MAX` 3–8;
  - raw prices 1–2;
  - `Item.AMULET_RESIST` ✅ 5%;
  - ✅ `Item.STACK_MAX` 20;
  - `Workbench.CRAFT_SECONDS` stays 10.
- **Files:**
  - new: `Crafting`;
  - changed: `Item`, `Workbench`, `Storeroom`, `PersonalWeapon`, `Rarity`, `CrewMember`, `Shop`, `Fortress`, `InfoSpec`, `InfoPanel`, `ItemSlots`, `FortressView`, `LogicTests`, `QolShots`, `BotPlaytest`.

## Save 56: the shop UI overhaul (a full-screen shop page)
Your answer (P2): a full-screen shop page that pauses the game like the books; items as cards with art, price, a hover description and stats; tabs (Weapons, Devices, Ammo, Services).
- **How it opens:** the shop room's card has a big gold **"Open the shop (N wares)"** button where the long list was. The haggler line stays on the card.
- **The page:**
  - a warm dark goblin-market background with lanterns;
  - "Goblin Shop", your Units, who haggles and their discount;
  - a cross to close (and **Esc**).
  - **The game is paused while it's open** (like the books and the class tree).
- **Tabs:** **Weapons · Devices · Ammo · Materials · Services**, each with its count.
  - **A call:** **Materials** is a fifth tab for the raws (and food), since raws came after the four tabs were named. Say if they should live somewhere else.
- **Cards** (5 to a row): each has the item's art on a round plate, its name, a short line ("Gem · gem pedestal", "Crafting material"), the stock ("1 in stock", "Plenty") and a gold **Buy · coin · price** button.
  - When you can't buy it, the button says why ("Not enough Units", "The rack is full", "Nothing to repair").
  - Rob the shop is a red button.
- **Hover a card:** the panel on the right shows its name, price, what it's for, and its **stats** (the same tiles as its book page: damage, charge, crit, scaling, reach…).
- **What's where:**
  - **Weapons:** the two personal weapons on sale.
  - **Devices:** Fire Gem, Glass Gem, Glass Orb, Stone Orb, Scorpion, Stage.
  - **Ammo:** Bolt crate, Quiver.
  - **Materials:** Food and the 7 raws.
  - **Services:** Repairs, Hire a blob, Rob the shop (robbing closes the page so you can fight).
- **A bought thing** shows a gold "Bought: …" line at the bottom and goes to the rack as before.
- **Bots:** the fuzzer's shop actions are unchanged (they call the same buys). 47 games, 0 problems.
- **Tests: 1115 pass** (+55):
  - every ware has a blurb and sits in an item tab; the tab sorting;
  - every ware has a picture; the pictures aren't real gems or orbs, and are made once;
  - stock counts.
  - The harness (+5 PASS): the page opens and covers the fortress; the game is paused; a real click on a card's Buy pays and puts it on the rack; Esc closes it and the game runs again. **R1 checks: all passed.**
- **Shots:** `handoff_images\2026-10-26_shop_page\` (6, `_INDEX.md`).
- **Files:**
  - new: `ShopPage`;
  - changed: `Shop` (tabs, blurbs, stock counts), `Fortress` (ware pictures), `FortressView`, `InfoPanel`, `LogicTests`, `QolShots`.

## Save 57: Morale, Madness and alignment (B8, P9)
Your answers: P9 (26 Oct) and the midday Chaos answer. The rules are in `logic/Morale.java`.
- **Meters (✅):** Morale 0–100 starting at 60; Madness 0–100 starting at 0.
  - On the crew card, under the health picture: two thin bars (Morale gold with marks at 25 and 75, Madness violet), each with its number.
  - On the right, the alignment icon: a sun for Good, scales for Neutral, a skull for Evil.
  - A tag line when it matters: Inspired / Shaken / About to desert 8 s (with a **TALK DOWN** button) / Lost to madness / Wandering / Chaos thrives.
- **On the map:** "DESERTING 8" (red) or "MAD" (violet) or "?" (wandering) over the blob; a small gold spark when inspired.
- **What moves them (✅ the table's shape; numbers PLACEHOLDER), per Good / Neutral / Evil:**
  - a kill: Morale −3 / 0 / +3 and Good Madness +4;
  - a crewmate dies: −10 / −6 / −2;
  - a theft: −2 / +4 / +4;
  - money: +1 / +3 / +2 per 25 Units, for everyone (arrival, a cleared camp, the boss);
  - loot taken from a chest: +1 / +3 / +2;
  - a level-up: +3 / +4 / +3;
  - a promotion: +8 / +10 / +8;
  - a crewmate talking you down: +5 / +5 / 0 to the talker;
  - heavy damage (dropping under 25% health): Madness +2.
- **Not wired yet** (their triggers don't exist): healing a crewmate, sparing / surrendering enemies, peaceful wins, Unspooler contact. They come with B7 events and R7.
- **The multipliers (✅):**
  - Morale gains × (1 + 1% Faith);
  - Morale losses × (1 − 0.5% Resolve);
  - Madness gains × (1 − 0.5% Resolve − 0.5% Faith), at most −60%.
- **Drift:** Morale drifts towards 50 (1 a minute); at 0 it stays 0. Resting in the Ward eases Madness 1 a minute (below 100).
- **Effects (✅):**
  - above 75 **inspired**: +10% work (stations, crafting, smelting, repairs, the Loom, the shield) and +5% damage;
  - below 25 **shaken**: −15% work.
- **Desertion (✅):**
  - at 0 Morale, a **10 s warning**. **TALK DOWN** on the card: the best-Charisma crewmate in the same room gives +20 Morale (+0.5 a point of their Charisma) and learns Charisma.
  - **Resting in the Ward** also stops it (Morale back to 10).
  - Otherwise the blob deserts: it won't take orders, walks to No Man's Land and is gone from the crew (not a death).
- **Madness (✅):**
  - above 50: a small chance (5% a minute) to wander off to a next-door room for 5 s, ignoring orders.
  - **At 100 it stays at 100**, and the blob has an **episode** now and then (30 s, then 60 s calm, PLACEHOLDER), ignoring orders:
    - **Good:** lashes out at the nearest blob in its room, friend or foe (one blow a second);
    - **Evil:** drops everything, walks to the weakest hurt ally and tends them (3 hp/s);
    - **Neutral:** gives what it carries to a crewmate in the room.
  - **Alignment never flips from Madness.**
- **Chaos-attuned blobs at 100 Madness (✅ midday):** +25% damage, no Morale loss (so they never desert), and their madness only ever hits enemies (a Good one only lashes at enemies; a Neutral one keeps its things).
- **Alignment (✅):** a hidden score −100..+100, labels at ±34 (±10 / ±25 a choice; the choices come with B7 events). **The class sets the start:** a Neutral blob promoted into a Paladin class becomes Good (+50), into an Assassin class Evil (−50). The full class list is open ("Pressing").
- **The blob's book page** has a Morale / Madness / alignment note.
- **Bots:** the fuzzer swings moods and talks deserters down. 47 games, 0 problems.
- **Tests: 1148 pass** (+33):
  - the starts; labels at ±34;
  - the table for Good, Neutral and Evil; a real kill moves the killer;
  - Resolve and Faith on losses, gains and Madness;
  - inspired (work and +5% on a real hit), shaken;
  - heavy damage → Madness;
  - the warning, the talk-down, nobody to talk, deserting (orders refused, gone), the Ward stopping it;
  - Madness stays at 100; a Good episode hits a crewmate and refuses orders; alignment doesn't flip;
  - a Chaos blob hits no ally, loses no Morale and does +25%;
  - an Evil episode heals the weakest ally; a Paladin starts Good.
  - The harness (+3 PASS): a real TALK DOWN click. **R1 checks: all passed.**
- **Shots:** `handoff_images\2026-10-26_morale\` (4, `_INDEX.md`).
- **New PLACEHOLDERs (all in `Morale`):**
  - the table's numbers;
  - the talk-down +20 and +0.5 per Charisma; the Ward lifting Morale to 10;
  - the drift (1 a minute) and the Ward's Madness easing (1 a minute);
  - wandering (5% a minute, 5 s);
  - episodes (30 s, 60 s calm); a mad blow a second; 3 hp/s tending.
- **Files:**
  - new: `Morale`;
  - changed: `CrewMember`, `Fortress`, `InfoSpec`, `InfoPanel`, `FortressView`, `LogicTests`, `QolShots`, `BotPlaytest`.

## Save 58: the afternoon answers (posts, duo damage types, the Axe, the Mana Blade, gem staffs)
Your answers: `roadmaps/archive/2026-10-26_answers_afternoon.md`.
- **Terms (✅):** mounts on Stations, conduits on Pedestals, devices on Anchorstones; **the spot a blob works from is its post.**
  - Every card and notice that said "station" for a post now says **post**: "nobody on its post", "ON THE POST", the POST tag, "On the post".
  - Workbench / Furnace cards say "Device · Anchorstone", not "Station · Anchorstone".
  - "Engine" became "Mount" in what you see ("Mount · Station", the shop's "Mount · any station", "drag a mount onto it").
  - "Station" stays only for the mount slot.
- **Putting a blob on a post (✅):**
  - **right-click the hitbox** of a mount or its station, a conduit or its pedestal, a device or its Anchorstone (or an empty Anchorstone), the Loom, or the training stand, and the selected blob walks to its post (`Fortress.postOf`);
  - **right-clicking a floor square never puts it on a post**: if the square's spot is a post, it goes to the nearest free spot that isn't (`Room.floorPadNear`).
  - The help line says so.
- **Posts everywhere (✅):** the Storeroom's Anchorstone has a post now, so a Workbench or Furnace set down there can be worked.
- **Duo damage types (✅, the duo-affinity rule):** each half is **60% of the hit and meets armour as its own type** (120% against no armour); `Hit.type2`.
  - **The Spellsword is Magic + Slashing** (was Slashing).
  - **The Axe is Slashing + Bludgeoning.**
  - Cards and pages read "Magic + Slashing".
- **The Axe (✅):** the Furnace smelts a **Hammer into a Molten Hammer**; **Molten Hammer + (Hammer) → Axe**.
  - **Heavy + Blade** duo attunement; Slashing + Bludgeoning.
  - PLACEHOLDER: Strength + Vitality, 13 dps, a blow every 1.5 s, 30 Units.
- **The Mana Blade (✅):** **Gem + Handle**; Magic damage, scales with **Arcana + Strength**, and **takes the gem's attunement** ("Fire Mana Blade"). PLACEHOLDER 9 dps / 1.0 s, 35 Units.
- **Staffs (✅):** **Wood + any gem → that gem's staff** ("Ice Staff", the Glass Magic Staff's numbers; a Glass Gem still makes the Glass Magic Staff).
- **Orbs other than Fire and Stone (✅):** not a recipe yet.
- **The Furnace (✅):**
  - **trains Strength and Knowledge** while it smelts (and on the training stand);
  - a stack goes in and smelts **one at a time**, the output piling up (up to 20).
- **Art:** the Dagger (it had none in save 55, sorry), the Axe, the Mana Blade, and staffs and Mana Blades tinted by their gem.
- **Alignment by path (✅ "holy Good, shadow Evil"):** a class starts its blob Good or Evil from the nearest of these up the tree (`Morale.CLASS_ALIGNMENT`):
  - **Good:** the Priest branch, the Initiate Paladin branch, the Angelic Warrior.
  - **Evil:** the Novice Dark Mage branch, the Thief branch, the Assassin branch, and the fallen ones under the holy branches (the Unholy Archbishop, the Oathbreaker, the Demonic Warrior).
  - It only applies while the blob is still Neutral. Say if any branch should move.
- **Bots:** unchanged. 47 games, 0 problems.
- **Tests: 1167 pass** (+19):
  - the Storeroom's post; every thing's post;
  - a floor square under a post gives a non-post square;
  - the Axe's and Spellsword's types; a duo hit at 60% + 60%;
  - Hammer → Molten Hammer, training Strength + Knowledge; the Axe; 3 Metal smelting one by one;
  - a Fire Mana Blade; an Ice Staff;
  - the path rule (Priest, Saint, Unholy Archbishop, Necromancer, Oathbreaker, Angelic Warrior, a plain class).
  - The harness (+3 PASS): a real right-click on the Furnace sends Aldric to its post; a right-click on the floor square under a post doesn't. **R1 checks: all passed.**
- **Shots:** `handoff_images\2026-10-26_afternoon\` (3, `_INDEX.md`).
- **Files:** `Hit`, `PersonalWeapon`, `Crafting`, `Furnace`, `Item`, `TrainingStand`, `Morale`, `Room`, `Fortress`, `InfoSpec`, `InfoPanel`, `FortressView`, `WeaponArt`, `ItemSlots`, `ShopPage`, `LogicTests`, `QolShots`.

## Save 59: the Loom-Web (R7 / P8)
Your answers: `roadmaps/archive/2026-10-26_answers_afternoon.md` (the Loom-Web part).
- **The tapestry (✅ shape, PLACEHOLDER counts):** a new game is a generated Loom-Web (`Fortress.newRun`).
  - **10 columns** of **2–4 knots**; each knot strings to **1–3** knots in the next column; every knot can be reached.
  - Column 1 is a Bandit Camp; the last column is the miniboss, alone.
  - The string's type says what waits at its knot:
    - **Chaos** (red): a bandit camp;
    - **Bounty** (gold): a Goblin Shop or a Treasure Cache;
    - **Shrine** (blue): the Sage, a Healing Spring, a Faith Shrine or an Attunement Shrine;
    - **Siege** (black): an enemy fortress (a hostile camp until save 60);
    - **Mystery** (violet): any of them.
  - 💡 kept from the draft: a Bounty or Shrine string at least every other column.
  - You see strings **2 columns ahead**; past that is fog.
- **The Unspoolers (✅ "after 2 sleeps"):**
  - The front waits 2 sleeps, then frays **one column a sleep**.
  - If it reaches your column, **Unspoolers board** through No Man's Land: 2 + 1 per 3 columns (PLACEHOLDER), white-thread blobs.
  - **Their touch raises Madness +6**, once a boarding (the draft's table).
  - You can't sleep while they're aboard.
- **The Loom-Web screen (new, full-screen, pauses the game):**
  - a dark woven cloth; strings coloured by type; **your fortress is the glowing knot**;
  - fog past 2 columns; the Unspoolers' **white frayed front** eats the cloth from the left, with a dashed line where it will be after your next sleep;
  - hovering a knot says its string type and what it leads to, and warns if the front would catch you there.
  - **SLEEP on the Loom card opens it.** Click a glowing knot to weave there, or press **Rest here** to stay a day (the front still comes).
  - The header's new **Tapestry** button opens it any time, to look.
- **The new knots:**
  - **Treasure Cache** (Bounty): an unguarded chest with 3 rolls of loot (PLACEHOLDER), worked like the camp's chest.
  - **Healing Spring:** the first blob in heals the whole crew to full and mends a bar in every room. Once.
  - **Faith Shrine:** the first blob to kneel lifts everyone's Morale (+10, +0.5 a point of Faith; PLACEHOLDER) and learns Faith (30 XP, PLACEHOLDER).
  - **Attunement Shrine (✅ re-attune only):** a blob holding a gem or a weapon there re-attunes it for **one Quintessence** of the new attunement from the rack. The room card lists a button per Quintessence you have.
  - New art for all four on the map, and room-card sections.
- **After the miniboss (✅):** **Deeper** (a new tapestry, the next floor, XP ×1.5 a floor, the fortress mended) or **Home** (the run ends: a "Run complete" card like the game over).
- **The header** reads "Column c/10 · <knot>" and only names the next knot when there's one way on.
- **Fixed:** the label under the east side crashed on any knot that wasn't a camp, shop, Sage or boss.
- **The test route stays** for the tests and bots: SLEEP still just sleeps there.
- **Bots:**
  - two new **weaver** games on generated tapestries: the turtle picks Shrine and Bounty strings, rests once, sends a blob into each shrine, and carries each cache home item by item; Deeper once, then Home;
  - every third fuzz game is on a tapestry, with a new fuzz action: weave, rest, Deeper or Home, re-attune.
  - The stuck-walking check now ignores a blob waiting at the locked treasure door (that wait is the rule: kill every enemy first).
  - 49 games, **0 problems**.
- **Tests: 1205 pass** (+38):
  - the tapestry's shape: columns, links, reachability, the peaceful-string rule, sight;
  - weaving, the front's grace and pace, resting, being caught and boarded;
  - the Unspooler's Madness touch;
  - the cache, the spring, the Faith shrine, the Attunement Shrine (with and without a Quintessence);
  - a later Chaos knot (the camp back, neutral); a Siege knot (hostile for now);
  - Deeper (floor 2, mended, XP ×1.5) and Home.
  - The harness: the Tapestry button opens the page; no weaving before the Loom is woven; SLEEP opens it; a click on a knot weaves; the front after 2 sleeps; each new knot reached on a fresh run and used; the Attunement Shrine re-attunes; Deeper and Home. **R1 checks: all passed.**
- **Not built yet:** the enemy fortress for Siege knots (save 60); the R6 sanctuary that Home leads to; "dying deep loses blobs" waits for R6.
- **Known gap (not new):** the RESTART button on the game-over card does nothing in the real game (nothing sets `setOnRestart` in `Fateweavers.main`). It's the same on the new Run complete card.
- **Shots:** `handoff_images\2026-10-26_loom_web\` (12, `_INDEX.md`).
- **PLACEHOLDER numbers:** `LoomRoute.COLUMNS 10, MIN_ROWS 2, MAX_ROWS 4, ARRIVAL_UNITS 10`, the string odds (Chaos 35%, Bounty 20%, Shrine 20%, Siege 10%, Mystery 15%) and the odds inside each; `Fortress.CACHE_ROLLS 3`, `FAITH_SHRINE_MORALE 10`, `FAITH_SHRINE_PER_FAITH 0.5`, `FAITH_SHRINE_XP 30`, `UNSPOOLERS_BASE 2` (+1 per 3 columns), the Unspoolers' level and stats.
- **Files:** `LoomRoute`, `Fortress`, `CrewMember`, `PersonalWeapon`, `Fateweavers`, `LoomPage` (new), `FortressView`, `InfoPanel`, `DeviceArt`, `LogicTests`, `BotPlaytest`, `QolShots`.

## Save 60: the evening answers, part 1 (the Mana Blade's duo, any orb, RESTART)
Your answers: `roadmaps/archive/2026-10-26_answers_evening.md`.
- **The Mana Blade (✅ revised):** a melee magic dagger.
  - Its attunement is a duo, **the gem's + Finesse**: a Glass Gem makes an **Arcane + Finesse** Mana Blade, a Fire Gem a **Fire + Finesse** one. Finesse gives it the +10% crit.
  - Its damage is a duo type, **Magic + Piercing** (each half 60%, the save 58 rule).
  - It still scales with Arcana + Strength (save 58), 9 dps a blow a second, 35 Units (PLACEHOLDER). Its page reads "Magic + Piercing · melee · Arcane + Finesse".
- **Any Quintessence + a Glass Orb = that orb (✅):** Fire and Stone make their own shields as before; **any other attunement makes a Mana shield carrying it** ("Ice Orb"), drawn in its colour.
  - An attuned orb isn't a Glass Orb in recipes (no orb from an orb).
  - **A matching operator recharges an attuned orb faster**: +10% × match power (the Ward's rule; PLACEHOLDER). That goes for the Fire and Stone Orbs too (Stone: its charge-up).
- **RESTART (✅):** on the game-over and Run complete cards, it now **starts a fresh run**: a new tapestry and a fresh crew, as if the game had just been launched (`Fateweavers.main` builds a new fortress and view and swaps them in). The sanctuary (R6) replaces this later.
- **Bots:** unchanged; 49 games, 0 problems.
- **Tests: 1217 pass** (+12):
  - the Glass and Fire Mana Blades (duo attunement, duo damage, Finesse);
  - an Ice Orb from Quintessence of Ice; an Ice Orb isn't a Glass Orb; Fire still makes the Fire Orb; the Ice Orb blocks what a Glass Orb blocks;
  - the match bonus (none for a Glass Orb or an unattuned operator; faster for an Ice operator).
  - The harness (+2 PASS): a real click on RESTART asks for a fresh run; the Ice Orb on the shield pedestal. **R1 checks: all passed.**
- **Shots:** `handoff_images\2026-10-26_evening\` (`_INDEX.md`).
- **New PLACEHOLDERs:** the attuned orb's +10% × match power.
- **Files:** `PersonalWeapon`, `Crafting`, `Fortress`, `WeaponArt`, `Fateweavers`, `LogicTests`, `QolShots`.

## Save 61: the camp's devices are our device objects (the camp merge)
Your answer (evening): merge them into our device code.
- **What changed (no visible difference in play):** the camp's four devices are now the same classes as yours.
  - The ballista is a **Mount** with a **Ballista engine** on the Battlement's first station; its reload is the mount's own charge.
  - The Glass Gem is a **Weapon** on the second station; its charge is the gem's own.
  - The shield is a **ShieldConduit** (a Glass Orb) on the shield's pedestal; its bars and recharge are the orb's.
  - The campfire is a **Campfire** on the quarters' Anchorstone.
  - They aren't in your lists (your mana bar, your mounts column); the camp works them (`Fortress.updateCamp`), with the same formulas as before.
- **Stealing takes the very thing:** the ballista's own engine, the campfire itself. A stolen gem or orb moves to your pedestal room as the same kind (a fresh charge).
- **A new band brings new device objects** (`BanditCamp.newGear`).
- **Why it matters next:** system damage (save 62) and the Siege fortress (save 63) work on device objects, the same on both sides.
- **Tests: 1228 pass** (+11): the four objects, their rooms and stations, not in your lists; the reload as the mount's charge; the orb recharging its own bars; stealing the very engine and the very campfire. Older tests now set the gem's charge and the orb's bars. **R1 checks: all passed.** Bots: 49 games, 0 problems.
- **New PLACEHOLDERs:** none.
- **Files:** `BanditCamp`, `Fortress`, `InfoPanel`, `LogicTests`.

## Save 62: system damage (both sides, FTL-style)
Your answers (evening): mount shots do room damage **and** system damage; a damaged system is unusable until repaired; both sides; one hit breaks one, repaired like a room bar.
- **What breaks:** a **mount's shot** (a Ballista or Scorpion bolt, yours or the camp's) that takes a bar off a room also **breaks one system standing in it**, picked at random. A gem's spark, a mana blast, a boarder's blows and the Goblin Boss's rocks take bars but break nothing.
- **What's a system:** a mount on its station (the Ranger Platform and the Stage too), a conduit on a pedestal (a gem, the shield orb), a device on an Anchorstone (the Workbench, the Furnace, the Permeator, a campfire), and the Loom. The camp's ballista, Glass Gem, Glass Orb and campfire too.
  - **Calls (say if you want them different):** the **Chrysalis Ward** and the **storage rack** aren't systems (healing, promotions and your items stay safe); the training stand isn't either.
- **A broken system is out:**
  - it powers off (its mana or stamina frees) and can't be powered again: "The Ballista is broken: repair the Mount Room first";
  - the Loom keeps its weave but doesn't weave; a broken Permeator stops (the Aether drains); a broken shield has no bars; the Workbench and Furnace wait (their progress kept); a broken campfire heals nobody;
  - the camp's broken ballista or Glass Gem stops firing ("Broken" on its card and its gunner's tag), a broken orb gives them no shield, a broken campfire no rest.
- **Repairs:** whoever repairs the room mends **the bars first, then each broken system at the same speed** (4 s, Knowledge speeds it; PLACEHOLDER, the room-bar rule). Whoever is in the room repairs, its own operator too. The camp's crew mends theirs the same way.
  - The shop's repairs count a broken system as a bar (a call), and going **Deeper** mends them too.
- **On the map:** a red **BROKEN** tag with a crack and a few sparks under a broken device; "Ballista broken" / "Ballista fixed" float up. The room card lists "BROKEN: Ballista" under its chips. Statuses read "Broken: repair the room".
- **Bots:** the turtle now sends its last free blob to repair any room with a broken system and nobody in it. Each game reports systems broken and fixed. 49 games, **0 problems**.
  - **Balance note:** system damage makes the camp much harder. The turtle used to reach node 4 in 4 minutes; now it clears the camp but doesn't get further (its Loom, shield or Permeator keep breaking). The ballista's 14 s and the 4 s repair are the knobs.
- **Tests: 1250 pass** (+22):
  - what counts as a system in each room (not the rack or the Ward); the camp's Battlement;
  - a camp bolt breaks your Ballista (an event, it can't be powered, its status, it can't fire, the shop counts it);
  - a non-mount shot breaks nothing; repairs mend the bar first, then the Ballista at the bar speed; fixed, it powers again;
  - a broken Permeator stops; a broken Workbench crafts nothing until its crafter repairs it; a broken Loom doesn't weave;
  - your mount's bolt breaks the camp's ballista or Glass Gem; a gem's shot breaks nothing; going Deeper mends.
  - The harness (+2 PASS): a real camp bolt breaks the Ballista; the camp's Battlement losing a device. **R1 checks: all passed.**
- **Shots:** `handoff_images\2026-10-26_evening\` 03–04.
- **New PLACEHOLDERs:** a system mends at the room-bar speed (4 s); a broken system costs a bar at the shop.
- **Files:** `Weapon`, `ShieldConduit`, `Loom`, `AnchorDevice`, `Permeator`, `Mount`, `Projectile`, `GameEvent`, `Fortress`, `InfoPanel`, `FortressView`, `LogicTests`, `QolShots`, `BotPlaytest`.

## Save 63: the Siege knot's enemy fortress
Your answers (evening): rooms, 3 crew, the shooter, Aether on both sides, kill all via the gate; and (17:31) **an Entrance Hall like yours**.
- **The fortress (✅), five walled rooms, facing west like yours:**
  - **Entrance Hall**: their gate onto No Man's Land, the only way in. Their guard goes out to No Man's Land and falls back behind the gate, like the camp's.
  - **Mount Room**: a **Ranger Platform** with their shooter on it.
  - **Pedestal Room**: a **Stone Orb** (a Stone shield) worked by their shield operator.
  - **Utility Room**: their **Permeator**.
  - **Treasure Room**: locked until every enemy is dead; the chest's loot (3 rolls, never empty) and **the spare quiver**.
  - Drawn behind a stone wall with crenellations, on a stone floor; "ENEMY FORTRESS · Hostile · Stone shield up" under it.
- **Three crew (✅):** the shooter, the shield operator, a guard (Level 1, like the camp's band; PLACEHOLDER).
- **Hostile from the start** (no sleeping while they live). **Win (✅): kill everyone**, in through their gate. Taken: the Treasure Room opens and you get **50 Units** (PLACEHOLDER; the camp pays 30).
- **Walls (✅):** their rooms are indoor: nobody shoots in or out, except from a Ranger Platform (yours or theirs, the platform rule).
- **Their shooter (✅):** a **Longbow and a quiver** (16). On the Platform (2 s climb) he shoots the nearest of yours **in range (his bow + 2 tiles), any room**. Out of arrows, he **fetches the spare quiver from the Treasure Room** (leaving the empty one there) and climbs back up. With none left he climbs down and fights.
- **Their Stone shield (our Stone rules):** it charges up (20 s, while its operator is on it), then stands with 50 health a bar.
  - While it stands, your **piercing and slashing shots bounce off** (ballista bolts, arrows), **blunt hits (x3) and magic wear it**, mental and neutral pass, and **nobody of yours gets through their gate**.
  - Shattered, it charges up again. A stony wash over the fortress while it stands; a charge / health bar beside the orb.
- **Their Aether (✅ both sides):** their rooms breathe their Permeator's Aether, door to door.
  - **Break it** (a mount's bolt, system damage) or **steal it** (a Thief): their rooms vent. Whoever is inside starts to suffocate, your boarders too.
  - **A stolen Permeator** set down on one of your empty Anchorstones fills that room too (no mana; a call).
- **System damage** works on their Ranger Platform, Stone Orb and Permeator.
- **Cards:** Enemy Pedestal (the Stone Orb: charge or health, operator, steal slot, what bounces), Enemy Mount (the Ranger Platform: the shooter, his arrows, range), Enemy Permeator (their Aether, broken / stolen). The room cards list them; the shooter's tag reads Shooting / Climbing on / Out of arrows.
- **Fixed on the way:**
  - a Siege knot never ran the camp's turn (save 59);
  - the chest of a node you leave is now really emptied (the check came after the room list was cleared).
- **Bots:** two new **siege** games (the turtle with the Fire Gem on their shield and the ballista on their Mount Room; once the shield is down, the melee crew storm the gate). Both times **the fortress held** for 7 minutes: the gem wears the Stone shield down, but it charges back up before the storm gets through. 51 games, **0 problems**.
  - **Balance note:** the Siege is hard as built. The knobs are the shield's 50 health a bar and its 20 s charge-up, and the shooter's arrows.
- **Tests: 1277 pass** (+27):
  - the five rooms (walled, theirs), the only gate; the devices; three crew; hostile; the shooter's bow and quiver; the spare quiver;
  - no shooting into their rooms from No Man's Land; the shield charges, stands and keeps you out;
  - the shooter climbs on and shoots Brenna; out of arrows he fetches the spare and leaves the empty one;
  - piercing bounces, magic wears it, blunt shatters it, the gate opens up;
  - everyone dead: taken, the Treasure Room open, +50 Units; a broken Permeator vents the Utility Room and Aldric, inside, suffocates;
  - a Thief steals their Permeator; weave on and the camp comes back; the stolen Permeator fills the Training Room.
  - The harness (+5 PASS): the fortress on the map, the shooter up, the shield standing, the Permeator venting, taken. **R1 checks: all passed.**
- **Shots:** `handoff_images\2026-10-26_evening\` 05–09.
- **New PLACEHOLDERs:** `Fortress.SIEGE_CLEARED_UNITS` 50; the three crew at Level 1; the chest's 3 rolls; the stolen Permeator's free fill.
- **Files:** `BanditCamp`, `Room`, `Fortress`, `InfoPanel`, `FortressView`, `LogicTests`, `QolShots`, `BotPlaytest`.

## Save 64: your 17:51 notes (A–E) and the corridor rule
- **A:** the room pictures on room cards no longer have door labels ("Entrance Hall ↓" and the like).
- **B (✅ one word):** the tag under a blob on its post, in a room picture, is just its **name** ("Brenna", "Blob").
- **C:** the Chrysalis crystal is 40% bigger and its middle sits on the Ward's centre (its hitbox follows).
- **D, stations (✅ "redesigned to fit everything"):** every mount now sits on **one station design**: a low, wide stone drum with an iron band and rivets, and a wooden turntable on top.
  - The Ballista, the Scorpion, the **Ranger Platform** and the **Stage** all stand on it (yours, the camp's ballista, the Siege's Platform).
  - An empty station shows its four bolt holes. A blob on the Platform or the Stage is drawn up on the station's height too.
  - The Platform's and Stage's card pictures stand on it as well.
- **D, Anchorstones:** every device on an Anchorstone now visibly stands on its **rune slab** (a ring of blue runes around its foot): the Workbench, the Furnace, the storage rack, the Permeator (yours and the Siege's), a campfire (stolen or the camp's), the chests.
- **E, the Siege's layout (✅ your sketch, room by room your pick):**
  - a **3×2 Entrance Hall** on No Man's Land (their gate), the **Mount Room above it**, the **Pedestal Room below it**, the **Utility Room to its right**, the **Treasure Room** past that;
  - the Mount and Pedestal Rooms sit over the hall's right two tiles, so the column next to No Man's Land stays clear.
  - The wall follows the cross shape.
- **The corridor rule (✅ 18:01, "there can never be rooms within the red box"):** beside No Man's Land only the entrance halls stand.
  - In the column next to it on each side, a room may only fill the entrance's two rows; nothing but No Man's Land stands in its own columns.
  - A new test checks every kind of knot.
  - **One change it forced:** the **Goblin Boss's arena** (a 6×6 field that touched No Man's Land top to bottom) now sits one column further east, behind a 2×2 **Arena Approach**.
- **Bots:** unchanged; 51 games, **0 problems**.
- **Tests: 1286 pass** (+9): the corridor rule at every knot type (9); the boss's approach door; the Siege's vent check waits longer (the bigger hall shares its Aether). **R1 checks: all passed.**
- **Shots:** `handoff_images\2026-10-26_evening\` 10–11.
- **New PLACEHOLDERs:** none (art only).
- **Files:** `DeviceArt`, `FortressView`, `InfoPanel`, `Fortress`, `LogicTests`.

## Save 65: the siege bot takes the fortress; a fix to the Siege's Stone shield
- **Fixed:** a Siege's standing Stone shield also kept your blobs **inside** their fortress from leaving through its gate. It now only keeps you **out** (a blob already in can walk back out). Found by the new siege bot (+1 test).
- **The siege bot plays it properly now:** the Fire Gem and the ballista both aim at their Pedestal Room. The gem wears the Stone shield down, and once it's down a bolt can break the Stone Orb (system damage). Whenever the shield is down or broken, everyone off the guns storms, nearest enemy first.
  - **Both siege games now take the fortress** inside 10 minutes, losing 2 of the 4 crew: the guard and the shooter fall early (the first storm gets in before the shield's first charge), and the shield operator holds the Pedestal Room until the gem finishes him.
  - That's the P10.2 data: the Siege can be won, but it's slow and costly.
- **Bots:** the siege games run 10 minutes (were 7). 51 games, **0 problems**.
- **Tests: 1287 pass** (+1). **R1 checks: all passed.**
- **Files:** `Fortress`, `LogicTests`, `BotPlaytest`.

## Save 66: system integrity (your 18:15 answers), the stolen Permeator, the Mana Blade's scaling
Answers recorded in `roadmaps/archive/2026-10-26_answers_night.md`.
- **System integrity replaces "one hit breaks it" (✅ P10.1).**
  - Every system (a mount, a gem, a shield orb, the Loom, a device on an Anchorstone, both sides) is at 100%. Each bolt that takes a bar also takes **1 point** of system damage, and each point takes **33%** off **every** number the system has (✅ "every number scales"):
    - mounts and gems: charge time ×1.5 at 67%, damage ×⅔;
    - shields: bars, Stone health and recharge speed;
    - the Loom: weave speed;
    - the Permeator: Aether fill;
    - the Workbench and Furnace: craft and smelt speed;
    - campfires: heal rate.
  - 3 points = 0% = **broken** (off, as before).
  - **Repairs:** the bars first, then the system **one point at a time**, each at a bar's speed. A **fully destroyed** system's first point takes **twice** as long (✅).
  - Maxima come back on repair, but current values have to recharge. Your example works exactly: a 90-health Stone shield hit once is at 60 / 60; repaired, it's at 60 / 90.
  - The shop and Deeper count every point as a bar.
- **On screen:** a damaged system wears an amber "67%" / "33%" tag (no sparks) and a floater ("Ballista 67%"); broken keeps the red BROKEN tag. The room card says "DAMAGED: Ballista 67%". New event `SYSTEM_DAMAGED`.
- **Calls 1–8 and 11 confirmed** (no change). **The Siege stays as it is** ("the last prompt balanced it").
- **A stolen Permeator (✅ P11.9):** it now **costs a mana like yours** and **runs only in one of your Utility Rooms**.
  - It shows up as a "Stolen Permeator" pip in a new **Devices** mana column, and its room card has its own power button.
  - Your three Utility Rooms start full, so lift something off an Anchorstone to make room for it.
- **The Mana Blade scales with Arcana + Dexterity** (✅ P11.10; was Arcana + Strength).
- **The bots:**
  - The turtle beats the first camp again; system damage is now far gentler, with 6–10 points taken and repaired per game and **0 systems fully broken** in the scripted games.
  - Weaver fix: once no hostiles remain, the bot frees mana for the Loom (staffs, then the shield) and puts the Loom's post first. A damaged system no longer powers itself off, so nothing freed that mana before.
  - 51 games, **0 problems**.
- **Tests: 1306 pass** (+19, several rewritten for points). **R1 checks: all passed.**
- **PLACEHOLDER:** rounding shield bars to the nearest whole bar at 67% / 33% (a 1-bar shield stays at 1 bar until it's broken).
- **Shots:** `handoff_images\2026-10-26_night\` 01–02.
- **Files:** `Fortress`, `Weapon`, `ShieldConduit`, `Loom`, `AnchorDevice`, `GameEvent`, `PersonalWeapon`, `FortressView`, `InfoPanel`, `LogicTests`, `QolShots`, `BotPlaytest`.

## Save 67: your picked icons (design thread A1, A2, A4)
- **The icons you picked are in the game,** drawn from your SVGs.
  - **New `SvgIcons`:** reads `IconSvgDefs` (your picks, verbatim) and turns them into shapes on the 100 × 100 grid. It handles the subset the picks use: rect, circle, ellipse, polygon, line, path with arcs, g, use with rotate, strokes, and masks where white keeps and black cuts.
  - It was checked against the same SVGs rendered in a browser; they match.
- **52 attunement icons** (Void, Speed, Stone … Deflection) now draw wherever an attunement icon shows: the attunement page insignia, chips, cards and the tree.
  - A child with no icon of its own uses its parent's.
  - **Still on the old drawn icons:** Fire, Water, Poison, Ice, Earth, Air, Lightning, Dark, Holy, Arcane.
  - **Still on the letter fallback (no pick yet):** Lava, Slowness, Metal, Space, the Loom weave and the door ward. These go to Pressing as a small follow-up.
- **11 attribute icons** sit next to each attribute on the crew card's Attributes rows and on a weapon page's "What the wielder adds" rows, in the row's colour.
- **The stats-book button** is now the book with a star (hover fill and ring kept).
- **Tests: 1306 pass. R1 checks: all passed** (+3 icon checks). **Bots:** 51 games, **0 problems**.
- **Shots:** `handoff_images\2026-10-26_icons\`: 00 the contact sheet of all 64, then the crew card, the Shadow page and the Mana Blade page.
- **Files:** new `SvgIcons`, new `IconSvgDefs`; `AttunementIcons`, `BookPage`, `InfoPanel`, `QolShots`.

## Save 68: the event screen, layout C (design thread C2) + the long-shot rule (your 18:40 answer)
- **The event is a white dialogue bar across the bottom of the screen, over the dimmed map** (✅ layout C). It's built from `design/cards/event_screen.md`.
  - **Left:** a tall picture labelled EVENT. For the Sage: a hooded old blob with a white beard, leaning on a staff topped with a glowing orb, with drifting motes.
  - **Right:** the title, then "<blob> is talking · click a blob on the map to swap". The selected blob talks and rolls; with nobody selected, the first standing blob does. Clicks above the bar still reach the map.
  - **Below that:** the event text, then the choices in two columns. Each choice has the roll's **attribute icon** in a round badge (the save 67 icons), the line, "Attribute · difficulty" under it, and the **chance in %** on the right.
  - Aggressive choices are red, with "· aggressive".
  - **After the roll:** the bar shows who rolled what and at what chance, then the result and a **Continue** button.
  - **Opening and closing:** the bar opens by itself when you arrive at the event. × or Continue hides it, and the Sage's room card has a **Talk to the sage** button to bring it back.
  - The room card no longer holds the choices.
- **The rules (✅):**
  - **Attribute choices never lock.** Every one can be attempted, and the chance never goes below **1%**.
  - **Recommended attribute 20** (was a hard lock at 40). Below it a choice is a **long shot**: the chance and the sub-line show in amber, "· long shot (best at 20+)".
  - **Your 18:40 answer:** a long shot's chance = the normal chance × (attribute ÷ 20). For example, Knowledge 10 on the hard debate = 31% × ½ = 16%.
  - **Attunement locks stay real locks** ("Show him the fate-thread": greyed, "Needs Attunement Lesser+").
- **Logic:**
  - `Choice.recommended`, `isLongShot`, and the scaling and floor in `chanceFor`; `SkillCheck.MIN_CHANCE`.
  - The Sage's debate and mind-read are recommended at 20 (were locked at 40).
  - `Fortress.choose` rolls at `chanceFor`, and the scene keeps the chance it rolled at.
- **Tests: 1310 pass** (+4). **R1 checks: all passed** (+5 event checks, by real clicks on the bar). **Bots:** 51 games, **0 problems**.
- **Shots:** `handoff_images\2026-10-26_event_screen\` 01–03.
- **Files:** new `EventBar`; `CardArt` (the Sage picture), `FortressView`, `InfoPanel`, `Choice`, `SkillCheck`, `EventScene`, `Fortress`, `LogicTests`, `QolShots`.

## Save 69: the class card, C2 (design thread C0; R5 §4 step 2)
- **The class tree screen's side card is now your C2 card.** It's built from the canvas file `CardC2.dc.html` on a dark book page (#1f1a22, white motes), top to bottom:
  - the **lineage crumb**;
  - the class's **blob model on a glow in its attunement's colour** (white-edged so it reads on the dark), with its **giant faint insignia** behind;
  - the **name in the attunement's colour** and "Path · Tier · status";
  - the **attunement row** (colour disc and icon, or the duo disc; the name);
  - the **attribute focus as 10 chips** (★ first in white, +1 second, then the rest with their icons), and "★ X +2 a level · Y +1 a level" under them;
  - **Perks (n)** (click to expand) beside a dashed **Ability: Locked** box;
  - the **lineage strip** (parent › this, filled in its colour › children, "+n" for more; click one to fly there);
  - for a blob, **the gate as two bars** (Level, ★ favourite) with a tag (Not yet in fire red / Gate met in green / Tier cap N in white) and no numbers;
  - **the button:** NEEDS LEVEL N or NEEDS <ATTRIBUTE> N (dark, does nothing), **PROMOTE!** (gold) or **OPEN CLASS PAGE** (white).
- **Perks shown** are the three decided so far, inherited down the path: Heavy Melee reinforce doors without a pad, Supporters +20% at Anchorstones, Thieves steal devices. The full list is B3.
- **OPEN CLASS PAGE** goes back to the fortress with the class's info page open in the book.
- **One gap the design didn't cover:** a class that's unlocked while the blob isn't in the Chrysalis Ward shows a dim "PROMOTE IN THE CHRYSALIS WARD" button. The old card said "Unlocked: go to the Chrysalis Ward".
- **Tests: 1310 pass. R1 checks: all passed** (+4 class card checks: NEEDS does nothing, PROMOTE! promotes, OPEN CLASS PAGE opens the page, a Thief's perk). **Bots:** 51 games, **0 problems**.
- **Shots:** `handoff_images\2026-10-26_class_card\` 01–05: locked, ready, current, a Thief with perks open, Unclassed.
- **Files:** `ClassTreePanel` (the card), `ClassTreeView`, `FortressView`, `Fateweavers`, `QolShots`.

## Save 70: blob renderer parity with tools/BlobPreview (design-thread build brief §1)
- **The game now draws every model in `class_blobs.txt` (191: T0–T3 and all 60 Magic T4) the same as `tools/BlobPreview`.**
  - **Checked by a new pixel-by-pixel comparison** of both renderers on every model (`test/fateweavers/swing/BlobParity.java`, run `blobcheck.bat`; it writes side-by-side sheets to `test\blob_parity\`).
  - **Before the fix, 16 models drew differently. Now 0 do,** apart from the preview's own red debug marks (below).
- **The fix:**
  - **Order inside a part:** a `cut` now carves every shape before it, and shapes after it are added back on top. The game used to carve only the shape just before the cut in `arms`, `legs` and `feet`. That broke hands over back items (Blade Dancer, Bulwark, Defender, Evade Tank, Viking, Crusader, Harlot, Artist, Fencer, Netcaster, Trapper, Pistolero, the Shieldbearer…).
  - **`line`** takes its width from its 5th number only (default 5), as the preview does; `stroke` no longer changes a line (Spellblade, Spellsword).
- **Already the same, checked:** `stroke W` outlines, `cut stroke W`, coordinates outside 0–100, `dark` with several parts, `belt` / `band` / `eyes none` / `eyes … size brow`, `legs none` / `feet none` / `arms hidden` / `extra none`, a child's part replacing the parent's whole part, and the draw order arms → body/legs/feet → head → extra.
- **Two places still drew the family model, now the class's own:**
  - **the crew card's big picture** (it drew the T1 family model);
  - **the class card** (a class without a model of its own now wears the nearest model above it, as on the map).
- **For you: models I didn't touch (rule: fix the renderer, not the models):**
  1. **An eye pokes outside the head** (the preview draws it red) in **T2_HURLER, T3_BOMBER, T3_ARCANE_WIZARD, T4_UNKILLABLE_JOE and T4_RITUALIST** (the Ritualist inherits the Arcane Wizard's narrow hat-tip head). In the game the part outside the head draws white on a white edge.
  2. **Two model ids don't match a class:**
     - `T3_SHADOWSTALKER` (parent Bowman) looks meant for the tree's **T3_HIGHWAYMAN**;
     - `T3_COMMANDER` (parent Squire) for the tree's **T3_STRATEGIST** (the tree's Commander is T4).
     - Until they're renamed, the Highwayman and the Strategist wear their T2's model.
- **Tests: 1315 pass** (+5: the file loads with no warnings, 191+ models, all 60 Magic T4 have one, the arms' cut order, `none` parts). **R1 checks: all passed** (+2). **Bots:** 51 games, **0 problems**.
- **Shots:** `handoff_images\2026-10-26_blob_parity\`: 20 sheets (every tier, the game over the preview) and 2 in-game shots.
- **Files:** `BlobArt`, `CrewRenderer`, `InfoPanel`, `ClassTreePanel`; new `test/fateweavers/swing/BlobParity.java`, `blobcheck.bat`; `LogicTests`, `QolShots`.

## Save 71: the approved device and item art (design-thread build brief §3) + your 20:00 answers
- **Furnace A** (the beehive kiln) and **Training stand A** (the wooden A-frame with a sword) are drawn from their SVGs everywhere the two devices appear: the map, the room plans, their cards and device rows, and when carried.
  - The Furnace still shows that it's smelting: smoke from the chimney, a flicker in the mouth and the thin progress bar.
  - The little input / output squares on the old kiln's front are gone (the card's slot row shows them).
  - Whatever is on the stand floats above the sword now (`STAND_TOP` 26 → 44).
- **The approved item icons** replace the old drawings wherever those things appear: rack, hand and device slots, the shop, book pages and blobs carrying things.
  - **Raws:** Wood, Metal, Twine, Glass, Crystal, Rock, Molten Metal, Molten Glass.
  - **Parts:** Stick, Small Blade, Blade, Arrows.
  - **Weapons:** Sword, Dagger, Axe, Hammer, Longbow, Glass Magic Staff, Mana Blade.
  - **Gear:** Quiver (empty = a grey slash), the Protection Amulet.
  - **Currencies:** Essence, Essence of Y, Quintessence, Shards.
  - **Gems and orbs** in slots.
- **Not in the game yet, so not shown anywhere:** the Backpack and the Attunement Crystal (their art is ready in `SvgArt`).
  - **Still on the old drawings (no pick):** Leather, String, Handle, the Molten Hammer, Rapier, Wrecking ball, Rapid crossbow, Spellsword, food, the notebook, crates.
- **Your answers (20:00):**
  - **Attuned items are tinted in Y's colour:** Essence of Y's liquid, Quintessence of Y's star and halo, Shards from a Y gem, a Y gem's staff and Mana Blade.
  - **Gems and orbs use the new icons in slots and the shop, tinted by attunement.** A Glass Gem or Glass Orb stays the approved pale blue. The map's pedestals keep their glowing art.
- **How it's drawn (`SvgArt`):** it follows the canvas's own CSS exactly. I checked all 30 pictures pixel by pixel against a browser render of the same SVGs, and they match (the bow is within 3%, from anti-aliasing).
- **For you (one thing the CSS does):** the canvas's outline rule overrides a shape's own stroke, so four pictures show a thin dark line where the SVG has a coloured stroke:
  - the **Longbow** (a thin dark curve, not a brown bow);
  - **Twine**'s loose end;
  - the **Amulet**'s chain;
  - the **Backpack**'s strap.

  That is how the canvas showed them, so the game matches it. Say if you meant them brown and gold.
- **PLACEHOLDER:** the icon's size in a slot (2.6 × the old half-size), against the old weapon drawings (× 1.1), and the two devices' sizes on the map (Furnace 46, stand 44).
- **Tests: 1315 pass. R1 checks: all passed** (+2 item art checks). **Bots:** 51 games, **0 problems**.
- **Shots:** `handoff_images\2026-10-26_item_art\` 01–07.
- **Files:** new `SvgArt`, `ItemSvgDefs`; `SvgIcons` (its path helpers shared), `DeviceArt`, `ItemSlots`, `WeaponArt`, `InfoPanel`, `QolShots`.

## Save 72: hands free: the weapon in the hand, armour tinting the torso (design-thread build brief §2)
Answers: `roadmaps/archive/2026-10-26_answers_design_build.md` (20:05–20:10, with the mock-up).
- **The equipped personal weapon is drawn on the blob** on the map, the crew card, portraits and book pages (`EquipArt`). Bandits with a weapon show theirs too (the Siege's shooter, his Longbow).
- **The hand (✅ "a default per path, overrides where needed"):**
  - **Path defaults** (each family's front fist): Unclassed (75, 62), Heavy (74.5, 62), Light (67.5, 58), Ranged (77.5, 64), Supporter (72, 56.5).
  - **Magic has no default:** a robe with no hands, or a Magic model without a `hand` line, **floats its weapon beside it** with a slow bob (✅).
  - **A new line in `class_blobs.txt`:** `hand x y [angle]`. It's documented at the top of the file and inherited like the eyes.
  - **I added it to 34 models** whose front hand is clearly away from their path's default: raised fists, wide arms, Magic models with visible hands. They are Novice Wizard, Slingshotter, Brawler, Medic, Earth Mage, Priest, Spellblade, Golem Smith, Runesmith, Soldier, Big Chuck, Boomeranger, Sling Master, Blowgun, Netcaster, Shadowdancer, Pugilist, Grappler, Kickboxer, Martial Artist, Fencer, Ogre-Kin, Dwarf, Evade Tank, Therapist, Bishop, Telekinesis, Inquisitor, Rune-Blade, Spellsword, Gunsmith, R&D, Spiritbound and Forgesmith.
    - Each was placed at the model's own front hand; edit any freely.
    - **Arms raised above the head or below the knee kept the default:** Throgg, Witch, Sage, Bubble Mage, Bone Crafter, Ritualist. Listed in Pressing P15.
- **The look (✅ option 3):** the weapon's silhouette in black with the blobs' white edge, and one colour accent: a blade's steel, or the gem (in its attunement's colour for a Y gem's staff or Mana Blade).
  - The silhouettes come from the approved item icons, so a weapon has the same shape in the hand and in its slot.
  - Drawn in front of the blob; it swings with the front arm and bobs with the body.
  - **Weapons with no icon of their own borrow the nearest** (a call): Rapier and Spellsword the sword, Wrecking ball the hammer, the Rapid crossbow the bow.
- **Armour (✅):**
  - **The tint:** the **torso is the body plus the shoulders** (arm shapes whose middle is in the body's top 40%). **Plate:** steel grey with a white shine streak. **Leather:** warm brown. **Mana:** the Arcane violet with a soft glow. Each keeps a thin dark edge.
  - **Who shows it:** today only classed enemies wear armour. Your crew's Armour slot stays empty until armour items exist, and then it shows the same way.
- **✅ Your ask: the Spellsword's floating sword is gone** (its sword and three glints). The backup is `class_tree/blob_options/class_blobs_backup_2026-10-26_before_spellsword.txt`.
- **PLACEHOLDER:**
  - the path defaults;
  - the float point (88, 50) and its 1.6 bob;
  - the weapon sizes (sword 52, dagger 40, hammer 50, axe 52, bow 56, staff 62, Mana Blade 50) and grip points;
  - the shoulder band (40%);
  - the armour colours.
- **Tests: 1315 pass. R1 checks: all passed** (+4 equipped checks). **Bots:** 51 games, **0 problems**. The blob parity is unchanged (191 models, only the 5 red-eye notes).
- **Shots:** `handoff_images\2026-10-26_equipped\`:
  - 00 the mock-up you picked from;
  - 01 every model holding a sword (blue names have a `hand` line);
  - 02 in the game (a Plate Knight with a sword, a Leather Blade Dancer with a dagger, a Mana Novice Wizard with a floating staff, a Longbowman);
  - 03 the Knight's crew card.
- **Files:** new `EquipArt`; `BlobArt`, `CrewRenderer`, `SvgArt`, `logic/ClassBlobs`; `class_tree/class_blobs.txt` (34 `hand` lines, the Spellsword's sword gone, the format note); `QolShots`.

## Save 73: your 20:20 notes (A–C): daggers face down, swords higher, belts for hip items
- **A:** **daggers are held blade-down** (mirrored about the grip, so the blade points down and forward).
- **B:** **swords are tilted higher**: turned 28° more upright (the Mana Blade too).
- **C:** **hip items hang from a belt instead of a headband** (your rule, and your picks of all four groups):
  - **blades:** Fencer, Swashbuckler, Master Assassin;
  - **holsters and hip gear:** Gunslinger (and its children Soldier and Sniper), Pistolero, Boomeranger, Surveyor, Trapper;
  - **tool and book belts:** Handyman, Engineer, Eldritch Scholar;
  - **the Valkyrie** (her helm stripe is now a belt at her hip, by her sword).
  - Each belt sits where its item hangs: the Fencer's rapier hilt at 60, the Swashbuckler's cutlass 64, the Gunslinger's holster and gunbelt 62, the Valkyrie 68, the others at their item.
- **Two renderer rules changed, in the game and in `tools/BlobPreview` alike** (the parity check still passes: 191 models, only the 5 red-eye notes):
  - **The nearest stripe wins:** a child's own `band` beats a parent's `belt`, so the Handyman's belt doesn't pass to the Repairman, Builder and Demolitionist, who keep their headbands. A child without its own stripe inherits the belt (Soldier, Sniper).
  - **A belt is no longer cut by the arms.** The arms are drawn behind the body, so the cut only made gaps; the Fencer's cape had hidden his whole belt.
- **`class_blobs.txt`:** 12 models changed `band` → `belt`. Backup: `class_tree/blob_options/class_blobs_backup_2026-10-26_before_belts.txt`. `BLOB_STYLE.md` has the new rules.
- **PLACEHOLDER:** the sword turn (−28°).
- **Tests: 1318 pass** (+3 belt checks). **R1 checks: all passed.** **Bots:** 51 games, **0 problems**.
- **Shots:** `handoff_images\2026-10-26_equipped\` 04 (the belted models and the new weapon angles), 05 (in the game).
- **Files:** `EquipArt`, `BlobArt`, `logic/ClassBlobs`, `tools/BlobPreview.java`, `class_tree/class_blobs.txt`, `class_tree/BLOB_STYLE.md`, `LogicTests`.

## Save 74: your 20:29 note: belt items on the side away from the weapon
- **One-sided hip items move to the back hip** (x < 50; the weapon hand is the front one, x > 50), each mirrored across the blob's middle and nudged so it still overlaps the coat or gown edge:
  - **holster:** Gunslinger (its gunbelt widened to reach it), Pistolero, Sniper;
  - **cutlass:** Swashbuckler;
  - **spyglass:** Surveyor (its barrel mostly sits under the belt, as before; the end cap shows on the left);
  - **snare loop:** Trapper (it now hangs over the bottom of his pack);
  - **sword:** Valkyrie (it angles down and back from her left hip).
- **Left as they were:**
  - already on the far side: the Fencer's rapier and the Eldritch Scholar's tome;
  - in pairs, one per hip: the Master Assassin's thigh daggers and the Boomeranger's two boomerangs;
  - no hip item, only the belt: Handyman and Engineer.
- **`class_blobs.txt`:** 15 lines in 7 models (each marked with a comment). Backup: `class_tree/blob_options/class_blobs_backup_2026-10-26_before_hip_side.txt`. `BLOB_STYLE.md` has the rule.
- **PLACEHOLDER:** the 4-unit nudge on the holsters and the gunbelt's new width (26 to 74).
- **Tests: 1318 pass. R1 checks: all passed.** **Bots:** 51 games, **0 problems**. The blob parity is unchanged (191 models, only the 5 red-eye notes).
- **Shots:** `handoff_images\2026-10-26_equipped\06_hip_items_back_side.png` (before and after, 11 belted models with a weapon in hand).
- **Files:** `class_tree/class_blobs.txt`, `class_tree/BLOB_STYLE.md`.

## Save 75: your 20:48 notes: the Ward, the class card, the rerolls
- **The class card isn't inverted any more.** In promotion mode the class tree is still flipped black-and-white, but the side card is now drawn after the flip, in its own colours (the dark C2 card, the yellow PROMOTE!) and at full sharpness. The old flip went through an off-screen image, which also blurred it.
- **The rerolls are gone** (your ask: a design overhaul later). The level-up pop-up and the promotion's Reroll button on the crew card are both removed. **Instead, at each promotion every attunement the blob carries** (kept or newly gained; a duo counts once) **has a chance to rise one rarity**, never past the new tier's cap. A notice says "Aldric's Fire rose to Rare!".
  - **The chance (Luck sets it):** 10% + 2% per Luck point (`Fortress.UPGRADE_BASE`, `UPGRADE_PER_LUCK`). **PLACEHOLDER.**
  - Essence still comes from deaths and from the rarity rolls when a blob gains a new attunement. There are no reroll payouts any more.
- **The Ward room has its room card back.** While a class was waiting, the room showed an old "UPGRADE!" tab that drew off-screen (the empty panel). It's removed.
- **The promotion button: the real bug, fixed for every card.** The "Save posts / Return / Open all / Close all" buttons and the mana bars sit underneath the bottom of the side card, and a click checked them before the card's own buttons. So a click on the lower part of a card's bottom button went to them instead. A left-click on the card now goes to the card first (the dev console, drawn on top, still wins). Tested: with the game paused, a real click on the blob's CLASS PROMOTIONS AVAILABLE! opens the class tree.
- **The Chrysalis Ward card:** no promotion button (your ask; it lives on the blob's own card); it always shows "View in class tree". **It now has a Restoration attunement card** like the other devices, its colours are Restoration's, and its Healing row counts the Restoration match (+10% x rarity), as the healing itself already did.
- **Tests: 1316 pass** (the reroll checks are replaced by upgrade-chance checks: the rate, the cap, one step at most). **R1 checks: all passed** (+5 Ward checks). **Bots:** 51 games, **0 problems**.
- **Shots:** `handoff_images\2026-10-26_ward\` (4, with an `_INDEX.md`).
- **Files:** `logic/Fortress`, `logic/CrewMember`, `logic/Rarity`, `logic/Item`, `swing/FortressView`, `swing/InfoPanel`, `swing/ClassTreeView`; `LogicTests`, `QolShots`, `Shots`, `BotPlaytest`.
- **Your answers:** `roadmaps/archive/2026-10-26_answers_rerolls_hands.md` (also the hand-point plan, next).

## Save 76: your 21:00 note: the lineage strip shows every next class
- **No more "+3":** the class card's lineage strip shows the icon of every next class (up to 5).
- **Everything stays on one row**, so the card keeps its height and the PROMOTE! button stays put. When the parent, this class and all the next classes don't fit, the **parent steps out** (the crumb at the top of the card already names it) and the next classes' columns **narrow to fit**. A long name shrinks until its longest word fits its column (9.5 down to 7 pt).
- **Tests: 1316 pass. R1 checks: all passed.** **Bots:** 51 games, **0 problems**.
- **Shots:** `handoff_images\2026-10-26_ward\ward_04` (updated) and `ward_05_lineage_all_next_classes.png` (classes with 5, 5, 4, 3 and 2 next classes).
- **Files:** `swing/ClassTreePanel`.

## The Hand Placer page (your 20:50 note; not a game change)
- **A page to place every model's hand:** the **Hand Placer** artifact (https://claude.ai/artifact/9RDQ66KfMcRUWZyugKYVtP, roadmap **P16**). It shows all 191 models holding a weapon, drawn by the game's own renderer, with the weapon drawn exactly as `EquipArt` does.
  - **Starting points:** a model's own `hand` line (34), its parent's (24), the **auto-detected front fist** (85 more; 137 found in all: the most forward arm shape that is fist-sized, 80% of the way down it), the path default (3), or floating (45 robes and armless models).
  - **Controls:** click or drag to place, scroll to tilt (Shift: 1°), arrow keys to nudge, Float, Reset, ✓. A side panel tunes each of the 7 weapon grips (grip point, size, turn, flip), live on every tile.
  - **It saves to the page's own store** as you go; Claude reads it back. A text export is the backup.
- **Next, when you say "import the hands":** a `hand` line for every model (a new `hand none` = floats), your grips into `EquipArt.GRIP`, then blobcheck, the tests and the shots.
- **Files (tools, no game change):** `test/fateweavers/swing/HandExport.java` (makes the page's data) and `tools/hand_placer_template.html`.

## Save 77: your 21:14 note: "?" for classes not unlocked yet, names on hover
- **A class still in the fog shows "?"** on a dashed ring in the lineage strip; hovering it says **"Unknown"**, and it can't be clicked. "Unlocked" uses the class tree's own fog rule: a class is known once it's on a living blob's route or among the classes a blob has unlocked next (on the class tree with no game, all are known).
- **No names under the rings any more:** hovering any ring pops up its name in a small white pill above it. The strip is one row of rings (52 px tall instead of 66), and the next classes' columns are 40 px (they narrow if needed; the parent steps out when all can't fit).
- **Tests: 1316 pass. R1 checks: all passed** (+4 lineage checks). **Bots:** 51 games, **0 problems**.
- **Shots:** `handoff_images\2026-10-26_ward\ward_04` (updated), `ward_05` (redrawn), `ward_06_lineage_hover.png`.
- **Files:** `swing/ClassTreePanel`, `swing/ClassTreeView`, `QolShots`.
- **Your answers (also the stats panel, next):** `roadmaps/archive/2026-10-26_answers_stats_panel.md`.

## Save 78: the stats panel (brief §3b; your answers 21:20)
- **The crew card's ATTRIBUTES are grouped value bars** (canvas `StatsC1`):
  - **A "Spend N points" pill** sits beside ATTRIBUTES when there are free points; pressing it shows a **+** on every attribute below the cap and the pill turns into **Done** (💡 ✅). It ends by itself when the points run out. (An Unclassed blob with no free points still sees "Pick a star".)
  - **A key line:** ★ class favourite · ▼ weak for the class · ▲ learning fastest · ▁ next point · ● practising now.
  - **Four families** in colour (✅ as specced): **Body** #c4553a (STR VIT DEX RES, feeds Stamina), **Magic** #4b5bd6 (ARC FAI PSY, feeds Mana), **Mind** #2f8f83 (PER KNO), **Presence** #b58a1e (CHA LUCK). `swing/StatFamily`.
  - **Each row:** the practising dot (✅ kept: it pulses while that attribute is being trained right now), icon, name and marks, then **a bar of the value out of the tier cap** in the family colour on its pale track with a black cap tick, **a thin green XP line** under it (hidden at the cap; Luck has none), the number and "/ 40", or a black **MAX** badge at the cap. **Stats at 1 fade to 45%.** Hovering a row still shows its exact learning % (now a small pill over the bar).
- **The stats book opens with the profile shape chart** (✅ at the top; canvas `StatsC2`, `swing/StatShape`): one spoke per attribute in family order, a tinted wedge per family with small gaps, faint rings every 10 (every 20 past a cap of 40), a dashed ring at the tier cap labelled "cap 40", the shape in translucent black with a solid outline and a dot per point in its family colour, the icons at the spoke ends, a legend, and **the two-column number list** (💡 ✅ kept). The book's tabbed rows follow below.
- **PLACEHOLDER:** the 6° gap between wedges; the 45% fade.
- **Tests: 1316 pass. R1 checks: all passed** (+7 stats checks; the star-pick test now follows the family order). **Bots:** 51 games, **0 problems**.
- **Shots:** `handoff_images\2026-10-26_stats\` (3, with an `_INDEX.md`).
- **Files:** new `swing/StatFamily`, `swing/StatShape`; `swing/InfoPanel`, `swing/BookPage`; `QolShots`.

## Save 79: your 21:47 note: two capped attributes = the level cap
- **The level now takes half of every attribute XP earned** (`Progression.LEVEL_SHARE` 0.5; ✅ your pick "Level XP = half the attribute XP"). Before, it got a flat 1 XP a second of work however many attributes were being trained, so Aldric could cap Arcana and Psyche at Lv 3 / 5.
  - **The level curve now uses the attribute curve's numbers** (22, 24, 26 ...; was 26, 28, 30 ...), so a tier's levels cost exactly one attribute's climb from cap to cap. Two attributes practised to the cap = the level cap, at every tier (tested).
  - **The crew now start at level 1** (✅ your pick; like the bandits), so Tier 0 has 4 levels for its 4 attribute points (1 -> 5). They skip the old 0 -> 1 level-up and its points.
  - **Every source of attribute XP counts:** work, the Faith shrine, haggling, holding doors, being hurt, talking a deserter down. **Resting in the Ward still gives no level XP** (as before: resting isn't work; a call, keeps the Ward from being an idle levelling room). XP earned on an attribute already at the cap still counts, so practice never stops a blob levelling.
- **Pace (PLACEHOLDER; F1 aimed for level 5 in 2–3 min):** practising two attributes reaches Lv 5 in **1.8 min** (was 2.5), one attribute alone in **3.6 min**, three in 1.2 min.
- **Note:** the class bonus (+2 star, +1 second a level) and free points raise attributes without XP, so a blob can show two capped attributes a little before its level catches up; its practice keeps filling the level.
- **Tests: 1320 pass** (+4: the per-tier alignment, two capped = Lv 5, a capped attribute still counts, Ward rest doesn't; the banked-points test now counts four Tier 0 levels). **R1 checks: all passed.** **Bots:** 51 games, **0 problems**.
- **Files:** `logic/Progression`, `logic/Fortress`, `logic/CrewMember`, `LogicTests`.

## Save 80: your Hand Placer hands, the new hammer, fists-only, no parrot (your 22:13–22:15 notes)
- **Your hands are in the game:** all 191 models now carry their own `hand` line from your Hand Placer export (the page's store matched it exactly), and **`hand none`** (new) makes the weapon float (the robes). Your **weapon grips** are in `EquipArt.GRIP` (sword 20/78, dagger 23/73 turned 66° and flipped, bow 47/49, staff 29/73, axe 29/70, hammer below).
- **Per-weapon hands for edge cases (new):** a model line `hand bow x y [angle]` (or sword, dagger, staff, hammer, axe, mana-blade; `hand bow none` floats just the bow) wins for that weapon. **The Hand Placer has a "Bow only" button on every tile** (it names the weapon on show): press it, then place that weapon's own spot. Use it for the Kickboxer's bow, then say "import the hands" again.
- **The hammer (✅ B, "small war hammer", everywhere):** a compact square head with a dark band on a thinner handle. It replaces the old approved icon in slots, the shop and the hand. Its grip moved onto the new handle (34, 71; your size 50 and turn −7 kept), in the game and on the page.
- **Throgg and the Pugilist fight with their fists:** equipping a weapon is refused ("fists only, no weapons"), and a blob promoted into them puts its weapon in its hands (or on the rack if its hands are full). Classes that come from them later inherit it (`Fortress.FIST_CLASSES`). No weapon is drawn on them.
- **The Swashbuckler has no parrot** (4 lines removed; the model's comment says so).
- **`class_blobs.txt`:** 191 hand lines (each marked), the parrot gone, the format notes for `hand none` and `hand <weapon>`. Backup: `class_tree/blob_options/class_blobs_backup_2026-10-26_before_hand_import.txt`. `BLOB_STYLE.md` has the rules. The blob parity is unchanged (191 models, only the 5 red-eye notes).
- **Tests: 1320 pass. R1 checks: all passed** (+9: every model has its own hand, `hand none` floats, a weapon's own hand wins, other weapons keep the model's, `hand dagger none`, no parrot, the Pugilist puts its weapon down and can't equip, Throgg's line is fists-only). **Bots:** 51 games, **0 problems**.
- **Shots:** `handoff_images\2026-10-26_hands\` (the hammer options and every model with a sword, a bow and a hammer).
- **Files:** `swing/ItemSvgDefs`, `swing/EquipArt`, `swing/CrewRenderer`, `logic/ClassBlobs`, `logic/CrewMember`, `logic/Fortress`; `class_tree/class_blobs.txt`, `class_tree/BLOB_STYLE.md`; `QolShots`, `HandExport`; `tools/hand_placer_template.html`.

## Tidy-up (26 Oct night, between saves 80 and 81; your 22:28 ask: "bring everything together … make sure everything is up to date")
- **No code changed.** The project's docs were brought up to date with the build: `00_START_HERE.md`, `01_Project_Instructions.md` (Desktop and project), `roadmaps/roadmap.md` (Pressing P17 and P18 added), the segment docs **R1, R2, R3, R5, R6** and `fable_high_fidelity_tasks.md` (their status lines now say what's built after save 80), `design/systems/stat_system.md` (v8.2 and its stale lines), the design-thread backlog and build brief (what's built), the two 25 Oct blob-model notes moved to `design/archive/`.
- **The project's code copy:** the files other threads use were synced (`Fateweavers/class_tree/class_blobs.txt`, `BLOB_STYLE.md`, `tools/BlobPreview.java`, `BlobArt.java`, `ClassBlobs.java`, `BlobShots.java`, `README.md`, the `.bat` files). The rest of `Fateweavers/src` and `test` is still the save-22 copy: the whole build (2.1 MB) is over the project's 2 MB limit. Whether to delete those stale copies is **P17** (it deletes things, so it's yours).
- **Found while checking:** the README still said "level n costs 24 + 2n" (fixed in save 81's README). The 13 dead `# TO DELETE (n)` files are still in `src\fateweavers\logic`, `src\fateweavers\swing` and `test\` (P17 item 3).

## Save 81: the bottom bar, concept 1 (design thread C9; `design/cards/bottom_bar.md`, mock-up HudC1)
- **The reactors count free power.** Bottom left, the **mana reactor**: a column of pips where the **free** ones are mana blue (#4b5bd6) and the used ones pale grey, a drop icon, **MANA** in blue and "**0** free of 4". The **stamina reactor** is the same in amber (#d08a1e) with a bolt. "MANA 0/4" (which read as "no mana") is gone.
- **Every system is a column:** its pips stacked over a **40 px icon tile**, the name and a status line ("2/2 on", "none wielded"). The tile is filled in the pool's colour with a white icon while anything in it is powered, pale with a grey outline while off. **Click the tile** (or its pips) to power the next member, **right-click** to switch the last one off; hovering lists the members. Columns: Permeator, Loom, Pedestals, **Weapons (always shown now; it fades back when no staff is wielded)**, Devices (only once a stolen Permeator is set down), then Mounts on the stamina side.
- **System integrity on the tile** (✅ your 22:26 answer): a damaged system's tile wears a small tag with its points left as a fraction, **"2/3"** or **"1/3"** in amber, **"0/3"** in red when broken. The map's tag under the device still says "67%".
- **The right side, grouped and labelled:** **CREW POSTS** (Save posts, Return) and **DOORS** (Open all, Close all), each button with a small icon; a dimmed Return stays dimmed. Then a round **"?"** at the far right: hovering it shows the control hints (one per line, in a card), a click keeps them open, Esc closes them. **The long hint line along the bottom is gone**; notices now show in the bar's free middle (wrapped to two lines).
- **Drawn over the map:** the bar used to be drawn under the map, so a panned map could hide it. It's drawn on top now, with a soft backing in the screen's colour (invisible until the map is under it). The default view leaves the bar a little more room (the map fits in 206 px less than the screen height, was 190: about 3% smaller). A side card covers the part of the bar under it, as before, and nothing peeks out below the card.
- **PLACEHOLDER (new, listed for the handoff):**
  - **every icon on the bar**: the mock-up's sketches in the new `swing/HudSvgDefs` (Permeator gem, Loom weave board loom-b, pedestal, sword for Weapons, mount, drop, bolt, flags, doors). The picks are ❓ (roadmap P13: a small round in the design thread). Devices borrows the Permeator's icon.
  - the sizes from the mock-up: 74 px columns, 30 × 9 pips, 40 px tiles, 84 px reactors with 34 × 10 pips, the 26 px gap between the two sides; the fit margin 206.
- **My calls (💡, say if you want them changed):**
  - Save posts is the dark button and the others light, as the mock-up draws them.
  - A shared column's tile shows its **most damaged** member's tag (the hover list gives each one's).
  - A broken system reads **"0/3"** (red) on the tile, not "BROKEN", to keep the fraction.
  - The hints card: hover to peek, click to pin, Esc to close. I added two lines the old hint line didn't have ("Right-click … attack an enemy" and "Bar tiles: click … right-click …"); everything else is the old line, one control per row.
  - Notices moved into the bar's middle (the old spot is where the stamina reactor is now).
  - An empty column (Weapons, no staff) keeps one outline pip, as in the mock-up.
- **Not built (💡 in the spec):** showing the hints once on the first run; reusing Arcana's drop for mana.
- **Tests: 1320 pass. R1 checks: all passed** (+14: the column order; a real click on the Loom's tile powers it and a right-click switches it off; 2/3 and 0/3; the Mounts tile carries the ballista's tag; Weapons faded with "none wielded"; the buttons in one row in order, clear of the systems; Open all and Close all still work; the "?" on hover, off when you move away, pinned by a click, closed by Esc). **Bots:** 51 games, **0 problems**.
- **Shots:** `handoff_images\2026-10-26_bottom_bar\` (7, with an `_INDEX.md`).
- **Files:** `swing/FortressView`, `swing/HudSvgDefs` (new), `swing/SvgIcons`, `swing/Theme`; `QolShots`; `README.md`.

## Save 82 (26 Oct 23:12; your answers to P18, ticked on the report)
- **Read from your marks:** Save posts dark ✓, most damaged member's tag ✓, **"BROKEN" instead of "0/3" ✗**, the "?" ✓, notices in the middle ✓; the map's "67%" as a fraction ✓; backpack: **"leave it out for now"**; the spill onto the floor ✓; Run 0: **"yes ✓"** (read as: Scene 2 and the Acts 1–2 boards are approved; how it starts is still unanswered, so nothing was built); the theme-chain line was ticked with no answers (read as still open).
- **BROKEN:** a broken system's bar tile reads **BROKEN** in red (a damaged one still reads "2/3", "1/3" in amber). The member list on hover says BROKEN too.
- **Fractions on the map:** the amber tag under a damaged device and the room card's "DAMAGED:" line read **"2/3"** / **"1/3"** (broken still reads BROKEN / "broken").
- **Loot spills onto the Storeroom floor:** essence that doesn't fit on the full rack (and an event reward with no room, which used to be lost) lies on the floor, up to **8 things** (`Fortress.FLOOR_MAX`, PLACEHOLDER), essence stacks piling; past 8 it's lost, as before. The Storeroom's card shows an **"On the floor n / 8"** row under the rack. Drag a floor item **onto a free rack slot** (a blob has to be in the Storeroom to lift it) or **into a blob's hands**; click it for the obvious move (the rack, else free hands). Nothing is drawn on the map for it yet (a call: say if you want a pile showing in the room).
- **Tutorial doc:** Scene 2 and the Acts 1–2 boards are marked ✅ approved (as mocked up). Not built.
- **PLACEHOLDER (new):** the floor's size, 8.
- **Tests: 1328 pass. R1 checks: all passed** (+8 floor-loot checks: essence spills, piles, the floor's limit, a full rack refuses, onto the rack, into hands, not into full hands; the bar check now says BROKEN). **Bots:** 51 games, **0 problems**. `blobcheck.bat` unchanged (no model touched).
- **Shots:** `handoff_images\2026-10-26_floor_loot\` (the Storeroom card with three things on the floor; BROKEN and 2/3 on the bar).
- **Files:** `logic/Fortress`, `swing/FortressView`, `swing/InfoPanel`; `LogicTests`, `QolShots`.

## Save 83 (27 Oct): Run 0, the tutorial, Scene 1 to the end of Act 2 (behind a dev button)
- **What it follows:** `design/systems/tutorial_run0_DRAFT.md` and the "Fateweavers Tutorial" canvas, **version 10** (your 00:24 note: "the start of the tutorial has been finalised"): Scene 1, Scene 2, Act 1, and Act 2 both ways (the fight and the peaceful way). The lines are the board's, word for word. **Act 3 and the Sanctuary arrival are on the board but not approved, so not built.** The tutorial ends where Act 3 starts.
- **How to try it:** open the dev console (` or F1) and click **"Start Run 0 (the tutorial)"**. It starts a new run with the tutorial. How a player starts Run 0 is still ❓ (roadmap P18).
- **What you see:**
  - **Scene 1:** black for 3 s, then a mote of white light floats up. Click it and it grows into the Chrysalis Ward, alone in the void, and "Welcome." types out.
  - **Scene 2:** a mote sinks into the crystal, the Ward glows green, and Aldric steps out. "Someone stirs." / "Touch them." Click him: his name tag shows and his card slides in on the left.
  - **Act 1:** the Permeator, the Loom, three more blobs born, the Glass Orb, the shield's glow, Save posts, the "?" and the pause, the Mount Room and stamina, then the Entrance Hall's doors. The bottom bar grows one part at a time.
  - **Act 2:** No Man's Land and the gate, then the Practice Camp and its choice.
    - **The fight:** Daria provokes the camp and the guard comes out, then the gate card. The camp fires two scripted shots: the orb blocks the first and the second takes a bar off the Entrance Hall. Cedric mends it. The Fire Orb is swapped in, then the parry drill. Cedric powers the Fire Gem and it hits their Battlement. The gate is yours, the assault clears the camp, then Return.
    - **The peaceful way:** a few lines, and the camp stays neutral.
  - **The end:** a grey line, "That is as far as Run 0 goes for now: Act 3 (the strings of fate) waits for its OK.". Then the void fades and the rest of the fortress is woven in. You keep playing a normal run (still Run 0: you can't lose).
- **The tutorial-only rules (the doc's "deliberate" list), as built:**
  - **Rails:**
    - Only the glowing thing takes a click; a stray click gets the board's nudges ("Close. The glowing one." …).
    - A left-click stands in for a right-click.
    - The key cap stands in for its key, and in the drill any click counts as S. The real keys work too.
  - **The world and the crew:**
    - Everything starts switched off, and the world holds still until the Permeator is powered.
    - The crew are born from the crystal. Daria carries no staff (it waits on the rack).
  - **The camp:**
    - The Practice Camp is the Bandit Camp without its shield room and ballista, with a guard and a caster.
    - Its gun is scripted: two shots, then the drill. Drill shots fly at ×0.4 and the shield goes up by itself after two misses.
    - The camp can't be provoked before it's shown.
  - **Scripted help:**
    - The orb swap happens by itself.
    - The assault is a montage: one click sends Daria and Cedric in, and the tutorial gives the attack orders.
  - **Safety:** no room drops below one bar, and a blob at 0 health stays at 1. Sleeping waits until the end.
  - **The screen:** the header is light and leaves out Class tree, Tapestry and Dev (the dev console stays on ` / F1). The Weapons column is left out.
- **My calls (💡, say if you want them changed):**
  - **The end:** a grey closing line, then the void fades and the run goes on as normal.
  - **The first mote over the Ward's floor:** it gets a faint dark rim, because white on the light floor was invisible.
  - **"Touch them":** the ring round Aldric is dark for the same reason (the board's is white).
  - **The shield's glow:** brighter on the void, with a rim. On black the game's faint wash read as nothing, and the line points at it.
  - **The layout:** the fortress's real layout differs from the board's.
    - The rack's Utility Room sits above the unwoven Workshop, so it appears with a gap.
    - Blobs walking through an unwoven room cross the void.
  - **Real game feedback stays:** the blobs level up as they work ("Level 2!" pops), and notices sit in the bar's middle (light on the void).
  - **Pressing Space during a line moves the line on.** The pause task takes Space once the text has finished.
- **PLACEHOLDER (new):** the tutorial camera's layout: the free space's top (150 in the scenes, 222 in the acts, 268 once No Man's Land shows), its bottom (132 over the bar, 70 before it), a 26 px margin; the mote's 30 px click radius. The timings (3 s dark, 2.4 s rise, 0.9 s sink, 90 and 42 ms a letter, ×5 after punctuation, ×0.4 drill) are the board's.
- **Tests: 1357 pass. R1 checks: all passed.**
  - **LogicTests (+29):** Run 0's setup; Scene 1's dark, mote and bloom; Scene 2's birth; the rails' nudge; the frozen world waking; the locked camp and sleep; Act 1 room by room; the two shots; the drill's help after two misses; the assault clearing the camp; the end; you can't lose; no room below one bar; the peaceful way.
  - **QolShots:** the clicks go through the view's own handlers: the mote, a click away from it, a key moving a line on, a stray click, the glowing tile, the key cap and the real Space key, the camp's choice, the drill, the end.
  - **Bots:** 51 games, **0 problems**.
- **Shots:** `handoff_images\2026-10-27_run0\` (13, with an `_INDEX.md`).
- **Files:** `logic/Tutorial` (new), `logic/Fortress` (Run 0: `runZero`, the frozen world, the locks, the scripted shots, the orb swap, the floors), `logic/EnemyShot` (speed, outcome), `swing/FortressView` (the void, the camera, the overlay, the rails, the dev button), `swing/Theme` (the typewriter face), `Fateweavers` (starting Run 0); `LogicTests`, `QolShots`.
