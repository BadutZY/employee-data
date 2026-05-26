package form;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.Timer;

public class MainDashboard extends javax.swing.JFrame {

    // =====================================================================
    // PALETTE
    // =====================================================================
    static final Color C_BG          = new Color(10, 11, 18);
    static final Color C_SIDEBAR     = new Color(14, 15, 26);
    static final Color C_CONTENT     = new Color(13, 14, 24);
    static final Color C_CARD        = new Color(19, 21, 35);
    static final Color C_FIELD       = new Color(26, 28, 46);
    static final Color C_ACCENT      = new Color(99, 102, 241);
    static final Color C_ACCENT2     = new Color(139, 92, 246);
    static final Color C_SUCCESS     = new Color(34, 197, 94);
    static final Color C_DANGER      = new Color(239, 68, 68);
    static final Color C_WARN        = new Color(234, 179, 8);
    static final Color C_INFO        = new Color(56, 189, 248);
    static final Color C_TEXT        = new Color(226, 232, 240);
    static final Color C_TEXT_MUTED  = new Color(100, 116, 139);
    static final Color C_BORDER      = new Color(30, 33, 55);
    static final Color C_NAV_HOVER   = new Color(20, 23, 40);
    static final Color C_NAV_ACTIVE  = new Color(99, 102, 241, 30);
    static final Color C_NAV_ACTIVE_BAR = new Color(99, 102, 241);

    // =====================================================================
    // STATE
    // =====================================================================
    private int activeNav = 0;
    private JPanel contentArea;
    private JPanel[] navButtons = new JPanel[5];
    private JPanel[] contentPanels;
    private String loggedUser = "Admin";

    // Animation
    private Timer slideInTimer;

    // Sidebar
    private int sidebarWidth = 230;

    // Cached nav images (scaled)
    private Image[] navImages = new Image[5];

    // =====================================================================
    // LAYOUT CONSTANTS
    // =====================================================================
    private static final int TOPBAR_H = 58;

    public MainDashboard() {
        applyLAF();
        setAppIcon();
        loadNavImages();
        initComponents();
    }

    MainDashboard(String username) {
        this.loggedUser = username;
        applyLAF();
        setAppIcon();
        loadNavImages();
        initComponents();
    }

