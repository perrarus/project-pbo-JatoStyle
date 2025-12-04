package JatoStyle.services;

import JatoStyle.models.Transaction;
import JatoStyle.models.Keranjang;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import JatoStyle.Konektor;
import java.util.Map;
import java.util.stream.Collectors;
import JatoStyle.models.TransactionDetail;

public class TransactionService {
    
    private Konektor konektor;
    
    public TransactionService() {
        this.konektor = new Konektor();
    }
    
    // Method untuk mengambil detail transaksi berdasarkan ID pesanan
    public List<TransactionDetail> getTransactionDetails(int idPesanan) {
        List<TransactionDetail> details = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            System.out.println("=== DEBUG: Mengambil detail transaksi untuk ID Pesanan: " + idPesanan);

            String sql = "SELECT d.id_detail, d.id_pesanan, d.id_menu, m.nama_menu, " +
                        "d.jumlah, d.harga_satuan, (d.jumlah * d.harga_satuan) as subtotal " +
                        "FROM detail_pesanan d " +
                        "JOIN menu m ON d.id_menu = m.id_menu " +
                        "WHERE d.id_pesanan = ? " +
                        "ORDER BY d.id_detail";

            conn = konektor.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idPesanan);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                TransactionDetail detail = new TransactionDetail(
                    rs.getInt("id_detail"),
                    rs.getInt("id_pesanan"),
                    rs.getInt("id_menu"),
                    rs.getString("nama_menu"),
                    rs.getInt("jumlah"),
                    rs.getInt("harga_satuan"),
                    rs.getInt("subtotal")
                );
                details.add(detail);

