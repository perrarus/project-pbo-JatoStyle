package JatoStyle.models;

public class Keranjang {
    private int idKeranjang;
    private int idUser;
    private int idMenu;
    private int jumlah;
    private String namaMenu;
    private int hargaMenu;
    private String namaRestoran;
    private int idRestoran; 

    // constructor
    public Keranjang() {}

    // getters & setters
    public int getIdKeranjang() { return idKeranjang; }
    public void setIdKeranjang(int idKeranjang) { this.idKeranjang = idKeranjang; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public int getIdMenu() { return idMenu; }
    public void setIdMenu(int idMenu) { this.idMenu = idMenu; }

    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }

    public String getNamaMenu() { return namaMenu; }
    public void setNamaMenu(String namaMenu) { this.namaMenu = namaMenu; }

    public int getHargaMenu() { return hargaMenu; }
    public void setHargaMenu(int hargaMenu) { this.hargaMenu = hargaMenu; }

    public String getNamaRestoran() { return namaRestoran; }
    public void setNamaRestoran(String namaRestoran) { this.namaRestoran = namaRestoran; }

    public int getIdRestoran() { return idRestoran; }
    public void setIdRestoran(int idRestoran) { this.idRestoran = idRestoran; }
}