package com.campusorg.patterns.singleton;

import com.campusorg.models.*;
import com.campusorg.patterns.composite.Division;
import com.campusorg.patterns.composite.Member;
import com.campusorg.patterns.observer.NewsPublisher;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap; // Pakai LinkedHashMap biar urutannya rapi

public class OrgManager {
    private static OrgManager instance;
    
    private Division rootOrg; // HIMAKOM
    
    // Map untuk Dropdown & Pencarian
    private Map<String, Division> divisionMap = new LinkedHashMap<>();
    
    private NewsPublisher publisher;

    private OrgManager() {
        publisher = new NewsPublisher();
        initStructure();
    }

    public static OrgManager getInstance() {
        if (instance == null) instance = new OrgManager();
        return instance;
    }

    private void initStructure() {
        rootOrg = new Division("HIMAKOM (Pusat)");
        
        // 1. BPH Inti (Petinggi)
        createAndMapDiv("BPH Inti", rootOrg);

        // 2. EKSEKUTIF
        Division eksekutif = new Division("Lembaga Eksekutif");
        rootOrg.addMember(eksekutif);

            // Birokrasi
            Division birokrasi = new Division("Birokrasi");
            eksekutif.addMember(birokrasi);
            createAndMapDiv("Biro KESRA", birokrasi);
            createAndMapDiv("Biro ADKES", birokrasi);

            // Departemen
            Division departemen = new Division("Departemen");
            eksekutif.addMember(departemen);
            createAndMapDiv("Dept. SENOR", departemen);
            createAndMapDiv("Dept. ILPROF", departemen);
            createAndMapDiv("Dept. KOMINFO", departemen);
            createAndMapDiv("Dept. LUHIM", departemen);
            createAndMapDiv("Dept. PSDA", departemen);
            createAndMapDiv("Unit TEKNO", departemen);

        // 3. LEGISLATIF (MPA)
        createAndMapDiv("MPA", rootOrg); 

        // 4. BADAN KHUSUS MSDH (Updated sesuai request)
        // Kita buat Container MSDH dulu
        Division msdh = new Division("MSDH"); // <-- NAMA DIPERBAIKI
        rootOrg.addMember(msdh);
        divisionMap.put("MSDH", msdh); // <-- Key Map juga diperbaiki

            // Sub-Divisi MSDH dimasukkan KE DALAM MSDH
            // TAPI tetap kita masukkan ke map agar bisa dipilih saat Input Anggota
            createAndMapDiv("Divisi Kaderisasi", msdh);
            createAndMapDiv("Divisi Apresiasi & Evaluasi", msdh);
    }

    // Helper
    private void createAndMapDiv(String name, Division parent) {
        Division d = new Division(name);
        parent.addMember(d);
        divisionMap.put(name, d);
    }

    public Division getRoot() { return rootOrg; }
    public NewsPublisher getPublisher() { return publisher; }
    
    public Division getDivisionByName(String name) {
        return divisionMap.get(name);
    }

    public String[] getDivisionNames() {
        return divisionMap.keySet().toArray(new String[0]);
    }

    public void registerMember(Division div, Member m) {
        div.addMember(m);
        publisher.subscribe(m); 
    }
}