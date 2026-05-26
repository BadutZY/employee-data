package form;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import Koneksi.Koneksi;
import com.toedter.calendar.JDateChooser;

public class PanelKaryawan extends JPanel {

    private DefaultTableModel model;
    private JTextField txtId, txtNama, txtAlamat, txtTempatLahir;
    private JComboBox<String> cmbJK, cmbStatus, cmbGolongan;
    private JDateChooser dateChooser;
    private JTable table;
    private JLabel lblStatus;

    private PanelKaryawan() { super(new BorderLayout()); }

    public static PanelKaryawan create() {
        PanelKaryawan p = new PanelKaryawan();
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

        // Init
        loadGolongan();
        loadData();
    }

    /**
     * Called every time this panel is shown/added to the hierarchy.
     * Refreshes golongan combo so newly added golongan appear without restart.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(() -> {
            loadGolongan();
            loadData();
        });
    }

    /** Public method so MainDashboard can trigger refresh when switching to this panel. */
    public void refreshGolongan() {
        loadGolongan();
    }

    public void refresh() {
        loadGolongan();
        loadData();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(null);
        root.setOpaque(false);
        root.setPreferredSize(new Dimension(950, 760));

        // Icon header panel
        JPanel headerPanel = buildIconHeader("Data Karyawan", "Manajemen data karyawan perusahaan",
                "/foto/user.png", MainDashboard.C_INFO);
        headerPanel.setBounds(20, 14, 920, 76);
        root.add(headerPanel);

        // Status bar
        lblStatus = new JLabel("");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setBounds(30, 92, 700, 18);
        root.add(lblStatus);

        // Form card
        JPanel formCard = buildFormCard();
        formCard.setBounds(20, 112, 920, 330);
        root.add(formCard);

        // Buttons
        JPanel btnRow = buildButtonRow();
        btnRow.setBounds(20, 450, 920, 48);
        root.add(btnRow);

        // Table card
        JPanel tableCard = buildTableCard();
        tableCard.setBounds(20, 506, 920, 250);
        root.add(tableCard);

        return root;
    }

    private JPanel buildFormCard() {
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(MainDashboard.C_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                GradientPaint gp=new GradientPaint(0,0,new Color(99,102,241,20),getWidth(),0,new Color(0,0,0,0));
                g2.setPaint(gp);
                g2.fillRoundRect(0,0,getWidth(),3,14,14);
                g2.setColor(MainDashboard.C_BORDER);
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                g2.dispose();
            }
        };
        card.setOpaque(false);

        int COL1 = 30, COL2 = 240, COL3 = 490, COL4 = 700;
        int LH = 28, FH = 36, GAP = 8;
        int Y1 = 24, Y2 = Y1 + LH + FH + GAP + 4, Y3 = Y2 + LH + FH + GAP + 4, Y4 = Y3 + LH + FH + GAP + 4;

        // Labels
        addLbl(card, "ID Karyawan",    COL1, Y1);
        addLbl(card, "Tempat Lahir",   COL3, Y1);
        addLbl(card, "Nama Karyawan",  COL1, Y2);
        addLbl(card, "Tanggal Lahir",  COL3, Y2);
        addLbl(card, "Alamat",         COL1, Y3);
        addLbl(card, "Status",         COL3, Y3);
        addLbl(card, "Jenis Kelamin",  COL1, Y4);
        addLbl(card, "Golongan",       COL3, Y4);

        // Fields
        txtId          = makeField();
        txtNama        = makeField();
        txtAlamat      = makeField();
        txtTempatLahir = makeField();
        cmbJK          = makeCombo(new String[]{"Laki-laki","Perempuan"});
        cmbStatus      = makeCombo(new String[]{"Tidak Menikah","Menikah","Cerai"});
        cmbGolongan    = makeCombo(new String[]{});
        dateChooser    = new JDateChooser();
        styleDateChooser(dateChooser);

        int FW1 = 190, FW2 = 200;
        txtId.setBounds(COL1, Y1+LH, FW1, FH);
        txtTempatLahir.setBounds(COL3, Y1+LH, FW2, FH);
        txtNama.setBounds(COL1, Y2+LH, FW1, FH);
        dateChooser.setBounds(COL3, Y2+LH, FW2, FH);
        txtAlamat.setBounds(COL1, Y3+LH, FW1, FH);
        cmbStatus.setBounds(COL3, Y3+LH, FW2, FH);
        cmbJK.setBounds(COL1, Y4+LH, FW1, FH);
        cmbGolongan.setBounds(COL3, Y4+LH, FW2, FH);

