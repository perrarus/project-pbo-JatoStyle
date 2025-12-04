/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package JatoStyle.services;

import JatoStyle.Konektor;
import JatoStyle.models.Review;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewService {
    
    private Konektor konektor;
    
    public ReviewService() {
        this.konektor = new Konektor();
    }
    
    // Cek apakah user sudah pernah membeli di restoran ini (status SUDAH SAMPAI)
    public boolean hasUserPurchasedFromRestoran(int userId, int restoranId) {
        String sql = "SELECT COUNT(*) as count FROM pesanan " +
                    "WHERE id_user = ? AND id_restoran = ? AND status_pesanan = 'SUDAH SAMPAI'";
        
        try (Connection conn = konektor.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, restoranId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            
        } catch (Exception e) {
            System.out.println("Error checking purchase history: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Get review by user untuk restoran tertentu
    public Review getUserReviewForRestoran(int userId, int restoranId) {
        String sql = "SELECT r.*, u.nama as nama_user FROM review r " +
                    "JOIN user u ON r.id_user = u.id_user " +
                    "WHERE r.id_user = ? AND r.id_restoran = ?";
        
        try (Connection conn = konektor.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, restoranId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Review review = new Review();
                review.setIdReview(rs.getInt("id_review"));
                review.setIdUser(rs.getInt("id_user"));
                review.setIdRestoran(rs.getInt("id_restoran"));
                review.setRating(rs.getInt("rating"));
                review.setKomentar(rs.getString("komentar"));
                review.setTanggalReview(rs.getTimestamp("tanggal_review"));
                review.setLastEdited(rs.getTimestamp("last_edited"));
                review.setNamaUser(rs.getString("nama_user"));
                return review;
            }
            
        } catch (Exception e) {
            System.out.println("Error getting user review: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Get semua review untuk restoran
    public List<Review> getAllReviewsForRestoran(int restoranId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.nama as nama_user FROM review r " +
                    "JOIN user u ON r.id_user = u.id_user " +
                    "WHERE r.id_restoran = ? " +
                    "ORDER BY r.tanggal_review DESC";
        
        try (Connection conn = konektor.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, restoranId);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Review review = new Review();
                review.setIdReview(rs.getInt("id_review"));
                review.setIdUser(rs.getInt("id_user"));
                review.setIdRestoran(rs.getInt("id_restoran"));
                review.setRating(rs.getInt("rating"));
                review.setKomentar(rs.getString("komentar"));
                review.setTanggalReview(rs.getTimestamp("tanggal_review"));
                review.setLastEdited(rs.getTimestamp("last_edited"));
                review.setNamaUser(rs.getString("nama_user"));
                reviews.add(review);
            }
            
        } catch (Exception e) {
            System.out.println("Error getting all reviews: " + e.getMessage());
            e.printStackTrace();
        }
        return reviews;
    }
    
    // Add atau update review
    public boolean saveOrUpdateReview(Review review) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = konektor.getConnection();
            
            // Cek apakah sudah ada review
            Review existing = getUserReviewForRestoran(review.getIdUser(), review.getIdRestoran());
            
            if (existing != null) {
                // Update review yang sudah ada
                String sql = "UPDATE review SET rating = ?, komentar = ?, last_edited = NOW() " +
                           "WHERE id_user = ? AND id_restoran = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, review.getRating());
                pstmt.setString(2, review.getKomentar());
                pstmt.setInt(3, review.getIdUser());
                pstmt.setInt(4, review.getIdRestoran());
            } else {
                // Insert review baru
                String sql = "INSERT INTO review (id_user, id_restoran, rating, komentar) " +
                           "VALUES (?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, review.getIdUser());
                pstmt.setInt(2, review.getIdRestoran());
                pstmt.setInt(3, review.getRating());
                pstmt.setString(4, review.getKomentar());
            }
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (Exception e) {
            System.out.println("Error saving review: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Get average rating untuk restoran
    public double getAverageRating(int restoranId) {
        String sql = "SELECT AVG(rating) as avg_rating FROM review WHERE id_restoran = ?";
        
        try (Connection conn = konektor.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, restoranId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("avg_rating");
            }
            
        } catch (Exception e) {
            System.out.println("Error getting average rating: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }
    
    // Get total reviews count
    public int getTotalReviews(int restoranId) {
        String sql = "SELECT COUNT(*) as total FROM review WHERE id_restoran = ?";
        
        try (Connection conn = konektor.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, restoranId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (Exception e) {
            System.out.println("Error getting total reviews: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}
