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
import net.proteanit.sql.DbUtils;
import javax.swing.table.DefaultTableModel;
import com.pap.prjpapfinal.datainfo.ModuloConexao;

/**
 *
 * @author Daniel Silva
 */
public class Clientscreen extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form Clientscreen
     */
    public Clientscreen() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    private void adicionar() {
        String sql = "Insert into tbclients(name_client,endclient,foneclient,email_client,client_postalcode) values(?,?,?,?,?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtclinome.getText());
            pst.setString(2, txtclimor1.getText());
            pst.setString(3, txtclinum.getText());
            pst.setString(4, txtclimail.getText());
            pst.setString(5, txtpostal.getText());
            //validar os campos obrigatorios
            if ((txtclinome.getText().isEmpty() || txtclinum.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios.");
            } else {
                //atualizar a tabela de utilizadores
                int adicionado = pst.executeUpdate();
                //System.out.println(adicionado);
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Cliente adicionado com sucesso.");
                    limpar();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    //metodo pesquisa de clientes
    private void pesquisar_clientes() {
        String sql = "Select idclient as ID_Cliente, name_client as Nome, endclient as Morada, foneclient as Telefone, email_client as Email, client_postalcode as Código_Postal from tbclients where name_client like ?";
        try {
            pst = conexao.prepareStatement(sql);
            // % = continuacao da string sql
            pst.setString(1, txtclipesquisar.getText() + "%");
            rs = pst.executeQuery();
            //rs2xml
            tblclientes.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    //metodo para preencher campos da tabela
    public void precampos() {
        int preencher = tblclientes.getSelectedRow();
        txtidcli.setText(tblclientes.getModel().getValueAt(preencher, 0).toString());
        txtclinome.setText(tblclientes.getModel().getValueAt(preencher, 1).toString());
        txtclimor1.setText(tblclientes.getModel().getValueAt(preencher, 2).toString());
        txtclinum.setText(tblclientes.getModel().getValueAt(preencher, 3).toString());
        txtclimail.setText(tblclientes.getModel().getValueAt(preencher, 4).toString());
        txtpostal.setText(tblclientes.getModel().getValueAt(preencher, 5).toString());
        txtclipesquisar.setText(tblclientes.getModel().getValueAt(preencher, 1).toString());
        //desabilitar o btn adicionar
        btnadd.setEnabled(false);
    }

    //alterar dados clientes
    private void alterar() {
        String sql = "update tbclients set name_client=?, endclient=?, foneclient=?, email_client=?, client_postalcode=? where idclient=? ";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtclinome.getText());
            pst.setString(2, txtclimor1.getText());
            pst.setString(3, txtclinum.getText());
            pst.setString(4, txtclimail.getText());
            pst.setString(5, txtpostal.getText());
            pst.setString(6, txtidcli.getText());
            if ((txtclinome.getText().isEmpty() || txtclinum.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios.");
            } else {
                //atualizar a tabela de utilizadores
                int adicionado = pst.executeUpdate();
                //System.out.println(adicionado);
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Dados do cliente alterados com sucesso.");
                    limpar();
                    btnadd.setEnabled(true);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void apagar() {
        //confirmar a remoção do utilizador
        int confirma = JOptionPane.showConfirmDialog(null, "Tem a certeza que deseja remover este cliente?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "Delete from tbclients where idclient=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtidcli.getText());
                int apagado = pst.executeUpdate();
                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "Cliente removido com sucesso.");
                    limpar();
                    btnadd.setEnabled(true);
                } else {
                }
            } catch (java.sql.SQLIntegrityConstraintViolationException e) {
                JOptionPane.showMessageDialog(null, "Não é possível remover.\nO cliente tem uma ordem de serviço cadastrada.");
            } catch (Exception e2) {
                JOptionPane.showMessageDialog(null, e2);
                //System.out.println(e2);
            }
        } else {
        }
    }

    private void limpar() {
        txtclimail.setText(null);
        txtclimor1.setText(null);
        txtclinome.setText(null);
        txtclinum.setText(null);
        txtpostal.setText(null);
        txtidcli.setText(null);
        txtclipesquisar.setText(null);
        ((DefaultTableModel) tblclientes.getModel()).setRowCount(0);
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
        txtclipesquisar = new javax.swing.JTextField();
        txtpostal = new javax.swing.JTextField();
        txtclinum = new javax.swing.JTextField();
        txtclimail = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btnadd = new javax.swing.JButton();
        btnalt = new javax.swing.JButton();
        btndel = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator6 = new javax.swing.JSeparator();
        txtclinome = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblclientes = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        txtclimor1 = new javax.swing.JTextField();
        jSeparator7 = new javax.swing.JSeparator();
        txtidcli = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Clientes");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("* Nome");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, 60, -1));

        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setText("Código Postal");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 220, 100, -1));

        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setText("* Telefone");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, -1, -1));

        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setText("Email");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 320, 50, -1));

        txtclipesquisar.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtclipesquisar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txtclipesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtclipesquisarKeyReleased(evt);
            }
        });
        getContentPane().add(txtclipesquisar, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, 390, 20));

        txtpostal.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtpostal.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txtpostal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtpostalActionPerformed(evt);
            }
        });
        getContentPane().add(txtpostal, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 220, 180, 20));

        txtclinum.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtclinum.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        getContentPane().add(txtclinum, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 320, 210, 20));

        txtclimail.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtclimail.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        getContentPane().add(txtclimail, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 320, 240, 20));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText("* Campos Obrigatórios");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 30, -1, -1));

        btnadd.setIcon(new javax.swing.ImageIcon("E:\\PAP\\Icons\\103511_text_add_document_icon.png")); // NOI18N
        btnadd.setToolTipText("Adicionar");
        btnadd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnaddActionPerformed(evt);
            }
        });
        getContentPane().add(btnadd, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 360, -1, -1));

        btnalt.setIcon(new javax.swing.ImageIcon("E:\\PAP\\Icons\\103514_edit_text_document_icon.png")); // NOI18N
        btnalt.setToolTipText("Alterar");
        btnalt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnalt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnaltActionPerformed(evt);
            }
        });
        getContentPane().add(btnalt, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 360, -1, -1));

        btndel.setIcon(new javax.swing.ImageIcon("E:\\PAP\\Icons\\103512_cancel_text_document_icon.png")); // NOI18N
        btndel.setToolTipText("Remover");
        btndel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btndel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btndelActionPerformed(evt);
            }
        });
        getContentPane().add(btndel, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 360, -1, -1));
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 340, 240, 10));
        getContentPane().add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 290, 230, 10));
        getContentPane().add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 340, 210, 10));
        getContentPane().add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 240, 180, 10));

        txtclinome.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtclinome.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        getContentPane().add(txtclinome, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 270, 230, 20));

        jLabel6.setIcon(new javax.swing.ImageIcon("E:\\PAP\\Icons\\7853742_find_kashifarif_explore_magnifier_zoom_icon.png")); // NOI18N
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        tblclientes = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblclientes.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        tblclientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID_Cliente", "Nome", "Morada", "Telefone", "Email", "Código_Postal"
            }
        ));
        tblclientes.setFocusable(false);
        tblclientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblclientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblclientes);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 610, 110));

        jLabel7.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel7.setText("Morada");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 270, 60, -1));

        txtclimor1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtclimor1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        getContentPane().add(txtclimor1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 270, 230, 20));
        getContentPane().add(jSeparator7, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 290, 230, 10));

        txtidcli.setEditable(false);
        txtidcli.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtidcli.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtidcli.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        getContentPane().add(txtidcli, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 220, 210, 20));

        jLabel8.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel8.setText("ID Cliente");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 80, -1));
        getContentPane().add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 240, 210, 10));

        setBounds(0, 0, 640, 477);
    }// </editor-fold>//GEN-END:initComponents

    private void txtclipesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtclipesquisarKeyReleased
        // TODO add your handling code here:
        //evento em tempo real
        pesquisar_clientes();
    }//GEN-LAST:event_txtclipesquisarKeyReleased

    private void txtpostalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtpostalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtpostalActionPerformed

    private void btnaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnaddActionPerformed
        // TODO add your handling code here:
        adicionar();
    }//GEN-LAST:event_btnaddActionPerformed

    private void btnaltActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnaltActionPerformed
        // TODO add your handling code here:
        alterar();
    }//GEN-LAST:event_btnaltActionPerformed

    private void btndelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btndelActionPerformed
        // TODO add your handling code here:
        apagar();
    }//GEN-LAST:event_btndelActionPerformed

    private void tblclientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblclientesMouseClicked
        // TODO add your handling code here:
        precampos();
    }//GEN-LAST:event_tblclientesMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnadd;
    private javax.swing.JButton btnalt;
    private javax.swing.JButton btndel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JTable tblclientes;
    private javax.swing.JTextField txtclimail;
    private javax.swing.JTextField txtclimor1;
    private javax.swing.JTextField txtclinome;
    private javax.swing.JTextField txtclinum;
    private javax.swing.JTextField txtclipesquisar;
    private javax.swing.JTextField txtidcli;
    private javax.swing.JTextField txtpostal;
    // End of variables declaration//GEN-END:variables
}
