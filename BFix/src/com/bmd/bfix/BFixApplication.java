package com.bmd.bfix;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.SwingUtilities;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.DoNotSend;
import quickfix.FieldConvertError;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.field.Account;
import quickfix.field.AccountType;
import quickfix.field.BeginString;
import quickfix.field.BidPx;
import quickfix.field.BidSize;
import quickfix.field.BusinessRejectReason;
import quickfix.field.ClOrdID;
import quickfix.field.DeliverToCompID;
import quickfix.field.ExecID;
import quickfix.field.ExecType;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LeavesQty;
import quickfix.field.MsgSeqNum;
import quickfix.field.MsgType;
import quickfix.field.OfferPx;
import quickfix.field.OfferSize;
import quickfix.field.OrdRejReason;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.Password;
import quickfix.field.Price;
import quickfix.field.QuoteID;
import quickfix.field.QuoteStatus;
import quickfix.field.RefMsgType;
import quickfix.field.RefSeqNum;
import quickfix.field.ResetSeqNumFlag;
import quickfix.field.SenderCompID;
import quickfix.field.SessionRejectReason;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.SymbolSfx;
import quickfix.field.TargetCompID;
import quickfix.field.Text;
import quickfix.field.TimeInForce;
import quickfix.field.TradSesStatus;
import quickfix.field.TradingSessionID;
import quickfix.field.TransactTime;
import quickfix.field.TrdMatchID;
import quickfix.field.Username;
import quickfix.fix50.TradingSessionStatus;

import com.bmd.bfix.model.BFixExecutionTableModel;
import com.bmd.bfix.model.BFixOrder;
import com.bmd.bfix.model.BFixOrderTableModel;
import com.bmd.bfix.model.BFixQuoteOrder;
import com.bmd.bfix.model.BFixQuoteOrderTableModel;
import com.bmd.bfix.model.BFixTradingSessionStatus;
import com.bmd.bfix.observable.ObservableBFixLogon;
import com.bmd.bfix.observable.ObservableBFixTradingStatus;
import com.bmd.bfix.process.BFixAprovalOrder;
import com.bmd.bfix.process.BFixCancelAprovalOrder;
import com.bmd.bfix.process.BFixCancelOrder;
import com.bmd.bfix.process.BFixErrorOrder;
import com.bmd.bfix.process.BFixExecuteOrder;
import com.bmd.bfix.process.BFixQuoteAprovalOrder;
import com.bmd.bfix.process.BFixReadOrder;
import com.bmd.bfix.process.BFixReplaceAprovalOrder;
import com.bmd.bfix.process.BFixTradingSessionStatusChange;
import com.bmd.bfix.ui.BFixPanel;

public class BFixApplication implements Application {

	private DefaultMessageFactory messageFactory = new DefaultMessageFactory();
	private BFixOrderTableModel orderTableModel = null;
	private BFixQuoteOrderTableModel quoteOrderTableModel = null;
	private BFixExecutionTableModel bFixExecutionTableModel = null;
	
	private ObservableLogon observableLogon = new ObservableLogon();
	private ObservableOrder observableOrder = new ObservableOrder();
	private ObservableBFixLogon observableBFixLogon = new ObservableBFixLogon();
	public static ObservableBFixTradingStatus observableBFixTradingStatus = new ObservableBFixTradingStatus();
	
	private boolean isAvailable = true;
	private boolean isMissingField;
	private boolean resetSeqNumFlag = false;
	public static SessionID activeSessionID;
	public static LinkedList<BFixTradingSessionStatus> bFixTradingSessionStatusList;

	
	public static int connectionStatus = 0;


	public BFixApplication(BFixOrderTableModel orderTableModel, BFixExecutionTableModel bFixExecutionTableModel, BFixQuoteOrderTableModel quoteOrderTableModel) {
		this.orderTableModel = orderTableModel;
		this.bFixExecutionTableModel = bFixExecutionTableModel;
		this.quoteOrderTableModel = quoteOrderTableModel;

	}

