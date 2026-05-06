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
        String[] kolom = {"ID Lembur", "ID Karyawan", "Tanggal Lembur", "Jumlah"};
        model = new DefaultTableModel(kolom, 0);
        tblLembur.setModel(model);
        loadKaryawan();
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

    // FORMAT ANGKA: 10000 -> 10.000
    private String formatAngka(long angka) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
        return nf.format(angka);
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
                    rs.getDate("tanggal_lembur"),
                    formatAngka(rs.getLong("jumlah"))   // <-- DIFORMAT
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load data: " + e.getMessage());
        }
    }

    private void clearForm() {
        txtIdLembur.setText("");
        if (cmbIdKaryawan.getItemCount() > 0) cmbIdKaryawan.setSelectedIndex(0);
        dateChooser.setDate(null);
        txtJumlah.setText("");
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtIdLembur   = new javax.swing.JTextField();
        txtJumlah     = new javax.swing.JTextField();
        dateChooser   = new com.toedter.calendar.JDateChooser();
        cmbIdKaryawan = new javax.swing.JComboBox<>();
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
        jLabel4.setText("Tanggal Lembur");
        jLabel5.setText("Jumlah");

        btnSave.setText("Save");
        btnSave.addActionListener(evt -> btnSaveActionPerformed(evt));
        btnReset.setText("Reset");
        btnReset.addActionListener(evt -> btnResetActionPerformed(evt));
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(evt -> btnUpdateActionPerformed(evt));
        btnDelete.setText("Delete");
        btnDelete.addActionListener(evt -> btnDeleteActionPerformed(evt));
        btnExit.setText("Exit");
        btnExit.addActionListener(evt -> btnExitActionPerformed(evt));

        tblLembur.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][]{}, new String[]{"ID Lembur", "ID Karyawan", "Tanggal", "Jumlah"}
        ));
        tblLembur.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { tblLemburMouseClicked(evt); }
        });
        jScrollPane1.setViewportView(tblLembur);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2).addComponent(jLabel3).addComponent(jLabel4).addComponent(jLabel5))
                        .addGap(30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtIdLembur, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                            .addComponent(cmbIdKaryawan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dateChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtJumlah)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10)
                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20).addComponent(jLabel1).addGap(20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2).addComponent(txtIdLembur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3).addComponent(cmbIdKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4).addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5).addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            String tanggalLembur = sdf.format(dateChooser.getDate());
            String jumlahStr = txtJumlah.getText().replace(".", "");  // hapus titik
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO tb_lembur VALUES (?,?,?,?)");
            ps.setString(1, txtIdLembur.getText());
            ps.setString(2, getIdKaryawan());
            ps.setString(3, tanggalLembur);
            ps.setInt(4, Integer.parseInt(jumlahStr));
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
            String tanggalLembur = sdf.format(dateChooser.getDate());
            String jumlahStr = txtJumlah.getText().replace(".", "");  // hapus titik
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE tb_lembur SET id_karyawan=?, tanggal_lembur=?, jumlah=? WHERE id_lembur=?");
            ps.setString(1, getIdKaryawan());
            ps.setString(2, tanggalLembur);
            ps.setInt(3, Integer.parseInt(jumlahStr));
            ps.setString(4, txtIdLembur.getText());
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
            dateChooser.setDate(sdf.parse(model.getValueAt(row, 2).toString()));
            // Hapus titik agar bisa diedit
            txtJumlah.setText(model.getValueAt(row, 3).toString().replace(".", ""));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private javax.swing.JButton btnDelete, btnExit, btnReset, btnSave, btnUpdate;
    private javax.swing.JComboBox<String> cmbIdKaryawan;
    private com.toedter.calendar.JDateChooser dateChooser;
    private javax.swing.JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblLembur;
    private javax.swing.JTextField txtIdLembur, txtJumlah;
}