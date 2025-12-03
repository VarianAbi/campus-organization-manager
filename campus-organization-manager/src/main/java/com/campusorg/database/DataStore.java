package com.campusorg.database;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.campusorg.models.Proker;
import com.campusorg.patterns.composite.Division;
import com.campusorg.patterns.composite.Member;
import com.campusorg.patterns.composite.OfficeHolder;
import com.campusorg.patterns.composite.OrgComponent;
import com.campusorg.patterns.composite.StaffAhli;
import com.campusorg.patterns.composite.StaffMuda;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * DataStore: Class untuk save/load data organisasi ke/dari JSON
 * 
 * File JSON disimpan di: campus-organization-manager/data/organization.json
 * 
 * Format JSON:
 * {
 * "divisions": [
 * {
 * "name": "BPH Inti",
 * "members": [...],
 * "prokers": [...],
 * "subdiv": [...]
 * }
 * ]
 * }
 */
public class DataStore {

    private static final String DATA_DIR = "data";
    private static final String DATA_FILE = "data/organization.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // ==================== SAVE DATA ====================
    /**
     * Simpan seluruh struktur organisasi ke JSON
     * 
     * @param rootOrg Division root (HIMAKOM)
     */
    public static void saveData(Division rootOrg) {
        try {
            // Buat folder data kalau belum ada
            Files.createDirectories(Paths.get(DATA_DIR));

            // Convert Division ke JSON-friendly format
            JsonObject rootJson = divisionToJson(rootOrg);

            // Tulis ke file
            try (FileWriter writer = new FileWriter(DATA_FILE)) {
                gson.toJson(rootJson, writer);
                System.out.println("✅ Data berhasil disimpan ke: " + DATA_FILE);
            }

        } catch (IOException e) {
            System.err.println("❌ Error menyimpan data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== LOAD DATA ====================
    /**
     * Load struktur organisasi dari JSON
     * 
     * @return Division root atau null jika file tidak ada
     */
    public static Division loadData() {
        File file = new File(DATA_FILE);

        // Kalau file tidak ada, return null (pakai data default)
        if (!file.exists()) {
            System.out.println("ℹ️ File data tidak ditemukan. Menggunakan data default.");
            return null;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonObject rootJson = gson.fromJson(reader, JsonObject.class);
            Division rootOrg = jsonToDivision(rootJson);

            System.out.println("✅ Data berhasil dimuat dari: " + DATA_FILE);
            return rootOrg;

        } catch (IOException e) {
            System.err.println("❌ Error membaca data: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ==================== HELPER: DIVISION → JSON ====================
    private static JsonObject divisionToJson(Division div) {
        JsonObject json = new JsonObject();
        json.addProperty("name", div.getName());
        json.addProperty("type", "Division");

        // Array members
        JsonArray membersArray = new JsonArray();
        for (OrgComponent comp : div.getMembers()) {
            if (comp instanceof Division) {
                // Rekursif untuk sub-divisi
                membersArray.add(divisionToJson((Division) comp));
            } else if (comp instanceof Member) {
                membersArray.add(memberToJson((Member) comp));
            }
        }
        json.add("members", membersArray);

        // Array prokers
        JsonArray prokersArray = new JsonArray();
        for (Proker p : div.getProkerList()) {
            prokersArray.add(prokerToJson(p));
        }
        json.add("prokers", prokersArray);

        return json;
    }

    // ==================== HELPER: MEMBER → JSON ====================
    private static JsonObject memberToJson(Member member) {
        JsonObject json = new JsonObject();
        json.addProperty("name", member.getName());
        json.addProperty("role", member.getRole());
        json.addProperty("uangKas", member.getUangKas());
        json.addProperty("perpanjangan", member.getPerpanjangan());

        // Tentukan tipe member
        if (member instanceof StaffMuda) {
            json.addProperty("type", "StaffMuda");
        } else if (member instanceof StaffAhli) {
            json.addProperty("type", "StaffAhli");
        } else if (member instanceof OfficeHolder) {
            json.addProperty("type", "OfficeHolder");
        } else {
            json.addProperty("type", "Member");
        }

        return json;
    }

    // ==================== HELPER: PROKER → JSON ====================
    private static JsonObject prokerToJson(Proker proker) {
        JsonObject json = new JsonObject();
        json.addProperty("namaProker", proker.getNamaProker());
        json.addProperty("deskripsi", proker.getDeskripsiDivisi());
        json.addProperty("ketupel", proker.getKetupel());
        json.addProperty("waketupel", proker.getWaketupel());
        json.addProperty("status", proker.getStatus());
        json.addProperty("progress", proker.getProgress());
        json.addProperty("parentDivisi", proker.getParentDivisi());
        return json;
    }

    // ==================== HELPER: JSON → DIVISION ====================
    private static Division jsonToDivision(JsonObject json) {
        String name = json.get("name").getAsString();
        Division div = new Division(name);

        // Load members
        if (json.has("members")) {
            JsonArray membersArray = json.getAsJsonArray("members");
            for (JsonElement elem : membersArray) {
                JsonObject obj = elem.getAsJsonObject();
                String type = obj.get("type").getAsString();

                if (type.equals("Division")) {
                    // Rekursif untuk sub-divisi
                    div.addMember(jsonToDivision(obj));
                } else {
                    // Member
                    div.addMember(jsonToMember(obj));
                }
            }
        }

        // Load prokers
        if (json.has("prokers")) {
            JsonArray prokersArray = json.getAsJsonArray("prokers");
            for (JsonElement elem : prokersArray) {
                div.addProker(jsonToProker(elem.getAsJsonObject()));
            }
        }

        return div;
    }

    // ==================== HELPER: JSON → MEMBER ====================
    private static Member jsonToMember(JsonObject json) {
        String name = json.get("name").getAsString();
        String type = json.get("type").getAsString();
        int uangKas = json.has("uangKas") ? json.get("uangKas").getAsInt() : 0;
        String perpanjangan = json.has("perpanjangan") ? json.get("perpanjangan").getAsString() : "-";
        String role = json.get("role").getAsString();

        // Buat member sesuai tipe
        Member member;
        String id = "ID-" + System.currentTimeMillis(); // Generate ID baru

        switch (type) {
            case "StaffMuda":
                member = new StaffMuda(name, id);
                break;
            case "StaffAhli":
                member = new StaffAhli(name, id);
                break;
            case "OfficeHolder":
                member = new OfficeHolder(name, id, role);
                break;
            default:
                member = new StaffMuda(name, id); // Fallback
        }

        // Set data tambahan
        member.bayarKas(uangKas);
        member.setPerpanjangan(perpanjangan);

        return member;
    }

    // ==================== HELPER: JSON → PROKER ====================
    private static Proker jsonToProker(JsonObject json) {
        String namaProker = json.get("namaProker").getAsString();
        String deskripsi = json.has("deskripsi") ? json.get("deskripsi").getAsString() : "-";
        String ketupel = json.has("ketupel") ? json.get("ketupel").getAsString() : "-";
        String waketupel = json.has("waketupel") ? json.get("waketupel").getAsString() : "-";
        String status = json.has("status") ? json.get("status").getAsString() : "Rencana";
        int progress = json.has("progress") ? json.get("progress").getAsInt() : 0;
        String parentDivisi = json.has("parentDivisi") ? json.get("parentDivisi").getAsString() : "-";

        return new Proker(namaProker, deskripsi, ketupel, waketupel, status, progress, parentDivisi);
    }
}
