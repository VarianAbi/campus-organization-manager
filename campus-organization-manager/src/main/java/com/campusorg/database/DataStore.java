package com.campusorg.database;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.campusorg.models.Proker;
import com.campusorg.patterns.composite.Division;
import com.campusorg.patterns.composite.Member;
import com.campusorg.patterns.composite.OfficeHolder;
import com.campusorg.patterns.composite.OrgComponent;
import com.campusorg.patterns.composite.StaffAhli;
import com.campusorg.patterns.composite.StaffMuda;
import com.campusorg.utils.Constants;
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
    private static final Logger LOGGER = Logger.getLogger(DataStore.class.getName());

    private DataStore() {}

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
                LOGGER.info("Data berhasil disimpan ke: " + DATA_FILE);
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error menyimpan data", e);
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
            LOGGER.info("File data tidak ditemukan. Menggunakan data default.");
            return null;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonObject rootJson = gson.fromJson(reader, JsonObject.class);
            Division rootOrg = jsonToDivision(rootJson);

            LOGGER.info("Data berhasil dimuat dari: " + DATA_FILE);
            return rootOrg;

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error membaca data", e);
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
            switch (comp) {
                case Division division -> membersArray.add(divisionToJson(division));
                case Member member -> membersArray.add(memberToJson(member));
                default -> {
                    // ignore other types
                }
            }
        }
        json.add(Constants.JSON_MEMBERS, membersArray);

        // Array prokers
        JsonArray prokersArray = new JsonArray();
        for (Proker p : div.getProkerList()) {
            prokersArray.add(prokerToJson(p));
        }
        json.add(Constants.JSON_PROKERS, prokersArray);

        return json;
    }

    // ==================== HELPER: MEMBER → JSON ====================
    private static JsonObject memberToJson(Member member) {
        JsonObject json = new JsonObject();
        json.addProperty("name", member.getName());
        json.addProperty("role", member.getRole());
        json.addProperty(Constants.JSON_UANG_KAS, member.getUangKas());
        json.addProperty(Constants.JSON_PERPANJANGAN, member.getPerpanjangan());

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
        json.addProperty(Constants.JSON_DESKRIPSI, proker.getDeskripsiDivisi());
        json.addProperty(Constants.JSON_KETUPEL, proker.getKetupel());
        json.addProperty(Constants.JSON_WAKETUPEL, proker.getWaketupel());
        json.addProperty(Constants.JSON_STATUS, proker.getStatus());
        json.addProperty(Constants.JSON_PROGRESS, proker.getProgress());
        json.addProperty(Constants.JSON_PARENT_DIVISI, proker.getParentDivisi());
        return json;
    }

    // ==================== HELPER: JSON → DIVISION ====================
    private static Division jsonToDivision(JsonObject json) {
        String name = json.get("name").getAsString();
        Division div = new Division(name);

        // Load members
        if (json.has(Constants.JSON_MEMBERS)) {
            JsonArray membersArray = json.getAsJsonArray(Constants.JSON_MEMBERS);
            for (JsonElement elem : membersArray) {
                JsonObject obj = elem.getAsJsonObject();
                String type = obj.get("type").getAsString();

                if ("Division".equals(type)) {
                    // Rekursif untuk sub-divisi
                    div.addMember(jsonToDivision(obj));
                } else {
                    // Member
                    div.addMember(jsonToMember(obj));
                }
            }
        }

        // Load prokers
        if (json.has(Constants.JSON_PROKERS)) {
            JsonArray prokersArray = json.getAsJsonArray(Constants.JSON_PROKERS);
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
        int uangKas = json.has(Constants.JSON_UANG_KAS) ? json.get(Constants.JSON_UANG_KAS).getAsInt() : 0;
        String perpanjangan = json.has(Constants.JSON_PERPANJANGAN) ? json.get(Constants.JSON_PERPANJANGAN).getAsString() : "-";
        String role = json.get("role").getAsString();

        // Buat member sesuai tipe
        String id = "ID-" + System.currentTimeMillis();
        Member member = switch (type) {
            case "StaffMuda" -> new StaffMuda(name, id);
            case "StaffAhli" -> new StaffAhli(name, id);
            case "OfficeHolder" -> new OfficeHolder(name, id, role);
            default -> new StaffMuda(name, id);
        };

        // Set data tambahan
        member.bayarKas(uangKas);
        member.setPerpanjangan(perpanjangan);

        return member;
    }

    // ==================== HELPER: JSON → PROKER ====================
    private static Proker jsonToProker(JsonObject json) {
        String namaProker = json.get("namaProker").getAsString();
        String deskripsi = json.has(Constants.JSON_DESKRIPSI) ? json.get(Constants.JSON_DESKRIPSI).getAsString() : "-";
        String ketupel = json.has(Constants.JSON_KETUPEL) ? json.get(Constants.JSON_KETUPEL).getAsString() : "-";
        String waketupel = json.has(Constants.JSON_WAKETUPEL) ? json.get(Constants.JSON_WAKETUPEL).getAsString() : "-";
        String status = json.has(Constants.JSON_STATUS) ? json.get(Constants.JSON_STATUS).getAsString() : Constants.STATUS_RENCANA;
        int progress = json.has(Constants.JSON_PROGRESS) ? json.get(Constants.JSON_PROGRESS).getAsInt() : 0;
        String parentDivisi = json.has(Constants.JSON_PARENT_DIVISI) ? json.get(Constants.JSON_PARENT_DIVISI).getAsString() : "-";

        return new Proker(namaProker, deskripsi, ketupel, waketupel, status, progress, parentDivisi);
    }
}
