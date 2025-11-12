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
public class FrmDestinasi extends javax.swing.JFrame {
    
  private DefaultTableModel model;
  
    public FrmDestinasi() {
      initComponents();
        setLocationRelativeTo(null);
        setTableModel();
        isiComboJenis();
        loadData();
        modeAwal();
    }

    // ================== SETUP TABLE ==================
    private void setTableModel() {
        model = new DefaultTableModel(
            new String[]{
                "ID", "Nama Destinasi", "Lokasi",
                "Jenis", "Harga Tiket", "Deskripsi"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabel hanya untuk lihat
            }
        };
        tblDestinasi.setModel(model);
        tblDestinasi.setRowHeight(22);
    }

    // ================== ISI COMBO JENIS ==================
    private void isiComboJenis() {
        cmbJenis.removeAllItems();
        cmbJenis.addItem("-- Pilih Jenis --");
        cmbJenis.addItem("Budaya");
        cmbJenis.addItem("Alam");
        cmbJenis.addItem("Religi");
        cmbJenis.addItem("Kuliner");
        cmbJenis.addItem("Lainnya");
    }

    // ================== MODE AWAL ==================
    private void modeAwal() {
        txtIdDestinasi.setEnabled(false);
        resetForm();
    }

    // ================== RESET FORM ==================
    private void resetForm() {
        txtIdDestinasi.setText("");
        txtNamaDestinasi.setText("");
        txtLokasi.setText("");
        txtHargaTiket.setText("");
        txtDeskripsi.setText("");
        cmbJenis.setSelectedIndex(0);
        tblDestinasi.clearSelection();
        txtNamaDestinasi.requestFocus();
    }

    // ================== LOAD DATA KE TABEL ==================
    private void loadData() {
        model.setRowCount(0);
        String sql = "SELECT * FROM destinasi ORDER BY id_destinasi DESC";

        try (Connection c = Koneksi.getKoneksi();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_destinasi"),
                    rs.getString("nama_destinasi"),
                    rs.getString("lokasi"),
                    rs.getString("jenis"),
                    rs.getDouble("harga_tiket"),
                    rs.getString("deskripsi")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal load data: " + e.getMessage());
        }
    }

