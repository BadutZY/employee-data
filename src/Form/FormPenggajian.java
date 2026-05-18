package form;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import Koneksi.Koneksi;
import com.toedter.calendar.JDateChooser;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FormPenggajian extends javax.swing.JFrame {

    private DefaultTableModel model;
    private boolean karyawanMenikah = false;

    public FormPenggajian() {
        initComponents();
        setAppIcon();
        setLocationRelativeTo(null);
        String[] kolom = {
            "ID Gaji", "Tanggal", "ID Karyawan", "Nama", "Golongan",
            "Gaji Pokok", "Tunj. Istri", "Jml Anak", "Tunj. Anak",
            "Total Tunj. Anak", "Transport", "Uang Makan",
            "Jml Lembur", "Potongan", "Tgl Gaji", "Total Gaji"
        };
        model = new DefaultTableModel(kolom, 0);
        tblPenggajian.setModel(model);
        loadKaryawan();
        loadData();
        updateFieldStatus(false);
    }

    private void setAppIcon() {
        try {
            java.net.URL imgURL = getClass().getResource("/foto/icon.png");
            if (imgURL != null) setIconImage(new ImageIcon(imgURL).getImage());
        } catch (Exception e) {
            System.err.println("Gagal memuat icon: " + e.getMessage());
        }
    }

    private ImageIcon createIconForButton(String iconFile) {
        try {
            java.net.URL imgURL = getClass().getResource("/foto/" + iconFile);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                java.awt.Image img = icon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat icon tombol: " + e.getMessage());
        }
        return null;
    }

    private void applyButtonIcon(JButton btn, String iconFile) {
        ImageIcon icon = createIconForButton(iconFile);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(5);
        }
    }

    private String formatAngka(double angka) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
        nf.setMaximumFractionDigits(0);
        return nf.format(angka);
    }

    private double parseAngka(String teks) {
        if (teks == null || teks.trim().isEmpty()) return 0;
        return Double.parseDouble(teks.trim().replace(".", "").replace(",", ""));
    }

    private void loadKaryawan() {
        cmbIdKaryawan.removeAllItems();
        try {
            Connection conn = Koneksi.getKoneksi();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id_karyawan, nama FROM tb_karyawan ORDER BY id_karyawan");
            while (rs.next()) {
                cmbIdKaryawan.addItem(rs.getString("id_karyawan") + " - " + rs.getString("nama"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load karyawan: " + e.getMessage());
        }
    }

    private String getIdKaryawan() {
        if (cmbIdKaryawan.getSelectedItem() == null) return "";
        return cmbIdKaryawan.getSelectedItem().toString().split(" - ")[0].trim();
    }

    private void updateFieldStatus(boolean menikah) {
        karyawanMenikah = menikah;
        txtTunjanganIstri.setEditable(menikah);
        txtTunjanganIstri.setEnabled(menikah);
        txtJumlahAnak.setEditable(menikah);
        txtJumlahAnak.setEnabled(menikah);
        txtTunjanganAnak.setEditable(menikah);
        txtTunjanganAnak.setEnabled(menikah);
        txtTotalTunjanganAnak.setEditable(false);
        if (!menikah) {
            txtTunjanganIstri.setText("0");
            txtJumlahAnak.setText("0");
            txtTunjanganAnak.setText("0");
            txtTotalTunjanganAnak.setText("0");
        }
    }

    private void onKaryawanSelected() {
        String idKar = getIdKaryawan();
        if (idKar.isEmpty()) return;
        try {
            Connection conn = Koneksi.getKoneksi();
            String sql =
                "SELECT k.nama, k.status, g.nama_golongan, " +
                "g.gaji_pokok, g.transport, g.uang_makan " +
                "FROM tb_karyawan k JOIN tb_golongan g ON k.id_golongan = g.id_golongan " +
                "WHERE k.id_karyawan = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, idKar);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtNamaKaryawan.setText(rs.getString("nama"));
                txtGolongan.setText(rs.getString("nama_golongan"));
                txtGajiPokok.setText(formatAngka(rs.getDouble("gaji_pokok")));
                txtTransport.setText(formatAngka(rs.getDouble("transport")));
                txtUangMakan.setText(formatAngka(rs.getDouble("uang_makan")));

                String status   = rs.getString("status");
                boolean menikah = (status != null && status.equalsIgnoreCase("Menikah"));
                updateFieldStatus(menikah);

                if (menikah) {
                    txtTunjanganIstri.setText("");
                    txtJumlahAnak.setText("");
                    txtTunjanganAnak.setText("");
                    txtTotalTunjanganAnak.setText("0");
                }
            }
            PreparedStatement psL = conn.prepareStatement(
                "SELECT COALESCE(SUM(total_lembur),0) AS total_lembur FROM tb_lembur WHERE id_karyawan=?");
            psL.setString(1, idKar);
            ResultSet rsL = psL.executeQuery();
            if (rsL.next()) txtJumlahLembur.setText(formatAngka(rsL.getDouble("total_lembur")));
            hitungTotal();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load data karyawan: " + e.getMessage());
        }
    }

    private void hitungTotalTunjanganAnak() {
        try {
            int    jumlahAnak    = Integer.parseInt(txtJumlahAnak.getText().trim().isEmpty() ? "0" : txtJumlahAnak.getText().trim());
            double tunjanganAnak = parseAngka(txtTunjanganAnak.getText());
            txtTotalTunjanganAnak.setText(formatAngka(jumlahAnak * tunjanganAnak));
            hitungTotal();
        } catch (NumberFormatException ex) { /* biarkan user ketik */ }
    }

    private void hitungTotal() {
        try {
            double gajiPokok     = parseAngka(txtGajiPokok.getText());
            double tunjanganIstri = karyawanMenikah ? parseAngka(txtTunjanganIstri.getText()) : 0;
            double totalTunjAnak  = karyawanMenikah ? parseAngka(txtTotalTunjanganAnak.getText()) : 0;
            double transport      = parseAngka(txtTransport.getText());
            double uangMakan      = parseAngka(txtUangMakan.getText());
            double lembur         = parseAngka(txtJumlahLembur.getText());
            double potong         = parseAngka(txtPotongan.getText());
            double jumlahGaji = gajiPokok + tunjanganIstri + totalTunjAnak + transport + uangMakan;
            double total      = jumlahGaji + lembur - potong;
            txtJumlahGaji.setText(formatAngka(jumlahGaji));
            txtTotalGaji.setText(formatAngka(total));
        } catch (NumberFormatException ex) { /* biarkan user ketik */ }
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            Connection conn = Koneksi.getKoneksi();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM tb_penggajian");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_gaji"),
                    rs.getDate("tanggal"),
                    rs.getString("id_karyawan"),
                    rs.getString("nama_karyawan"),
                    rs.getString("golongan"),
                    formatAngka(rs.getDouble("gaji_pokok")),
                    formatAngka(rs.getDouble("tunjangan_istri")),
                    rs.getInt("jumlah_anak"),
                    formatAngka(rs.getDouble("tunjangan_anak")),
                    formatAngka(rs.getDouble("total_tunjangan_anak")),
                    formatAngka(rs.getDouble("transport")),
                    formatAngka(rs.getDouble("uang_makan")),
                    formatAngka(rs.getDouble("jumlah_lembur")),
                    formatAngka(rs.getDouble("potongan")),
                    rs.getDate("tanggal_gaji"),
                    formatAngka(rs.getDouble("total_gaji"))
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load data: " + e.getMessage());
        }
    }

    private void clearForm() {
        txtIdGaji.setText("");
        dateTanggal.setDate(null);
        if (cmbIdKaryawan.getItemCount() > 0) cmbIdKaryawan.setSelectedIndex(0);
        txtNamaKaryawan.setText("");
        txtGolongan.setText("");
        txtGajiPokok.setText("");
        txtTransport.setText("");
        txtUangMakan.setText("");
        txtJumlahGaji.setText("");
        txtJumlahLembur.setText("");
        txtPotongan.setText("");
        dateTanggalGaji.setDate(null);
        txtTotalGaji.setText("");
        updateFieldStatus(false);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jLabel1  = new javax.swing.JLabel();
        jLabel2  = new javax.swing.JLabel();
        jLabel3  = new javax.swing.JLabel();
        jLabel4  = new javax.swing.JLabel();
        jLabel5  = new javax.swing.JLabel();
        jLabel6  = new javax.swing.JLabel();
        jLabel7  = new javax.swing.JLabel();
        jLabel8  = new javax.swing.JLabel();
        jLabel9  = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();

        txtIdGaji            = new javax.swing.JTextField();
        dateTanggal          = new com.toedter.calendar.JDateChooser();
        cmbIdKaryawan        = new javax.swing.JComboBox<>();
        txtNamaKaryawan      = new javax.swing.JTextField();
        txtGolongan          = new javax.swing.JTextField();
        txtGajiPokok         = new javax.swing.JTextField();
        txtTunjanganIstri    = new javax.swing.JTextField();
        txtJumlahAnak        = new javax.swing.JTextField();
        txtTunjanganAnak      = new javax.swing.JTextField();
        txtTotalTunjanganAnak = new javax.swing.JTextField();
        txtTransport          = new javax.swing.JTextField();
        txtUangMakan          = new javax.swing.JTextField();
        txtJumlahGaji         = new javax.swing.JTextField();
        txtJumlahLembur       = new javax.swing.JTextField();
        txtPotongan           = new javax.swing.JTextField();
        dateTanggalGaji       = new com.toedter.calendar.JDateChooser();
        txtTotalGaji          = new javax.swing.JTextField();

        txtNamaKaryawan.setEditable(false);
        txtGolongan.setEditable(false);
        txtGajiPokok.setEditable(false);
        txtTransport.setEditable(false);
        txtUangMakan.setEditable(false);
        txtJumlahGaji.setEditable(false);
        txtJumlahLembur.setEditable(false);
        txtTotalGaji.setEditable(false);
        txtTotalTunjanganAnak.setEditable(false);

        cmbIdKaryawan.addActionListener(evt -> onKaryawanSelected());

        txtPotongan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { hitungTotal(); }
        });
        txtJumlahAnak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { hitungTotalTunjanganAnak(); }
        });
        txtTunjanganAnak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { hitungTotalTunjanganAnak(); }
        });
        txtTunjanganIstri.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { hitungTotal(); }
        });

        btnSave   = new javax.swing.JButton();
        btnReset  = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnExit   = new javax.swing.JButton();
        jScrollPane1  = new javax.swing.JScrollPane();
        tblPenggajian = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Form Penggajian");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16));
        jLabel1.setText("DATA PENGGAJIAN");
        jLabel2.setText("Id. Gaji");
        jLabel3.setText("Tanggal");
        jLabel4.setText("Id Karyawan");
        jLabel5.setText("Nama Karyawan");
        jLabel6.setText("Golongan");
        jLabel7.setText("Gaji Pokok");
        jLabel8.setText("Tunjangan Istri");
        jLabel9.setText("Jumlah Anak");
        jLabel10.setText("Tunjangan/Anak");
        jLabel11.setText("Total Tunj. Anak");
        jLabel12.setText("Transport");
        jLabel13.setText("Uang Makan");
        jLabel14.setText("Jumlah Gaji");
        jLabel15.setText("Jumlah Lembur");
        jLabel16.setText("Potongan");
        jLabel17.setText("Tanggal Gaji");
        jLabel18.setText("Total Gaji");

        btnSave.setText("Save");
        applyButtonIcon(btnSave, "save.png");
        btnSave.addActionListener(evt -> btnSaveActionPerformed(evt));

        btnReset.setText("Reset");
        applyButtonIcon(btnReset, "reset.png");
        btnReset.addActionListener(evt -> btnResetActionPerformed(evt));

        btnUpdate.setText("Update");
        applyButtonIcon(btnUpdate, "update.png");
        btnUpdate.addActionListener(evt -> btnUpdateActionPerformed(evt));

        btnDelete.setText("Delete");
        applyButtonIcon(btnDelete, "delete.png");
        btnDelete.addActionListener(evt -> btnDeleteActionPerformed(evt));

        btnExit.setText("Exit");
        applyButtonIcon(btnExit, "exit.png");
        btnExit.addActionListener(evt -> btnExitActionPerformed(evt));

        tblPenggajian.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{
                "ID Gaji", "Tanggal", "ID Karyawan", "Nama", "Golongan",
                "Gaji Pokok", "Tunj. Istri", "Jml Anak", "Tunj. Anak",
                "Total Tunj. Anak", "Transport", "Uang Makan",
                "Jml Lembur", "Potongan", "Tgl Gaji", "Total Gaji"
            }
        ));
        tblPenggajian.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { tblPenggajianMouseClicked(evt); }
        });
        jScrollPane1.setViewportView(tblPenggajian);

        int W = 200;

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addGap(20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtIdGaji,         javax.swing.GroupLayout.DEFAULT_SIZE, W, Short.MAX_VALUE)
                            .addComponent(dateTanggal,       javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbIdKaryawan,     0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtNamaKaryawan)
                            .addComponent(txtGolongan)
                            .addComponent(txtGajiPokok)
                            .addComponent(txtTunjanganIstri)
                            .addComponent(txtJumlahAnak))
                        .addGap(40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18))
                        .addGap(20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTunjanganAnak,      javax.swing.GroupLayout.DEFAULT_SIZE, W, Short.MAX_VALUE)
                            .addComponent(txtTotalTunjanganAnak)
                            .addComponent(txtTransport)
                            .addComponent(txtUangMakan)
                            .addComponent(txtJumlahGaji)
                            .addComponent(txtJumlahLembur)
                            .addComponent(txtPotongan)
                            .addComponent(dateTanggalGaji,       javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtTotalGaji)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSave,   javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                        .addComponent(btnReset,  javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                        .addComponent(btnExit,   javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20)
                .addComponent(jLabel1)
                .addGap(20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtIdGaji,           javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtTunjanganAnak,    javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(dateTanggal,         javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(txtTotalTunjanganAnak,javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cmbIdKaryawan,       javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(txtTransport,        javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtNamaKaryawan,     javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(txtUangMakan,        javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtGolongan,         javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(txtJumlahGaji,       javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtGajiPokok,        javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(txtJumlahLembur,     javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtTunjanganIstri,   javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(txtPotongan,         javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtJumlahAnak,       javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(dateTanggalGaji,     javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtTotalGaji,        javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave).addComponent(btnReset).addComponent(btnUpdate)
                    .addComponent(btnDelete).addComponent(btnExit))
                .addGap(20)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tanggal     = sdf.format(dateTanggal.getDate());
            String tanggalGaji = sdf.format(dateTanggalGaji.getDate());
            int jumlahAnak = 0;
            try { jumlahAnak = Integer.parseInt(txtJumlahAnak.getText().trim()); } catch (Exception ex) {}
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO tb_penggajian (id_gaji, tanggal, id_karyawan, nama_karyawan, golongan, " +
                "gaji_pokok, tunjangan_istri, jumlah_anak, tunjangan_anak, total_tunjangan_anak, " +
                "transport, uang_makan, jumlah_gaji, jumlah_lembur, potongan, tanggal_gaji, total_gaji) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, txtIdGaji.getText());
            ps.setString(2, tanggal);
            ps.setString(3, getIdKaryawan());
            ps.setString(4, txtNamaKaryawan.getText());
            ps.setString(5, txtGolongan.getText());
            ps.setDouble(6,  parseAngka(txtGajiPokok.getText()));
            ps.setDouble(7,  karyawanMenikah ? parseAngka(txtTunjanganIstri.getText()) : 0);
            ps.setInt(8,     karyawanMenikah ? jumlahAnak : 0);
            ps.setDouble(9,  karyawanMenikah ? parseAngka(txtTunjanganAnak.getText()) : 0);
            ps.setDouble(10, karyawanMenikah ? parseAngka(txtTotalTunjanganAnak.getText()) : 0);
            ps.setDouble(11, parseAngka(txtTransport.getText()));
            ps.setDouble(12, parseAngka(txtUangMakan.getText()));
            ps.setDouble(13, parseAngka(txtJumlahGaji.getText()));
            ps.setDouble(14, parseAngka(txtJumlahLembur.getText()));
            ps.setDouble(15, parseAngka(txtPotongan.getText()));
            ps.setString(16, tanggalGaji);
            ps.setDouble(17, parseAngka(txtTotalGaji.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
            clearForm(); loadData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) { clearForm(); }

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tanggal     = sdf.format(dateTanggal.getDate());
            String tanggalGaji = sdf.format(dateTanggalGaji.getDate());
            int jumlahAnak = 0;
            try { jumlahAnak = Integer.parseInt(txtJumlahAnak.getText().trim()); } catch (Exception ex) {}
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE tb_penggajian SET tanggal=?, id_karyawan=?, nama_karyawan=?, golongan=?, " +
                "gaji_pokok=?, tunjangan_istri=?, jumlah_anak=?, tunjangan_anak=?, total_tunjangan_anak=?, " +
                "transport=?, uang_makan=?, jumlah_gaji=?, jumlah_lembur=?, potongan=?, tanggal_gaji=?, total_gaji=? " +
                "WHERE id_gaji=?");
            ps.setString(1,  tanggal);
            ps.setString(2,  getIdKaryawan());
            ps.setString(3,  txtNamaKaryawan.getText());
            ps.setString(4,  txtGolongan.getText());
            ps.setDouble(5,  parseAngka(txtGajiPokok.getText()));
            ps.setDouble(6,  karyawanMenikah ? parseAngka(txtTunjanganIstri.getText()) : 0);
            ps.setInt(7,     karyawanMenikah ? jumlahAnak : 0);
            ps.setDouble(8,  karyawanMenikah ? parseAngka(txtTunjanganAnak.getText()) : 0);
            ps.setDouble(9,  karyawanMenikah ? parseAngka(txtTotalTunjanganAnak.getText()) : 0);
            ps.setDouble(10, parseAngka(txtTransport.getText()));
            ps.setDouble(11, parseAngka(txtUangMakan.getText()));
            ps.setDouble(12, parseAngka(txtJumlahGaji.getText()));
            ps.setDouble(13, parseAngka(txtJumlahLembur.getText()));
            ps.setDouble(14, parseAngka(txtPotongan.getText()));
            ps.setString(15, tanggalGaji);
            ps.setDouble(16, parseAngka(txtTotalGaji.getText()));
            ps.setString(17, txtIdGaji.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
            clearForm(); loadData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus data?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = Koneksi.getKoneksi();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_penggajian WHERE id_gaji=?");
                ps.setString(1, txtIdGaji.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                clearForm(); loadData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) { this.dispose(); }

    private void tblPenggajianMouseClicked(java.awt.event.MouseEvent evt) {
        try {
            int row = tblPenggajian.getSelectedRow();
            txtIdGaji.setText(model.getValueAt(row, 0).toString());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateTanggal.setDate(sdf.parse(model.getValueAt(row, 1).toString()));
            String idKar = model.getValueAt(row, 2).toString();
            for (int i = 0; i < cmbIdKaryawan.getItemCount(); i++) {
                if (cmbIdKaryawan.getItemAt(i).toString().startsWith(idKar + " - ")) {
                    cmbIdKaryawan.setSelectedIndex(i); break;
                }
            }
            txtNamaKaryawan.setText(model.getValueAt(row, 3).toString());
            txtGolongan.setText(model.getValueAt(row, 4).toString());
            txtGajiPokok.setText(model.getValueAt(row, 5).toString().replace(".", ""));
            double tunjIstri = parseAngka(model.getValueAt(row, 6).toString());
            int    jmlAnak   = Integer.parseInt(model.getValueAt(row, 7).toString());
            updateFieldStatus(tunjIstri > 0 || jmlAnak > 0);
            txtTunjanganIstri.setText(model.getValueAt(row, 6).toString().replace(".", ""));
            txtJumlahAnak.setText(model.getValueAt(row, 7).toString());
            txtTunjanganAnak.setText(model.getValueAt(row, 8).toString().replace(".", ""));
            txtTotalTunjanganAnak.setText(model.getValueAt(row, 9).toString().replace(".", ""));
            txtTransport.setText(model.getValueAt(row, 10).toString().replace(".", ""));
            txtUangMakan.setText(model.getValueAt(row, 11).toString().replace(".", ""));
            txtJumlahLembur.setText(model.getValueAt(row, 12).toString().replace(".", ""));
            txtPotongan.setText(model.getValueAt(row, 13).toString().replace(".", ""));
            dateTanggalGaji.setDate(sdf.parse(model.getValueAt(row, 14).toString()));
            txtTotalGaji.setText(model.getValueAt(row, 15).toString().replace(".", ""));
            hitungTotal();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private javax.swing.JButton btnDelete, btnExit, btnReset, btnSave, btnUpdate;
    private javax.swing.JComboBox<String> cmbIdKaryawan;
    private com.toedter.calendar.JDateChooser dateTanggal, dateTanggalGaji;
    private javax.swing.JLabel jLabel1,  jLabel2,  jLabel3,  jLabel4,  jLabel5,
                               jLabel6,  jLabel7,  jLabel8,  jLabel9,  jLabel10,
                               jLabel11, jLabel12, jLabel13, jLabel14, jLabel15,
                               jLabel16, jLabel17, jLabel18;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblPenggajian;
    private javax.swing.JTextField txtIdGaji, txtNamaKaryawan, txtGolongan,
                                   txtGajiPokok, txtTunjanganIstri, txtJumlahAnak,
                                   txtTunjanganAnak, txtTotalTunjanganAnak,
                                   txtTransport, txtUangMakan,
                                   txtJumlahGaji, txtJumlahLembur, txtPotongan, txtTotalGaji;
}