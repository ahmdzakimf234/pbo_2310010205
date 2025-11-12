/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package form;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import pbo_2310010205.Koneksi;


public class FrmPemesanan extends javax.swing.JFrame {
    
    private DefaultTableModel model;
    // Menyimpan harga paket berdasarkan id_paket
    private Map<Integer, Double> mapHargaPaket = new HashMap<>();

    public FrmPemesanan() {
     initComponents();
        setLocationRelativeTo(null);
        setTableModel();
        loadPelanggan();
        loadPaketWisata();
        loadData();
        isiDefaultTanggal();
    }

    // ================== SETUP TABLE ==================
    private void setTableModel() {
        model = new DefaultTableModel(
            new String[]{
                "ID Pemesanan",
                "ID Pelanggan",
                "Nama Pelanggan",
                "ID Paket",
                "Nama Paket",
                "Tgl Pesan",
                "Tgl Berangkat",
                "Jumlah Peserta",
                "Total Bayar",
                "Status"
            }, 0
        );
        tblPemesanan.setModel(model);
        tblPemesanan.setRowHeight(22);
    }

    // ================== LOAD COMBO PELANGGAN ==================
    private void loadPelanggan() {
        cmbPelanggan.removeAllItems();
        cmbPelanggan.addItem("-- Pilih Pelanggan --");
        String sql = "SELECT id_pelanggan, nama_pelanggan FROM pelanggan ORDER BY nama_pelanggan";
        try (Connection c = Koneksi.getKoneksi();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id_pelanggan");
                String nama = rs.getString("nama_pelanggan");
                cmbPelanggan.addItem(id + " - " + nama);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal load pelanggan: " + e.getMessage());
        }
    }

    // ================== LOAD COMBO PAKET WISATA ==================
    private void loadPaketWisata() {
        cmbPaket.removeAllItems();
        cmbPaket.addItem("-- Pilih Paket --");
        mapHargaPaket.clear();

        String sql = "SELECT id_paket, nama_paket, harga_paket FROM paket_wisata ORDER BY nama_paket";
        try (Connection c = Koneksi.getKoneksi();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id_paket");
                String nama = rs.getString("nama_paket");
                double harga = rs.getDouble("harga_paket");

                cmbPaket.addItem(id + " - " + nama);
                mapHargaPaket.put(id, harga);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal load paket: " + e.getMessage());
        }
    }

