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
('001', 'Golongan Keren', 10000000.00, 500000.00, 2, 1000000.00, 100000.00, 300000.00);

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
('001', 'Samsudin Asep', 'BLM Kecamatan Rt 4 RW 11', 'Laki-laki', 'Bogor', '1989-05-06', 'Menikah', '001');

CREATE TABLE `tb_lembur` (
  `id_lembur` varchar(10) NOT NULL,
  `id_karyawan` varchar(10) DEFAULT NULL,
  `tanggal_lembur` date DEFAULT NULL,
  `jumlah` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `tb_lembur` (`id_lembur`, `id_karyawan`, `tanggal_lembur`, `jumlah`) VALUES
('001', '001', '2026-05-01', 500000);

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

INSERT INTO `tb_penggajian` (`id_gaji`, `tanggal`, `id_karyawan`, `nama_karyawan`, `golongan`, `jumlah_gaji`, `jumlah_lembur`, `potongan`, `tanggal_gaji`, `total_gaji`) VALUES
('001', '2026-05-06', '001', 'Samsudin Asep', 'Golongan Keren', 11900000.00, 500000.00, 1000000.00, '2026-06-01', 11400000.00);

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