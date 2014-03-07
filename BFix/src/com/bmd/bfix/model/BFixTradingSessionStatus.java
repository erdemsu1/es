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

public class BFixTradingSessionStatus implements Cloneable {
	private SessionID sessionID = null;
	
    private String marketSegmentID;		// (1300)
    private String tradingSessionID;	// (336)
    private int tradSesStatus;			// (340)
	private Date transactTime;
    private String message;
    private String marketName;
    
    
    
    public BFixTradingSessionStatus() {
        
    }
    
    public SessionID getSessionID() {
		return sessionID;
	}

	public void setSessionID(SessionID sessionID) {
		this.sessionID = sessionID;
	}

	public String getMarketSegmentID() {
		return marketSegmentID;
	}

	public void setMarketSegmentID(String marketSegmentID) {
		this.marketSegmentID = marketSegmentID;
	}

	public String getTradingSessionID() {
		return tradingSessionID;
	}

	public void setTradingSessionID(String tradingSessionID) {
		this.tradingSessionID = tradingSessionID;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
   
    public Date getTransactTime() {
		return transactTime;
	}

	public void setTransactTime(Date transactTime) {
		this.transactTime = transactTime;
	}

	public int getTradSesStatus() {
		return tradSesStatus;
	}

	public void setTradSesStatus(int tradSesStatus) {
		this.tradSesStatus = tradSesStatus;
	}

	public String getMarketName() {
		return marketName;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}
	 public BFixTradingSessionStatus clone() {
	        try {
	        	BFixTradingSessionStatus bFixTradingSessionStatus = (BFixTradingSessionStatus)super.clone();
	            
	            return bFixTradingSessionStatus;
	        } catch(CloneNotSupportedException e) {}
	        return null;
	    }

}
