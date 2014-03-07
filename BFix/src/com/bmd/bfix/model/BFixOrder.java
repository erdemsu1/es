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

import java.util.Date;

import quickfix.SessionID;
import quickfix.field.Account;
import quickfix.field.AccountType;
import quickfix.field.ClOrdID;
import quickfix.field.ExpireDate;
import quickfix.field.HandlInst;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.OrderQty2;
import quickfix.field.Price;
import quickfix.field.Price2;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.SymbolSfx;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import quickfix.fix50.NewOrderSingle;
import quickfix.fix50.OrderCancelReplaceRequest;
import quickfix.fix50.OrderCancelRequest;
import quickfix.fix50.OrderStatusRequest;

public class BFixOrder implements Cloneable {
	private SessionID sessionID = null;
	
    private String clOrdID;		// (11)
    private String symbol;		// (55)
    private String symbolSfx;	// (65)
    private char side; 			// (54)		1=Alış 2=Satış 5=Açığa Satış
    private char ordType; 		// (40)		1=Piyasa Emri 2=Normal Emir
    private char timeInForce;  	// (59)		0=Seanslık Emir 3=Kalanı İptal Et Emri 6=Tarihli Emir
    private String expireDate; 	// (432) 	YYYYAAGG Zorunlu değil
    private Double price;		// (44)		.01 - 9999999.99
    private Double orderQty;	// (38)
    private Double cashOrderQty;// (152)	.01 - 9999999.99
    private String account;		// (1)
    private int noPartyIDs;		// (453)
    private String partyID;		// (448)
    private int partyRole;		// (452)	28=Saklamacı Kurum
    private int accountType;	// (581)	1=Müşteri 2=Portföy 9=Fon
    private Date transactTime;	// (60)		YYYYAAGG-SS:DD:SS
    private char handlInst;		// (21)		'1'=Otomatik Emir İletimi '3'=Manuel İletim
    private Double price2;		// 			Emir değiştirme için .01 - 9999999.99
    private Double orderQty2;	// (38)		Emir değiştirme için 
    
    private String OrderID;		// (37)		16 hane sayısal
    private char execType;		// (150)	0=Yeni emir kabulü 4=İptal edildi 5=Değiştirildi 6=İptal istendi 8=Reddedildi F=İşlem gerçekleşti I=Emir durumu C=Geçerlilik süresi doldu.	
    private char ordStatus;		// (39)		0=Yeni 1=Kısmen Gerçekleşti 2=Tamamen gerçekleşti 4=İptal edildi 5=Değiştirildi 6=İptal istendi
    
    private Double lastPx;		// (31)		Gerçekleşen fiyat
    private Double lastQty;		// (32)		Gerçekleşen miktar
    private Double leavesQty;	// (151)	
    
    private String trdMatchID;	// (880)
    
    private String execID;		// (17)
    private int msgSeqNum;		// (34)
    private String message;
    private String description;
    private int OrdRejReason;   // (103)	Red edilme nedeni kodu
    
    public BFixOrder() {
        
    }

