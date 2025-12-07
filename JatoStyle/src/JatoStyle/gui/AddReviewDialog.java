/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package JatoStyle.gui;

import JatoStyle.models.User;
import JatoStyle.models.Toko;
import JatoStyle.models.Review;
import JatoStyle.services.ReviewService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class AddReviewDialog extends JDialog {
    
    private User currentUser;
    private Toko currentRestoran;
    private ReviewService reviewService;
    private Review existingReview;
    private boolean reviewSaved = false;
    
    private JComboBox<Integer> ratingCombo;
    private JTextArea commentArea;
    private JButton saveButton;
    private JButton cancelButton;
    private JLabel[] starLabels = new JLabel[5];
    
    /**
     * Creates new form AddReviewDialog
     */
    public AddReviewDialog(JFrame parent, User user, Toko restoran, 
                      ReviewService reviewService, Review existingReview) {
        super(parent, "Add/Edit Review", true);
        this.currentUser = user;
        this.currentRestoran = restoran;
        this.reviewService = reviewService;
        this.existingReview = existingReview;

        initDialogUI();
        setupUI();
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Method untuk membuat UI dialog secara manual
     */
    private void initDialogUI() {
        // 1. Set properties dialog
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setTitle("Add/Edit Review");
        setModal(true);
        setResizable(false);
        
        // 2. Main panel dengan GridBagLayout untuk kontrol lebih baik
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(250, 240, 227));
        
        // 3. Header dengan "Ayo beri reviewmu!"
        JLabel titleLabel = new JLabel("Ayo beri reviewmu!");
        titleLabel.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        titleLabel.setForeground(new Color(59, 31, 11));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // 4. Content panel dengan GridBagLayout
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // 5. Restoran info
        JLabel restoLabel = new JLabel("Restoran: " + currentRestoran.getNamaRestoran());
        restoLabel.setFont(new Font("Bahnschrift", Font.BOLD, 16));
        restoLabel.setForeground(new Color(59, 31, 11));
        restoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 6. Rating panel
        JPanel ratingPanel = new JPanel();
        ratingPanel.setLayout(new BoxLayout(ratingPanel, BoxLayout.Y_AXIS));
        ratingPanel.setOpaque(false);
        ratingPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        ratingPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel ratingTextLabel = new JLabel("Berikan rating:");
        ratingTextLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 14));
        ratingTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 7. Stars panel
        JPanel starsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        starsPanel.setOpaque(false);
        starsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        // Buat 5 bintang dengan font yang support atau gunakan JLabel dengan text
        for (int i = 0; i < 5; i++) {
            starLabels[i] = new JLabel("★");
            starLabels[i].setFont(new Font("Segoe UI Symbol", Font.BOLD, 36)); // Font yang support bintang
            starLabels[i].setForeground(new Color(200, 200, 200));
            starLabels[i].setName(String.valueOf(i + 1));
            starLabels[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            final int index = i;
            starLabels[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int starRating = index + 1;
                    ratingCombo.setSelectedItem(starRating);
                    updateStarsDisplay(starRating);
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    highlightStars(index + 1);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    int currentRating = (int) ratingCombo.getSelectedItem();
                    updateStarsDisplay(currentRating);
                }
            });
            
            starsPanel.add(starLabels[i]);
        }
        
        // 8. Rating combo
        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        comboPanel.setOpaque(false);
        
        JLabel ratingLabel = new JLabel("Pilih rating:");
        ratingLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 14));
        
        Integer[] ratings = {1, 2, 3, 4, 5};
        ratingCombo = new JComboBox<>(ratings);
        ratingCombo.setFont(new Font("Bahnschrift", Font.PLAIN, 14));
        ratingCombo.setPreferredSize(new Dimension(80, 30));
        
        ratingCombo.addActionListener(e -> {
            int rating = (int) ratingCombo.getSelectedItem();
            updateStarsDisplay(rating);
        });
        
        comboPanel.add(ratingLabel);
        comboPanel.add(ratingCombo);
        
        ratingPanel.add(ratingTextLabel);
        ratingPanel.add(starsPanel);
        ratingPanel.add(comboPanel);
        
        // 9. Comment panel
        JPanel commentPanel = new JPanel();
        commentPanel.setLayout(new BoxLayout(commentPanel, BoxLayout.Y_AXIS));
        commentPanel.setOpaque(false);
        commentPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        commentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel commentLabel = new JLabel("Tulis komentar:");
        commentLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 14));
        commentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        commentLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
        
        commentArea = new JTextArea(6, 40); // Lebih lebar
        commentArea.setFont(new Font("Bahnschrift", Font.PLAIN, 12));
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 75, 31), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JScrollPane scrollPane = new JScrollPane(commentArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setPreferredSize(new Dimension(450, 120));
        scrollPane.setMaximumSize(new Dimension(450, 120));
        
        commentPanel.add(commentLabel);
        commentPanel.add(Box.createVerticalStrut(5));
        commentPanel.add(scrollPane);
        
        // 10. Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        saveButton = new JButton("Simpan Review");
        saveButton.setFont(new Font("Bahnschrift", Font.BOLD, 14));
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        cancelButton = new JButton("Batal");
        cancelButton.setFont(new Font("Bahnschrift", Font.BOLD, 14));
        cancelButton.setBackground(new Color(229, 75, 31));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // 11. Add action listeners
        saveButton.addActionListener(e -> saveReview());
        cancelButton.addActionListener(e -> dispose());
        
        // 12. Load existing data
        if (existingReview != null) {
            ratingCombo.setSelectedItem(existingReview.getRating());
            commentArea.setText(existingReview.getKomentar());
            updateStarsDisplay(existingReview.getRating());
        } else {
            ratingCombo.setSelectedItem(5);
            updateStarsDisplay(5);
        }
        
        // 13. Assemble everything
        contentPanel.add(restoLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(ratingPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(commentPanel);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(550, 500); // Lebih besar untuk text area yang lebih lebar
    }
    
    private void updateStarsDisplay(int rating) {
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                starLabels[i].setText("★");
                starLabels[i].setForeground(new Color(255, 193, 7));
            } else {
                starLabels[i].setText("☆");
                starLabels[i].setForeground(new Color(200, 200, 200));
            }
        }
    }
    
    private void highlightStars(int rating) {
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                starLabels[i].setForeground(new Color(255, 220, 100)); // Highlight color
            } else {
                starLabels[i].setForeground(new Color(200, 200, 200));
            }
        }
    }
    
    private void saveReview() {
        try {
            int rating = (int) ratingCombo.getSelectedItem();
            String comment = commentArea.getText().trim();
            
            // Validasi
            if (comment.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Silakan masukkan komentar untuk review Anda.",
                    "Validasi Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (rating < 1 || rating > 5) {
                JOptionPane.showMessageDialog(this,
                    "Rating harus antara 1-5.",
                    "Validasi Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Cek purchase history
            boolean hasPurchased = reviewService.hasUserPurchasedFromRestoran(
                currentUser.getIdUser(),
                currentRestoran.getIdRestoran()
            );
            
            if (!hasPurchased) {
                JOptionPane.showMessageDialog(this,
                    "Anda harus pernah menyelesaikan pembelian dari restoran ini untuk bisa memberikan review.\n" +
                    "Hanya pengguna dengan pesanan status 'SUDAH SAMPAI' yang dapat mereview.",
                    "Tidak Dapat Mereview",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Save review
            Review review = new Review();
            review.setIdUser(currentUser.getIdUser());
            review.setIdRestoran(currentRestoran.getIdRestoran());
            review.setRating(rating);
            review.setKomentar(comment);
            
            boolean success = reviewService.saveOrUpdateReview(review);
            
            if (success) {
                reviewSaved = true;
                JOptionPane.showMessageDialog(this,
                    existingReview != null ? "Review berhasil diperbarui!" : "Review berhasil ditambahkan!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan review.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            System.err.println("Error saving review: " + e.getMessage());
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setupUI() {
        getContentPane().setBackground(new Color(250, 240, 227));
    }
    
    public boolean isReviewSaved() {
        return reviewSaved;
    }
    
    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, 
                "Dialog ini harus dibuka melalui LihatReviewFrame", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
}