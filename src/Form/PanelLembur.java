package form;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.Locale;
import Koneksi.Koneksi;
import com.toedter.calendar.JDateChooser;

public class PanelLembur extends JPanel {

    private DefaultTableModel model;
    private JTextField txtIdLembur, txtJumlahJam, txtUpahPerJam, txtTotalLembur;
    private JComboBox<String> cmbKaryawan;
    private JDateChooser dateMulai, dateSelesai;
    private JTable table;
    private JLabel lblStatus;

    private PanelLembur() { super(new BorderLayout()); }

    public static PanelLembur create() {
        PanelLembur p = new PanelLembur();
        p.buildUI();
        return p;
    }

    /** Public method so MainDashboard can refresh karyawan list when switching to this panel. */
    public void refreshKaryawan() {
        loadKaryawan();
    }

    public void refresh() {
        loadKaryawan();
        loadData();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(() -> {
            loadKaryawan();
            loadData();
        });
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
        loadKaryawan();
        loadData();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(null);
        root.setOpaque(false);
        root.setPreferredSize(new Dimension(950, 760));

        JPanel headerPanel = buildIconHeader("Data Lembur", "Pencatatan data lembur karyawan",
                "/foto/time.png", MainDashboard.C_WARN);
        headerPanel.setBounds(20, 14, 920, 76);
        root.add(headerPanel);

        lblStatus = new JLabel(""); lblStatus.setFont(new Font("Segoe UI",Font.PLAIN,11));
        lblStatus.setBounds(30,92,700,18); root.add(lblStatus);

        JPanel formCard = buildFormCard();
        formCard.setBounds(20,112,920,330); root.add(formCard);

        JPanel btnRow = buildButtonRow();
        btnRow.setBounds(20,450,920,48); root.add(btnRow);

        JPanel tblCard = buildTableCard();
        tblCard.setBounds(20,506,920,240); root.add(tblCard);

        return root;
    }

    private JPanel buildFormCard() {
        JPanel card = makePanelCard();
        int C1=30,C3=490,LH=28,FH=36,G=8;

        addLbl(card,"ID Lembur",       C1,24);
        addLbl(card,"ID Karyawan",     C1,24+LH+FH+G);
        addLbl(card,"Tanggal Mulai",   C1,24+(LH+FH+G)*2);
        addLbl(card,"Jumlah Jam",      C1,24+(LH+FH+G)*3);
        addLbl(card,"Tanggal Selesai", C3,24);
        addLbl(card,"Upah per Jam (Rp)", C3,24+LH+FH+G);
        addLbl(card,"Total Lembur (Rp)", C3,24+(LH+FH+G)*2);

        txtIdLembur   = makeField(); txtIdLembur.setBounds(C1,24+LH,200,FH);
        cmbKaryawan   = makeCombo(new String[]{}); cmbKaryawan.setBounds(C1,24+LH+FH+G+LH,200,FH);
        dateMulai     = new JDateChooser(); styleDC(dateMulai); dateMulai.setBounds(C1,24+(LH+FH+G)*2+LH,200,FH);
        txtJumlahJam  = makeField();
        txtJumlahJam.setBounds(C1,24+(LH+FH+G)*3+LH,200,FH);
        dateSelesai   = new JDateChooser(); styleDC(dateSelesai); dateSelesai.setBounds(C3,24+LH,200,FH);
        txtUpahPerJam = makeField(); txtUpahPerJam.setBounds(C3,24+LH+FH+G+LH,200,FH);
        txtTotalLembur = makeField(); txtTotalLembur.setEditable(false);
        txtTotalLembur.setBackground(new Color(20,22,36)); txtTotalLembur.setBounds(C3,24+(LH+FH+G)*2+LH,200,FH);

        // Auto-calculate on keyup
        KeyAdapter calc = new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { hitungTotal(); }
        };
        txtJumlahJam.addKeyListener(calc);
        txtUpahPerJam.addKeyListener(calc);

