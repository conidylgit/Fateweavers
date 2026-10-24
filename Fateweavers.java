import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * FATEWEAVERS - MODULE 1: FORTRESS & CHARACTER SANDBOX (minimal build)
 * Run directly via: java Fateweavers.java   (Java 17+)
 *
 * Light, black-and-white, outline-free layout. Only the characters keep outlines.
 * 3 core rooms (Weapons Tower, Fateweaver Loom, Chrysalis Ward) + 2 neutral rooms.
 * Clicking a person, room or door opens an info panel on the right.
 */
@SuppressWarnings("serial")
public class Fateweavers extends JPanel implements ActionListener {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 760;

    // --- LAYOUT ---
    private static final int ROOM_W = 240, ROOM_H = 180, GAP = 10;
    private static final int PANEL_W = 320, PANEL_MARGIN = 24;

    // --- PALETTE (greyscale only) ---
    private static final Color BG = new Color(238, 238, 238);
    private static final Color CORE_FLOOR = Color.WHITE;
    private static final Color NEUTRAL_FLOOR = new Color(226, 226, 226);
    private static final Color INK = new Color(17, 17, 17);
    private static final Color INK_SOFT = new Color(120, 120, 120);
    private static final Color INK_FAINT = new Color(175, 175, 175);
    private static final Color PAD = new Color(0, 0, 0, 14);
    private static final Color PAD_HOVER = new Color(0, 0, 0, 40);
    private static final Color BUTTON = new Color(236, 236, 236);
    private static final Color OUTLINE = new Color(17, 17, 17);
    private static final Color SKIN_LIGHT = Color.WHITE;
    private static final Color SKIN_SHADE = new Color(222, 222, 222);

