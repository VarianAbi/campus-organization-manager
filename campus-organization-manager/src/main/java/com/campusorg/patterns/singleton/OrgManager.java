package com.campusorg.patterns.singleton;

import java.util.LinkedHashMap;
import java.util.Map;

import com.campusorg.database.DataStore;
import com.campusorg.patterns.composite.Division;
import com.campusorg.patterns.composite.Member;
import com.campusorg.patterns.composite.OrgComponent;
import com.campusorg.patterns.observer.NewsPublisher;

@SuppressWarnings({"java:S6548", "squid:S6548"})
public class OrgManager {
    
    private Division rootOrg; // HIMAKOM
    
    // Map untuk Dropdown & Pencarian
    private final Map<String, Division> divisionMap = new LinkedHashMap<>();
    
    private final NewsPublisher publisher;

    private OrgManager() {
        publisher = new NewsPublisher();
        
        // Coba load data dari JSON dulu
        Division loadedData = DataStore.loadData();
        
        if (loadedData != null) {
            // Data berhasil dimuat dari JSON
            rootOrg = loadedData;
            rebuildDivisionMap(rootOrg); // Rebuild map untuk dropdown
            resubscribeAllMembers(rootOrg); // Re-subscribe semua member ke publisher
        } else {
            // Tidak ada data tersimpan, pakai struktur default
            initStructure();
        }
    }

    private static class Holder { private static final OrgManager INSTANCE = new OrgManager(); }
    public static OrgManager getInstance() { return Holder.INSTANCE; }

    private void initStructure() {
        rootOrg = new Division("HIMAKOM (Pusat)");
        
        // 1. BPH Inti (Petinggi)
        createAndMapDiv("BPH Inti", rootOrg);

        // 2. BIRO (Langsung di bawah HIMAKOM)
        createAndMapDiv("Biro KESRA", rootOrg);
        createAndMapDiv("Biro ADKES", rootOrg);

        // 3. DEPARTEMEN (Langsung di bawah HIMAKOM)
        createAndMapDiv("Dept. SENOR", rootOrg);
        createAndMapDiv("Dept. ILPROF", rootOrg);
        createAndMapDiv("Dept. KOMINFO", rootOrg);
        createAndMapDiv("Dept. LUHIM", rootOrg);
        createAndMapDiv("Dept. PSDA", rootOrg);
        createAndMapDiv("Unit TEKNO", rootOrg);

        // 4. LEGISLATIF (MPA)
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
        return divisionMap.keySet().toArray(new String[divisionMap.size()]);
    }

    public void registerMember(Division div, Member m) {
        div.addMember(m);
        publisher.subscribe(m); 
    }

    // ==================== PERSISTENCE: SAVE/LOAD ====================
    
    /**
     * Simpan seluruh data organisasi ke JSON
     */
    public void saveData() {
        DataStore.saveData(rootOrg);
    }

    /**
     * Rebuild divisionMap dari struktur tree (untuk setelah load JSON)
     */
    private void rebuildDivisionMap(Division div) {
        if (!"HIMAKOM (Pusat)".equals(div.getName())) {
            divisionMap.put(div.getName(), div);
        }
        for (OrgComponent comp : div.getMembers()) {
            if (comp instanceof Division division) {
                rebuildDivisionMap(division);
            }
        }
    }

    /**
     * Re-subscribe semua member ke NewsPublisher (setelah load JSON)
     */
    private void resubscribeAllMembers(Division div) {
        for (OrgComponent comp : div.getMembers()) {
            switch (comp) {
                case Member member -> publisher.subscribe(member);
                case Division division -> resubscribeAllMembers(division);
                default -> { /* ignore other component types */ }
            }
        }
    }
}