    public NewOrderSingle NewOrderFill(){
    	NewOrderSingle newOrderSingle = new NewOrderSingle(new ClOrdID(getClOrdID()),new Side(getSide()),new TransactTime(getTransactTime()),new OrdType(getOrdType()));
    	
 
    	//newOrderSingle.set(new ClOrdID(getClOrdID()));
    	newOrderSingle.set(new Symbol(getSymbol()));
    	newOrderSingle.set(new SymbolSfx(getSymbolSfx()));
    	//newOrderSingle.set(new Side(getSide()));
    	//newOrderSingle.set(new OrdType(getOrdType()));
    	newOrderSingle.set(new TimeInForce(getTimeInForce()));
    	if(getTimeInForce()=='6')
    		newOrderSingle.set(new ExpireDate(getExpireDate()));
    	newOrderSingle.set(new Price(getPrice()));
    	newOrderSingle.set(new OrderQty(getOrderQty()));
    	//newOrderSingle.set(new CashOrderQty(getCashOrderQty()));
    	newOrderSingle.set(new Account(getAccount()));
    	//newOrderSingle.set(new NoPartyIDs(getNoPartyIDs()));
    	//newOrderSingle.set(new PartyID(getPartyID()));
    	//newOrderSingle.set(new PartyRole(getPartyRole()));
    	newOrderSingle.set(new AccountType(getAccountType()));
    	//newOrderSingle.set(new TransactTime(getTransactTime()));
    	newOrderSingle.set(new HandlInst(getHandlInst()));
    	//newOrderSingle.set(new OrderID(""));
       	
        return newOrderSingle;
    }
    public OrderCancelReplaceRequest OrderCancelReplaceFill(){
    	OrderCancelReplaceRequest orderCancelReplaceRequest = new OrderCancelReplaceRequest();
    	 //(new ClOrdID(getClOrdID()),new Side(getSide()),new TransactTime(getTransactTime()),new OrdType(getOrdType())
 
    	orderCancelReplaceRequest.set(new ClOrdID(getClOrdID()));
    	orderCancelReplaceRequest.set(new Symbol(getSymbol()));
    	orderCancelReplaceRequest.set(new SymbolSfx(getSymbolSfx()));
    	orderCancelReplaceRequest.set(new Side(getSide()));
    	orderCancelReplaceRequest.set(new OrdType(getOrdType()));
    	orderCancelReplaceRequest.set(new TimeInForce(getTimeInForce()));
    	if(getTimeInForce()=='6')
    		orderCancelReplaceRequest.set(new ExpireDate(getExpireDate()));
        //newOrderSingle.set(new ExpireDate(getExpireDate()));
    	orderCancelReplaceRequest.set(new Price(getPrice()));
    	orderCancelReplaceRequest.set(new OrderQty(getOrderQty()));
    	orderCancelReplaceRequest.set(new Price2(getPrice2()));
    	orderCancelReplaceRequest.set(new OrderQty2(getOrderQty2()));
    	//newOrderSingle.set(new CashOrderQty(getCashOrderQty()));
    	//orderCancelReplaceRequest.set(new Account(getAccount()));
    	//newOrderSingle.set(new NoPartyIDs(getNoPartyIDs()));
    	//newOrderSingle.set(new PartyID(getPartyID()));
    	//newOrderSingle.set(new PartyRole(getPartyRole()));
    	//orderCancelReplaceRequest.set(new AccountType(getAccountType()));
    	//newOrderSingle.set(new TransactTime(getTransactTime()));
    	orderCancelReplaceRequest.set(new HandlInst(getHandlInst()));
    	orderCancelReplaceRequest.set(new quickfix.field.OrderID(getOrderID()));
    	orderCancelReplaceRequest.set(new TransactTime(getTransactTime()));
        return orderCancelReplaceRequest;
    }
    
    public OrderCancelRequest OrderCancelFill(){
    	OrderCancelRequest orderCancelRequest = new OrderCancelRequest();
    	orderCancelRequest.set(new quickfix.field.OrderID(getOrderID()));
    	orderCancelRequest.set(new Symbol(getSymbol()));
    	orderCancelRequest.set(new SymbolSfx(getSymbolSfx()));
    	orderCancelRequest.set(new Side(getSide()));
    	orderCancelRequest.set(new TransactTime(getTransactTime()));
       	
        return orderCancelRequest;
    }
    public OrderStatusRequest OrderStatusRequestFill(){
    	OrderStatusRequest orderStatusRequest = new OrderStatusRequest();
    	orderStatusRequest.set(new quickfix.field.OrderID(getOrderID()));
    	orderStatusRequest.set(new Symbol(getSymbol()));
    	orderStatusRequest.set(new SymbolSfx(getSymbolSfx()));
    	orderStatusRequest.set(new Side(getSide()));
       	
        return orderStatusRequest;
    }
    

    public Object clone() {
        try {
        	BFixOrder order = (BFixOrder)super.clone();
            
            return order;
        } catch(CloneNotSupportedException e) {}
        return null;
    }

	public String getClOrdID() {
		return clOrdID;
	}




	public void setClOrdID(String clOrdID) {
		this.clOrdID = clOrdID;
	}




