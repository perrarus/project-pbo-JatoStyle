/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package JatoStyle.gui;

import JatoStyle.models.User;
import JatoStyle.services.AuthService;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.util.logging.Level;
import javax.swing.SwingConstants; 
import javax.swing.border.AbstractBorder;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Component;
import java.awt.Insets;

public class ProfileFrame extends javax.swing.JFrame {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ProfileFrame.class.getName());
    private User currentUser;
    private AuthService authService;
    private JFrame dashboardFrame;
    private boolean isEditMode = false;
    
    // warna
    private final Color PRIMARY_BG = new Color(250, 240, 227); 
    private final Color CARD_BG = Color.WHITE; 
    private final Color PRIMARY_ORANGE = new Color(241, 124, 42); 
    private final Color BORDER_ORANGE = new Color(229, 75, 31); 

    /**
     * Creates new form ProfileFrame
     */
    public ProfileFrame(User user, JFrame dashboard) {
        if (user == null || dashboard == null) {
            throw new IllegalArgumentException("User dan Dashboard tidak boleh null.");
        }
        this.currentUser = user;
        this.dashboardFrame = dashboard;
        this.authService = new AuthService();

        initComponents();
        initializeCustomComponents();
        applyCustomStyles();
        loadUserData();

        setTitle("Profil Pengguna");
        setLocationRelativeTo(null);
    }
    
    public ProfileFrame() {
        initComponents();
        this.currentUser = new User("Test User", "test@example.com", "password123", "081234567890");
        this.authService = new AuthService();
        
        applyCustomStyles();
        initializeCustomComponents();
        loadUserData();
        setTitle("Profil Pengguna (Test Mode)");
        setLocationRelativeTo(null);
    }
    
    private void initializeCustomComponents() {
        // listener untuk masuk ke Edit Mode saat field diklik
        MouseAdapter editModeListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                setEditMode(true);
            }
        };

        // tombol cancel
        btnCancle.addActionListener(evt -> {
            loadUserData();   
            setEditMode(false); 
        });


        txtNama.addMouseListener(editModeListener);
        txtNoHP.addMouseListener(editModeListener);
        txtEmail.addMouseListener(editModeListener);
    }
    
   private void applyCustomStyles() {
        getContentPane().setBackground(PRIMARY_BG);
        jPanel3.setBackground(CARD_BG);
        Border kotak = BorderFactory.createLineBorder(BORDER_ORANGE, 2);
        Border padding2 = BorderFactory.createEmptyBorder(15, 15, 15, 15);
        jPanel3.setBorder(BorderFactory.createCompoundBorder(kotak, padding2));

        // header
        Color darkText = new Color(59, 31, 11);
        jLabel2.setForeground(darkText);
        jLabel2.setFont(new Font("Bahnschrift", Font.BOLD, 24));
        
        // font label & field
        Font fontLabel = new Font("Bahnschrift", Font.PLAIN, 16);
        Font fontField = new Font("Bahnschrift", Font.PLAIN, 16);

        jLabel3.setFont(fontLabel);
        jLabel4.setFont(fontLabel);
        jLabel5.setFont(fontLabel);
 
        java.awt.Dimension labelDim = new java.awt.Dimension(120, 30);
        jLabel3.setPreferredSize(labelDim);
        jLabel4.setPreferredSize(labelDim);
        jLabel5.setPreferredSize(labelDim);
        
        jLabel3.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel4.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel5.setHorizontalAlignment(SwingConstants.LEFT);

        txtNama.setFont(fontField);
        txtNoHP.setFont(fontField);
        txtEmail.setFont(fontField);

        // styling border text field
        Border lineBorder = BorderFactory.createLineBorder(BORDER_ORANGE, 1);
        Border padding = BorderFactory.createEmptyBorder(5, 10, 5, 10);
        Border compoundBorder = BorderFactory.createCompoundBorder(lineBorder, padding);

        txtNama.setBorder(compoundBorder);
        txtNoHP.setBorder(compoundBorder);
        txtEmail.setBorder(compoundBorder);

        // styling button
        btnUpdate.setText("Update");
        styleButton(btnUpdate, PRIMARY_ORANGE);

        btnCancle.setText("Cancle");
        styleButton(btnCancle, PRIMARY_ORANGE);

        btnKembali.setText("Kembali");
        styleButton(btnKembali, PRIMARY_ORANGE);

        // logout buttom
        logoutButton.setText("Logout");
        styleButton(logoutButton, PRIMARY_ORANGE);
        logoutButton.setForeground(Color.WHITE);

        // tombol update dan cancel disembunyikan
        setEditMode(false);
    }
   
    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
      
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1), 
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
    }

    
    private void setEditMode(boolean edit) {
        this.isEditMode = edit;

        txtNama.setEditable(edit);
        txtNoHP.setEditable(edit);
        txtEmail.setEditable(edit);

        // warna background fields
        Color editableBg = Color.WHITE;
        Color readOnlyBg = CARD_BG; // Gunakan warna latar belakang JPanel saat read-only
        txtNama.setBackground(edit ? editableBg : readOnlyBg);
        txtNoHP.setBackground(edit ? editableBg : readOnlyBg);
        txtEmail.setBackground(edit ? editableBg : readOnlyBg);

        // visibilitas Tombol
        btnUpdate.setVisible(edit);
        btnCancle.setVisible(edit);
        btnKembali.setVisible(!edit); 
        
        if (edit) {
            txtNama.requestFocusInWindow();
        } else {
            txtNama.setEditable(false);
            txtNoHP.setEditable(false);
            txtEmail.setEditable(false);
        }
    }
    
    private class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int stroke;

        RoundBorder(Color color, int radius, int stroke) {
            this.color = color;
            this.radius = radius;
            this.stroke = stroke;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(color);
            g2d.setStroke(new java.awt.BasicStroke(stroke));
            g2d.drawRoundRect(x + stroke/2, y + stroke/2, width - stroke, height - stroke, radius, radius);
            
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            int inset = radius / 2 + stroke;
            return new Insets(inset, inset, inset, inset);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            int inset = radius / 2 + stroke;
            insets.left = insets.right = insets.top = insets.bottom = inset;
            return insets;
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

        jLabel1 = new javax.swing.JLabel();
        logoutButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        txtNoHP = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        btnKembali = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnCancle = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/JatoStyle/gui/dashboardlogo.png"))); // NOI18N

        logoutButton.setFont(new java.awt.Font("Bahnschrift", 1, 12)); // NOI18N
        logoutButton.setText("Logout");
        logoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutButtonActionPerformed(evt);
            }
        });

        jLabel2.setEditable(false);
        jLabel2.setBackground(new java.awt.Color(250, 240, 227));
        jLabel2.setFont(new java.awt.Font("Bahnschrift", 1, 20)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(250, 240, 227));
        jLabel2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jLabel2.setBorder(null);
        jLabel2.setDisabledTextColor(new java.awt.Color(59, 31, 11));
        jLabel2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLabel2ActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(250, 240, 227));

        jLabel3.setFont(new java.awt.Font("Bahnschrift", 1, 12)); // NOI18N
        jLabel3.setText("Nama           :");

        jLabel4.setFont(new java.awt.Font("Bahnschrift", 1, 12)); // NOI18N
        jLabel4.setText("No. Telepon :");

        jLabel5.setFont(new java.awt.Font("Bahnschrift", 1, 12)); // NOI18N
        jLabel5.setText("Email            :");

        txtNama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNamaKeyPressed(evt);
            }
        });

        txtNoHP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNoHPKeyPressed(evt);
            }
        });

        txtEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEmailKeyPressed(evt);
            }
        });

        btnKembali.setText("Kembali");
        btnKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliActionPerformed(evt);
            }
        });

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnCancle.setText("Cancle");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE))
                .addGap(46, 46, 46)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                    .addComponent(txtNoHP)
                    .addComponent(txtNama))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancle)
                .addGap(18, 18, 18)
                .addComponent(btnUpdate)
                .addGap(30, 30, 30)
                .addComponent(btnKembali)
                .addGap(27, 27, 27))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtNoHP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 140, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnKembali)
                    .addComponent(btnUpdate)
                    .addComponent(btnCancle))
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 536, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(logoutButton)))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(logoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(31, 31, 31)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(78, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void loadUserData() {
        if (currentUser != null) {
            txtNama.setText(currentUser.getNama());
            txtNoHP.setText(currentUser.getNoHp());
            txtEmail.setText(currentUser.getEmail());
            jLabel2.setText("Profil Pengguna: " + currentUser.getNama());
        } else {
            txtNama.setText("Nama Tidak Tersedia");
            txtNoHP.setText("No HP Tidak Tersedia");
            txtEmail.setText("Email Tidak Tersedia");
            jLabel2.setText("Profil Pengguna");
        }
    }
    
    private void btnCancleActionPerformed(java.awt.event.ActionEvent evt) {
        loadUserData();
        setEditMode(false);
    }
    
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

    private void jLabel2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLabel2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel2ActionPerformed

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        if (!isEditMode && dashboardFrame != null) {
            this.dispose();
            dashboardFrame.setVisible(true);
        } else if (isEditMode) {
            btnCancleActionPerformed(evt);
        }
    }//GEN-LAST:event_btnKembaliActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (currentUser == null) {
              JOptionPane.showMessageDialog(this, "Tidak ada user yang login.", "Error", JOptionPane.ERROR_MESSAGE);
              return;
        }

        String namaBaru = txtNama.getText().trim();
        String noHpBaru = txtNoHP.getText().trim();
        String emailBaru = txtEmail.getText().trim();

        if (namaBaru.isEmpty() || noHpBaru.isEmpty() || emailBaru.isEmpty()) {
              JOptionPane.showMessageDialog(this, "Semua kolom harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
              return;
        }

        User updatedUser = new User(
            namaBaru,
            emailBaru,
            currentUser.getPassword(),
            noHpBaru
        );
        
        updatedUser.setIdUser(currentUser.getIdUser());

        if (authService.updateProfileUser(updatedUser)) {
            this.currentUser.setNama(namaBaru);
            this.currentUser.setEmail(emailBaru);
            this.currentUser.setNoHp(noHpBaru);

            JOptionPane.showMessageDialog(this, "Profil berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            setEditMode(false);

            if (dashboardFrame instanceof DashboardUserFrame) {
                ((DashboardUserFrame) dashboardFrame).setUserData(this.currentUser);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate profil. Silakan coba lagi.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void txtNamaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNamaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            btnUpdate.doClick();  
        }
    }//GEN-LAST:event_txtNamaKeyPressed

    private void txtNoHPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoHPKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            btnUpdate.doClick();  
        }
    }//GEN-LAST:event_txtNoHPKeyPressed

    private void txtEmailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmailKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            btnUpdate.doClick();  
        }
    }//GEN-LAST:event_txtEmailKeyPressed

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
        
        java.awt.EventQueue.invokeLater(() -> new ProfileFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancle;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton logoutButton;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtNoHP;
    // End of variables declaration//GEN-END:variables
}