    // --- FONTS (first one installed wins: Segoe UI on Windows) ---
    private static final String FONT_FAMILY = pickFont("Segoe UI", "Inter", "Helvetica Neue", "Arial");
    private static final Font F_TITLE = new Font(FONT_FAMILY, Font.BOLD, 15);
    private static final Font F_LABEL = new Font(FONT_FAMILY, Font.PLAIN, 12);
    private static final Font F_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 11);
    private static final Font F_BODY = new Font(FONT_FAMILY, Font.PLAIN, 13);
    private static final Font F_BODY_BOLD = new Font(FONT_FAMILY, Font.BOLD, 13);
    private static final Font F_HEADING = new Font(FONT_FAMILY, Font.BOLD, 22);

    private static String pickFont(String... names) {
        Set<String> installed = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String n : names) if (installed.contains(n)) return n;
        return Font.SANS_SERIF;
    }

    // --- THE UNCLASSED + 5 BASIC CLASSES (greyscale tones; gear tells them apart) ---
    public enum BasicClass {
        UNCLASSED("Unclassed", new Color(245, 245, 245), new Color(205, 205, 205), "Plain jumpsuit survivor with no specialisation yet. Earns XP doing anything."),
        MAGIC("Magic User", new Color(95, 95, 95), new Color(62, 62, 62), "Arcane tunic and a floating mana orb. Powers magical stations faster."),
        RANGED("Ranged User", new Color(160, 160, 160), new Color(122, 122, 122), "Ranger cloak and recurve bow. Mans defensive weapon stations."),
        LIGHT_MELEE("Light Melee", new Color(48, 48, 48), new Color(22, 22, 22), "Agile garb and twin daggers. Fastest walker in the fortress."),
        HEAVY_MELEE("Heavy Melee", new Color(190, 190, 190), new Color(150, 150, 150), "Steel plate and a heater shield. Built to hold doorways."),
        SUPPORTER("Supporter", new Color(218, 218, 218), new Color(178, 178, 178), "Apron, goggles and wrench. Operates rooms and repairs machinery.");

        final String title;
        final Color mainColor, shadeColor;
        final String description;

        BasicClass(String title, Color mainColor, Color shadeColor, String description) {
            this.title = title;
            this.mainColor = mainColor;
            this.shadeColor = shadeColor;
            this.description = description;
        }
    }

    // --- ROOM MODEL (4 pads per room; pad 0 is the station) ---
    private static class RoomNode {
        final String id, name, description;
        final boolean core;
        final int gx, gy;
        final Rectangle bounds;
        final Point[] slotCenters = new Point[4];
        final CrewUnit[] slotOccupants = new CrewUnit[4];

        RoomNode(String id, String name, boolean core, String description, int gx, int gy, int ox, int oy) {
            this.id = id;
            this.name = name;
            this.core = core;
            this.description = description;
            this.gx = gx;
            this.gy = gy;
            int rx = ox + gx * (ROOM_W + GAP);
            int ry = oy + gy * (ROOM_H + GAP);
            this.bounds = new Rectangle(rx, ry, ROOM_W, ROOM_H);

            // Top pads sit low enough that heads/hats clear the room label
            int padX = 64;
            slotCenters[0] = new Point(rx + padX, ry + 84);
            slotCenters[1] = new Point(rx + ROOM_W - padX, ry + 84);
            slotCenters[2] = new Point(rx + padX, ry + 140);
            slotCenters[3] = new Point(rx + ROOM_W - padX, ry + 140);
        }

        int getFirstFreeSlot() {
            for (int i = 0; i < 4; i++) if (slotOccupants[i] == null) return i;
            return -1;
        }

        CrewUnit stationOperator() {
            CrewUnit u = slotOccupants[0];
            return (u != null && !u.isMoving() && u.currentRoom == this) ? u : null;
        }
    }

    // --- DOOR MODEL ---
    public enum DoorMode {
        AUTO("Auto"), HELD_OPEN("Held open"), LOCKED("Locked");
        final String label;
        DoorMode(String label) { this.label = label; }
    }

    private static class Doorway {
        final RoomNode roomA, roomB;
        final boolean verticalWall; // true = connects left/right rooms
        final Point center;
        final Rectangle hitbox;
        DoorMode mode = DoorMode.AUTO;
        float openAmount = 0f;

        Doorway(RoomNode a, RoomNode b) {
            this.roomA = a;
            this.roomB = b;
            this.verticalWall = (a.gy == b.gy);
            int cx = (a.bounds.x + a.bounds.width / 2 + b.bounds.x + b.bounds.width / 2) / 2;
            int cy = (a.bounds.y + a.bounds.height / 2 + b.bounds.y + b.bounds.height / 2) / 2;
            this.center = new Point(cx, cy);
            this.hitbox = verticalWall
                    ? new Rectangle(cx - 12, cy - 30, 24, 60)
                    : new Rectangle(cx - 30, cy - 12, 60, 24);
        }

        RoomNode getNeighbor(RoomNode r) {
            if (r == roomA) return roomB;
            if (r == roomB) return roomA;
            return null;
        }
    }

    // A point to walk to. If 'door' is set, reaching this point means walking through that door.
    private record Step(Point p, Doorway door) {}

    // --- CREW MODEL ---
    private static class CrewUnit {
        final String name;
        BasicClass basicClass;
        RoomNode currentRoom;
        float x, y;
        boolean facingRight = true;
        boolean walking = false;
        boolean waitingAtDoor = false;
        float walkCycle = 0f;
        float idleBreath;
        float squashStretch = 1.0f;
        final Deque<Step> steps = new ArrayDeque<>();
        Doorway crossing = null; // door currently being walked through
        RoomNode destinationRoom;
        int destinationSlot;

        CrewUnit(String name, BasicClass cls, RoomNode startRoom, int slotIdx) {
            this.name = name;
            this.basicClass = cls;
            this.currentRoom = startRoom;
            this.destinationRoom = startRoom;
            this.destinationSlot = slotIdx;
            startRoom.slotOccupants[slotIdx] = this;
            Point pt = startRoom.slotCenters[slotIdx];
            this.x = pt.x;
            this.y = pt.y;
            this.idleBreath = (float) (Math.random() * Math.PI * 2);
        }

        boolean isMoving() { return !steps.isEmpty(); }

        Rectangle getSpriteHitbox() {
            return new Rectangle((int) x - 20, (int) y - 46, 40, 60);
        }
    }

    // --- SANDBOX STATE ---
    private final Map<String, RoomNode> rooms = new LinkedHashMap<>();
    private final List<Doorway> doorways = new ArrayList<>();
    private final List<CrewUnit> crewList = new ArrayList<>();

    private CrewUnit selectedCrew = null;  // who right-click moves
    private Object inspected = null;       // what the info panel shows (CrewUnit, RoomNode or Doorway)
    private float panelSlide = 0f;         // 0 = hidden, 1 = fully shown

    private CrewUnit hoveredCrew = null;
    private RoomNode hoveredRoom = null;
    private Doorway hoveredDoor = null;
    private int hoveredSlotIndex = -1;

    // Panel buttons are rebuilt every frame while drawing
    private final Map<Rectangle, Runnable> panelButtons = new LinkedHashMap<>();

    private String notice = null;
    private float noticeTimer = 0f;
    private long lastFrameNanos = System.nanoTime();

    public Fateweavers() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(BG);
        setFocusable(true);

        buildFortress();
        spawnStartingCrew();
        setupControls();

        new javax.swing.Timer(16, this).start();
    }

    // =========================================================================
    // 1. LAYOUT & STARTING CREW
    // =========================================================================
    private void buildFortress() {
        int areaW = WIDTH - PANEL_W - PANEL_MARGIN * 2;
        int ox = (areaW - (3 * ROOM_W + 2 * GAP)) / 2 + 12;
        int oy = 92;

        addRoom("WEAPONS", "Weapons Tower", true,
                "Mans the fortress weapons, such as the Magical Fire Orb Turret.", 1, 0, ox, oy);
        addRoom("WEST", "West Room", false,
                "An empty room. Rooms unlocked through classes can be built here later.", 0, 1, ox, oy);
        addRoom("LOOM", "Fateweaver Loom", true,
                "Charges while powered and manned. When full it unlocks Sleep, which moves you to the next node.", 1, 1, ox, oy);
        addRoom("EAST", "East Room", false,
                "An empty room. Rooms unlocked through classes can be built here later.", 2, 1, ox, oy);
        addRoom("CHRYSALIS", "Chrysalis Ward", true,
                "Heals crew and is where they change class. Its level caps the highest tier you can unlock.", 1, 2, ox, oy);

        connectRooms("WEAPONS", "LOOM");
        connectRooms("WEST", "LOOM");
        connectRooms("LOOM", "EAST");
        connectRooms("LOOM", "CHRYSALIS");
    }

    private void addRoom(String id, String name, boolean core, String desc, int gx, int gy, int ox, int oy) {
        rooms.put(id, new RoomNode(id, name, core, desc, gx, gy, ox, oy));
    }

    private void connectRooms(String idA, String idB) {
        doorways.add(new Doorway(rooms.get(idA), rooms.get(idB)));
    }

    private void spawnStartingCrew() {
        crewList.add(new CrewUnit("Aldric", BasicClass.UNCLASSED, rooms.get("LOOM"), 0));
        crewList.add(new CrewUnit("Brenna", BasicClass.MAGIC, rooms.get("LOOM"), 1));
        crewList.add(new CrewUnit("Cedric", BasicClass.HEAVY_MELEE, rooms.get("WEAPONS"), 0));
        crewList.add(new CrewUnit("Daria", BasicClass.SUPPORTER, rooms.get("CHRYSALIS"), 0));
    }

    // =========================================================================
    // 2. PATHFINDING
    // =========================================================================
    private List<RoomNode> findRoomPath(RoomNode start, RoomNode goal) {
        if (start == goal) return List.of(start);
        Queue<RoomNode> queue = new ArrayDeque<>();
        Map<RoomNode, RoomNode> parent = new HashMap<>();
        Set<RoomNode> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            RoomNode curr = queue.poll();
            if (curr == goal) {
                LinkedList<RoomNode> path = new LinkedList<>();
                for (RoomNode at = goal; at != null; at = parent.get(at)) path.addFirst(at);
                return path;
            }
            for (Doorway d : doorways) {
                if (d.mode == DoorMode.LOCKED) continue;
                RoomNode nb = d.getNeighbor(curr);
                if (nb != null && visited.add(nb)) {
                    parent.put(nb, curr);
                    queue.add(nb);
                }
            }
        }
        return Collections.emptyList();
    }

    /** Returns true if the move was accepted. */
    private boolean orderCrewMove(CrewUnit unit, RoomNode targetRoom, int preferredSlot) {
        int slot = preferredSlot;
        if (slot < 0 || (targetRoom.slotOccupants[slot] != null && targetRoom.slotOccupants[slot] != unit)) {
            slot = targetRoom.getFirstFreeSlot();
            if (unit.destinationRoom == targetRoom && slot < 0) slot = unit.destinationSlot;
        }
        if (slot < 0) {
            showNotice(targetRoom.name + " is full.");
            return false;
        }

        List<RoomNode> roomPath = findRoomPath(unit.currentRoom, targetRoom);
        if (roomPath.isEmpty()) {
            showNotice("No open route to " + targetRoom.name + ".");
            return false;
        }

        unit.destinationRoom.slotOccupants[unit.destinationSlot] = null;
        targetRoom.slotOccupants[slot] = unit;
        unit.destinationRoom = targetRoom;
        unit.destinationSlot = slot;
        unit.steps.clear();
        unit.waitingAtDoor = false;

        for (int i = 0; i < roomPath.size() - 1; i++) {
            RoomNode rA = roomPath.get(i), rB = roomPath.get(i + 1);
            Doorway door = findDoorBetween(rA, rB);
            if (door == null) continue;
            if (door.verticalWall) {
                int dir = (rB.gx > rA.gx) ? 1 : -1;
                unit.steps.add(new Step(new Point(door.center.x - dir * 26, door.center.y + 8), null));
                unit.steps.add(new Step(new Point(door.center.x + dir * 26, door.center.y + 8), door));
            } else {
                int dir = (rB.gy > rA.gy) ? 1 : -1;
                unit.steps.add(new Step(new Point(door.center.x, door.center.y - dir * 26), null));
                unit.steps.add(new Step(new Point(door.center.x, door.center.y + dir * 26), door));
            }
        }
        Point finalPt = targetRoom.slotCenters[slot];
        unit.steps.add(new Step(new Point(finalPt), null));
        unit.squashStretch = 1.15f;
        return true;
    }

    private Doorway findDoorBetween(RoomNode a, RoomNode b) {
        for (Doorway d : doorways) {
            if ((d.roomA == a && d.roomB == b) || (d.roomA == b && d.roomB == a)) return d;
        }
        return null;
    }

    /** Called when a door on the planned route gets locked. Re-route, or stay in the current room. */
    private void handleBlockedRoute(CrewUnit u) {
        if (orderCrewMove(u, u.destinationRoom, u.destinationSlot)) return;
        u.steps.clear();
        u.destinationRoom.slotOccupants[u.destinationSlot] = null;
        int free = u.currentRoom.getFirstFreeSlot();
        if (free >= 0) {
            u.destinationRoom = u.currentRoom;
            u.destinationSlot = free;
            u.currentRoom.slotOccupants[free] = u;
            u.steps.add(new Step(new Point(u.currentRoom.slotCenters[free]), null));
        }
        showNotice(u.name + " stopped: the route is locked.");
    }

    private void showNotice(String text) {
        notice = text;
        noticeTimer = 3.5f;
    }

    // =========================================================================
    // 3. UPDATE LOOP
    // =========================================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        long now = System.nanoTime();
        float dt = Math.min(0.05f, (now - lastFrameNanos) / 1_000_000_000f);
        lastFrameNanos = now;

        // Doors
        for (Doorway d : doorways) {
            boolean open = false;
            for (CrewUnit u : crewList) {
                if (u.crossing == d) { open = true; break; } // never close on someone mid-doorway
            }
            if (!open && d.mode == DoorMode.HELD_OPEN) open = true;
            if (!open && d.mode == DoorMode.AUTO) {
                for (CrewUnit u : crewList) {
                    if (u.isMoving() && Point2D.distance(u.x, u.y, d.center.x, d.center.y) < 56) { open = true; break; }
                }
            }
            float target = open ? 1f : 0f;
            d.openAmount += (target - d.openAmount) * Math.min(1f, dt * 10f);
        }

        // Crew
        for (CrewUnit u : crewList) {
            u.idleBreath += dt * 3.2f;
            u.squashStretch += (1.0f - u.squashStretch) * Math.min(1f, dt * 9f);
            u.waitingAtDoor = false;

            if (u.steps.isEmpty()) {
                u.walking = false;
                continue;
            }

            Step next = u.steps.peekFirst();
            if (next.door() != null && u.crossing != next.door()) {
                // Standing at the doorway, about to walk through
                if (next.door().mode == DoorMode.LOCKED) {
                    handleBlockedRoute(u);
                    continue;
                }
                if (next.door().openAmount < 0.6f) {
                    u.walking = false;
                    u.waitingAtDoor = true;
                    continue;
                }
                u.crossing = next.door();
            }

            u.walking = true;
            float dx = next.p().x - u.x, dy = next.p().y - u.y;
            float dist = (float) Math.hypot(dx, dy);
            if (Math.abs(dx) > 1f) u.facingRight = dx > 0;

            boolean fast = u.basicClass == BasicClass.LIGHT_MELEE;
            float step = (fast ? 190f : 140f) * dt;
            u.walkCycle += dt * (fast ? 16f : 12f);

            if (dist <= step) {
                u.x = next.p().x;
                u.y = next.p().y;
                u.steps.pollFirst();
                if (next.door() != null) {
                    u.crossing = null;
                    u.currentRoom = next.door().getNeighbor(u.currentRoom);
                }
                if (u.steps.isEmpty()) {
                    u.walking = false;
                    u.squashStretch = 0.88f;
                }
            } else {
                u.x += dx / dist * step;
                u.y += dy / dist * step;
            }
        }

        if (noticeTimer > 0) noticeTimer -= dt;

        float panelTarget = inspected != null ? 1f : 0f;
        panelSlide += (panelTarget - panelSlide) * Math.min(1f, dt * 14f);

        repaint();
    }

    // =========================================================================
    // 4. INPUT
    // =========================================================================
    private void setupControls() {
        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point pt = e.getPoint();
                hoveredCrew = crewAt(pt);
                hoveredDoor = hoveredCrew == null ? doorAt(pt) : null;
                hoveredRoom = roomAt(pt);
                hoveredSlotIndex = hoveredRoom != null ? padAt(hoveredRoom, pt) : -1;
                setCursor(Cursor.getPredefinedCursor(
                        hoveredCrew != null || hoveredDoor != null || buttonAt(pt) != null
                                ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                Point pt = e.getPoint();

                if (SwingUtilities.isRightMouseButton(e)) {
                    RoomNode r = roomAt(pt);
                    if (selectedCrew == null) {
                        showNotice("Select a person first.");
                    } else if (r != null) {
                        orderCrewMove(selectedCrew, r, padAt(r, pt));
                    }
                    return;
                }

                Runnable button = buttonAt(pt);
                if (button != null) { button.run(); return; }

                if (inspected != null && panelBounds().contains(pt)) return; // clicks on the panel body do nothing

                CrewUnit u = crewAt(pt);
                if (u != null) {
                    selectedCrew = u;
                    inspected = u;
                    u.squashStretch = 1.15f;
                    return;
                }
                Doorway d = doorAt(pt);
                if (d != null) { inspected = d; return; }
                RoomNode r = roomAt(pt);
                if (r != null) { inspected = r; return; }

                // Empty space: clear everything
                inspected = null;
                selectedCrew = null;
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_1 -> selectCrew(0);
                    case KeyEvent.VK_2 -> selectCrew(1);
                    case KeyEvent.VK_3 -> selectCrew(2);
                    case KeyEvent.VK_4 -> selectCrew(3);
                    case KeyEvent.VK_ESCAPE -> { inspected = null; selectedCrew = null; }
                }
            }
        });
    }

    private void selectCrew(int idx) {
        if (idx < crewList.size()) {
            selectedCrew = crewList.get(idx);
            inspected = selectedCrew;
            selectedCrew.squashStretch = 1.15f;
        }
    }

    private CrewUnit crewAt(Point pt) {
        // Front-most first (highest y is drawn last)
        List<CrewUnit> sorted = new ArrayList<>(crewList);
        sorted.sort(Comparator.comparingDouble((CrewUnit c) -> c.y).reversed());
        for (CrewUnit u : sorted) if (u.getSpriteHitbox().contains(pt)) return u;
        return null;
    }

    private Doorway doorAt(Point pt) {
        for (Doorway d : doorways) if (d.hitbox.contains(pt)) return d;
        return null;
    }

    private RoomNode roomAt(Point pt) {
        for (RoomNode r : rooms.values()) if (r.bounds.contains(pt)) return r;
        return null;
    }

    private int padAt(RoomNode r, Point pt) {
        for (int s = 0; s < 4; s++) {
            if (Point2D.distance(pt.x, pt.y, r.slotCenters[s].x, r.slotCenters[s].y) <= 30) return s;
        }
        return -1;
    }

    private Runnable buttonAt(Point pt) {
        if (inspected == null) return null;
        for (Map.Entry<Rectangle, Runnable> b : panelButtons.entrySet()) {
            if (b.getKey().contains(pt)) return b.getValue();
        }
        return null;
    }

    // =========================================================================
    // 5. RENDERING
    // =========================================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        drawHeader(g2);
        drawRooms(g2);
        drawDoors(g2);
        drawPathPreview(g2);
        drawCrew(g2);
        drawFooter(g2);
        drawInfoPanel(g2);
    }

    private void drawHeader(Graphics2D g2) {
        g2.setFont(F_TITLE);
        g2.setColor(INK);
        g2.drawString("Fateweavers", 32, 44);
        int w = g2.getFontMetrics().stringWidth("Fateweavers");
        g2.setFont(F_LABEL);
        g2.setColor(INK_SOFT);
        g2.drawString("Module 1  ·  Fortress sandbox", 32 + w + 14, 44);
    }

    private void drawRooms(Graphics2D g2) {
        boolean showPads = selectedCrew != null;
        for (RoomNode r : rooms.values()) {
            Rectangle b = r.bounds;
            boolean inspectedRoom = inspected == r;

            g2.setColor(r.core ? CORE_FLOOR : NEUTRAL_FLOOR);
            g2.fillRect(b.x, b.y, b.width, b.height);

            // Selected room: solid bar along the top edge (no outline)
            if (inspectedRoom) {
                g2.setColor(INK);
                g2.fillRect(b.x, b.y, b.width, 4);
            }

            // Room label
            g2.setFont(F_SMALL);
            g2.setColor(inspectedRoom ? INK : INK_SOFT);
            g2.drawString(r.name.toUpperCase(), b.x + 14, b.y + 24);

            // Pads: faint, and only shown while someone is selected to move
            if (showPads) {
                for (int s = 0; s < 4; s++) {
                    Point p = r.slotCenters[s];
                    boolean hov = r == hoveredRoom && s == hoveredSlotIndex && hoveredCrew == null;
                    g2.setColor(hov ? PAD_HOVER : PAD);
                    g2.fillOval(p.x - 22, p.y - 6, 44, 18);
                }
            }

            // Station console beside pad 0 in core rooms: dark when manned
            if (r.core) {
                Point p = r.slotCenters[0];
                g2.setColor(r.stationOperator() != null ? INK : new Color(212, 212, 212));
                g2.fillRoundRect(p.x - 46, p.y - 22, 12, 22, 4, 4);
            }
        }
    }

    private void drawDoors(Graphics2D g2) {
        for (Doorway d : doorways) {
            int cx = d.center.x, cy = d.center.y;
            int half = 26;
            // Passage across the wall gap, in floor colour so it reads as an opening
            boolean bothNeutral = !d.roomA.core && !d.roomB.core;
            g2.setColor(bothNeutral ? NEUTRAL_FLOOR : CORE_FLOOR);
            if (d.verticalWall) g2.fillRect(cx - GAP / 2 - 1, cy - half, GAP + 2, half * 2);
            else g2.fillRect(cx - half, cy - GAP / 2 - 1, half * 2, GAP + 2);

            Color panel = switch (d.mode) {
                case LOCKED -> INK;
                case HELD_OPEN, AUTO -> new Color(150, 150, 150);
            };
            if (d == hoveredDoor || d == inspected) panel = d.mode == DoorMode.LOCKED ? INK : new Color(90, 90, 90);

            int len = (int) (half * (1f - d.openAmount));
            g2.setColor(panel);
            if (len > 1) {
                if (d.verticalWall) {
                    g2.fillRect(cx - 3, cy - half, 6, len);
                    g2.fillRect(cx - 3, cy + half - len, 6, len);
                } else {
                    g2.fillRect(cx - half, cy - 3, len, 6);
                    g2.fillRect(cx + half - len, cy - 3, len, 6);
                }
            }
        }
    }

    private void drawPathPreview(Graphics2D g2) {
        if (selectedCrew == null || selectedCrew.steps.isEmpty()) return;
        g2.setColor(INK_FAINT);
        float px = selectedCrew.x, py = selectedCrew.y + 6;
        for (Step s : selectedCrew.steps) {
            float qx = s.p().x, qy = s.p().y + 6;
            float len = (float) Math.hypot(qx - px, qy - py);
            for (float t = 0; t < len; t += 10) {
                float k = t / Math.max(1, len);
                g2.fillOval((int) (px + (qx - px) * k) - 1, (int) (py + (qy - py) * k) - 1, 3, 3);
            }
            px = qx;
            py = qy;
        }
    }

    private void drawFooter(Graphics2D g2) {
        g2.setFont(F_LABEL);
        if (noticeTimer > 0 && notice != null) {
            int a = (int) (255 * Math.min(1f, noticeTimer));
            g2.setColor(new Color(17, 17, 17, a));
            g2.drawString(notice, 32, HEIGHT - 28);
        } else {
            g2.setColor(INK_SOFT);
            g2.drawString("Left-click to inspect  ·  Right-click a room to move the selected person  ·  1–4 select crew  ·  Esc to deselect",
                    32, HEIGHT - 28);
        }
    }

    // =========================================================================
    // 6. INFO PANEL (slides in from the right when something is clicked)
    // =========================================================================
    private Object lastInspected = null; // keeps content visible while the panel slides out

    private Rectangle panelBounds() {
        int shownX = WIDTH - PANEL_W - PANEL_MARGIN;
        int x = (int) (WIDTH + (shownX - WIDTH) * panelSlide);
        return new Rectangle(x, PANEL_MARGIN, PANEL_W, HEIGHT - PANEL_MARGIN * 2);
    }

    private void drawInfoPanel(Graphics2D g2) {
        panelButtons.clear();
        if (inspected != null) lastInspected = inspected;
        if (panelSlide < 0.01f || lastInspected == null) return;

        Rectangle p = panelBounds();
        g2.setColor(new Color(0, 0, 0, 10));
        g2.fillRoundRect(p.x - 2, p.y + 2, p.width + 4, p.height + 2, 18, 18); // soft shadow, no outline
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(p.x, p.y, p.width, p.height, 16, 16);

        int x = p.x + 28;
        int y = p.y + 44;
        int w = p.width - 56;
        Object o = lastInspected;

        if (o instanceof CrewUnit u) {
            y = heading(g2, u.name, "Crew member", x, y);
            y = row(g2, "Class", u.basicClass.title, x, y, w);
            y = row(g2, "Location", u.currentRoom.name, x, y, w);
            String status = u.waitingAtDoor ? "Waiting for a door"
                    : u.isMoving() ? "Walking to " + u.destinationRoom.name
                    : (u.destinationSlot == 0 && u.currentRoom.core ? "Operating station" : "Idle");
            y = row(g2, "Status", status, x, y, w);
            y += 6;
            y = paragraph(g2, u.basicClass.description, x, y, w);
            y += 18;
            y = sectionLabel(g2, "Change class", x, y);
            BasicClass[] classes = BasicClass.values();
            int bw = (w - 8) / 2, bh = 32;
            for (int i = 0; i < classes.length; i++) {
                BasicClass bc = classes[i];
                Rectangle r = new Rectangle(x + (i % 2) * (bw + 8), y + (i / 2) * (bh + 8), bw, bh);
                button(g2, r, bc.title, u.basicClass == bc, () -> {
                    u.basicClass = bc;
                    u.squashStretch = 1.25f;
                });
            }
        } else if (o instanceof RoomNode r) {
            y = heading(g2, r.name, r.core ? "Core room" : "Neutral room", x, y);
            y = paragraph(g2, r.description, x, y, w);
            y += 14;
            long count = crewList.stream().filter(c -> c.currentRoom == r && !c.isMoving()).count();
            y = row(g2, "Occupants", count + " / 4", x, y, w);
            if (r.core) {
                CrewUnit op = r.stationOperator();
                y = row(g2, "Station", op != null ? "Manned by " + op.name : "Unmanned", x, y, w);
            }
            y += 14;
            y = sectionLabel(g2, "Crew here", x, y);
            boolean any = false;
            for (CrewUnit c : crewList) {
                if (c.currentRoom == r && !c.isMoving()) {
                    any = true;
                    Rectangle br = new Rectangle(x, y, w, 30);
                    button(g2, br, c.name + "  ·  " + c.basicClass.title, false, () -> {
                        selectedCrew = c;
                        inspected = c;
                    });
                    y += 36;
                }
            }
            if (!any) {
                g2.setFont(F_BODY);
                g2.setColor(INK_SOFT);
                g2.drawString("Nobody", x, y + 16);
            }
        } else if (o instanceof Doorway d) {
            y = heading(g2, "Door", d.roomA.name + "  ↔  " + d.roomB.name, x, y);
            y = row(g2, "Mode", d.mode.label, x, y, w);
            y = row(g2, "State", d.openAmount > 0.5f ? "Open" : "Closed", x, y, w);
            y += 6;
            String help = switch (d.mode) {
                case AUTO -> "Opens when someone walks through.";
                case HELD_OPEN -> "Stays open.";
                case LOCKED -> "Stays shut. Crew re-route around it, or stop if there is no other way.";
            };
            y = paragraph(g2, help, x, y, w);
            y += 18;
            y = sectionLabel(g2, "Set mode", x, y);
            DoorMode[] modes = DoorMode.values();
            int bw = (w - 16) / 3;
            for (int i = 0; i < modes.length; i++) {
                DoorMode m = modes[i];
                button(g2, new Rectangle(x + i * (bw + 8), y, bw, 32), m.label, d.mode == m, () -> d.mode = m);
            }
        }

        // Close button (top-right)
        Rectangle close = new Rectangle(p.x + p.width - 44, p.y + 16, 28, 28);
        g2.setColor(close.contains(mousePos()) ? BUTTON : Color.WHITE);
        g2.fillOval(close.x, close.y, close.width, close.height);
        g2.setColor(INK_SOFT);
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(close.x + 10, close.y + 10, close.x + 18, close.y + 18);
        g2.drawLine(close.x + 18, close.y + 10, close.x + 10, close.y + 18);
        g2.setStroke(new BasicStroke(1f));
        panelButtons.put(close, () -> inspected = null);
    }

    private Point mousePos() {
        Point m = getMousePosition();
        return m != null ? m : new Point(-1, -1);
    }

    private int heading(Graphics2D g2, String title, String subtitle, int x, int y) {
        g2.setFont(F_HEADING);
        g2.setColor(INK);
        g2.drawString(title, x, y);
        g2.setFont(F_LABEL);
        g2.setColor(INK_SOFT);
        g2.drawString(subtitle, x, y + 22);
        return y + 56;
    }

    private int sectionLabel(Graphics2D g2, String text, int x, int y) {
        g2.setFont(F_SMALL);
        g2.setColor(INK_SOFT);
        g2.drawString(text.toUpperCase(), x, y);
        return y + 12;
    }

    private int row(Graphics2D g2, String label, String value, int x, int y, int w) {
        g2.setFont(F_BODY);
        g2.setColor(INK_SOFT);
        g2.drawString(label, x, y);
        g2.setFont(F_BODY_BOLD);
        g2.setColor(INK);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(value, x + w - fm.stringWidth(value), y);
        return y + 26;
    }

    private int paragraph(Graphics2D g2, String text, int x, int y, int w) {
        g2.setFont(F_BODY);
        g2.setColor(new Color(70, 70, 70));
        FontMetrics fm = g2.getFontMetrics();
        StringBuilder line = new StringBuilder();
        for (String word : text.split(" ")) {
            String trial = line.isEmpty() ? word : line + " " + word;
            if (fm.stringWidth(trial) > w && !line.isEmpty()) {
                g2.drawString(line.toString(), x, y);
                y += 19;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(trial);
            }
        }
        if (!line.isEmpty()) {
            g2.drawString(line.toString(), x, y);
            y += 19;
        }
        return y;
    }

    private void button(Graphics2D g2, Rectangle r, String label, boolean active, Runnable action) {
        boolean hov = r.contains(mousePos());
        g2.setColor(active ? INK : (hov ? new Color(222, 222, 222) : BUTTON));
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 8, 8);
        g2.setFont(F_LABEL);
        g2.setColor(active ? Color.WHITE : INK);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(label, r.x + 12, r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2);
        panelButtons.put(r, action);
    }

    // =========================================================================
    // 7. CHARACTERS (the only things with outlines)
    // =========================================================================
    private void drawCrew(Graphics2D g2) {
        List<CrewUnit> sorted = new ArrayList<>(crewList);
        sorted.sort(Comparator.comparingDouble(u -> u.y));
        for (CrewUnit u : sorted) drawCharacter(g2, u);
    }

    private void drawCharacter(Graphics2D g2, CrewUnit u) {
        boolean sel = u == selectedCrew;
        boolean hov = u == hoveredCrew;

        // Shadow (darker when selected/hovered)
        g2.setColor(new Color(0, 0, 0, sel ? 70 : hov ? 50 : 28));
        g2.fillOval((int) u.x - 15, (int) u.y + 1, 30, 12);

        float legSwing = u.walking ? (float) Math.sin(u.walkCycle) * 5.5f : 0f;
        float armSwing = u.walking ? (float) -Math.sin(u.walkCycle) * 5.0f : (float) Math.sin(u.idleBreath * 2f) * 1.2f;
        float bodyBob = u.walking ? (float) Math.abs(Math.sin(u.walkCycle)) * -3.5f : (float) Math.sin(u.idleBreath) * 1.2f;

        AffineTransform old = g2.getTransform();
        g2.translate(u.x, u.y + bodyBob);
        g2.scale((u.facingRight ? 1f : -1f) * (2.0f - u.squashStretch), u.squashStretch);

        Color main = u.basicClass.mainColor, shade = u.basicClass.shadeColor;

        // Back arm
        outlinedRect(g2, 6, (int) (-21 + armSwing * 0.6f), 7, 13, 4, shade);
        outlinedRect(g2, 7, (int) (-11 + armSwing * 0.6f), 6, 5, 3, SKIN_SHADE);

        // Legs
        int leftLegY = (int) (-9 + Math.max(0, legSwing));
        int rightLegY = (int) (-9 + Math.max(0, -legSwing));
        outlinedRect(g2, -9, leftLegY, 8, 11, 4, shade);
        outlinedRect(g2, -10, leftLegY + 7, 9, 5, 3, main.darker());
        outlinedRect(g2, 1, rightLegY, 8, 11, 4, main);
        outlinedRect(g2, 1, rightLegY + 7, 9, 5, 3, shade);

        // Torso
        outlinedRect(g2, -10, -24, 20, 17, 6, main);
        g2.setColor(shade);
        g2.fillRoundRect(-8, -22, 6, 13, 4, 4);

        // Front arm
        outlinedRect(g2, -14, (int) (-21 - armSwing), 8, 13, 4, main);
        outlinedRect(g2, -13, (int) (-11 - armSwing), 6, 6, 3, SKIN_LIGHT);

        // Head
        g2.setColor(OUTLINE);
        g2.fillOval(-13, -44, 26, 25);
        g2.setColor(SKIN_LIGHT);
        g2.fillOval(-11, -42, 22, 21);
        g2.setColor(SKIN_SHADE);
        g2.fillArc(-11, -42, 22, 21, 115, 165);

        drawGear(g2, u.basicClass, armSwing);
        g2.setTransform(old);

        // Name sits under the feet so it never covers room labels. Selected = bold black.
        if (sel || hov) {
            g2.setFont(sel ? F_LABEL.deriveFont(Font.BOLD) : F_LABEL);
            g2.setColor(sel ? INK : INK_SOFT);
            int tw = g2.getFontMetrics().stringWidth(u.name);
            g2.drawString(u.name, (int) u.x - tw / 2, (int) u.y + 27);
        }
    }

    private void drawGear(Graphics2D g2, BasicClass cls, float armSwing) {
        switch (cls) {
            case MAGIC -> {
                int[] hx = {-14, 0, 14};
                int[] hy = {-39, -55, -39};
                g2.setColor(cls.mainColor);
                g2.fillPolygon(hx, hy, 3);
                g2.setColor(OUTLINE);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawPolygon(hx, hy, 3);
                g2.setColor(Color.WHITE);
                g2.fillOval(14, (int) (-28 + armSwing), 8, 8);
                g2.setColor(OUTLINE);
                g2.drawOval(14, (int) (-28 + armSwing), 8, 8);
            }
            case RANGED -> {
                outlinedRect(g2, -12, -44, 24, 6, 3, cls.shadeColor);
                g2.setColor(new Color(60, 60, 60));
                g2.setStroke(new BasicStroke(2.2f));
                g2.drawArc(8, -26, 10, 20, -80, 160);
            }
            case LIGHT_MELEE -> {
                g2.setColor(cls.mainColor);
                g2.fillRect(-11, -38, 22, 4);
                g2.setColor(new Color(90, 90, 90));
                g2.fillRect(-18, (int) (-9 - armSwing), 9, 3);
                g2.fillRect(11, (int) (-9 + armSwing), 9, 3);
            }
            case HEAVY_MELEE -> {
                outlinedRect(g2, -15, -24, 8, 7, 3, new Color(225, 225, 225));
                outlinedRect(g2, 8, -19, 9, 14, 4, new Color(235, 235, 235));
                g2.setColor(new Color(90, 90, 90));
                g2.fillRect(12, -15, 2, 8);
            }
            case SUPPORTER -> {
                outlinedRect(g2, -9, -38, 8, 6, 2, Color.WHITE);
                outlinedRect(g2, 1, -38, 8, 6, 2, Color.WHITE);
                g2.setColor(new Color(80, 80, 80));
                g2.fillRect(-16, (int) (-16 - armSwing), 4, 10);
            }
            case UNCLASSED -> { }
        }
        g2.setStroke(new BasicStroke(1f));
    }

    private void outlinedRect(Graphics2D g2, int x, int y, int w, int h, int arc, Color fill) {
        g2.setColor(OUTLINE);
        g2.fillRoundRect(x - 2, y - 2, w + 4, h + 4, arc + 2, arc + 2);
        g2.setColor(fill);
        g2.fillRoundRect(x, y, w, h, arc, arc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Fateweavers");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Fateweavers game = new Fateweavers();
            frame.setContentPane(game);
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            game.requestFocusInWindow();
        });
    }
}
