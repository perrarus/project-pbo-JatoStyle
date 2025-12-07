/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JatoStyle.models;

public class Item {
    private int idItem;
    private int idToko;
    private String namaItem;
    private int harga;

    public int getIdItem() { return idItem; }
    public void setIdItem(int idItem) { this.idItem = idItem; }

    public int getIdToko() { return idToko; }
    public void setIdToko(int idToko) { this.idToko = idToko; }

    public String getNamaItem() { return namaItem; }
    public void setNamaItem(String namaItem) { this.namaItem = namaItem; }

    public int getHarga() { return harga; }
    public void setHarga(int harga) { this.harga = harga; }
}