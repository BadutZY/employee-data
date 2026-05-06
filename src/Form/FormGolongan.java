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
        
        String[] kolom = {"ID Golongan", "Nama Golongan", "Gaji Pokok", "Tunj. Istri", 
                         "Jml Anak", "Tunj. Anak", "Transport", "Uang Makan"};
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

    // FORMAT ANGKA: 5000000 -> 5.000.000
    private String formatAngka(double angka) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
        nf.setMaximumFractionDigits(0);
        return nf.format(angka);
    }

    // Hapus titik sebelum parse ke double
    private double parseAngka(String teks) {
        return Double.parseDouble(teks.replace(".", "").replace(",", ""));
    }
    
    private void loadData() {
        model.setRowCount(0);
        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = "SELECT * FROM tb_golongan";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_golongan"),
                    rs.getString("nama_golongan"),
                    formatAngka(rs.getDouble("gaji_pokok")),
                    formatAngka(rs.getDouble("tunjangan_istri")),
                    rs.getInt("jumlah_anak"),
                    formatAngka(rs.getDouble("tunjangan_anak")),
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
        txtTunjanganIstri.setText("");
        txtJumlahAnak.setText("");
        txtTunjanganAnak.setText("");
        txtTransport.setText("");
        txtUangMakan.setText("");
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
        txtIdGolongan = new javax.swing.JTextField();
        txtNamaGolongan = new javax.swing.JTextField();
        txtGajiPokok = new javax.swing.JTextField();
        txtTunjanganIstri = new javax.swing.JTextField();
        txtJumlahAnak = new javax.swing.JTextField();
        txtTunjanganAnak = new javax.swing.JTextField();
        txtTransport = new javax.swing.JTextField();
        txtUangMakan = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblGolongan = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Form Golongan");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16));
        jLabel1.setText("DATA GOLONGAN");
        jLabel2.setText("Id. Golongan");
        jLabel3.setText("Nama Golongan");
        jLabel4.setText("Gaji Pokok");
        jLabel5.setText("Tunjangan Istri");
        jLabel6.setText("Jumlah Anak");
        jLabel7.setText("Tunjangan Anak");
        jLabel8.setText("Transport");
        jLabel9.setText("Uang Makan");

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnSaveActionPerformed(evt); }
        });
        btnReset.setText("Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnResetActionPerformed(evt); }
        });
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnUpdateActionPerformed(evt); }
        });
        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnDeleteActionPerformed(evt); }
        });
        btnExit.setText("Exit");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnExitActionPerformed(evt); }
        });

        tblGolongan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"ID", "Nama", "Gaji Pokok", "Tunj Istri", "Jml Anak", "Tunj Anak", "Transport", "Uang Makan"}
        ));
        tblGolongan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { tblGolonganMouseClicked(evt); }
        });
        jScrollPane1.setViewportView(tblGolongan);

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
                            .addComponent(txtIdGolongan, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addComponent(txtNamaGolongan).addComponent(txtGajiPokok).addComponent(txtTunjanganIstri))
                        .addGap(50)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6).addComponent(jLabel7).addComponent(jLabel8).addComponent(jLabel9))
                        .addGap(30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtJumlahAnak, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addComponent(txtTunjanganAnak).addComponent(txtTransport).addComponent(txtUangMakan)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10)
                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20).addComponent(jLabel1).addGap(20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtIdGolongan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtJumlahAnak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtNamaGolongan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtTunjanganAnak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtGajiPokok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtTransport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtTunjanganIstri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtUangMakan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            String sql = "INSERT INTO tb_golongan VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtIdGolongan.getText());
            ps.setString(2, txtNamaGolongan.getText());
            ps.setDouble(3, parseAngka(txtGajiPokok.getText()));
            ps.setDouble(4, parseAngka(txtTunjanganIstri.getText()));
            ps.setInt(5, Integer.parseInt(txtJumlahAnak.getText()));
            ps.setDouble(6, parseAngka(txtTunjanganAnak.getText()));
            ps.setDouble(7, parseAngka(txtTransport.getText()));
            ps.setDouble(8, parseAngka(txtUangMakan.getText()));
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
            String sql = "UPDATE tb_golongan SET nama_golongan=?, gaji_pokok=?, tunjangan_istri=?, " +
                        "jumlah_anak=?, tunjangan_anak=?, transport=?, uang_makan=? WHERE id_golongan=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtNamaGolongan.getText());
            ps.setDouble(2, parseAngka(txtGajiPokok.getText()));
            ps.setDouble(3, parseAngka(txtTunjanganIstri.getText()));
            ps.setInt(4, Integer.parseInt(txtJumlahAnak.getText()));
            ps.setDouble(5, parseAngka(txtTunjanganAnak.getText()));
            ps.setDouble(6, parseAngka(txtTransport.getText()));
            ps.setDouble(7, parseAngka(txtUangMakan.getText()));
            ps.setString(8, txtIdGolongan.getText());
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
        // Hapus titik agar bisa diedit langsung
        txtGajiPokok.setText(model.getValueAt(row, 2).toString().replace(".", ""));
        txtTunjanganIstri.setText(model.getValueAt(row, 3).toString().replace(".", ""));
        txtJumlahAnak.setText(model.getValueAt(row, 4).toString());
        txtTunjanganAnak.setText(model.getValueAt(row, 5).toString().replace(".", ""));
        txtTransport.setText(model.getValueAt(row, 6).toString().replace(".", ""));
        txtUangMakan.setText(model.getValueAt(row, 7).toString().replace(".", ""));
    }

    private javax.swing.JButton btnDelete, btnExit, btnReset, btnSave, btnUpdate;
    private javax.swing.JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5,
                               jLabel6, jLabel7, jLabel8, jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblGolongan;
    private javax.swing.JTextField txtGajiPokok, txtIdGolongan, txtJumlahAnak,
                                   txtNamaGolongan, txtTransport, txtTunjanganAnak,
                                   txtTunjanganIstri, txtUangMakan;
}