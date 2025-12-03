package com.campusorg.models;

public class Proker {
    private String namaProker;
    private String status;       
    private String parentDivisi; 
    private int progress;        
    
    // Detail Tambahan
    private String ketupel;
    private String waketupel;
    private String deskripsiDivisi; 

    // --- KONSTRUKTOR UTAMA (Sesuai punya teman + support parentDivisi) ---
    // Urutan: Nama, Deskripsi, Ketupel, Waketupel, Status, Progress, Divisi
    public Proker(String nama, String deskripsi, String ketupel, String waketupel, String status, int progress, String parentDivisi) {
        this.namaProker = nama;
        this.deskripsiDivisi = deskripsi;
        this.ketupel = ketupel;
        this.waketupel = waketupel;
        this.status = status;
        this.progress = progress;
        this.parentDivisi = parentDivisi;
    }

    // --- KONSTRUKTOR ALTERNATIF (Versi Teman Lama) ---
    public Proker(String nama, String deskripsi, String ketupel, String waketupel, String status) {
        this(nama, deskripsi, ketupel, waketupel, status, 0, "-");
    }

    // --- GETTERS ---
    public String getNamaProker() { return namaProker; }
    public String getStatus() { return status; }
    public String getParentDivisi() { return parentDivisi; }
    public String getKetupel() { return ketupel; }
    public String getWaketupel() { return waketupel; }
    public String getDeskripsiDivisi() { return deskripsiDivisi; }
    public int getProgress() { return progress; }

    // --- SETTERS (WAJIB ADA AGAR HOMEPANEL TIDAK ERROR) ---
    public void setNamaProker(String namaProker) { this.namaProker = namaProker; }
    public void setStatus(String status) { this.status = status; }
    public void setKetupel(String ketupel) { this.ketupel = ketupel; }
    public void setWaketupel(String waketupel) { this.waketupel = waketupel; }
    public void setDeskripsiDivisi(String deskripsi) { this.deskripsiDivisi = deskripsi; }
    
    // Setter tambahan punya teman
    public void setProgress(int progress) { this.progress = progress; }
    public void setParentDivisi(String parentDivisi) { this.parentDivisi = parentDivisi; }
}