	@Override
	public void onCreate(SessionID sessionId) {
		connectionStatus = -1;
		BFixPanel.logonStatus(connectionStatus);
	}

	@Override
	public void onLogon(SessionID sessionId) {
		//setActiveSessionID(sessionId);
		connectionStatus = 1;
		(new Thread(new BFixReadOrder(this, orderTableModel, quoteOrderTableModel))).start();
		BFixPanel.logonStatus(connectionStatus);
	}

	@Override
	public void onLogout(SessionID sessionId) {
		connectionStatus = 0;
		BFixPanel.logonStatus(connectionStatus);
		//setActiveSessionID(null);
	}

	public void onLogonBreak() {
		connectionStatus = 0;
		BFixPanel.logonStatus(connectionStatus);
	}

	public void toAdmin(Message message, SessionID sessionId) {
		final Message.Header header = message.getHeader();
		try {
			if (header.getField(new MsgType()).valueEquals(MsgType.LOGON)) {
				
				
				message.setField(new Username(BFix.settings.getString(sessionId,"Username")));
				message.setField(new Password(BFix.settings.getString(sessionId,"Password")));
				message.setField(new ResetSeqNumFlag(resetSeqNumFlag));
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
		BFixPanel.txaConsole.setText("<-- " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date())+ " - " + message.toString() + "\n" + BFixPanel.txaConsole.getText());

	}

	public void toApp(quickfix.Message message, SessionID sessionID)
			throws DoNotSend {
		BFixPanel.txaConsole.setText("<-- " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date())+ " - " + message.toString() + "\n" + BFixPanel.txaConsole.getText());

	}

	public void fromAdmin(quickfix.Message message, SessionID sessionID)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
			RejectLogon {
		//BFixPanel.txaConsole.setText("--> " +  new SimpleDateFormat("HH:mm:ss.SSS").format(new Date())+ " - " + message.toString() + "\n" + BFixPanel.txaConsole.getText());
	}

