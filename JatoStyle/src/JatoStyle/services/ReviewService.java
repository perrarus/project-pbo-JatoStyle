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
    
    // Cek apakah user sudah pernah membeli di toko ini (status SUDAH SAMPAI)
    public boolean hasUserPurchasedFromToko(int userId, int tokoId) {
        String sql = "SELECT COUNT(*) as count FROM pesanan " +
                    "WHERE id_user = ? AND id_toko = ? AND status_pesanan = 'SUDAH SAMPAI'";
        
        try (Connection conn = konektor.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, tokoId);
            
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
    
    // Get review by user untuk toko tertentu
    public Review getUserReviewForToko(int userId, int tokoId) {
        String sql = "SELECT r.*, u.nama as nama_user FROM review r " +
                    "JOIN user u ON r.id_user = u.id_user " +
                    "WHERE r.id_user = ? AND r.id_toko = ?";
        
        try (Connection conn = konektor.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, tokoId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Review review = new Review();
                review.setIdReview(rs.getInt("id_review"));
                review.setIdUser(rs.getInt("id_user"));
                review.setIdToko(rs.getInt("id_toko"));
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
    
    // Get semua review untuk toko
    public List<Review> getAllReviewsForToko(int tokoId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.nama as nama_user FROM review r " +
                    "JOIN user u ON r.id_user = u.id_user " +
                    "WHERE r.id_toko = ? " +
                    "ORDER BY r.tanggal_review DESC";
        
        try (Connection conn = konektor.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tokoId);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Review review = new Review();
                review.setIdReview(rs.getInt("id_review"));
                review.setIdUser(rs.getInt("id_user"));
                review.setIdToko(rs.getInt("id_toko"));
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
            Review existing = getUserReviewForToko(review.getIdUser(), review.getIdToko());
            
            if (existing != null) {
                // Update review yang sudah ada
                String sql = "UPDATE review SET rating = ?, komentar = ?, last_edited = NOW() " +
                           "WHERE id_user = ? AND id_toko = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, review.getRating());
                pstmt.setString(2, review.getKomentar());
                pstmt.setInt(3, review.getIdUser());
                pstmt.setInt(4, review.getIdToko());
            } else {
                // Insert review baru
                String sql = "INSERT INTO review (id_user, id_toko, rating, komentar) " +
                           "VALUES (?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, review.getIdUser());
                pstmt.setInt(2, review.getIdToko());
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
    
    // Get average rating untuk toko
    public double getAverageRating(int tokoId) {
        String sql = "SELECT AVG(rating) as avg_rating FROM review WHERE id_toko = ?";
        
        try (Connection conn = konektor.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tokoId);
            
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
    public int getTotalReviews(int tokoId) {
        String sql = "SELECT COUNT(*) as total FROM review WHERE id_toko = ?";
        
        try (Connection conn = konektor.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tokoId);
            
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