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
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;


public class BFixOrderTableModel extends AbstractTableModel {

	private final static int CLORDID = 0;
	private final static int ORDERID = 1;
	private final static int SYMBOL = 2;
	private final static int SIDE = 3;
	private final static int PRICE = 4;
	private final static int ORDERQUANTITY = 5;
	private final static int AMOUNT = 6;
	private final static int TIMEINFORCE = 7;
	private final static int TRANSACTTIME = 8;
	private final static int EXECTYPE = 9;


	private LinkedList<BFixOrder> orders;

	//private HashMap<Integer, Order> rowToOrder;
	//private HashMap<String, Integer> idToRow;
	//private HashMap<String, Order> idToOrder;

	private String[] headers;

	public BFixOrderTableModel() {
		orders = new LinkedList<BFixOrder>();
		//rowToOrder = new HashMap<Integer, Order>();
		//idToRow = new HashMap<String, Integer>();
		//idToOrder = new HashMap<String, Order>();

		headers = new String[] { "Kurum No", "Emir No", "Sembol", "İşlem", "Fiyat",
				"Miktar", "Tutar", "Süre", "Saat", "Durum" };
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void addOrder(BFixOrder order) {
		int row = orders.size();

		orders.add(order);
		
		//rowToOrder.put(Integer.valueOf(row), order);
		//idToRow.put(order.getClOrdID(), Integer.valueOf(row));
		//idToOrder.put(order.getClOrdID(), order);

		fireTableRowsInserted(row, row);
	}

	public void updateOrderByOrderID(BFixOrder order) {
		BFixOrder oldOrder = selectFirst(orders,
				having(on(BFixOrder.class).getOrderID(), equalTo(order.getOrderID())));
		
		Integer row = orders.indexOf(oldOrder);
		if (row == -1)
			return;

		orders.set(row, order);
		
		//rowToOrder.put(row, order);
		//idToRow.put(order.getClOrdID(), row);
		//idToOrder.put(order.getClOrdID(), order);

		fireTableRowsUpdated(row.intValue(), row.intValue());
	}

	public void updateOrderByClOrdID(BFixOrder order) {

		BFixOrder oldOrder = selectFirst(orders,
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

	public BFixOrder getOrderByClOrdID(String clOrdID) {
		BFixOrder order = selectFirst(orders,
				having(on(BFixOrder.class).getClOrdID(), equalTo(clOrdID)));
		return order;
	}
	public BFixOrder getOrderByOrderID(String orderID) {
		BFixOrder order = selectFirst(orders,having(on(BFixOrder.class).getOrderID(), equalTo(orderID)));
		return order;
	}

	public BFixOrder getOrder(int row) {
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
		BFixOrder order = orders.get(Integer.valueOf(rowIndex));
		switch (columnIndex) {
		case CLORDID:
			return order.getClOrdID();
		case ORDERID:
			return order.getOrderID();
		case SYMBOL:
			return order.getSymbol();
		case SIDE:
			if(order.getSide()=='1' )
				return "Alış";
			else if(order.getSide()=='2' )
			return "Satış";
			else if(order.getSide()=='5' )
				return "Açığa Satış";
			else
				return "";
		case PRICE:
			return order.getPrice();
		case ORDERQUANTITY:
			return order.getOrderQty();
		case AMOUNT:
			return order.getOrderQty() * order.getPrice();
		case TIMEINFORCE:
			if(order.getTimeInForce()=='0')
			return "Seanslık";
			else if(order.getTimeInForce()=='6')
				return "Günlük";
			else if(order.getTimeInForce()=='3')
				return "KİE";
			else
				return "";
		case TRANSACTTIME:
			return new SimpleDateFormat(
                    "dd/MM/yyyy hh:mm:ss.SSS").format(order.getTransactTime());
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
			else if(order.getExecType()=='F' && order.getOrdStatus()=='2')
				return "Gerçekleşti";
			else if(order.getExecType()=='F' && order.getOrdStatus()=='1')
				return "Kısmi Gerçekleşti";
			else if(order.getExecType()=='C')
				return "Süresi Doldu";
			else if(order.getExecType()=='I' && order.getOrdStatus()=='0')
				return "Durum Kabul";
			else if(order.getExecType()=='I' && order.getOrdStatus()=='4')
				return "Durum İptal";
			else if(order.getExecType()=='I' && order.getOrdStatus()=='5')
				return "Durum Değiştirildi";
			else if(order.getExecType()=='I' && order.getOrdStatus()=='1')
				return "Durum Kısmi";
			else if(order.getExecType()=='I' && order.getOrdStatus()=='2')
				return "Durum Gerçekleşti";
			else if(order.getExecType()=='I' && order.getOrdStatus()=='8')
				return "Durum Reddedildi";
		
		
		
		}
		return "";
	}
}
class MyComparator implements Comparator {
	  protected boolean isSortAsc;

	  public MyComparator( boolean sortAsc) {
	    isSortAsc = sortAsc;
	  }

	  public int compare(Object o1, Object o2) {
	    if (!(o1 instanceof Integer) || !(o2 instanceof Integer))
	      return 0;
	    Integer s1 = (Integer) o1;
	    Integer s2 = (Integer) o2;
	    int result = 0;
	    result = s1.compareTo(s2);
	    if (!isSortAsc)
	      result = -result;
	    return result;
	  }

	  public boolean equals(Object obj) {
	    if (obj instanceof MyComparator) {
	      MyComparator compObj = (MyComparator) obj;
	      return compObj.isSortAsc == isSortAsc;
	    }
	    return false;
	  }
	}
