package com.bmd.bfix.process;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bmd.bfix.BFixApplication;
import com.bmd.bfix.BFix;
import com.bmd.bfix.model.BFixOrder;
import com.bmd.bfix.model.BFixOrderTableModel;
import com.bmd.bfix.model.BFixQuoteOrder;
import com.bmd.bfix.model.BFixQuoteOrderTableModel;

import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.field.TransactTime;

public class BFixReadOrder implements Runnable {

	private BFixApplication bFixAplication;
	private BFixOrderTableModel orderTableModel;
	private BFixQuoteOrderTableModel quoteOrderTableModel;
	//public SessionID sessionID;

	public BFixReadOrder(BFixApplication bFixAplication, BFixOrderTableModel orderTableModel, BFixQuoteOrderTableModel quoteOrderTableModel) {
		//sessionID = ssessionID;
		this.orderTableModel = orderTableModel;
		this.quoteOrderTableModel = quoteOrderTableModel;
		this.bFixAplication = bFixAplication;
	}

	public void run() {
		

		try {
			while (true) {
				if (bFixAplication.connectionStatus == 1) {
					Message message = getRequest();
					if (message != null){
						
						Session.sendToTarget(message, bFixAplication.activeSessionID);
						
					}
				}
				//Thread.sleep(1);
			}
		} catch (SessionNotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	@SuppressWarnings("finally")
	public Message getRequest() {
		Message message = null;
		CallableStatement cs = null;
		BFixDbOperation bfixDbOperation = new BFixDbOperation();
		Connection conn = bfixDbOperation.createConnection();
		try {

			if (conn == null) {
				System.out.println("Read Request Db Bağlantı Hatası! (3001)");
			} else {
				cs = conn.prepareCall("{call uop14.senkron_kanal.istek_al_bfix(?, ?, ?, ?)}");
				cs.setInt(1, BFix.pcNo); 
				cs.setInt(2, 1);
				cs.registerOutParameter(3, Types.NUMERIC);
				cs.registerOutParameter(4, Types.VARCHAR);
				cs.executeQuery();
				if (cs.getInt(3) > 0)
					message = decodeOrder(cs.getInt(3), cs.getString(4));
				
			}
		} catch (Exception e) {
			System.out.println("Read Request ExecuteQuery Hatası! (3002)");
			e.printStackTrace();
		} finally {
			try {
				if (!conn.isClosed())
					conn.close();
			} catch (SQLException e) {
		
				e.printStackTrace();
			}
			return message;
		}
	}

	public Message decodeOrder(int RequestType, String ReqPackage) {

		String[] values = null;
		values = ReqPackage.split(" ");

		if (RequestType == 1) { // Yeni Emir
			BFixOrder order = new BFixOrder();

			order.setClOrdID(values[1]);
			order.setSymbol(values[3]);
			order.setSymbolSfx(values[4]);
			order.setSide((values[5].equals("A")) ? '1' : (values[5]
					.equals("S")) ? '2' : '5');
			order.setOrdType((values[6].equals("A")) ? '1' : '2'); // kontrol
																	// edilecek
			order.setTimeInForce((values[10].equals("00")) ? '0' : '6');
			if (order.getTimeInForce() == '6')
				order.setExpireDate(new SimpleDateFormat(
	                    "yyyyMMdd").format(new Date()));
			if (order.getOrdType() == '2')
				order.setPrice(new Double(values[8]));
			if (!values[6].equals("O") && !values[6].equals("D"))
				order.setOrderQty(new Double(values[9]));
			else if (values[6].equals("D"))
				order.setCashOrderQty(new Double(values[14]));
			order.setAccount(values[11]);
			// order.setNoPartyIDs(1); //kontrol edilecek
			// order.setPartyID("1");
			// order.setPartyRole(1);
			order.setAccountType((values[7].equals("M")) ? 1 : (values[7]
					.equals("F")) ? 9 : 2);
			order.setTransactTime(new TransactTime(new Date()).getValue());
			order.setHandlInst('1');
			orderTableModel.addOrder(order);
			return order.NewOrderFill();

		} else if (RequestType == 2) { // Emir Değiştirme
			BFixOrder order = new BFixOrder();

			order.setClOrdID(values[1]);
			order.setSymbol(values[3]);
			order.setSymbolSfx(values[4]);
			order.setSide((values[5].equals("A")) ? '1' : (values[5]
					.equals("S")) ? '2' : '5');
			order.setOrdType((values[6].equals("A")) ? '1' : '2'); // kontrol
																	// edilecek
			order.setTimeInForce((values[10].equals("00")) ? '0' : '6');
			if (order.getTimeInForce() == '6')
				order.setExpireDate(new SimpleDateFormat(
	                    "yyyyMMdd").format(new Date()));
			if (order.getOrdType() == '2'){
				order.setPrice(new Double(values[8]));
				order.setPrice2(new Double(values[17]));
			}
			if (!values[6].equals("O") && !values[6].equals("D")){
				order.setOrderQty(new Double(values[9]));
				order.setOrderQty2(new Double(values[18]));
			}
			else if (values[6].equals("D"))
				order.setCashOrderQty(new Double(values[14]));
			//order.setAccount(values[11]);
			// order.setNoPartyIDs(1); //kontrol edilecek
			// order.setPartyID("1");
			// order.setPartyRole(1);
			order.setAccountType((values[7].equals("M")) ? 1 : (values[7]
					.equals("F")) ? 9 : 2);
			order.setTransactTime(new TransactTime(new Date()).getValue());
			order.setHandlInst('1');
			
			order.setOrderID(values[16]);
			
			
		
			
			orderTableModel.addOrder(order);
			return order.OrderCancelReplaceFill();
			
		} else if (RequestType == 3) { // Emir Bölme
			return null;
		} else if (RequestType == 4) { // Emir İptal
			BFixOrder order = new BFixOrder();
			order.setOrderID(values[1]);
			order.setSymbol(values[2]);
			order.setSymbolSfx(values[3]);
			order.setSide((values[4].equals("A")) ? '1' : (values[4].equals("S")) ? '2' : '5');
			order.setTransactTime(new TransactTime(new Date()).getValue());
			return order.OrderCancelFill();
		} else if (RequestType == 11) { // Kotasyon Yeni Emir
			BFixQuoteOrder quoteOrder = new BFixQuoteOrder();
			quoteOrder.setClOrdID(values[1]);
			quoteOrder.setSymbol(values[3]);
			quoteOrder.setSymbolSfx(values[4]);
			quoteOrder.setBidSize(new Double(values[5]));
			quoteOrder.setBidPx(new Double(values[6]));
			quoteOrder.setOfferPx(new Double(values[7]));
			quoteOrder.setOfferSize(new Double(values[8]));
			//quoteOrder.setQuoteID(values[9]);
			quoteOrder.setAccount(values[10]);
			quoteOrder.setAccountType((values[11].equals("M")) ? 1 : (values[11].equals("F")) ? 9 : 2);
			quoteOrder.setPartyID(values[12]);
			quoteOrderTableModel.addOrder(quoteOrder);
			
			return quoteOrder.NewQuoteOrderFill();
		}else if (RequestType == 12) { // Kotasyon Emir Değiştirme
			BFixQuoteOrder quoteOrder = new BFixQuoteOrder();
			quoteOrder.setClOrdID(values[1]);
			quoteOrder.setQuoteID(values[2]);
			quoteOrder.setBidSize2(new Double(values[4]));
			quoteOrder.setBidPx2(new Double(values[5]));
			quoteOrder.setOfferPx2(new Double(values[6]));
			quoteOrder.setOfferSize2(new Double(values[7]));
			quoteOrder.setBidSize(new Double(values[8]));
			quoteOrder.setBidPx(new Double(values[9]));
			quoteOrder.setOfferPx(new Double(values[10]));
			quoteOrder.setOfferSize(new Double(values[11]));
			
			
			//quoteOrder.setClOrdID(values[1]);
			//quoteOrder.setSymbol(values[3]);
			//quoteOrder.setSymbolSfx(values[4]);
			
			
			//quoteOrder.setAccount(values[10]);
			//quoteOrder.setAccountType((values[11].equals("M")) ? 1 : (values[11].equals("F")) ? 9 : 2);
			//quoteOrder.setPartyID(values[12]);
			quoteOrderTableModel.addOrder(quoteOrder);
			return quoteOrder.ReplaceQuoteOrderFill();
		}else if (RequestType == 5) { // Emir Durum Sorgusu
			BFixOrder order = new BFixOrder();
			order.setOrderID(values[1]);
			order.setSymbol(values[2]);
			order.setSymbolSfx(values[3]);
			order.setSide((values[4].equals("A")) ? '1' : (values[4].equals("S")) ? '2' : '5');
			order.setTransactTime(new TransactTime(new Date()).getValue());
			return order.OrderStatusRequestFill();
		}
		return null;
	}
}
