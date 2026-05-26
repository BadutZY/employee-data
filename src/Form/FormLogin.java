package form;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.sql.*;
import javax.swing.Timer;
import Koneksi.Koneksi;

public class FormLogin extends javax.swing.JFrame {

    // =====================================================================
    // MODERN DARK THEME PALETTE
    // =====================================================================
    private static final Color C_BG           = new Color(10, 11, 18);
    private static final Color C_SURFACE      = new Color(16, 18, 30);
    private static final Color C_CARD         = new Color(20, 22, 36);
    private static final Color C_FIELD        = new Color(28, 31, 50);
    private static final Color C_FIELD_FOCUS  = new Color(33, 37, 60);
    private static final Color C_ACCENT       = new Color(99, 102, 241);
    private static final Color C_ACCENT2      = new Color(139, 92, 246);
    private static final Color C_SUCCESS      = new Color(34, 197, 94);
    private static final Color C_DANGER       = new Color(239, 68, 68);
    private static final Color C_TEXT         = new Color(226, 232, 240);
    private static final Color C_TEXT_MUTED   = new Color(100, 116, 139);
    private static final Color C_BORDER       = new Color(38, 42, 64);
    private static final Color C_BORDER_FOCUS = new Color(99, 102, 241);

    // =====================================================================
    // SWAP ANIMATION STATE
    // =====================================================================
    private boolean showingLogin = true;
    private boolean animating    = false;
    private Timer   animTimer;

    // Animation progress: 0.0 = fully in current state, 1.0 = fully in next state
    private float   animProgress = 0f;
    // Direction: +1 means going to register, -1 means going to login
    private int     animDirection = 1;

    // The two half-panels: formPanel (the card side) and infoPanel (the branding side)
    // We animate them by rendering snapshots and cross-fading + translating
    private JPanel  leftHalf;   // left 500px
    private JPanel  rightHalf;  // right 500px
    private JPanel  mainContainer; // full width container that draws the animation

    // Snapshot images used during animation
    private BufferedImage snapLeftFrom, snapLeftTo;
    private BufferedImage snapRightFrom, snapRightTo;
    private boolean snapshotReady = false;

    // Login form components (inside loginFormCard)
    private JPanel      loginFormCard;
    private JTextField     loginUser;
    private JPasswordField loginPass;
    private JLayeredPane   loginPassWrap;
    private JButton        btnLogin;
    private JButton        btnToRegister;
    private JLabel         lblLoginMsg;

    // Register form components (inside registerFormCard)
    private JPanel      registerFormCard;
    private JTextField     regUser;
    private JPasswordField regPass;
    private JPasswordField regPass2;
    private JButton        btnRegister;
    private JButton        btnToLogin;
    private JLabel         lblRegMsg;

    // Info panels (branding)
    private JPanel loginInfoPanel;    // shown on right during login
    private JPanel registerInfoPanel; // shown on left during register

    // Particle animation
    private Timer particleTimer;
    private float[] px = new float[50];
    private float[] py = new float[50];
    private float[] pvx = new float[50];
    private float[] pvy = new float[50];
    private float[] palpha = new float[50];
    private int W_WIN = 1000, H_WIN = 600;
    private int HALF = W_WIN / 2;