                System.out.println("Detail ditemukan: " + detail.getNamaMenu() + 
                                 " x" + detail.getJumlah() + 
                                 " @Rp " + detail.getHargaSatuan());
            }

            System.out.println("Total detail ditemukan: " + details.size());

        } catch (Exception e) {
            System.out.println("ERROR di getTransactionDetails: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return details;
    }
    
    // Method untuk mengambil detail transaksi berdasarkan ID pesanan dan ID Restoran
    public List<TransactionDetail> getTransactionDetailsForRestoran(int idPesanan, int idRestoran) {
        List<TransactionDetail> details = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            System.out.println("=== DEBUG: Mengambil detail transaksi untuk ID Pesanan: " + idPesanan + ", ID Restoran: " + idRestoran);

            String sql = "SELECT d.id_detail, d.id_pesanan, d.id_menu, m.nama_menu, " +
                        "d.jumlah, d.harga_satuan, (d.jumlah * d.harga_satuan) as subtotal " +
                        "FROM detail_pesanan d " +
                        "JOIN menu m ON d.id_menu = m.id_menu " +
                        "WHERE d.id_pesanan = ? AND m.id_restoran = ? " +
                        "ORDER BY d.id_detail";

            conn = konektor.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idPesanan);
            pstmt.setInt(2, idRestoran);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                TransactionDetail detail = new TransactionDetail(
                    rs.getInt("id_detail"),
                    rs.getInt("id_pesanan"),
                    rs.getInt("id_menu"),
                    rs.getString("nama_menu"),
                    rs.getInt("jumlah"),
                    rs.getInt("harga_satuan"),
                    rs.getInt("subtotal")
                );
                details.add(detail);

                System.out.println("Detail ditemukan: " + detail.getNamaMenu() + 
                                 " x" + detail.getJumlah() + 
                                 " @Rp " + detail.getHargaSatuan());
            }

            System.out.println("Total detail ditemukan: " + details.size());

        } catch (Exception e) {
            System.out.println("ERROR di getTransactionDetailsForRestoran: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return details;
    }
    
    // mengambil riwayat transaksi untuk user tertentu dari database
    public List<Transaction> getTransactionsByUser(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            System.out.println("=== DEBUG: Memulai getTransactionsByUser untuk user ID: " + userId);
            
            String sql = "SELECT p.id_pesanan, p.id_user, p.id_restoran, p.tanggal_pesan, p.status_pesanan, " +
                        "r.nama_restoran, " +
                        "COALESCE(SUM(d.jumlah * d.harga_satuan), 0) as total_harga " +
                        "FROM pesanan p " +
                        "JOIN restoran r ON p.id_restoran = r.id_restoran " +
                        "LEFT JOIN detail_pesanan d ON p.id_pesanan = d.id_pesanan " +
                        "WHERE p.id_user = ? " +
                        "GROUP BY p.id_pesanan, p.id_user, p.id_restoran, p.tanggal_pesan, p.status_pesanan, r.nama_restoran " +
                        "ORDER BY p.tanggal_pesan DESC";
            
            conn = konektor.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            rs = pstmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                count++;
                Transaction transaction = new Transaction(
                    rs.getInt("id_pesanan"),
                    rs.getInt("id_user"), 
                    rs.getInt("id_restoran"),
                    rs.getTimestamp("tanggal_pesan"),
                    rs.getInt("total_harga"),
                    rs.getString("status_pesanan"),
                    rs.getString("nama_restoran")
                );
                transactions.add(transaction);
                
                System.out.println("Data ditemukan: ID=" + transaction.getIdTransaksi() + 
                                 ", Resto=" + transaction.getNamaRestoran() + 
                                 ", Total=Rp " + transaction.getTotalPembayaran() +
                                 ", Status=" + transaction.getStatusTransaksi());
            }
            
            System.out.println("Total data ditemukan: " + count);
            
        } catch (Exception e) {
            System.out.println("ERROR di getTransactionsByUser: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return transactions;
    }
    
    public boolean createTransactionFromKeranjang(int userId, List<Keranjang> items) {
        if (items.isEmpty()) {
            System.out.println("ERROR: Items keranjang kosong");
            return false;
        }
        
        System.out.println("=== DEBUG: Memulai createTransactionFromKeranjang");
        System.out.println("User ID: " + userId);
        System.out.println("Jumlah items: " + items.size());
        
        // tampilkan detail semua item
        for (int i = 0; i < items.size(); i++) {
            Keranjang k = items.get(i);
            System.out.println("Item " + (i+1) + ": " + k.getNamaMenu() + 
                             " | Resto: " + k.getNamaRestoran() + 
                             " | ID Resto: " + k.getIdRestoran() +
                             " | ID Menu: " + k.getIdMenu());
        }
        
        Connection conn = null;
        boolean success = false;
        
        try {
            conn = konektor.getConnection();
            conn.setAutoCommit(false);
            
            // kelompokkan item keranjang berdasarkan Restoran
            Map<Integer, List<Keranjang>> groupedByResto = items.stream()
                .collect(Collectors.groupingBy(Keranjang::getIdRestoran));
            
            System.out.println("Jumlah restoran berbeda: " + groupedByResto.size());
            
            // cek apakah ada ID Restoran yang 0 atau tidak valid
            for (Map.Entry<Integer, List<Keranjang>> entry : groupedByResto.entrySet()) {
                int idRestoran = entry.getKey();
                if (idRestoran <= 0) {
                    System.out.println("❌ ERROR: ID Restoran tidak valid: " + idRestoran);
                    System.out.println("Items dengan ID Restoran tidak valid:");
                    for (Keranjang k : entry.getValue()) {
                        System.out.println("  - " + k.getNamaMenu() + " | Resto: " + k.getNamaRestoran());
                    }
                    throw new SQLException("ID Restoran tidak valid: " + idRestoran);
                }
            }
            
            // buat transaksi untuk setiap restoran
            for (Map.Entry<Integer, List<Keranjang>> entry : groupedByResto.entrySet()) {
                int idRestoran = entry.getKey();
                List<Keranjang> restoItems = entry.getValue();
                String namaRestoran = restoItems.get(0).getNamaRestoran();
                
                System.out.println("Memproses restoran: " + namaRestoran + " (ID: " + idRestoran + ")");
                
                // cek apakah restoran ada di database
                if (!isRestoranExists(conn, idRestoran)) {
                    System.out.println("❌ ERROR: Restoran dengan ID " + idRestoran + " tidak ditemukan di database");
                    throw new SQLException("Restoran dengan ID " + idRestoran + " tidak ditemukan");
                }
                
                // hitung total harga untuk restoran ini
                int totalHarga = 0;
                for (Keranjang k : restoItems) {
                    totalHarga += k.getJumlah() * k.getHargaMenu();
                    System.out.println("  - Item: " + k.getNamaMenu() + " x" + k.getJumlah() + " = Rp " + (k.getJumlah() * k.getHargaMenu()));
                }
                
                System.out.println("Total untuk " + namaRestoran + ": Rp " + totalHarga);
                
                // insert ke tabel pesanan
                String insertPesananSQL = "INSERT INTO pesanan (id_user, id_restoran, tanggal_pesan, status_pesanan) " +
                                         "VALUES (?, ?, NOW(), 'PENDING')";
                PreparedStatement pstmtPesanan = conn.prepareStatement(insertPesananSQL, Statement.RETURN_GENERATED_KEYS);
                pstmtPesanan.setInt(1, userId);
                pstmtPesanan.setInt(2, idRestoran);
                int affectedRows = pstmtPesanan.executeUpdate();
                
                System.out.println("Insert pesanan - affected rows: " + affectedRows);
                
                // ambil ID pesanan yang baru dibuat
                int idPesanan = -1;
                ResultSet generatedKeys = pstmtPesanan.getGeneratedKeys();
                if (generatedKeys.next()) {
                    idPesanan = generatedKeys.getInt(1);
                    System.out.println("ID Pesanan baru: " + idPesanan);
                } else {
                    System.out.println("Tidak mendapatkan generated keys!");
                }
                pstmtPesanan.close();
                
                if (idPesanan == -1) {
                    throw new SQLException("Gagal mendapatkan ID pesanan");
                }
                
                // insert ke  tabel detail_pesanan untuk setiap item
                String insertDetailSQL = "INSERT INTO detail_pesanan (id_pesanan, id_menu, jumlah, harga_satuan) " +
                                       "VALUES (?, ?, ?, ?)";
                PreparedStatement pstmtDetail = conn.prepareStatement(insertDetailSQL);
                
                int detailCount = 0;
                for (Keranjang k : restoItems) {
                    // cek apakah menu ada di database
                    if (!isMenuExists(conn, k.getIdMenu())) {
                        System.out.println("❌ ERROR: Menu dengan ID " + k.getIdMenu() + " tidak ditemukan");
                        throw new SQLException("Menu dengan ID " + k.getIdMenu() + " tidak ditemukan");
                    }
                    
                    pstmtDetail.setInt(1, idPesanan);
                    pstmtDetail.setInt(2, k.getIdMenu());
                    pstmtDetail.setInt(3, k.getJumlah());
                    pstmtDetail.setDouble(4, k.getHargaMenu());
                    pstmtDetail.addBatch();
                    detailCount++;
                    
                    System.out.println("  - Detail: Pesanan#" + idPesanan + ", Menu#" + k.getIdMenu() + 
                                     " x" + k.getJumlah() + " @Rp " + k.getHargaMenu());
                }
                
                int[] batchResults = pstmtDetail.executeBatch();
                System.out.println("Insert detail - batch executed: " + batchResults.length + " items");
                pstmtDetail.close();
                
                System.out.println("✓ Transaksi berhasil dibuat: Pesanan #" + idPesanan + 
                                 " untuk " + namaRestoran + " - Total: Rp " + totalHarga);
            }
            
            // hapus item keranjang yang sudah di-checkout
            String deleteKeranjangSQL = "DELETE FROM keranjang WHERE id_user = ? AND status_checkout = 0";
            PreparedStatement pstmtDelete = conn.prepareStatement(deleteKeranjangSQL);
            pstmtDelete.setInt(1, userId);
            int deletedRows = pstmtDelete.executeUpdate();
            pstmtDelete.close();
            
            System.out.println("Deleted keranjang rows: " + deletedRows);
            
            // commit transaction
            conn.commit();
            success = true;
            System.out.println("Transaction COMMIT berhasil");
            
        } catch (Exception e) {
            System.out.println("ERROR di createTransactionFromKeranjang: " + e.getMessage());
            e.printStackTrace();
            
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Rollback dilakukan");
                } catch (SQLException ex) {
                    System.out.println("Error saat rollback: " + ex.getMessage());
                }
            }
            success = false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Error saat close connection: " + e.getMessage());
                }
            }
        }
        
        return success;
    }
    
    // method untuk cek apakah restoran exists di database
    private boolean isRestoranExists(Connection conn, int idRestoran) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT 1 FROM restoran WHERE id_restoran = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idRestoran);
            rs = pstmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("Error checking restoran: " + e.getMessage());
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // method untuk cek apakah menu exists di database
    private boolean isMenuExists(Connection conn, int idMenu) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT 1 FROM menu WHERE id_menu = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idMenu);
            rs = pstmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("Error checking menu: " + e.getMessage());
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // method untuk update status pesanan
    public boolean updateStatusPesanan(int idPesanan, String statusBaru) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            System.out.println("Update status pesanan #" + idPesanan + " menjadi: " + statusBaru);
            
            String sql = "UPDATE pesanan SET status_pesanan = ? WHERE id_pesanan = ?";
            conn = konektor.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, statusBaru);
            pstmt.setInt(2, idPesanan);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Update status - affected rows: " + rowsAffected);
            
            return rowsAffected > 0;
            
        } catch (Exception e) {
            System.out.println("ERROR di updateStatusPesanan: " + e.getMessage());
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
    
    // method untuk cek data restoran di database
    public void checkRestoranData() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = konektor.getConnection();
            
            System.out.println("CHECKING RESTORAN DATA");
            
            String sql = "SELECT id_restoran, nama_restoran FROM restoran ORDER BY id_restoran";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            System.out.println("Data restoran di database:");
            while (rs.next()) {
                System.out.println("  - ID: " + rs.getInt("id_restoran") + 
                                 " | Nama: " + rs.getString("nama_restoran"));
            }
            
        } catch (Exception e) {
            System.out.println("ERROR checking restoran data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}