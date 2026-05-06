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

    public FormPenggajian() {
        initComponents();
        setAppIcon();
        setLocationRelativeTo(null);
        String[] kolom = {"ID Gaji", "Tanggal", "ID Karyawan", "Nama", "Golongan",
                          "Jumlah Gaji", "Jumlah Lembur", "Potongan", "Tgl Gaji", "Total Gaji"};
        model = new DefaultTableModel(kolom, 0);
        tblPenggajian.setModel(model);
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

    // FORMAT ANGKA: 5000000 -> 5.000.000
    private String formatAngka(double angka) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
        nf.setMaximumFractionDigits(0);
        return nf.format(angka);
    }

    // Hapus titik sebelum parse ke double
    private double parseAngka(String teks) {
        if (teks == null || teks.isEmpty()) return 0;
        return Double.parseDouble(teks.replace(".", "").replace(",", ""));
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

    private void onKaryawanSelected() {
        String idKar = getIdKaryawan();
        if (idKar.isEmpty()) return;
        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = "SELECT k.nama, g.nama_golongan, " +
                         "(g.gaji_pokok + g.tunjangan_istri + g.tunjangan_anak + g.transport + g.uang_makan) AS total_komponen " +
                         "FROM tb_karyawan k JOIN tb_golongan g ON k.id_golongan = g.id_golongan WHERE k.id_karyawan = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, idKar);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtNamaKaryawan.setText(rs.getString("nama"));
                txtGolongan.setText(rs.getString("nama_golongan"));
                txtJumlahGaji.setText(formatAngka(rs.getDouble("total_komponen")));
            }
            PreparedStatement psL = conn.prepareStatement(
                "SELECT COALESCE(SUM(jumlah), 0) AS total_lembur FROM tb_lembur WHERE id_karyawan = ?");
            psL.setString(1, idKar);
            ResultSet rsL = psL.executeQuery();
            if (rsL.next()) {
                txtJumlahLembur.setText(formatAngka(rsL.getDouble("total_lembur")));
            }
            hitungTotal();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load data karyawan: " + e.getMessage());
        }
    }

    private void hitungTotal() {
        try {
            double gaji   = parseAngka(txtJumlahGaji.getText());
            double lembur = parseAngka(txtJumlahLembur.getText());
            double potong = parseAngka(txtPotongan.getText());
            double total  = gaji + lembur - potong;
            txtTotalGaji.setText(formatAngka(total));
        } catch (NumberFormatException ex) {
            // biarkan user selesai mengetik
        }
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
                    formatAngka(rs.getDouble("jumlah_gaji")),    // <-- DIFORMAT
                    formatAngka(rs.getDouble("jumlah_lembur")),  // <-- DIFORMAT
                    formatAngka(rs.getDouble("potongan")),       // <-- DIFORMAT
                    rs.getDate("tanggal_gaji"),
                    formatAngka(rs.getDouble("total_gaji"))      // <-- DIFORMAT
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
        txtJumlahGaji.setText("");
        txtJumlahLembur.setText("");
        txtPotongan.setText("");
        dateTanggalGaji.setDate(null);
        txtTotalGaji.setText("");
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

        txtIdGaji       = new javax.swing.JTextField();
        txtNamaKaryawan = new javax.swing.JTextField();
        txtGolongan     = new javax.swing.JTextField();
        txtJumlahGaji   = new javax.swing.JTextField();
        txtJumlahLembur = new javax.swing.JTextField();
        txtPotongan     = new javax.swing.JTextField();
        txtTotalGaji    = new javax.swing.JTextField();
        dateTanggal     = new com.toedter.calendar.JDateChooser();
        dateTanggalGaji = new com.toedter.calendar.JDateChooser();

        txtNamaKaryawan.setEditable(false);
        txtGolongan.setEditable(false);
        txtTotalGaji.setEditable(false);

        cmbIdKaryawan = new javax.swing.JComboBox<>();
        cmbIdKaryawan.addActionListener(evt -> onKaryawanSelected());

        txtPotongan.addKeyListener(new java.awt.event.KeyAdapter() {
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
        jLabel7.setText("Jumlah Gaji");
        jLabel8.setText("Jumlah Lembur");
        jLabel9.setText("Potongan");
        jLabel10.setText("Tanggal Gaji");
        jLabelTotal = new javax.swing.JLabel("Total Gaji");

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

        tblPenggajian.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{"ID Gaji", "Tanggal", "ID Karyawan", "Nama", "Golongan",
                         "Jumlah Gaji", "Jumlah Lembur", "Potongan", "Tgl Gaji", "Total Gaji"}
        ));
        tblPenggajian.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { tblPenggajianMouseClicked(evt); }
        });
        jScrollPane1.setViewportView(tblPenggajian);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2).addComponent(jLabel3).addComponent(jLabel4)
                            .addComponent(jLabel5).addComponent(jLabel6).addComponent(jLabel7)
                            .addComponent(jLabel8).addComponent(jLabel9).addComponent(jLabel10)
                            .addComponent(jLabelTotal))
                        .addGap(30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtIdGaji, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                            .addComponent(dateTanggal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbIdKaryawan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtNamaKaryawan).addComponent(txtGolongan)
                            .addComponent(txtJumlahGaji).addComponent(txtJumlahLembur)
                            .addComponent(txtPotongan)
                            .addComponent(dateTanggalGaji, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtTotalGaji)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10)
                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20).addComponent(jLabel1).addGap(20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2).addComponent(txtIdGaji, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3).addComponent(dateTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4).addComponent(cmbIdKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5).addComponent(txtNamaKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6).addComponent(txtGolongan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7).addComponent(txtJumlahGaji, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8).addComponent(txtJumlahLembur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9).addComponent(txtPotongan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10).addComponent(dateTanggalGaji, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTotal).addComponent(txtTotalGaji, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO tb_penggajian VALUES (?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, txtIdGaji.getText());
            ps.setString(2, tanggal);
            ps.setString(3, getIdKaryawan());
            ps.setString(4, txtNamaKaryawan.getText());
            ps.setString(5, txtGolongan.getText());
            ps.setDouble(6, parseAngka(txtJumlahGaji.getText()));
            ps.setDouble(7, parseAngka(txtJumlahLembur.getText()));
            ps.setDouble(8, parseAngka(txtPotongan.getText()));
            ps.setString(9, tanggalGaji);
            ps.setDouble(10, parseAngka(txtTotalGaji.getText()));
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
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE tb_penggajian SET tanggal=?, id_karyawan=?, nama_karyawan=?, golongan=?, " +
                "jumlah_gaji=?, jumlah_lembur=?, potongan=?, tanggal_gaji=?, total_gaji=? WHERE id_gaji=?");
            ps.setString(1, tanggal);
            ps.setString(2, getIdKaryawan());
            ps.setString(3, txtNamaKaryawan.getText());
            ps.setString(4, txtGolongan.getText());
            ps.setDouble(5, parseAngka(txtJumlahGaji.getText()));
            ps.setDouble(6, parseAngka(txtJumlahLembur.getText()));
            ps.setDouble(7, parseAngka(txtPotongan.getText()));
            ps.setString(8, tanggalGaji);
            ps.setDouble(9, parseAngka(txtTotalGaji.getText()));
            ps.setString(10, txtIdGaji.getText());
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
            // Hapus titik agar bisa diedit
            txtJumlahGaji.setText(model.getValueAt(row, 5).toString().replace(".", ""));
            txtJumlahLembur.setText(model.getValueAt(row, 6).toString().replace(".", ""));
            txtPotongan.setText(model.getValueAt(row, 7).toString().replace(".", ""));
            dateTanggalGaji.setDate(sdf.parse(model.getValueAt(row, 8).toString()));
            txtTotalGaji.setText(model.getValueAt(row, 9).toString().replace(".", ""));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private javax.swing.JButton btnDelete, btnExit, btnReset, btnSave, btnUpdate;
    private javax.swing.JComboBox<String> cmbIdKaryawan;
    private com.toedter.calendar.JDateChooser dateTanggal, dateTanggalGaji;
    private javax.swing.JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5,
                               jLabel6, jLabel7, jLabel8, jLabel9, jLabel10, jLabelTotal;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblPenggajian;
    private javax.swing.JTextField txtIdGaji, txtNamaKaryawan, txtGolongan,
                                   txtJumlahGaji, txtJumlahLembur, txtPotongan, txtTotalGaji;
}