    public FormLogin() {
        applyLAF();
        initParticles();
        initComponents();
        setAppIcon();
        setLocationRelativeTo(null);
        startParticleAnimation();
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

    private void initParticles() {
        for (int i = 0; i < px.length; i++) {
            px[i]     = (float)(Math.random() * W_WIN);
            py[i]     = (float)(Math.random() * H_WIN);
            pvx[i]    = (float)(Math.random() * 0.6 - 0.3);
            pvy[i]    = (float)(Math.random() * -0.8 - 0.2);
            palpha[i] = (float)(Math.random() * 0.6 + 0.1);
        }
    }

    private void startParticleAnimation() {
        particleTimer = new Timer(30, e -> {
            for (int i = 0; i < px.length; i++) {
                px[i] += pvx[i]; py[i] += pvy[i];
                if (py[i] < -5)  { py[i] = H_WIN + 5; px[i] = (float)(Math.random() * W_WIN); }
                if (px[i] < -5)  px[i] = W_WIN + 5;
                if (px[i] > W_WIN + 5) px[i] = -5;
            }
            repaint();
        });
        particleTimer.start();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Sistem Penggajian Karyawan - Rizky Maulana Putra");
        setUndecorated(false);
        setResizable(false);

        // ---- ROOT BACKGROUND PANEL ----
        JPanel root = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, C_BG, getWidth(), getHeight(), new Color(12, 10, 28));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 4));
                g2.setStroke(new BasicStroke(1f));
                for (int x = 0; x < getWidth(); x += 40) g2.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 40) g2.drawLine(0, y, getWidth(), y);
                drawGlow(g2, 150, 150, 220, new Color(99, 102, 241, 22));
                drawGlow(g2, getWidth()-150, getHeight()-100, 200, new Color(139, 92, 246, 18));
                for (int i = 0; i < px.length; i++) {
                    g2.setColor(new Color(1f, 1f, 1f, palpha[i] * 0.35f));
                    g2.fillOval((int)px[i], (int)py[i], 3, 3);
                }
                g2.dispose();
            }
            private void drawGlow(Graphics2D g2, int cx, int cy, int r, Color c) {
                for (int i = r; i > 0; i -= 12) {
                    float alpha = (float)(r - i) / r * 0.25f;
                    g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(alpha * 255)));
                    g2.fillOval(cx - i, cy - i, i * 2, i * 2);
                }
            }
        };
        root.setPreferredSize(new Dimension(W_WIN, H_WIN));

        // ---- BUILD FOUR HALF-PANELS ----
        loginFormCard    = buildLoginFormCard();
        registerFormCard = buildRegisterFormCard();
        loginInfoPanel   = buildInfoPanel(HALF, H_WIN, true);
        registerInfoPanel= buildInfoPanel(HALF, H_WIN, false);

        // ---- MAIN CONTAINER: draws the swap animation ----
        mainContainer = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (animating && snapshotReady) {
                    drawSwapAnimation((Graphics2D) g);
                }
            }
        };
        mainContainer.setOpaque(false);
        mainContainer.setBounds(0, 0, W_WIN, H_WIN);

        // ---- LEFT HALF (holds content, swaps between form and info) ----
        leftHalf = new JPanel(null);
        leftHalf.setOpaque(false);
        leftHalf.setBounds(0, 0, HALF, H_WIN);

        // ---- RIGHT HALF ----
        rightHalf = new JPanel(null);
        rightHalf.setOpaque(false);
        rightHalf.setBounds(HALF, 0, HALF, H_WIN);

        // Initial state: Login → form on left, info on right
        setupLoginLayout();

        // Divider
        JPanel divider = makeVerticalDivider(H_WIN);
        divider.setBounds(HALF - 1, 0, 2, H_WIN);

        mainContainer.add(leftHalf);
        mainContainer.add(rightHalf);
        mainContainer.add(divider);
        root.add(mainContainer);

        setContentPane(root);
        pack();
        setMinimumSize(new Dimension(W_WIN, H_WIN));
    }

    // Place login form card centered in the given half panel
    private void placeCardInHalf(JPanel half, JPanel card, int cardW, int cardH) {
        half.removeAll();
        int x = (HALF - cardW) / 2;
        int y = (H_WIN - cardH) / 2;
        card.setBounds(x, y, cardW, cardH);
        half.add(card);
        half.revalidate();
        half.repaint();
    }

    private void placeInfoInHalf(JPanel half, JPanel info) {
        half.removeAll();
        info.setBounds(0, 0, HALF, H_WIN);
        half.add(info);
        half.revalidate();
        half.repaint();
    }

    private void setupLoginLayout() {
        placeCardInHalf(leftHalf, loginFormCard, 380, 430);
        placeInfoInHalf(rightHalf, loginInfoPanel);
    }

    private void setupRegisterLayout() {
        placeInfoInHalf(leftHalf, registerInfoPanel);
        placeCardInHalf(rightHalf, registerFormCard, 380, 480);
    }

    // =====================================================================
    // SWAP ANIMATION — panels swap with a cross-fade + scale effect
    // The form card and info panel elegantly swap positions
    // =====================================================================
    private void swapTo(boolean toLogin) {
        if (animating) return;
        if (animTimer != null && animTimer.isRunning()) animTimer.stop();

        animating = true;
        animProgress = 0f;
        animDirection = toLogin ? -1 : 1;

        // Take snapshots of current state
        snapLeftFrom  = capturePanel(leftHalf,  HALF, H_WIN);
        snapRightFrom = capturePanel(rightHalf, HALF, H_WIN);

        // Prepare the target state (render off-screen)
        JPanel tempLeft  = new JPanel(null); tempLeft.setOpaque(false);  tempLeft.setSize(HALF, H_WIN);
        JPanel tempRight = new JPanel(null); tempRight.setOpaque(false); tempRight.setSize(HALF, H_WIN);

        if (toLogin) {
            JPanel fc = buildLoginFormCardSnapshot();
            JPanel ic = buildInfoPanelSnapshot(HALF, H_WIN, true);
            int cx = (HALF - 380) / 2, cy = (H_WIN - 430) / 2;
            fc.setBounds(cx, cy, 380, 430); tempLeft.add(fc);
            ic.setBounds(0, 0, HALF, H_WIN); tempRight.add(ic);
        } else {
            JPanel ic = buildInfoPanelSnapshot(HALF, H_WIN, false);
            JPanel fc = buildRegisterFormCardSnapshot();
            ic.setBounds(0, 0, HALF, H_WIN); tempLeft.add(ic);
            int cx = (HALF - 380) / 2, cy = (H_WIN - 480) / 2;
            fc.setBounds(cx, cy, 380, 480); tempRight.add(fc);
        }

        // Force layout and paint to get snapshot
        tempLeft.doLayout();  tempRight.doLayout();
        snapLeftTo  = capturePanel(tempLeft,  HALF, H_WIN);
        snapRightTo = capturePanel(tempRight, HALF, H_WIN);
        snapshotReady = true;

        // Hide real panels during animation
        leftHalf.setVisible(false);
        rightHalf.setVisible(false);

        final float DURATION = 45f; // ticks at 16ms = ~720ms total
        animTimer = new Timer(16, null);
        animTimer.addActionListener(e -> {
            animProgress += 1f / DURATION;
            if (animProgress >= 1f) {
                animProgress = 1f;
                animTimer.stop();
                animating = false;
                snapshotReady = false;
                leftHalf.setVisible(true);
                rightHalf.setVisible(true);
                // Set final layout
                if (toLogin) {
                    showingLogin = true;
                    setupLoginLayout();
                } else {
                    showingLogin = false;
                    setupRegisterLayout();
                }
                mainContainer.repaint();
            } else {
                mainContainer.repaint();
            }
        });
        animTimer.start();
    }

    /**
     * Draws the swap animation using eased cross-dissolve + directional scale.
     *
     * The LEFT half fades from "fromLeft" to "toLeft" with a slight horizontal shift.
     * The RIGHT half fades from "fromRight" to "toRight" with opposite shift.
     * This creates the illusion of the panels swapping places beautifully.
     */
    private void drawSwapAnimation(Graphics2D g) {
        // Ease in-out cubic
        float t = easeInOutCubic(animProgress);

        // Phase 1 (0→0.5): form card moves toward center (shrinks/fades out)
        // Phase 2 (0.5→1): new content expands from center (grows/fades in)

        // --- LEFT HALF ---
        Graphics2D gL = (Graphics2D) g.create(0, 0, HALF, H_WIN);
        gL.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gL.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // FROM: fade out + slide out to center (move right by t * HALF/2)
        float alphaFrom = Math.max(0f, 1f - t * 2f);
        int   shiftFrom = (int)(t * HALF / 3f * animDirection);

        if (snapLeftFrom != null && alphaFrom > 0) {
            gL.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaFrom));
            gL.drawImage(snapLeftFrom, shiftFrom, 0, null);
        }

        // TO: fade in + slide in from center
        float alphaTo = Math.max(0f, (t - 0.5f) * 2f);
        int   shiftTo = (int)((1f - t) * HALF / 3f * -animDirection);

        if (snapLeftTo != null && alphaTo > 0) {
            gL.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaTo));
            gL.drawImage(snapLeftTo, shiftTo, 0, null);
        }
        gL.dispose();

        // --- RIGHT HALF ---
        Graphics2D gR = (Graphics2D) g.create(HALF, 0, HALF, H_WIN);
        gR.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gR.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // FROM: fade out + slide out (opposite direction)
        int shiftFromR = (int)(t * HALF / 3f * -animDirection);
        if (snapRightFrom != null && alphaFrom > 0) {
            gR.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaFrom));
            gR.drawImage(snapRightFrom, shiftFromR, 0, null);
        }

        // TO: fade in + slide in from opposite side
        int shiftToR = (int)((1f - t) * HALF / 3f * animDirection);
        if (snapRightTo != null && alphaTo > 0) {
            gR.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaTo));
            gR.drawImage(snapRightTo, shiftToR, 0, null);
        }
        gR.dispose();

        // --- DIVIDER (always visible) ---
        Graphics2D gD = (Graphics2D) g.create(HALF - 1, 0, 2, H_WIN);
        float divAlpha = Math.abs(t - 0.5f) < 0.15f ? 0.2f : 0.7f; // dim during mid-swap
        gD.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, divAlpha));
        GradientPaint divGp = new GradientPaint(0, 0, new Color(99,102,241,0), 0, H_WIN/2, new Color(99,102,241,100));
        gD.setPaint(divGp); gD.fillRect(0, 0, 2, H_WIN/2);
        GradientPaint divGp2 = new GradientPaint(0, H_WIN/2, new Color(99,102,241,100), 0, H_WIN, new Color(99,102,241,0));
        gD.setPaint(divGp2); gD.fillRect(0, H_WIN/2, 2, H_WIN/2);
        gD.dispose();
    }

    private float easeInOutCubic(float t) {
        if (t < 0.5f) return 4 * t * t * t;
        float f = 2 * t - 2;
        return 1 + f * f * f / 2;
    }

    private BufferedImage capturePanel(JPanel panel, int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        panel.paint(g);
        g.dispose();
        return img;
    }

    // =====================================================================
    // LOGIN FORM CARD
    // =====================================================================
    private JPanel buildLoginFormCard() {
        int cardW = 380, cardH = 430;
        JPanel card = makeCard(cardW, cardH);

        JLabel title = makeLabel("Selamat Datang", new Font("Segoe UI", Font.BOLD, 24), C_TEXT, SwingConstants.CENTER);
        JLabel sub   = makeLabel("Masuk ke akun Anda", new Font("Segoe UI", Font.PLAIN, 13), C_TEXT_MUTED, SwingConstants.CENTER);
        title.setBounds(0, 30, cardW, 32);
        sub.setBounds(0, 66, cardW, 22);

        JPanel bar = makeAccentBar(C_ACCENT, C_ACCENT2);
        bar.setBounds(cardW/2 - 35, 100, 70, 3);

        JLabel uLabel = makeLabel("Username", new Font("Segoe UI", Font.PLAIN, 12), C_TEXT_MUTED, SwingConstants.LEFT);
        uLabel.setBounds(32, 118, 316, 18);
        loginUser = buildTextField("Masukkan username");
        loginUser.setBounds(32, 138, 316, 40);

        JLabel pLabel = makeLabel("Password", new Font("Segoe UI", Font.PLAIN, 12), C_TEXT_MUTED, SwingConstants.LEFT);
        pLabel.setBounds(32, 190, 316, 18);
        loginPass = buildPasswordField("Masukkan password");
        loginPassWrap = wrapPasswordField(loginPass, 32, 210, 316, 40);

        lblLoginMsg = makeLabel("", new Font("Segoe UI", Font.PLAIN, 11), C_DANGER, SwingConstants.CENTER);
        lblLoginMsg.setBounds(32, 260, 316, 18);

        btnLogin = buildGradientButton("Masuk", C_ACCENT, C_ACCENT2);
        btnLogin.setBounds(32, 282, 316, 44);

        btnToRegister = buildLinkButton("Belum punya akun? Daftar sekarang →");
        btnToRegister.setBounds(32, 360, 316, 28);

        card.add(title); card.add(sub); card.add(bar);
        card.add(uLabel); card.add(loginUser);
        card.add(pLabel); card.add(loginPassWrap);
        card.add(lblLoginMsg); card.add(btnLogin);
        card.add(btnToRegister);

        btnLogin.addActionListener(e -> doLogin());
        loginPass.addActionListener(e -> doLogin());
        btnToRegister.addActionListener(e -> swapTo(false));

        return card;
    }

    // Snapshot version (static, no event listeners — used for animation capture)
    private JPanel buildLoginFormCardSnapshot() {
        int cardW = 380, cardH = 430;
        JPanel card = makeCard(cardW, cardH);
        JLabel title = makeLabel("Selamat Datang", new Font("Segoe UI", Font.BOLD, 24), C_TEXT, SwingConstants.CENTER);
        JLabel sub   = makeLabel("Masuk ke akun Anda", new Font("Segoe UI", Font.PLAIN, 13), C_TEXT_MUTED, SwingConstants.CENTER);
        title.setBounds(0, 30, cardW, 32); sub.setBounds(0, 66, cardW, 22);
        JPanel bar = makeAccentBar(C_ACCENT, C_ACCENT2); bar.setBounds(cardW/2-35, 100, 70, 3);
        JLabel uL = makeLabel("Username", new Font("Segoe UI", Font.PLAIN,12), C_TEXT_MUTED, SwingConstants.LEFT); uL.setBounds(32,118,316,18);
        JTextField tu = new JTextField(loginUser.getText()); styleSnapField(tu); tu.setBounds(32,138,316,40);
        JLabel pL = makeLabel("Password", new Font("Segoe UI", Font.PLAIN,12), C_TEXT_MUTED, SwingConstants.LEFT); pL.setBounds(32,190,316,18);
        JPasswordField tp = new JPasswordField(); styleSnapField(tp); 
        tp.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 36));
        tp.setBounds(32,210,316,40);
        JLabel msg = makeLabel("", new Font("Segoe UI",Font.PLAIN,11), C_DANGER, SwingConstants.CENTER); msg.setBounds(32,260,316,18);
        JButton bL = buildGradientButton("Masuk", C_ACCENT, C_ACCENT2); bL.setBounds(32,282,316,44);
        JButton bR = buildLinkButton("Belum punya akun? Daftar sekarang →"); bR.setBounds(32,360,316,28);
        card.add(title); card.add(sub); card.add(bar); card.add(uL); card.add(tu);
        card.add(pL); card.add(tp); card.add(msg); card.add(bL); card.add(bR);
        return card;
    }

    // =====================================================================
    // REGISTER FORM CARD
    // =====================================================================
    private JPanel buildRegisterFormCard() {
        int cardW = 380, cardH = 480;
        JPanel card = makeCard(cardW, cardH);

        JLabel title = makeLabel("Buat Akun Baru", new Font("Segoe UI", Font.BOLD, 24), C_TEXT, SwingConstants.CENTER);
        JLabel sub   = makeLabel("Isi data untuk mendaftar", new Font("Segoe UI", Font.PLAIN, 13), C_TEXT_MUTED, SwingConstants.CENTER);
        title.setBounds(0, 28, cardW, 32);
        sub.setBounds(0, 64, cardW, 22);

        JPanel bar = makeAccentBar(C_ACCENT2, C_SUCCESS);
        bar.setBounds(cardW/2 - 35, 98, 70, 3);

        JLabel uLabel = makeLabel("Username", new Font("Segoe UI", Font.PLAIN, 12), C_TEXT_MUTED, SwingConstants.LEFT);
        uLabel.setBounds(32, 114, 316, 18);
        regUser = buildTextField("Buat username baru");
        regUser.setBounds(32, 134, 316, 40);

        JLabel pLabel = makeLabel("Password", new Font("Segoe UI", Font.PLAIN, 12), C_TEXT_MUTED, SwingConstants.LEFT);
        pLabel.setBounds(32, 186, 316, 18);
        regPass = buildPasswordField("Buat password");
        JLayeredPane regPassWrap = wrapPasswordField(regPass, 32, 206, 316, 40);

        JLabel p2Label = makeLabel("Konfirmasi Password", new Font("Segoe UI", Font.PLAIN, 12), C_TEXT_MUTED, SwingConstants.LEFT);
        p2Label.setBounds(32, 258, 316, 18);
        regPass2 = buildPasswordField("Ulangi password");
        JLayeredPane regPass2Wrap = wrapPasswordField(regPass2, 32, 278, 316, 40);

        lblRegMsg = makeLabel("", new Font("Segoe UI", Font.PLAIN, 11), C_DANGER, SwingConstants.CENTER);
        lblRegMsg.setBounds(32, 330, 316, 18);

        btnRegister = buildGradientButton("Daftar Sekarang", C_ACCENT2, C_SUCCESS);
        btnRegister.setBounds(32, 352, 316, 44);

        btnToLogin = buildLinkButton("← Sudah punya akun? Masuk");
        btnToLogin.setBounds(32, 410, 316, 28);

        card.add(title); card.add(sub); card.add(bar);
        card.add(uLabel); card.add(regUser);
        card.add(pLabel); card.add(regPassWrap);
        card.add(p2Label); card.add(regPass2Wrap);
        card.add(lblRegMsg); card.add(btnRegister);
        card.add(btnToLogin);

        btnRegister.addActionListener(e -> doRegister());
        btnToLogin.addActionListener(e -> swapTo(true));

        return card;
    }

    private JPanel buildRegisterFormCardSnapshot() {
        int cardW = 380, cardH = 480;
        JPanel card = makeCard(cardW, cardH);
        JLabel title = makeLabel("Buat Akun Baru", new Font("Segoe UI", Font.BOLD, 24), C_TEXT, SwingConstants.CENTER);
        JLabel sub   = makeLabel("Isi data untuk mendaftar", new Font("Segoe UI", Font.PLAIN, 13), C_TEXT_MUTED, SwingConstants.CENTER);
        title.setBounds(0, 28, cardW, 32); sub.setBounds(0, 64, cardW, 22);
        JPanel bar = makeAccentBar(C_ACCENT2, C_SUCCESS); bar.setBounds(cardW/2-35, 98, 70, 3);
        JLabel uL = makeLabel("Username", new Font("Segoe UI", Font.PLAIN,12), C_TEXT_MUTED, SwingConstants.LEFT); uL.setBounds(32,114,316,18);
        JTextField tu = buildTextField("Buat username baru"); tu.setBounds(32,134,316,40);
        JLabel pL = makeLabel("Password", new Font("Segoe UI", Font.PLAIN,12), C_TEXT_MUTED, SwingConstants.LEFT); pL.setBounds(32,186,316,18);
        JPasswordField tp = buildPasswordField("Buat password"); tp.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 36)); tp.setBounds(32,206,316,40);
        JLabel p2L = makeLabel("Konfirmasi Password", new Font("Segoe UI", Font.PLAIN,12), C_TEXT_MUTED, SwingConstants.LEFT); p2L.setBounds(32,258,316,18);
        JPasswordField tp2 = buildPasswordField("Ulangi password"); tp2.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 36)); tp2.setBounds(32,278,316,40);
        JLabel msg = makeLabel("", new Font("Segoe UI",Font.PLAIN,11), C_DANGER, SwingConstants.CENTER); msg.setBounds(32,330,316,18);
        JButton bR = buildGradientButton("Daftar Sekarang", C_ACCENT2, C_SUCCESS); bR.setBounds(32,352,316,44);
        JButton bL = buildLinkButton("← Sudah punya akun? Masuk"); bL.setBounds(32,410,316,28);
        card.add(title); card.add(sub); card.add(bar); card.add(uL); card.add(tu);
        card.add(pL); card.add(tp); card.add(p2L); card.add(tp2); card.add(msg); card.add(bR); card.add(bL);
        return card;
    }

    private void styleSnapField(JComponent f) {
        f.setBackground(C_FIELD); f.setForeground(C_TEXT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        f.setOpaque(true);
    }

    // =====================================================================
    // INFO / BRANDING PANEL
    // =====================================================================
    private JPanel buildInfoPanel(int W, int H, boolean forLogin) {
        JPanel p = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = forLogin
                    ? new GradientPaint(0, 0, new Color(99, 102, 241, 35), W, H, new Color(139, 92, 246, 15))
                    : new GradientPaint(0, 0, new Color(139, 92, 246, 30), W, H, new Color(34, 197, 94, 15));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 6));
                g2.fillOval(-80, -80, 300, 300);
                g2.fillOval(W - 160, H - 160, 300, 300);
                g2.dispose();
            }
        };
        p.setOpaque(false);

        JLabel lblLogo = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth()/2, cy = getHeight()/2, r = 42;
                GradientPaint gp = new GradientPaint(cx-r, cy-r, C_ACCENT, cx+r, cy+r, C_ACCENT2);
                g2.setPaint(gp);
                int[] xp = new int[6], yp = new int[6];
                for (int i = 0; i < 6; i++) {
                    xp[i] = (int)(cx + r * Math.cos(Math.PI/6 + Math.PI/3 * i));
                    yp[i] = (int)(cy + r * Math.sin(Math.PI/6 + Math.PI/3 * i));
                }
                g2.fillPolygon(xp, yp, 6);
                g2.setColor(new Color(255,255,255,40)); g2.fillPolygon(xp, yp, 6);
                g2.setColor(new Color(255,255,255,200));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("P", cx - fm.stringWidth("P")/2, cy + fm.getAscent()/2 - 2);
                g2.dispose();
            }
        };
        lblLogo.setBounds(0, H/2 - 200, W, 100);

        JLabel lblTitle  = makeLabel("PAYROLL", new Font("Segoe UI", Font.BOLD, 34), C_TEXT, SwingConstants.CENTER);
        JLabel lblTitle2 = makeLabel("SYSTEM", new Font("Segoe UI", Font.BOLD, 34), C_ACCENT, SwingConstants.CENTER);
        JLabel lblSub    = makeLabel("Sistem Penggajian Karyawan", new Font("Segoe UI", Font.PLAIN, 14), C_TEXT_MUTED, SwingConstants.CENTER);

        int cy = H/2 - 100;
        lblTitle.setBounds(0, cy, W, 40);
        lblTitle2.setBounds(0, cy + 42, W, 40);
        lblSub.setBounds(0, cy + 90, W, 24);

        String[] features = forLogin
            ? new String[]{ "-  Manajemen Data Karyawan", "- Pengelolaan Golongan & Jabatan",
                            "-  Pencatatan Data Lembur", "- Proses Penggajian Otomatis" }
            : new String[]{ "-  Data Aman & Terenkripsi", "- Akses Kapan Saja",
                            "-  Laporan Lengkap & Akurat", "- Mudah Digunakan" };

        int fy = cy + 130;
        for (String f : features) {
            JLabel fl = makeLabel(f, new Font("Segoe UI", Font.PLAIN, 13), new Color(170, 178, 210), SwingConstants.CENTER);
            fl.setBounds(20, fy, W - 40, 24);
            p.add(fl);
            fy += 30;
        }

        p.add(lblLogo); p.add(lblTitle); p.add(lblTitle2); p.add(lblSub);
        return p;
    }

    // Static snapshot version (same visuals, no interaction needed)
    private JPanel buildInfoPanelSnapshot(int W, int H, boolean forLogin) {
        return buildInfoPanel(W, H, forLogin);
    }

    // =====================================================================
    // BUSINESS LOGIC
    // =====================================================================
    private void doLogin() {
        String user = loginUser.getText().trim();
        String pass = new String(loginPass.getPassword()).trim();
        if (user.isEmpty() || pass.isEmpty()) {
            showMsg(lblLoginMsg, "Username dan password harus diisi!", C_DANGER); return;
        }
        try {
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM tb_user WHERE username=? AND password=?");
            ps.setString(1, user); ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                showMsg(lblLoginMsg, "Login berhasil!", C_SUCCESS);
                Timer delay = new Timer(700, ev -> {
                    if (particleTimer != null) particleTimer.stop();
                    new MainDashboard(user).setVisible(true);
                    dispose();
                });
                delay.setRepeats(false); delay.start();
            } else {
                showMsg(lblLoginMsg, "Username atau password salah.", C_DANGER);
                shakeField(loginUser); shakeField(loginPassWrap);
            }
        } catch (SQLException e) { showMsg(lblLoginMsg, "Koneksi DB: " + e.getMessage(), C_DANGER); }
    }

    private void doRegister() {
        String user  = regUser.getText().trim();
        String pass  = new String(regPass.getPassword()).trim();
        String pass2 = new String(regPass2.getPassword()).trim();
        if (user.isEmpty() || pass.isEmpty()) { showMsg(lblRegMsg, "Semua kolom harus diisi!", C_DANGER); return; }
        if (!pass.equals(pass2)) { showMsg(lblRegMsg, "Password tidak cocok!", C_DANGER); return; }
        if (pass.length() < 4)  { showMsg(lblRegMsg, "Password minimal 4 karakter.", C_DANGER); return; }
        try {
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement chk = conn.prepareStatement("SELECT username FROM tb_user WHERE username=?");
            chk.setString(1, user);
            if (chk.executeQuery().next()) { showMsg(lblRegMsg, "Username sudah dipakai!", C_DANGER); return; }
            PreparedStatement ps = conn.prepareStatement("INSERT INTO tb_user (username, password) VALUES (?,?)");
            ps.setString(1, user); ps.setString(2, pass); ps.executeUpdate();
            showMsg(lblRegMsg, "Akun berhasil dibuat! Silakan login.", C_SUCCESS);
            Timer delay = new Timer(1200, ev -> swapTo(true));
            delay.setRepeats(false); delay.start();
        } catch (SQLException e) { showMsg(lblRegMsg, "Error: " + e.getMessage(), C_DANGER); }
    }

    private void showMsg(JLabel lbl, String text, Color color) {
        lbl.setText(text); lbl.setForeground(color); lbl.repaint();
    }

    private void shakeField(JComponent comp) {
        int origX = comp.getX();
        Timer t = new Timer(30, null);
        int[] step = {0};
        int[] offsets = {-6, 6, -5, 5, -3, 3, -1, 1, 0};
        t.addActionListener(e -> {
            if (step[0] >= offsets.length) { comp.setLocation(origX, comp.getY()); t.stop(); return; }
            comp.setLocation(origX + offsets[step[0]++], comp.getY());
        });
        t.start();
    }

    // =====================================================================
    // COMPONENT BUILDERS
    // =====================================================================
    private JPanel makeCard(int w, int h) {
        return new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(C_BORDER); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 22, 22);
                g2.dispose();
            }
        };
    }

    private JPanel makeAccentBar(Color c1, Color c2) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), 0, c2);
                g2.setPaint(gp); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.dispose();
            }
        };
    }

    private JPanel makeVerticalDivider(int h) {
        JPanel d = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(99,102,241,0), 0, h/2, new Color(99,102,241,70)));
                g2.fillRect(0, 0, 2, h/2);
                g2.setPaint(new GradientPaint(0, h/2, new Color(99,102,241,70), 0, h, new Color(99,102,241,0)));
                g2.fillRect(0, h/2, 2, h/2);
                g2.dispose();
            }
        };
        d.setOpaque(false);
        return d;
    }

    private JLabel makeLabel(String text, Font font, Color fg, int align) {
        JLabel l = new JLabel(text, align);
        l.setFont(font); l.setForeground(fg); l.setOpaque(false);
        return l;
    }

    private JTextField buildTextField(String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(C_TEXT_MUTED); g2.setFont(getFont());
                    g2.drawString(placeholder, 12, getHeight()/2 + g2.getFontMetrics().getAscent()/2 - 2);
                    g2.dispose();
                }
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hasFocus() ? C_BORDER_FOCUS : C_BORDER);
                g2.setStroke(new BasicStroke(hasFocus() ? 1.5f : 1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
            }
        };
        f.setBackground(C_FIELD); f.setForeground(C_TEXT); f.setCaretColor(C_ACCENT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12)); f.setOpaque(true);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { f.setBackground(C_FIELD_FOCUS); f.repaint(); }
            @Override public void focusLost(FocusEvent e)   { f.setBackground(C_FIELD); f.repaint(); }
        });
        return f;
    }

    private JPasswordField buildPasswordField(String placeholder) {
        JPasswordField f = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(C_TEXT_MUTED); g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    g2.drawString(placeholder, 12, getHeight()/2 + g2.getFontMetrics().getAscent()/2 - 2);
                    g2.dispose();
                }
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hasFocus() ? C_BORDER_FOCUS : C_BORDER);
                g2.setStroke(new BasicStroke(hasFocus() ? 1.5f : 1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
            }
        };
        f.setBackground(C_FIELD); f.setForeground(C_TEXT); f.setCaretColor(C_ACCENT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12)); f.setOpaque(true);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { f.setBackground(C_FIELD_FOCUS); f.repaint(); }
            @Override public void focusLost(FocusEvent e)   { f.setBackground(C_FIELD); f.repaint(); }
        });
        return f;
    }

    // =====================================================================
    // SHOW PASSWORD TOGGLE — eye icon button (drawn via Graphics2D, no assets)
    // =====================================================================
    private JButton buildEyeButton(JPasswordField passField) {
        JButton eye = new JButton() {
            private boolean hover = false;
            private boolean showing = false;

            {
                setOpaque(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setToolTipText("Tampilkan / Sembunyikan Password");

                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) {
                        showing = !showing;
                        if (showing) {
                            passField.setEchoChar('\0');
                        } else {
                            passField.setEchoChar('\u2022');
                        }
                        passField.repaint();
                        repaint();
                    }
                });
            }

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                int w = getWidth(), h = getHeight();
                int cx = w / 2, cy = h / 2;

                // Hover background circle
                if (hover) {
                    g2.setColor(new Color(99, 102, 241, 30));
                    g2.fillOval(cx - 12, cy - 12, 24, 24);
                }

                Color iconColor = hover ? C_ACCENT : new Color(100, 116, 139, 200);
                g2.setColor(iconColor);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Draw outer eye outline (almond shape using quadratic curves)
                int ew = 18, eh = 11;
                int ex = cx - ew/2, ey = cy - eh/2;

                GeneralPath eyeShape = new GeneralPath();
                eyeShape.moveTo(ex, cy);
                eyeShape.quadTo(cx, ey - 3, ex + ew, cy);
                eyeShape.quadTo(cx, ey + eh + 3, ex, cy);
                g2.draw(eyeShape);

                if (!showing) {
                    // OPEN EYE: draw iris and pupil
                    g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawOval(cx - 3, cy - 3, 6, 6);
                    g2.fillOval(cx - 1, cy - 1, 3, 3);
                } else {
                    // CLOSED EYE: draw slash line through the eye
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(cx - 8, cy + 5, cx + 8, cy - 5);
                    // Tiny eyelash-like notch lines for polish
                    g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(cx - 5, cy + 7, cx - 4, cy + 4);
                    g2.drawLine(cx,     cy + 7, cx,     cy + 4);
                    g2.drawLine(cx + 5, cy + 5, cx + 4, cy + 2);
                }

                g2.dispose();
            }
        };
        return eye;
    }

    /**
     * Wraps a JPasswordField inside a JLayeredPane so the eye-toggle button
     * overlays the right side of the field — matching the dark-theme style.
     * Returns a JLayeredPane sized to match the original field's bounds.
     */
    private JLayeredPane wrapPasswordField(JPasswordField passField, int x, int y, int w, int h) {
        JLayeredPane wrap = new JLayeredPane();
        wrap.setBounds(x, y, w, h);
        wrap.setOpaque(false);

        // Field fills the wrapper but with right padding for the eye button
        passField.setBounds(0, 0, w, h);
        // Add extra right padding so text doesn't go under the button
        passField.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 36));

        JButton eyeBtn = buildEyeButton(passField);
        eyeBtn.setBounds(w - 36, 0, 36, h);

        wrap.add(passField, JLayeredPane.DEFAULT_LAYER);
        wrap.add(eyeBtn,    JLayeredPane.PALETTE_LAYER);

        return wrap;
    }

    private JButton buildGradientButton(String text, Color c1, Color c2) {
        JButton b = new JButton(text) {
            private boolean hover = false;
            { addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color a = hover ? c1.brighter() : c1, b2 = hover ? c2.brighter() : c2;
                GradientPaint gp = new GradientPaint(0, 0, a, getWidth(), 0, b2);
                g2.setPaint(gp); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (hover) { g2.setColor(new Color(255,255,255,25)); g2.fillRoundRect(0, 0, getWidth(), getHeight()/2, 12, 12); }
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 14)); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); b.setOpaque(false);
        return b;
    }

    private JButton buildLinkButton(String text) {
        JButton b = new JButton(text) {
            private boolean hover = false;
            { addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? C_ACCENT.brighter() : C_ACCENT); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()-fm.stringWidth(getText()))/2;
                int y = (getHeight()+fm.getAscent()-fm.getDescent())/2;
                g2.drawString(getText(), x, y);
                if (hover) { g2.setStroke(new BasicStroke(1f)); g2.drawLine(x, y+2, x+fm.stringWidth(getText()), y+2); }
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); b.setOpaque(false);
        return b;
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FormLogin().setVisible(true));
    }
}