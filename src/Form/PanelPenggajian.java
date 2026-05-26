package form;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.Locale;
import Koneksi.Koneksi;
import com.toedter.calendar.JDateChooser;

public class PanelPenggajian extends JPanel {

    private DefaultTableModel model;
    private boolean karyawanMenikah = false;

    // Fields
    private JTextField txtIdGaji, txtNamaKaryawan, txtGolongan;
    private JTextField txtGajiPokok, txtTunjanganIstri, txtJumlahAnak;
    private JTextField txtTunjanganAnak, txtTotalTunjanganAnak;
    private JTextField txtTransport, txtUangMakan;
    private JTextField txtJumlahGaji, txtJumlahLembur, txtPotongan, txtTotalGaji;
    private JComboBox<String> cmbKaryawan;
    private JDateChooser dateTanggal, dateTanggalGaji;
    private JTable table;
    private JLabel lblStatus;

    private PanelPenggajian() { super(new BorderLayout()); }

    public static PanelPenggajian create() {
        PanelPenggajian p = new PanelPenggajian();
        p.buildUI();
        return p;
    }

    private void buildUI() {
        setOpaque(false);
        JScrollPane scroll = new JScrollPane(buildContent());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        styleScrollBar(scroll);
        add(scroll, BorderLayout.CENTER);
        loadKaryawan();
        loadData();
        updateFieldStatus(false);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(() -> {
            loadKaryawan();
            loadData();
        });
    }

    public void refresh() {
        loadKaryawan();
        loadData();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(null);
        root.setOpaque(false);

        // Precise layout calculation:
        // Form card: top_pad=24 + 8 rows × 68px + bottom_pad=24 = 592px height
        // All positions computed from that anchor.
        root.setPreferredSize(new Dimension(960, 1144));

        JPanel headerPanel = buildIconHeader("Data Penggajian", "Proses penggajian dan tunjangan karyawan",
                "/foto/money.png", MainDashboard.C_SUCCESS);
        headerPanel.setBounds(20, 14, 920, 76);
        root.add(headerPanel);

        lblStatus = new JLabel("");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setBounds(30, 92, 800, 18);
        root.add(lblStatus);

        // Form card: y=114, h=536 → bottom=650
        // Proof: top_pad=20 + 8rows×(LH18+FH34+GAP10=62) + bottom_pad=20 = 20+496+20 = 536
        JPanel formCard = buildFormCard();
        formCard.setBounds(20, 114, 920, 536);
        root.add(formCard);

        // Total Gaji card: y=660, h=70 → bottom=730
        JPanel totalCard = buildTotalCard();
        totalCard.setBounds(20, 660, 920, 70);
        root.add(totalCard);

        // Buttons: y=740, h=48 → bottom=788
        JPanel btnRow = buildButtonRow();
        btnRow.setBounds(20, 740, 920, 48);
        root.add(btnRow);

        // Table: y=798, h=270 → bottom=1068
        JPanel tblCard = buildTableCard();
        tblCard.setBounds(20, 798, 920, 270);
        root.add(tblCard);
        root.add(tblCard);

        return root;
    }

