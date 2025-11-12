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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import pbo_2310010205.Koneksi;
/**
 *
 * @author Anomali
 */
public class FrmPaketWisata extends javax.swing.JFrame {
    
  private DefaultTableModel model;
  
    public FrmPaketWisata() {
      initComponents();
        setLocationRelativeTo(null);
        setTableModel();
        loadDestinasi();
        loadData();
        modeAwal();
    }

    // ================== SETUP TABLE ==================
    private void setTableModel() {
        model = new DefaultTableModel(
            new String[]{
                "ID Paket",
                "Nama Paket",
                "ID Destinasi",
                "Nama Destinasi",
                "Durasi (hari)",
                "Harga Paket",
                "Kuota",
                "Fasilitas"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabel hanya display
            }
        };
        tblPaketWisata.setModel(model);
        tblPaketWisata.setRowHeight(22);
    }

    // ================== MODE AWAL & RESET ==================
    private void modeAwal() {
        txtIdPaket.setEnabled(false);
        resetForm();
    }

    private void resetForm() {
        txtIdPaket.setText("");
        txtNamaPaket.setText("");
        cmbDestinasi.setSelectedIndex(0);
        txtDurasi.setText("");
        txtHargaPaket.setText("");
        txtKuota.setText("");
        txtFasilitas.setText("");
        tblPaketWisata.clearSelection();
        txtNamaPaket.requestFocus();
    }

    // ================== LOAD DESTINASI KE COMBO ==================
    private void loadDestinasi() {
        cmbDestinasi.removeAllItems();
        cmbDestinasi.addItem("-- Pilih Destinasi --");

        String sql = "SELECT id_destinasi, nama_destinasi FROM destinasi ORDER BY nama_destinasi";
        try (Connection c = Koneksi.getKoneksi();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id_destinasi");
                String nama = rs.getString("nama_destinasi");
                // format: "id - nama"
                cmbDestinasi.addItem(id + " - " + nama);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal load destinasi: " + e.getMessage());
        }
    }

