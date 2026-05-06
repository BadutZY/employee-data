# Aplikasi Data Karyawan - Sistem Penggajian

Aplikasi manajemen data karyawan dan penggajian berbasis Java Desktop menggunakan NetBeans dan database MySQL (XAMPP).

---

## Kebutuhan Sistem

Pastikan perangkat lunak berikut sudah terinstal sebelum menjalankan project ini.

- **Java JDK**
  - Download: https://www.oracle.com/java/technologies/downloads/
- **Apache NetBeans IDE**
  - Download: https://netbeans.apache.org/download/
- **XAMPP**
  - Download: https://www.apachefriends.org/download.html
- **MySQL Connector/J** dan **JCalendar** (sudah tersedia di folder `LIBRARIES` dalam repository ini)

---

## Cara Clone Repository

1. Buka terminal atau command prompt.
2. Jalankan perintah berikut untuk meng-clone repository.

```
git clone https://github.com/username/nama-repository.git
```

3. Buka NetBeans, pilih menu **File** lalu pilih **Open Project**.
4. Arahkan ke folder hasil clone tadi, kemudian klik **Open Project**.

---

## Setup Database

1. Buka **XAMPP Control Panel**.
2. Klik tombol **Start** pada baris **Apache** dan **MySQL**, pastikan keduanya berstatus Running.
3. Buka browser, ketik alamat berikut di address bar.

```
http://localhost/phpmyadmin
```

4. Setelah phpMyAdmin terbuka, klik tab **SQL** yang ada di bagian atas halaman.
5. Buka file SQL yang tersedia di dalam repository ini pada path berikut.

```
database/db_penggajian.sql
```

6. Salin seluruh isi file tersebut, tempelkan ke dalam kotak SQL di phpMyAdmin, kemudian klik tombol **Go**.
7. Jika berhasil, database beserta tabelnya akan muncul di panel sebelah kiri phpMyAdmin.

---

## Setup Libraries

Semua library yang dibutuhkan sudah disertakan di dalam repository ini pada folder **LIBRARIES**, sehingga tidak perlu mengunduh dari internet.

1. Di panel **Projects** pada NetBeans, cari bagian **Libraries**.
2. Klik kanan pada **Libraries**, kemudian pilih **Add JAR/Folder**.
3. Pada jendela yang muncul, arahkan ke folder **LIBRARIES** yang ada di dalam folder project ini.
4. Pilih file-file berikut satu per satu:
   - `mysql-connector-j-9.7.0.jar`
   - `jcalendar-1.4.jar`
5. Klik **Open**.
6. Pastikan kedua file `.jar` tersebut sudah muncul di dalam daftar **Libraries** pada panel Projects.

---

## Konfigurasi Koneksi Database

File konfigurasi koneksi database berada di.

```
src/Koneksi/Koneksi.java
```

Buka file tersebut, kemudian sesuaikan bagian berikut dengan pengaturan database Anda.

```java
private static final String URL      = "jdbc:mysql://localhost:3306/db_penggajian";
private static final String USER     = "root";
private static final String PASSWORD = "";
```

Penjelasan masing-masing bagian.

- **URL** : Alamat koneksi ke database. Format penulisannya adalah `jdbc:mysql://HOST:PORT/NAMA_DATABASE`.
  - `localhost` adalah alamat server, tidak perlu diubah jika menjalankan di komputer sendiri.
  - `3306` adalah port default MySQL/MariaDB, tidak perlu diubah jika tidak ada perubahan konfigurasi XAMPP.
  - `db_penggajian` adalah nama database. Jika Anda membuat database dengan nama yang berbeda, ubah bagian ini sesuai nama database yang telah dibuat di phpMyAdmin.
- **USER** : Username database. Nilai defaultnya adalah `root`, tidak perlu diubah untuk penggunaan XAMPP standar.
- **PASSWORD** : Password database. Biarkan kosong jika tidak mengatur password saat instalasi XAMPP. Jika ada password, isi di antara tanda kutip.

Contoh jika nama database Anda berbeda, misalnya **penggajian_saya**.

```java
private static final String URL = "jdbc:mysql://localhost:3306/penggajian_saya";
```

---

## Menjalankan Aplikasi

1. Pastikan **XAMPP** sudah berjalan dan MySQL sudah dalam status **Running**.
2. Pastikan database sudah dibuat sesuai langkah pada bagian Setup Database.
3. Pastikan semua library sudah ditambahkan ke Libraries sesuai langkah sebelumnya.
4. Buka file `src/form/FormLogin.java` di NetBeans.
5. Klik kanan pada file tersebut, kemudian pilih **Run File**.
6. Jika koneksi berhasil, aplikasi akan terbuka dan menampilkan form login.

> **Login default:** Username `admin` | Password `admin`

---

## Struktur Project

```
DataKaryawan/
├── src/
│   ├── form/
│   │   ├── FormLogin.java           # Form login aplikasi
│   │   ├── FormPilihan.java         # Form menu utama / pilihan
│   │   ├── FormKaryawan.java        # Form data karyawan
│   │   ├── FormGolongan.java        # Form data golongan
│   │   ├── FormLembur.java          # Form data lembur
│   │   └── FormPenggajian.java      # Form data penggajian
│   ├── Koneksi/
│   │   └── Koneksi.java             # Konfigurasi koneksi database
│   └── foto/
│       └── icon.png                 # Icon aplikasi
├── LIBRARIES/
│   ├── mysql-connector-j-9.7.0.jar  # Library MySQL Connector
│   └── jcalendar-1.4.jar            # Library JCalendar (date picker)
├── database/
│   └── db_penggajian.sql            # Script pembuatan database dan tabel
└── README.md                        # Dokumentasi project
```

---

## Catatan Tambahan

- Jika muncul pesan error **"Driver JDBC tidak ditemukan"**, pastikan file `mysql-connector-j-9.7.0.jar` pada folder LIBRARIES sudah ditambahkan ke Libraries dengan benar.
- Jika muncul pesan error **"Gagal koneksi ke database"**, pastikan MySQL pada XAMPP sudah dalam keadaan Running dan nama database pada `Koneksi.java` sudah sesuai.
- Jika muncul pesan error **"Unknown database"**, pastikan database sudah dibuat terlebih dahulu melalui phpMyAdmin menggunakan file `database/db_penggajian.sql` yang tersedia di repository.
- Jika komponen date picker tidak tampil, pastikan file `jcalendar-1.4.jar` sudah ditambahkan ke Libraries dengan benar.