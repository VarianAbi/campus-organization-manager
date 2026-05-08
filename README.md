# Campus Organization Manager (HIMAKOM App)

Aplikasi desktop untuk mengelola organisasi kampus (HIMAKOM) berbasis Java Swing. Fokus utama aplikasi ini adalah manajemen struktur organisasi, data anggota, keuangan kas, serta program kerja (proker) per divisi. Data disimpan secara lokal dalam format JSON dan akan otomatis tersimpan saat aplikasi ditutup.

## Fitur Utama

- Dashboard struktur organisasi dan daftar divisi
- Manajemen anggota: tambah, edit, hapus, dan filter per divisi
- Pencatatan kas anggota dan perpanjangan (delegasi biro)
- Manajemen proker: tambah, edit, hapus, dan update status/progress
- Detail proker per divisi dengan tampilan list dan detail
- Persistensi data ke file JSON lokal

## Tech Stack

- Java 24
- Java Swing (GUI)
- Maven
- Gson (JSON)
- JUnit 4 (testing)

## Pola Desain yang Digunakan

- Composite: struktur organisasi (divisi dan anggota)
- Factory: pembuatan objek anggota
- Singleton: manajer organisasi
- Observer: notifikasi ke anggota

## Struktur Data

Data organisasi disimpan di:

- campus-organization-manager/data/organization.json

File ini akan dibuat otomatis saat aplikasi menutup window.

## Cara Menjalankan

Prasyarat:

- JDK 24
- Maven

Langkah:

```bash
cd campus-organization-manager
mvn clean compile
mvn exec:java
```

## Menjalankan Test

```bash
cd campus-organization-manager
mvn test
```

## Catatan

- Ikon gambar berada di campus-organization-manager/src/main/resources/images
- Data default akan dipakai jika file JSON belum ada