	public void fromApp(quickfix.Message message, SessionID sessionID)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
			UnsupportedMessageType {
		try {
			//BFixPanel.txaConsole.setText("--> " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date())+ " - " + message.toString() + "\n" + BFixPanel.txaConsole.getText());
			SwingUtilities.invokeLater(new MessageProcessor(message, sessionID));

		} catch (Exception e) {
		}
	}
	public static String getActiveUsername(){
		try {
		 return	BFix.settings.getString(activeSessionID,"Username");
		} catch (ConfigError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (FieldConvertError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	private void sendBusinessReject(Message message, int rejectReason,
			String rejectText) throws FieldNotFound, SessionNotFound {
		Message reply = createMessage(message, MsgType.BUSINESS_MESSAGE_REJECT);
		reverseRoute(message, reply);
		String refSeqNum = message.getHeader().getString(MsgSeqNum.FIELD);
		reply.setString(RefSeqNum.FIELD, refSeqNum);
		reply.setString(RefMsgType.FIELD,
				message.getHeader().getString(MsgType.FIELD));
		reply.setInt(BusinessRejectReason.FIELD, rejectReason);
		reply.setString(Text.FIELD, rejectText);
		Session.sendToTarget(reply);
	}

	private void sendSessionReject(Message message, int rejectReason)
			throws FieldNotFound, SessionNotFound {
		Message reply = createMessage(message, MsgType.REJECT);
		reverseRoute(message, reply);
		String refSeqNum = message.getHeader().getString(MsgSeqNum.FIELD);
		reply.setString(RefSeqNum.FIELD, refSeqNum);
		reply.setString(RefMsgType.FIELD,
				message.getHeader().getString(MsgType.FIELD));
		reply.setInt(SessionRejectReason.FIELD, rejectReason);
		Session.sendToTarget(reply);
	}

	private Message createMessage(Message message, String msgType)
			throws FieldNotFound {
		return messageFactory.create(
				message.getHeader().getString(BeginString.FIELD), msgType);
	}

	private void reverseRoute(Message message, Message reply)
			throws FieldNotFound {
		reply.getHeader().setString(SenderCompID.FIELD,
				message.getHeader().getString(TargetCompID.FIELD));
		reply.getHeader().setString(TargetCompID.FIELD,
				message.getHeader().getString(SenderCompID.FIELD));
	}
	

	private void executionReport(Message message, SessionID sessionID)
			throws FieldNotFound {

		
		BFixOrder order = null;
		BFixOrder orginalOrder = null;
		
		if(message.getField(new ExecType()).valueEquals('F') || message.getField(new ExecType()).valueEquals('4')|| message.getField(new ExecType()).valueEquals('6')|| message.getField(new ExecType()).valueEquals('I'))
			orginalOrder = orderTableModel.getOrderByOrderID(message.getField(new OrderID()).getValue());
		else
			orginalOrder = orderTableModel.getOrderByClOrdID(message.getField(new ClOrdID()).getValue());
		

		if (orginalOrder == null) {
			order = new BFixOrder();
		}else{
			order = (BFixOrder) orginalOrder.clone();
		}
		
		
		order.setExecType(message.getField(new ExecType()).getValue());
		order.setExecID(message.getField(new ExecID()).getValue());
		order.setOrdStatus(message.getField(new OrdStatus()).getValue());
		order.setMsgSeqNum(message.getHeader().getField(new MsgSeqNum()).getValue());
		order.setMessage(message.toString()); 

		if (order.getExecType()=='0') { //İletim Mesajı
			order.setOrderID(message.getField(new OrderID()).getValue());
			//order.setExecID(message.getField(new ExecID()).getValue());
			order.setOrdStatus(message.getField(new OrdStatus()).getValue());
			(new Thread(new BFixAprovalOrder(order, this, orderTableModel))).start();
		} else if (order.getExecType()=='F') { //Gerçekleşme Mesajı
			if (orginalOrder == null) {
				
				order.setSymbol(message.getField(new Symbol()).getValue());
				order.setSymbolSfx(message.getField(new SymbolSfx()).getValue());
				order.setSide(message.getField(new Side()).getValue());
				order.setTimeInForce(message.getField(new TimeInForce()).getValue());
				order.setPrice(message.getField(new Price()).getValue());
				order.setOrderQty(message.getField(new OrderQty()).getValue());
				order.setAccount(message.getField(new Account()).getValue());
			}
			//order.setExecID(message.getField(new ExecID()).getValue());
			order.setTransactTime(message.getField(new TransactTime()).getValue());
			order.setLastPx(message.getField(new LastPx()).getValue());
			order.setLeavesQty(message.getField(new LeavesQty()).getValue());
			order.setLastQty(message.getField(new LastQty()).getValue());
			order.setTrdMatchID(message.getField(new TrdMatchID()).getValue());
			order.setOrderID(message.getField(new OrderID()).getValue());
			(new Thread(new BFixExecuteOrder(order, this, bFixExecutionTableModel))).start();
		} else if (order.getExecType()=='8') {  //Reddedildi Mesajı
			if (orginalOrder == null) {
				
				order.setSymbol(message.getField(new Symbol()).getValue());
				order.setSymbolSfx(message.getField(new SymbolSfx()).getValue());
				order.setSide(message.getField(new Side()).getValue());
				order.setTimeInForce(message.getField(new TimeInForce()).getValue());
				order.setPrice(message.getField(new Price()).getValue());
				order.setOrderQty(message.getField(new OrderQty()).getValue());
				order.setAccount(message.getField(new Account()).getValue());
			}
			//order.setTrdMatchID(message.getField(new TrdMatchID()).getValue());
			//order.setExecID(message.getField(new ExecID()).getValue());
			order.setOrdRejReason(message.getField(new OrdRejReason()).getValue());
			order.setDescription(order.getOrdRejReason() + " - " + message.getField(new Text()).getValue());
			order.setMessage(message.toString());
			(new Thread(new BFixErrorOrder(order, this, orderTableModel))).start();
		}else if (order.getExecType()=='6') {  //İptal İletildi Mesajı

			if (orginalOrder == null) {
				
				order.setSymbol(message.getField(new Symbol()).getValue());
				order.setSymbolSfx(message.getField(new SymbolSfx()).getValue());
				order.setSide(message.getField(new Side()).getValue());
				order.setTimeInForce(message.getField(new TimeInForce()).getValue());
				order.setPrice(message.getField(new Price()).getValue());
				order.setOrderQty(message.getField(new OrderQty()).getValue());
				order.setAccount(message.getField(new Account()).getValue());
			}
			order.setMessage(message.toString());
			order.setOrderID(message.getField(new OrderID()).getValue());
			//order.setTrdMatchID(message.getField(new TrdMatchID()).getValue());
			//order.setExecID(message.getField(new ExecID()).getValue());
			//order.setDescription(message.getField(new OrdRejReason()).getValue() + " - " + message.getField(new Text()).getValue());
			(new Thread(new BFixCancelAprovalOrder(order, this, orderTableModel))).start();

			
		}else if (order.getExecType()=='4') {  //İptal Gerçekleşti Mesajı

			if (orginalOrder == null) {
				
				order.setSymbol(message.getField(new Symbol()).getValue());
				order.setSymbolSfx(message.getField(new SymbolSfx()).getValue());
				order.setSide(message.getField(new Side()).getValue());
				//order.setTimeInForce(message.getField(new TimeInForce()).getValue());
				order.setPrice(message.getField(new Price()).getValue());
				order.setOrderQty(message.getField(new OrderQty()).getValue());
				//order.setAccount(message.getField(new Account()).getValue());
				order.setTransactTime(message.getField(new TransactTime()).getValue());
			}
			//order.setTrdMatchID(message.getField(new TrdMatchID()).getValue());
			//order.setExecID(message.getField(new ExecID()).getValue());
			order.setOrderID(message.getField(new OrderID()).getValue());
			order.setDescription(message.getField(new Text()).getValue());
			(new Thread(new BFixCancelOrder(order, this, bFixExecutionTableModel))).start();

			
		}else if (order.getExecType()=='5') {  //Değiştirildi İletim Mesajı

			if (orginalOrder == null) {
				
				order.setSymbol(message.getField(new Symbol()).getValue());
				order.setSymbolSfx(message.getField(new SymbolSfx()).getValue());
				order.setSide(message.getField(new Side()).getValue());
				order.setTimeInForce(message.getField(new TimeInForce()).getValue());
				order.setPrice(message.getField(new Price()).getValue());
				order.setOrderQty(message.getField(new OrderQty()).getValue());
				order.setAccount(message.getField(new Account()).getValue());
			}
			order.setMessage(message.toString());
			order.setOrderID(message.getField(new OrderID()).getValue());
			//order.setTrdMatchID(message.getField(new TrdMatchID()).getValue());
			//order.setExecID(message.getField(new ExecID()).getValue());
			//order.setDescription(message.getField(new OrdRejReason()).getValue() + " - " + message.getField(new Text()).getValue());
			(new Thread(new BFixReplaceAprovalOrder(order, this, orderTableModel))).start();
			
		}else if (order.getExecType()=='C') {  //Geçerlilik Süresi Doldu Mesajı

			
			
		}else if (order.getExecType()=='I') {  //Durum Sorgusu İletildi Mesajı
		
			
			
		}
		
	}
	private void quoteExecutionReport(Message message, SessionID sessionID)
			throws FieldNotFound {
		BFixQuoteOrder quoteOrder = null;
		BFixQuoteOrder orginalQuoteOrder = quoteOrderTableModel.getOrderByClOrdID(message.getField(new ClOrdID()).getValue());
		
		if(orginalQuoteOrder == null)
			quoteOrder = new BFixQuoteOrder();
		else
			quoteOrder = (BFixQuoteOrder) orginalQuoteOrder.clone();
		//quoteOrder.setExecType(message.getField(new ExecType()).getValue());
		//quoteOrder.setExecID(message.getField(new ExecID()).getValue());
		quoteOrder.setQuoteStatus(message.getField(new QuoteStatus()).getValue());
		quoteOrder.setMsgSeqNum(message.getHeader().getField(new MsgSeqNum()).getValue());
		quoteOrder.setMessage(message.toString()); 
		
		if(quoteOrder.getQuoteStatus()==0){
			if (orginalQuoteOrder == null) {
				quoteOrder.setClOrdID(message.getField(new ClOrdID()).getValue());
				quoteOrder.setSymbol(message.getField(new Symbol()).getValue());
				quoteOrder.setSymbolSfx(message.getField(new SymbolSfx()).getValue());
				quoteOrder.setBidPx(message.getField(new BidPx()).getValue());
				quoteOrder.setBidSize(message.getField(new BidSize()).getValue());
				quoteOrder.setOfferPx(message.getField(new OfferSize()).getValue());
				quoteOrder.setOfferPx(message.getField(new OfferPx()).getValue());
				quoteOrder.setAccount(message.getField(new Account()).getValue());
				quoteOrder.setAccountType(message.getField(new AccountType()).getValue());
			}
			quoteOrder.setQuoteID(message.getField(new QuoteID()).getValue());
			quoteOrder.setOrderIDSell(message.getString(20002));
			quoteOrder.setOrderIDBuy(message.getString(20001));
			quoteOrder.setTransactTime(message.getField(new TransactTime()).getValue());
			quoteOrder.setMessage(message.toString());
			(new Thread(new BFixQuoteAprovalOrder(quoteOrder, this, quoteOrderTableModel))).start();
		}else if (quoteOrder.getQuoteStatus()==5) {  
			if (orginalQuoteOrder == null) {
				quoteOrder.setClOrdID(message.getField(new ClOrdID()).getValue());
				quoteOrder.setSymbol(message.getField(new Symbol()).getValue());
				quoteOrder.setSymbolSfx(message.getField(new SymbolSfx()).getValue());
				quoteOrder.setBidPx(message.getField(new BidPx()).getValue());
				quoteOrder.setBidSize(message.getField(new BidSize()).getValue());
				quoteOrder.setOfferPx(message.getField(new OfferSize()).getValue());
				quoteOrder.setOfferPx(message.getField(new OfferPx()).getValue());
				quoteOrder.setAccount(message.getField(new Account()).getValue());
				quoteOrder.setAccountType(message.getField(new AccountType()).getValue());
			}
			//quoteOrder.setTransactTime(message.getField(new TransactTime()).getValue());
			quoteOrder.setMessage(message.toString());
			(new Thread(new BFixQuoteAprovalOrder(quoteOrder, this, quoteOrderTableModel))).start();
		}
	}
	private void securityStatusReport(Message message, SessionID sessionID)
			throws FieldNotFound {
		System.out.println("securityStatus mesajı alındı : " + message.toString());
	}
	private void tradingSessionStatusReport(Message message, SessionID sessionID) throws FieldNotFound {
		BFixTradingSessionStatus bFixTradingSessionStatus = new BFixTradingSessionStatus();
		bFixTradingSessionStatus.setTradingSessionID(message.getField(new TradingSessionID()).getValue());
		bFixTradingSessionStatus.setMarketSegmentID(message.getString(1300));
		bFixTradingSessionStatus.setTradSesStatus(message.getField(new TradSesStatus()).getValue());
		bFixTradingSessionStatus.setTransactTime(message.getField(new TransactTime()).getValue());
		bFixTradingSessionStatus.setMessage(message.toString());
		
		(new Thread(new BFixTradingSessionStatusChange(bFixTradingSessionStatus, this))).start();
	}
	private void cancelReject(Message message, SessionID sessionID)
			throws FieldNotFound {
		System.out.println("cancelReject mesajı alındı : " + message.toString());
	}

	public boolean isMissingField() {
		return isMissingField;
	}

	public void setMissingField(boolean isMissingField) {
		this.isMissingField = isMissingField;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	
	public boolean isResetSeqNumFlag() {
		return resetSeqNumFlag;
	}

	public void setResetSeqNumFlag(boolean resetSeqNumFlag) {
		this.resetSeqNumFlag = resetSeqNumFlag;
	}
	
	
	// ------------------- BFixApplication End
	// -------------------------------------

	
	

	public void addLogonObserver(Observer observer) {
		observableLogon.addObserver(observer);
	}

	public void deleteLogonObserver(Observer observer) {
		observableLogon.deleteObserver(observer);
	}

	public void addOrderObserver(Observer observer) {
		observableOrder.addObserver(observer);
	}

	public void deleteOrderObserver(Observer observer) {
		observableOrder.deleteObserver(observer);
	}

	public void addBFixLogonObserver(Observer observer) {
		observableBFixLogon.addObserver(observer);
	}

	public void bFixLogon() {
		observableBFixLogon.logon();
	}

	public void bFixLogoff() {
		observableBFixLogon.logoff();
	}

	private static class ObservableOrder extends Observable {
		public void update(BFixOrder order) {
			setChanged();
			notifyObservers(order);
			clearChanged();
		}
	}

	private static class ObservableLogon extends Observable {
		private HashSet<SessionID> set = new HashSet<SessionID>();

		public void logon(SessionID sessionID) {
			set.add(sessionID);
			setChanged();
			notifyObservers(new BFixLogonEvent(sessionID, true));
			clearChanged();
		}

		public void logoff(SessionID sessionID) {
			set.remove(sessionID);
			setChanged();
			notifyObservers(new BFixLogonEvent(sessionID, false));
			clearChanged();
		}
	}

	// ------------------- ObservableLogon End
	// -------------------------------------

	public class MessageProcessor implements Runnable {
		private quickfix.Message message;
		private SessionID sessionID;

		public MessageProcessor(quickfix.Message message, SessionID sessionID) {
			this.message = message;
			this.sessionID = sessionID;
		}

		public void run() {
			try {
				MsgType msgType = new MsgType();
				if (isAvailable) {
					
					if (isMissingField) {
						// For OpenFIX certification testing
						sendBusinessReject(
								message,
								BusinessRejectReason.CONDITIONALLY_REQUIRED_FIELD_MISSING,
								"Conditionally required field missing");
					} else if (message.getHeader().isSetField(
							DeliverToCompID.FIELD)) {
						// This is here to support OpenFIX certification
						sendSessionReject(message,
								SessionRejectReason.COMPID_PROBLEM);
					}else if (message.getHeader().getField(msgType)
							.valueEquals("f")) {
						securityStatusReport(message, sessionID);
						
					}else if (message.getHeader().getField(msgType)
							.valueEquals("8")) {
						executionReport(message, sessionID);
					} else if (message.getHeader().getField(msgType)
							.valueEquals("9")) {
						cancelReject(message, sessionID);
					}else if (message.getHeader().getField(msgType)
							.valueEquals("AI")) {
						quoteExecutionReport(message, sessionID);
					}else if (message.getHeader().getField(msgType) // 
							.valueEquals("h")) {
						tradingSessionStatusReport(message, sessionID);
					}else {
						sendBusinessReject(message,
								BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE,
								"Unsupported Message Type");
					}
					
				} else {
					sendBusinessReject(message,
							BusinessRejectReason.APPLICATION_NOT_AVAILABLE,
							"Application not available");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		

	}

	// ------------------- MessageProcessor End
	// -------------------------------------
}
