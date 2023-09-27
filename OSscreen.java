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
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import com.pap.prjpapfinal.datainfo.ModuloConexao;
import java.awt.Desktop;
import java.awt.HeadlessException;
import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

/**
 *
 * @author Daniel Silva
 */
public class OSscreen extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    //variavel tipo radiobutton
    private String tipo;

    /**
     * Creates new form OSscreen
     */
    public OSscreen() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    private void pesquisar_cliente() {
        String sql = "Select idclient as ID_Cliente, name_client as Nome, endclient as Morada, foneclient as Telefone from tbclients where name_client like ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtclipesq.getText() + "%");
            rs = pst.executeQuery();
            tblcli.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void precampos() {
        int pre = tblcli.getSelectedRow();
        txtcliid.setText(tblcli.getModel().getValueAt(pre, 0).toString());
        txtclipesq.setText(tblcli.getModel().getValueAt(pre, 1).toString());

    }

    private void emitiros() {
        String sql = "Insert into tbservice_order (tipo,situacao,equipament,fault,type_service,tecnico,valor,idclient) values(?,?,?,?,?,?,?,?)";

        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, tipo);
            pst.setString(2, cboos.getSelectedItem().toString());
            pst.setString(3, txtosequi.getText());
            pst.setString(4, txtosdef.getText());
            pst.setString(5, txtosserv.getText());
            pst.setString(6, txtosteq.getText());
            //replace ,.
            pst.setString(7, txtosval.getText().replace(",", "."));
            pst.setString(8, txtcliid.getText());

            //validar
            if ((txtcliid.getText().isEmpty() || txtosequi.getText().isEmpty()) || txtosdef.getText().isEmpty() || cboos.getSelectedItem().equals("Introduza o tipo de OS")) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios.");
            } else {
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    recuperarOs();
                    dataos();
                    JOptionPane.showMessageDialog(null, "Ordem de Serviço emitida com sucesso.");
                    btnosadd.setEnabled(false);
                    btnospesq.setEnabled(false);
                    btnosprint.setEnabled(true);
                    //
                    btnosalt.setEnabled(true);
                    btnosdel.setEnabled(true);
                    btnosclean.setEnabled(true);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void pesquisar_os() {
        String num_os = JOptionPane.showInputDialog("Número da Ordem de Serviço");
        String sql = "Select service_order, date_format(data_serviceorder,'%d/%m/%Y - %H:%i'),tipo,situacao,equipament,fault,type_service,tecnico,valor,idclient from tbservice_order where service_order=" + num_os;
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                txtos.setText(rs.getString(1));
                txtdata.setText(rs.getString(2));
                //rbtn
                String rbttipo = rs.getString(3);
                if (rbttipo.equals("Ordem de Serviço")) {
                    rbtos.setSelected(true);
                    tipo = "Ordem de Serviço";
                } else {
                    rbtorc.setSelected(true);
                    tipo = "Orçamento";
                }
                cboos.setSelectedItem(rs.getString(4));
                txtosequi.setText(rs.getString(5));
                txtosdef.setText(rs.getString(6));
                txtosserv.setText(rs.getString(7));
                txtosteq.setText(rs.getString(8));
                txtosval.setText(rs.getString(9));
                txtcliid.setText(rs.getString(10));
                //desativar btn add
                btnosadd.setEnabled(false);
                btnospesq.setEnabled(false);
                btnosalt.setEnabled(true);
                btnosprint.setEnabled(true);
                btnosdel.setEnabled(true);
                txtclipesq.setEnabled(false);
                tblcli.setVisible(false);
                btnosclean.setEnabled(true);
                txtclipesq.setText("");

            } else {
                if (num_os == null) {

                } else {
                    JOptionPane.showMessageDialog(null, "Ordem de Serviço não encontrada.");
                }
            }
        } catch (SQLSyntaxErrorException e) {
            JOptionPane.showMessageDialog(null, "Ordem de Serviço inválida.");
            //System.out.println(e);
        } catch (Exception e2) {
            JOptionPane.showMessageDialog(null, e2);
        }
    }

    private void alterar_os() {
        String sql = "Update tbservice_order set tipo=?,situacao=?,equipament=?,fault=?,type_service=?,tecnico=?,valor=? where service_order=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, tipo);
            pst.setString(2, cboos.getSelectedItem().toString());
            pst.setString(3, txtosequi.getText());
            pst.setString(4, txtosdef.getText());
            pst.setString(5, txtosserv.getText());
            pst.setString(6, txtosteq.getText());
            //replace ,.
            pst.setString(7, txtosval.getText().replace(",", "."));
            pst.setString(8, txtos.getText());

            //validar
            if ((txtcliid.getText().isEmpty() || txtosequi.getText().isEmpty()) || txtosdef.getText().isEmpty() || cboos.getSelectedItem().equals("Introduza o tipo de OS")) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios.");
            } else {
                int confirma = JOptionPane.showConfirmDialog(null, "Tem a certeza que alterar esta Ordem de Serviço?", "Atenção", JOptionPane.YES_NO_OPTION);
                if (confirma == JOptionPane.YES_OPTION) {
                    int adicionado = pst.executeUpdate();
                    if (adicionado > 0) {
                        JOptionPane.showMessageDialog(null, "Ordem de Serviço alterada com sucesso.");
                        limpar();
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void apagar_os() {
        int confirma = JOptionPane.showConfirmDialog(null, "Tem a certeza que deseja remover esta Ordem de Serviço?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "Delete from tbservice_order where service_order=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtos.getText());
                int apagado = pst.executeUpdate();
                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "Ordem de Serviço removida com sucesso.");
                    limpar();
                } else {
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        } else {
        }
    }

    private void recuperarOs() {
        String sql = "select max(service_order) from tbservice_order";
        try {
            conexao = ModuloConexao.conector();
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                txtos.setText(rs.getString(1));
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void dataos() {
        String sql = "select date_format(data_serviceorder,'%d/%m/%Y - %H:%i') from tbservice_order;";
        try {
            conexao = ModuloConexao.conector();
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                txtdata.setText(rs.getString(1));
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    //limpar campos
    private void limpar() {
        txtcliid.setText(null);
        txtosequi.setText(null);
        txtosdef.setText(null);
        txtosserv.setText(null);
        txtosteq.setText(null);
        txtosval.setText("0");
        txtos.setText(null);
        txtdata.setText(null);
        txtclipesq.setText(null);
        ((DefaultTableModel) tblcli.getModel()).setRowCount(0);
        cboos.setSelectedItem("Introduza o tipo de OS");
        //reabilitar objetos
        btnosadd.setEnabled(true);
        btnospesq.setEnabled(true);
        txtclipesq.setEnabled(true);
        tblcli.setVisible(true);
        btnosdel.setEnabled(false);
        btnosalt.setEnabled(false);
        btnosprint.setEnabled(false);
        btnosclean.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtos = new javax.swing.JTextField();
        rbtorc = new javax.swing.JRadioButton();
        rbtos = new javax.swing.JRadioButton();
        jSeparator2 = new javax.swing.JSeparator();
        txtdata = new javax.swing.JTextField();
        jSeparator6 = new javax.swing.JSeparator();
        cboos = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblcli = new javax.swing.JTable();
        txtcliid = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        txtclipesq = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtosteq = new javax.swing.JTextField();
        txtosequi = new javax.swing.JTextField();
        txtosdef = new javax.swing.JTextField();
        txtosserv = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator7 = new javax.swing.JSeparator();
        jSeparator8 = new javax.swing.JSeparator();
        jSeparator9 = new javax.swing.JSeparator();
        txtosval = new javax.swing.JTextField();
        jSeparator11 = new javax.swing.JSeparator();
        jLabel10 = new javax.swing.JLabel();
        btnosdel = new javax.swing.JButton();
        btnosprint = new javax.swing.JButton();
        btnosalt = new javax.swing.JButton();
        btnosadd = new javax.swing.JButton();
        btnospesq = new javax.swing.JButton();
        btnosclean = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Ordem de Serviços");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Nº OS");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 90, -1));

        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Data");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, 130, -1));

        txtos.setEditable(false);
        txtos.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtos.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtos.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txtos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtosActionPerformed(evt);
            }
        });
        jPanel1.add(txtos, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 47, 90, 20));

        buttonGroup1.add(rbtorc);
        rbtorc.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        rbtorc.setText("Orçamento");
        rbtorc.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rbtorc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtorcActionPerformed(evt);
            }
        });
        jPanel1.add(rbtorc, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 90, -1));

        buttonGroup1.add(rbtos);
        rbtos.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        rbtos.setText("Ordem de Serviço");
        rbtos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rbtos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtosActionPerformed(evt);
            }
        });
        jPanel1.add(rbtos, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 80, 130, -1));
        jPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 90, 10));

        txtdata.setEditable(false);
        txtdata.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtdata.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtdata.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txtdata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtdataActionPerformed(evt);
            }
        });
        jPanel1.add(txtdata, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 47, 130, 20));
        jPanel1.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 70, 130, 10));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 260, 120));

        cboos.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cboos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Introduza o tipo de OS", "Entrega OK", "Orçamento Reprovado", "Aguarda Aprovação", "Aguarda Peças", "Abandonado", "Na Bancada", "Retornou" }));
        cboos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        getContentPane().add(cboos, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 150, 190, 40));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Cliente"));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setIcon(new javax.swing.ImageIcon("E:\\PAP\\Icons\\7853742_find_kashifarif_explore_magnifier_zoom_icon.png")); // NOI18N
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));
        jPanel2.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 50, 270, 10));

        tblcli = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblcli.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID_Cliente", "Nome", "Telefone"
            }
        ));
        tblcli.setFocusable(false);
        tblcli.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblcliMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblcli);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 320, 100));

        txtcliid.setEditable(false);
        txtcliid.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtcliid.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtcliid.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txtcliid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcliidActionPerformed(evt);
            }
        });
        jPanel2.add(txtcliid, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, 60, 20));

        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setText("* ID");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 60, -1, -1));
        jPanel2.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 80, 60, 10));

        txtclipesq.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtclipesq.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txtclipesq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtclipesqActionPerformed(evt);
            }
        });
        txtclipesq.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtclipesqKeyReleased(evt);
            }
        });
        jPanel2.add(txtclipesq, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, 270, 20));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, 340, 200));

        jLabel5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Valor Total");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 310, 90, 20));

        txtosteq.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtosteq.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txtosteq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtosteqActionPerformed(evt);
            }
        });
        getContentPane().add(txtosteq, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 310, 190, 20));

        txtosequi.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtosequi.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txtosequi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtosequiActionPerformed(evt);
            }
        });
        getContentPane().add(txtosequi, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 220, 500, 20));

        txtosdef.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtosdef.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txtosdef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtosdefActionPerformed(evt);
            }
        });
        getContentPane().add(txtosdef, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 250, 500, 20));

        txtosserv.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtosserv.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txtosserv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtosservActionPerformed(evt);
            }
        });
        getContentPane().add(txtosserv, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 280, 500, 20));

        jLabel6.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel6.setText("Situação");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, -1, -1));

        jLabel7.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("* Equipamento");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 110, -1));

        jLabel8.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("* Defeito");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 110, -1));

        jLabel9.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Serviço");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 110, -1));
        getContentPane().add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 330, 190, 10));
        getContentPane().add(jSeparator7, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 240, 500, 10));
        getContentPane().add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 270, 500, 10));
        getContentPane().add(jSeparator9, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 300, 500, 10));

        txtosval.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtosval.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtosval.setText("0");
        txtosval.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txtosval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtosvalActionPerformed(evt);
            }
        });
        getContentPane().add(txtosval, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 310, 210, 20));
        getContentPane().add(jSeparator11, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 330, 210, 10));

        jLabel10.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Técnico");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 110, -1));

        btnosdel.setIcon(new javax.swing.ImageIcon("E:\\PAP\\Icons\\103512_cancel_text_document_icon.png")); // NOI18N
        btnosdel.setToolTipText("Apagar");
        btnosdel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnosdel.setEnabled(false);
        btnosdel.setPreferredSize(new java.awt.Dimension(80, 80));
        btnosdel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnosdelActionPerformed(evt);
            }
        });
        getContentPane().add(btnosdel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 350, -1, -1));

        btnosprint.setIcon(new javax.swing.ImageIcon("E:\\PAP\\Icons\\326675_print_icon.png")); // NOI18N
        btnosprint.setToolTipText("Imprimir");
        btnosprint.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnosprint.setEnabled(false);
        btnosprint.setPreferredSize(new java.awt.Dimension(80, 80));
        btnosprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnosprintActionPerformed(evt);
            }
        });
        getContentPane().add(btnosprint, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 350, -1, -1));

        btnosalt.setIcon(new javax.swing.ImageIcon("E:\\PAP\\Icons\\103514_edit_text_document_icon.png")); // NOI18N
        btnosalt.setToolTipText("Alterar");
        btnosalt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnosalt.setEnabled(false);
        btnosalt.setPreferredSize(new java.awt.Dimension(80, 80));
        btnosalt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnosaltActionPerformed(evt);
            }
        });
        getContentPane().add(btnosalt, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 350, -1, -1));

        btnosadd.setIcon(new javax.swing.ImageIcon("E:\\PAP\\Icons\\103511_text_add_document_icon.png")); // NOI18N
        btnosadd.setToolTipText("Emitir");
        btnosadd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnosadd.setPreferredSize(new java.awt.Dimension(80, 80));
        btnosadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnosaddActionPerformed(evt);
            }
        });
        getContentPane().add(btnosadd, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 350, -1, -1));

        btnospesq.setIcon(new javax.swing.ImageIcon("E:\\PAP\\Icons\\103515_text_document_information_icon.png")); // NOI18N
        btnospesq.setToolTipText("Pesquisar");
        btnospesq.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnospesq.setPreferredSize(new java.awt.Dimension(80, 80));
        btnospesq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnospesqActionPerformed(evt);
            }
        });
        getContentPane().add(btnospesq, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 350, -1, -1));

        btnosclean.setIcon(new javax.swing.ImageIcon("E:\\PAP\\Icons\\8541770_broom_clean_icon.png")); // NOI18N
        btnosclean.setToolTipText("Limpar");
        btnosclean.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnosclean.setEnabled(false);
        btnosclean.setPreferredSize(new java.awt.Dimension(80, 80));
        btnosclean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnoscleanActionPerformed(evt);
            }
        });
        getContentPane().add(btnosclean, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 350, -1, -1));

        setBounds(0, 0, 640, 477);
    }// </editor-fold>//GEN-END:initComponents

    private void txtosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtosActionPerformed

    private void rbtorcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtorcActionPerformed
        // TODO add your handling code here:
        tipo = "Orçamento";
    }//GEN-LAST:event_rbtorcActionPerformed

    private void rbtosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtosActionPerformed
        // TODO add your handling code here:
        tipo = "Ordem de Serviço";
    }//GEN-LAST:event_rbtosActionPerformed

    private void txtdataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtdataActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtdataActionPerformed

    private void tblcliMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblcliMouseClicked
        // TODO add your handling code here:
        precampos();
    }//GEN-LAST:event_tblcliMouseClicked

    private void txtcliidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcliidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcliidActionPerformed

    private void txtclipesqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtclipesqActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtclipesqActionPerformed

    private void txtclipesqKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtclipesqKeyReleased
        // TODO add your handling code here:
        pesquisar_cliente();
    }//GEN-LAST:event_txtclipesqKeyReleased

    private void txtosteqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtosteqActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtosteqActionPerformed

    private void txtosequiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtosequiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtosequiActionPerformed

    private void txtosdefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtosdefActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtosdefActionPerformed

    private void txtosservActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtosservActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtosservActionPerformed

    private void txtosvalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtosvalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtosvalActionPerformed

    private void btnosdelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnosdelActionPerformed
        // TODO add your handling code here:
        apagar_os();
    }//GEN-LAST:event_btnosdelActionPerformed

    private void btnosprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnosprintActionPerformed
        // TODO add your handling code here:
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressão deste relatório?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String nos = txtos.getText();
            String sql = "select service_order as idos, date_format(data_serviceorder,'%d/%m/%Y - %H:%i') as data,tipo,situacao,equipament,fault,type_service as tiposerv,tecnico,valor,idclient from tbservice_order where service_order=" + nos;
            //criar o ficheiro
            File relatorioespc = new File("E:\\PAP\\Reports\\relatorioespc.pdf");
            try {
                //verificar se o ficheiro ja existe 
                if (relatorioespc.exists()) {
                    if (relatorioespc.delete()) {
                        //System.out.println("Ficheiro anterior apagado");
                    } else {
                        //System.out.println("Erro ao apagar ficheiro anterior");
                    }
                }
                pst = conexao.prepareStatement(sql);
                rs = pst.executeQuery();

                // Obter a data atual
                java.util.Date dataAtual = new java.util.Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
                String dataFormatada = dateFormat.format(dataAtual);

                PDDocument document = new PDDocument();
                PDPage page = new PDPage(new PDRectangle(800, 1000)); // Tamanho de página personalizado
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                PDType0Font titleFont = PDType0Font.load(document, new File(
                        "E:\\PAP\\Fonts Rel\\arial\\arial_bold.ttf"));
                PDType0Font dataFont = PDType0Font.load(document, new File("E:\\PAP\\Fonts Rel\\arial\\arial.ttf"));
                PDType0Font fontNegrito = PDType0Font.load(document, new File("E:\\PAP\\Fonts Rel\\arial\\arial_bold.ttf"));

                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float yPosition = yStart;
                int rowsPerPage = 25; // Aumentei o número de linhas por página
                float rowHeight = 25f; // Tamanho da fonte dos dados dos clientes diminuído
                float tableHeight = rowHeight * rowsPerPage;

                // Defina o tamanho da fonte para o título (aumentado)
                contentStream.setFont(titleFont, 20); // Tamanho do título aumentado

                // Centralize o título horizontalmente
                float titleWidth = titleFont.getStringWidth("Ordem de Serviço Nº **") / 1000f * 20;
                float titleX = (page.getMediaBox().getWidth() - titleWidth) / 2;

                // Posicione verticalmente o título abaixo do topo da página
                float titleY = yStart - 20;

                // Desenhe o título "Relatório de Clientes"
                contentStream.beginText();
                contentStream.newLineAtOffset(titleX, titleY);
                contentStream.showText("Ordem de Serviço Nº " + nos);
                contentStream.endText();

                // Desenhe um separador abaixo do título
                float separatorY = titleY - 15; // Posição do separador entre o título e os dados
                contentStream.setLineWidth(0.7f); // Largura da linha
                contentStream.moveTo(margin, separatorY);
                contentStream.lineTo(page.getMediaBox().getWidth() - margin, separatorY);
                contentStream.stroke();

                // Cabeçalho da tabela
                float yPositionTable = yStart - 100; // Ajuste o espaço para o cabeçalho da tabela
                contentStream.setFont(fontNegrito, 12); // Tamanho da fonte dos dados dos clientes diminuído
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPositionTable);
                contentStream.showText("Data");
                contentStream.newLineAtOffset(100, 0); // Espaço reduzido entre colunas
                contentStream.showText("Tipo");
                contentStream.newLineAtOffset(100, 0); // Espaço aumentado entre colunas
                contentStream.showText("Situação");
                contentStream.newLineAtOffset(120, 0); // Espaço reduzido entre colunas
                contentStream.showText("Equipamento");
                contentStream.newLineAtOffset(120, 0); // Espaço aumentado entre colunas
                contentStream.showText("Defeito");
                contentStream.newLineAtOffset(120, 0); // Espaço aumentado entre colunas
                contentStream.showText("Serviço");
                contentStream.endText();

                // Adicionar a data de criação no canto superior direito
                float dateX = page.getMediaBox().getWidth() - margin - 165; // Posição X
                float dateY = yStart - 30; // Posição Y
                contentStream.setFont(dataFont, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(dateX, dateY);
                contentStream.showText("Data de Criação: " + dataFormatada);
                contentStream.endText();

                yPosition -= 120; // Atualiza a posição Y para os dados, deixando espaço após o título e cabeçalho

                int rowCount = 0;
                int pageNumber = 1;

                float idClienteX = margin;
                float idClienteY = separatorY - 30; // Posição Y para o ID do cliente

                float valorOSX = margin + 110; // Ajuste a posição X conforme necessário
                float valorOSY = separatorY - 30; // Posição Y para o valor da ordem de serviço

                float tecnicoX = margin + 220; // Ajuste a posição X conforme necessário
                float tecnicoY = separatorY - 30; // Posição Y para o nome do técnico

                while (rs.next()) {
                    int id = rs.getInt("idclient");
                    double valor = rs.getInt("valor");
                    NumberFormat format = NumberFormat.getCurrencyInstance(Locale.GERMANY); // Usando o Euro (€) como símbolo
                    String valorFormatado = format.format(valor);
                    String tecni = rs.getString("tecnico");
                    String data = rs.getString("data");
                    String tipoos = rs.getString("tipo");
                    String sit = rs.getString("situacao");
                    String equi = rs.getString("equipament");
                    String defeito = rs.getString("fault");
                    String servico = rs.getString("tiposerv");

                    // Adicione o ID do cliente
                    contentStream.setFont(dataFont, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(idClienteX, idClienteY);
                    contentStream.showText("ID do Cliente: " + id);
                    contentStream.endText();

                    // Adicione o valor da ordem de serviço
                    contentStream.setFont(dataFont, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(valorOSX, valorOSY);
                    contentStream.showText("Valor: " + valorFormatado);
                    contentStream.endText();

                    // Adicione o nome do técnico
                    contentStream.setFont(dataFont, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(tecnicoX, tecnicoY);
                    contentStream.showText("Técnico: " + tecni);
                    contentStream.endText();

                    // Preenche os dados dos clientes na tabela
                    contentStream.setFont(dataFont, 10); // Tamanho da fonte dos dados dos clientes diminuído
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(data);
                    contentStream.newLineAtOffset(100, 0); // Espaço reduzido entre colunas
                    contentStream.showText(tipoos);
                    contentStream.newLineAtOffset(100, 0); // Espaço aumentado entre colunas
                    contentStream.showText(sit);
                    contentStream.newLineAtOffset(120, 0); // Espaço reduzido entre colunas
                    contentStream.showText(equi);
                    contentStream.newLineAtOffset(120, 0); // Espaço aumentado entre colunas
                    contentStream.showText(defeito);
                    contentStream.newLineAtOffset(120, 0); // Espaço reduzido entre colunas
                    contentStream.showText(servico);
                    contentStream.endText();

                    yPosition -= rowHeight;

                    rowCount++;

                    // Verifica se precisa criar uma nova página para o restante dos dados
                    if (rowCount >= rowsPerPage) {
                        // Crie uma nova página
                        page = new PDPage(new PDRectangle(800, 1000)); // Tamanho de página personalizado
                        document.addPage(page);
                        contentStream.close(); // Feche o contentStream atual
                        contentStream = new PDPageContentStream(document, page);
                        yStart = page.getMediaBox().getHeight() - margin;
                        yPosition = yStart - 60; // Ajuste o espaço para o cabeçalho da tabela
                        rowCount = 0;
                        pageNumber++; // Incremente o número da página
                    }
                }

                // Feche o último contentStream
                contentStream.close();

                // Adicione o número da página no rodapé de todas as páginas
                for (PDPage p : document.getPages()) {
                    contentStream = new PDPageContentStream(document, p, PDPageContentStream.AppendMode.APPEND, true);
                    contentStream.setFont(dataFont, 10); // Tamanho da fonte para o número da página
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, margin + 2);
                    contentStream.showText("Página " + pageNumber);
                    contentStream.endText();

                    // Desenhe uma linha horizontal abaixo do número da página
                    contentStream.setLineWidth(0.7f); // Largura da linha
                    contentStream.moveTo(margin, margin - 5); // Posição inicial da linha
                    contentStream.lineTo(p.getMediaBox().getWidth() - margin, margin - 5); // Posição final da linha
                    contentStream.stroke(); // Desenhe a linha

                    contentStream.close();
                }

                document.save("E:\\PAP\\Reports\\relatorioespc.pdf");
                document.close();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                //System.out.println(e);
            }

            try {
                // Abre o explorador de arquivos na pasta do relatório
                Desktop.getDesktop().open(relatorioespc);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Não foi possível abrir o explorador de arquivos: " + e.getMessage());
                //System.out.println(e);
            }
        }
    }//GEN-LAST:event_btnosprintActionPerformed

    private void btnosaltActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnosaltActionPerformed
        // TODO add your handling code here:
        alterar_os();
    }//GEN-LAST:event_btnosaltActionPerformed

    private void btnosaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnosaddActionPerformed
        // TODO add your handling code here:
        emitiros();
    }//GEN-LAST:event_btnosaddActionPerformed

    private void btnospesqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnospesqActionPerformed
        // TODO add your handling code here:
        pesquisar_os();
    }//GEN-LAST:event_btnospesqActionPerformed

    private void btnoscleanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnoscleanActionPerformed
        // TODO add your handling code here:
        int confirma = JOptionPane.showConfirmDialog(null, "Tem a certeza que deseja limpar os campos?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            limpar();
            JOptionPane.showMessageDialog(null, "Campos limpos com sucesso.");
        }
    }//GEN-LAST:event_btnoscleanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnosadd;
    private javax.swing.JButton btnosalt;
    private javax.swing.JButton btnosclean;
    private javax.swing.JButton btnosdel;
    private javax.swing.JButton btnospesq;
    private javax.swing.JButton btnosprint;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JRadioButton rbtorc;
    private javax.swing.JRadioButton rbtos;
    private javax.swing.JTable tblcli;
    private javax.swing.JTextField txtcliid;
    private javax.swing.JTextField txtclipesq;
    private javax.swing.JTextField txtdata;
    private javax.swing.JTextField txtos;
    private javax.swing.JTextField txtosdef;
    private javax.swing.JTextField txtosequi;
    private javax.swing.JTextField txtosserv;
    private javax.swing.JTextField txtosteq;
    private javax.swing.JTextField txtosval;
    // End of variables declaration//GEN-END:variables
}