	public String getSymbol() {
		return symbol;
	}




	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}




	public String getSymbolSfx() {
		return symbolSfx;
	}




	public void setSymbolSfx(String symbolSfx) {
		this.symbolSfx = symbolSfx;
	}




	public char getSide() {
		return side;
	}




	public void setSide(char side) {
		this.side = side;
	}




	public char getOrdType() {
		return ordType;
	}




	public void setOrdType(char ordType) {
		this.ordType = ordType;
	}




	public char getTimeInForce() {
		return timeInForce;
	}




	public void setTimeInForce(char timeInForce) {
		this.timeInForce = timeInForce;
	}




	public String getExpireDate() {
		return expireDate;
	}




	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}




	public Double getPrice() {
		return price;
	}




	public void setPrice(Double price) {
		this.price = price;
	}




	public Double getOrderQty() {
		return orderQty;
	}




	public void setOrderQty(Double orderQty) {
		this.orderQty = orderQty;
	}




	public Double getCashOrderQty() {
		return cashOrderQty;
	}




	public void setCashOrderQty(Double cashOrderQty) {
		this.cashOrderQty = cashOrderQty;
	}




	public String getAccount() {
		return account;
	}




	public void setAccount(String account) {
		this.account = account;
	}




	public int getNoPartyIDs() {
		return noPartyIDs;
	}




	public void setNoPartyIDs(int noPartyIDs) {
		this.noPartyIDs = noPartyIDs;
	}




	public String getPartyID() {
		return partyID;
	}




	public void setPartyID(String partyID) {
		this.partyID = partyID;
	}




	public int getPartyRole() {
		return partyRole;
	}




	public void setPartyRole(int partyRole) {
		this.partyRole = partyRole;
	}




	public int getAccountType() {
		return accountType;
	}




	public void setAccountType(int accountType) {
		this.accountType = accountType;
	}




	public Date getTransactTime() {
		return transactTime;
	}




	public void setTransactTime(Date transactTime) {
		this.transactTime = transactTime;
	}




	public char getHandlInst() {
		return handlInst;
	}




	public void setHandlInst(char handlInst) {
		this.handlInst = handlInst;
	}

	public SessionID getSessionID() {
		return sessionID;
	}

	public void setSessionID(SessionID sessionID) {
		this.sessionID = sessionID;
	}

	public Double getPrice2() {
		return price2;
	}

	public void setPrice2(Double price2) {
		this.price2 = price2;
	}

	public Double getOrderQty2() {
		return orderQty2;
	}

	public void setOrderQty2(Double orderQty2) {
		this.orderQty2 = orderQty2;
	}

	public String getOrderID() {
		return OrderID;
	}

	public void setOrderID(String orderID) {
		OrderID = orderID;
	}

	public char getExecType() {
		return execType;
	}

	public void setExecType(char execType) {
		this.execType = execType;
	}

	public char getOrdStatus() {
		return ordStatus;
	}

	public void setOrdStatus(char ordStatus) {
		this.ordStatus = ordStatus;
	}

	public Double getLastPx() {
		return lastPx;
	}

	public void setLastPx(Double lastPx) {
		this.lastPx = lastPx;
	}

	public Double getLastQty() {
		return lastQty;
	}

	public void setLastQty(Double lastQty) {
		this.lastQty = lastQty;
	}

	public Double getLeavesQty() {
		return leavesQty;
	}

	public void setLeavesQty(Double leavesQty) {
		this.leavesQty = leavesQty;
	}

	public String getTrdMatchID() {
		return trdMatchID;
	}

	public void setTrdMatchID(String trdMatchID) {
		this.trdMatchID = trdMatchID;
	}

	public int getMsgSeqNum() {
		return msgSeqNum;
	}

	public void setMsgSeqNum(int msgSeqNum) {
		this.msgSeqNum = msgSeqNum;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getExecID() {
		return execID;
	}

	public void setExecID(String execID) {
		this.execID = execID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getOrdRejReason() {
		return OrdRejReason;
	}

	public void setOrdRejReason(int ordRejReason) {
		OrdRejReason = ordRejReason;
	}

	
    
    
}
