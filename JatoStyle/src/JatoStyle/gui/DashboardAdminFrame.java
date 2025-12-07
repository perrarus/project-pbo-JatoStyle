/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package JatoStyle.gui;

import JatoStyle.services.AuthService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import JatoStyle.models.Admin;
import javax.swing.table.DefaultTableCellRenderer;
import JatoStyle.services.PasswordHasher;

public class DashboardAdminFrame extends JFrame {
    
    private static final Logger logger = Logger.getLogger(DashboardAdminFrame.class.getName());
    
    private final AuthService auth = new AuthService();
    
    private Admin currentAdmin;

    private JLabel logoLabel;
    private JLabel headerTitle;
    private JButton logoutButton;

    private JTabbedPane jTabbedPane;

    private DefaultTableModel usersModel;
    private JTable usersTable;

    private DefaultTableModel restoModel;
    private JTable restoTable;

    /**
     * Creates new form DashboardAdminFrame
     */
    public DashboardAdminFrame() {
        initComponents();
        styleTables();
        setupUI();
        loadAllUsers();
        loadAllRestoran();
    }
    
    public DashboardAdminFrame(Admin admin) {
        this.currentAdmin = admin;
        initComponents();
        
        UIManager.put(
            "Table.focusCellHighlightBorder",
            BorderFactory.createLineBorder(new Color(149, 189, 226), 2) // DIUBAH #95BDE2
        );

        setupUI();
        loadAllUsers();
        loadAllRestoran();
        styleTables();
    }

    
    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 620);
        setLocationRelativeTo(null);

        logoLabel = new JLabel(new ImageIcon(getClass().getResource("/JatoStyle/gui/logo_mini.png")));

        headerTitle = new JLabel("Dashboard Admin");
        logoutButton = new JButton("Logout");

        // tables
        usersModel = new DefaultTableModel(
            new String[]{"ID", "Nama", "Email", "No HP", "Password"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        usersTable = new JTable(usersModel);
        usersTable.setRowHeight(28);

        restoModel = new DefaultTableModel(
            new String[]{"ID", "Nama Toko", "Jam Buka", "Jam Tutup", "Email", "Password"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        restoTable = new JTable(restoModel);
        restoTable.setRowHeight(28);
    }
    
    private void styleTables() {
        JTable[] tables = { usersTable, restoTable };
        for (JTable t : tables) {
            t.setRowHeight(32);
            t.setShowGrid(false);

            // warna background + font tabel
            t.setBackground(new Color(206, 220, 239)); // DIUBAH [206,220,239]
            t.setForeground(new Color(0, 51, 79)); // #00334F - DIUBAH
            t.setFont(new Font("Bahnschrift", Font.PLAIN, 14));

            t.getTableHeader().setOpaque(true);
            t.getTableHeader().setPreferredSize(new Dimension(0, 35));
            t.getTableHeader().setBorder(BorderFactory.createMatteBorder(
                0, 0, 2, 0, new Color(149, 189, 226) // #95BDE2 - DIUBAH
            ));

            // header renderer - WARNA [30,73,138]
            DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    // set semua style
                    setHorizontalAlignment(JLabel.CENTER);
                    setFont(new Font("Bahnschrift", Font.BOLD, 13));
                    setBackground(new Color(30, 73, 138));  // [30,73,138] - DIUBAH
                    setForeground(Color.WHITE);  // FONT HEADER putih
                    setBorder(BorderFactory.createEmptyBorder());

                    return this;
                }
            };

            // apply ke semua kolom header
            for (int i = 0; i < t.getColumnCount(); i++) {
                t.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
            }

            // tabel border vertikal
            DefaultTableCellRenderer center = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    // garis vertikal di kanan setiap cell
                    ((JLabel) c).setBorder(BorderFactory.createMatteBorder(
                        0, 0, 0, 1, new Color(149, 189, 226) // #95BDE2 - DIUBAH
                    ));

                    // set warna background dan foreground sel
                    setBackground(new Color(206, 220, 239)); // [206,220,239]
                    setForeground(new Color(0, 51, 79)); // #00334F

                    return c;
                }
            };
            center.setHorizontalAlignment(JLabel.CENTER);
            center.setFont(new Font("Bahnschrift", Font.PLAIN, 14));

            for (int i = 0; i < t.getColumnCount(); i++) {
                t.getColumnModel().getColumn(i).setCellRenderer(center);
            }

            t.setSelectionBackground(new Color(149, 189, 226, 100)); // #95BDE2 transparan - DIUBAH
            t.setSelectionForeground(new Color(59, 31, 11)); // #3B1F0B
        }
    }
    
    private void setupUI() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(206, 220, 239)); // DIUBAH [206,220,239]
        wrapper.setBorder(new EmptyBorder(25, 30, 25, 30));

        // header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(10, 0, 20, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(logoLabel);

        JPanel center = new JPanel();
        center.setOpaque(false);
        headerTitle.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        headerTitle.setForeground(new Color(0, 51, 79)); // #00334F - DIUBAH
        center.add(headerTitle);

        logoutButton.setBackground(new Color(0, 51, 79)); // #00334F - DIUBAH
        logoutButton.setForeground(new Color(206, 220, 239)); // #CEDCEF - DIUBAH
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Bahnschrift", Font.BOLD, 12));
        logoutButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(logoutButton);

        headerPanel.add(left, BorderLayout.WEST);
        headerPanel.add(center, BorderLayout.CENTER);
        headerPanel.add(right, BorderLayout.EAST);
        wrapper.add(headerPanel, BorderLayout.NORTH);

        // tabs
        jTabbedPane = new JTabbedPane();
        jTabbedPane.setFont(new Font("Bahnschrift", Font.BOLD, 12));
        jTabbedPane.setForeground(new Color(0, 51, 79)); // #00334F - DIUBAH
        jTabbedPane.setBackground(new Color(206, 220, 239)); // DIUBAH [206,220,239]
        jTabbedPane.setBorder(BorderFactory.createLineBorder(new Color(149, 189, 226), 2)); // #95BDE2 - DIUBAH
        jTabbedPane.setTabPlacement(JTabbedPane.LEFT);
        jTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        // set custom UI 
        jTabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                              int x, int y, int w, int h, boolean isSelected) {

                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isSelected) {
                    g2d.setColor(new Color(149, 189, 226)); // #95BDE2 - DIUBAH
                } else {
                    g2d.setColor(new Color(206, 220, 239)); // [206,220,239] - DIUBAH
                }

                int margin = 8;
                int leftMargin = 15;
                int tabHeight = h - margin;
                int tabWidth = w - 10;

                g2d.fillRoundRect(x + leftMargin, y + margin, tabWidth, tabHeight, 15, 15);
                g2d.dispose();
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, 
                                        int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isSelected) {
                    g2d.setColor(new Color(0, 51, 79)); // #00334F - DIUBAH
                } else {
                    g2d.setColor(new Color(200, 200, 200)); // Abu-abu muda untuk tab tidak aktif
                }

                int margin = 8; // jarak antar tab 
                int leftMargin = 15; // geser ke kanan
                int tabHeight = h - margin; // tinggi tab
                int tabWidth = w - 10; // lebar tab
                g2d.drawRoundRect(x + leftMargin, y + margin, tabWidth, tabHeight, 15, 15);
                g2d.dispose();
            }
            
            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects,
                                               int tabIndex, Rectangle iconRect, Rectangle textRect,
                                               boolean isSelected) {
            }

            @Override
            protected int getTabRunIndent(int tabPlacement, int run) {
                return 15; // indentasi utk geser tab ke atas
            }

            @Override
            protected Insets getTabAreaInsets(int tabPlacement) {
                return new Insets(15, 10, 0, 0);
            }

            @Override
            protected Insets getContentBorderInsets(int tabPlacement) {
                return new Insets(5, 0, 0, 0); // top, left, bottom, right
            }

            @Override
            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 30; // tambah 30px lebar tab
            }

            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) + 15; // tambah 15px tinggi tab
            }

        });

        // PASTIKAN BACKGROUND SETIAP TAB JUGA [206,220,239]
        for (int i = 0; i < jTabbedPane.getTabCount(); i++) {
            jTabbedPane.setBackgroundAt(i, new Color(206, 220, 239));
        }

        jTabbedPane.addTab("Users", buildUsersTab());
        jTabbedPane.addTab("Toko", buildRestoranTab());
        wrapper.add(jTabbedPane, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }
    
    // tab users
    private void showOrangeFormDialog(String title, Object[][] fields, Runnable onOK) {
        JPanel form = createStyledForm(fields);

        OrangeDialog dialog = new OrangeDialog(
            this,
            title,
            form,
            onOK
        );

        dialog.setVisible(true);
    }
    
    private JPanel buildUsersTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(206, 220, 239)); // DIUBAH [206,220,239]

        JScrollPane scroll = new JScrollPane(usersTable);
        scroll.getViewport().setBackground(new Color(206, 220, 239)); // DIUBAH
        scroll.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(149, 189, 226))); // #95BDE2 - DIUBAH
        p.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.setBackground(new Color(206, 220, 239)); // DIUBAH
        
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadAllUsers());
        JButton add = new JButton("Tambah User");
        add.addActionListener(e -> addUserDialog());
        JButton edit = new JButton("Edit User");
        edit.addActionListener(e -> editSelectedUser());
        JButton del = new JButton("Hapus User");
        
        styleButton(refresh, new Color(149, 189, 226)); // #95BDE2 - DIUBAH
        styleButton(add, new Color(149, 189, 226)); // #95BDE2 - DIUBAH
        styleButton(edit, new Color(149, 189, 226)); // #95BDE2 - DIUBAH
        styleButton(del, new Color(0, 51, 79));  // #00334F - DIUBAH

        del.addActionListener(e -> deleteSelectedUser());
        bottom.add(refresh); bottom.add(add); bottom.add(edit); bottom.add(del);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }
    
    private void loadAllUsers() {
        usersModel.setRowCount(0);

        try {
            String sql = "SELECT id_user, nama, email, no_hp, password FROM user ORDER BY id_user";
            ResultSet rs = auth.getKonektor().getData(sql);

            while (rs.next()) {
                usersModel.addRow(new Object[]{
                    rs.getInt("id_user"),
                    rs.getString("nama"),
                    rs.getString("email"),
                    rs.getString("no_hp"),
                    rs.getString("password")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat data user: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addUserDialog() {
        JTextField nama = new JTextField();
        JTextField email = new JTextField();
        JTextField nohp = new JTextField();
        JPasswordField pass = new JPasswordField();

        showOrangeFormDialog("Tambah User", new Object[][]{
            {"Nama:", nama},
            {"Email:", email},
            {"No HP:", nohp},
            {"Password:", pass}
        }, () -> {
            try {
                String sNama = nama.getText().trim();
                String sEmail = email.getText().trim();
                String sHP = nohp.getText().trim();
                String sPass = new String(pass.getPassword()).trim();

                if (sNama.isEmpty() || sEmail.isEmpty() || sPass.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Semua field wajib diisi!");
                    return;
                }

                // hash password sebelum disimpan
                String hashedPassword = PasswordHasher.hashPassword(sPass);

                auth.getKonektor().query(String.format(
                    "INSERT INTO user (nama,email,password,no_hp) VALUES('%s','%s','%s','%s')",
                    sNama.replace("'", "''"),
                    sEmail.replace("'", "''"),
                    hashedPassword, // Gunakan hashed password
                    sHP.replace("'", "''")
                ));
                loadAllUsers();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
    }
    
    private void editSelectedUser() {
        int r = usersTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this,"Pilih user dulu!"); return; }

        int id = (int) usersModel.getValueAt(r, 0);

        try {
            ResultSet rs = auth.getKonektor().getData(
                "SELECT nama,email,no_hp FROM user WHERE id_user=" + id
            );

            if (rs != null && rs.next()) {
                JTextField nama = new JTextField(rs.getString("nama"));
                JTextField email = new JTextField(rs.getString("email"));
                JTextField nohp = new JTextField(rs.getString("no_hp"));

                showOrangeFormDialog("Edit User", new Object[][]{
                    {"Nama:", nama},
                    {"Email:", email},
                    {"No HP:", nohp}
                }, () -> {
                    try {
                        auth.getKonektor().query(String.format(
                            "UPDATE user SET nama='%s', email='%s', no_hp='%s' WHERE id_user=%d",
                            nama.getText().trim().replace("'", "''"),
                            email.getText().trim().replace("'", "''"),
                            nohp.getText().trim().replace("'", "''"),
                            id
                        ));
                        loadAllUsers();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                    }
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
    
    private void deleteSelectedUser() {
        int r = usersTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Pilih user dulu"); return; }
        int id = (int) usersModel.getValueAt(r, 0);
        int ok = JOptionPane.showConfirmDialog(this, "Hapus user ID " + id + " ?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            auth.getKonektor().query("DELETE FROM user WHERE id_user = " + id);
            loadAllUsers();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "deleteSelectedUser", ex);
            JOptionPane.showMessageDialog(this, "Gagal hapus user: " + ex.getMessage());
        }
    }
    
    // tab restoran
    private JPanel buildRestoranTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(206, 220, 239)); // DIUBAH [206,220,239]
        
        JScrollPane scroll = new JScrollPane(restoTable);
        scroll.getViewport().setBackground(new Color(206, 220, 239)); // DIUBAH
        scroll.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(149, 189, 226))); // #95BDE2 - DIUBAH
        p.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.setBackground(new Color(206, 220, 239)); // DIUBAH
        
        JButton refresh = new JButton("Refresh"); 
        refresh.addActionListener(e -> loadAllRestoran());
        JButton add = new JButton("Tambah Toko"); 
        add.addActionListener(e -> addRestoranDialog());
        JButton edit = new JButton("Edit Toko"); 
        edit.addActionListener(e -> editSelectedRestoran());
        JButton del = new JButton("Hapus Toko"); 
        del.addActionListener(e -> deleteSelectedRestoran());
        
        styleButton(refresh, new Color(149, 189, 226)); // #95BDE2 - DIUBAH
        styleButton(add, new Color(149, 189, 226)); // #95BDE2 - DIUBAH
        styleButton(edit, new Color(149, 189, 226)); // #95BDE2 - DIUBAH
        styleButton(del, new Color(0, 51, 79));  // #00334F - DIUBAH
        
        bottom.add(refresh); bottom.add(add); bottom.add(edit); bottom.add(del);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }
    
    private void loadAllRestoran() {
        restoModel.setRowCount(0);
        try {
            String sql = "SELECT id_restoran, nama_restoran, jam_buka, jam_tutup, email, password FROM restoran ORDER BY id_restoran";
            ResultSet rs = auth.getKonektor().getData(sql);
            while (rs != null && rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id_restoran"));
                row.add(rs.getString("nama_restoran"));
                row.add(rs.getString("jam_buka"));
                row.add(rs.getString("jam_tutup"));
                row.add(rs.getString("email"));
                row.add(rs.getString("password"));
                restoModel.addRow(row);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "loadAllRestoran", ex);
            JOptionPane.showMessageDialog(this, "Gagal memuat toko: " + ex.getMessage());
        }
    }

    private void addRestoranDialog() {
        JTextField nama = new JTextField();
        JTextField email = new JTextField();
        JPasswordField pass = new JPasswordField();
        JTextField jamBuka = new JTextField("08:00:00");
        JTextField jamTutup = new JTextField("22:00:00");

        showOrangeFormDialog("Tambah Toko", new Object[][]{
            {"Nama Toko:", nama},
            {"Email (login):", email},
            {"Password (login):", pass},
            {"Jam Buka:", jamBuka},
            {"Jam Tutup:", jamTutup}
        }, () -> {
            try {
                // hash password sebelum disimpan
                String plainPassword = new String(pass.getPassword()).trim();
                String hashedPassword = PasswordHasher.hashPassword(plainPassword);

                auth.getKonektor().query(String.format(
                    "INSERT INTO restoran(id_admin,nama_restoran,email,password,jam_buka,jam_tutup) VALUES(%d,'%s','%s','%s','%s','%s')",
                    currentAdmin.getIdAdmin(),
                    nama.getText().trim().replace("'", "''"),
                    email.getText().trim().replace("'", "''"),
                    hashedPassword, 
                    jamBuka.getText().trim(),
                    jamTutup.getText().trim()
                ));
                loadAllRestoran();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
    }
    
    private void editSelectedRestoran() {
        int r = restoTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this,"Pilih toko dulu!"); return; }

        int id = (int) restoModel.getValueAt(r, 0);

        try {
            ResultSet rs = auth.getKonektor().getData(
                "SELECT nama_restoran,email,jam_buka,jam_tutup FROM restoran WHERE id_restoran=" + id
            );

            if (rs != null && rs.next()) {
                JTextField nama = new JTextField(rs.getString("nama_restoran"));
                JTextField email = new JTextField(rs.getString("email"));
                JTextField jamBuka = new JTextField(rs.getString("jam_buka"));
                JTextField jamTutup = new JTextField(rs.getString("jam_tutup"));

                showOrangeFormDialog("Edit Toko", new Object[][]{
                    {"Nama Toko:", nama},
                    {"Email:", email},
                    {"Jam Buka:", jamBuka},
                    {"Jam Tutup:", jamTutup}
                }, () -> {
                    try {
                        auth.getKonektor().query(String.format(
                            "UPDATE restoran SET nama_restoran='%s', email='%s', jam_buka='%s', jam_tutup='%s' WHERE id_restoran=%d",
                            nama.getText().trim().replace("'", "''"),
                            email.getText().trim().replace("'", "''"),
                            jamBuka.getText().trim(),
                            jamTutup.getText().trim(),
                            id
                        ));
                        loadAllRestoran();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                    }
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void deleteSelectedRestoran() {
        int r = restoTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Pilih toko dulu"); return; }
        int id = (int) restoModel.getValueAt(r, 0);
        int ok = JOptionPane.showConfirmDialog(this, "Hapus toko ID " + id + " (akan menghapus menu & keranjang terkait)?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            // 1. hapus keranjang terkait menu dari restoran
            String delCart = "DELETE FROM keranjang " +
                             "WHERE id_menu IN (SELECT id_menu FROM menu WHERE id_restoran = " + id + ")";
            auth.getKonektor().query(delCart);

            // 2. hapus semua menu restoran
            auth.getKonektor().query("DELETE FROM menu WHERE id_restoran = " + id);

            // 3. hapus restoran
            auth.getKonektor().query("DELETE FROM restoran WHERE id_restoran = " + id);

            loadAllRestoran();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "deleteSelectedRestoran", ex);
            JOptionPane.showMessageDialog(this, "Gagal hapus toko: " + ex.getMessage());
        }
    }
    
    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setFont(new Font("Bahnschrift", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        // Tentukan warna teks berdasarkan warna background
        if (bg.equals(new Color(0, 51, 79))) { // #00334F
            btn.setForeground(new Color(206, 220, 239)); // #CEDCEF untuk button gelap
        } else {
            btn.setForeground(new Color(59, 31, 11)); // #3B1F0B untuk button terang
        }
    }
    
    private JPanel createStyledForm(Object[][] fields) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(206, 220, 239)); // DIUBAH [206,220,239]
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        Font labelFont = new Font("Bahnschrift", Font.BOLD, 14);
        Color labelColor = new Color(0, 51, 79); // #00334F - DIUBAH

        for (Object[] field : fields) {
            String text = (String) field[0];
            JComponent input = (JComponent) field[1];

            JLabel label = new JLabel(text);
            label.setFont(labelFont);
            label.setForeground(labelColor);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);

            input.setFont(new Font("Bahnschrift", Font.PLAIN, 14));
            input.setBackground(Color.WHITE); // DIUBAH putih
            input.setForeground(new Color(0, 51, 79)); // #00334F - DIUBAH
            input.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(149, 189, 226), 2), // #95BDE2 - DIUBAH
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));
            input.setAlignmentX(Component.LEFT_ALIGNMENT);
            input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32)); 

            panel.add(label);
            panel.add(Box.createVerticalStrut(4));   // jarak kecil label dan input
            panel.add(input);
            panel.add(Box.createVerticalStrut(10));  // jarak antar field
        }

        return panel;
    }
    
    class OrangeDialog extends JDialog {

    public OrangeDialog(JFrame owner, String title, JPanel formPanel, Runnable onOK) {
        super(owner, title, true);
        setSize(400, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(206, 220, 239)); // [206,220,239] - DIUBAH

        // wrapper form
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(206, 220, 239)); // DIUBAH
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        wrapper.add(formPanel, BorderLayout.CENTER);

        // button panel
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(new Color(206, 220, 239)); // DIUBAH

        JButton ok = new JButton("OK");
        ok.setBackground(new Color(149, 189, 226)); // #95BDE2 - DIUBAH
        ok.setForeground(new Color(59, 31, 11)); // #3B1F0B - DIUBAH
        ok.setFocusPainted(false);
        ok.setFont(new Font("Bahnschrift", Font.BOLD, 12));
        ok.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        ok.addActionListener(e -> {
            onOK.run();
            dispose();
        });

        JButton cancel = new JButton("Cancel");
        cancel.setBackground(new Color(0, 51, 79)); // #00334F - DIUBAH
        cancel.setForeground(new Color(206, 220, 239)); // #CEDCEF - DIUBAH
        cancel.setFocusPainted(false);
        cancel.setFont(new Font("Bahnschrift", Font.BOLD, 12));
        cancel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        cancel.addActionListener(e -> dispose());

        buttons.add(ok);
        buttons.add(cancel);

        add(wrapper, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        SwingUtilities.invokeLater(() -> {
            DashboardAdminFrame f = new DashboardAdminFrame();
            f.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}