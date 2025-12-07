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
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.filechooser.FileNameExtensionFilter;

public class EditMenuFrame extends JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(EditMenuFrame.class.getName());

    private DashboardRestoranFrame parent;
    private Restoran restoran;
    private int idMenu;
    private String currentImagePath;

    private AuthService auth = new AuthService();

    private JTextField namaField;
    private JTextField hargaField;
    private JTextField stokField;
    private JLabel imageLabel;
    private JButton browseButton;
    private JButton saveBtn;
    private JButton deleteBtn;
    private JButton cancelBtn;

    public EditMenuFrame(DashboardRestoranFrame parent, Restoran restoran, int idMenu, String nama, int harga, int stok) {
        super(parent, "Edit Menu", true);
        this.parent = parent;
        this.restoran = restoran;
        this.idMenu = idMenu;
        initUI(nama, harga, stok);
        loadCurrentImage();
    }

    private void initUI(String nama, int harga, int stok) {
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
        JLabel lblNama = new JLabel("Nama Menu");
        lblNama.setFont(new Font("Bahnschrift", Font.BOLD, 12));
        lblNama.setForeground(new Color(0, 51, 79)); // #00334F
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        form.add(lblNama, gbc);

        namaField = new JTextField(nama);
        namaField.setFont(new Font("Bahnschrift", Font.PLAIN, 12));
        namaField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(149, 189, 226), 2), // #95BDE2
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.8;
        form.add(namaField, gbc);

        // Label dan field untuk Harga
        JLabel lblHarga = new JLabel("Harga");
        lblHarga.setFont(new Font("Bahnschrift", Font.BOLD, 12));
        lblHarga.setForeground(new Color(0, 51, 79)); // #00334F
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        form.add(lblHarga, gbc);

        hargaField = new JTextField(String.valueOf(harga));
        hargaField.setFont(new Font("Bahnschrift", Font.PLAIN, 12));
        hargaField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(149, 189, 226), 2),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.8;
        form.add(hargaField, gbc);

        // Label dan field untuk Stok
        JLabel lblStok = new JLabel("Stok");
        lblStok.setFont(new Font("Bahnschrift", Font.BOLD, 12));
        lblStok.setForeground(new Color(0, 51, 79)); // #00334F
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        form.add(lblStok, gbc);

        stokField = new JTextField(String.valueOf(stok));
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

        deleteBtn = new JButton("Hapus");
        styleButton(deleteBtn, new Color(0, 51, 79)); // #00334F
        deleteBtn.addActionListener(e -> onDelete());

        cancelBtn = new JButton("Batal");
        styleButton(cancelBtn, new Color(149, 189, 226)); // #95BDE2
        cancelBtn.addActionListener(e -> dispose());

        saveBtn = new JButton("Simpan");
        styleButton(saveBtn, new Color(149, 189, 226)); // #95BDE2
        saveBtn.addActionListener(e -> onSave());

        bottom.add(deleteBtn);
        bottom.add(cancelBtn);
        bottom.add(saveBtn);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadCurrentImage() {
        try {
            // Coba load gambar dari database (BLOB)
            String sql = "SELECT gambar FROM menu WHERE id_menu = " + idMenu;
            ResultSet rs = auth.getKonektor().getData(sql);
            
            if (rs != null && rs.next()) {
                Blob blob = rs.getBlob("gambar");
                if (blob != null) {
                    // Simpan gambar ke file sementara
                    byte[] imageData = blob.getBytes(1, (int) blob.length());
                    ImageIcon icon = new ImageIcon(imageData);
                    Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledImage));
                    imageLabel.setText("");
                }
            }
        } catch (Exception e) {
            logger.warning("Failed to load image: " + e.getMessage());
        }
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
            currentImagePath = selectedFile.getAbsolutePath();
            
            // Tampilkan preview gambar
            ImageIcon icon = new ImageIcon(currentImagePath);
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
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
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

        try {
            // Jika tidak ada gambar baru, update hanya data biasa
            if (currentImagePath == null) {
                String safeNama = nama.replace("'", "''");
                String sql = String.format(
                    "UPDATE menu SET nama_menu = '%s', harga = %d, stok = %d, status_habis = %d WHERE id_menu = %d",
                    safeNama, harga, stok, (stok == 0 ? 1 : 0), idMenu
                );
                auth.getKonektor().query(sql);
            } 
            // Jika ada gambar baru, update dengan gambar menggunakan PreparedStatement
            else {
                File imageFile = new File(currentImagePath);
                try (FileInputStream fis = new FileInputStream(imageFile)) {
                    // Gunakan static getConnection() dari Konektor
                    Connection conn = JatoStyle.Konektor.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE menu SET nama_menu = ?, harga = ?, stok = ?, status_habis = ?, gambar = ? WHERE id_menu = ?"
                    );
                    pstmt.setString(1, nama);
                    pstmt.setInt(2, harga);
                    pstmt.setInt(3, stok);
                    pstmt.setInt(4, (stok == 0 ? 1 : 0));
                    pstmt.setBinaryStream(5, fis, (int) imageFile.length());
                    pstmt.setInt(6, idMenu);
                    pstmt.executeUpdate();
                    pstmt.close();
                    conn.close();
                    
                    // Juga simpan ke folder images untuk kemudahan akses
                    saveImageToFolder(imageFile);
                }
            }
            
            JOptionPane.showMessageDialog(this, "Menu berhasil diupdate!");
            dispose();
            if (parent != null) parent.refreshAfterAddMenu();
            
        } catch (Exception e) {
            logger.severe("Gagal update menu: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Gagal update menu: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void saveImageToFolder(File imageFile) {
        try {
            // Buat folder images/menu jika belum ada
            File imagesDir = new File("images/menu");
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }
            
            // Salin file ke folder images/menu
            String newFileName = "menu_" + idMenu + "_" + System.currentTimeMillis() + 
                                 getFileExtension(imageFile.getName());
            Path destination = Paths.get("images/menu", newFileName);
            Files.copy(imageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            
            // Update path di database (opsional)
            String updatePathSql = "UPDATE menu SET gambar_path = '" + destination.toString() + "' WHERE id_menu = " + idMenu;
            auth.getKonektor().query(updatePathSql);
            
        } catch (Exception e) {
            logger.warning("Failed to save image to folder: " + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }

    private void onDelete() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Yakin ingin menghapus menu ini?\nIni akan menghapus semua data terkait.", 
            "Konfirmasi", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // Hapus dari keranjang dan detail pesanan terlebih dahulu
            auth.getKonektor().query("DELETE FROM keranjang WHERE id_menu = " + idMenu);
            auth.getKonektor().query("DELETE FROM detail_pesanan WHERE id_menu = " + idMenu);
            
            // Hapus menu
            String sql = String.format("DELETE FROM menu WHERE id_menu = %d", idMenu);
            auth.getKonektor().query(sql);
            
            JOptionPane.showMessageDialog(this, "Menu berhasil dihapus!");
            dispose();
            if (parent != null) parent.refreshAfterAddMenu();
            
        } catch (Exception e) {
            logger.severe("Gagal hapus menu: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Gagal hapus menu: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
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