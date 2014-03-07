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
import quickfix.field.BidPx;
import quickfix.field.BidSize;
import quickfix.field.ClOrdID;
import quickfix.field.ExpireDate;
import quickfix.field.HandlInst;
import quickfix.field.OfferPx;
import quickfix.field.OfferSize;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.OrderQty2;
import quickfix.field.PartyID;
import quickfix.field.Price;
import quickfix.field.Price2;
import quickfix.field.QuoteID;
import quickfix.field.QuoteReqID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.SymbolSfx;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import quickfix.fix50.Quote;

public class BFixQuoteOrder implements Cloneable {
	private SessionID sessionID = null;
	
	
	private String quoteID;		// (117)
    private String clOrdID;		// (11)
    private String symbol;		// (55)
    private String symbolSfx;	// (65)
    private Double bidSize;		// (134)
    private Double bidPx;		// (132)
    private Double offerSize;	// (135)
    private Double offerPx;		// (133)
    private Double bidSize2;	// (6052)
    private Double bidPx2;		// (6050)
    private Double offerSize2;	// (6053)
    private Double offerPx2;	// (6051)
    private String account;		// (1)
    private int accountType;	// (581)	1=Müşteri 2=Portföy 9=Fon
    private int noPartyIDs;		// (453)
    private String partyID;		// (448)
    private int partyRole;		// (452)	28=Saklamacı Kurum
    private Date transactTime;	// (60)		YYYYAAGG-SS:DD:SS
    
    private String orderIDBuy;	// (20001)
    private String orderIDSell;	// (20001)
    private String execID;		// (17)
    private int msgSeqNum;		// (34)
    private String message;
    private char execType;		// (150)	0=Yeni emir kabulü 4=İptal edildi 5=Değiştirildi 6=İptal istendi 8=Reddedildi F=İşlem gerçekleşti I=Emir durumu C=Geçerlilik süresi doldu.	
    private int quoteStatus;	// (297)	0=Kabul edildi
 
    private String description;
 
    
    public Quote NewQuoteOrderFill(){
    	Quote quote = new Quote();
    	quote.set(new QuoteID("0"));
    	quote.set(new QuoteReqID(getClOrdID()));
    	quote.setField(new ClOrdID(getClOrdID()));
    	//quote.set(new ClOrdID(getClOrdID()));
    	quote.set(new Symbol(getSymbol()));
    	quote.set(new SymbolSfx(getSymbolSfx()));
    	quote.set(new BidSize(getBidSize()));
    	quote.set(new BidPx(getBidPx()));
    	quote.set(new OfferSize(getOfferSize()));
    	quote.set(new OfferPx(getOfferPx()));
    	
    	quote.set(new Account(getAccount()));
    	quote.set(new AccountType(getAccountType()));
    	//quote.setField(new PartyID(getPartyID()));
    	return quote;
    }
    public Quote ReplaceQuoteOrderFill(){
    	Quote quote = new Quote();
    	quote.set(new QuoteID( getQuoteID()));
    	//quote.set(new QuoteReqID(getClOrdID()));
    	//quote.setField(new ClOrdID(getClOrdID()));
    	quote.setField(new ClOrdID(getClOrdID()));
    	//quote.set(new Symbol(getSymbol()));
    	//quote.set(new SymbolSfx(getSymbolSfx()));
    	quote.set(new BidSize(getBidSize()));
    	quote.set(new BidPx(getBidPx()));
    	quote.set(new OfferSize(getOfferSize()));
    	quote.set(new OfferPx(getOfferPx()));
    	//quote.set(new BidSize2(getBidSize()));
    	//quote.set(new BidPx2(getBidPx()));
    	//quote.set(new OfferSize2(getOfferSize()));
    	//quote.set(new OfferPx2(getOfferPx()));
    	
    	
    	//quote.set(new Account(getAccount()));
    	//quote.set(new AccountType(getAccountType()));
    	//quote.setField(new PartyID(getPartyID()));
    	return quote;
    }
    
    public SessionID getSessionID() {
		return sessionID;
	}



	public void setSessionID(SessionID sessionID) {
		this.sessionID = sessionID;
	}



	public String getQuoteID() {
		return quoteID;
	}



	public void setQuoteID(String quoteID) {
		this.quoteID = quoteID;
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



	public Double getBidSize() {
		return bidSize;
	}



	public void setBidSize(Double bidSize) {
		this.bidSize = bidSize;
	}



	public Double getBidPx() {
		return bidPx;
	}



	public void setBidPx(Double bidPx) {
		this.bidPx = bidPx;
	}



	public Double getOfferSize() {
		return offerSize;
	}



	public void setOfferSize(Double offerSize) {
		this.offerSize = offerSize;
	}



	public Double getOfferPx() {
		return offerPx;
	}



	public void setOfferPx(Double offerPx) {
		this.offerPx = offerPx;
	}



	public Double getBidSize2() {
		return bidSize2;
	}



	public void setBidSize2(Double bidSize2) {
		this.bidSize2 = bidSize2;
	}



	public Double getBidPx2() {
		return bidPx2;
	}



	public void setBidPx2(Double bidPx2) {
		this.bidPx2 = bidPx2;
	}



	public Double getOfferSize2() {
		return offerSize2;
	}



	public void setOfferSize2(Double offerSize2) {
		this.offerSize2 = offerSize2;
	}



	public Double getOfferPx2() {
		return offerPx2;
	}



	public void setOfferPx2(Double offerPx2) {
		this.offerPx2 = offerPx2;
	}



	public String getAccount() {
		return account;
	}



	public void setAccount(String account) {
		this.account = account;
	}



	public int getAccountType() {
		return accountType;
	}



	public void setAccountType(int accountType) {
		this.accountType = accountType;
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



	public Date getTransactTime() {
		return transactTime;
	}



	public void setTransactTime(Date transactTime) {
		this.transactTime = transactTime;
	}



	public String getOrderIDBuy() {
		return orderIDBuy;
	}



	public void setOrderIDBuy(String orderIDBuy) {
		this.orderIDBuy = orderIDBuy;
	}



	public String getOrderIDSell() {
		return orderIDSell;
	}



	public void setOrderIDSell(String orderIDSell) {
		this.orderIDSell = orderIDSell;
	}



	public String getExecID() {
		return execID;
	}



	public void setExecID(String execID) {
		this.execID = execID;
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



	public char getExecType() {
		return execType;
	}



	public void setExecType(char execType) {
		this.execType = execType;
	}



	public int getQuoteStatus() {
		return quoteStatus;
	}



	public void setQuoteStatus(int quoteStatus) {
		this.quoteStatus = quoteStatus;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public BFixQuoteOrder() {
        
    }

     

    public Object clone() {
        try {
        	BFixQuoteOrder quoteOrder = (BFixQuoteOrder)super.clone();
            
            return quoteOrder;
        } catch(CloneNotSupportedException e) {}
        return null;
    }

		
    
    
}