    // ================== VALIDASI INPUT ==================
    private boolean validasiInput() {
        if (txtNamaDestinasi.getText().trim().isEmpty()
                || txtLokasi.getText().trim().isEmpty()
                || cmbJenis.getSelectedIndex() == 0
                || txtHargaTiket.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Nama, Lokasi, Jenis, dan Harga Tiket wajib diisi");
            return false;
        }

        try {
            Double.parseDouble(txtHargaTiket.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Harga tiket harus berupa angka");
            return false;
        }
        return true;
    }

    // ================== SIMPAN DATA ==================
    private void simpanData() {
        if (!validasiInput()) return;

        String nama = txtNamaDestinasi.getText().trim();
        String lokasi = txtLokasi.getText().trim();
        String jenis = (String) cmbJenis.getSelectedItem();
        String deskripsi = txtDeskripsi.getText().trim();
        double harga = Double.parseDouble(txtHargaTiket.getText().trim());

        String sql = "INSERT INTO destinasi "
                   + "(nama_destinasi, lokasi, jenis, deskripsi, harga_tiket) "
                   + "VALUES (?,?,?,?,?)";

        try (Connection c = Koneksi.getKoneksi();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nama);
            ps.setString(2, lokasi);
            ps.setString(3, jenis);
            ps.setString(4, deskripsi);
            ps.setDouble(5, harga);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                    "Data destinasi berhasil disimpan");
            loadData();
            resetForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal simpan: " + e.getMessage());
        }
    }

    // ================== UBAH DATA ==================
    private void ubahData() {
        String idStr = txtIdDestinasi.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data dari tabel yang akan diubah");
            return;
        }
        if (!validasiInput()) return;

        int id = Integer.parseInt(idStr);
        String nama = txtNamaDestinasi.getText().trim();
        String lokasi = txtLokasi.getText().trim();
        String jenis = (String) cmbJenis.getSelectedItem();
        String deskripsi = txtDeskripsi.getText().trim();
        double harga = Double.parseDouble(txtHargaTiket.getText().trim());

        String sql = "UPDATE destinasi SET "
                   + "nama_destinasi=?, lokasi=?, jenis=?, "
                   + "deskripsi=?, harga_tiket=? "
                   + "WHERE id_destinasi=?";

        try (Connection c = Koneksi.getKoneksi();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nama);
            ps.setString(2, lokasi);
            ps.setString(3, jenis);
            ps.setString(4, deskripsi);
            ps.setDouble(5, harga);
            ps.setInt(6, id);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                    "Data destinasi berhasil diubah");
            loadData();
            resetForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal ubah: " + e.getMessage());
        }
    }

    // ================== HAPUS DATA ==================
    private void hapusData() {
        String idStr = txtIdDestinasi.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data dari tabel yang akan dihapus");
            return;
        }

        int konfirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus data ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (konfirm != JOptionPane.YES_OPTION) return;

        int id = Integer.parseInt(idStr);
        String sql = "DELETE FROM destinasi WHERE id_destinasi=?";

        try (Connection c = Koneksi.getKoneksi();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                    "Data destinasi berhasil dihapus");
            loadData();
            resetForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal hapus: " + e.getMessage());
        }
    }

    // ================== ISI FORM DARI TABEL ==================
    private void isiFormDariTabel() {
        int row = tblDestinasi.getSelectedRow();
        if (row == -1) return;

        txtIdDestinasi.setText(model.getValueAt(row, 0).toString());
        txtNamaDestinasi.setText(model.getValueAt(row, 1).toString());
        txtLokasi.setText(model.getValueAt(row, 2).toString());

        String jenis = model.getValueAt(row, 3).toString();
        cmbJenis.setSelectedItem(jenis);

        txtHargaTiket.setText(model.getValueAt(row, 4).toString());
        txtDeskripsi.setText(model.getValueAt(row, 5) == null
                ? "" : model.getValueAt(row, 5).toString());
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
        txtIdDestinasi = new javax.swing.JTextField();
        btnUbah = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtNamaDestinasi = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtHargaTiket = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDestinasi = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cmbJenis = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        txtLokasi = new javax.swing.JTextField();
        jScroolPane2 = new javax.swing.JScrollPane();
        txtDeskripsi = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        jLabel1.setText("ID Destinasi");

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        txtIdDestinasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdDestinasiActionPerformed(evt);
            }
        });

        btnUbah.setText("Ubah");
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
            }
        });

        jLabel2.setText("Nama Destinasi");

        jLabel3.setText("Harga");

        tblDestinasi.setModel(new javax.swing.table.DefaultTableModel(
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
        tblDestinasi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDestinasiMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblDestinasi);

        jLabel4.setText("Deskripsi");

        jLabel5.setText("Jenis");

        cmbJenis.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mobil", "Motor", "All", " " }));
        cmbJenis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbJenisActionPerformed(evt);
            }
        });

        jLabel6.setText("Lokasi");

        txtDeskripsi.setColumns(20);
        txtDeskripsi.setRows(5);
        jScroolPane2.setViewportView(txtDeskripsi);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtHargaTiket, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtIdDestinasi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNamaDestinasi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLokasi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSimpan)
                                .addGap(18, 18, 18)
                                .addComponent(btnUbah)
                                .addGap(18, 18, 18)
                                .addComponent(btnHapus))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(52, 52, 52)
                                .addComponent(cmbJenis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScroolPane2)))
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
                    .addComponent(txtIdDestinasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNamaDestinasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtLokasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cmbJenis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtHargaTiket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScroolPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan)
                    .addComponent(btnUbah)
                    .addComponent(btnHapus))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbJenisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbJenisActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbJenisActionPerformed

    private void txtIdDestinasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdDestinasiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdDestinasiActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        simpanData();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahActionPerformed
        ubahData();
    }//GEN-LAST:event_btnUbahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        hapusData();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void tblDestinasiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDestinasiMouseClicked
        if (evt.getClickCount() >= 1) {
            isiFormDariTabel();
        }
    }//GEN-LAST:event_tblDestinasiMouseClicked

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
        java.awt.EventQueue.invokeLater(() -> new FrmDestinasi().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnUbah;
    private javax.swing.JComboBox<String> cmbJenis;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScroolPane2;
    private javax.swing.JTable tblDestinasi;
    private javax.swing.JTextArea txtDeskripsi;
    private javax.swing.JTextField txtHargaTiket;
    private javax.swing.JTextField txtIdDestinasi;
    private javax.swing.JTextField txtLokasi;
    private javax.swing.JTextField txtNamaDestinasi;
    // End of variables declaration//GEN-END:variables
}
