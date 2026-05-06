package form;

import javax.swing.*;

public class FormPilihan extends javax.swing.JFrame {
    
    public FormPilihan() {
        initComponents();
        setAppIcon();
        setLocationRelativeTo(null);
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
    
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        btnKaryawan = new javax.swing.JButton();
        btnGolongan = new javax.swing.JButton();
        btnLembur = new javax.swing.JButton();
        btnPenggajian = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Menu Utama");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18));
        jLabel1.setText("SISTEM PENGGAJIAN KARYAWAN");

        btnKaryawan.setText("Form Karyawan");
        btnKaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKaryawanActionPerformed(evt);
            }
        });

        btnGolongan.setText("Form Golongan");
        btnGolongan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGolonganActionPerformed(evt);
            }
        });

        btnLembur.setText("Form Lembur");
        btnLembur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLemburActionPerformed(evt);
            }
        });

        btnPenggajian.setText("Form Penggajian");
        btnPenggajian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPenggajianActionPerformed(evt);
            }
        });

        btnExit.setText("Exit");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(btnKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGolongan, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLembur, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPenggajian, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1)
                .addGap(30, 30, 30)
                .addComponent(btnKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(btnGolongan, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(btnLembur, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(btnPenggajian, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }

    private void btnKaryawanActionPerformed(java.awt.event.ActionEvent evt) {
        new FormKaryawan().setVisible(true);
    }

    private void btnGolonganActionPerformed(java.awt.event.ActionEvent evt) {
        new FormGolongan().setVisible(true);
    }

    private void btnLemburActionPerformed(java.awt.event.ActionEvent evt) {
        new FormLembur().setVisible(true);
    }

    private void btnPenggajianActionPerformed(java.awt.event.ActionEvent evt) {
        new FormPenggajian().setVisible(true);
    }

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
    }

    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnGolongan;
    private javax.swing.JButton btnKaryawan;
    private javax.swing.JButton btnLembur;
    private javax.swing.JButton btnPenggajian;
    private javax.swing.JLabel jLabel1;
}