/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package JatoStyle.gui;

import JatoStyle.models.Restoran;
import JatoStyle.services.AuthService;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TambahMenuFrame extends JDialog {
    
    private static final Logger logger = Logger.getLogger(TambahMenuFrame.class.getName());

    private DashboardRestoranFrame parent;
    private Restoran restoran;
    private AuthService auth = new AuthService();
    private String imagePath;

    private JTextField namaField;
    private JTextField hargaField;
    private JTextField stokField;
    private JLabel imageLabel;
    private JButton browseButton;
    private JButton saveBtn;
    private JButton cancelBtn;

    public TambahMenuFrame(DashboardRestoranFrame parent, Restoran restoran) {
        super(parent, "Tambah Menu", true);
        this.parent = parent;
        this.restoran = restoran;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 450);
        setResizable(false);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(206, 220, 239)); // [206,220,239]
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label dan field untuk Nama Menu
        JLabel lblNama = new JLabel("Nama Menu*");
        lblNama.setFont(new Font("Bahnschrift", Font.BOLD, 12));
        lblNama.setForeground(new Color(0, 51, 79)); // #00334F
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        form.add(lblNama, gbc);

        namaField = new JTextField();
        namaField.setFont(new Font("Bahnschrift", Font.PLAIN, 12));
        namaField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(149, 189, 226), 2), // #95BDE2
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.8;
        form.add(namaField, gbc);

        // Label dan field untuk Harga
        JLabel lblHarga = new JLabel("Harga*");
        lblHarga.setFont(new Font("Bahnschrift", Font.BOLD, 12));
        lblHarga.setForeground(new Color(0, 51, 79)); // #00334F
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        form.add(lblHarga, gbc);

        hargaField = new JTextField();
        hargaField.setFont(new Font("Bahnschrift", Font.PLAIN, 12));
        hargaField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(149, 189, 226), 2),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.8;
        form.add(hargaField, gbc);

        // Label dan field untuk Stok
        JLabel lblStok = new JLabel("Stok*");
        lblStok.setFont(new Font("Bahnschrift", Font.BOLD, 12));
        lblStok.setForeground(new Color(0, 51, 79)); // #00334F
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        form.add(lblStok, gbc);

        stokField = new JTextField("0");
        stokField.setFont(new Font("Bahnschrift", Font.PLAIN, 12));
        stokField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(149, 189, 226), 2),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.8;
        form.add(stokField, gbc);

        // Label dan field untuk Gambar
        JLabel lblGambar = new JLabel("Gambar");
        lblGambar.setFont(new Font("Bahnschrift", Font.BOLD, 12));
        lblGambar.setForeground(new Color(0, 51, 79)); // #00334F
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.2;
        form.add(lblGambar, gbc);

        JPanel imagePanel = new JPanel(new BorderLayout(10, 0));
        imagePanel.setOpaque(false);
        
        imageLabel = new JLabel("No Image", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(100, 100));
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(149, 189, 226), 2));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE);
        imageLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 11));
        imageLabel.setForeground(new Color(100, 100, 100));
        
        browseButton = new JButton("Browse...");
        browseButton.setFont(new Font("Bahnschrift", Font.BOLD, 11));
        browseButton.setBackground(new Color(149, 189, 226)); // #95BDE2
        browseButton.setForeground(new Color(0, 51, 79)); // #00334F
        browseButton.setFocusPainted(false);
        browseButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        browseButton.addActionListener(e -> browseImage());
        
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.add(browseButton, BorderLayout.EAST);
        
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.8;
        form.add(imagePanel, gbc);

        // Panel tombol
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setOpaque(false);

        cancelBtn = new JButton("Batal");
        styleButton(cancelBtn, new Color(149, 189, 226)); // #95BDE2
        cancelBtn.addActionListener(e -> dispose());

        saveBtn = new JButton("Simpan");
        styleButton(saveBtn, new Color(149, 189, 226)); // #95BDE2
        saveBtn.addActionListener(e -> onSave());

        bottom.add(cancelBtn);
        bottom.add(saveBtn);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void browseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih Gambar Menu");
        fileChooser.setAcceptAllFileFilterUsed(false);
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Image Files", "jpg", "jpeg", "png", "gif", "bmp");
        fileChooser.addChoosableFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imagePath = selectedFile.getAbsolutePath();
            
            // Tampilkan preview gambar
            ImageIcon icon = new ImageIcon(imagePath);
            Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
            imageLabel.setText("");
        }
    }

    private void onSave() {
        String nama = namaField.getText().trim();
        String hargaText = hargaField.getText().trim();
        String stokText = stokField.getText().trim();
        
        if (nama.isEmpty() || hargaText.isEmpty() || stokText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int harga, stok;
        try {
            harga = Integer.parseInt(hargaText);
            stok = Integer.parseInt(stokText);
            if (harga < 0 || stok < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga dan stok harus berupa angka positif!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        FileInputStream fis = null;
        
        try {
            // Gunakan static getConnection() dari Konektor
            conn = JatoStyle.Konektor.getConnection();
            
            // Insert data menu ke database
            String safeNama = nama.replace("'", "''");
            String insertSql = String.format(
                "INSERT INTO menu (id_restoran, nama_menu, harga, stok, status_habis) " +
                "VALUES (%d, '%s', %d, %d, %d)",
                restoran.getIdRestoran(),
                safeNama,
                harga,
                stok,
                (stok == 0 ? 1 : 0)
            );
            
            // Execute insert dan dapatkan ID yang di-generate
            pstmt = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.executeUpdate();
            
            // Dapatkan ID menu yang baru dibuat
            rs = pstmt.getGeneratedKeys();
            int newMenuId = -1;
            if (rs.next()) {
                newMenuId = rs.getInt(1);
                System.out.println("Menu baru dibuat dengan ID: " + newMenuId);
            }
            
            // Jika ada gambar, simpan gambar
            if (imagePath != null && newMenuId != -1) {
                File imageFile = new File(imagePath);
                fis = new FileInputStream(imageFile);
                
                // Update menu dengan gambar
                PreparedStatement pstmt2 = conn.prepareStatement(
                    "UPDATE menu SET gambar = ? WHERE id_menu = ?"
                );
                pstmt2.setBinaryStream(1, fis, (int) imageFile.length());
                pstmt2.setInt(2, newMenuId);
                pstmt2.executeUpdate();
                pstmt2.close();
                
                // Juga simpan ke folder images
                saveImageToFolder(imageFile, newMenuId);
            }
            
            JOptionPane.showMessageDialog(this, "Menu berhasil ditambahkan!");
            dispose();
            if (parent != null) parent.refreshAfterAddMenu();
            
        } catch (Exception e) {
            logger.severe("Gagal menambahkan menu: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Gagal menambahkan menu: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            // Tutup resources
            try { if (fis != null) fis.close(); } catch (Exception e) {}
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    private void saveImageToFolder(File imageFile, int menuId) {
        try {
            // Buat folder images/menu jika belum ada
            File imagesDir = new File("images/menu");
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }
            
            // Salin file ke folder images/menu
            String newFileName = "menu_" + menuId + getFileExtension(imageFile.getName());
            Path destination = Paths.get("images/menu", newFileName);
            Files.copy(imageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            
            // Update path di database
            String updatePathSql = "UPDATE menu SET gambar_path = '" + destination.toString() + "' WHERE id_menu = " + menuId;
            auth.getKonektor().query(updatePathSql);
            
            System.out.println("Gambar disimpan di: " + destination.toString());
            
        } catch (Exception e) {
            logger.warning("Failed to save image to folder: " + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? ".jpg" : fileName.substring(dotIndex);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setFont(new Font("Bahnschrift", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        if (bg.equals(new Color(0, 51, 79))) { // #00334F
            btn.setForeground(new Color(206, 220, 239)); // #CEDCEF
        } else {
            btn.setForeground(new Color(0, 51, 79)); // #00334F
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        pack();
    }// </editor-fold>                        

    // Variables declaration - do not modify                     
    // End of variables declaration                   
}