        card.add(txtIdLembur); card.add(cmbKaryawan); card.add(dateMulai);
        card.add(txtJumlahJam); card.add(dateSelesai); card.add(txtUpahPerJam); card.add(txtTotalLembur);
        return card;
    }

    private void hitungTotal() {
        try {
            double jam  = parse(txtJumlahJam.getText());
            double upah = parse(txtUpahPerJam.getText());
            txtTotalLembur.setText(fmt(jam * upah));
        } catch (Exception ignored) {}
    }

    private JPanel buildButtonRow() {
        JPanel p = new JPanel(null); p.setOpaque(false);
        JButton bSave=makeBtn("Simpan",MainDashboard.C_SUCCESS);
        JButton bUpd=makeBtn("Update",MainDashboard.C_INFO);
        JButton bDel=makeBtn("Hapus",MainDashboard.C_DANGER);
        JButton bRst=makeBtn("Reset",MainDashboard.C_WARN);
        bSave.setBounds(0,4,130,40); bUpd.setBounds(140,4,130,40);
        bDel.setBounds(280,4,130,40); bRst.setBounds(420,4,130,40);
        bSave.addActionListener(e->doSave()); bUpd.addActionListener(e->doUpdate());
        bDel.addActionListener(e->doDelete()); bRst.addActionListener(e->clearForm());
        p.add(bSave); p.add(bUpd); p.add(bDel); p.add(bRst);
        return p;
    }

    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout()){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setColor(MainDashboard.C_CARD); g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(MainDashboard.C_BORDER); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        String[] cols={"ID Lembur","ID Karyawan","Tgl Mulai","Tgl Selesai","Jumlah Jam","Upah/Jam","Total Lembur"};
        model=new DefaultTableModel(cols,0){@Override public boolean isCellEditable(int r,int c){return false;}};
        table=PanelKaryawan.buildStyledTable(model);
        table.addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent e){fillForm();}});
        JScrollPane sp=new JScrollPane(table);
        sp.setOpaque(false); sp.getViewport().setOpaque(false); sp.setBorder(null);
        styleScrollBar(sp);
        card.add(sp,BorderLayout.CENTER);
        return card;
    }

    // DB
    private void loadKaryawan() {
        cmbKaryawan.removeAllItems();
        try {
            ResultSet rs=Koneksi.getKoneksi().createStatement().executeQuery("SELECT id_karyawan,nama FROM tb_karyawan ORDER BY id_karyawan");
            while(rs.next()) cmbKaryawan.addItem(rs.getString("id_karyawan")+" - "+rs.getString("nama"));
        } catch(Exception ignored){}
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            ResultSet rs=Koneksi.getKoneksi().createStatement().executeQuery("SELECT * FROM tb_lembur");
            while(rs.next()) model.addRow(new Object[]{
                rs.getString("id_lembur"),rs.getString("id_karyawan"),
                rs.getDate("tanggal_mulai"),rs.getDate("tanggal_selesai"),
                rs.getInt("jumlah_jam"),fmt(rs.getDouble("upah_per_jam")),fmt(rs.getDouble("total_lembur"))
            });
        } catch(Exception e){showStatus("Error: "+e.getMessage(),MainDashboard.C_DANGER);}
    }

    private void doSave() {
        try {
            if(dateMulai.getDate()==null||dateSelesai.getDate()==null){
                showStatus("Tanggal mulai dan selesai harus diisi!",MainDashboard.C_WARN);return;
            }
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            PreparedStatement ps=Koneksi.getKoneksi().prepareStatement(
                "INSERT INTO tb_lembur(id_lembur,id_karyawan,tanggal_mulai,tanggal_selesai,jumlah_jam,upah_per_jam,total_lembur) VALUES(?,?,?,?,?,?,?)");
            ps.setString(1,txtIdLembur.getText()); ps.setString(2,getIdKaryawan());
            ps.setString(3,sdf.format(dateMulai.getDate())); ps.setString(4,sdf.format(dateSelesai.getDate()));
            ps.setInt(5,Integer.parseInt(txtJumlahJam.getText().isEmpty()?"0":txtJumlahJam.getText()));
            ps.setDouble(6,parse(txtUpahPerJam.getText())); ps.setDouble(7,parse(txtTotalLembur.getText()));
            ps.executeUpdate();
            showStatus("Data berhasil disimpan!",MainDashboard.C_SUCCESS);
            clearForm(); loadData();
        } catch(Exception e){showStatus("Error: "+e.getMessage(),MainDashboard.C_DANGER);}
    }

    private void doUpdate() {
        try {
            if(dateMulai.getDate()==null||dateSelesai.getDate()==null){
                showStatus("Tanggal harus diisi!",MainDashboard.C_WARN);return;
            }
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            PreparedStatement ps=Koneksi.getKoneksi().prepareStatement(
                "UPDATE tb_lembur SET id_karyawan=?,tanggal_mulai=?,tanggal_selesai=?,jumlah_jam=?,upah_per_jam=?,total_lembur=? WHERE id_lembur=?");
            ps.setString(1,getIdKaryawan()); ps.setString(2,sdf.format(dateMulai.getDate()));
            ps.setString(3,sdf.format(dateSelesai.getDate()));
            ps.setInt(4,Integer.parseInt(txtJumlahJam.getText().isEmpty()?"0":txtJumlahJam.getText()));
            ps.setDouble(5,parse(txtUpahPerJam.getText())); ps.setDouble(6,parse(txtTotalLembur.getText()));
            ps.setString(7,txtIdLembur.getText());
            ps.executeUpdate();
            showStatus("Data berhasil diupdate!",MainDashboard.C_SUCCESS);
            clearForm(); loadData();
        } catch(Exception e){showStatus("Error: "+e.getMessage(),MainDashboard.C_DANGER);}
    }

    private void doDelete() {
        if(JOptionPane.showConfirmDialog(this,"Yakin hapus data?","Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try{
                PreparedStatement ps=Koneksi.getKoneksi().prepareStatement("DELETE FROM tb_lembur WHERE id_lembur=?");
                ps.setString(1,txtIdLembur.getText()); ps.executeUpdate();
                showStatus("Data berhasil dihapus!",MainDashboard.C_SUCCESS);
                clearForm(); loadData();
            }catch(Exception e){showStatus("Error: "+e.getMessage(),MainDashboard.C_DANGER);}
        }
    }

    private void clearForm() {
        txtIdLembur.setText(""); txtJumlahJam.setText(""); txtUpahPerJam.setText(""); txtTotalLembur.setText("");
        dateMulai.setDate(null); dateSelesai.setDate(null);
        if(cmbKaryawan.getItemCount()>0) cmbKaryawan.setSelectedIndex(0);
    }

    private void fillForm() {
        try {
            int row=table.getSelectedRow();
            txtIdLembur.setText(model.getValueAt(row,0).toString());
            String idKar=model.getValueAt(row,1).toString();
            for(int i=0;i<cmbKaryawan.getItemCount();i++)
                if(cmbKaryawan.getItemAt(i).startsWith(idKar+" - ")){cmbKaryawan.setSelectedIndex(i);break;}
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            dateMulai.setDate(sdf.parse(model.getValueAt(row,2).toString()));
            dateSelesai.setDate(sdf.parse(model.getValueAt(row,3).toString()));
            txtJumlahJam.setText(model.getValueAt(row,4).toString());
            txtUpahPerJam.setText(model.getValueAt(row,5).toString().replace(".",""));
            txtTotalLembur.setText(model.getValueAt(row,6).toString().replace(".",""));
        }catch(Exception e){showStatus("Error: "+e.getMessage(),MainDashboard.C_DANGER);}
    }

    private String getIdKaryawan() {
        if(cmbKaryawan.getSelectedItem()==null)return "";
        return cmbKaryawan.getSelectedItem().toString().split(" - ")[0].trim();
    }

    private String fmt(double v){return NumberFormat.getInstance(new Locale("id","ID")).format(v);}
    private double parse(String s){if(s==null||s.trim().isEmpty())return 0;return Double.parseDouble(s.trim().replace(".","").replace(",",""));}
    private void showStatus(String m,Color c){lblStatus.setText(m);lblStatus.setForeground(c);}

    private JPanel makePanelCard() {
        JPanel card=new JPanel(null){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(MainDashboard.C_CARD); g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                GradientPaint gp=new GradientPaint(0,0,new Color(99,102,241,20),getWidth(),0,new Color(0,0,0,0));
                g2.setPaint(gp); g2.fillRoundRect(0,0,getWidth(),3,14,14);
                g2.setColor(MainDashboard.C_BORDER); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }
    private void addLbl(JPanel p,String t,int x,int y){JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",Font.PLAIN,11));l.setForeground(MainDashboard.C_TEXT_MUTED);l.setBounds(x,y,220,18);p.add(l);}
    private JTextField makeField(){
        JTextField f=new JTextField(){@Override protected void paintBorder(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(hasFocus()?MainDashboard.C_ACCENT:MainDashboard.C_BORDER);g2.setStroke(new BasicStroke(hasFocus()?1.5f:1f));g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);g2.dispose();}};
        f.setBackground(MainDashboard.C_FIELD);f.setForeground(MainDashboard.C_TEXT);f.setCaretColor(MainDashboard.C_ACCENT);f.setFont(new Font("Segoe UI",Font.PLAIN,12));f.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));f.setOpaque(true);
        f.addFocusListener(new FocusAdapter(){@Override public void focusGained(FocusEvent e){f.repaint();}@Override public void focusLost(FocusEvent e){f.repaint();}});
        return f;
    }
    private JComboBox<String> makeCombo(String[] items){JComboBox<String> c=new JComboBox<>(items);c.setBackground(MainDashboard.C_FIELD);c.setForeground(MainDashboard.C_TEXT);c.setFont(new Font("Segoe UI",Font.PLAIN,12));c.setBorder(BorderFactory.createLineBorder(MainDashboard.C_BORDER,1));return c;}
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

    private JButton makeBtn(String text,Color color){
        JButton b=new JButton(text){private boolean h=false;{addMouseListener(new MouseAdapter(){@Override public void mouseEntered(MouseEvent e){h=true;repaint();}@Override public void mouseExited(MouseEvent e){h=false;repaint();}});}
        @Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);Color bg=h?new Color(color.getRed(),color.getGreen(),color.getBlue(),200):new Color(color.getRed(),color.getGreen(),color.getBlue(),80);g2.setColor(bg);g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);g2.setColor(h?color.brighter():color);g2.setStroke(new BasicStroke(1f));g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);g2.setColor(h?Color.WHITE:color.brighter());g2.setFont(getFont());FontMetrics fm=g2.getFontMetrics();g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);g2.dispose();}
        @Override protected void paintBorder(Graphics g){}};
        b.setFont(new Font("Segoe UI",Font.BOLD,12));b.setContentAreaFilled(false);b.setBorderPainted(false);b.setFocusPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));return b;
    }
    private void styleDC(JDateChooser dc){dc.setBackground(MainDashboard.C_FIELD);dc.setForeground(MainDashboard.C_TEXT);dc.setBorder(BorderFactory.createLineBorder(MainDashboard.C_BORDER,1));Component comp=dc.getDateEditor().getUiComponent();if(comp instanceof JTextField){JTextField tf=(JTextField)comp;tf.setBackground(MainDashboard.C_FIELD);tf.setForeground(MainDashboard.C_TEXT);tf.setCaretColor(MainDashboard.C_ACCENT);tf.setFont(new Font("Segoe UI",Font.PLAIN,12));tf.setBorder(BorderFactory.createEmptyBorder(4,8,4,4));tf.setOpaque(true);}styleAllDC(dc);SwingUtilities.invokeLater(()->{styleAllDC(dc);dc.repaint();});}
    private void styleAllDC(Container c){for(Component comp:c.getComponents()){if(comp instanceof JFormattedTextField){JFormattedTextField f=(JFormattedTextField)comp;f.setBackground(MainDashboard.C_FIELD);f.setForeground(MainDashboard.C_TEXT);f.setCaretColor(MainDashboard.C_ACCENT);f.setDisabledTextColor(MainDashboard.C_TEXT);f.setBorder(BorderFactory.createEmptyBorder(4,8,4,4));f.setFont(new Font("Segoe UI",Font.PLAIN,12));f.setOpaque(true);}else if(comp instanceof JButton){JButton btn=(JButton)comp;btn.setBackground(new Color(30,33,55));btn.setForeground(MainDashboard.C_TEXT);btn.setBorderPainted(false);btn.setFocusPainted(false);btn.setOpaque(true);}else{comp.setBackground(MainDashboard.C_FIELD);comp.setForeground(MainDashboard.C_TEXT);}if(comp instanceof Container)styleAllDC((Container)comp);}}
    private void styleScrollBar(JScrollPane sp){sp.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI(){@Override protected void configureScrollBarColors(){thumbColor=new Color(60,65,100);trackColor=MainDashboard.C_SIDEBAR;}@Override protected JButton createDecreaseButton(int o){return z();}@Override protected JButton createIncreaseButton(int o){return z();}private JButton z(){JButton b=new JButton();b.setPreferredSize(new Dimension(0,0));return b;}});}
}