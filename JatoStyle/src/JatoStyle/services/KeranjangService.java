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

        // PERBAIKI QUERY: Tambahkan m.id_restoran
        String query = "SELECT k.*, m.nama_menu, m.harga, m.id_restoran, r.nama_restoran "
                + "FROM keranjang k "
                + "JOIN menu m ON k.id_menu = m.id_menu "
                + "JOIN restoran r ON m.id_restoran = r.id_restoran "
                + "WHERE k.id_user=" + idUser + " AND k.status_checkout=0";

        try {
            ResultSet rs = db.getData(query);

            while (rs != null && rs.next()) {
                Keranjang k = new Keranjang();
                k.setIdKeranjang(rs.getInt("id_keranjang"));
                k.setIdUser(rs.getInt("id_user"));
                k.setIdMenu(rs.getInt("id_menu"));
                k.setJumlah(rs.getInt("jumlah"));

                k.setNamaMenu(rs.getString("nama_menu"));
                k.setHargaMenu(rs.getInt("harga"));
                k.setNamaRestoran(rs.getString("nama_restoran"));
                k.setIdRestoran(rs.getInt("id_restoran")); // TAMBAHKAN INI

                list.add(k);
                
                // DEBUG: Tampilkan data yang diambil
                System.out.println("DEBUG Keranjang: " + k.getNamaMenu() + 
                                 " | Resto: " + k.getNamaRestoran() + 
                                 " | ID Resto: " + k.getIdRestoran());
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