    private void applyLAF() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
    }

    private void setAppIcon() {
        try {
            java.net.URL u = getClass().getResource("/foto/icon.png");
            if (u != null) setIconImage(new ImageIcon(u).getImage());
        } catch (Exception ignored) {}
    }

    /**
     * Load and scale nav images:
     * index 0 = Dashboard  (no specific icon, use accent circle)
     * index 1 = Karyawan   -> /foto/user.png
     * index 2 = Golongan   -> /foto/document.png
     * index 3 = Lembur     -> /foto/time.png
     * index 4 = Penggajian -> /foto/money.png
     */
    private void loadNavImages() {
        String[] paths = { null, "/foto/user.png", "/foto/document.png", "/foto/time.png", "/foto/money.png" };
        for (int i = 0; i < paths.length; i++) {
            if (paths[i] != null) {
                try {
                    java.net.URL u = getClass().getResource(paths[i]);
                    if (u != null) {
                        Image raw = new ImageIcon(u).getImage();
                        navImages[i] = raw.getScaledInstance(22, 22, Image.SCALE_SMOOTH);
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Sistem Penggajian Karyawan - Rizky Maulana Putra");

        // ---- Start FULLSCREEN ----
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 650));

        JPanel root = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(C_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setOpaque(false);

        // We use a BorderLayout wrapper so everything resizes correctly
        setLayout(new BorderLayout());

        // ---- TOP BAR ----
        JPanel topBar = buildTopBar();
        add(topBar, BorderLayout.NORTH);

        // ---- CENTER: sidebar + content ----
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);

        JPanel sidebar = buildSidebar();
        sidebar.setPreferredSize(new Dimension(sidebarWidth, 0));

        contentArea = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(C_CONTENT);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        contentArea.setOpaque(false);

        center.add(sidebar, BorderLayout.WEST);
        center.add(contentArea, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        // Build content panels
        contentPanels = new JPanel[]{
            new DashboardHomePanel(this),
            PanelKaryawan.create(),
            PanelGolongan.create(),
            PanelLembur.create(),
            PanelPenggajian.create()
        };

        showContent(0);
    }

    // =====================================================================
    // TOP BAR
    // =====================================================================
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(C_SIDEBAR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(C_BORDER);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                // accent top line
                GradientPaint gp = new GradientPaint(0, 0, new Color(99,102,241,80), getWidth(), 0, new Color(139,92,246,40));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), 3);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, TOPBAR_H);
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(1200, TOPBAR_H));

        // App title
        JLabel appTitle = new JLabel("SISTEM PENGGAJIAN KARYAWAN") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp=new GradientPaint(0,0,C_ACCENT,getWidth(),0,C_ACCENT2);
                g2.setPaint(gp);
                g2.setFont(getFont());
                g2.drawString(getText(),0,g2.getFontMetrics().getAscent());
                g2.dispose();
            }
        };
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appTitle.setBounds(20, 0, 400, TOPBAR_H);
        bar.add(appTitle);

        // Clock label - using ComponentListener to position it dynamically
        JLabel clockLabel = new JLabel("", SwingConstants.RIGHT);
        clockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clockLabel.setForeground(C_TEXT_MUTED);
        clockLabel.setBounds(400, 0, 400, TOPBAR_H);
        bar.add(clockLabel);
        clockLabel.setText(new java.text.SimpleDateFormat("EEEE, dd MMM yyyy  |  HH:mm:ss",
                new java.util.Locale("id","ID")).format(new java.util.Date()));
        Timer clock = new Timer(1000, e -> {
            clockLabel.setText(new java.text.SimpleDateFormat("EEEE, dd MMM yyyy  |  HH:mm:ss",
                    new java.util.Locale("id","ID")).format(new java.util.Date()));
            // Center the clock in the bar
            int barW = bar.getWidth();
            int lw = 400;
            clockLabel.setBounds((barW - lw)/2, 0, lw, TOPBAR_H);
        });
        clock.start();

        // User avatar + name (right side)
        JPanel userArea = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp=new GradientPaint(0,9,C_ACCENT,32,37,C_ACCENT2);
                g2.setPaint(gp);
                g2.fillOval(0,9,32,32);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI",Font.BOLD,14));
                FontMetrics fm=g2.getFontMetrics();
                String ini=loggedUser.length()>0?String.valueOf(loggedUser.charAt(0)).toUpperCase():"A";
                g2.drawString(ini,(32-fm.stringWidth(ini))/2,9+fm.getAscent()+(32-fm.getHeight())/2);
                g2.setColor(C_TEXT);
                g2.setFont(new Font("Segoe UI",Font.PLAIN,12));
                g2.drawString(loggedUser,42,(TOPBAR_H+g2.getFontMetrics().getAscent()-g2.getFontMetrics().getDescent())/2+1);
                g2.dispose();
            }
        };
        userArea.setOpaque(false);
        // position from right - use ComponentListener
        userArea.setBounds(900, 0, 180, TOPBAR_H);
        bar.add(userArea);

        // Logout button
        JButton logoutBtn = new JButton("X") {
            private boolean h=false;
            {addMouseListener(new MouseAdapter(){
                @Override public void mouseEntered(MouseEvent e){h=true;repaint();}
                @Override public void mouseExited(MouseEvent e){h=false;repaint();}
            });}
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if(h){g2.setColor(new Color(239,68,68,35));g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);}
                g2.setColor(h?C_DANGER:C_TEXT_MUTED);
                g2.setFont(new Font("Segoe UI",Font.PLAIN,18));
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g){}
        };
        logoutBtn.setContentAreaFilled(false); logoutBtn.setBorderPainted(false); logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setBounds(1140, 13, 34, 34);
        logoutBtn.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "Yakin keluar dari aplikasi?", "Konfirmasi Logout",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (r == JOptionPane.YES_OPTION) {
                new FormLogin().setVisible(true);
                dispose();
            }
        });
        bar.add(logoutBtn);

        // Reposition right-side elements on resize
        bar.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                int w = bar.getWidth();
                userArea.setBounds(w - 210, 0, 180, TOPBAR_H);
                logoutBtn.setBounds(w - 50, 13, 34, 34);
            }
        });

        return bar;
    }

    // =====================================================================
    // SIDEBAR
    // =====================================================================
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g.create();
                g2.setColor(C_SIDEBAR);
                g2.fillRect(0,0,getWidth(),getHeight());
                // right border
                g2.setColor(C_BORDER);
                g2.drawLine(getWidth()-1,0,getWidth()-1,getHeight());
                // bottom glow
                GradientPaint gp=new GradientPaint(0,getHeight()-120,new Color(99,102,241,0),0,getHeight(),new Color(99,102,241,20));
                g2.setPaint(gp);
                g2.fillRect(0,getHeight()-120,getWidth(),120);
                g2.dispose();
            }
        };
        sidebar.setOpaque(false);

        // Nav items: label, desc, icon index, navColor
        String[][] navItems = {
            { "Dashboard",   "Ringkasan data"  },
            { "Karyawan",    "Data karyawan"   },
            { "Golongan",    "Kelola golongan"  },
            { "Lembur",      "Data lembur"     },
            { "Penggajian",  "Proses gaji"     }
        };
        // Fallback text icons for when image not loaded
        String[] fallbackIcons = { "DB", "KR", "GL", "LB", "GJ" };
        Color[] navColors = { C_ACCENT, C_INFO, C_ACCENT2, C_WARN, C_SUCCESS };

        int y = 16;
        for (int i = 0; i < navItems.length; i++) {
            final int idx = i;
            JPanel nav = buildNavItem(fallbackIcons[i], navItems[i][0], navItems[i][1], navColors[i], i == 0, navImages[i]);
            nav.setBounds(10, y, sidebarWidth - 20, 58);
            nav.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { setActiveNav(idx); }
            });
            navButtons[i] = nav;
            sidebar.add(nav);
            y += 64;
        }

        return sidebar;
    }

    private JPanel buildNavItem(String fallbackIcon, String label, String desc,
                                 Color accentColor, boolean active, Image navImg) {
        JPanel p = new JPanel(null) {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e){ hover=true; repaint(); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
                    @Override public void mouseExited(MouseEvent e) { hover=false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isActive = getClientProperty("active") != null && (Boolean)getClientProperty("active");
                Color bg = isActive ? C_NAV_ACTIVE : (hover ? C_NAV_HOVER : new Color(0,0,0,0));
                g2.setColor(bg);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);

                // Active left accent bar
                if (isActive) {
                    GradientPaint gp=new GradientPaint(0,0,accentColor,0,getHeight(),accentColor.darker());
                    g2.setPaint(gp);
                    g2.fillRoundRect(0,8,4,getHeight()-16,4,4);
                }

                // Icon circle background
                Color iconBg = isActive
                    ? new Color(accentColor.getRed(),accentColor.getGreen(),accentColor.getBlue(),55)
                    : new Color(26,29,50);
                g2.setColor(iconBg);
                int iconSize = 36;
                int iconX = 14, iconY = (getHeight()-iconSize)/2;
                g2.fillOval(iconX, iconY, iconSize, iconSize);

                // Draw image icon or fallback text
                if (navImg != null) {
                    // Center the 22x22 icon inside the 36x36 circle
                    int imgX = iconX + (iconSize - 22) / 2;
                    int imgY = iconY + (iconSize - 22) / 2;
                    // Tint image based on active state
                    if (isActive) {
                        g2.drawImage(navImg, imgX, imgY, 22, 22, null);
                        // color overlay
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.0f));
                    } else {
                        // Draw with slight muted tint when not active
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
                    }
                    g2.drawImage(navImg, imgX, imgY, 22, 22, null);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                } else {
                    // Fallback text icon
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    FontMetrics ifm=g2.getFontMetrics();
                    g2.setColor(isActive ? accentColor : C_TEXT_MUTED);
                    g2.drawString(fallbackIcon, iconX+(iconSize-ifm.stringWidth(fallbackIcon))/2,
                            iconY+ifm.getAscent()+(iconSize-ifm.getHeight())/2);
                }

                // Label text
                int textX = iconX + iconSize + 12;
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g2.setColor(isActive ? C_TEXT : (hover ? C_TEXT : C_TEXT_MUTED));
                g2.drawString(label, textX, getHeight()/2 - 2);

                // Description text
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(isActive
                    ? new Color(accentColor.getRed(),accentColor.getGreen(),accentColor.getBlue(),170)
                    : new Color(65,72,100));
                g2.drawString(desc, textX, getHeight()/2 + 14);

                g2.dispose();
            }
        };
        p.putClientProperty("active", active);
        p.setOpaque(false);
        return p;
    }

    // =====================================================================
    // NAVIGATION
    // =====================================================================
    public void setActiveNav(int idx) {
        if (idx == activeNav) return;
        for (int i = 0; i < navButtons.length; i++) {
            navButtons[i].putClientProperty("active", i == idx);
            navButtons[i].repaint();
        }
        activeNav = idx;
        showContent(idx);
    }

    private void showContent(int idx) {
        contentArea.removeAll();
        JPanel panel = contentPanels[idx];
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
        // Real-time refresh: setiap kali panel ditampilkan, reload semua data dari DB
        if (panel instanceof PanelKaryawan)   ((PanelKaryawan)   panel).refresh();
        if (panel instanceof PanelGolongan)   ((PanelGolongan)   panel).refresh();
        if (panel instanceof PanelLembur)     ((PanelLembur)     panel).refresh();
        if (panel instanceof PanelPenggajian) ((PanelPenggajian) panel).refresh();
        animateContentIn();
    }

    private void animateContentIn() {
        final int[] step = {0};
        final int STEPS = 14;
        if (slideInTimer != null && slideInTimer.isRunning()) slideInTimer.stop();
        contentArea.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));
        slideInTimer = new Timer(16, e -> {
            step[0]++;
            int offset = (int)(22 * (1.0 - (double)step[0]/STEPS));
            contentArea.setBorder(BorderFactory.createEmptyBorder(Math.max(0,offset), 0, 0, 0));
            if (step[0] >= STEPS) {
                contentArea.setBorder(null);
                ((Timer)e.getSource()).stop();
            }
            contentArea.repaint();
        });
        slideInTimer.start();
    }

    // =====================================================================
    // HELPER: Standard section title
    // =====================================================================
    public static JLabel makeSectionTitle(String text) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp=new GradientPaint(0,0,C_ACCENT,getWidth()/2,0,C_ACCENT2);
                g2.setPaint(gp);
                g2.setFont(getFont());
                g2.drawString(getText(),0,g2.getFontMetrics().getAscent());
                g2.dispose();
            }
        };
        l.setFont(new Font("Segoe UI", Font.BOLD, 21));
        l.setForeground(C_ACCENT);
        return l;
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new MainDashboard().setVisible(true));
    }
}


