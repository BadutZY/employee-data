-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 26 Bulan Mei 2026 pada 02.09
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_penggajian`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_golongan`
--

CREATE TABLE `tb_golongan` (
  `id_golongan` varchar(10) NOT NULL,
  `nama_golongan` varchar(50) DEFAULT NULL,
  `gaji_pokok` decimal(15,2) DEFAULT NULL,
  `tunjangan_istri` decimal(15,2) DEFAULT 0.00,
  `jumlah_anak` int(11) DEFAULT 0,
  `tunjangan_anak` decimal(15,2) DEFAULT 0.00,
  `transport` decimal(15,2) DEFAULT NULL,
  `uang_makan` decimal(15,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_golongan`
--

INSERT INTO `tb_golongan` (`id_golongan`, `nama_golongan`, `gaji_pokok`, `tunjangan_istri`, `jumlah_anak`, `tunjangan_anak`, `transport`, `uang_makan`) VALUES
('GOL001', 'Karyawan', 3000000.00, 0.00, 0, 0.00, 100000.00, 250000.00),
('GOL002', 'Admin', 5000000.00, 0.00, 0, 0.00, 100000.00, 250000.00),
('GOL003', 'Developer', 10000000.00, 0.00, 0, 0.00, 100000.00, 250000.00),
('GOL004', 'Manager', 15000000.00, 0.00, 0, 0.00, 100000.00, 250000.00);

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_karyawan`
--

CREATE TABLE `tb_karyawan` (
  `id_karyawan` varchar(10) NOT NULL,
  `nama` varchar(100) DEFAULT NULL,
  `alamat` text DEFAULT NULL,
  `jenis_kelamin` varchar(10) DEFAULT NULL,
  `tempat_lahir` varchar(50) DEFAULT NULL,
  `tanggal_lahir` date DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `id_golongan` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_karyawan`
--

INSERT INTO `tb_karyawan` (`id_karyawan`, `nama`, `alamat`, `jenis_kelamin`, `tempat_lahir`, `tanggal_lahir`, `status`, `id_golongan`) VALUES
('001', 'Rizky', 'Menteng Asri', 'Laki-laki', 'Bogor', '2009-03-06', 'Menikah', 'GOL003'),
('002', 'Kimmy', 'JL Farmasi', 'Perempuan', 'Bogor', '2010-03-08', 'Menikah', 'GOL001'),
('003', 'Mario', 'GG Kelor', 'Laki-laki', 'Bogor', '2008-03-21', 'Tidak Menikah', 'GOL002'),
('004', 'Supardi Sanjaya', 'JL Sudirman', 'Laki-laki', 'Jakarta', '1978-10-02', 'Cerai', 'GOL004'),
('005', 'Hanni', 'South Korea', 'Perempuan', 'Vietnam', '1995-06-15', 'Tidak Menikah', 'GOL001');

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_lembur`
--

CREATE TABLE `tb_lembur` (
  `id_lembur` varchar(10) NOT NULL,
  `id_karyawan` varchar(10) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `jumlah_jam` int(11) DEFAULT NULL,
  `upah_per_jam` decimal(15,2) DEFAULT NULL,
  `total_lembur` decimal(15,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_lembur`
--

INSERT INTO `tb_lembur` (`id_lembur`, `id_karyawan`, `tanggal_mulai`, `tanggal_selesai`, `jumlah_jam`, `upah_per_jam`, `total_lembur`) VALUES
('LMBR003', '005', '2026-05-27', '2026-05-28', 3, 100000.00, 300000.00),
('LMBR01', '001', '2026-05-17', '2026-05-18', 2, 100000.00, 200000.00),
('LMBR02', '004', '2026-05-25', '2026-05-30', 6, 100000.00, 600000.00);

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_penggajian`
--

CREATE TABLE `tb_penggajian` (
  `id_gaji` varchar(10) NOT NULL,
  `tanggal` date DEFAULT NULL,
  `id_karyawan` varchar(10) DEFAULT NULL,
  `nama_karyawan` varchar(100) DEFAULT NULL,
  `golongan` varchar(50) DEFAULT NULL,
  `gaji_pokok` decimal(15,2) DEFAULT NULL,
  `tunjangan_istri` decimal(15,2) DEFAULT 0.00,
  `jumlah_anak` int(11) DEFAULT 0,
  `tunjangan_anak` decimal(15,2) DEFAULT 0.00,
  `total_tunjangan_anak` decimal(15,2) DEFAULT 0.00,
  `transport` decimal(15,2) DEFAULT NULL,
  `uang_makan` decimal(15,2) DEFAULT NULL,
  `jumlah_gaji` decimal(15,2) DEFAULT NULL,
  `jumlah_lembur` decimal(15,2) DEFAULT 0.00,
  `potongan` decimal(15,2) DEFAULT 0.00,
  `tanggal_gaji` date DEFAULT NULL,
  `total_gaji` decimal(15,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_penggajian`
--

INSERT INTO `tb_penggajian` (`id_gaji`, `tanggal`, `id_karyawan`, `nama_karyawan`, `golongan`, `gaji_pokok`, `tunjangan_istri`, `jumlah_anak`, `tunjangan_anak`, `total_tunjangan_anak`, `transport`, `uang_makan`, `jumlah_gaji`, `jumlah_lembur`, `potongan`, `tanggal_gaji`, `total_gaji`) VALUES
('GJI001', '2026-05-26', '001', 'Rizky', 'Developer', 10000000.00, 100000.00, 2, 250000.00, 500000.00, 100000.00, 250000.00, 10950000.00, 200000.00, 0.00, '2026-06-01', 11150000.00),
('GJI002', '2026-05-17', '002', 'Kimmy', 'Karyawan', 3000000.00, 100000.00, 2, 50000.00, 100000.00, 100000.00, 250000.00, 3550000.00, 0.00, 0.00, '2026-06-01', 3550000.00),
('GJI003', '2026-05-26', '003', 'Mario', 'Admin', 5000000.00, 0.00, 0, 0.00, 0.00, 100000.00, 250000.00, 5350000.00, 0.00, 0.00, '2026-06-01', 5350000.00),
('GJI004', '2026-05-26', '004', 'Supardi Sanjaya', 'Manager', 15000000.00, 0.00, 0, 0.00, 0.00, 100000.00, 250000.00, 15350000.00, 600000.00, 500000.00, '2026-06-01', 15450000.00),
('GJI005', '2026-05-26', '005', 'Hanni', 'Karyawan', 3000000.00, 0.00, 0, 0.00, 0.00, 100000.00, 250000.00, 3350000.00, 300000.00, 0.00, '2026-06-01', 3650000.00);

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_user`
--

CREATE TABLE `tb_user` (
  `username` varchar(50) NOT NULL,
  `password` varchar(100) DEFAULT NULL,
  `nama_lengkap` varchar(100) DEFAULT NULL,
  `role` varchar(20) DEFAULT 'user',
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_user`
--

INSERT INTO `tb_user` (`username`, `password`, `nama_lengkap`, `role`, `created_at`) VALUES
('admin', 'admin', 'Administrator', 'admin', '2026-05-25 22:17:08'),
('BadutZY', 'Admin123', NULL, 'user', '2026-05-26 05:26:23');

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `tb_golongan`
--
ALTER TABLE `tb_golongan`
  ADD PRIMARY KEY (`id_golongan`);

--
-- Indeks untuk tabel `tb_karyawan`
--
ALTER TABLE `tb_karyawan`
  ADD PRIMARY KEY (`id_karyawan`),
  ADD KEY `id_golongan` (`id_golongan`);

--
-- Indeks untuk tabel `tb_lembur`
--
ALTER TABLE `tb_lembur`
  ADD PRIMARY KEY (`id_lembur`),
  ADD KEY `id_karyawan` (`id_karyawan`);

--
-- Indeks untuk tabel `tb_penggajian`
--
ALTER TABLE `tb_penggajian`
  ADD PRIMARY KEY (`id_gaji`),
  ADD KEY `id_karyawan` (`id_karyawan`);

--
-- Indeks untuk tabel `tb_user`
--
ALTER TABLE `tb_user`
  ADD PRIMARY KEY (`username`);

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `tb_karyawan`
--
ALTER TABLE `tb_karyawan`
  ADD CONSTRAINT `tb_karyawan_ibfk_1` FOREIGN KEY (`id_golongan`) REFERENCES `tb_golongan` (`id_golongan`);

--
-- Ketidakleluasaan untuk tabel `tb_lembur`
--
ALTER TABLE `tb_lembur`
  ADD CONSTRAINT `tb_lembur_ibfk_1` FOREIGN KEY (`id_karyawan`) REFERENCES `tb_karyawan` (`id_karyawan`);

--
-- Ketidakleluasaan untuk tabel `tb_penggajian`
--
ALTER TABLE `tb_penggajian`
  ADD CONSTRAINT `tb_penggajian_ibfk_1` FOREIGN KEY (`id_karyawan`) REFERENCES `tb_karyawan` (`id_karyawan`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
