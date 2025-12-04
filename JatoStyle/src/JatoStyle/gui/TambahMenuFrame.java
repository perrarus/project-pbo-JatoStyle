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

public class TambahMenuFrame extends JDialog {
    
    private static final Logger logger = Logger.getLogger(TambahMenuFrame.class.getName());

    private DashboardRestoranFrame parent;
    private Restoran restoran;
    private AuthService auth = new AuthService();

    private JTextField namaField;
    private JTextField hargaField;
    private JTextField stokField;
    private JButton saveBtn;
    private JButton cancelBtn;

    /**
     * Creates new form TambahMenuFrame
     */
    // mode tambah menu
    public TambahMenuFrame(DashboardRestoranFrame parent, Restoran restoran) {
        super(parent, "Tambah Menu", true); // modal dialog
        this.parent = parent;
        this.restoran = restoran;
        initUI(false, -1, "", 0);
    }
    
    public TambahMenuFrame(DashboardRestoranFrame parent, Restoran restoran, int idMenu, String nama, int harga) {
        super(parent, "Tambah Menu / Prefill", true);
        this.parent = parent;
        this.restoran = restoran;
        initUI(true, idMenu, nama, harga);
    }

    private void initUI(boolean prefill, int idMenu, String nama, int harga) {
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

        namaField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.8;
        form.add(namaField, gbc);

        JLabel lblHarga = new JLabel("Harga (angka)");
        lblHarga.setFont(new Font("Bahnschrift", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        form.add(lblHarga, gbc);

        hargaField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.8;
        form.add(hargaField, gbc);
        
        JLabel stokLabel = new JLabel("Stok");
        stokLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        form.add(stokLabel, gbc);

        stokField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.8;
        form.add(stokField, gbc);


        if (prefill) {
            namaField.setText(nama);
            hargaField.setText(String.valueOf(harga));
        }

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);

        saveBtn = new JButton(prefill ? "Simpan / Tambah" : "Simpan");
        saveBtn.setBackground(new Color(241, 124, 42));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> onSave(prefill, idMenu));

        cancelBtn = new JButton("Batal");
        cancelBtn.addActionListener(e -> dispose());

        bottom.add(cancelBtn);
        bottom.add(saveBtn);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void onSave(boolean prefill, int idMenu) {
        String nama = namaField.getText().trim();
        String hargaText = hargaField.getText().trim();
        String stokText = stokField.getText().trim();

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
        
        if (stokText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Stok harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int stok;
        try {
            stok = Integer.parseInt(stokText);
            if (stok < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Stok harus berupa angka positif!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // masukkan ke tabel menu
            String safeNama = nama.replace("'", "''");
            String sql = String.format(
                "INSERT INTO menu (id_restoran, nama_menu, harga, stok, status_habis) " +
                "VALUES (%d, '%s', %d, %d, %d)",
                restoran.getIdRestoran(),
                safeNama,
                harga,
                stok,
                (stok == 0 ? 1 : 0)
            );


            auth.getKonektor().query(sql);

            JOptionPane.showMessageDialog(this, "Menu berhasil ditambahkan!");
            dispose();

            if (parent != null) parent.refreshAfterAddMenu();

        } catch (Exception e) {
            logger.severe("Gagal menambahkan menu: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Gagal menambahkan menu: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