        card.add(txtId); card.add(txtTempatLahir); card.add(txtNama);
        card.add(dateChooser); card.add(txtAlamat); card.add(cmbStatus);
        card.add(cmbJK); card.add(cmbGolongan);

        return card;
    }

    private JPanel buildButtonRow() {
        JPanel p = new JPanel(null);
        p.setOpaque(false);

        JButton btnSave   = makeBtn("Simpan",  MainDashboard.C_SUCCESS);
        JButton btnUpdate = makeBtn("Update",   MainDashboard.C_INFO);
        JButton btnDelete = makeBtn("Hapus",   MainDashboard.C_DANGER);
        JButton btnReset  = makeBtn("Reset",    MainDashboard.C_WARN);

        btnSave.setBounds(0, 4, 130, 40);
        btnUpdate.setBounds(140, 4, 130, 40);
        btnDelete.setBounds(280, 4, 130, 40);
        btnReset.setBounds(420, 4, 130, 40);

        btnSave.addActionListener(e -> doSave());
        btnUpdate.addActionListener(e -> doUpdate());
        btnDelete.addActionListener(e -> doDelete());
        btnReset.addActionListener(e -> clearForm());

        p.add(btnSave); p.add(btnUpdate); p.add(btnDelete); p.add(btnReset);
        return p;
    }

    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setColor(MainDashboard.C_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(MainDashboard.C_BORDER);
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] cols = {"ID","Nama","Alamat","JK","Tempat Lahir","Tgl Lahir","Status","Golongan"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = buildStyledTable(model);
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { fillFormFromTable(); }
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
    // DB OPERATIONS
    // =====================================================================
    private void loadGolongan() {
        cmbGolongan.removeAllItems();
        try {
            Connection c = Koneksi.getKoneksi();
            ResultSet rs = c.createStatement().executeQuery("SELECT id_golongan, nama_golongan FROM tb_golongan ORDER BY id_golongan");
            while (rs.next()) cmbGolongan.addItem(rs.getString("id_golongan") + " - " + rs.getString("nama_golongan"));
        } catch (Exception ignored) {}
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            Connection c = Koneksi.getKoneksi();
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM tb_karyawan");
            while (rs.next()) model.addRow(new Object[]{
                rs.getString("id_karyawan"), rs.getString("nama"), rs.getString("alamat"),
                rs.getString("jenis_kelamin"), rs.getString("tempat_lahir"),
                rs.getDate("tanggal_lahir"), rs.getString("status"), rs.getString("id_golongan")
            });
        } catch (Exception e) { showStatus("Error: " + e.getMessage(), MainDashboard.C_DANGER); }
    }

    private void doSave() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tgl = sdf.format(dateChooser.getDate());
            Connection c = Koneksi.getKoneksi();
            PreparedStatement ps = c.prepareStatement("INSERT INTO tb_karyawan VALUES (?,?,?,?,?,?,?,?)");
            ps.setString(1, txtId.getText());
            ps.setString(2, txtNama.getText());
            ps.setString(3, txtAlamat.getText());
            ps.setString(4, cmbJK.getSelectedItem().toString());
            ps.setString(5, txtTempatLahir.getText());
            ps.setString(6, tgl);
            ps.setString(7, cmbStatus.getSelectedItem().toString());
            ps.setString(8, getIdGolongan());
            ps.executeUpdate();
            showStatus("Data berhasil disimpan!", MainDashboard.C_SUCCESS);
            clearForm(); loadData();
        } catch (Exception e) { showStatus("Error: " + e.getMessage(), MainDashboard.C_DANGER); }
    }

    private void doUpdate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tgl = sdf.format(dateChooser.getDate());
            Connection c = Koneksi.getKoneksi();
            PreparedStatement ps = c.prepareStatement(
                "UPDATE tb_karyawan SET nama=?,alamat=?,jenis_kelamin=?,tempat_lahir=?,tanggal_lahir=?,status=?,id_golongan=? WHERE id_karyawan=?");
            ps.setString(1, txtNama.getText()); ps.setString(2, txtAlamat.getText());
            ps.setString(3, cmbJK.getSelectedItem().toString()); ps.setString(4, txtTempatLahir.getText());
            ps.setString(5, tgl); ps.setString(6, cmbStatus.getSelectedItem().toString());
            ps.setString(7, getIdGolongan()); ps.setString(8, txtId.getText());
            ps.executeUpdate();
            showStatus("Data berhasil diupdate!", MainDashboard.C_SUCCESS);
            clearForm(); loadData();
        } catch (Exception e) { showStatus("Error: " + e.getMessage(), MainDashboard.C_DANGER); }
    }

    private void doDelete() {
        if (JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement ps = Koneksi.getKoneksi().prepareStatement("DELETE FROM tb_karyawan WHERE id_karyawan=?");
                ps.setString(1, txtId.getText());
                ps.executeUpdate();
                showStatus("Data berhasil dihapus!", MainDashboard.C_SUCCESS);
                clearForm(); loadData();
            } catch (Exception e) { showStatus("Error: " + e.getMessage(), MainDashboard.C_DANGER); }
        }
    }

    private void clearForm() {
        txtId.setText(""); txtNama.setText(""); txtAlamat.setText(""); txtTempatLahir.setText("");
        cmbJK.setSelectedIndex(0); cmbStatus.setSelectedIndex(0);
        if (cmbGolongan.getItemCount() > 0) cmbGolongan.setSelectedIndex(0);
        dateChooser.setDate(null);
    }

    private void fillFormFromTable() {
        try {
            int row = table.getSelectedRow();
            txtId.setText(model.getValueAt(row,0).toString());
            txtNama.setText(model.getValueAt(row,1).toString());
            txtAlamat.setText(model.getValueAt(row,2).toString());
            cmbJK.setSelectedItem(model.getValueAt(row,3).toString());
            txtTempatLahir.setText(model.getValueAt(row,4).toString());
            dateChooser.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(model.getValueAt(row,5).toString()));
            cmbStatus.setSelectedItem(model.getValueAt(row,6).toString());
            String idGol = model.getValueAt(row,7).toString();
            for (int i=0;i<cmbGolongan.getItemCount();i++)
                if (cmbGolongan.getItemAt(i).startsWith(idGol+" - ")) { cmbGolongan.setSelectedIndex(i); break; }
        } catch (Exception e) { showStatus("Error: "+e.getMessage(), MainDashboard.C_DANGER); }
    }

    private String getIdGolongan() {
        if (cmbGolongan.getSelectedItem()==null) return "";
        return cmbGolongan.getSelectedItem().toString().split(" - ")[0].trim();
    }

    private void showStatus(String msg, Color color) {
        lblStatus.setText(msg);
        lblStatus.setForeground(color);
    }

    // =====================================================================
    // HELPERS
    // =====================================================================
    private void addLbl(JPanel p, String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(MainDashboard.C_TEXT_MUTED);
        l.setBounds(x, y, 200, 18);
        p.add(l);
    }

    private JTextField makeField() {
        JTextField f = new JTextField() {
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hasFocus()?MainDashboard.C_ACCENT:MainDashboard.C_BORDER);
                g2.setStroke(new BasicStroke(hasFocus()?1.5f:1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                g2.dispose();
            }
        };
        f.setBackground(MainDashboard.C_FIELD);
        f.setForeground(MainDashboard.C_TEXT);
        f.setCaretColor(MainDashboard.C_ACCENT);
        f.setFont(new Font("Segoe UI",Font.PLAIN,12));
        f.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
        f.setOpaque(true);
        f.addFocusListener(new FocusAdapter(){
            @Override public void focusGained(FocusEvent e){f.repaint();}
            @Override public void focusLost(FocusEvent e){f.repaint();}
        });
        return f;
    }

    private JComboBox<String> makeCombo(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setBackground(MainDashboard.C_FIELD);
        c.setForeground(MainDashboard.C_TEXT);
        c.setFont(new Font("Segoe UI",Font.PLAIN,12));
        c.setBorder(BorderFactory.createLineBorder(MainDashboard.C_BORDER,1));
        return c;
    }

    // =====================================================================
    // ICON HEADER BUILDER (shared pattern for all panels)
    // =====================================================================
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

        // Icon circle
        JPanel iconCircle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, accentColor,
                        getWidth(), getHeight(), accentColor.darker());
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                // Load and draw icon
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
        iconCircle.setBounds(14, (76-46)/2, 46, 46);
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
            private boolean h=false;
            {addMouseListener(new MouseAdapter(){
                @Override public void mouseEntered(MouseEvent e){h=true;repaint();}
                @Override public void mouseExited(MouseEvent e){h=false;repaint();}
            });}
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg=h?new Color(color.getRed(),color.getGreen(),color.getBlue(),200)
                          :new Color(color.getRed(),color.getGreen(),color.getBlue(),80);
                g2.setColor(bg);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(h?color.brighter():color);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(h?Color.WHITE:color.brighter());
                g2.setFont(getFont());
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g){}
        };
        b.setFont(new Font("Segoe UI",Font.BOLD,12));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    static JTable buildStyledTable(DefaultTableModel model) {
        JTable t = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row%2==0 ? MainDashboard.C_CARD : new Color(16,18,30));
                    c.setForeground(MainDashboard.C_TEXT);
                } else {
                    c.setBackground(new Color(99,102,241,80));
                    c.setForeground(Color.WHITE);
                }
                if (c instanceof JLabel) ((JLabel)c).setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
                return c;
            }
        };
        t.setBackground(MainDashboard.C_CARD);
        t.setForeground(MainDashboard.C_TEXT);
        t.setGridColor(MainDashboard.C_BORDER);
        t.setRowHeight(32);
        t.setFont(new Font("Segoe UI",Font.PLAIN,12));
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setIntercellSpacing(new Dimension(0,1));
        t.setSelectionBackground(new Color(99,102,241,80));
        t.setSelectionForeground(Color.WHITE);
        t.setFocusable(false);
        JTableHeader h = t.getTableHeader();
        h.setBackground(new Color(20,22,38));
        h.setForeground(MainDashboard.C_ACCENT);
        h.setFont(new Font("Segoe UI",Font.BOLD,12));
        h.setBorder(BorderFactory.createMatteBorder(0,0,2,0,MainDashboard.C_ACCENT));
        h.setReorderingAllowed(false);
        return t;
    }

    private void styleDateChooser(JDateChooser dc) {
        dc.setBackground(MainDashboard.C_FIELD);
        dc.setForeground(MainDashboard.C_TEXT);
        dc.setBorder(BorderFactory.createLineBorder(MainDashboard.C_BORDER,1));
        Component comp = dc.getDateEditor().getUiComponent();
        if (comp instanceof JTextField) {
            JTextField tf = (JTextField) comp;
            tf.setBackground(MainDashboard.C_FIELD);
            tf.setForeground(MainDashboard.C_TEXT);
            tf.setCaretColor(MainDashboard.C_ACCENT);
            tf.setFont(new Font("Segoe UI",Font.PLAIN,12));
            tf.setBorder(BorderFactory.createEmptyBorder(4,8,4,4));
            tf.setOpaque(true);
        }
        styleAllDateChildren(dc);
        SwingUtilities.invokeLater(() -> { styleAllDateChildren(dc); dc.repaint(); });
    }

    private void styleAllDateChildren(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JFormattedTextField) {
                JFormattedTextField f=(JFormattedTextField)comp;
                f.setBackground(MainDashboard.C_FIELD);f.setForeground(MainDashboard.C_TEXT);
                f.setCaretColor(MainDashboard.C_ACCENT);f.setDisabledTextColor(MainDashboard.C_TEXT);
                f.setBorder(BorderFactory.createEmptyBorder(4,8,4,4));
                f.setFont(new Font("Segoe UI",Font.PLAIN,12));f.setOpaque(true);
            } else if (comp instanceof JButton) {
                JButton btn=(JButton)comp;
                btn.setBackground(new Color(30,33,55));btn.setForeground(MainDashboard.C_TEXT);
                btn.setBorderPainted(false);btn.setFocusPainted(false);btn.setOpaque(true);
            } else { comp.setBackground(MainDashboard.C_FIELD); comp.setForeground(MainDashboard.C_TEXT); }
            if (comp instanceof Container) styleAllDateChildren((Container)comp);
        }
    }

    private void styleScrollBar(JScrollPane sp) {
        sp.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors(){thumbColor=new Color(60,65,100);trackColor=MainDashboard.C_SIDEBAR;}
            @Override protected JButton createDecreaseButton(int o){return z();}
            @Override protected JButton createIncreaseButton(int o){return z();}
            private JButton z(){JButton b=new JButton();b.setPreferredSize(new Dimension(0,0));return b;}
        });
        sp.getHorizontalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors(){thumbColor=new Color(60,65,100);trackColor=MainDashboard.C_SIDEBAR;}
            @Override protected JButton createDecreaseButton(int o){return z();}
            @Override protected JButton createIncreaseButton(int o){return z();}
            private JButton z(){JButton b=new JButton();b.setPreferredSize(new Dimension(0,0));return b;}
        });
    }
}