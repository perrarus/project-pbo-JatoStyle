package JatoStyle.models;

public class Keranjang {
    private int idKeranjang;
    private int idUser;
    private int idItem;
    private int jumlah;
    private String namaItem;
    private int hargaItem;
    private String namaToko;
    private int idToko; 

    // constructor
    public Keranjang() {}

    // getters & setters
    public int getIdKeranjang() { return idKeranjang; }
    public void setIdKeranjang(int idKeranjang) { this.idKeranjang = idKeranjang; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public int getIdItem() { return idItem; }
    public void setIdItem(int idItem) { this.idItem = idItem; }

    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }

    public String getNamaItem() { return namaItem; }
    public void setNamaItem(String namaItem) { this.namaItem = namaItem; }

    public int getHargaItem() { return hargaItem; }
    public void setHargaItem(int hargaItem) { this.hargaItem = hargaItem; }

    public String getNamaToko() { return namaToko; }
    public void setNamaToko(String namaToko) { this.namaToko = namaToko; }

    public int getIdToko() { return idToko; }
    public void setIdToko(int idToko) { this.idToko = idToko; }
}