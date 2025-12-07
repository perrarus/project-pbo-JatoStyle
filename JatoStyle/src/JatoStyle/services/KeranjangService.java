package JatoStyle.services;

import JatoStyle.Konektor;
import JatoStyle.models.Keranjang;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class KeranjangService {
    
    Konektor db = new Konektor();

    // ambil keranjang user (status_checkout = 0)
    public List<Keranjang> getKeranjangUser(int idUser) {
        List<Keranjang> list = new ArrayList<>();

        // PERBAIKI QUERY: Tambahkan i.id_toko
        String query = "SELECT k.*, i.nama_item, i.harga, i.id_toko, t.nama_toko "
                + "FROM keranjang k "
                + "JOIN item i ON k.id_item = i.id_item "
                + "JOIN toko t ON i.id_toko = t.id_toko "
                + "WHERE k.id_user=" + idUser + " AND k.status_checkout=0";

        try {
            ResultSet rs = db.getData(query);

            while (rs != null && rs.next()) {
                Keranjang k = new Keranjang();
                k.setIdKeranjang(rs.getInt("id_keranjang"));
                k.setIdUser(rs.getInt("id_user"));
                k.setIdItem(rs.getInt("id_item"));
                k.setJumlah(rs.getInt("jumlah"));

                k.setNamaItem(rs.getString("nama_item"));
                k.setHargaItem(rs.getInt("harga"));
                k.setNamaToko(rs.getString("nama_toko"));
                k.setIdToko(rs.getInt("id_toko")); // TAMBAHKAN INI

                list.add(k);
                
                // DEBUG: Tampilkan data yang diambil
                System.out.println("DEBUG Keranjang: " + k.getNamaItem() + 
                                 " | Toko: " + k.getNamaToko() + 
                                 " | ID Toko: " + k.getIdToko());
            }
        } catch (Exception e) {
            System.out.println("Error get keranjang: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    // update jumlah
    public void updateJumlah(int idKeranjang, int jumlah) {
        String q = "UPDATE keranjang SET jumlah=" + jumlah 
                   + " WHERE id_keranjang=" + idKeranjang;
        db.query(q);
    }

    // hapus item
    public void deleteItem(int idKeranjang) {
        String q = "DELETE FROM keranjang WHERE id_keranjang=" + idKeranjang;
        db.query(q);
    }
    
    public boolean clearAll(int idUser) {
        String sql = "DELETE FROM keranjang WHERE id_user = ?";

        try (Connection conn = Konektor.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUser);
            pstmt.executeUpdate();

            return true; 

        } catch (SQLException e) {
            System.err.println("Gagal mengosongkan keranjang untuk User ID " + idUser + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}