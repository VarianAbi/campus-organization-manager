package com.campusorg.models;

public class Proker {
    private String namaProker;
    private String status;      // Rencana/Berjalan/Selesai
    private String parentDivisi; // Asal Divisi (Misal: Biro ADKES)
    
    // Detail Tambahan
    private String ketupel;
    private String waketupel;
    private String deskripsiDivisi; // List divisi internal (misal: Acara, Humas, Logistik)

    public Proker(String namaProker, String parentDivisi, String ketupel, String waketupel, String deskripsiDivisi) {
        this.namaProker = namaProker;
        this.parentDivisi = parentDivisi;
        this.ketupel = ketupel;
        this.waketupel = waketupel;
        this.deskripsiDivisi = deskripsiDivisi;
        this.status = "Rencana"; // Default
    }

    public String getNamaProker() { return namaProker; }
    public String getStatus() { return status; }
    public String getParentDivisi() { return parentDivisi; }
    public String getKetupel() { return ketupel; }
    public String getWaketupel() { return waketupel; }
    public String getDeskripsiDivisi() { return deskripsiDivisi; }
    
    public void setStatus(String status) { this.status = status; }
}