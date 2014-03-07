/*******************************************************************************
 * Copyright (c) quickfixengine.org  All rights reserved. 
 * 
 * This file is part of the QuickFIX FIX Engine 
 * 
 * This file may be distributed under the terms of the quickfixengine.org 
 * license as defined by quickfixengine.org and appearing in the file 
 * LICENSE included in the packaging of this file. 
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING 
 * THE WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 * 
 * See http://www.quickfixengine.org/LICENSE for licensing information. 
 * 
 * Contact ask@quickfixengine.org if any conditions of this licensing 
 * are not clear to you.
 ******************************************************************************/

package com.bmd.bfix.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.bmd.bfix.BFixApplication;
import com.bmd.bfix.model.BFixOrder;
import com.bmd.bfix.model.BFixOrderTableModel;
import com.bmd.bfix.process.BFixMessageParse;

public class BFixOrderTable extends JTable implements MouseListener {
    private transient BFixApplication application;
    private final JPopupMenu popupMenu = new JPopupMenu();
    public BFixOrderTable(BFixOrderTableModel orderTableModel) {
        super(orderTableModel);
        this.application = application;
        addMouseListener(this);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    public Component prepareRenderer(TableCellRenderer renderer,
                                     int row, int column) {
        BFixOrder order = (BFixOrder)((BFixOrderTableModel)dataModel).getOrder(row);

        int open = 0;
        int executed = 0;
        boolean rejected = false;
        boolean canceled = false;

        DefaultTableCellRenderer r = (DefaultTableCellRenderer)renderer;
        r.setForeground(Color.black);

        if(order.getExecType() == '0' && order.getOrdStatus() == '0') //gönderildi.
            r.setBackground(Color.white);
        else if(order.getExecType() == 'I')  //durum sorgusu
            r.setBackground(Color.PINK);
        else if(order.getOrdStatus() == '1') //kısmen gerçekleşti
            r.setBackground(Color.yellow);
        else if(order.getOrdStatus() == '2') //tamamen gerçekleşti
            r.setBackground(Color.green);
        else if(order.getOrdStatus() == '4') //iptal edildi
            r.setBackground(Color.lightGray);
        else if(order.getOrdStatus() == '5') //değiştirildi
            r.setBackground(Color.gray);
        else if(order.getOrdStatus() == '6') //iptal istendi
            r.setBackground(Color.darkGray);
        else if(order.getOrdStatus() == '8') //reddedildi
            r.setBackground(Color.red);
        else if(order.getOrdStatus() == 'C') //geçerlilik süresi doldu
            r.setBackground(Color.magenta);
        else{
        	r.setBackground(Color.black);
        	r.setForeground(Color.white);
        }
        
        

        return super.prepareRenderer(renderer, row, column);
    }

    public void mouseClicked(MouseEvent e) {

        if(e.getButton() == MouseEvent.BUTTON3){
        int row = rowAtPoint(e.getPoint());
		this.setRowSelectionInterval(row, row);
        BFixOrder order = (BFixOrder)((BFixOrderTableModel)dataModel).getOrder(row);
        BFixMessageParse mespars = new BFixMessageParse();
        JOptionPane.showMessageDialog(null,mespars.FixMessageParseByString(order.getMessage()));
        }
        //application.cancel(order);
    }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
}