// =========================================================================
// DASHBOARD HOME PANEL
// =========================================================================
class DashboardHomePanel extends JPanel {
    public DashboardHomePanel(MainDashboard dashboard) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JScrollPane scroll = new JScrollPane(buildContent(dashboard));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        styleScrollBar(scroll);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildContent(MainDashboard dashboard) {
        JPanel p = new JPanel(null);
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(900, 720));

        // Header
        JLabel title = MainDashboard.makeSectionTitle("Dashboard Overview");
        title.setBounds(32, 30, 420, 36);
        p.add(title);

        JLabel sub = new JLabel("Ringkasan sistem penggajian karyawan Anda");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(MainDashboard.C_TEXT_MUTED);
        sub.setBounds(32, 68, 500, 20);
        p.add(sub);

        // Stat cards — adaptive width
        String[][] stats = {
            { "KR", "Total Karyawan",  "tb_karyawan"   },
            { "GL", "Golongan",        "tb_golongan"   },
            { "LB", "Entri Lembur",    "tb_lembur"     },
            { "GJ", "Data Gaji",       "tb_penggajian" }
        };
        Color[] statColors = {
            MainDashboard.C_ACCENT, MainDashboard.C_ACCENT2,
            MainDashboard.C_WARN,   MainDashboard.C_SUCCESS
        };

