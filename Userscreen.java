/*
 * The MIT License
 *
 * Copyright 2023 Daniel Silva.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.pap.prjpapfinal.screens;

import java.sql.*;
import javax.swing.JOptionPane;
import com.pap.prjpapfinal.datainfo.ModuloConexao;

/**
 *
 * @author Daniel Silva
 */
public class Userscreen extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form Userscreen
     */
    public Userscreen() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    private void consultar() {
        String sql = "Select * from tbusers where iduser=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtuseid.getText());
            rs = pst.executeQuery();
            if (rs.next()) {
                txtusen.setText(rs.getString(2));
                txtusenum.setText(rs.getString(3));
                txtuselogin.setText(rs.getString(4));
                txtusepass.setText(rs.getString(5));
                // combo box
                cbousertype.setSelectedItem(rs.getString(6));
            } else {
                JOptionPane.showMessageDialog(null, "Utilizador não encontrado.");
                //limpar os campos
                txtusen.setText(null);
                txtusenum.setText(null);
                txtuselogin.setText(null);
                txtusepass.setText(null);
                txtuseid.setText(null);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void adicionar() {
        String sql = "Insert into tbusers(iduser,utilizador,fone,login,pass_word,perfil) values(?,?,?,?,?,?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtuseid.getText());
            pst.setString(2, txtusen.getText());
            pst.setString(3, txtusenum.getText());
            pst.setString(4, txtuselogin.getText());
            pst.setString(5, txtusepass.getText());
            pst.setString(6, cbousertype.getSelectedItem().toString());
            //validar os campos obrigatorios
            if ((txtuseid.getText().isEmpty() || txtusen.getText().isEmpty()) || txtuselogin.getText().isEmpty() || txtusepass.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios.");
            } else {
                //atualizar a tabela de utilizadores
                int adicionado = pst.executeUpdate();
                //System.out.println(adicionado);
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Utilizador adicionado com sucesso.");
                    txtusen.setText(null);
                    txtusenum.setText(null);
                    txtuselogin.setText(null);
                    txtusepass.setText(null);
                    txtuseid.setText(null);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void alterar() {
        String sql = "Update tbusers set utilizador=?,fone=?,login=?,pass_word=?,perfil=? where iduser=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtusen.getText());
            pst.setString(2, txtusenum.getText());
            pst.setString(3, txtuselogin.getText());
            pst.setString(4, txtusepass.getText());
            pst.setString(5, cbousertype.getSelectedItem().toString());
            pst.setString(6, txtuseid.getText());
            if ((txtuseid.getText().isEmpty() || txtusen.getText().isEmpty()) || txtuselogin.getText().isEmpty() || txtusepass.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios.");
            } else {
                //atualizar a tabela de utilizadores
                int adicionado = pst.executeUpdate();
                //System.out.println(adicionado);
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Dados do utilizador alterados com sucesso.");
                    txtusen.setText(null);
                    txtusenum.setText(null);
                    txtuselogin.setText(null);
                    txtusepass.setText(null);
                    txtuseid.setText(null);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void apagar() {
        //confirmar a remoção do utilizador
        int confirma = JOptionPane.showConfirmDialog(null, "Tem a certeza que deseja remover este utilizador?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "Delete from tbusers where iduser=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtuseid.getText());
                int apagado = pst.executeUpdate();
                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "Utilizador removido com sucesso.");
                    txtusen.setText(null);
                    txtusenum.setText(null);
                    txtuselogin.setText(null);
                    txtusepass.setText(null);
                    txtuseid.setText(null);
                } else {
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        } else {
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtuseid = new javax.swing.JTextField();
        txtusenum = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtusepass = new javax.swing.JTextField();
        cbousertype = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        txtusen = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtuselogin = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        btnusecreate = new javax.swing.JButton();
        btnuseread = new javax.swing.JButton();
        btnuseupt = new javax.swing.JButton();
        btnusedel = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Utilizadores");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("* ID Utilizador");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setText("* Nome");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 60, -1));

        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setText("* Palavra-Passe");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 360, -1, -1));

        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Perfil");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, 80, -1));

        txtuseid.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtuseid.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txtuseid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtuseidActionPerformed(evt);
            }
        });
        getContentPane().add(txtuseid, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, 220, 20));

        txtusenum.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtusenum.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        getContentPane().add(txtusenum, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 200, 250, 20));

        jLabel5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel5.setText("Telefone");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, -1, -1));

        txtusepass.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtusepass.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        getContentPane().add(txtusepass, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 360, 200, 20));

        cbousertype.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cbousertype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "user", "admin" }));
        cbousertype.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        getContentPane().add(cbousertype, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 50, 80, -1));
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 390, 200, 10));
        getContentPane().add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 80, 220, 10));

        txtusen.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtusen.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        getContentPane().add(txtusen, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 260, 20));

        jLabel7.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel7.setText("* Login");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 50, -1));

        txtuselogin.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtuselogin.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        getContentPane().add(txtuselogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 280, 260, 20));
        getContentPane().add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 150, 260, 10));
        getContentPane().add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 230, 250, 10));
        getContentPane().add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 310, 260, 10));

        btnusecreate.setIcon(new javax.swing.ImageIcon("E:\\PAP\\Icons\\103511_text_add_document_icon.png")); // NOI18N
        btnusecreate.setToolTipText("Adicionar");
        btnusecreate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnusecreate.setPreferredSize(new java.awt.Dimension(80, 80));
        btnusecreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnusecreateActionPerformed(evt);
            }
        });
        getContentPane().add(btnusecreate, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 250, -1, -1));

        btnuseread.setIcon(new javax.swing.ImageIcon("E:\\PAP\\Icons\\103515_text_document_information_icon.png")); // NOI18N
        btnuseread.setToolTipText("Consultar");
        btnuseread.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnuseread.setPreferredSize(new java.awt.Dimension(80, 80));
        btnuseread.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnusereadActionPerformed(evt);
            }
        });
        getContentPane().add(btnuseread, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 350, -1, -1));

        btnuseupt.setIcon(new javax.swing.ImageIcon("E:\\PAP\\Icons\\103514_edit_text_document_icon.png")); // NOI18N
        btnuseupt.setToolTipText("Alterar");
        btnuseupt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnuseupt.setPreferredSize(new java.awt.Dimension(80, 80));
        btnuseupt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnuseuptActionPerformed(evt);
            }
        });
        getContentPane().add(btnuseupt, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 250, -1, -1));

        btnusedel.setIcon(new javax.swing.ImageIcon("E:\\PAP\\Icons\\103512_cancel_text_document_icon.png")); // NOI18N
        btnusedel.setToolTipText("Remover");
        btnusedel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnusedel.setPreferredSize(new java.awt.Dimension(80, 80));
        btnusedel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnusedelActionPerformed(evt);
            }
        });
        getContentPane().add(btnusedel, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 350, -1, -1));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText("* Campos Obrigatórios");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, -1, -1));

        setBounds(0, 0, 640, 477);
    }// </editor-fold>//GEN-END:initComponents

    private void txtuseidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtuseidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtuseidActionPerformed

    private void btnusecreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnusecreateActionPerformed
        // TODO add your handling code here:
        adicionar();
    }//GEN-LAST:event_btnusecreateActionPerformed

    private void btnusereadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnusereadActionPerformed
        // TODO add your handling code here:
        consultar();
    }//GEN-LAST:event_btnusereadActionPerformed

    private void btnuseuptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnuseuptActionPerformed
        // TODO add your handling code here:
        alterar();
    }//GEN-LAST:event_btnuseuptActionPerformed

    private void btnusedelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnusedelActionPerformed
        // TODO add your handling code here:
        apagar();
    }//GEN-LAST:event_btnusedelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnusecreate;
    private javax.swing.JButton btnusedel;
    private javax.swing.JButton btnuseread;
    private javax.swing.JButton btnuseupt;
    private javax.swing.JComboBox<String> cbousertype;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JTextField txtuseid;
    private javax.swing.JTextField txtuselogin;
    private javax.swing.JTextField txtusen;
    private javax.swing.JTextField txtusenum;
    private javax.swing.JTextField txtusepass;
    // End of variables declaration//GEN-END:variables
}
