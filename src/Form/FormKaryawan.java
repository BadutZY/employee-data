package form;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import Koneksi.Koneksi;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;

public class FormKaryawan extends javax.swing.JFrame {

    private DefaultTableModel model;

    public FormKaryawan() {
        initComponents();
        setAppIcon();
        setLocationRelativeTo(null);

        String[] kolom = {"ID Karyawan", "Nama", "Alamat", "Jenis Kelamin",
                          "Tempat Lahir", "Tanggal Lahir", "Status", "ID Golongan"};
        model = new DefaultTableModel(kolom, 0);
        tblKaryawan.setModel(model);

        loadGolongan();
        loadData();
    }

    private void setAppIcon() {
        try {
            java.net.URL imgURL = getClass().getResource("/foto/icon.png");
            if (imgURL != null) {
                setIconImage(new ImageIcon(imgURL).getImage());
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

    private void applyButtonIcon(JButton btn, String iconFile) {
        ImageIcon icon = createIconForButton(iconFile);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(5);
        }
    }

    private void loadGolongan() {
        cmbGolongan.removeAllItems();
        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = "SELECT id_golongan, nama_golongan FROM tb_golongan ORDER BY id_golongan";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                cmbGolongan.addItem(rs.getString("id_golongan") + " - " + rs.getString("nama_golongan"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load golongan: " + e.getMessage());
        }
    }

    private String getIdGolongan() {
        if (cmbGolongan.getSelectedItem() == null) return "";
        return cmbGolongan.getSelectedItem().toString().split(" - ")[0].trim();
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            Connection conn = Koneksi.getKoneksi();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM tb_karyawan");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_karyawan"),
                    rs.getString("nama"),
                    rs.getString("alamat"),
                    rs.getString("jenis_kelamin"),
                    rs.getString("tempat_lahir"),
                    rs.getDate("tanggal_lahir"),
                    rs.getString("status"),
                    rs.getString("id_golongan")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load data: " + e.getMessage());
        }
    }

    private void clearForm() {
        txtIdKaryawan.setText("");
        txtNama.setText("");
        txtAlamat.setText("");
        if (cmbJenisKelamin.getItemCount() > 0) cmbJenisKelamin.setSelectedIndex(0);
        txtTempatLahir.setText("");
        dateChooser.setDate(null);
        if (cmbStatus.getItemCount() > 0) cmbStatus.setSelectedIndex(0);
        if (cmbGolongan.getItemCount() > 0) cmbGolongan.setSelectedIndex(0);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        txtIdKaryawan  = new javax.swing.JTextField();
        txtNama        = new javax.swing.JTextField();
        txtAlamat      = new javax.swing.JTextField();
        txtTempatLahir = new javax.swing.JTextField();
        dateChooser    = new com.toedter.calendar.JDateChooser();

        cmbJenisKelamin = new javax.swing.JComboBox<>(new String[]{"Laki-laki", "Perempuan"});
        cmbStatus       = new javax.swing.JComboBox<>(new String[]{"Tidak Menikah", "Menikah", "Cerai"});
        cmbGolongan     = new javax.swing.JComboBox<>();

        btnSave   = new javax.swing.JButton();
        btnReset  = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnExit   = new javax.swing.JButton();

        jScrollPane1 = new javax.swing.JScrollPane();
        tblKaryawan  = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Form Karyawan");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16));
        jLabel1.setText("DATA KARYAWAN");

        jLabel2.setText("Id. Karyawan");
        jLabel3.setText("Nama");
        jLabel4.setText("Alamat");
        jLabel5.setText("Jenis Kelamin");
        jLabel6.setText("Tempat Lahir");
        jLabel7.setText("Tanggal Lahir");
        jLabel8.setText("Status");
        jLabel9.setText("Golongan");

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

        tblKaryawan.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{"ID Karyawan", "Nama", "Alamat", "Jenis Kelamin",
                         "Tempat Lahir", "Tanggal Lahir", "Status", "ID Golongan"}
        ));
        tblKaryawan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKaryawanMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblKaryawan);

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
                            .addComponent(jLabel5))
                        .addGap(20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtIdKaryawan,    javax.swing.GroupLayout.DEFAULT_SIZE, W, Short.MAX_VALUE)
                            .addComponent(txtNama)
                            .addComponent(txtAlamat)
                            .addComponent(cmbJenisKelamin,  0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addGap(20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTempatLahir,   javax.swing.GroupLayout.DEFAULT_SIZE, W, Short.MAX_VALUE)
                            .addComponent(dateChooser,      javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbStatus,        0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbGolongan,      0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSave,   javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                        .addComponent(btnReset,  javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                        .addComponent(btnExit,   javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(txtIdKaryawan,  javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtTempatLahir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtNama,        javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(dateChooser,    javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtAlamat,      javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(cmbStatus,      javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cmbJenisKelamin,javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(cmbGolongan,    javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            String tglLahir = sdf.format(dateChooser.getDate());
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO tb_karyawan VALUES (?,?,?,?,?,?,?,?)");
            ps.setString(1, txtIdKaryawan.getText());
            ps.setString(2, txtNama.getText());
            ps.setString(3, txtAlamat.getText());
            ps.setString(4, cmbJenisKelamin.getSelectedItem().toString());
            ps.setString(5, txtTempatLahir.getText());
            ps.setString(6, tglLahir);
            ps.setString(7, cmbStatus.getSelectedItem().toString());
            ps.setString(8, getIdGolongan());
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
            String tglLahir = sdf.format(dateChooser.getDate());
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE tb_karyawan SET nama=?, alamat=?, jenis_kelamin=?, tempat_lahir=?, " +
                "tanggal_lahir=?, status=?, id_golongan=? WHERE id_karyawan=?");
            ps.setString(1, txtNama.getText());
            ps.setString(2, txtAlamat.getText());
            ps.setString(3, cmbJenisKelamin.getSelectedItem().toString());
            ps.setString(4, txtTempatLahir.getText());
            ps.setString(5, tglLahir);
            ps.setString(6, cmbStatus.getSelectedItem().toString());
            ps.setString(7, getIdGolongan());
            ps.setString(8, txtIdKaryawan.getText());
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
                PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_karyawan WHERE id_karyawan=?");
                ps.setString(1, txtIdKaryawan.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                clearForm(); loadData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) { this.dispose(); }

    private void tblKaryawanMouseClicked(java.awt.event.MouseEvent evt) {
        try {
            int row = tblKaryawan.getSelectedRow();
            txtIdKaryawan.setText(model.getValueAt(row, 0).toString());
            txtNama.setText(model.getValueAt(row, 1).toString());
            txtAlamat.setText(model.getValueAt(row, 2).toString());
            cmbJenisKelamin.setSelectedItem(model.getValueAt(row, 3).toString());
            txtTempatLahir.setText(model.getValueAt(row, 4).toString());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateChooser.setDate(sdf.parse(model.getValueAt(row, 5).toString()));
            cmbStatus.setSelectedItem(model.getValueAt(row, 6).toString());
            String idGol = model.getValueAt(row, 7).toString();
            for (int i = 0; i < cmbGolongan.getItemCount(); i++) {
                if (cmbGolongan.getItemAt(i).toString().startsWith(idGol + " - ")) {
                    cmbGolongan.setSelectedIndex(i); break;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private javax.swing.JButton btnDelete, btnExit, btnReset, btnSave, btnUpdate;
    private javax.swing.JComboBox<String> cmbJenisKelamin, cmbStatus, cmbGolongan;
    private com.toedter.calendar.JDateChooser dateChooser;
    private javax.swing.JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5,
                               jLabel6, jLabel7, jLabel8, jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblKaryawan;
    private javax.swing.JTextField txtIdKaryawan, txtNama, txtAlamat, txtTempatLahir;
}