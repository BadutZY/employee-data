-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 06 Bulan Mei 2026 pada 01.25
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
  `tunjangan_istri` decimal(15,2) DEFAULT NULL,
  `jumlah_anak` int(11) DEFAULT NULL,
  `tunjangan_anak` decimal(15,2) DEFAULT NULL,
  `transport` decimal(15,2) DEFAULT NULL,
  `uang_makan` decimal(15,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_golongan`
--

INSERT INTO `tb_golongan` (`id_golongan`, `nama_golongan`, `gaji_pokok`, `tunjangan_istri`, `jumlah_anak`, `tunjangan_anak`, `transport`, `uang_makan`) VALUES
('001', 'Golongan Keren', 10000000.00, 500000.00, 2, 1000000.00, 100000.00, 300000.00);

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
('001', 'Samsudin Asep', 'BLM Kecamatan Rt 4 RW 11', 'Laki-laki', 'Bogor', '1989-05-06', 'Menikah', '001');

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_lembur`
--

CREATE TABLE `tb_lembur` (
  `id_lembur` varchar(10) NOT NULL,
  `id_karyawan` varchar(10) DEFAULT NULL,
  `tanggal_lembur` date DEFAULT NULL,
  `jumlah` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_lembur`
--

INSERT INTO `tb_lembur` (`id_lembur`, `id_karyawan`, `tanggal_lembur`, `jumlah`) VALUES
('001', '001', '2026-05-01', 500000);

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
  `jumlah_gaji` decimal(15,2) DEFAULT NULL,
  `jumlah_lembur` decimal(15,2) DEFAULT NULL,
  `potongan` decimal(15,2) DEFAULT NULL,
  `tanggal_gaji` date DEFAULT NULL,
  `total_gaji` decimal(15,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_penggajian`
--

INSERT INTO `tb_penggajian` (`id_gaji`, `tanggal`, `id_karyawan`, `nama_karyawan`, `golongan`, `jumlah_gaji`, `jumlah_lembur`, `potongan`, `tanggal_gaji`, `total_gaji`) VALUES
('001', '2026-05-06', '001', 'Samsudin Asep', 'Golongan Keren', 11900000.00, 500000.00, 1000000.00, '2026-06-01', 11400000.00);

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_user`
--

CREATE TABLE `tb_user` (
  `username` varchar(50) NOT NULL,
  `password` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_user`
--

INSERT INTO `tb_user` (`username`, `password`) VALUES
('admin', 'admin');

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
