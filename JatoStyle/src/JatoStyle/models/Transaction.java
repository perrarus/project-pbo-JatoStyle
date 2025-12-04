package JatoStyle.models;

import java.util.Date;

public class Transaction {
    
    private int idTransaksi;
    private int idUser;
    private int idRestoran;
    private Date tanggalTransaksi;
    private int totalPembayaran;
    private String statusTransaksi;
    private String namaRestoran;

    // constructor untuk select data dari db
    public Transaction(int idTransaksi, int idRestoran, Date tanggalTransaksi, int totalPembayaran, String statusTransaksi, String namaRestoran) {
        this.idTransaksi = idTransaksi;
        this.idRestoran = idRestoran;
        this.tanggalTransaksi = tanggalTransaksi;
        this.totalPembayaran = totalPembayaran;
        this.statusTransaksi = statusTransaksi;
        this.namaRestoran = namaRestoran;
    }
    
    // konstruktor untuk insert data baru (memasukkan idUser)
    public Transaction(int idTransaksi, int idUser, int idRestoran, Date tanggalTransaksi, int totalPembayaran, String statusTransaksi, String namaRestoran) {
        this.idTransaksi = idTransaksi;
        this.idUser = idUser;
        this.idRestoran = idRestoran;
        this.tanggalTransaksi = tanggalTransaksi;
        this.totalPembayaran = totalPembayaran;
        this.statusTransaksi = statusTransaksi;
        this.namaRestoran = namaRestoran;
    }

    // getter
    public int getIdTransaksi() { return idTransaksi; }
    public int getIdUser() { return idUser; }
    public int getIdRestoran() { return idRestoran; }
    public Date getTanggalTransaksi() { return tanggalTransaksi; }
    public int getTotalPembayaran() { return totalPembayaran; }
    public String getStatusTransaksi() { return statusTransaksi; }
    public String getNamaRestoran() { return namaRestoran; }
    
    // setter
    public void setIdTransaksi(int idTransaksi) { this.idTransaksi = idTransaksi; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
    public void setIdRestoran(int idRestoran) { this.idRestoran = idRestoran; }
    public void setTanggalTransaksi(Date tanggalTransaksi) { this.tanggalTransaksi = tanggalTransaksi; }
    public void setTotalPembayaran(int totalPembayaran) { this.totalPembayaran = totalPembayaran; }
    public void setStatusTransaksi(String statusTransaksi) { this.statusTransaksi = statusTransaksi; }
    public void setNamaRestoran(String namaRestoran) { this.namaRestoran = namaRestoran; }
}