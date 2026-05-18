package form;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import Koneksi.Koneksi;
import java.text.NumberFormat;
import java.util.Locale;

public class FormGolongan extends javax.swing.JFrame {

    private DefaultTableModel model;

    public FormGolongan() {
        initComponents();
        setAppIcon();
        setLocationRelativeTo(null);

        String[] kolom = {"ID Golongan", "Nama Golongan", "Gaji Pokok", "Transport", "Uang Makan"};
        model = new DefaultTableModel(kolom, 0);
        tblGolongan.setModel(model);

        loadData();
    }

    private void setAppIcon() {
        try {
            java.net.URL imgURL = getClass().getResource("/foto/icon.png");
            if (imgURL != null) {
                setIconImage(new ImageIcon(imgURL).getImage());
            } else {
                System.err.println("Icon tidak ditemukan: /foto/icon.png");
            }
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

    private String formatAngka(double angka) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
        nf.setMaximumFractionDigits(0);
        return nf.format(angka);
    }

    private double parseAngka(String teks) {
        if (teks == null || teks.trim().isEmpty()) return 0;
        return Double.parseDouble(teks.trim().replace(".", "").replace(",", ""));
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = "SELECT id_golongan, nama_golongan, gaji_pokok, transport, uang_makan FROM tb_golongan";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_golongan"),
                    rs.getString("nama_golongan"),
                    formatAngka(rs.getDouble("gaji_pokok")),
                    formatAngka(rs.getDouble("transport")),
                    formatAngka(rs.getDouble("uang_makan"))
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load data: " + e.getMessage());
        }
    }

    private void clearForm() {
        txtIdGolongan.setText("");
        txtNamaGolongan.setText("");
        txtGajiPokok.setText("");
        txtTransport.setText("");
        txtUangMakan.setText("");
    }

    private void applyButtonIcon(JButton btn, String iconFile) {
        ImageIcon icon = createIconForButton(iconFile);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(5);
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        txtIdGolongan   = new javax.swing.JTextField();
        txtNamaGolongan = new javax.swing.JTextField();
        txtGajiPokok    = new javax.swing.JTextField();
        txtTransport    = new javax.swing.JTextField();
        txtUangMakan    = new javax.swing.JTextField();

        btnSave   = new javax.swing.JButton();
        btnReset  = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnExit   = new javax.swing.JButton();

        jScrollPane1 = new javax.swing.JScrollPane();
        tblGolongan  = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Form Golongan");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16));
        jLabel1.setText("DATA GOLONGAN");

        jLabel2.setText("Id. Golongan");
        jLabel3.setText("Nama Golongan");
        jLabel4.setText("Gaji Pokok");
        jLabel5.setText("Transport");
        jLabel6.setText("Uang Makan");

        btnSave.setText("Save");
        applyButtonIcon(btnSave, "save.png");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnSaveActionPerformed(evt); }
        });

        btnReset.setText("Reset");
        applyButtonIcon(btnReset, "reset.png");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnResetActionPerformed(evt); }
        });

        btnUpdate.setText("Update");
        applyButtonIcon(btnUpdate, "update.png");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnUpdateActionPerformed(evt); }
        });

        btnDelete.setText("Delete");
        applyButtonIcon(btnDelete, "delete.png");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnDeleteActionPerformed(evt); }
        });

        btnExit.setText("Exit");
        applyButtonIcon(btnExit, "exit.png");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnExitActionPerformed(evt); }
        });

        tblGolongan.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{"ID Golongan", "Nama Golongan", "Gaji Pokok", "Transport", "Uang Makan"}
        ));
        tblGolongan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { tblGolonganMouseClicked(evt); }
        });
        jScrollPane1.setViewportView(tblGolongan);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        int W = 200;

        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtIdGolongan,   javax.swing.GroupLayout.DEFAULT_SIZE, W, Short.MAX_VALUE)
                            .addComponent(txtNamaGolongan))
                        .addGap(50)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGap(30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtGajiPokok, javax.swing.GroupLayout.DEFAULT_SIZE, W, Short.MAX_VALUE)
                            .addComponent(txtTransport)
                            .addComponent(txtUangMakan)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSave,   javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                        .addComponent(btnReset,  javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                        .addComponent(btnExit,   javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(txtIdGolongan,   javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtGajiPokok,    javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtNamaGolongan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtTransport,    javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtUangMakan,    javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            Connection conn = Koneksi.getKoneksi();
            String sql = "INSERT INTO tb_golongan " +
                         "(id_golongan, nama_golongan, gaji_pokok, tunjangan_istri, jumlah_anak, tunjangan_anak, transport, uang_makan) " +
                         "VALUES (?,?,?,0,0,0,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtIdGolongan.getText());
            ps.setString(2, txtNamaGolongan.getText());
            ps.setDouble(3, parseAngka(txtGajiPokok.getText()));
            ps.setDouble(4, parseAngka(txtTransport.getText()));
            ps.setDouble(5, parseAngka(txtUangMakan.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
            clearForm(); loadData();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) { clearForm(); }

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = "UPDATE tb_golongan SET nama_golongan=?, gaji_pokok=?, transport=?, uang_makan=? " +
                         "WHERE id_golongan=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtNamaGolongan.getText());
            ps.setDouble(2, parseAngka(txtGajiPokok.getText()));
            ps.setDouble(3, parseAngka(txtTransport.getText()));
            ps.setDouble(4, parseAngka(txtUangMakan.getText()));
            ps.setString(5, txtIdGolongan.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
            clearForm(); loadData();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus data?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = Koneksi.getKoneksi();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_golongan WHERE id_golongan=?");
                ps.setString(1, txtIdGolongan.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                clearForm(); loadData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) { this.dispose(); }

    private void tblGolonganMouseClicked(java.awt.event.MouseEvent evt) {
        int row = tblGolongan.getSelectedRow();
        txtIdGolongan.setText(model.getValueAt(row, 0).toString());
        txtNamaGolongan.setText(model.getValueAt(row, 1).toString());
        txtGajiPokok.setText(model.getValueAt(row, 2).toString().replace(".", ""));
        txtTransport.setText(model.getValueAt(row, 3).toString().replace(".", ""));
        txtUangMakan.setText(model.getValueAt(row, 4).toString().replace(".", ""));
    }

    private javax.swing.JButton btnDelete, btnExit, btnReset, btnSave, btnUpdate;
    private javax.swing.JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblGolongan;
    private javax.swing.JTextField txtIdGolongan, txtNamaGolongan,
                                   txtGajiPokok, txtTransport, txtUangMakan;
}