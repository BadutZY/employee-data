package form;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;
import Koneksi.Koneksi;

public class PanelGolongan extends JPanel {

    private DefaultTableModel model;
    private JTextField txtId, txtNama, txtGajiPokok, txtTransport, txtUangMakan;
    private JTable table;
    private JLabel lblStatus;

    private PanelGolongan() { super(new BorderLayout()); }

    public static PanelGolongan create() {
        PanelGolongan p = new PanelGolongan();
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
        loadData();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(this::loadData);
    }

    public void refresh() {
        loadData();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(null);
        root.setOpaque(false);
        root.setPreferredSize(new Dimension(950, 680));

        JPanel headerPanel = buildIconHeader("Data Golongan", "Kelola golongan dan komponen gaji",
                "/foto/document.png", MainDashboard.C_ACCENT2);
        headerPanel.setBounds(20, 14, 920, 76);
        root.add(headerPanel);

        lblStatus = new JLabel("");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setBounds(30, 92, 700, 18);
        root.add(lblStatus);

        // Form card
        JPanel formCard = buildFormCard();
        formCard.setBounds(20, 112, 920, 240);
        root.add(formCard);

        // Buttons
        JPanel btnRow = buildButtonRow();
        btnRow.setBounds(20, 360, 920, 48);
        root.add(btnRow);

        // Table
        JPanel tblCard = buildTableCard();
        tblCard.setBounds(20, 416, 920, 250);
        root.add(tblCard);

        return root;
    }

    private JPanel buildFormCard() {
        JPanel card = makePanelCard();
        int COL1=30, COL3=490;
        int LH=28, FH=36;

        addLbl(card,"ID Golongan",     COL1, 24);
        addLbl(card,"Nama Golongan",   COL1, 24+LH+FH+8);
        addLbl(card,"Gaji Pokok (Rp)", COL3, 24);
        addLbl(card,"Transport (Rp)",  COL3, 24+LH+FH+8);
        addLbl(card,"Uang Makan (Rp)", COL3, 24+(LH+FH+8)*2);

        txtId         = makeField(); txtId.setBounds(COL1, 24+LH, 200, FH);
        txtNama       = makeField(); txtNama.setBounds(COL1, 24+LH+FH+8+LH, 200, FH);
        txtGajiPokok  = makeField(); txtGajiPokok.setBounds(COL3, 24+LH, 200, FH);
        txtTransport  = makeField(); txtTransport.setBounds(COL3, 24+LH+FH+8+LH, 200, FH);
        txtUangMakan  = makeField(); txtUangMakan.setBounds(COL3, 24+(LH+FH+8)*2+LH, 200, FH);

        card.add(txtId); card.add(txtNama); card.add(txtGajiPokok);
        card.add(txtTransport); card.add(txtUangMakan);
        return card;
    }