        int cx = 32;
        int cardW = 195;
        for (int i = 0; i < stats.length; i++) {
            final String tbl   = stats[i][2];
            final String label = stats[i][1];
            final String icon  = stats[i][0];
            final Color color  = statColors[i];
            JPanel card = buildStatCard(icon, label, tbl, color);
            card.setBounds(cx, 106, cardW, 114);
            p.add(card);
            cx += cardW + 14;
        }

        // Quick access
        JLabel qTitle = new JLabel("Akses Cepat");
        qTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        qTitle.setForeground(MainDashboard.C_TEXT);
        qTitle.setBounds(32, 252, 200, 26);
        p.add(qTitle);

        String[] qLabels = { "Form Karyawan", "Form Golongan", "Form Lembur", "Form Penggajian" };
        String[] qIcons  = { "KR", "GL", "LB", "GJ" };
        Color[]  qColors = { MainDashboard.C_ACCENT, MainDashboard.C_ACCENT2,
                              MainDashboard.C_WARN,   MainDashboard.C_SUCCESS };
        int[] navIdx     = { 1, 2, 3, 4 };

        int qx = 32;
        for (int i = 0; i < qLabels.length; i++) {
            final int ni = navIdx[i];
            JPanel qBtn = buildQuickBtn(qIcons[i], qLabels[i], qColors[i]);
            qBtn.setBounds(qx, 288, 195, 84);
            qBtn.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { dashboard.setActiveNav(ni); }
            });
            p.add(qBtn);
            qx += 195 + 14;
        }

        // Info card
        JPanel infoCard = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(MainDashboard.C_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),16,16);
                GradientPaint gp=new GradientPaint(0,0,new Color(99,102,241,35),getWidth(),0,new Color(139,92,246,12));
                g2.setPaint(gp);
                g2.fillRoundRect(0,0,getWidth(),4,16,16);
                g2.setColor(MainDashboard.C_BORDER);
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,16,16);
                g2.dispose();
            }
        };
        infoCard.setOpaque(false);
        infoCard.setBounds(32, 398, 840, 140);

        JLabel infoTitle = new JLabel("Informasi Sistem");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        infoTitle.setForeground(MainDashboard.C_ACCENT);
        infoTitle.setBounds(20, 18, 300, 22);
        infoCard.add(infoTitle);

        String[] infos = {
            "• Gunakan sidebar kiri untuk navigasi antar form.",
            "• Klik baris pada tabel untuk memuat data ke form.",
            "• Tombol Save, Update, Delete memerlukan data yang valid.",
            "• Pastikan koneksi database aktif sebelum menggunakan aplikasi."
        };
        int iy = 46;
        for (String info : infos) {
            JLabel il = new JLabel(info);
            il.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            il.setForeground(MainDashboard.C_TEXT_MUTED);
            il.setBounds(20, iy, 800, 20);
            infoCard.add(il);
            iy += 22;
        }
        p.add(infoCard);

        return p;
    }

    private JPanel buildStatCard(String icon, String label, String table, Color color) {
        JPanel card = new JPanel(null) {
            private int count = -1;
            {
                new Thread(() -> {
                    try {
                        java.sql.Connection conn = Koneksi.Koneksi.getKoneksi();
                        java.sql.ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM " + table);
                        if (rs.next()) count = rs.getInt(1);
                    } catch (Exception ignored) { count = 0; }
                    repaint();
                }).start();
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(MainDashboard.C_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),16,16);
                GradientPaint gp=new GradientPaint(0,0,new Color(color.getRed(),color.getGreen(),color.getBlue(),45),
                        getWidth(),getHeight(),new Color(color.getRed(),color.getGreen(),color.getBlue(),8));
                g2.setPaint(gp);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),16,16);
                g2.setColor(color);
                g2.fillRoundRect(0,0,getWidth(),3,16,16);
                // icon circle
                g2.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),65));
                g2.fillOval(14,14,38,38);
                g2.setFont(new Font("Segoe UI",Font.BOLD,14));
                FontMetrics fm=g2.getFontMetrics();
                g2.setColor(color);
                g2.drawString(icon,14+(38-fm.stringWidth(icon))/2,14+fm.getAscent()+(38-fm.getHeight())/2);
                // count
                g2.setFont(new Font("Segoe UI",Font.BOLD,30));
                g2.setColor(MainDashboard.C_TEXT);
                String cnt = count >= 0 ? String.valueOf(count) : "--";
                g2.drawString(cnt,18,82);
                // label
                g2.setFont(new Font("Segoe UI",Font.PLAIN,11));
                g2.setColor(MainDashboard.C_TEXT_MUTED);
                g2.drawString(label,18,100);
                g2.setColor(MainDashboard.C_BORDER);
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,16,16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return card;
    }

    private JPanel buildQuickBtn(String icon, String label, Color color) {
        JPanel p = new JPanel(null) {
            private boolean hover=false;
            {addMouseListener(new MouseAdapter(){
                @Override public void mouseEntered(MouseEvent e){hover=true;repaint();setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));}
                @Override public void mouseExited(MouseEvent e){hover=false;repaint();}
            });}
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = hover ? new Color(color.getRed(),color.getGreen(),color.getBlue(),45)
                                 : MainDashboard.C_CARD;
                g2.setColor(bg);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                if(hover){
                    GradientPaint gp=new GradientPaint(0,0,new Color(color.getRed(),color.getGreen(),color.getBlue(),60),
                            getWidth(),getHeight(),new Color(0,0,0,0));
                    g2.setPaint(gp);
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                }
                g2.setColor(hover?color:MainDashboard.C_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                g2.setFont(new Font("Segoe UI",Font.BOLD,20));
                FontMetrics fm=g2.getFontMetrics();
                g2.setColor(color);
                g2.drawString(icon,(getWidth()-fm.stringWidth(icon))/2,36);
                g2.setFont(new Font("Segoe UI",Font.BOLD,11));
                fm=g2.getFontMetrics();
                g2.setColor(hover?MainDashboard.C_TEXT:MainDashboard.C_TEXT_MUTED);
                g2.drawString(label,(getWidth()-fm.stringWidth(label))/2,60);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    private void styleScrollBar(JScrollPane sp) {
        sp.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors(){
                thumbColor = new Color(60,65,100);
                trackColor = MainDashboard.C_SIDEBAR;
            }
            @Override protected JButton createDecreaseButton(int o){return zeroBtn();}
            @Override protected JButton createIncreaseButton(int o){return zeroBtn();}
            private JButton zeroBtn(){JButton b=new JButton();b.setPreferredSize(new Dimension(0,0));return b;}
        });
    }
}