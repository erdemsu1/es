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
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.bmd.bfix.BFixApplication;
import com.bmd.bfix.model.BFixQuoteOrder;
import com.bmd.bfix.model.BFixQuoteOrderTableModel;
import com.bmd.bfix.process.BFixMessageParse;

public class BFixQuoteTable extends JTable implements MouseListener {
	private transient BFixApplication application;

	public BFixQuoteTable(BFixQuoteOrderTableModel bFixQuoteOrderTableModel) {
		super(bFixQuoteOrderTableModel);
		this.application = application;
		addMouseListener(this);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}

	public Component prepareRenderer(TableCellRenderer renderer, int row,
			int column) {
		BFixQuoteOrder quoteOrder = (BFixQuoteOrder) ((BFixQuoteOrderTableModel) dataModel)
				.getOrder(row);

		int open = 0;
		int executed = 0;
		boolean rejected = false;
		boolean canceled = false;

		DefaultTableCellRenderer r = (DefaultTableCellRenderer) renderer;
		r.setForeground(Color.black);

		if (quoteOrder.getQuoteStatus() == '0') 
			r.setBackground(Color.green);
		else if (quoteOrder.getQuoteStatus() == '5') 
			r.setBackground(Color.red);
		else
			r.setBackground(Color.white);;

		return super.prepareRenderer(renderer, row, column);
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			int row = rowAtPoint(e.getPoint());
			this.setRowSelectionInterval(row, row);
			BFixQuoteOrder quoteOrder = (BFixQuoteOrder) ((BFixQuoteOrderTableModel) dataModel)
					.getOrder(row);
			BFixMessageParse mespars = new BFixMessageParse();
			JOptionPane.showMessageDialog(null,
					mespars.FixMessageParseByString(quoteOrder.getMessage()));
			
		}
		// application.cancel(order);
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
}
