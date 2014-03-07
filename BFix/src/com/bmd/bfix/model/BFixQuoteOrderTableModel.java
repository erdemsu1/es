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

package com.bmd.bfix.model;

import static ch.lambdaj.Lambda.*;

import static org.hamcrest.Matchers.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;


public class BFixQuoteOrderTableModel extends AbstractTableModel {

	private final static int CLORDID = 0;
	private final static int QUOTEORDERID = 1;
	private final static int SYMBOL = 2;
	private final static int ORDERIDBUY = 3;
	private final static int ORDERIDSELL = 4;
	private final static int BIDPX = 5;
	private final static int OFFERPX = 6;
	private final static int BIDSIZE = 7;
	private final static int OFFERSIZE = 8;
	private final static int TRANSACTTIME = 9;
	private final static int EXECTYPE = 10;
	
	
	private List<BFixQuoteOrder> orders;

	//private HashMap<Integer, Order> rowToOrder;
	//private HashMap<String, Integer> idToRow;
	//private HashMap<String, Order> idToOrder;

	private String[] headers;

	public BFixQuoteOrderTableModel() {
		orders = new ArrayList<BFixQuoteOrder>();
		//rowToOrder = new HashMap<Integer, Order>();
		//idToRow = new HashMap<String, Integer>();
		//idToOrder = new HashMap<String, Order>();

		headers = new String[] { "Kurum No", "Kotasyon No", "Sembol","Alış Emir No", "Satış Emir No", "Alış Fiyat", "Satış Fiyat",
				"Alış Miktar", "Satış Miktar", "Saat", "Durum" };
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void addOrder(BFixQuoteOrder order) {
		int row = orders.size();

		orders.add(order);

		//rowToOrder.put(Integer.valueOf(row), order);
		//idToRow.put(order.getClOrdID(), Integer.valueOf(row));
		//idToOrder.put(order.getClOrdID(), order);

		fireTableRowsInserted(row, row);
	}

	public void updateOrderByOrderID(BFixQuoteOrder order) {
		BFixQuoteOrder oldOrder = selectFirst(orders,
				having(on(BFixOrder.class).getOrderID(), equalTo(order.getQuoteID())));
		
		Integer row = orders.indexOf(oldOrder);
		if (row == -1)
			return;

		orders.set(row, order);
		
		//rowToOrder.put(row, order);
		//idToRow.put(order.getClOrdID(), row);
		//idToOrder.put(order.getClOrdID(), order);

		fireTableRowsUpdated(row.intValue(), row.intValue());
	}

	public void updateOrderByClOrdID(BFixQuoteOrder order) {

		BFixQuoteOrder oldOrder = selectFirst(orders,
				having(on(BFixOrder.class).getClOrdID(), equalTo(order.getClOrdID())));
		
		Integer row = orders.indexOf(oldOrder);
		if (row == -1)
			return;

		orders.set(row, order);
		
		//rowToOrder.put(row, order);
		//idToRow.put(order.getClOrdID(), row);
		//idToOrder.put(order.getClOrdID(), order);

		fireTableRowsUpdated(row.intValue(), row.intValue());
	}

	public void addID(BFixOrder order, String newID) {
		//idToOrder.put(newID, order);
	}

	public BFixQuoteOrder getOrderByClOrdID(String clOrdID) {
		BFixQuoteOrder order = selectFirst(orders,
				having(on(BFixQuoteOrder.class).getClOrdID(), equalTo("000"+clOrdID)));
		return order;
	}
	public BFixQuoteOrder getOrderByQuoteID(String orderID) {
		BFixQuoteOrder order = selectFirst(orders,having(on(BFixQuoteOrder.class).getQuoteID(), equalTo(orderID)));
		return order;
	}

	public BFixQuoteOrder getOrder(int row) {
		return orders.get(Integer.valueOf(row));
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
	}

	public Class<String> getColumnClass(int columnIndex) {
		return String.class;
	}

	public int getRowCount() {
		return orders.size();
	}

	public int getColumnCount() {
		return headers.length;
	}

	public String getColumnName(int columnIndex) {
		return headers[columnIndex];
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		BFixQuoteOrder order = orders.get(Integer.valueOf(rowIndex));
		switch (columnIndex) {
		
		case CLORDID:
			return order.getClOrdID();
		case QUOTEORDERID:
			return order.getQuoteID();
		
		case SYMBOL:
			return order.getSymbol();
		case ORDERIDBUY:
			return order.getOrderIDBuy();
		case ORDERIDSELL:
			return order.getOrderIDSell();
		case OFFERSIZE:
			return order.getOfferSize();
		case OFFERPX:
			return order.getOfferPx();
		case BIDSIZE:
			return order.getBidSize();
		case BIDPX:
			return order.getBidPx();
		/*case TRANSACTTIME:
			return new SimpleDateFormat(
                    "dd/MM/yyyy hh:mm:ss.SSS").format(order.getTransactTime());
		*/
		case EXECTYPE:
			if(order.getExecType()=='0')
			return "Kabul Edildi";
			else if(order.getExecType()=='4')
				return "İptal Edildi";
			else if(order.getExecType()=='5')
				return "Değitirildi";
			else if(order.getExecType()=='6')
				return "İptal İstendi";
			else if(order.getExecType()=='8')
				return "Reddedildi";
			else if(order.getExecType()=='F' && order.getQuoteStatus()=='2')
				return "Gerçekleşti";
			else if(order.getExecType()=='F' && order.getQuoteStatus()=='1')
				return "Kısmi Gerçekleşti";
			else if(order.getExecType()=='C')
				return "Süresi Doldu";
			else if(order.getExecType()=='I' && order.getQuoteStatus()=='0')
				return "Durum Kabul";
			else if(order.getExecType()=='I' && order.getQuoteStatus()=='4')
				return "Durum İptal";
			else if(order.getExecType()=='I' && order.getQuoteStatus()=='5')
				return "Durum Değiştirildi";
			else if(order.getExecType()=='I' && order.getQuoteStatus()=='1')
				return "Durum Kısmi";
			else if(order.getExecType()=='I' && order.getQuoteStatus()=='2')
				return "Durum Gerçekleşti";
			else if(order.getExecType()=='I' && order.getQuoteStatus()=='8')
				return "Durum Reddedildi";

		}
		return "";
	}
}
