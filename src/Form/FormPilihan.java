package form;

import javax.swing.*;
import java.awt.*;

public class FormPilihan extends javax.swing.JFrame {

    public FormPilihan() {
        initComponents();
        setAppIcon();
        setLocationRelativeTo(null);
    }

    private void setAppIcon() {
        try {
            java.net.URL imgURL = getClass().getResource("/foto/icon.png");
            if (imgURL != null) setIconImage(new ImageIcon(imgURL).getImage());
        } catch (Exception e) {
            System.err.println("Gagal memuat icon: " + e.getMessage());
        }
    }

    // =====================================================================
    // HELPER — load PNG dari resource, scale ke ukuran yang diinginkan
    // =====================================================================

    private ImageIcon loadIcon(String resourcePath, int width, int height) {
        try {
            java.net.URL imgURL = getClass().getResource(resourcePath);
            if (imgURL != null) {
                ImageIcon raw = new ImageIcon(imgURL);
                Image scaled  = raw.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat icon tombol: " + e.getMessage());
        }
        return null;
    }

    // =====================================================================
    // BUAT TOMBOL — ikon PNG di atas teks, tampilan default Swing
    // =====================================================================
    private JButton buatTombol(String teks, String iconPath) {
        ImageIcon icon = loadIcon(iconPath, 40, 40);
        JButton btn = (icon != null) ? new JButton(teks, icon) : new JButton(teks);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setFont(new Font("Tahoma", Font.PLAIN, 12));
        btn.setPreferredSize(new Dimension(160, 90));
        return btn;
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Menu Utama");
        setResizable(false);

        // Judul
        JLabel jLabel1 = new JLabel("SISTEM PENGGAJIAN KARYAWAN");
        jLabel1.setFont(new Font("Tahoma", Font.BOLD, 18));
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);

        // Tombol — semua menggunakan icon.png, scale 40x40
        btnKaryawan   = buatTombol("Form Karyawan",   "/foto/user.png");
        btnGolongan   = buatTombol("Form Golongan",   "/foto/document.png");
        btnLembur     = buatTombol("Form Lembur",     "/foto/time.png");
        btnPenggajian = buatTombol("Form Penggajian", "/foto/money.png");
        btnExit       = buatTombol("Exit",             "/foto/exit.png");

        // Action listeners
        btnKaryawan.addActionListener(e   -> new FormKaryawan().setVisible(true));
        btnGolongan.addActionListener(e   -> new FormGolongan().setVisible(true));
        btnLembur.addActionListener(e     -> new FormLembur().setVisible(true));
        btnPenggajian.addActionListener(e -> new FormPenggajian().setVisible(true));
        btnExit.addActionListener(e       -> System.exit(0));

        // ---- LAYOUT ----
        JPanel panelUtama = new JPanel(new BorderLayout(0, 20));
        panelUtama.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Grid 2x2
        JPanel panelGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        panelGrid.add(btnKaryawan);
        panelGrid.add(btnGolongan);
        panelGrid.add(btnLembur);
        panelGrid.add(btnPenggajian);

        // Exit rata tengah bawah
        JPanel panelExit = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelExit.add(btnExit);

        panelUtama.add(jLabel1,   BorderLayout.NORTH);
        panelUtama.add(panelGrid, BorderLayout.CENTER);
        panelUtama.add(panelExit, BorderLayout.SOUTH);

        setContentPane(panelUtama);
        pack();
    }

    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnGolongan;
    private javax.swing.JButton btnKaryawan;
    private javax.swing.JButton btnLembur;
    private javax.swing.JButton btnPenggajian;
}