    private JPanel buildFormCard() {
        JPanel card = makePanelCard();

        // ---------------------------------------------------------------
        // VERIFIED layout — all coordinates explicitly computed:
        //   card width = 920
        //   LH=18 (label), FH=34 (field), GAP=10 → ROW=62px
        //   top_pad=20, bottom_pad=20
        //   8 rows → 20 + 8×62 + 20 = 536px  ← card height in buildContent
        //
        //   Left col:  label/field x=24,  width=400  (24+400=424)
        //   Right col: label/field x=496, width=400  (496+400=896 ≤ 920 ✓)
        //   Gutter = 496-424 = 72px
        // ---------------------------------------------------------------
        final int LH  = 18;
        final int FH  = 34;
        final int GAP = 10;
        final int ROW = LH + FH + GAP; // = 62px
        final int FW  = 400;

        final int C1  = 24;   // left column x
        final int C2  = 496;  // right column x

        // Row y positions (absolute within card)
        final int R1 = 20;
        final int R2 = R1 + ROW; //  82
        final int R3 = R2 + ROW; // 144
        final int R4 = R3 + ROW; // 206
        final int R5 = R4 + ROW; // 268
        final int R6 = R5 + ROW; // 330
        final int R7 = R6 + ROW; // 392
        final int R8 = R7 + ROW; // 454  →  field bottom = 454+18+34 = 506  (card h=536 ✓)

        // ---- LEFT COLUMN ----
        addLbl(card, "ID Gaji", C1, R1);
        txtIdGaji = makeField(true);
        txtIdGaji.setBounds(C1, R1+LH, FW, FH);
        card.add(txtIdGaji);

        addLbl(card, "Tanggal", C1, R2);
        dateTanggal = new JDateChooser(); styleDC(dateTanggal);
        dateTanggal.setBounds(C1, R2+LH, FW, FH);
        card.add(dateTanggal);

        addLbl(card, "ID Karyawan", C1, R3);
        cmbKaryawan = makeCombo();
        cmbKaryawan.setBounds(C1, R3+LH, FW, FH);
        card.add(cmbKaryawan);
        cmbKaryawan.addActionListener(e -> onKaryawanSelected());

        addLbl(card, "Nama Karyawan", C1, R4);
        txtNamaKaryawan = makeField(false);
        txtNamaKaryawan.setBounds(C1, R4+LH, FW, FH);
        card.add(txtNamaKaryawan);

        addLbl(card, "Golongan", C1, R5);
        txtGolongan = makeField(false);
        txtGolongan.setBounds(C1, R5+LH, FW, FH);
        card.add(txtGolongan);

        addLbl(card, "Gaji Pokok (Rp)", C1, R6);
        txtGajiPokok = makeField(false);
        txtGajiPokok.setBounds(C1, R6+LH, FW, FH);
        card.add(txtGajiPokok);

        addLbl(card, "Tunjangan Istri (Rp)", C1, R7);
        txtTunjanganIstri = makeField(true);
        txtTunjanganIstri.setBounds(C1, R7+LH, FW, FH);
        card.add(txtTunjanganIstri);
        txtTunjanganIstri.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { hitungTotal(); }
        });

        addLbl(card, "Jumlah Anak", C1, R8);
        txtJumlahAnak = makeField(true);
        txtJumlahAnak.setBounds(C1, R8+LH, FW, FH);
        card.add(txtJumlahAnak);
        txtJumlahAnak.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { hitungTotalTunjAnak(); }
        });

        // ---- RIGHT COLUMN ----
        addLbl(card, "Tunjangan/Anak (Rp)", C2, R1);
        txtTunjanganAnak = makeField(true);
        txtTunjanganAnak.setBounds(C2, R1+LH, FW, FH);
        card.add(txtTunjanganAnak);
        txtTunjanganAnak.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { hitungTotalTunjAnak(); }
        });

        addLbl(card, "Total Tunj. Anak (Rp)", C2, R2);
        txtTotalTunjanganAnak = makeField(false);
        txtTotalTunjanganAnak.setBounds(C2, R2+LH, FW, FH);
        card.add(txtTotalTunjanganAnak);

        addLbl(card, "Transport (Rp)", C2, R3);
        txtTransport = makeField(false);
        txtTransport.setBounds(C2, R3+LH, FW, FH);
        card.add(txtTransport);

        addLbl(card, "Uang Makan (Rp)", C2, R4);
        txtUangMakan = makeField(false);
        txtUangMakan.setBounds(C2, R4+LH, FW, FH);
        card.add(txtUangMakan);

        addLbl(card, "Jumlah Gaji (Rp)", C2, R5);
        txtJumlahGaji = makeField(false);
        txtJumlahGaji.setBounds(C2, R5+LH, FW, FH);
        card.add(txtJumlahGaji);

        addLbl(card, "Jumlah Lembur (Rp)", C2, R6);
        txtJumlahLembur = makeField(false);
        txtJumlahLembur.setBounds(C2, R6+LH, FW, FH);
        card.add(txtJumlahLembur);

        addLbl(card, "Potongan (Rp)", C2, R7);
        txtPotongan = makeField(true);
        txtPotongan.setBounds(C2, R7+LH, FW, FH);
        card.add(txtPotongan);
        txtPotongan.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { hitungTotal(); }
        });

        addLbl(card, "Tanggal Gaji", C2, R8);
        dateTanggalGaji = new JDateChooser(); styleDC(dateTanggalGaji);
        dateTanggalGaji.setBounds(C2, R8+LH, FW, FH);
        card.add(dateTanggalGaji);

        return card;
    }

    private JPanel buildTotalCard() {
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(34, 197, 94, 50), getWidth(), 0, new Color(99, 102, 241, 30));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(34, 197, 94, 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        JLabel lbl = new JLabel("TOTAL GAJI BERSIH (Rp)");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(MainDashboard.C_TEXT_MUTED);
        lbl.setBounds(30, 8, 260, 18);
        card.add(lbl);

        txtTotalGaji = new JTextField() {
            @Override protected void paintBorder(Graphics g) {}
        };
        txtTotalGaji.setEditable(false);
        txtTotalGaji.setOpaque(false);
        txtTotalGaji.setBackground(new Color(0, 0, 0, 0));
        txtTotalGaji.setForeground(MainDashboard.C_SUCCESS);
        txtTotalGaji.setFont(new Font("Segoe UI", Font.BOLD, 26));
        txtTotalGaji.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        txtTotalGaji.setBounds(20, 26, 500, 36);
        card.add(txtTotalGaji);

        return card;
    }

    private JPanel buildButtonRow() {
        JPanel p = new JPanel(null);
        p.setOpaque(false);
        JButton bSave = makeBtn("Simpan", MainDashboard.C_SUCCESS);
        JButton bUpd  = makeBtn("Update", MainDashboard.C_INFO);
        JButton bDel  = makeBtn("Hapus", MainDashboard.C_DANGER);
        JButton bRst  = makeBtn("Reset", MainDashboard.C_WARN);
        bSave.setBounds(0, 4, 130, 40);
        bUpd.setBounds(140, 4, 130, 40);
        bDel.setBounds(280, 4, 130, 40);
        bRst.setBounds(420, 4, 130, 40);
        bSave.addActionListener(e -> doSave());
        bUpd.addActionListener(e -> doUpdate());
        bDel.addActionListener(e -> doDelete());
        bRst.addActionListener(e -> clearForm());
        p.add(bSave); p.add(bUpd); p.add(bDel); p.add(bRst);
        return p;
    }

    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(MainDashboard.C_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(MainDashboard.C_BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        String[] cols = {"ID Gaji", "Tanggal", "ID Kar.", "Nama", "Golongan", "Gaji Pokok",
                         "Tunj.Istri", "Jml Anak", "Tunj.Anak", "Total T.Anak",
                         "Transport", "Makan", "Jml Lembur", "Potongan", "Tgl Gaji", "Total Gaji"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = PanelKaryawan.buildStyledTable(model);
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { fillForm(); }
        });
        JScrollPane sp = new JScrollPane(table);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(null);
        styleScrollBar(sp);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    // =====================================================================
    // BUSINESS LOGIC
    // =====================================================================
    private void loadKaryawan() {
        cmbKaryawan.removeAllItems();
        try {
            ResultSet rs = Koneksi.getKoneksi().createStatement()
                .executeQuery("SELECT id_karyawan,nama FROM tb_karyawan ORDER BY id_karyawan");
            while (rs.next())
                cmbKaryawan.addItem(rs.getString("id_karyawan") + " - " + rs.getString("nama"));
        } catch (Exception ignored) {}
    }

    private String getIdKaryawan() {
        if (cmbKaryawan.getSelectedItem() == null) return "";
        return cmbKaryawan.getSelectedItem().toString().split(" - ")[0].trim();
    }

    private void updateFieldStatus(boolean menikah) {
        karyawanMenikah = menikah;
        Color bgOn = MainDashboard.C_FIELD, bgOff = new Color(20, 22, 36);
        Color fgOn = MainDashboard.C_TEXT,  fgOff = MainDashboard.C_TEXT_MUTED;
        for (JTextField f : new JTextField[]{txtTunjanganIstri, txtJumlahAnak, txtTunjanganAnak}) {
            f.setEditable(menikah); f.setEnabled(menikah);
            f.setBackground(menikah ? bgOn : bgOff);
            f.setForeground(menikah ? fgOn : fgOff);
        }
        txtTotalTunjanganAnak.setEditable(false);
        if (!menikah) {
            txtTunjanganIstri.setText("0"); txtJumlahAnak.setText("0");
            txtTunjanganAnak.setText("0"); txtTotalTunjanganAnak.setText("0");
        }
    }

    private void onKaryawanSelected() {
        String idKar = getIdKaryawan();
        if (idKar.isEmpty()) return;
        try {
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(
                "SELECT k.nama,k.status,g.nama_golongan,g.gaji_pokok,g.transport,g.uang_makan " +
                "FROM tb_karyawan k JOIN tb_golongan g ON k.id_golongan=g.id_golongan WHERE k.id_karyawan=?");
            ps.setString(1, idKar);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtNamaKaryawan.setText(rs.getString("nama"));
                txtGolongan.setText(rs.getString("nama_golongan"));
                txtGajiPokok.setText(fmt(rs.getDouble("gaji_pokok")));
                txtTransport.setText(fmt(rs.getDouble("transport")));
                txtUangMakan.setText(fmt(rs.getDouble("uang_makan")));
                boolean menikah = ("Menikah".equalsIgnoreCase(rs.getString("status")));
                updateFieldStatus(menikah);
                if (menikah) {
                    txtTunjanganIstri.setText(""); txtJumlahAnak.setText("");
                    txtTunjanganAnak.setText(""); txtTotalTunjanganAnak.setText("0");
                }
            }
            PreparedStatement psL = Koneksi.getKoneksi().prepareStatement(
                "SELECT COALESCE(SUM(total_lembur),0) AS tl FROM tb_lembur WHERE id_karyawan=?");
            psL.setString(1, idKar);
            ResultSet rsL = psL.executeQuery();
            if (rsL.next()) txtJumlahLembur.setText(fmt(rsL.getDouble("tl")));
            hitungTotal();
        } catch (Exception e) { showStatus("Error: " + e.getMessage(), MainDashboard.C_DANGER); }
    }

    private void hitungTotalTunjAnak() {
        try {
            int jml = Integer.parseInt(txtJumlahAnak.getText().trim().isEmpty() ? "0" : txtJumlahAnak.getText().trim());
            double tunj = parse(txtTunjanganAnak.getText());
            txtTotalTunjanganAnak.setText(fmt(jml * tunj));
            hitungTotal();
        } catch (Exception ignored) {}
    }

    private void hitungTotal() {
        try {
            double gp  = parse(txtGajiPokok.getText());
            double ti  = karyawanMenikah ? parse(txtTunjanganIstri.getText()) : 0;
            double tta = karyawanMenikah ? parse(txtTotalTunjanganAnak.getText()) : 0;
            double tr  = parse(txtTransport.getText());
            double um  = parse(txtUangMakan.getText());
            double lmb = parse(txtJumlahLembur.getText());
            double pot = parse(txtPotongan.getText());
            double jg  = gp + ti + tta + tr + um;
            double total = jg + lmb - pot;
            txtJumlahGaji.setText(fmt(jg));
            txtTotalGaji.setText(fmt(total));
        } catch (Exception ignored) {}
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            ResultSet rs = Koneksi.getKoneksi().createStatement().executeQuery("SELECT * FROM tb_penggajian");
            while (rs.next()) model.addRow(new Object[]{
                rs.getString("id_gaji"), rs.getDate("tanggal"),
                rs.getString("id_karyawan"), rs.getString("nama_karyawan"), rs.getString("golongan"),
                fmt(rs.getDouble("gaji_pokok")), fmt(rs.getDouble("tunjangan_istri")),
                rs.getInt("jumlah_anak"), fmt(rs.getDouble("tunjangan_anak")),
                fmt(rs.getDouble("total_tunjangan_anak")), fmt(rs.getDouble("transport")),
                fmt(rs.getDouble("uang_makan")), fmt(rs.getDouble("jumlah_lembur")),
                fmt(rs.getDouble("potongan")), rs.getDate("tanggal_gaji"), fmt(rs.getDouble("total_gaji"))
            });
        } catch (Exception e) { showStatus("Error: " + e.getMessage(), MainDashboard.C_DANGER); }
    }

    private void clearForm() {
        txtIdGaji.setText(""); dateTanggal.setDate(null);
        if (cmbKaryawan.getItemCount() > 0) cmbKaryawan.setSelectedIndex(0);
        txtNamaKaryawan.setText(""); txtGolongan.setText("");
        txtGajiPokok.setText(""); txtTransport.setText(""); txtUangMakan.setText("");
        txtJumlahGaji.setText(""); txtJumlahLembur.setText(""); txtPotongan.setText("");
        dateTanggalGaji.setDate(null); txtTotalGaji.setText("");
        updateFieldStatus(false);
    }

    private void doSave() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tgl = sdf.format(dateTanggal.getDate());
            String tglGaji = sdf.format(dateTanggalGaji.getDate());
            int jmlAnak = 0;
            try { jmlAnak = Integer.parseInt(txtJumlahAnak.getText().trim()); } catch (Exception ignored) {}
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(
                "INSERT INTO tb_penggajian(id_gaji,tanggal,id_karyawan,nama_karyawan,golongan," +
                "gaji_pokok,tunjangan_istri,jumlah_anak,tunjangan_anak,total_tunjangan_anak," +
                "transport,uang_makan,jumlah_gaji,jumlah_lembur,potongan,tanggal_gaji,total_gaji) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, txtIdGaji.getText()); ps.setString(2, tgl); ps.setString(3, getIdKaryawan());
            ps.setString(4, txtNamaKaryawan.getText()); ps.setString(5, txtGolongan.getText());
            ps.setDouble(6, parse(txtGajiPokok.getText()));
            ps.setDouble(7, karyawanMenikah ? parse(txtTunjanganIstri.getText()) : 0);
            ps.setInt(8, karyawanMenikah ? jmlAnak : 0);
            ps.setDouble(9, karyawanMenikah ? parse(txtTunjanganAnak.getText()) : 0);
            ps.setDouble(10, karyawanMenikah ? parse(txtTotalTunjanganAnak.getText()) : 0);
            ps.setDouble(11, parse(txtTransport.getText())); ps.setDouble(12, parse(txtUangMakan.getText()));
            ps.setDouble(13, parse(txtJumlahGaji.getText())); ps.setDouble(14, parse(txtJumlahLembur.getText()));
            ps.setDouble(15, parse(txtPotongan.getText())); ps.setString(16, tglGaji);
            ps.setDouble(17, parse(txtTotalGaji.getText()));
            ps.executeUpdate();
            showStatus("Data berhasil disimpan!", MainDashboard.C_SUCCESS);
            clearForm(); loadData();
        } catch (Exception e) { showStatus("Error: " + e.getMessage(), MainDashboard.C_DANGER); }
    }

    private void doUpdate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tgl = sdf.format(dateTanggal.getDate());
            String tglGaji = sdf.format(dateTanggalGaji.getDate());
            int jmlAnak = 0;
            try { jmlAnak = Integer.parseInt(txtJumlahAnak.getText().trim()); } catch (Exception ignored) {}
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(
                "UPDATE tb_penggajian SET tanggal=?,id_karyawan=?,nama_karyawan=?,golongan=?," +
                "gaji_pokok=?,tunjangan_istri=?,jumlah_anak=?,tunjangan_anak=?,total_tunjangan_anak=?," +
                "transport=?,uang_makan=?,jumlah_gaji=?,jumlah_lembur=?,potongan=?,tanggal_gaji=?,total_gaji=? WHERE id_gaji=?");
            ps.setString(1, tgl); ps.setString(2, getIdKaryawan()); ps.setString(3, txtNamaKaryawan.getText());
            ps.setString(4, txtGolongan.getText()); ps.setDouble(5, parse(txtGajiPokok.getText()));
            ps.setDouble(6, karyawanMenikah ? parse(txtTunjanganIstri.getText()) : 0);
            ps.setInt(7, karyawanMenikah ? jmlAnak : 0);
            ps.setDouble(8, karyawanMenikah ? parse(txtTunjanganAnak.getText()) : 0);
            ps.setDouble(9, karyawanMenikah ? parse(txtTotalTunjanganAnak.getText()) : 0);
            ps.setDouble(10, parse(txtTransport.getText())); ps.setDouble(11, parse(txtUangMakan.getText()));
            ps.setDouble(12, parse(txtJumlahGaji.getText())); ps.setDouble(13, parse(txtJumlahLembur.getText()));
            ps.setDouble(14, parse(txtPotongan.getText())); ps.setString(15, tglGaji);
            ps.setDouble(16, parse(txtTotalGaji.getText())); ps.setString(17, txtIdGaji.getText());
            ps.executeUpdate();
            showStatus("Data berhasil diupdate!", MainDashboard.C_SUCCESS);
            clearForm(); loadData();
        } catch (Exception e) { showStatus("Error: " + e.getMessage(), MainDashboard.C_DANGER); }
    }

    private void doDelete() {
        if (JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement ps = Koneksi.getKoneksi().prepareStatement("DELETE FROM tb_penggajian WHERE id_gaji=?");
                ps.setString(1, txtIdGaji.getText()); ps.executeUpdate();
                showStatus("Data berhasil dihapus!", MainDashboard.C_SUCCESS);
                clearForm(); loadData();
            } catch (Exception e) { showStatus("Error: " + e.getMessage(), MainDashboard.C_DANGER); }
        }
    }

    private void fillForm() {
        try {
            int row = table.getSelectedRow();
            txtIdGaji.setText(model.getValueAt(row, 0).toString());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateTanggal.setDate(sdf.parse(model.getValueAt(row, 1).toString()));
            String idKar = model.getValueAt(row, 2).toString();
            for (int i = 0; i < cmbKaryawan.getItemCount(); i++)
                if (cmbKaryawan.getItemAt(i).startsWith(idKar + " - ")) { cmbKaryawan.setSelectedIndex(i); break; }
            txtNamaKaryawan.setText(model.getValueAt(row, 3).toString());
            txtGolongan.setText(model.getValueAt(row, 4).toString());
            txtGajiPokok.setText(model.getValueAt(row, 5).toString().replace(".", ""));
            double ti = parse(model.getValueAt(row, 6).toString());
            int jmlAnak = Integer.parseInt(model.getValueAt(row, 7).toString());
            updateFieldStatus(ti > 0 || jmlAnak > 0);
            txtTunjanganIstri.setText(model.getValueAt(row, 6).toString().replace(".", ""));
            txtJumlahAnak.setText(model.getValueAt(row, 7).toString());
            txtTunjanganAnak.setText(model.getValueAt(row, 8).toString().replace(".", ""));
            txtTotalTunjanganAnak.setText(model.getValueAt(row, 9).toString().replace(".", ""));
            txtTransport.setText(model.getValueAt(row, 10).toString().replace(".", ""));
            txtUangMakan.setText(model.getValueAt(row, 11).toString().replace(".", ""));
            txtJumlahLembur.setText(model.getValueAt(row, 12).toString().replace(".", ""));
            txtPotongan.setText(model.getValueAt(row, 13).toString().replace(".", ""));
            dateTanggalGaji.setDate(sdf.parse(model.getValueAt(row, 14).toString()));
            hitungTotal();
        } catch (Exception e) { showStatus("Error: " + e.getMessage(), MainDashboard.C_DANGER); }
    }

    // =====================================================================
    // HELPERS
    // =====================================================================
    private String fmt(double v) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
        nf.setMaximumFractionDigits(0);
        return nf.format(v);
    }
    private double parse(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        return Double.parseDouble(s.trim().replace(".", "").replace(",", ""));
    }
    private void showStatus(String m, Color c) { lblStatus.setText(m); lblStatus.setForeground(c); }

    private JPanel makePanelCard() {
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(MainDashboard.C_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                GradientPaint gp = new GradientPaint(0, 0, new Color(99, 102, 241, 20), getWidth(), 0, new Color(0, 0, 0, 0));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), 3, 14, 14);
                g2.setColor(MainDashboard.C_BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }

    private void addLbl(JPanel p, String t, int x, int y) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(MainDashboard.C_TEXT_MUTED);
        l.setBounds(x, y, 400, 18);
        p.add(l);
    }

    private JTextField makeField(boolean editable) {
        JTextField f = new JTextField() {
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hasFocus() ? MainDashboard.C_ACCENT : MainDashboard.C_BORDER);
                g2.setStroke(new BasicStroke(hasFocus() ? 1.5f : 1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
            }
        };
        f.setEditable(editable);
        f.setBackground(editable ? MainDashboard.C_FIELD : new Color(20, 22, 36));
        f.setForeground(editable ? MainDashboard.C_TEXT : MainDashboard.C_TEXT_MUTED);
        f.setCaretColor(MainDashboard.C_ACCENT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        f.setOpaque(true);
        if (editable) f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { f.repaint(); }
            @Override public void focusLost(FocusEvent e) { f.repaint(); }
        });
        return f;
    }

    private JComboBox<String> makeCombo() {
        JComboBox<String> c = new JComboBox<>();
        c.setBackground(MainDashboard.C_FIELD);
        c.setForeground(MainDashboard.C_TEXT);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        c.setBorder(BorderFactory.createLineBorder(MainDashboard.C_BORDER, 1));
        return c;
    }

    private JPanel buildIconHeader(String titleText, String subText, String iconPath, Color accentColor) {
        JPanel header = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(MainDashboard.C_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                GradientPaint gp = new GradientPaint(0, 0,
                    new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 35),
                    getWidth(), 0, new Color(0, 0, 0, 0));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(MainDashboard.C_BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        JPanel iconCircle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, accentColor, getWidth(), getHeight(), accentColor.darker());
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                try {
                    java.net.URL u = getClass().getResource(iconPath);
                    if (u != null) {
                        Image img = new ImageIcon(u).getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
                        g2.drawImage(img, (getWidth()-28)/2, (getHeight()-28)/2, 28, 28, null);
                    }
                } catch (Exception ignored) {}
                g2.dispose();
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setBounds(14, 15, 46, 46);
        header.add(iconCircle);
        JLabel lTitle = MainDashboard.makeSectionTitle(titleText);
        lTitle.setBounds(72, 10, 500, 30);
        header.add(lTitle);
        JLabel lSub = new JLabel(subText);
        lSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lSub.setForeground(MainDashboard.C_TEXT_MUTED);
        lSub.setBounds(72, 42, 500, 18);
        header.add(lSub);
        return header;
    }

    private JButton makeBtn(String text, Color color) {
        JButton b = new JButton(text) {
            private boolean h = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { h = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { h = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = h ? new Color(color.getRed(), color.getGreen(), color.getBlue(), 200)
                             : new Color(color.getRed(), color.getGreen(), color.getBlue(), 80);
                g2.setColor(bg); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(h ? color.brighter() : color); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setColor(h ? Color.WHITE : color.brighter()); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void styleDC(JDateChooser dc) {
        dc.setBackground(MainDashboard.C_FIELD); dc.setForeground(MainDashboard.C_TEXT);
        dc.setBorder(BorderFactory.createLineBorder(MainDashboard.C_BORDER, 1));
        Component comp = dc.getDateEditor().getUiComponent();
        if (comp instanceof JTextField) {
            JTextField tf = (JTextField) comp;
            tf.setBackground(MainDashboard.C_FIELD); tf.setForeground(MainDashboard.C_TEXT);
            tf.setCaretColor(MainDashboard.C_ACCENT); tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            tf.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4)); tf.setOpaque(true);
        }
        styleAllDC(dc);
        SwingUtilities.invokeLater(() -> { styleAllDC(dc); dc.repaint(); });
    }

    private void styleAllDC(Container c) {
        for (Component comp : c.getComponents()) {
            if (comp instanceof JFormattedTextField) {
                JFormattedTextField f = (JFormattedTextField) comp;
                f.setBackground(MainDashboard.C_FIELD); f.setForeground(MainDashboard.C_TEXT);
                f.setCaretColor(MainDashboard.C_ACCENT); f.setDisabledTextColor(MainDashboard.C_TEXT);
                f.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));
                f.setFont(new Font("Segoe UI", Font.PLAIN, 12)); f.setOpaque(true);
            } else if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setBackground(new Color(30, 33, 55)); btn.setForeground(MainDashboard.C_TEXT);
                btn.setBorderPainted(false); btn.setFocusPainted(false); btn.setOpaque(true);
            } else {
                comp.setBackground(MainDashboard.C_FIELD); comp.setForeground(MainDashboard.C_TEXT);
            }
            if (comp instanceof Container) styleAllDC((Container) comp);
        }
    }

    private void styleScrollBar(JScrollPane sp) {
        sp.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { thumbColor = new Color(60,65,100); trackColor = MainDashboard.C_SIDEBAR; }
            @Override protected JButton createDecreaseButton(int o) { return z(); }
            @Override protected JButton createIncreaseButton(int o) { return z(); }
            private JButton z() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0, 0)); return b; }
        });
        sp.getHorizontalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { thumbColor = new Color(60,65,100); trackColor = MainDashboard.C_SIDEBAR; }
            @Override protected JButton createDecreaseButton(int o) { return z(); }
            @Override protected JButton createIncreaseButton(int o) { return z(); }
            private JButton z() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0, 0)); return b; }
        });
    }
}