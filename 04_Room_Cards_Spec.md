# Handoff Spec: Room cards, design C "Floor plan" (25 Oct 2026, Utility Rooms added 26 Oct)

Built in `InfoPanel.drawRoom` (Swing). Design reference: the canvas "Room Cards: Redesign Drafts", row C and the "Health orbs and Aether states" board. Station cards (Pedestal, Mount, Loom, Workbench, Storeroom rack, Permeator, Ward, Campfire, enemy devices) are **not** part of this redesign.

## Overview
Clicking a room on the map opens its room card in the left panel. The card answers three questions at a glance: what the room looks like right now (the floor plan), is it healthy and breathable (the chips), and who works what (device rows and station cards). Descriptions were removed (user edit); the plan carries the information instead.

## Layout (top to bottom, panel content width 272 px, 24 px margin)
| Part | Size | Content |
|---|---|---|
| Header | name 24 px bold + 14 px sub-line | Name in ink (ours) or enemy red (camp). Sub-line: kind, plus "2 weapon slots" (Battlement) or "No station" (fortress rooms without one). **Utility Rooms (26 Oct, Q39 A):** every room with an Anchorstone is named "Utility Room" on the map and the card; its sub-line names the device: Permeator / Workbench / Storage / Campfire |
| Floor plan | 272 wide, height = room aspect, clamped 150–340 | The map's own drawing of the room (`FortressView.paintRoomPlan`): floor, Aether wash, damage shade, doors, devices, station rings, people. Clipped to the room + 4 world px, so neighbours never show |
| Plan labels | 20 px pills, 11 px bold | Door pills "Workshop ↑" just inside the wall the door is on (left-wall labels above the door, right-wall below); top-left: Aether pill (ours) or "Fire Gem aimed here" (enemy room your AUTO targets); under each station operator: name (ours) or "Gunner · Reloading" (theirs) with a pulsing dot |
| Chips | 56 px tall, 8 px gap; 3 in a row, or 2×2 when there are 4 | HEALTH / REPAIRING (orbs), AETHER (state word, glowing border) on your rooms, ENEMIES tally on camp and visit rooms, CREW tally on non-camp rooms, AMMO tally where there are bolts (Mount Room, Battlement) |
| Node sections | as before | Shop wares, the Sage's choices, the Goblin Boss, the camp's state (Quarters) |
| Device rows | 48 px, 8 px gap | Small device picture, "Pedestal · Glass Orb", "Mount · Ballista", "Anchorstone · Permeator / Workbench / Storage rack / Campfire" (caption dropped if the name would be cut), state on the right, chevron; opens that device's station card (checked by the harness for all 11 rooms with devices) |
| Station cards | 60 px icon card per station | "ON THE STATION" + name + "Working" tag with a pulsing dot (ours); "GUNNER · BALLISTA" / "CASTER · GLASS GEM" + "Level 1 Blob" + Reloading/Aiming or Charging/Casting (theirs). Empty: dashed card "Nobody · Empty: it powers off, its charge is lost" |
| Also here | occupant cards | People in the room who aren't on a station |
| Power | Permeator room only | The Permeator's small power toggle (kept) |

## Design tokens
| Token | Value | Usage |
|---|---|---|
| `health-orb` | #1BD6D2 (light #E6FFFE, dark #0F9F9C) | Health orbs and their glow |
| `health-socket` | #9FB8B7 | Dashed ring of a lost orb |
| `aether-infused` | #1BD6D2 glow, #0E8F8C text | Full air: "Infused" / "Air infused with Aether" |
| `aether-refilling` | #10E157 glow, #0B8F3A text | Below full, rising |
| `aether-leaking` | #B3261E | Falling (rate below −0.002 / s) |
| `enemy-name` | #B3261E | Camp room names, enemy devices, enemy tallies |
| `card` | Theme.CARD (245) | Chip and row background |
| `caption` | 11 px tracked caps, #6B6B6B | Chip captions, station captions |

Text colours for Aether are darker shades of the chosen glows so they stay readable on the grey chip (the pure #10E157 / #1BD6D2 are under 2:1 on grey).

## States and interactions
| Element | State | Behaviour |
|---|---|---|
| Health orb | Intact | Glowing orb |
| Health orb | Just lost | Swells 35 % and fades over 0.6 s, leaving a dashed socket |
| Health orb | Repairing | Caption reads REPAIRING; the next orb grows in its socket with the room's real repair progress (15 % → 100 %) and 8 sparkle rays pulse round it |
| Aether chip | Infused / Refilling / Leaking | Word + coloured border with a soft 3-ring glow |
| Device row | Hover | Background lightens; click opens the device's station card |
| Station card | Worked | Tag dot breathes (4.5 rad/s) |
| Station card | Empty | Dashed outline; the device has powered off and lost its charge (rule below) |
| Plan | Live | Redrawn every frame from the map: units walk, rings turn, damage darkens |

## Rules decided alongside (26 Oct, R1)
- **Placement rule:** one device slot per 2×2 room (its centre vertex), two per 2×3 (the inner vertices), the Chrysalis Ward's in the middle of its 3×3 (`Room.deviceSlots`). Pedestals, mounts and Anchorstones all sit on these.
- **Station spot (E7):** the operator stands on a spot beside the device, left of it on our side and right of it on the camp's, facing it (`Room.stationSpot`, STATION_GAP 30 / STATION_DROP 4 PLACEHOLDER). The floor plan shows it.
- **Anchorstone (Q38 B):** the slot a utility device sits on (Permeator, Workbench, storage rack, campfire; Furnace and the training stand's cousin later). Rooms with one are Utility Rooms (E11).
- **Hitboxes (E3, E4, E5):** the plan's devices and blobs are the map's; on the map, hover and click hug the drawn body / the device and its base.

## Rules decided alongside (25 Oct)
- A station left empty powers its device off and loses all built charge (weapons, mount, shield bars, Loom weave); the enemies' stations follow the same rule (their gun timers and shield bars reset).
- The Battlement (was Archer Tower): 2 wide × 3 tall, a mount (ballista) and a pedestal (Glass Gem) on the grid's two inner vertices, the same dais under each, each with its own operator (Gunner, Caster). Four bandits per camp.
- The Glass Gem: Neutral, magic, 2.5 damage / 5.5 s (Fire Gem 3 / 5 s), Dexterity load speed +1 % per point (Fire Gem +0.5 %). Sold in the Goblin Shop for 25 shards. PLACEHOLDER numbers.
- The East Room is the Mount Room.

## Edge cases
- No Man's Land keeps its own card; the Chrysalis Ward still swaps to the UPGRADE tab when someone can change class.
- Visit rooms (shop, hut) and the arena show no HEALTH or AETHER chip.
- A room with no station shows no station card; its people appear under "In the room".
- Long room or door names: door pills keep their text, chip captions are cut with "…", device names drop the caption first.

## Accessibility notes
- Every state also has a word (REPAIRING, Infused/Refilling/Leaking, Reloading…), so colour is never the only signal.
- Health orbs count is readable from shape: full orb vs dashed socket.
