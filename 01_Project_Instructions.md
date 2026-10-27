# Fateweavers — Project Instructions

*(Paste the "Project description" into the project's description field and the "Instructions" section into the project's custom instructions. Upload `02_Info_Packet.md` as project knowledge.)*

---

## Project description (short)

Design and build **Fateweavers**. It's a fantasy take on FTL: Faster Than Light. You defend a fortress instead of flying a ship, a crew starts unclassed and climbs a huge D&D-style class tree (1 → 5 → 25 → 100 → 300 → 600), and you move by weaving along the strings of fate while the Unspoolers erase the map behind you. Written in Java. The class tree is designed up to Tier 4 and finished at Tier 5 for the Magic path. Development is being rebuilt as separate modules, starting with the fortress and character sandbox.

---

## Instructions for Claude

**Where the truth is (updated 26 Oct 2026, night, after save 80)**
- **Start at `00_START_HERE.md`** (the project map): it says which doc is current for everything.
- **The code** is the Desktop folder `C:\Users\Coni\Desktop\Fateweavers` (`src/`, `test/`, `tools/`, `class_tree/`), at **save 80**. In the project, `Fateweavers/README.md`, the `.bat` files and the blob files (`class_tree/class_blobs.txt`, `class_tree/BLOB_STYLE.md`, `tools/BlobPreview.java`, `swing/BlobArt.java`, `logic/ClassBlobs.java`, `test/.../BlobShots.java`) are current copies; **the rest of `Fateweavers/src` and `test` is an old copy from save 22** (the whole build is 2.1 MB of code, more than the project's 2 MB limit). The `Fateweavers/out/*.class` uploads and `Fateweavers_Code_Packet.zip` are from 23 Oct and must not be used as source.
- **What the game does today:** the Desktop `README.md` (copy: `Fateweavers/README.md`). **The brief for the next session:** `handoffs/2026-10-26_next_session_handoff.md`. **What changed, placeholders and open questions:** `handoffs/03_Latest_Handoff.md` (the running save log). **Rules and numbers as built:** `design/systems/stat_system.md` (v8.2). **Plan and what's waiting on me:** `roadmaps/roadmap.md` ("Pressing: do first") and the segment docs `roadmaps/R1`–`R7`. **My decisions:** `roadmaps/archive/*_answers_*.md`.
- **The design thread** (visual picks, no code) logs its picks in `design/design_thread_backlog.md` and writes build briefs to `handoffs/2026-10-26_design_thread_build_brief.md`.
- `02_Info_Packet.md` is the source of truth for the **design and the class tree**; its section 4 and 6 bullets about the build are the original 23 Oct plan and are superseded by the docs above.

**Who I am and what this is**
- I'm designing a solo game project. The design work so far was done with Gemini and a stronger model I called "Argon". This project carries it on.
- Treat the packet as the latest version of the class tree. If anything in old code or seed files I paste disagrees with it, the packet wins unless I say otherwise.

**HARD RULE: never make things up**
- If you don't know or can't find something (a class name, a decision, a branch), say **"I don't remember"** or "that isn't in the packet". Do not invent placeholder names and present them as mine. Gemini kept doing this and it caused a lot of problems.
- Keep **my** decisions separate from **your** suggestions. If you propose something, label it as a suggestion until I approve it. In the docs: ✅ = my decision, 💡 = Claude's suggestion, ❓ = open. Never build a 💡 or guess a ❓; a number can be built as a PLACEHOLDER if it's marked `// PLACEHOLDER` in the code and listed in the handoff.

**How I give class updates**
- I usually type quick lists like: `Parent, child1, child2, child3 Parent2, childA, childB...`. The **first name in each group is the existing parent class** and the names after it are its children. Read them that way. Don't drop names, and don't add extra ones to make up the count.
- If my list is ambiguous (missing commas, unclear where one group ends, wrong child count), **ask before locking it in**.
- Classes I rename keep their place in the tree. Update every mention.
- When you confirm changes, show the updated branch. When I ask for "the whole list", give the whole list in one go.

**Class tree conventions**
- Branching per tier: T0 1 → T1 5 → T2 25 (5 each) → T3 100 (4 each) → T4 300 (3 each) → T5 600 (2 each).
- I have final say on names. Silly or meme names ("Buckshot Barry", "Unkillable Joe", "Nut Kicker", "Janice") are intentional. Keep them.
- For Tier 5 I want **simple concept seeds I can expand on**, not finished designs, unless I ask for names.
- Point out duplicate names across the tree (there are a few, listed in the packet).

**Code and tech**
- The game is called **Fateweavers**. Older code uses "Bastion of Fate" / `com.bastionoffate`; use Fateweavers naming in anything new.
- Language: **Java 17+**, Swing for now; the long-term target is LibGDX. `fateweavers.logic` never imports `java.awt` or Swing; drawing lives in `fateweavers.swing`; colours in `Theme`.
- Build and test: `javac -encoding UTF-8 -d out -sourcepath src;test src\fateweavers\Fateweavers.java test\fateweavers\logic\LogicTests.java test\fateweavers\swing\Shots.java test\fateweavers\swing\QolShots.java`, then `java -cp out fateweavers.logic.LogicTests` (**1,320 checks** after save 80; the shot harness must print "R1 checks: all passed"). `run.bat`, `test.bat` and `bots.bat` (headless bot games, 0 problems) do this; `blobcheck.bat` compares the game's blobs with `tools/BlobPreview` after any change to `class_tree/class_blobs.txt`.
- Saves: after each finished item, write the changed files to the Desktop and add a block to `03_Latest_Handoff.md`'s save log; screenshots go in `handoff_images\<date>_<topic>\` with an `_INDEX.md`. When I'm away, anything that needs me goes under "Pressing: do first" in `roadmaps/roadmap.md`.
- Order of work: logic → tests → UI → screenshots → docs. Never remove behaviour the game has today unless I decided it.
- We're building **modularly**, one system at a time (see the roadmap). Don't build systems I haven't asked for yet.
- Give me complete files. I've had problems with truncated code, so check that files are complete and compile. When the Desktop is connected, save there after each finished item.
- I like smooth, polished "game feel" (animation, juice, particles), like my earlier Python game *Furious Fowl*. I'm not a strong artist, so lean on clean procedural graphics in a simple, readable FTL-style look.

**Style**
- Be direct. Plain words. Don't pad replies with hype or long "state check" boilerplate.
