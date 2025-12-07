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

            String sql = "SELECT d.id_detail, d.id_pesanan, d.id_item, i.nama_item, " +
                        "d.jumlah, d.harga_satuan, (d.jumlah * d.harga_satuan) as subtotal " +
                        "FROM detail_pesanan d " +
                        "JOIN item i ON d.id_item = i.id_item " +
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
                    rs.getInt("id_item"),
                    rs.getString("nama_item"),
                    rs.getInt("jumlah"),
                    rs.getInt("harga_satuan"),
                    rs.getInt("subtotal")
                );
                details.add(detail);

                System.out.println("Detail ditemukan: " + detail.getNamaItem() + 
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
    
    // Method untuk mengambil detail transaksi berdasarkan ID pesanan dan ID Toko
    public List<TransactionDetail> getTransactionDetailsForToko(int idPesanan, int idToko) {
        List<TransactionDetail> details = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            System.out.println("=== DEBUG: Mengambil detail transaksi untuk ID Pesanan: " + idPesanan + ", ID Toko: " + idToko);

            String sql = "SELECT d.id_detail, d.id_pesanan, d.id_item, i.nama_item, " +
                        "d.jumlah, d.harga_satuan, (d.jumlah * d.harga_satuan) as subtotal " +
                        "FROM detail_pesanan d " +
                        "JOIN item i ON d.id_item = i.id_item " +
                        "WHERE d.id_pesanan = ? AND i.id_toko = ? " +
                        "ORDER BY d.id_detail";

            conn = konektor.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idPesanan);
            pstmt.setInt(2, idToko);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                TransactionDetail detail = new TransactionDetail(
                    rs.getInt("id_detail"),
                    rs.getInt("id_pesanan"),
                    rs.getInt("id_item"),
                    rs.getString("nama_item"),
                    rs.getInt("jumlah"),
                    rs.getInt("harga_satuan"),
                    rs.getInt("subtotal")
                );
                details.add(detail);

                System.out.println("Detail ditemukan: " + detail.getNamaItem() + 
                                 " x" + detail.getJumlah() + 
                                 " @Rp " + detail.getHargaSatuan());
            }

            System.out.println("Total detail ditemukan: " + details.size());

        } catch (Exception e) {
            System.out.println("ERROR di getTransactionDetailsForToko: " + e.getMessage());
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
            
            String sql = "SELECT p.id_pesanan, p.id_user, p.id_toko, p.tanggal_pesan, p.status_pesanan, " +
                        "t.nama_toko, " +
                        "COALESCE(SUM(d.jumlah * d.harga_satuan), 0) as total_harga " +
                        "FROM pesanan p " +
                        "JOIN toko t ON p.id_toko = t.id_toko " +
                        "LEFT JOIN detail_pesanan d ON p.id_pesanan = d.id_pesanan " +
                        "WHERE p.id_user = ? " +
                        "GROUP BY p.id_pesanan, p.id_user, p.id_toko, p.tanggal_pesan, p.status_pesanan, t.nama_toko " +
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
                    rs.getInt("id_toko"),
                    rs.getTimestamp("tanggal_pesan"),
                    rs.getInt("total_harga"),
                    rs.getString("status_pesanan"),
                    rs.getString("nama_toko")
                );
                transactions.add(transaction);
                
                System.out.println("Data ditemukan: ID=" + transaction.getIdTransaksi() + 
                                 ", Toko=" + transaction.getNamaToko() + 
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
            System.out.println("Item " + (i+1) + ": " + k.getNamaItem() + 
                             " | Toko: " + k.getNamaToko() + 
                             " | ID Toko: " + k.getIdToko() +
                             " | ID Item: " + k.getIdItem());
        }
        
        Connection conn = null;
        boolean success = false;
        
        try {
            conn = konektor.getConnection();
            conn.setAutoCommit(false);
            
            // kelompokkan item keranjang berdasarkan Toko
            Map<Integer, List<Keranjang>> groupedByToko = items.stream()
                .collect(Collectors.groupingBy(Keranjang::getIdToko));
            
            System.out.println("Jumlah toko berbeda: " + groupedByToko.size());
            
            // cek apakah ada ID Toko yang 0 atau tidak valid
            for (Map.Entry<Integer, List<Keranjang>> entry : groupedByToko.entrySet()) {
                int idToko = entry.getKey();
                if (idToko <= 0) {
                    System.out.println("❌ ERROR: ID Toko tidak valid: " + idToko);
                    System.out.println("Items dengan ID Toko tidak valid:");
                    for (Keranjang k : entry.getValue()) {
                        System.out.println("  - " + k.getNamaItem() + " | Toko: " + k.getNamaToko());
                    }
                    throw new SQLException("ID Toko tidak valid: " + idToko);
                }
            }
            
            // buat transaksi untuk setiap toko
            for (Map.Entry<Integer, List<Keranjang>> entry : groupedByToko.entrySet()) {
                int idToko = entry.getKey();
                List<Keranjang> tokoItems = entry.getValue();
                String namaToko = tokoItems.get(0).getNamaToko();
                
                System.out.println("Memproses toko: " + namaToko + " (ID: " + idToko + ")");
                
                // cek apakah toko ada di database
                if (!isTokoExists(conn, idToko)) {
                    System.out.println("❌ ERROR: Toko dengan ID " + idToko + " tidak ditemukan di database");
                    throw new SQLException("Toko dengan ID " + idToko + " tidak ditemukan");
                }
                
                // hitung total harga untuk toko ini
                int totalHarga = 0;
                for (Keranjang k : tokoItems) {
                    totalHarga += k.getJumlah() * k.getHargaItem();
                    System.out.println("  - Item: " + k.getNamaItem() + " x" + k.getJumlah() + " = Rp " + (k.getJumlah() * k.getHargaItem()));
                }
                
                System.out.println("Total untuk " + namaToko + ": Rp " + totalHarga);
                
                // insert ke tabel pesanan
                String insertPesananSQL = "INSERT INTO pesanan (id_user, id_toko, tanggal_pesan, status_pesanan) " +
                                         "VALUES (?, ?, NOW(), 'PENDING')";
                PreparedStatement pstmtPesanan = conn.prepareStatement(insertPesananSQL, Statement.RETURN_GENERATED_KEYS);
                pstmtPesanan.setInt(1, userId);
                pstmtPesanan.setInt(2, idToko);
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
                String insertDetailSQL = "INSERT INTO detail_pesanan (id_pesanan, id_item, jumlah, harga_satuan) " +
                                       "VALUES (?, ?, ?, ?)";
                PreparedStatement pstmtDetail = conn.prepareStatement(insertDetailSQL);
                
                int detailCount = 0;
                for (Keranjang k : tokoItems) {
                    // cek apakah item ada di database
                    if (!isItemExists(conn, k.getIdItem())) {
                        System.out.println("❌ ERROR: Item dengan ID " + k.getIdItem() + " tidak ditemukan");
                        throw new SQLException("Item dengan ID " + k.getIdItem() + " tidak ditemukan");
                    }
                    
                    pstmtDetail.setInt(1, idPesanan);
                    pstmtDetail.setInt(2, k.getIdItem());
                    pstmtDetail.setInt(3, k.getJumlah());
                    pstmtDetail.setDouble(4, k.getHargaItem());
                    pstmtDetail.addBatch();
                    detailCount++;
                    
                    System.out.println("  - Detail: Pesanan#" + idPesanan + ", Item#" + k.getIdItem() + 
                                     " x" + k.getJumlah() + " @Rp " + k.getHargaItem());
                }
                
                int[] batchResults = pstmtDetail.executeBatch();
                System.out.println("Insert detail - batch executed: " + batchResults.length + " items");
                pstmtDetail.close();
                
                System.out.println("✓ Transaksi berhasil dibuat: Pesanan #" + idPesanan + 
                                 " untuk " + namaToko + " - Total: Rp " + totalHarga);
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
    
    // method untuk cek apakah toko exists di database
    private boolean isTokoExists(Connection conn, int idToko) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT 1 FROM toko WHERE id_toko = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idToko);
            rs = pstmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("Error checking toko: " + e.getMessage());
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
    
    // method untuk cek apakah item exists di database
    private boolean isItemExists(Connection conn, int idItem) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT 1 FROM item WHERE id_item = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idItem);
            rs = pstmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("Error checking item: " + e.getMessage());
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
    
    // method untuk cek data toko di database
    public void checkTokoData() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = konektor.getConnection();
            
            System.out.println("CHECKING TOKO DATA");
            
            String sql = "SELECT id_toko, nama_toko FROM toko ORDER BY id_toko";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            System.out.println("Data toko di database:");
            while (rs.next()) {
                System.out.println("  - ID: " + rs.getInt("id_toko") + 
                                 " | Nama: " + rs.getString("nama_toko"));
            }
            
        } catch (Exception e) {
            System.out.println("ERROR checking toko data: " + e.getMessage());
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