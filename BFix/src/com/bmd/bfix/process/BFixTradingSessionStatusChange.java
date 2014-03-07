package com.bmd.bfix.process;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bmd.bfix.BFix;
import com.bmd.bfix.BFixApplication;
import com.bmd.bfix.model.BFixOrder;
import com.bmd.bfix.model.BFixOrderTableModel;
import com.bmd.bfix.model.BFixQuoteOrder;
import com.bmd.bfix.model.BFixQuoteOrderTableModel;
import com.bmd.bfix.model.BFixTradingSessionStatus;

public class BFixTradingSessionStatusChange implements Runnable {
	private BFixApplication bFixAplication;

	private BFixTradingSessionStatus bFixTradingSessionStatus;
	
	public BFixTradingSessionStatusChange(BFixTradingSessionStatus bFixTradingSessionStatus, BFixApplication bFixAplication) {


		this.bFixAplication = bFixAplication;
		this.bFixTradingSessionStatus = bFixTradingSessionStatus;
	}

	@Override
	public void run() {
		
			CallableStatement cs = null;
			BFixDbOperation bfixDbOperation = new BFixDbOperation();
			Connection conn = bfixDbOperation.createConnection();
			try {

				if (conn == null) {
					System.out.println("Trading Session Status Change Db Bağlantı Hatası! (1501)");
				} else {
					cs = conn.prepareCall("{? = call uop14.asenkron_kanal.genel_yayin_mesaji_bfix(?,?,?,?,?,?,?,?)}");
					cs.registerOutParameter(1, Types.INTEGER);
					cs.setString(2, ""); 
					cs.setString(3, null);//log dosya satır no
					cs.setString(4, null);//log sıra no
					cs.setString(5, bFixTradingSessionStatus.getMessage());
					cs.setString(6, bFixTradingSessionStatus.getMarketSegmentID());
					cs.setString(7, bFixTradingSessionStatus.getTradingSessionID());
					cs.setInt(8, bFixTradingSessionStatus.getTradSesStatus());
					cs.setInt(9, BFix.pcNo); 
					cs.executeQuery();
					int result = cs.getInt(1);
					System.out.println(new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()) + " csTradingSessionStatusChange - " + cs.getInt(1) + " -- " + bFixTradingSessionStatus.getMarketSegmentID()+ " -- " + bFixTradingSessionStatus.getTradingSessionID()+ " -- " + bFixTradingSessionStatus.getTradSesStatus());
					if(result==0){
						System.out.println("TradingSessionStatusChange " + bFixTradingSessionStatus.getMarketSegmentID()+ " -- " + bFixTradingSessionStatus.getTradingSessionID()+ " -- " + bFixTradingSessionStatus.getTradSesStatus());

					}else
						System.out.println("TradingSessionStatusChange hatası :" + bFixTradingSessionStatus.getMarketSegmentID()+ " -- " + bFixTradingSessionStatus.getTradingSessionID()+ " -- " + bFixTradingSessionStatus.getTradSesStatus());
						
					BFixReadTradingSessionStatus bFixReadTradingSessionStatus = new BFixReadTradingSessionStatus(bFixAplication);
					bFixReadTradingSessionStatus.run();			
				}
		} catch (Exception e) {
			System.out.println("Trading Session Status Change ExecuteQuery Hatası! (1502)");
			e.printStackTrace();
		} finally {
			try {
				if (!conn.isClosed())
					conn.close();
			} catch (SQLException e) {
		
				e.printStackTrace();
			}
			
		}
	
	}
	
	

}
