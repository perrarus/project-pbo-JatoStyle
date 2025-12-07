package JatoStyle.models;

import java.util.Date;

public class Transaction {
    
    private int idTransaksi;
    private int idUser;
    private int idToko;
    private Date tanggalTransaksi;
    private int totalPembayaran;
    private String statusTransaksi;
    private String namaToko;

    // constructor untuk select data dari db
    public Transaction(int idTransaksi, int idToko, Date tanggalTransaksi, int totalPembayaran, String statusTransaksi, String namaToko) {
        this.idTransaksi = idTransaksi;
        this.idToko = idToko;
        this.tanggalTransaksi = tanggalTransaksi;
        this.totalPembayaran = totalPembayaran;
        this.statusTransaksi = statusTransaksi;
        this.namaToko = namaToko;
    }
    
    // konstruktor untuk insert data baru (memasukkan idUser)
    public Transaction(int idTransaksi, int idUser, int idToko, Date tanggalTransaksi, int totalPembayaran, String statusTransaksi, String namaToko) {
        this.idTransaksi = idTransaksi;
        this.idUser = idUser;
        this.idToko = idToko;
        this.tanggalTransaksi = tanggalTransaksi;
        this.totalPembayaran = totalPembayaran;
        this.statusTransaksi = statusTransaksi;
        this.namaToko = namaToko;
    }

    // getter
    public int getIdTransaksi() { return idTransaksi; }
    public int getIdUser() { return idUser; }
    public int getIdToko() { return idToko; }
    public Date getTanggalTransaksi() { return tanggalTransaksi; }
    public int getTotalPembayaran() { return totalPembayaran; }
    public String getStatusTransaksi() { return statusTransaksi; }
    public String getNamaToko() { return namaToko; }
    
    // setter
    public void setIdTransaksi(int idTransaksi) { this.idTransaksi = idTransaksi; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
    public void setIdToko(int idToko) { this.idToko = idToko; }
    public void setTanggalTransaksi(Date tanggalTransaksi) { this.tanggalTransaksi = tanggalTransaksi; }
    public void setTotalPembayaran(int totalPembayaran) { this.totalPembayaran = totalPembayaran; }
    public void setStatusTransaksi(String statusTransaksi) { this.statusTransaksi = statusTransaksi; }
    public void setNamaToko(String namaToko) { this.namaToko = namaToko; }
}