    // ================== LOAD DATA TABEL ==================
    private void loadData() {
        model.setRowCount(0);
        String sql = "SELECT p.id_pemesanan, pel.id_pelanggan, pel.nama_pelanggan, " +
                     "pk.id_paket, pk.nama_paket, p.tanggal_pesan, p.tanggal_berangkat, " +
                     "p.jumlah_peserta, p.total_bayar, p.status_pesanan " +
                     "FROM pemesanan p " +
                     "JOIN pelanggan pel ON p.id_pelanggan = pel.id_pelanggan " +
                     "JOIN paket_wisata pk ON p.id_paket = pk.id_paket " +
                     "ORDER BY p.id_pemesanan DESC";

        try (Connection c = Koneksi.getKoneksi();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_pemesanan"),
                    rs.getInt("id_pelanggan"),
                    rs.getString("nama_pelanggan"),
                    rs.getInt("id_paket"),
                    rs.getString("nama_paket"),
                    rs.getDate("tanggal_pesan").toString(),
                    rs.getDate("tanggal_berangkat").toString(),
                    rs.getInt("jumlah_peserta"),
                    rs.getDouble("total_bayar"),
                    rs.getString("status_pesanan")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + e.getMessage());
        }
    }

    // ================== UTIL: AMBIL ID DARI COMBO ==================
    private int getIdDariCombo(javax.swing.JComboBox<String> combo) {
        int idx = combo.getSelectedIndex();
        if (idx <= 0) return 0; // index 0 = "-- Pilih --"
        String item = (String) combo.getSelectedItem();
        if (item == null || !item.contains(" - ")) return 0;
        try {
            return Integer.parseInt(item.split(" - ")[0]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void isiDefaultTanggal() {
        // boleh dikosongkan, atau isi tanggal hari ini manual sebelum simpan
        // misal: txtTanggalPesan.setText("2025-11-11");
    }

    // ================== HITUNG TOTAL ==================
    private void hitungTotal() {
        int idPaket = getIdDariCombo(cmbPaket);
        if (idPaket == 0) {
            txtTotalBayar.setText("");
            return;
        }

        double harga = mapHargaPaket.getOrDefault(idPaket, 0.0);
        txtHargaPaket.setText(String.valueOf(harga));

        String jmlStr = txtJumlahPeserta.getText().trim();
        if (jmlStr.isEmpty()) {
            txtTotalBayar.setText("");
            return;
        }

        try {
            int jml = Integer.parseInt(jmlStr);
            double total = harga * jml;
            txtTotalBayar.setText(String.valueOf(total));
        } catch (NumberFormatException e) {
            txtTotalBayar.setText("");
        }
    }

    // ================== RESET FORM ==================
    private void resetForm() {
        txtIdPemesanan.setText("");
        cmbPelanggan.setSelectedIndex(0);
        cmbPaket.setSelectedIndex(0);
        txtTanggalPesan.setText("");
        txtTanggalBerangkat.setText("");
        txtJumlahPeserta.setText("");
        txtHargaPaket.setText("");
        txtTotalBayar.setText("");
        cmbStatus.setSelectedIndex(0);
        tblPemesanan.clearSelection();
    }

    // ================== SIMPAN ==================
    private void simpanData() {
        int idPelanggan = getIdDariCombo(cmbPelanggan);
        int idPaket = getIdDariCombo(cmbPaket);
        String tglPesan = txtTanggalPesan.getText().trim();
        String tglBerangkat = txtTanggalBerangkat.getText().trim();
        String jmlStr = txtJumlahPeserta.getText().trim();
        String totalStr = txtTotalBayar.getText().trim();
        String status = (String) cmbStatus.getSelectedItem();

        if (idPelanggan == 0 || idPaket == 0 || tglPesan.isEmpty() || tglBerangkat.isEmpty()
                || jmlStr.isEmpty() || totalStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lengkapi semua data pemesanan");
            return;
        }

        String sql = "INSERT INTO pemesanan " +
                     "(id_pelanggan, id_paket, tanggal_pesan, tanggal_berangkat, " +
                     "jumlah_peserta, total_bayar, status_pesanan) " +
                     "VALUES (?,?,?,?,?,?,?)";
        try (Connection c = Koneksi.getKoneksi();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idPelanggan);
            ps.setInt(2, idPaket);
            ps.setString(3, tglPesan);
            ps.setString(4, tglBerangkat);
            ps.setInt(5, Integer.parseInt(jmlStr));
            ps.setDouble(6, Double.parseDouble(totalStr));
            ps.setString(7, status);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data pemesanan berhasil disimpan");
            loadData();
            resetForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan: " + e.getMessage());
        }
    }

    // ================== UBAH ==================
    private void ubahData() {
        String idStr = txtIdPemesanan.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data di tabel terlebih dahulu");
            return;
        }

        int idPemesanan = Integer.parseInt(idStr);
        int idPelanggan = getIdDariCombo(cmbPelanggan);
        int idPaket = getIdDariCombo(cmbPaket);
        String tglPesan = txtTanggalPesan.getText().trim();
        String tglBerangkat = txtTanggalBerangkat.getText().trim();
        String jmlStr = txtJumlahPeserta.getText().trim();
        String totalStr = txtTotalBayar.getText().trim();
        String status = (String) cmbStatus.getSelectedItem();

        if (idPelanggan == 0 || idPaket == 0 || tglPesan.isEmpty() || tglBerangkat.isEmpty()
                || jmlStr.isEmpty() || totalStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lengkapi semua data sebelum mengubah");
            return;
        }

        String sql = "UPDATE pemesanan SET " +
                     "id_pelanggan=?, id_paket=?, tanggal_pesan=?, tanggal_berangkat=?, " +
                     "jumlah_peserta=?, total_bayar=?, status_pesanan=? " +
                     "WHERE id_pemesanan=?";

        try (Connection c = Koneksi.getKoneksi();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idPelanggan);
            ps.setInt(2, idPaket);
            ps.setString(3, tglPesan);
            ps.setString(4, tglBerangkat);
            ps.setInt(5, Integer.parseInt(jmlStr));
            ps.setDouble(6, Double.parseDouble(totalStr));
            ps.setString(7, status);
            ps.setInt(8, idPemesanan);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data pemesanan berhasil diubah");
            loadData();
            resetForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal ubah: " + e.getMessage());
        }
    }

    // ================== HAPUS ==================
    private void hapusData() {
        String idStr = txtIdPemesanan.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data di tabel terlebih dahulu");
            return;
        }

        int konfirm = JOptionPane.showConfirmDialog(this,
                "Yakin hapus data ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);

        if (konfirm != JOptionPane.YES_OPTION) return;

        int idPemesanan = Integer.parseInt(idStr);
        String sql = "DELETE FROM pemesanan WHERE id_pemesanan=?";

        try (Connection c = Koneksi.getKoneksi();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idPemesanan);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data pemesanan berhasil dihapus");
            loadData();
            resetForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal hapus: " + e.getMessage());
        }
    }

    // ================== HELPER METHOD ==================


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnSimpan = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnHapus = new javax.swing.JButton();
        txtIdPemesanan = new javax.swing.JTextField();
        btnUbah = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtTanggalPesan = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtTanggalBerangkat = new javax.swing.JTextField();
        txtJumlahPeserta = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPemesanan = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        txtHargaPaket = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtTotalBayar = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        cmbPelanggan = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        cmbPaket = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        jLabel1.setText("Id Pemesanan");

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnUbah.setText("Ubah");
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
            }
        });

        jLabel2.setText("Tgl Pemesanan");

        jLabel3.setText("Tgl Berangkat");

        txtJumlahPeserta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtJumlahPesertaKeyReleased(evt);
            }
        });

        tblPemesanan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblPemesanan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPemesananMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPemesanan);

        jLabel4.setText("Jumlah");

        jLabel5.setText("Harga");

        txtTotalBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalBayarActionPerformed(evt);
            }
        });

        jLabel6.setText("Total");

        jLabel7.setText("Status");

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Belum", "Lunas" }));

        cmbPelanggan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Id", "Nama", " " }));

        jLabel8.setText("Pelanggan");

        cmbPaket.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Id", "Nama", " " }));
        cmbPaket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPaketActionPerformed(evt);
            }
        });

        jLabel9.setText("Paket");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIdPemesanan, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtHargaPaket, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTanggalPesan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTanggalBerangkat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtJumlahPeserta, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTotalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSimpan)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnUbah)
                                .addGap(18, 18, 18)
                                .addComponent(btnHapus))
                            .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbPelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbPaket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 91, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 552, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtIdPemesanan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(cmbPelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(cmbPaket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtTanggalPesan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtTanggalBerangkat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtJumlahPeserta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtHargaPaket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtTotalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(66, 66, 66)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSimpan)
                            .addComponent(btnUbah)
                            .addComponent(btnHapus)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        simpanData();// TODO add your handling code here:
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahActionPerformed
        ubahData();//TODO add your handling code here:
    }//GEN-LAST:event_btnUbahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        hapusData();// TODO add your handling code here:
    }//GEN-LAST:event_btnHapusActionPerformed

    private void tblPemesananMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPemesananMouseClicked
       int row = tblPemesanan.getSelectedRow();
        if (row == -1) return;

        txtIdPemesanan.setText(model.getValueAt(row, 0).toString());

        // Set combo pelanggan
        String idPel = model.getValueAt(row, 1).toString();
        String namaPel = model.getValueAt(row, 2).toString();
        String itemPel = idPel + " - " + namaPel;
        cmbPelanggan.setSelectedItem(itemPel);

        // Set combo paket
        String idPak = model.getValueAt(row, 3).toString();
        String namaPak = model.getValueAt(row, 4).toString();
        String itemPak = idPak + " - " + namaPak;
        cmbPaket.setSelectedItem(itemPak);

        txtTanggalPesan.setText(model.getValueAt(row, 5).toString());
        txtTanggalBerangkat.setText(model.getValueAt(row, 6).toString());
        txtJumlahPeserta.setText(model.getValueAt(row, 7).toString());
        txtTotalBayar.setText(model.getValueAt(row, 8).toString());
        cmbStatus.setSelectedItem(model.getValueAt(row, 9).toString());

        // update harga paket berdasar paket terpilih
        hitungTotal();
       
    }//GEN-LAST:event_tblPemesananMouseClicked

    private void txtTotalBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalBayarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalBayarActionPerformed

    private void cmbPaketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPaketActionPerformed
        hitungTotal();// TODO add your handling code here:
    }//GEN-LAST:event_cmbPaketActionPerformed

    private void txtJumlahPesertaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtJumlahPesertaKeyReleased
        hitungTotal();// TODO add your handling code here:
    }//GEN-LAST:event_txtJumlahPesertaKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
      } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
    java.util.logging.Logger.getAnonymousLogger()
        .log(java.util.logging.Level.SEVERE, null, ex);
}     
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FrmPemesanan().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnUbah;
    private javax.swing.JComboBox<String> cmbPaket;
    private javax.swing.JComboBox<String> cmbPelanggan;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblPemesanan;
    private javax.swing.JTextField txtHargaPaket;
    private javax.swing.JTextField txtIdPemesanan;
    private javax.swing.JTextField txtJumlahPeserta;
    private javax.swing.JTextField txtTanggalBerangkat;
    private javax.swing.JTextField txtTanggalPesan;
    private javax.swing.JTextField txtTotalBayar;
    // End of variables declaration//GEN-END:variables
}
