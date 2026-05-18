package form;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import Koneksi.Koneksi;
import com.toedter.calendar.JDateChooser;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FormLembur extends javax.swing.JFrame {

    private DefaultTableModel model;

    public FormLembur() {
        initComponents();
        setAppIcon();
        setLocationRelativeTo(null);
        String[] kolom = {
            "ID Lembur", "ID Karyawan", "Tgl Mulai", "Tgl Selesai",
            "Jumlah Jam", "Upah/Jam", "Total Lembur"
        };
        model = new DefaultTableModel(kolom, 0);
        tblLembur.setModel(model);
        loadKaryawan();
        loadData();
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

    private void hitungTotalLembur() {
        try {
            double jam     = parseAngka(txtJumlahJam.getText());
            double upahJam = parseAngka(txtUpahPerJam.getText());
            txtTotalLembur.setText(formatAngka(jam * upahJam));
        } catch (NumberFormatException ex) { /* biarkan user ketik */ }
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            Connection conn = Koneksi.getKoneksi();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM tb_lembur");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_lembur"),
                    rs.getString("id_karyawan"),
                    rs.getDate("tanggal_mulai"),
                    rs.getDate("tanggal_selesai"),
                    rs.getInt("jumlah_jam"),
                    formatAngka(rs.getDouble("upah_per_jam")),
                    formatAngka(rs.getDouble("total_lembur"))
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load data: " + e.getMessage());
        }
    }

    private void clearForm() {
        txtIdLembur.setText("");
        if (cmbIdKaryawan.getItemCount() > 0) cmbIdKaryawan.setSelectedIndex(0);
        dateMulai.setDate(null);
        dateSelesai.setDate(null);
        txtJumlahJam.setText("");
        txtUpahPerJam.setText("");
        txtTotalLembur.setText("");
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jLabel1    = new javax.swing.JLabel();
        jLabel2    = new javax.swing.JLabel();
        jLabel3    = new javax.swing.JLabel();
        jLabel4    = new javax.swing.JLabel();
        jLabel5    = new javax.swing.JLabel();
        jLabel6    = new javax.swing.JLabel();
        jLabel7    = new javax.swing.JLabel();
        jLabelJam  = new javax.swing.JLabel();

        txtIdLembur    = new javax.swing.JTextField();
        txtJumlahJam   = new javax.swing.JTextField();
        txtUpahPerJam  = new javax.swing.JTextField();
        txtTotalLembur = new javax.swing.JTextField();
        dateMulai      = new com.toedter.calendar.JDateChooser();
        dateSelesai    = new com.toedter.calendar.JDateChooser();
        cmbIdKaryawan  = new javax.swing.JComboBox<>();

        txtTotalLembur.setEditable(false);

        txtJumlahJam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { hitungTotalLembur(); }
        });
        txtUpahPerJam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { hitungTotalLembur(); }
        });

        btnSave   = new javax.swing.JButton();
        btnReset  = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnExit   = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLembur    = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Form Lembur");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16));
        jLabel1.setText("DATA LEMBUR");
        jLabel2.setText("Id. Lembur");
        jLabel3.setText("Id Karyawan");
        jLabel4.setText("Tanggal Mulai");
        jLabel5.setText("Tanggal Selesai");
        jLabel6.setText("Upah per Jam (Rp)");
        jLabel7.setText("Total Lembur (Rp)");
        jLabelJam.setText("Jumlah Jam");

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

        tblLembur.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{"ID Lembur", "ID Karyawan", "Tgl Mulai", "Tgl Selesai",
                         "Jumlah Jam", "Upah/Jam", "Total Lembur"}
        ));
        tblLembur.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { tblLemburMouseClicked(evt); }
        });
        jScrollPane1.setViewportView(tblLembur);

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
                            .addComponent(jLabelJam))
                        .addGap(20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtIdLembur,   javax.swing.GroupLayout.DEFAULT_SIZE, W, Short.MAX_VALUE)
                            .addComponent(cmbIdKaryawan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dateMulai,     javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtJumlahJam))
                        .addGap(40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addGap(20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dateSelesai,    javax.swing.GroupLayout.DEFAULT_SIZE, W, Short.MAX_VALUE)
                            .addComponent(txtUpahPerJam)
                            .addComponent(txtTotalLembur)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSave,   javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                        .addComponent(btnReset,  javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                        .addComponent(btnExit,   javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 750, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(txtIdLembur,   javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(dateSelesai,   javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cmbIdKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtUpahPerJam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(dateMulai,     javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtTotalLembur,javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelJam)
                    .addComponent(txtJumlahJam,  javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            if (dateMulai.getDate() == null || dateSelesai.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Tanggal mulai dan selesai harus diisi!", "Perhatian", JOptionPane.WARNING_MESSAGE);
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int jumlahJam = Integer.parseInt(txtJumlahJam.getText().trim().isEmpty() ? "0" : txtJumlahJam.getText().trim());
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO tb_lembur (id_lembur, id_karyawan, tanggal_mulai, tanggal_selesai, " +
                "jumlah_jam, upah_per_jam, total_lembur) VALUES (?,?,?,?,?,?,?)");
            ps.setString(1, txtIdLembur.getText());
            ps.setString(2, getIdKaryawan());
            ps.setString(3, sdf.format(dateMulai.getDate()));
            ps.setString(4, sdf.format(dateSelesai.getDate()));
            ps.setInt(5,    jumlahJam);
            ps.setDouble(6, parseAngka(txtUpahPerJam.getText()));
            ps.setDouble(7, parseAngka(txtTotalLembur.getText()));
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
            if (dateMulai.getDate() == null || dateSelesai.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Tanggal mulai dan selesai harus diisi!", "Perhatian", JOptionPane.WARNING_MESSAGE);
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int jumlahJam = Integer.parseInt(txtJumlahJam.getText().trim().isEmpty() ? "0" : txtJumlahJam.getText().trim());
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE tb_lembur SET id_karyawan=?, tanggal_mulai=?, tanggal_selesai=?, " +
                "jumlah_jam=?, upah_per_jam=?, total_lembur=? WHERE id_lembur=?");
            ps.setString(1, getIdKaryawan());
            ps.setString(2, sdf.format(dateMulai.getDate()));
            ps.setString(3, sdf.format(dateSelesai.getDate()));
            ps.setInt(4,    jumlahJam);
            ps.setDouble(5, parseAngka(txtUpahPerJam.getText()));
            ps.setDouble(6, parseAngka(txtTotalLembur.getText()));
            ps.setString(7, txtIdLembur.getText());
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
                PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_lembur WHERE id_lembur=?");
                ps.setString(1, txtIdLembur.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                clearForm(); loadData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) { this.dispose(); }

    private void tblLemburMouseClicked(java.awt.event.MouseEvent evt) {
        try {
            int row = tblLembur.getSelectedRow();
            txtIdLembur.setText(model.getValueAt(row, 0).toString());
            String idKar = model.getValueAt(row, 1).toString();
            for (int i = 0; i < cmbIdKaryawan.getItemCount(); i++) {
                if (cmbIdKaryawan.getItemAt(i).toString().startsWith(idKar + " - ")) {
                    cmbIdKaryawan.setSelectedIndex(i); break;
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateMulai.setDate(sdf.parse(model.getValueAt(row, 2).toString()));
            dateSelesai.setDate(sdf.parse(model.getValueAt(row, 3).toString()));
            txtJumlahJam.setText(model.getValueAt(row, 4).toString());
            txtUpahPerJam.setText(model.getValueAt(row, 5).toString().replace(".", ""));
            txtTotalLembur.setText(model.getValueAt(row, 6).toString().replace(".", ""));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private javax.swing.JButton btnDelete, btnExit, btnReset, btnSave, btnUpdate;
    private javax.swing.JComboBox<String> cmbIdKaryawan;
    private com.toedter.calendar.JDateChooser dateMulai, dateSelesai;
    private javax.swing.JLabel jLabel1, jLabel2, jLabel3, jLabel4,
                               jLabel5, jLabel6, jLabel7, jLabelJam;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblLembur;
    private javax.swing.JTextField txtIdLembur, txtJumlahJam, txtUpahPerJam, txtTotalLembur;
}