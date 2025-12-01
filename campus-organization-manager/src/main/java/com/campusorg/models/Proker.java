package com.campusorg.models;

public class Proker {
    private String namaProker;
    private String status;      // Rencana/Berjalan/Selesai
    private String parentDivisi; // Asal Divisi (Misal: Biro ADKES)
    private int progress; // persentase progress 0-100
    
    // Detail Tambahan
    private String ketupel;
    private String waketupel;
    private String deskripsiDivisi; // List divisi internal (misal: Acara, Humas, Logistik)

    // Konstruktor lama
    public Proker(String nama, String deskripsi, String ketupel, String waketupel, String status) {
        this.namaProker = nama;
        this.deskripsiDivisi = deskripsi;
        this.ketupel = ketupel;
        this.waketupel = waketupel;
        this.status = status;
        this.progress = 0;
        this.parentDivisi = "";
    }

    // Konstruktor dengan progress
    public Proker(String nama, String deskripsi, String ketupel, String waketupel, String status, int progress) {
        this.namaProker = nama;
        this.deskripsiDivisi = deskripsi;
        this.ketupel = ketupel;
        this.waketupel = waketupel;
        this.status = status;
        this.progress = progress;
        this.parentDivisi = "";
    }

    // Konstruktor lengkap (untuk kebutuhan panel)
    public Proker(String nama, String deskripsi, String ketupel, String waketupel, String status, int progress, String parentDivisi) {
        this.namaProker = nama;
        this.deskripsiDivisi = deskripsi;
        this.ketupel = ketupel;
        this.waketupel = waketupel;
        this.status = status;
        this.progress = progress;
        this.parentDivisi = parentDivisi;
    }

    public String getNamaProker() { return namaProker; }
    public String getStatus() { return status; }
    public String getParentDivisi() { return parentDivisi; }
    public String getKetupel() { return ketupel; }
    public String getWaketupel() { return waketupel; }
    public String getDeskripsiDivisi() { return deskripsiDivisi; }
    public int getProgress() { return progress; }

    public void setStatus(String status) { this.status = status; }
    public void setProgress(int progress) { this.progress = progress; }
    public void setParentDivisi(String parentDivisi) { this.parentDivisi = parentDivisi; }
}