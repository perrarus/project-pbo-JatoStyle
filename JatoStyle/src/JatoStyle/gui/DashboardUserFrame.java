/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package JatoStyle.gui;

import JatoStyle.models.User;
import JatoStyle.models.Restoran;
import JatoStyle.models.Keranjang;
import JatoStyle.services.KeranjangService;
import javax.swing.*;
import JatoStyle.services.AuthService;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import javax.swing.BorderFactory;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.awt.BorderLayout;
import java.awt.FlowLayout; 
import javax.swing.Box; 
import java.awt.Color;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import javax.swing.border.Border;
import JatoStyle.models.Transaction; 
import JatoStyle.services.TransactionService; 
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.Font;
import java.util.Vector;
import java.text.SimpleDateFormat;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import java.awt.Dimension;
import JatoStyle.models.TransactionDetail;
import javax.swing.table.TableCellEditor;
import javax.swing.DefaultCellEditor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;


public class DashboardUserFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DashboardUserFrame.class.getName());
    private javax.swing.JPanel restoContainerPanel;
    private AuthService authService;
    private KeranjangService keranjangService = new KeranjangService();
    private JPanel keranjangContainer;
    private int userLoginId;
    private User currentUser; 
    private TransactionService transactionService; 
    private DefaultTableModel transactionTableModel; 
    private JTable transactionTable;
    private JLabel totalHargaLabel;
    

    /**
     * Creates new form DashboardUserFrame
     */
    public DashboardUserFrame() {
        initComponents();
        authService = new AuthService();
        setupRestoContainer(); 
        loadRestoFromDatabase(); 
        haloNamaOutput.setCaretColor(new java.awt.Color(0, 0, 0, 0)); 
        applyDashboardTheme();
        setupKeranjangContainer();
        this.transactionService = new TransactionService();

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabChanged(evt);
            }
        });
    }
    
    public DashboardUserFrame(User user) {
        this(); // panggil constructor tanpa parameter
        this.currentUser = user;
        this.userLoginId = user.getIdUser();
        haloNamaOutput.setText("Halo, " + user.getNama());
    }
    
    private void setupTransactionPanel() {
        System.out.println("=== SETUP TRANSACTION PANEL ===");

        // setup Model Tabel
        String[] columnNames = {"ID", "Tanggal", "Restoran", "Total Harga", "Status", "Detail"};
        transactionTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Kolom Detail bisa diklik
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 5) {
                    return JButton.class;
                }
                return Object.class;
            }
        };

        // setup Tabel
        transactionTable = new JTable(transactionTableModel);
        transactionTable.setFont(new Font("Bahnschrift", Font.PLAIN, 12));
        transactionTable.setRowHeight(35);
        transactionTable.setBackground(new Color(250, 240, 227));
        transactionTable.setSelectionBackground(new Color(241, 124, 42, 100));
        transactionTable.setSelectionForeground(new Color(59, 31, 11));

        // center alignment untuk semua kolom
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < transactionTable.getColumnCount(); i++) {
            if (i != 2 && i != 5) { // Kolom Restoran dan Detail tidak di-center
                transactionTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // left alignment untuk kolom resto
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        transactionTable.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);

        // custom renderer untuk kolom detail (button)
        transactionTable.getColumnModel().getColumn(5).setCellRenderer(new DetailButtonRenderer());
        transactionTable.getColumnModel().getColumn(5).setCellEditor(new DetailButtonEditor(new JCheckBox()));

        // styling header
        JTableHeader header = transactionTable.getTableHeader();
        header.setFont(new Font("Bahnschrift", Font.BOLD, 13));
        header.setBackground(new Color(241, 124, 42));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // setup scroll pane
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 75, 31), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        scrollPane.getViewport().setBackground(new Color(250, 240, 227));

        // tambahkan ke tab Transaksi
        jPanel4.removeAll();
        jPanel4.setLayout(new BorderLayout());
        jPanel4.setBackground(new Color(250, 240, 227));
        jPanel4.add(scrollPane, BorderLayout.CENTER);

        System.out.println("Transaction panel setup completed");
    }

    // Custom renderer untuk tombol detail
    class DetailButtonRenderer extends JButton implements TableCellRenderer {
        public DetailButtonRenderer() {
            setOpaque(true);
            setText("Lihat Detail");
            setBackground(new Color(229, 75, 31));
            setForeground(Color.WHITE);
            setFont(new Font("Bahnschrift", Font.PLAIN, 11));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(new Color(241, 124, 42));
            } else {
                setBackground(new Color(229, 75, 31));
            }
            return this;
        }
    }

    // Custom editor untuk tombol detail
    class DetailButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int selectedRow;

        public DetailButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(new Color(229, 75, 31));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Bahnschrift", Font.PLAIN, 11));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(true);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    showTransactionDetail(selectedRow);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "Lihat Detail" : value.toString();
            button.setText(label);
            isPushed = true;
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    // Method untuk menampilkan detail transaksi
    private void showTransactionDetail(int rowIndex) {
        try {
            // Ambil ID transaksi dari kolom pertama (format: TRX0001)
            String idString = transactionTableModel.getValueAt(rowIndex, 0).toString();
            int idTransaksi = Integer.parseInt(idString.replace("TRX", ""));

            System.out.println("Menampilkan detail transaksi ID: " + idTransaksi);

            // Ambil data detail transaksi dari service
            List<TransactionDetail> details = transactionService.getTransactionDetails(idTransaksi);

            if (details.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Tidak ditemukan detail untuk transaksi ini.", 
                    "Detail Transaksi", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Buat dialog untuk menampilkan detail
            JDialog detailDialog = new JDialog(this, "Detail Pesanan", true);
            detailDialog.setLayout(new BorderLayout());
            detailDialog.setSize(500, 400);
            detailDialog.setLocationRelativeTo(this);

            // Panel header
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(241, 124, 42));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            JLabel headerLabel = new JLabel("Detail Pesanan #" + idString);
            headerLabel.setFont(new Font("Bahnschrift", Font.BOLD, 16));
            headerLabel.setForeground(Color.WHITE);
            headerPanel.add(headerLabel, BorderLayout.WEST);

            // Ambil info restoran dan total
            String namaRestoran = transactionTableModel.getValueAt(rowIndex, 2).toString();
            String totalHarga = transactionTableModel.getValueAt(rowIndex, 3).toString();
            String status = transactionTableModel.getValueAt(rowIndex, 4).toString();

            JLabel infoLabel = new JLabel(namaRestoran + " | " + totalHarga + " | " + status);
            infoLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 12));
            infoLabel.setForeground(Color.WHITE);
            headerPanel.add(infoLabel, BorderLayout.EAST);

            // Panel detail menu
            JPanel menuPanel = new JPanel();
            menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
            menuPanel.setBackground(Color.WHITE);
            menuPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            int totalItems = 0;
            int totalPrice = 0;

            for (TransactionDetail detail : details) {
                JPanel itemPanel = new JPanel(new BorderLayout());
                itemPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
                itemPanel.setBackground(Color.WHITE);

                JLabel menuLabel = new JLabel(detail.getNamaMenu());
                menuLabel.setFont(new Font("Bahnschrift", Font.BOLD, 13));

                JLabel detailLabel = new JLabel("Jumlah: " + detail.getJumlah() + 
                                               " x Rp " + String.format("%,d", detail.getHargaSatuan()) + 
                                               " = Rp " + String.format("%,d", detail.getSubtotal()));
                detailLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 12));

                JPanel leftPanel = new JPanel(new BorderLayout());
                leftPanel.setBackground(Color.WHITE);
                leftPanel.add(menuLabel, BorderLayout.NORTH);
                leftPanel.add(detailLabel, BorderLayout.SOUTH);

                itemPanel.add(leftPanel, BorderLayout.CENTER);
                menuPanel.add(itemPanel);
                menuPanel.add(Box.createVerticalStrut(8));

                totalItems += detail.getJumlah();
                totalPrice += detail.getSubtotal();
            }

            // Panel total
            JPanel totalPanel = new JPanel(new BorderLayout());
            totalPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(229, 75, 31)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            totalPanel.setBackground(new Color(250, 240, 227));

            JLabel totalItemsLabel = new JLabel("Total Items: " + totalItems);
            totalItemsLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 12));

            JLabel totalPriceLabel = new JLabel("Total: Rp " + String.format("%,d", totalPrice));
            totalPriceLabel.setFont(new Font("Bahnschrift", Font.BOLD, 14));
            totalPriceLabel.setForeground(new Color(59, 31, 11));

            totalPanel.add(totalItemsLabel, BorderLayout.WEST);
            totalPanel.add(totalPriceLabel, BorderLayout.EAST);

            // Scroll pane untuk menu
            JScrollPane scrollPane = new JScrollPane(menuPanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(Color.WHITE);

            // Tambahkan semua komponen ke dialog
            detailDialog.add(headerPanel, BorderLayout.NORTH);
            detailDialog.add(scrollPane, BorderLayout.CENTER);
            detailDialog.add(totalPanel, BorderLayout.SOUTH);

            detailDialog.setVisible(true);

        } catch (Exception e) {
            System.out.println("Error menampilkan detail transaksi: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error menampilkan detail: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tabChanged(javax.swing.event.ChangeEvent evt) {
        int selectedIndex = jTabbedPane1.getSelectedIndex();
        if (selectedIndex == 1 && userLoginId > 0) { // tab keranjang
            loadKeranjang();
        } else if (selectedIndex == 2 && userLoginId > 0) { // tab transaksi
            loadTransactions();
        }
    }
    
    private void applyDashboardTheme() {
        // bg utama #FAF0E3
        getContentPane().setBackground(new Color(250, 240, 227));

        // tabbed pane 
        jTabbedPane1.setBackground(new Color(250, 240, 227));
        jTabbedPane1.setForeground(new Color(59, 31, 11)); // #3B1F0B
        jTabbedPane1.setBorder(BorderFactory.createLineBorder(new Color(229, 75, 31), 1)); // #E54B1F

        // UI custom utk tab
        jTabbedPane1.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, 
                                            int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isSelected) {
                    // tab aktif (orange)
                    g2d.setColor(new Color(241, 124, 42)); // #F17C2A
                } else {
                    // tab tidak aktif (default)
                    g2d.setColor(new Color(250, 240, 227)); // #FAF0E3
                }

                // rounded
                int margin = 8; // jarak antar tab 
                int leftMargin = 15; // geser kanan
                int tabHeight = h - margin; // tinggi tab
                int tabWidth = w - 10; // lebar tab
                g2d.fillRoundRect(x + leftMargin, y + margin, tabWidth, tabHeight, 15, 15);
                g2d.dispose();
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, 
                                        int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isSelected) {
                    g2d.setColor(new Color(229, 75, 31)); // #E54B1F
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
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                
            }

            @Override
            protected int getTabRunIndent(int tabPlacement, int run) {
                return 15; // indentasi utk geser tab ke atas
            }

            @Override
            protected Insets getTabAreaInsets(int tabPlacement) {
                return new Insets(15, 10, 0, 0); // top, left, bottom, right
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

        // panel tab 
        jPanel1.setBackground(new Color(250, 240, 227));
        jPanel3.setBackground(new Color(250, 240, 227));
        jPanel4.setBackground(new Color(250, 240, 227));

        // scroll pane (no border)
        restoScrollPane.setBackground(new Color(250, 240, 227));
        restoScrollPane.setBorder(BorderFactory.createEmptyBorder());
        restoScrollPane.getViewport().setBackground(new Color(250, 240, 227));

        // halo nama output 
        haloNamaOutput.setBackground(new Color(250, 240, 227));
        haloNamaOutput.setForeground(new Color(59, 31, 11));
        haloNamaOutput.setBorder(null);

        // button 
        profilButton.setBackground(new Color(241, 124, 42));
        profilButton.setForeground(Color.WHITE);
        profilButton.setFocusPainted(false);
        profilButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        logoutButton.setBackground(new Color(229, 75, 31));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }
    
    private void customizeTabbedPane() {
        // custom UI utk tabbed pane
        jTabbedPane1.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                highlight = new Color(241, 124, 42); // #F17C2A
                lightHighlight = new Color(250, 240, 227); // #FAF0E3
                shadow = new Color(229, 75, 31); // #E54B1F
                darkShadow = new Color(59, 31, 11); // #3B1F0B
                focus = new Color(241, 124, 42); // #F17C2A
            }

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, 
                                            int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) {
                    g.setColor(new Color(241, 124, 42)); // tab aktif
                } else {
                    g.setColor(new Color(250, 240, 227)); // tab tidak aktif
                }
                g.fillRect(x, y, w, h);
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, 
                                        int x, int y, int w, int h, boolean isSelected) {
                g.setColor(new Color(229, 75, 31)); // #E54B1F untuk border
                g.drawRect(x, y, w, h);
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                int width = tabPane.getWidth();
                int height = tabPane.getHeight();
                Insets insets = tabPane.getInsets();

                int x = insets.left;
                int y = insets.top;
                int w = width - insets.right - insets.left;
                int h = height - insets.top - insets.bottom;

                g.setColor(new Color(229, 75, 31)); // #E54B1F untuk border content
                g.drawRect(x, y, w, h);
            }
        });

        // set font dan warna tab
        jTabbedPane1.setFont(new java.awt.Font("Bahnschrift", 1, 12));
        jTabbedPane1.setForeground(new Color(59, 31, 11)); // #3B1F0B

        // background tab yang tidak aktif
        jTabbedPane1.setBackgroundAt(0, new Color(250, 240, 227));
        jTabbedPane1.setBackgroundAt(1, new Color(250, 240, 227));
        jTabbedPane1.setBackgroundAt(2, new Color(250, 240, 227));

        // foreground tab
        jTabbedPane1.setForegroundAt(0, new Color(59, 31, 11));
        jTabbedPane1.setForegroundAt(1, new Color(59, 31, 11));
        jTabbedPane1.setForegroundAt(2, new Color(59, 31, 11));
    }
    
    public void setUserData(User user) {
        this.currentUser = user; // SIMPAN USER OBJECT
        this.userLoginId = user.getIdUser(); // SIMPAN ID USER
        haloNamaOutput.setText("Halo, " + user.getNama() + "! Ayo makan!");
        loadKeranjang(); // Load keranjang setelah user ID diset
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        restoScrollPane = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        haloNamaOutput = new javax.swing.JTextField();
        profilButton = new javax.swing.JButton();
        logoutButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(250, 240, 227));

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane1.setFont(new java.awt.Font("Bahnschrift", 1, 12)); // NOI18N

        restoScrollPane.setBorder(null);
        restoScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(restoScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(restoScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Resto", jPanel1);

        jPanel3.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jPanel3AncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 725, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 391, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Keranjang", jPanel3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 725, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 391, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Transaksi", jPanel4);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/JatoStyle/gui/dashboardlogo.png"))); // NOI18N

        haloNamaOutput.setEditable(false);
        haloNamaOutput.setBackground(new java.awt.Color(250, 240, 227));
        haloNamaOutput.setFont(new java.awt.Font("Bahnschrift", 1, 20)); // NOI18N
        haloNamaOutput.setForeground(new java.awt.Color(250, 240, 227));
        haloNamaOutput.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        haloNamaOutput.setBorder(null);
        haloNamaOutput.setDisabledTextColor(new java.awt.Color(59, 31, 11));
        haloNamaOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                haloNamaOutputActionPerformed(evt);
            }
        });

        profilButton.setText("👤");
        profilButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profilButtonActionPerformed(evt);
            }
        });

        logoutButton.setFont(new java.awt.Font("Bahnschrift", 1, 12)); // NOI18N
        logoutButton.setText("Logout");
        logoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(26, 26, 26)
                        .addComponent(haloNamaOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 536, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(logoutButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(profilButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(31, 31, 31))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(logoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(profilButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(haloNamaOutput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addGap(26, 26, 26)))
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void setupRestoContainer() {
        restoContainerPanel = new javax.swing.JPanel();
        restoContainerPanel.setLayout(new javax.swing.BoxLayout(restoContainerPanel, javax.swing.BoxLayout.Y_AXIS));
        restoContainerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        restoContainerPanel.setBackground(new Color(250, 240, 227));

        restoScrollPane.setViewportView(restoContainerPanel);
        restoScrollPane.getViewport().setBackground(new Color(250, 240, 227));

        restoScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // custom scroll bar
        javax.swing.JScrollBar verticalScrollBar = restoScrollPane.getVerticalScrollBar();
        verticalScrollBar.setBackground(new Color(250, 240, 227));
        verticalScrollBar.setForeground(new Color(229, 75, 31));
        verticalScrollBar.setUnitIncrement(16); // smooth scrolling
    }
        
    private boolean isRestoBuka(Restoran resto, java.sql.Time currentTime) {
        try {
            if (resto.getJamBuka() == null || resto.getJamTutup() == null) {
                return false;
            }

            // konversi java.sql.Time ke java.time.LocalTime
            java.time.LocalTime current = currentTime.toLocalTime();
            java.time.LocalTime jamBuka = resto.getJamBuka().toLocalTime();
            java.time.LocalTime jamTutup = resto.getJamTutup().toLocalTime();

            System.out.println("Cek: " + resto.getNamaRestoran() + 
                             " | Buka: " + jamBuka + 
                             " | Tutup: " + jamTutup + 
                             " | Sekarang: " + current);

            // cek jika buka 24 jam
            if (jamBuka.equals(java.time.LocalTime.of(0, 0)) && 
                jamTutup.equals(java.time.LocalTime.of(23, 59, 59))) {
                System.out.println("  -> 24 Jam: BUKA");
                return true;
            }

            // jika jam tutup >= jam buka (normal case)
            if (!jamTutup.isBefore(jamBuka)) {
                // Current time antara buka dan tutup
                boolean buka = !current.isBefore(jamBuka) && !current.isAfter(jamTutup);
                System.out.println("  -> Normal: " + (buka ? "BUKA" : "TUTUP"));
                return buka;
            } 
            // Jika jam tutup < jam buka (lewat tengah malam)
            else {
                // Current time setelah buka ATAU sebelum tutup
                boolean buka = !current.isBefore(jamBuka) || !current.isAfter(jamTutup);
                System.out.println("  -> Malam: " + (buka ? "BUKA" : "TUTUP"));
                return buka;
            }

        } catch (Exception e) {
            System.out.println("Error cek status restoran " + resto.getNamaRestoran() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void setupKeranjangContainer() {
        keranjangContainer = new JPanel();
        keranjangContainer.setLayout(new BoxLayout(keranjangContainer, BoxLayout.Y_AXIS));
        keranjangContainer.setBackground(new Color(250, 240, 227));

        JScrollPane scroll = new JScrollPane(keranjangContainer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(250, 240, 227));

        jPanel3.setLayout(new BorderLayout());
        jPanel3.add(scroll, BorderLayout.CENTER);
       }
        
    private void loadKeranjang() {
        keranjangContainer.removeAll();

        // cek jika userLoginId belum diset
        if (userLoginId == 0) {
            showKeranjangKosongMessage();
            return;
        }

        List<Keranjang> list = keranjangService.getKeranjangUser(userLoginId);

        // cek jika keranjang kosong
        if (list.isEmpty()) {
            showKeranjangKosongMessage();
            return;
        }

        // group items by restaurant
        Map<String, List<Keranjang>> groupedByResto = new HashMap<>();
        int total = 0;

        // grouping items
        for (int i = 0; i < list.size(); i++) {
            Keranjang k = list.get(i);
            String namaResto = k.getNamaRestoran();
            if (!groupedByResto.containsKey(namaResto)) {
                groupedByResto.put(namaResto, new ArrayList<>());
            }
            groupedByResto.get(namaResto).add(k);

            total += k.getJumlah() * k.getHargaMenu();
        }

        // UI untuk masing-masing resto
        Set<String> restoKeys = groupedByResto.keySet();
        String[] restoArray = restoKeys.toArray(new String[0]);

        for (int i = 0; i < restoArray.length; i++) {
            String namaResto = restoArray[i];
            List<Keranjang> items = groupedByResto.get(namaResto);

            // resto header
            keranjangContainer.add(createRestoHeader(namaResto));
            keranjangContainer.add(Box.createVerticalStrut(10));
            
            for (int j = 0; j < items.size(); j++) {
                Keranjang k = items.get(j);
                keranjangContainer.add(createKeranjangItem(k));
                keranjangContainer.add(Box.createVerticalStrut(8)); 
            }

            // tambah separator antar resto
            keranjangContainer.add(Box.createVerticalStrut(15));
        }

        if (keranjangContainer.getComponentCount() > 0) {
            keranjangContainer.remove(keranjangContainer.getComponentCount() - 1);
        }

        keranjangContainer.add(createTotalPanel(total));
        keranjangContainer.add(createCheckoutButton());

        keranjangContainer.revalidate();
        keranjangContainer.repaint();
    }
    
    private void showKeranjangKosongMessage() {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
        messagePanel.setBackground(new Color(250, 240, 227));

        JLabel messageLabel = new JLabel("Keranjang masih kosong");
        messageLabel.setFont(new java.awt.Font("Bahnschrift", 1, 16));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setForeground(new Color(128, 128, 128));

        messagePanel.add(messageLabel, BorderLayout.CENTER);
        keranjangContainer.add(messagePanel);

        keranjangContainer.revalidate();
        keranjangContainer.repaint();
    }

    private JPanel createKeranjangItem(Keranjang k) {
    // panel utama
    JPanel panel = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // bg rounded corners
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            // border rounded corners
            g2d.setColor(new Color(229, 75, 31)); // #E54B1F
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

            g2d.dispose();
        }
    };

    panel.setOpaque(false);
    panel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
    panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, 70)); // FIXED HEIGHT
    panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70)); // FIXED MAX HEIGHT

    // panel kiri untuk menu
    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
    leftPanel.setOpaque(false);
    leftPanel.setPreferredSize(new Dimension(300, 50)); 

    JLabel namaMenu = new JLabel(k.getNamaMenu());
    namaMenu.setFont(new Font("Bahnschrift", Font.BOLD, 14));
    namaMenu.setForeground(new Color(59, 31, 11));

    JLabel hargaLabel = new JLabel("Rp " + String.format("%,d", k.getHargaMenu()));
    hargaLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 12));
    hargaLabel.setForeground(new Color(59, 31, 11));

    leftPanel.add(namaMenu);
    leftPanel.add(Box.createVerticalStrut(3)); 
    leftPanel.add(hargaLabel);

    // panel kanan untuk kontrol jumlah
    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    rightPanel.setOpaque(false);
    rightPanel.setPreferredSize(new Dimension(200, 40));

    JLabel jumlahLabel = new JLabel(String.valueOf(k.getJumlah()));
    jumlahLabel.setFont(new Font("Bahnschrift", Font.BOLD, 14));
    jumlahLabel.setForeground(new Color(59, 31, 11));
    jumlahLabel.setPreferredSize(new Dimension(30, 20));

    // button dengan ukuran konsisten
    JButton minusButton = createRoundedButton("-", new Color(229, 75, 31));
    minusButton.setPreferredSize(new Dimension(30, 25));
    
    JButton plusButton = createRoundedButton("+", new Color(229, 75, 31));
    plusButton.setPreferredSize(new Dimension(30, 25));
    
    JButton hapusButton = createRoundedButton("X", new Color(200, 0, 0));
    hapusButton.setPreferredSize(new Dimension(30, 25));

    // action listeners
    minusButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (k.getJumlah() > 1) {
                keranjangService.updateJumlah(k.getIdKeranjang(), k.getJumlah() - 1);
                loadKeranjang();
            }
        }
    });

    plusButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            keranjangService.updateJumlah(k.getIdKeranjang(), k.getJumlah() + 1);
            loadKeranjang();
        }
    });

    hapusButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            keranjangService.deleteItem(k.getIdKeranjang());
            loadKeranjang();
        }
    });

    rightPanel.add(minusButton);
    rightPanel.add(jumlahLabel);
    rightPanel.add(plusButton);
    rightPanel.add(hapusButton);

    panel.add(leftPanel, BorderLayout.WEST);
    panel.add(rightPanel, BorderLayout.EAST);

    return panel;
}
    
    private JPanel createRestoHeader(String namaResto) {
    // panel header 
    JPanel headerPanel = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // bg rounded corners
            g2d.setColor(new Color(241, 124, 42)); // #F17C2A
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

            // border rounded corners
            g2d.setColor(new Color(229, 75, 31)); // #E54B1F
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

            g2d.dispose();
        }
    };

    headerPanel.setOpaque(false);
    headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    headerPanel.setPreferredSize(new Dimension(headerPanel.getPreferredSize().width, 45)); // FIXED HEIGHT
    headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); // FIXED MAX HEIGHT

    JLabel restoLabel = new JLabel(namaResto);
    restoLabel.setFont(new Font("Bahnschrift", Font.BOLD, 14)); // Smaller font
    restoLabel.setForeground(Color.WHITE);

    headerPanel.add(restoLabel, BorderLayout.WEST);

    return headerPanel;
}

    private JPanel createTotalPanel(int total) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(new Color(250, 240, 227));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        JLabel totalLabel = new JLabel("Total: Rp " + total);
        totalLabel.setFont(new java.awt.Font("Bahnschrift", 1, 18));
        totalLabel.setForeground(new Color(59, 31, 11));

        panel.add(totalLabel);
        return panel;
    }
    
    private JPanel createCheckoutButton() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(new Color(250, 240, 227));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // tombol checkout dengan rounded corners
        JButton checkoutButton = new JButton("Checkout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // bg rounded
                g2d.setColor(new Color(229, 75, 31)); // #E54B1F
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // text
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), textX, textY);

                g2d.dispose();
            }

            @Override
            public void setBorder(javax.swing.border.Border border) {
            }
        };

        checkoutButton.setOpaque(false);
        checkoutButton.setContentAreaFilled(false);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setFont(new java.awt.Font("Bahnschrift", 1, 14));
        checkoutButton.setPreferredSize(new java.awt.Dimension(120, 40));

        checkoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleCheckoutAction();
            }
        });

        panel.add(checkoutButton);
        return panel;
    }
    
    private void handleCheckoutAction() {
        // keranjangService.getKeranjangUser() mengambil item keranjang
        List<Keranjang> currentCart = keranjangService.getKeranjangUser(userLoginId);

        if (currentCart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang Anda kosong!", "Checkout Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int totalSum = 0;
        for (int i = 0; i < currentCart.size(); i++) {
            Keranjang k = currentCart.get(i);
            totalSum += k.getJumlah() * k.getHargaMenu();
        }
        
        transactionService.checkRestoranData();

        int dialogResult = JOptionPane.showConfirmDialog(this, 
            "Total pesanan: Rp " + String.format("%,d", totalSum) + "\nLanjutkan pembayaran?", 
            "Konfirmasi Pesanan", JOptionPane.YES_NO_OPTION);

        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                // 1. proses transaksi (membuat entri baru)
                boolean success = transactionService.createTransactionFromKeranjang(userLoginId, currentCart);

                if (success) {
                    // 2. hapus Keranjang & refresh tampilan keranjang
                    keranjangService.clearAll(userLoginId); 

                    JOptionPane.showMessageDialog(this, "Pesanan berhasil dibuat! Cek di Tab Riwayat Transaksi.", "Transaksi Berhasil", JOptionPane.INFORMATION_MESSAGE);

                    // pindah ke tab transaksi (index 2) dan muat ulang data
                    jTabbedPane1.setSelectedIndex(2);
                    loadTransactions(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal membuat transaksi. Cek log.", "Transaksi Gagal", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                logger.severe("Error saat checkout: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memproses transaksi.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTransactions() {
        if (userLoginId == 0) {
            System.out.println("User ID belum diset, skip load transactions");
            return;
        }

        System.out.println("=== DEBUG: Memulai loadTransactions untuk user ID: " + userLoginId);

        // cek jika panel transaksi sudah di-setup
        if (jPanel4 == null) {
            System.out.println("ERROR: jPanel4 is null");
            return;
        }

        // setup transaction panel jika belum
        if (transactionTableModel == null) {
            System.out.println("Setting up transaction panel...");
            setupTransactionPanel();
        }

        // clear existing data
        transactionTableModel.setRowCount(0);
        System.out.println("Cleared table model, row count: " + transactionTableModel.getRowCount());

        try {
            List<Transaction> transactions = transactionService.getTransactionsByUser(userLoginId);
            System.out.println("Retrieved " + transactions.size() + " transactions from service");

            if (transactions.isEmpty()) {
                System.out.println("No transactions found, showing empty message");
                showNoTransactionMessage();
                return;
            }

            // add data to table
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            for (int i = 0; i < transactions.size(); i++) {
                Transaction t = transactions.get(i);
                System.out.println("Adding to table: " + t.getIdTransaksi() + " - " + t.getNamaRestoran());

                transactionTableModel.addRow(new Object[]{
                    "TRX" + String.format("%04d", t.getIdTransaksi()),
                    dateFormat.format(t.getTanggalTransaksi()), 
                    t.getNamaRestoran(),
                    String.format("Rp %,d", t.getTotalPembayaran()),
                    t.getStatusTransaksi(),
                    "Lihat Detail"  // TAMBAHKAN TOMBOL DETAIL
                });
            }

            System.out.println("Table model row count after adding: " + transactionTableModel.getRowCount());

            // auto resize columns
            if (transactionTable != null) {
                transactionTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

                // set preferred column widths
                transactionTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
                transactionTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Tanggal
                transactionTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Restoran
                transactionTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Total Harga
                transactionTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Status
                transactionTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Detail

                System.out.println("Table configured successfully");
            } else {
                System.out.println("ERROR: transactionTable is null");
            }

            // UI refresh
            jPanel4.revalidate();
            jPanel4.repaint();
            System.out.println("Panel refreshed");

        } catch (Exception e) {
            System.out.println("ERROR di loadTransactions: " + e.getMessage());
            e.printStackTrace();
            showNoTransactionMessage();
        }
    }
    
    private String formatStatus(String status) {
        if (status == null) return "PENDING";
        return status;
    }

    private void showNoTransactionMessage() {
    System.out.println("Showing no transaction message");
    
    jPanel4.removeAll();
    jPanel4.setLayout(new BorderLayout());
    jPanel4.setBackground(new Color(250, 240, 227));

    JPanel messagePanel = new JPanel();
    messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
    messagePanel.setBackground(new Color(250, 240, 227));
    messagePanel.setBorder(BorderFactory.createEmptyBorder(100, 20, 100, 20));

    JLabel messageLabel = new JLabel("Belum ada riwayat transaksi");
    messageLabel.setFont(new Font("Bahnschrift", Font.BOLD, 16));
    messageLabel.setForeground(new Color(128, 128, 128));
    messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel subMessageLabel = new JLabel("Transaksi yang Anda lakukan akan muncul di sini");
    subMessageLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 12));
    subMessageLabel.setForeground(new Color(160, 160, 160));
    subMessageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    subMessageLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

    messagePanel.add(messageLabel);
    messagePanel.add(subMessageLabel);

    jPanel4.add(messagePanel, BorderLayout.CENTER);
    
    jPanel4.revalidate();
    jPanel4.repaint();
    System.out.println("No transaction message displayed");
}

    private void loadRestoFromDatabase() {
        if (restoContainerPanel == null) {
            return;
        }

        restoContainerPanel.removeAll();

        try {
            System.out.println("Loading restoran dari database...");

            List<Restoran> restoranList = authService.getAllRestoran();

            if (restoranList.isEmpty()) {
                System.out.println("Tidak ada data restoran ditemukan");
                showNoDataMessage();
                return;
            }

            System.out.println("Ditemukan " + restoranList.size() + " restoran");

            // waktu sekarang
            java.util.Date now = new java.util.Date();
            java.sql.Time currentTime = new java.sql.Time(now.getTime());

            System.out.println("Waktu sekarang: " + currentTime);

            for (int i = 0; i < restoranList.size(); i++) {
                Restoran resto = restoranList.get(i);
                String jamBuka = "08:00";
                String jamTutup = "22:00";

                if (resto.getJamBuka() != null) {
                    jamBuka = resto.getJamBuka().toString().substring(0, 5);
                }
                if (resto.getJamTutup() != null) {
                    jamTutup = resto.getJamTutup().toString().substring(0, 5);
                }

                String jamOperasional = jamBuka + " - " + jamTutup;

                // cek apakah resto sedang buka atau tutup
                boolean isBuka = isRestoBuka(resto, currentTime);

                // tampilkan status setiap restoran
                System.out.println("Resto: " + resto.getNamaRestoran() + 
                                 " | Jam: " + jamOperasional + 
                                 " | Status: " + (isBuka ? "BUKA" : "TUTUP") +
                                 " | DB Buka: " + resto.getJamBuka() +
                                 " | DB Tutup: " + resto.getJamTutup());

                javax.swing.JPanel restoPanel = createRestoPanel(
                    resto.getIdRestoran(), 
                    resto.getNamaRestoran(), 
                    jamOperasional,
                    isBuka
                );
                restoContainerPanel.add(restoPanel);

                restoContainerPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 10)));
            }

            restoContainerPanel.revalidate();
            restoContainerPanel.repaint();

            System.out.println("Berhasil menampilkan " + restoranList.size() + " restoran");

        } catch (Exception ex) {
            System.out.println("Error loading resto from database: " + ex.getMessage());
            ex.printStackTrace();
            showErrorMessage();
        }
    }
    
    private void showNoDataMessage() {
        javax.swing.JPanel messagePanel = new javax.swing.JPanel();
        messagePanel.setLayout(new java.awt.BorderLayout());
        messagePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(50, 20, 50, 20));
        
        javax.swing.JLabel messageLabel = new javax.swing.JLabel("Tidak ada restoran tersedia saat ini");
        messageLabel.setFont(new java.awt.Font("Bahnschrift", 1, 16));
        messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        messageLabel.setForeground(new java.awt.Color(128, 128, 128));
        
        messagePanel.add(messageLabel, java.awt.BorderLayout.CENTER);
        restoContainerPanel.add(messagePanel);
        
        restoContainerPanel.revalidate();
        restoContainerPanel.repaint();
    }
    
    private void showErrorMessage() {
        javax.swing.JPanel messagePanel = new javax.swing.JPanel();
        messagePanel.setLayout(new java.awt.BorderLayout());
        messagePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(50, 20, 50, 20));
        
        javax.swing.JLabel messageLabel = new javax.swing.JLabel("Error: Tidak dapat terhubung ke database");
        messageLabel.setFont(new java.awt.Font("Bahnschrift", 1, 16));
        messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        messageLabel.setForeground(new java.awt.Color(255, 0, 0));
        
        messagePanel.add(messageLabel, java.awt.BorderLayout.CENTER);
        restoContainerPanel.add(messagePanel);
        
        restoContainerPanel.revalidate();
        restoContainerPanel.repaint();
    }
    
    private javax.swing.JPanel createRestoPanel(int idResto, String namaResto, String jamOperasional, boolean isBuka) {
        // panel utama rounded corners
        javax.swing.JPanel mainPanel = new javax.swing.JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // bg rounded corners
                if (!isBuka) {
                    g2d.setColor(new Color(245, 245, 245)); // tutup
                } else {
                    g2d.setColor(Color.WHITE); // buka
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // border rounded corners
                g2d.setColor(new Color(229, 75, 31)); // #E54B1F
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

                g2d.dispose();
            }
        };

        mainPanel.setLayout(new java.awt.BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setPreferredSize(new java.awt.Dimension(650, 90)); 
        mainPanel.setMaximumSize(new java.awt.Dimension(650, 90));   

        // nama resto field 
        javax.swing.JTextField namaField = new javax.swing.JTextField(namaResto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // bg rounded
                if (!isBuka) {
                    g2d.setColor(new Color(245, 245, 245));
                } else {
                    g2d.setColor(Color.WHITE);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // set text
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();

                // set warna text disesuaikan dgn status buka/tutup
                if (!isBuka) {
                    g2d.setColor(new Color(128, 128, 128));
                } else {
                    g2d.setColor(new Color(59, 31, 11));
                }
                
                int textX = 20; // geser kanan
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() - 2; 

                g2d.drawString(getText(), textX, textY);

                g2d.dispose();
            }

            @Override
            public void setBorder(javax.swing.border.Border border) {
            }

            @Override
            public java.awt.Dimension getPreferredSize() {
                return new java.awt.Dimension(650, 40); 
            }
        };

        namaField.setEditable(false);
        namaField.setFont(new java.awt.Font("Century Gothic", 1, 18));
        namaField.setOpaque(false);

        // jam operasional field 
        javax.swing.JTextField jamField = new javax.swing.JTextField("Jam Operasional: " + jamOperasional) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // bg rounded
                if (!isBuka) {
                    g2d.setColor(new Color(245, 245, 245));
                } else {
                    g2d.setColor(Color.WHITE);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                //set text 
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();

                // set warna text disesuaikan dgn status buka/tutup
                if (!isBuka) {
                    g2d.setColor(new Color(128, 128, 128));
                } else {
                    g2d.setColor(new Color(59, 31, 11));
                }

                int textX = 20; // geser kanan
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() - 1; 

                g2d.drawString(getText(), textX, textY);

                g2d.dispose();
            }

            @Override
            public void setBorder(javax.swing.border.Border border) {
            }

            @Override
            public java.awt.Dimension getPreferredSize() {
                return new java.awt.Dimension(650, 35); 
            }
        };

        jamField.setEditable(false);
        jamField.setFont(new java.awt.Font("Century Gothic", 0, 12));
        jamField.setOpaque(false);

        // action button rounded corners
        javax.swing.JButton actionButton = new javax.swing.JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // bg rounded berdasarkan status
                if (isBuka) {
                    g2d.setColor(new Color(241, 124, 42)); // Orange untuk buka
                } else {
                    g2d.setColor(new Color(160, 160, 160)); // Abu-abu untuk tutup
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // text
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();

                String text;
                if (isBuka) {
                    text = "Pesan Disini!";
                } else {
                    text = "Tutup";
                }

                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(text, textX, textY);

                g2d.dispose();
            }

            @Override
            public void setBorder(javax.swing.border.Border border) {
            }
        };

        // set font button berdasarkan status
        if (isBuka) {
            actionButton.setFont(new java.awt.Font("Bahnschrift", 1, 10));
        } else {
            actionButton.setFont(new java.awt.Font("Bahnschrift", 1, 12));
        }

        actionButton.setOpaque(false);
        actionButton.setContentAreaFilled(false);
        actionButton.setFocusPainted(false);
        actionButton.setPreferredSize(new java.awt.Dimension(100, 30));

        if (isBuka) {
            actionButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    bukaRestoranFrame(idResto, namaResto);
                }
            });
        } else {
            actionButton.setEnabled(false);
        }

        // layout
        javax.swing.JPanel namaPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
        namaPanel.setOpaque(false);
        namaPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0)); 
        namaPanel.add(namaField, java.awt.BorderLayout.CENTER);

        mainPanel.add(namaPanel, java.awt.BorderLayout.NORTH);

        javax.swing.JPanel bottomPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 8, 15)); 

        bottomPanel.add(jamField, java.awt.BorderLayout.CENTER);
        bottomPanel.add(actionButton, java.awt.BorderLayout.EAST);

        mainPanel.add(bottomPanel, java.awt.BorderLayout.SOUTH);

        return mainPanel;
    }
    
    private void bukaRestoranFrame(int idResto, String namaResto) {
        try {
            // ambil data restoran lengkap dari database
            List<Restoran> restoranList = authService.getAllRestoran();
            Restoran selectedRestoran = null;

            // cari restoran berdasarkan ID
            for (int i = 0; i < restoranList.size(); i++) {
                Restoran r = restoranList.get(i);
                if (r.getIdRestoran() == idResto) {
                    selectedRestoran = r;
                    break;
                }
            }

            if (selectedRestoran != null && currentUser != null) {
                // buka RestoranFrame dengan data user dan restoran
                RestoranFrame restoranFrame = new RestoranFrame(currentUser, selectedRestoran);
                restoranFrame.setVisible(true);
                this.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Data restoran atau user tidak valid", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Error Membuka Restoran", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void refreshRestoData() {
        loadRestoFromDatabase();
    }
    
    private void haloNamaOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_haloNamaOutputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_haloNamaOutputActionPerformed

    private void profilButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profilButtonActionPerformed
        ProfileFrame pf = new ProfileFrame(currentUser, this); // kirim data user + frame dashboard
        pf.setVisible(true);
        this.setVisible(false); 
    }//GEN-LAST:event_profilButtonActionPerformed

    private void logoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutButtonActionPerformed
            int confirm = javax.swing.JOptionPane.showConfirmDialog(
            this, 
            "Apakah Anda yakin ingin logout?", 
            "Konfirmasi Logout", 
            javax.swing.JOptionPane.YES_NO_OPTION
        );

        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            // tutup dashboard
            this.dispose();

            // buka lagi login frame
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new JatoStyle.gui.LoginFrame().setVisible(true);
                }
            });

            System.out.println("User logged out successfully");
        }
    }//GEN-LAST:event_logoutButtonActionPerformed

    private JButton createRoundedButton(String text, Color backgroundColor) {
    JButton button = new JButton(text) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // bg rounded
            g2d.setColor(backgroundColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12); // Smaller radius

            // text
            g2d.setColor(Color.WHITE);
            g2d.setFont(getFont());
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(getText())) / 2;
            int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(getText(), textX, textY);

            g2d.dispose();
        }

        @Override
        public void setBorder(javax.swing.border.Border border) {
        }
    };

    button.setOpaque(false);
    button.setContentAreaFilled(false);
    button.setFocusPainted(false);
    button.setPreferredSize(new Dimension(30, 25)); // CONSISTENT SIZE
    button.setFont(new Font("Bahnschrift", Font.BOLD, 12));

    return button;
}
    
    private void jPanel3AncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jPanel3AncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel3AncestorAdded

 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField haloNamaOutput;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton logoutButton;
    private javax.swing.JButton profilButton;
    private javax.swing.JScrollPane restoScrollPane;
    // End of variables declaration//GEN-END:variables
}
