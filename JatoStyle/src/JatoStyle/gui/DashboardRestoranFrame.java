/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package JatoStyle.gui;

import JatoStyle.models.Toko;
import JatoStyle.services.AuthService;
import java.awt.*;
import javax.swing.*;
import java.sql.ResultSet;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.text.SimpleDateFormat;
import JatoStyle.models.TransactionDetail;
import JatoStyle.services.TransactionService;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import java.sql.Blob;
import java.io.File;
import javax.swing.ImageIcon;

public class DashboardRestoranFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DashboardRestoranFrame.class.getName());

    private Toko currentRestoran;
    private JPanel menuContainer;
    private JPanel transaksiContainer;
    private JPanel wrapper;
    private JScrollPane scrollPane;
    private JScrollPane transaksiScrollPane;
    private AuthService auth = new AuthService();
    private DefaultTableModel transaksiTableModel;
    private JTable transaksiTable;

    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton logoutButton;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JLabel headerTitle;
    
    private TransactionService transactionService = new TransactionService();

    public DashboardRestoranFrame(Toko restoran) {
        this.currentRestoran = restoran;
        initComponents();
        setLocationRelativeTo(null);
        setupUI();
        loadMenuFromDatabase();
        loadTransaksiFromDatabase();
    }
    
    public DashboardRestoranFrame() {
        super("Dashboard Restoran");
        JOptionPane.showMessageDialog(this,
            "Frame ini membutuhkan data restoran untuk dibuka!",
            "Error", JOptionPane.ERROR_MESSAGE);
        dispose();
    }
    
    private void setupUI() {
        applyTemplateStyles();
        initDynamicUI();
        applyTabbedPaneStyle();
        headerTitle.setText("Data Restoran " + currentRestoran.getNamaRestoran());
    }

    @SuppressWarnings("unchecked")
    
    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700); // Diperbesar untuk menampilkan gambar
        setLocationRelativeTo(null);

        jTabbedPane = new JTabbedPane();
        mainPanel = new JPanel(new BorderLayout());
        logoLabel = new JLabel(new ImageIcon(getClass().getResource("/JatoStyle/gui/logo_mini.png")));
        headerTitle = new JLabel("Data Restoran " + currentRestoran.getNamaRestoran());
        logoutButton = new JButton("Logout");
    }

    
    private void applyTemplateStyles() {
        getContentPane().setBackground(new Color(206, 220, 239)); // [206,220,239]
        setTitle("Dashboard Restoran - " + (currentRestoran != null ? currentRestoran.getNamaRestoran() : ""));
        setLayout(new BorderLayout());
    }
    
    private void applyTabbedPaneStyle() {
        jTabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                              int x, int y, int w, int h, boolean isSelected) {

                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isSelected) {
                    g2d.setColor(new Color(149, 189, 226)); // #95BDE2
                } else {
                    g2d.setColor(new Color(206, 220, 239)); // [206,220,239]
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
                    g2d.setColor(new Color(0, 51, 79)); // #00334F
                } else {
                    g2d.setColor(new Color(200, 200, 200));
                }

                int margin = 8;
                int leftMargin = 15;
                int tabHeight = h - margin;
                int tabWidth = w - 10;
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
                return 15;
            }

            @Override
            protected Insets getTabAreaInsets(int tabPlacement) {
                return new Insets(15, 10, 0, 0);
            }

            @Override
            protected Insets getContentBorderInsets(int tabPlacement) {
                return new Insets(5, 0, 0, 0);
            }

            @Override
            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 30;
            }

            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) + 15;
            }

        });

        jTabbedPane.setFont(new Font("Bahnschrift", Font.BOLD, 12));
        jTabbedPane.setForeground(new Color(0, 51, 79)); // #00334F
        jTabbedPane.setBackground(new Color(206, 220, 239)); // [206,220,239]
        jTabbedPane.setBorder(BorderFactory.createLineBorder(new Color(149, 189, 226), 2)); // #95BDE2
    }
    
    private void initDynamicUI() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(25, 30, 25, 30));
        wrapper.setBackground(new Color(206, 220, 239)); // [206,220,239]
        
        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(206, 220, 239)); // [206,220,239]
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        // logo di kiri atas
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(logoLabel);

        // title center
        JPanel center = new JPanel();
        center.setOpaque(false);
        headerTitle.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        headerTitle.setForeground(new Color(0, 51, 79)); // #00334F
        center.add(headerTitle);

        // logout button
        logoutButton.setBackground(new Color(0, 51, 79)); // #00334F
        logoutButton.setForeground(new Color(206, 220, 239)); // #CEDCEF
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Bahnschrift", Font.BOLD, 12));
        logoutButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
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

        // tabbed pane
        jTabbedPane.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane.setFont(new Font("Bahnschrift", Font.BOLD, 12));
        jTabbedPane.setBackground(new Color(206, 220, 239)); // [206,220,239]
        jTabbedPane.setForeground(new Color(0, 51, 79)); // #00334F
        jTabbedPane.setBorder(BorderFactory.createLineBorder(new Color(149, 189, 226), 2)); // #95BDE2

        // tab 1 = Menu
        JPanel tabMenu = new JPanel(new BorderLayout());
        tabMenu.setBackground(new Color(206, 220, 239)); // [206,220,239]
        jTabbedPane.addTab("Item", tabMenu);

        // tab 2 = Transaksi
        JPanel tabTransaksi = new JPanel(new BorderLayout());
        tabTransaksi.setBackground(new Color(206, 220, 239)); // [206,220,239]
        jTabbedPane.addTab("Transaksi", tabTransaksi);

        wrapper.add(jTabbedPane, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        // UI list menu
        menuContainer = new JPanel();
        menuContainer.setBackground(new Color(206, 220, 239)); // [206,220,239]
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBorder(new EmptyBorder(15, 15, 15, 15));

        scrollPane = new JScrollPane(menuContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(206, 220, 239)); // [206,220,239]
        JScrollBar vBar = scrollPane.getVerticalScrollBar();
        vBar.setUnitIncrement(16);

        tabMenu.add(scrollPane, BorderLayout.CENTER);

        // bottom panel dgn "Tambah Menu +"
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(206, 220, 239)); // [206,220,239]
        JButton addBtn = new JButton("+ Tambah Item");
        addBtn.setBackground(new Color(149, 189, 226)); // #95BDE2
        addBtn.setForeground(new Color(0, 51, 79)); // #00334F
        addBtn.setFocusPainted(false);
        addBtn.setFont(new Font("Bahnschrift", Font.BOLD, 13));
        addBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        addBtn.addActionListener(e -> openTambahMenuFrame());
        bottomPanel.add(addBtn);
        tabMenu.add(bottomPanel, BorderLayout.SOUTH);

        // UI panel transaction
        setupTransactionPanel(tabTransaksi);
    }

    private void setupTransactionPanel(JPanel tabTransaksi) {
        // table model
        String[] columnNames = {"ID Pesanan", "Tanggal", "Customer", "Total", "Status", "Aksi", "Detail"};
        transaksiTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 6;
            }
        };

        // table
        transaksiTable = new JTable(transaksiTableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                if (!isRowSelected(row)) {
                    if (row % 2 == 0) {
                        c.setBackground(new Color(206, 220, 239)); // [206,220,239]
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }

                if (column == 4) {
                    String status = getValueAt(row, column).toString();
                    if (c instanceof JLabel) {
                        JLabel label = (JLabel) c;
                        label.setOpaque(true);
                        label.setHorizontalAlignment(SwingConstants.CENTER);

                        Color fontColor = isRowSelected(row) ? 
                            new Color(0, 51, 79) : Color.WHITE;

                        switch (status.toUpperCase()) {
                            case "PENDING":
                            case "SEDANG DIBUAT":
                                label.setBackground(new Color(255, 193, 7));
                                label.setForeground(isRowSelected(row) ? new Color(0, 51, 79) : Color.BLACK);
                                break;
                            case "SEDANG DIANTAR":
                                label.setBackground(new Color(33, 150, 243));
                                label.setForeground(fontColor);
                                break;
                            case "SUDAH SAMPAI":
                                label.setBackground(new Color(76, 175, 80));
                                label.setForeground(fontColor);
                                break;
                            default:
                                label.setBackground(new Color(149, 189, 226));
                                label.setForeground(fontColor);
                        }
                    }
                }

                if (column == 5) {
                    if (c instanceof JLabel) {
                        JLabel label = (JLabel) c;
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        label.setForeground(new Color(0, 51, 79));
                        label.setFont(new Font("Bahnschrift", Font.BOLD, 11));
                        if (!isRowSelected(row)) {
                            label.setBackground(new Color(149, 189, 226));
                            label.setOpaque(true);
                        }
                    }
                }

                if (column == 6) {
                    if (c instanceof JLabel) {
                        JLabel label = (JLabel) c;
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        label.setForeground(new Color(0, 51, 79));
                        label.setFont(new Font("Bahnschrift", Font.BOLD, 11));
                        if (!isRowSelected(row)) {
                            label.setBackground(new Color(149, 189, 226));
                            label.setOpaque(true);
                        }
                    }
                }

                return c;
            }
        };

        transaksiTable.setFont(new Font("Bahnschrift", Font.PLAIN, 14));
        transaksiTable.setRowHeight(32);
        transaksiTable.setBackground(new Color(206, 220, 239));

        transaksiTable.setSelectionBackground(new Color(149, 189, 226, 100));
        transaksiTable.setSelectionForeground(new Color(0, 51, 79));

        transaksiTable.setForeground(new Color(0, 51, 79));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setForeground(new Color(0, 51, 79));

        for (int i = 0; i < transaksiTable.getColumnCount(); i++) {
            if (i != 2) {
                DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
                renderer.setHorizontalAlignment(SwingConstants.CENTER);
                renderer.setForeground(new Color(0, 51, 79));
                transaksiTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }
        }

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        leftRenderer.setForeground(new Color(0, 51, 79));
        transaksiTable.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(JLabel.CENTER);
                setFont(new Font("Bahnschrift", Font.BOLD, 13));
                setBackground(new Color(30, 73, 138));
                setForeground(Color.WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(149, 189, 226)),
                    BorderFactory.createEmptyBorder(8, 5, 8, 5)
                ));

                return this;
            }
        };

        JTableHeader header = transaksiTable.getTableHeader();
        for (int i = 0; i < transaksiTable.getColumnCount(); i++) {
            transaksiTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        
        header.setPreferredSize(new Dimension(0, 35));

        transaksiScrollPane = new JScrollPane(transaksiTable);
        transaksiScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(149, 189, 226), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        transaksiScrollPane.getViewport().setBackground(new Color(206, 220, 239));

        tabTransaksi.add(transaksiScrollPane, BorderLayout.CENTER);

        setupTableMouseListener();
    }

    private void setupTableMouseListener() {
        transaksiTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = transaksiTable.rowAtPoint(evt.getPoint());
                int col = transaksiTable.columnAtPoint(evt.getPoint());

                if (row >= 0) {
                    String idPesananStr = transaksiTable.getValueAt(row, 0).toString();
                    int idPesanan = Integer.parseInt(idPesananStr.replace("PES", ""));

                    if (col == 5) {
                        String currentStatus = transaksiTable.getValueAt(row, 4).toString();
                        showStatusChangeDialog(idPesanan, currentStatus, row);
                    } else if (col == 6) {
                        showOrderDetail(idPesanan, row);
                    }
                }
            }
        });
    }
    
    private void showOrderDetail(int idPesanan, int row) {
        try {
            System.out.println("Menampilkan detail pesanan ID: " + idPesanan);

            List<TransactionDetail> details = transactionService.getTransactionDetailsForRestoran(
                idPesanan, currentRestoran.getIdRestoran()
            );

            if (details.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Tidak ditemukan detail untuk pesanan ini.", 
                    "Detail Pesanan", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String idString = "PES" + String.format("%04d", idPesanan);
            String tanggal = transaksiTable.getValueAt(row, 1).toString();
            String customer = transaksiTable.getValueAt(row, 2).toString();
            String totalHarga = transaksiTable.getValueAt(row, 3).toString();
            String status = transaksiTable.getValueAt(row, 4).toString();

            JDialog detailDialog = new JDialog(this, "Detail Pesanan " + idString, true);
            detailDialog.setLayout(new BorderLayout());
            detailDialog.setSize(500, 450);
            detailDialog.setLocationRelativeTo(this);
            detailDialog.getContentPane().setBackground(new Color(206, 220, 239));

            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(30, 73, 138));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JLabel headerLabel = new JLabel("Detail Pesanan #" + idString);
            headerLabel.setFont(new Font("Bahnschrift", Font.BOLD, 16));
            headerLabel.setForeground(Color.WHITE);
            headerPanel.add(headerLabel, BorderLayout.WEST);

            JLabel infoLabel = new JLabel("Resto: " + currentRestoran.getNamaRestoran());
            infoLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 12));
            infoLabel.setForeground(Color.WHITE);
            headerPanel.add(infoLabel, BorderLayout.EAST);

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(new Color(206, 220, 239));
            infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(149, 189, 226)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            ));

            JLabel customerLabel = new JLabel("Customer: " + customer);
            customerLabel.setFont(new Font("Bahnschrift", Font.BOLD, 13));
            customerLabel.setForeground(new Color(0, 51, 79));

            JLabel tanggalLabel = new JLabel("Tanggal: " + tanggal);
            tanggalLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 12));
            tanggalLabel.setForeground(new Color(0, 51, 79));

            JLabel statusLabel = new JLabel("Status: " + status);
            statusLabel.setFont(new Font("Bahnschrift", Font.BOLD, 12));
            statusLabel.setForeground(new Color(0, 51, 79));

            infoPanel.add(customerLabel);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(tanggalLabel);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(statusLabel);

            JPanel menuPanel = new JPanel();
            menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
            menuPanel.setBackground(Color.WHITE);
            menuPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            int totalItems = 0;
            int totalPrice = 0;

            for (TransactionDetail detail : details) {
                JPanel itemPanel = new JPanel(new BorderLayout());
                itemPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(149, 189, 226)),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
                itemPanel.setBackground(Color.WHITE);

                JLabel menuLabel = new JLabel(detail.getNamaMenu());
                menuLabel.setFont(new Font("Bahnschrift", Font.BOLD, 13));
                menuLabel.setForeground(new Color(0, 51, 79));

                JLabel detailLabel = new JLabel("Jumlah: " + detail.getJumlah() + 
                                               " x Rp " + String.format("%,d", detail.getHargaSatuan()) + 
                                               " = Rp " + String.format("%,d", detail.getSubtotal()));
                detailLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 12));
                detailLabel.setForeground(new Color(0, 51, 79));

                JPanel leftPanel = new JPanel(new BorderLayout());
                leftPanel.setBackground(Color.WHITE);
                leftPanel.add(menuLabel, BorderLayout.NORTH);
                leftPanel.add(Box.createVerticalStrut(3));
                leftPanel.add(detailLabel, BorderLayout.SOUTH);

                itemPanel.add(leftPanel, BorderLayout.CENTER);
                menuPanel.add(itemPanel);
                menuPanel.add(Box.createVerticalStrut(8));

                totalItems += detail.getJumlah();
                totalPrice += detail.getSubtotal();
            }

            JPanel totalPanel = new JPanel(new BorderLayout());
            totalPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(149, 189, 226)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            ));
            totalPanel.setBackground(new Color(206, 220, 239));

            JLabel totalItemsLabel = new JLabel("Total Items: " + totalItems);
            totalItemsLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 12));
            totalItemsLabel.setForeground(new Color(0, 51, 79));

            JLabel totalPriceLabel = new JLabel("Total: Rp " + String.format("%,d", totalPrice));
            totalPriceLabel.setFont(new Font("Bahnschrift", Font.BOLD, 14));
            totalPriceLabel.setForeground(new Color(0, 51, 79));

            totalPanel.add(totalItemsLabel, BorderLayout.WEST);
            totalPanel.add(totalPriceLabel, BorderLayout.EAST);

            JScrollPane scrollPane = new JScrollPane(menuPanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(Color.WHITE);

            detailDialog.add(headerPanel, BorderLayout.NORTH);
            detailDialog.add(infoPanel, BorderLayout.CENTER);
            detailDialog.add(scrollPane, BorderLayout.CENTER);
            detailDialog.add(totalPanel, BorderLayout.SOUTH);

            detailDialog.setVisible(true);

        } catch (Exception e) {
            System.out.println("Error menampilkan detail pesanan: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error menampilkan detail: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showStatusChangeDialog(int idPesanan, String currentStatus, int row) {
        JDialog dialog = new JDialog(this, "Ubah Status Pesanan", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(new Color(206, 220, 239));
        
        JLabel titleLabel = new JLabel("Ubah Status Pesanan: " + "PES" + String.format("%04d", idPesanan));
        titleLabel.setFont(new Font("Bahnschrift", Font.BOLD, 16));
        titleLabel.setForeground(new Color(0, 51, 79));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(20, 15, 15, 15));
        
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        buttonPanel.setBackground(new Color(206, 220, 239));
        buttonPanel.setBorder(new EmptyBorder(10, 30, 20, 30));
        
        JButton btnMasak = createDialogButton("Sedang Dikemas", new Color(149, 189, 226));
        JButton btnAntar = createDialogButton("Sedang Diantar", new Color(149, 189, 226));
        JButton btnSampai = createDialogButton("Sudah Sampai", new Color(149, 189, 226));
        
        switch (currentStatus.toUpperCase()) {
            case "PENDING":
            case "SEDANG DIBUAT":
                break;
            case "SEDANG DIANTAR":
                btnMasak.setEnabled(false);
                break;
            case "SUDAH SAMPAI":
                btnMasak.setEnabled(false);
                btnAntar.setEnabled(false);
                btnSampai.setEnabled(false);
                break;
        }
        
        btnMasak.addActionListener(e -> {
            updateStatusAndClose(idPesanan, "SEDANG DIBUAT", dialog);
        });
        
        btnAntar.addActionListener(e -> {
            updateStatusAndClose(idPesanan, "SEDANG DIANTAR", dialog);
        });
        
        btnSampai.addActionListener(e -> {
            updateStatusAndClose(idPesanan, "SUDAH SAMPAI", dialog);
        });
        
        buttonPanel.add(btnMasak);
        buttonPanel.add(btnAntar);
        buttonPanel.add(btnSampai);
        
        dialog.add(titleLabel, BorderLayout.NORTH);
        dialog.add(buttonPanel, BorderLayout.CENTER);
        
        dialog.setVisible(true);
    }

    private JButton createDialogButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Bahnschrift", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(new Color(0, 51, 79));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 51, 79), 1),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        button.setPreferredSize(new Dimension(200, 50));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 51, 79), 2),
                    BorderFactory.createEmptyBorder(12, 20, 12, 20)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 51, 79), 1),
                    BorderFactory.createEmptyBorder(12, 20, 12, 20)
                ));
            }
        });
        
        return button;
    }

    private void updateStatusAndClose(int idPesanan, String newStatus, JDialog dialog) {
        updateStatusPesanan(idPesanan, newStatus);
        dialog.dispose();
    }
    
    private void openTambahMenuFrame() {
        TambahItemFrame dialog = new TambahItemFrame(this, currentRestoran);
        dialog.setVisible(true);
    }
    
    private void openEditMenuFrame(int idMenu, String namaMenu, int harga, int stok) {
        EditMenuFrame edit = new EditMenuFrame(this, currentRestoran, idMenu, namaMenu, harga, stok);
        edit.setVisible(true);
    }
    
    private void deleteMenu(int idMenu) {
        try {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin menghapus item ini?\n" +
                "(Akan menghapus semua data terkait di keranjang dan detail pesanan)", 
                "Konfirmasi Hapus", 
                JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            auth.getKonektor().query("DELETE FROM detail_pesanan WHERE id_menu = " + idMenu);
            auth.getKonektor().query("DELETE FROM keranjang WHERE id_menu = " + idMenu);
            auth.getKonektor().query("DELETE FROM menu WHERE id_menu = " + idMenu);

            JOptionPane.showMessageDialog(this, "Item berhasil dihapus!");
            loadMenuFromDatabase();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Gagal menghapus item: " + e.getMessage() + 
                "\n\nPastikan tidak ada pesanan aktif yang menggunakan item ini.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshAfterAddMenu() {
        loadMenuFromDatabase();
    }
    
    private void loadMenuFromDatabase() {
        if (menuContainer == null) return;
        menuContainer.removeAll();

        try {
            String sql = "SELECT * FROM menu WHERE id_restoran = " + currentRestoran.getIdRestoran() + " ORDER BY nama_menu";
            ResultSet rs = auth.getKonektor().getData(sql);

            boolean any = false;
            while (rs != null && rs.next()) {
                any = true;
                int idMenu = rs.getInt("id_menu");
                String nama = rs.getString("nama_menu");
                int harga = rs.getInt("harga");
                int stok = rs.getInt("stok");
                
                // Load gambar
                ImageIcon imageIcon = null;
                try {
                    Blob blob = rs.getBlob("gambar");
                    if (blob != null) {
                        byte[] imageData = blob.getBytes(1, (int) blob.length());
                        Image image = new ImageIcon(imageData).getImage();
                        Image scaledImage = image.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                        imageIcon = new ImageIcon(scaledImage);
                    }
                } catch (Exception e) {
                    // Jika gagal load dari BLOB, coba dari file path
                    String imagePath = rs.getString("gambar_path");
                    if (imagePath != null && !imagePath.isEmpty()) {
                        File imageFile = new File(imagePath);
                        if (imageFile.exists()) {
                            ImageIcon icon = new ImageIcon(imagePath);
                            Image scaledImage = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                            imageIcon = new ImageIcon(scaledImage);
                        }
                    }
                }

                JPanel card = createMenuCard(idMenu, nama, harga, stok, imageIcon);
                menuContainer.add(card);
                menuContainer.add(Box.createVerticalStrut(10));
            }

            if (!any) {
                JPanel messagePanel = new JPanel(new BorderLayout());
                messagePanel.setOpaque(false);
                JLabel l = new JLabel("Tidak ada item. Klik '+ Tambah Item' untuk menambahkan.");
                l.setFont(new Font("Bahnschrift", Font.PLAIN, 14));
                l.setForeground(new Color(0, 51, 79));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                messagePanel.add(l, BorderLayout.CENTER);
                menuContainer.add(messagePanel);
            }

        } catch (Exception e) {
            System.out.println("Load item error: " + e.getMessage());
            e.printStackTrace();
            JPanel messagePanel = new JPanel(new BorderLayout());
            messagePanel.setOpaque(false);
            JLabel l = new JLabel("Error: Tidak dapat memuat item dari database");
            l.setFont(new Font("Bahnschrift", Font.BOLD, 14));
            l.setForeground(Color.RED);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            messagePanel.add(l, BorderLayout.CENTER);
            menuContainer.add(messagePanel);
        }

        menuContainer.revalidate();
        menuContainer.repaint();
    }

    private void loadTransaksiFromDatabase() {
        if (transaksiTableModel == null) return;
        transaksiTableModel.setRowCount(0);

        try {
            String sql = "SELECT p.id_pesanan, p.tanggal_pesan, p.status_pesanan, " +
                        "u.nama as nama_customer, " +
                        "SUM(d.jumlah * d.harga_satuan) as total_harga " +
                        "FROM pesanan p " +
                        "JOIN user u ON p.id_user = u.id_user " +
                        "LEFT JOIN detail_pesanan d ON p.id_pesanan = d.id_pesanan " +
                        "WHERE p.id_restoran = " + currentRestoran.getIdRestoran() + " " +
                        "GROUP BY p.id_pesanan, p.tanggal_pesan, p.status_pesanan, u.nama " +
                        "ORDER BY p.tanggal_pesan DESC";

            ResultSet rs = auth.getKonektor().getData(sql);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            while (rs != null && rs.next()) {
                int idPesanan = rs.getInt("id_pesanan");
                String tanggal = dateFormat.format(rs.getTimestamp("tanggal_pesan"));
                String customer = rs.getString("nama_customer");
                int total = rs.getInt("total_harga");
                String status = rs.getString("status_pesanan");

                transaksiTableModel.addRow(new Object[]{
                    "PES" + String.format("%04d", idPesanan),
                    tanggal,
                    customer,
                    String.format("Rp %,d", total),
                    status,
                    "Ubah Status",
                    "Lihat Detail"
                });
            }

            transaksiTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            transaksiTable.getColumnModel().getColumn(1).setPreferredWidth(120);
            transaksiTable.getColumnModel().getColumn(2).setPreferredWidth(120);
            transaksiTable.getColumnModel().getColumn(3).setPreferredWidth(100);
            transaksiTable.getColumnModel().getColumn(4).setPreferredWidth(100);
            transaksiTable.getColumnModel().getColumn(5).setPreferredWidth(120);
            transaksiTable.getColumnModel().getColumn(6).setPreferredWidth(120);

        } catch (Exception e) {
            System.out.println("Load transaksi error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error: Tidak dapat memuat data transaksi",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatusPesanan(int idPesanan, String statusBaru) {
        try {
            String sql = "UPDATE pesanan SET status_pesanan = '" + statusBaru + "' WHERE id_pesanan = " + idPesanan;
            auth.getKonektor().query(sql);

            JOptionPane.showMessageDialog(this, 
                "Status pesanan berhasil diupdate menjadi: " + statusBaru, 
                "Sukses", 
                JOptionPane.INFORMATION_MESSAGE);

            loadTransaksiFromDatabase();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Gagal mengupdate status pesanan: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createMenuCard(int idMenu, String namaMenu, int harga, int stok, ImageIcon imageIcon) {
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(149, 189, 226));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 16, 12, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Panel kiri untuk gambar
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(100, 100));
        
        if (imageIcon != null) {
            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setBorder(BorderFactory.createLineBorder(new Color(149, 189, 226), 1));
            leftPanel.add(imageLabel, BorderLayout.CENTER);
        } else {
            JLabel noImageLabel = new JLabel("No Image", SwingConstants.CENTER);
            noImageLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 10));
            noImageLabel.setForeground(new Color(150, 150, 150));
            noImageLabel.setBorder(BorderFactory.createLineBorder(new Color(149, 189, 226), 1));
            noImageLabel.setOpaque(true);
            noImageLabel.setBackground(Color.WHITE);
            leftPanel.add(noImageLabel, BorderLayout.CENTER);
        }

        // Panel tengah untuk info menu
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(5, 15, 5, 15));

        JLabel lblNama = new JLabel(namaMenu);
        lblNama.setFont(new Font("Bahnschrift", Font.BOLD, 16));
        lblNama.setForeground(new Color(0, 51, 79));

        JLabel lblHarga = new JLabel("Rp " + String.format("%,d", harga));
        lblHarga.setFont(new Font("Bahnschrift", Font.PLAIN, 14));
        lblHarga.setForeground(new Color(0, 51, 79));

        // Panel stok
        JPanel stokPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        stokPanel.setOpaque(false);
        
        JLabel stokLabel = new JLabel("Stok: " + stok);
        stokLabel.setFont(new Font("Bahnschrift", Font.BOLD, 12));
        
        // Warna stok berdasarkan jumlah
        if (stok <= 0) {
            stokLabel.setForeground(new Color(220, 53, 69)); // Merah untuk habis
            stokLabel.setText("Stok: HABIS");
        } else if (stok <= 5) {
            stokLabel.setForeground(new Color(255, 193, 7)); // Kuning untuk menipis
            stokLabel.setText("Stok: " + stok + " (Menipis)");
        } else {
            stokLabel.setForeground(new Color(40, 167, 69)); // Hijau untuk cukup
        }
        
        stokPanel.add(stokLabel);

        centerPanel.add(lblNama);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(lblHarga);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(stokPanel);

        // Panel kanan untuk tombol
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new GridLayout(2, 1, 0, 5));
        rightPanel.setBorder(new EmptyBorder(0, 0, 0, 5));

        JButton editBtn = createSmallButton("Edit", new Color(149, 189, 226));
        editBtn.addActionListener(e -> openEditMenuFrame(idMenu, namaMenu, harga, stok));

        JButton deleteBtn = createSmallButton("Hapus", new Color(0, 51, 79));
        deleteBtn.addActionListener(e -> deleteMenu(idMenu));

        rightPanel.add(editBtn);
        rightPanel.add(deleteBtn);

        card.add(leftPanel, BorderLayout.WEST);
        card.add(centerPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }
    
    private JButton createSmallButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Bahnschrift", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setBackground(color);
        
        if (color.equals(new Color(0, 51, 79))) {
            btn.setForeground(new Color(206, 220, 239));
        } else {
            btn.setForeground(new Color(0, 51, 79));
        }
        
        return btn;
    }
    
    public static void main(String args[]) {
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

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "Jalankan DashboardRestoranFrame dari Login setelah loginRestoran sukses.", "Info", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}