    // ================== BANTUAN: AMBIL ID DARI COMBO ==================
    private int getIdDestinasiDariCombo() {
        int idx = cmbDestinasi.getSelectedIndex();
        if (idx <= 0) return 0; // "-- Pilih Destinasi --"

        String item = (String) cmbDestinasi.getSelectedItem();
        if (item == null || !item.contains(" - ")) return 0;

        try {
            return Integer.parseInt(item.split(" - ")[0]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ================== LOAD DATA KE TABEL ==================
    private void loadData() {
        model.setRowCount(0);
        String sql =
            "SELECT p.id_paket, p.nama_paket, d.id_destinasi, d.nama_destinasi, " +
            "p.durasi_hari, p.harga_paket, p.kuota, p.fasilitas " +
            "FROM paket_wisata p " +
            "JOIN destinasi d ON p.id_destinasi = d.id_destinasi " +
            "ORDER BY p.id_paket DESC";

        try (Connection c = Koneksi.getKoneksi();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_paket"),
                    rs.getString("nama_paket"),
                    rs.getInt("id_destinasi"),
                    rs.getString("nama_destinasi"),
                    rs.getInt("durasi_hari"),
                    rs.getDouble("harga_paket"),
                    rs.getInt("kuota"),
                    rs.getString("fasilitas")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal load data paket: " + e.getMessage());
        }
    }

    // ================== VALIDASI ==================
    private boolean validasiInput() {
        if (txtNamaPaket.getText().trim().isEmpty()
                || cmbDestinasi.getSelectedIndex() <= 0
                || txtDurasi.getText().trim().isEmpty()
                || txtHargaPaket.getText().trim().isEmpty()
                || txtKuota.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Nama paket, destinasi, durasi, harga, dan kuota wajib diisi");
            return false;
        }

        try {
            Integer.parseInt(txtDurasi.getText().trim());
            Integer.parseInt(txtKuota.getText().trim());
            Double.parseDouble(txtHargaPaket.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Durasi, kuota, dan harga harus berupa angka");
            return false;
        }
        return true;
    }

    // ================== SIMPAN ==================
    private void simpanData() {
        if (!validasiInput()) return;

        String namaPaket = txtNamaPaket.getText().trim();
        int idDest = getIdDestinasiDariCombo();
        int durasi = Integer.parseInt(txtDurasi.getText().trim());
        double harga = Double.parseDouble(txtHargaPaket.getText().trim());
        int kuota = Integer.parseInt(txtKuota.getText().trim());
        String fasilitas = txtFasilitas.getText().trim();

        String sql = "INSERT INTO paket_wisata " +
                     "(nama_paket, id_destinasi, durasi_hari, harga_paket, kuota, fasilitas) " +
                     "VALUES (?,?,?,?,?,?)";

        try (Connection c = Koneksi.getKoneksi();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, namaPaket);
            ps.setInt(2, idDest);
            ps.setInt(3, durasi);
            ps.setDouble(4, harga);
            ps.setInt(5, kuota);
            ps.setString(6, fasilitas);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                    "Data paket wisata berhasil disimpan");
            loadData();
            resetForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal simpan: " + e.getMessage());
        }
    }

    // ================== UBAH ==================
    private void ubahData() {
        String idStr = txtIdPaket.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data paket dari tabel dulu");
            return;
        }
        if (!validasiInput()) return;

        int idPaket = Integer.parseInt(idStr);
        String namaPaket = txtNamaPaket.getText().trim();
        int idDest = getIdDestinasiDariCombo();
        int durasi = Integer.parseInt(txtDurasi.getText().trim());
        double harga = Double.parseDouble(txtHargaPaket.getText().trim());
        int kuota = Integer.parseInt(txtKuota.getText().trim());
        String fasilitas = txtFasilitas.getText().trim();

        String sql = "UPDATE paket_wisata SET " +
                     "nama_paket=?, id_destinasi=?, durasi_hari=?, " +
                     "harga_paket=?, kuota=?, fasilitas=? " +
                     "WHERE id_paket=?";

        try (Connection c = Koneksi.getKoneksi();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, namaPaket);
            ps.setInt(2, idDest);
            ps.setInt(3, durasi);
            ps.setDouble(4, harga);
            ps.setInt(5, kuota);
            ps.setString(6, fasilitas);
            ps.setInt(7, idPaket);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                    "Data paket wisata berhasil diubah");
            loadData();
            resetForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal ubah: " + e.getMessage());
        }
    }

    // ================== HAPUS ==================
    private void hapusData() {
        String idStr = txtIdPaket.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data paket yang akan dihapus");
            return;
        }

        int konfirm = JOptionPane.showConfirmDialog(this,
                "Yakin hapus data paket ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (konfirm != JOptionPane.YES_OPTION) return;

        int idPaket = Integer.parseInt(idStr);
        String sql = "DELETE FROM paket_wisata WHERE id_paket=?";

        try (Connection c = Koneksi.getKoneksi();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idPaket);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                    "Data paket wisata berhasil dihapus");
            loadData();
            resetForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal hapus: " + e.getMessage());
        }
    }

    // ================== ISI FORM DARI TABEL ==================
    private void isiFormDariTabel() {
        int row = tblPaketWisata.getSelectedRow();
        if (row == -1) return;

        txtIdPaket.setText(model.getValueAt(row, 0).toString());
        txtNamaPaket.setText(model.getValueAt(row, 1).toString());

        // Set combo destinasi sesuai ID + Nama
        String idDest = model.getValueAt(row, 2).toString();
        String namaDest = model.getValueAt(row, 3).toString();
        String item = idDest + " - " + namaDest;
        cmbDestinasi.setSelectedItem(item);

        txtDurasi.setText(model.getValueAt(row, 4).toString());
        txtHargaPaket.setText(model.getValueAt(row, 5).toString());
        txtKuota.setText(model.getValueAt(row, 6).toString());
        txtFasilitas.setText(model.getValueAt(row, 7) == null
                ? "" : model.getValueAt(row, 7).toString());
    }

 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnSimpan = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnHapus = new javax.swing.JButton();
        txtIdPaket = new javax.swing.JTextField();
        btnUbah = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtNamaPaket = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtHargaPaket = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPaketWisata = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cmbDestinasi = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        txtDurasi = new javax.swing.JTextField();
        jScroolPane2 = new javax.swing.JScrollPane();
        txtFasilitas = new javax.swing.JTextArea();
        txtKuota = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        jLabel1.setText("ID Paket");

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        txtIdPaket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdPaketActionPerformed(evt);
            }
        });

        btnUbah.setText("Ubah");
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
            }
        });

        jLabel2.setText("Nama Paket");

        jLabel3.setText("Harga");

        tblPaketWisata.setModel(new javax.swing.table.DefaultTableModel(
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
        tblPaketWisata.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPaketWisataMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPaketWisata);

        jLabel4.setText("Fasilitas");

        jLabel5.setText("Jenis");

        cmbDestinasi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mobil", "Motor", "All", " " }));
        cmbDestinasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbDestinasiActionPerformed(evt);
            }
        });

        jLabel6.setText("Durasi");

        txtFasilitas.setColumns(20);
        txtFasilitas.setRows(5);
        jScroolPane2.setViewportView(txtFasilitas);

        jLabel7.setText("Kouta");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtIdPaket, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNamaPaket, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDurasi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtHargaPaket, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScroolPane2))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(52, 52, 52)
                                .addComponent(cmbDestinasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSimpan)
                                .addGap(18, 18, 18)
                                .addComponent(btnUbah)
                                .addGap(18, 18, 18)
                                .addComponent(btnHapus)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtKuota, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 551, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtIdPaket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNamaPaket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cmbDestinasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtDurasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtHargaPaket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtKuota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScroolPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan)
                    .addComponent(btnUbah)
                    .addComponent(btnHapus))
                .addContainerGap(14, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbDestinasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbDestinasiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbDestinasiActionPerformed

    private void txtIdPaketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdPaketActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdPaketActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        simpanData();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahActionPerformed
        ubahData();
    }//GEN-LAST:event_btnUbahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        hapusData();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void tblPaketWisataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPaketWisataMouseClicked
        if (evt.getClickCount() >= 1) {
            isiFormDariTabel();
        }
    }//GEN-LAST:event_tblPaketWisataMouseClicked

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
        java.awt.EventQueue.invokeLater(() -> new FrmPaketWisata().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnUbah;
    private javax.swing.JComboBox<String> cmbDestinasi;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScroolPane2;
    private javax.swing.JTable tblPaketWisata;
    private javax.swing.JTextField txtDurasi;
    private javax.swing.JTextArea txtFasilitas;
    private javax.swing.JTextField txtHargaPaket;
    private javax.swing.JTextField txtIdPaket;
    private javax.swing.JTextField txtKuota;
    private javax.swing.JTextField txtNamaPaket;
    // End of variables declaration//GEN-END:variables
}
