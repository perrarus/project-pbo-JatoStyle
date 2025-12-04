/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package JatoStyle.models;

public class TransactionDetail {
    private int idDetail;
    private int idPesanan;
    private int idMenu;
    private String namaMenu;
    private int jumlah;
    private int hargaSatuan;
    private int subtotal;

    public TransactionDetail() {}

    public TransactionDetail(int idDetail, int idPesanan, int idMenu, String namaMenu, 
                            int jumlah, int hargaSatuan, int subtotal) {
        this.idDetail = idDetail;
        this.idPesanan = idPesanan;
        this.idMenu = idMenu;
        this.namaMenu = namaMenu;
        this.jumlah = jumlah;
        this.hargaSatuan = hargaSatuan;
        this.subtotal = subtotal;
    }

    // Getter and Setter
    public int getIdDetail() { return idDetail; }
    public void setIdDetail(int idDetail) { this.idDetail = idDetail; }

    public int getIdPesanan() { return idPesanan; }
    public void setIdPesanan(int idPesanan) { this.idPesanan = idPesanan; }

    public int getIdMenu() { return idMenu; }
    public void setIdMenu(int idMenu) { this.idMenu = idMenu; }

    public String getNamaMenu() { return namaMenu; }
    public void setNamaMenu(String namaMenu) { this.namaMenu = namaMenu; }

    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }

    public int getHargaSatuan() { return hargaSatuan; }
    public void setHargaSatuan(int hargaSatuan) { this.hargaSatuan = hargaSatuan; }

    public int getSubtotal() { return subtotal; }
    public void setSubtotal(int subtotal) { this.subtotal = subtotal; }
}
