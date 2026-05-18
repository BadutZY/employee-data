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

INSERT INTO `tb_golongan` (`id_golongan`, `nama_golongan`, `gaji_pokok`, `tunjangan_istri`, `jumlah_anak`, `tunjangan_anak`, `transport`, `uang_makan`) VALUES
('GOL001', 'Karyawan', 3000000.00, 0.00, 0, 0.00, 100000.00, 250000.00),
('GOL002', 'Admin', 5000000.00, 0.00, 0, 0.00, 100000.00, 250000.00);

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

INSERT INTO `tb_karyawan` (`id_karyawan`, `nama`, `alamat`, `jenis_kelamin`, `tempat_lahir`, `tanggal_lahir`, `status`, `id_golongan`) VALUES
('001', 'Rizky', 'Menteng Asri', 'Laki-laki', 'Bogor', '2009-03-06', 'Tidak Menikah', 'GOL002'),
('002', 'Suhendra', 'JL Farmasi', 'Laki-laki', 'Bogor', '1971-10-02', 'Menikah', 'GOL001');

CREATE TABLE `tb_lembur` (
  `id_lembur` varchar(10) NOT NULL,
  `id_karyawan` varchar(10) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `jumlah_jam` int(11) DEFAULT NULL,
  `upah_per_jam` decimal(15,2) DEFAULT NULL,
  `total_lembur` decimal(15,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `tb_lembur` (`id_lembur`, `id_karyawan`, `tanggal_mulai`, `tanggal_selesai`, `jumlah_jam`, `upah_per_jam`, `total_lembur`) VALUES
('LMBR01', '001', '2026-05-17', '2026-05-18', 2, 100000.00, 200000.00);

CREATE TABLE `tb_penggajian` (
  `id_gaji` varchar(10) NOT NULL,
  `tanggal` date DEFAULT NULL,
  `id_karyawan` varchar(10) DEFAULT NULL,
  `nama_karyawan` varchar(100) DEFAULT NULL,
  `golongan` varchar(50) DEFAULT NULL,
  `gaji_pokok` decimal(15,2) DEFAULT NULL,
  `tunjangan_istri` decimal(15,2) DEFAULT NULL,
  `jumlah_anak` int(11) DEFAULT NULL,
  `tunjangan_anak` decimal(15,2) DEFAULT NULL,
  `total_tunjangan_anak` decimal(15,2) DEFAULT NULL,
  `transport` decimal(15,2) DEFAULT NULL,
  `uang_makan` decimal(15,2) DEFAULT NULL,
  `jumlah_gaji` decimal(15,2) DEFAULT NULL,
  `jumlah_lembur` decimal(15,2) DEFAULT NULL,
  `potongan` decimal(15,2) DEFAULT NULL,
  `tanggal_gaji` date DEFAULT NULL,
  `total_gaji` decimal(15,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `tb_penggajian` (`id_gaji`, `tanggal`, `id_karyawan`, `nama_karyawan`, `golongan`, `gaji_pokok`, `tunjangan_istri`, `jumlah_anak`, `tunjangan_anak`, `total_tunjangan_anak`, `transport`, `uang_makan`, `jumlah_gaji`, `jumlah_lembur`, `potongan`, `tanggal_gaji`, `total_gaji`) VALUES
('GJI001', '2026-05-17', '001', 'Rizky', 'Admin', 5000000.00, 0.00, 0, 0.00, 0.00, 100000.00, 250000.00, 5350000.00, 200000.00, 0.00, '2026-06-01', 5550000.00),
('GJI002', '2026-05-17', '002', 'Suhendra', 'Karyawan', 3000000.00, 100000.00, 2, 50000.00, 100000.00, 100000.00, 250000.00, 3550000.00, 0.00, 0.00, '2026-06-01', 3550000.00);

CREATE TABLE `tb_user` (
  `username` varchar(50) NOT NULL,
  `password` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `tb_user` (`username`, `password`) VALUES
('admin', 'admin');

ALTER TABLE `tb_golongan`
  ADD PRIMARY KEY (`id_golongan`);

ALTER TABLE `tb_karyawan`
  ADD PRIMARY KEY (`id_karyawan`),
  ADD KEY `id_golongan` (`id_golongan`);

ALTER TABLE `tb_lembur`
  ADD PRIMARY KEY (`id_lembur`),
  ADD KEY `id_karyawan` (`id_karyawan`);

ALTER TABLE `tb_penggajian`
  ADD PRIMARY KEY (`id_gaji`),
  ADD KEY `id_karyawan` (`id_karyawan`);

ALTER TABLE `tb_user`
  ADD PRIMARY KEY (`username`);

ALTER TABLE `tb_karyawan`
  ADD CONSTRAINT `tb_karyawan_ibfk_1` FOREIGN KEY (`id_golongan`) REFERENCES `tb_golongan` (`id_golongan`);

ALTER TABLE `tb_lembur`
  ADD CONSTRAINT `tb_lembur_ibfk_1` FOREIGN KEY (`id_karyawan`) REFERENCES `tb_karyawan` (`id_karyawan`);

ALTER TABLE `tb_penggajian`
  ADD CONSTRAINT `tb_penggajian_ibfk_1` FOREIGN KEY (`id_karyawan`) REFERENCES `tb_karyawan` (`id_karyawan`);
COMMIT;