    private JPanel buildButtonRow() {
        JPanel p = new JPanel(null);
        p.setOpaque(false);
        JButton bSave   = makeBtn("Simpan",  MainDashboard.C_SUCCESS);
        JButton bUpdate = makeBtn("Update",   MainDashboard.C_INFO);
        JButton bDelete = makeBtn("Hapus",   MainDashboard.C_DANGER);
        JButton bReset  = makeBtn("Reset",    MainDashboard.C_WARN);
        bSave.setBounds(0,4,130,40); bUpdate.setBounds(140,4,130,40);
        bDelete.setBounds(280,4,130,40); bReset.setBounds(420,4,130,40);
        bSave.addActionListener(e -> doSave());
        bUpdate.addActionListener(e -> doUpdate());
        bDelete.addActionListener(e -> doDelete());
        bReset.addActionListener(e -> clearForm());
        p.add(bSave); p.add(bUpdate); p.add(bDelete); p.add(bReset);
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
        card.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        String[] cols={"ID Golongan","Nama Golongan","Gaji Pokok","Transport","Uang Makan"};
        model = new DefaultTableModel(cols,0){ @Override public boolean isCellEditable(int r,int c){return false;} };
        table = PanelKaryawan.buildStyledTable(model);
        table.addMouseListener(new MouseAdapter(){
            @Override public void mouseClicked(MouseEvent e){ fillForm(); }
        });
        JScrollPane sp = new JScrollPane(table);
        sp.setOpaque(false); sp.getViewport().setOpaque(false); sp.setBorder(null);
        styleScrollBar(sp);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    // DB
    private void loadData() {
        model.setRowCount(0);
        try {
            ResultSet rs = Koneksi.getKoneksi().createStatement().executeQuery(
                "SELECT id_golongan,nama_golongan,gaji_pokok,transport,uang_makan FROM tb_golongan");
            while (rs.next()) model.addRow(new Object[]{
                rs.getString("id_golongan"), rs.getString("nama_golongan"),
                fmt(rs.getDouble("gaji_pokok")), fmt(rs.getDouble("transport")), fmt(rs.getDouble("uang_makan"))
            });
        } catch (Exception e) { showStatus("Error: "+e.getMessage(), MainDashboard.C_DANGER); }
    }

    private void doSave() {
        try {
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(
                "INSERT INTO tb_golongan (id_golongan,nama_golongan,gaji_pokok,tunjangan_istri,jumlah_anak,tunjangan_anak,transport,uang_makan) VALUES (?,?,?,0,0,0,?,?)");
            ps.setString(1,txtId.getText()); ps.setString(2,txtNama.getText());
            ps.setDouble(3,parse(txtGajiPokok.getText())); ps.setDouble(4,parse(txtTransport.getText())); ps.setDouble(5,parse(txtUangMakan.getText()));
            ps.executeUpdate();
            showStatus("Data berhasil disimpan!", MainDashboard.C_SUCCESS);
            clearForm(); loadData();
        } catch (Exception e) { showStatus("Error: "+e.getMessage(), MainDashboard.C_DANGER); }
    }

    private void doUpdate() {
        try {
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(
                "UPDATE tb_golongan SET nama_golongan=?,gaji_pokok=?,transport=?,uang_makan=? WHERE id_golongan=?");
            ps.setString(1,txtNama.getText()); ps.setDouble(2,parse(txtGajiPokok.getText()));
            ps.setDouble(3,parse(txtTransport.getText())); ps.setDouble(4,parse(txtUangMakan.getText()));
            ps.setString(5,txtId.getText());
            ps.executeUpdate();
            showStatus("Data berhasil diupdate!", MainDashboard.C_SUCCESS);
            clearForm(); loadData();
        } catch (Exception e) { showStatus("Error: "+e.getMessage(), MainDashboard.C_DANGER); }
    }

    private void doDelete() {
        if (JOptionPane.showConfirmDialog(this,"Yakin hapus data ini?","Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            try {
                PreparedStatement ps = Koneksi.getKoneksi().prepareStatement("DELETE FROM tb_golongan WHERE id_golongan=?");
                ps.setString(1,txtId.getText()); ps.executeUpdate();
                showStatus("Data berhasil dihapus!", MainDashboard.C_SUCCESS);
                clearForm(); loadData();
            } catch (Exception e) { showStatus("Error: "+e.getMessage(), MainDashboard.C_DANGER); }
        }
    }

    private void clearForm() {
        txtId.setText(""); txtNama.setText(""); txtGajiPokok.setText(""); txtTransport.setText(""); txtUangMakan.setText("");
    }

    private void fillForm() {
        int row=table.getSelectedRow();
        txtId.setText(model.getValueAt(row,0).toString());
        txtNama.setText(model.getValueAt(row,1).toString());
        txtGajiPokok.setText(model.getValueAt(row,2).toString().replace(".",""));
        txtTransport.setText(model.getValueAt(row,3).toString().replace(".",""));
        txtUangMakan.setText(model.getValueAt(row,4).toString().replace(".",""));
    }

    private String fmt(double v) {
        return NumberFormat.getInstance(new Locale("id","ID")).format(v);
    }
    private double parse(String s) {
        if (s==null||s.trim().isEmpty()) return 0;
        return Double.parseDouble(s.trim().replace(".","").replace(",",""));
    }
    private void showStatus(String msg, Color c) { lblStatus.setText(msg); lblStatus.setForeground(c); }

    // Helpers
    private JPanel makePanelCard() {
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
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
        return card;
    }

    private void addLbl(JPanel p, String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI",Font.PLAIN,11));
        l.setForeground(MainDashboard.C_TEXT_MUTED);
        l.setBounds(x,y,220,18); p.add(l);
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
        f.setBackground(MainDashboard.C_FIELD); f.setForeground(MainDashboard.C_TEXT);
        f.setCaretColor(MainDashboard.C_ACCENT); f.setFont(new Font("Segoe UI",Font.PLAIN,12));
        f.setBorder(BorderFactory.createEmptyBorder(0,10,0,10)); f.setOpaque(true);
        f.addFocusListener(new FocusAdapter(){
            @Override public void focusGained(FocusEvent e){f.repaint();}
            @Override public void focusLost(FocusEvent e){f.repaint();}
        });
        return f;
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
            private boolean h=false;
            {addMouseListener(new MouseAdapter(){
                @Override public void mouseEntered(MouseEvent e){h=true;repaint();}
                @Override public void mouseExited(MouseEvent e){h=false;repaint();}
            });}
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg=h?new Color(color.getRed(),color.getGreen(),color.getBlue(),200):new Color(color.getRed(),color.getGreen(),color.getBlue(),80);
                g2.setColor(bg);g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(h?color.brighter():color);g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(h?Color.WHITE:color.brighter());g2.setFont(getFont());
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g){}
        };
        b.setFont(new Font("Segoe UI",Font.BOLD,12));
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void styleScrollBar(JScrollPane sp) {
        sp.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors(){thumbColor=new Color(60,65,100);trackColor=MainDashboard.C_SIDEBAR;}
            @Override protected JButton createDecreaseButton(int o){return z();}
            @Override protected JButton createIncreaseButton(int o){return z();}
            private JButton z(){JButton b=new JButton();b.setPreferredSize(new Dimension(0,0));return b;}
        });
    }
}