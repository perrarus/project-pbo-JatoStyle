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

public class EditMenuFrame extends JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(EditMenuFrame.class.getName());

    private DashboardRestoranFrame parent;
    private Restoran restoran;
    private int idMenu;

    private AuthService auth = new AuthService();

    private JTextField namaField;
    private JTextField hargaField;
    private JButton saveBtn;
    private JButton deleteBtn;
    private JButton cancelBtn;

    public EditMenuFrame(DashboardRestoranFrame parent, Restoran restoran, int idMenu, String nama, int harga) {
        super(parent, "Edit Menu", true);
        this.parent = parent;
        this.restoran = restoran;
        this.idMenu = idMenu;
        initUI(nama, harga);
    }

    private void initUI(String nama, int harga) {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 230);
        setResizable(false);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(250, 240, 227));
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblNama = new JLabel("Nama Menu");
        lblNama.setFont(new Font("Bahnschrift", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        form.add(lblNama, gbc);

        namaField = new JTextField(nama);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.8;
        form.add(namaField, gbc);

        JLabel lblHarga = new JLabel("Harga (angka)");
        lblHarga.setFont(new Font("Bahnschrift", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        form.add(lblHarga, gbc);

        hargaField = new JTextField(String.valueOf(harga));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.8;
        form.add(hargaField, gbc);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);

        saveBtn = new JButton("Simpan");
        saveBtn.setBackground(new Color(241, 124, 42));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> onSave());

        deleteBtn = new JButton("Hapus");
        deleteBtn.setBackground(new Color(200, 0, 0));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(e -> onDelete());

        cancelBtn = new JButton("Batal");
        cancelBtn.addActionListener(e -> dispose());

        bottom.add(deleteBtn);
        bottom.add(cancelBtn);
        bottom.add(saveBtn);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void onSave() {
        String nama = namaField.getText().trim();
        String hargaText = hargaField.getText().trim();
        if (nama.isEmpty() || hargaText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan harga harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int harga;
        try {
            harga = Integer.parseInt(hargaText);
            if (harga < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka positif!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String safeNama = nama.replace("'", "''");
            String sql = String.format(
                "UPDATE menu SET nama_menu = '%s', harga = %d WHERE id_menu = %d",
                safeNama, harga, idMenu
            );
            auth.getKonektor().query(sql);
            JOptionPane.showMessageDialog(this, "Menu berhasil diupdate!");
            dispose();
            if (parent != null) parent.refreshAfterAddMenu();
        } catch (Exception e) {
            logger.severe("Gagal update menu: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Gagal update menu: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void onDelete() {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus menu ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
