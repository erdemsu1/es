package com.bmd.bfix.process;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import com.bmd.bfix.BFix;
import com.bmd.bfix.BFixApplication;
import com.bmd.bfix.model.BFixExecutionTableModel;
import com.bmd.bfix.model.BFixOrder;
import com.bmd.bfix.model.BFixOrderTableModel;

public class BFixErrorOrder implements Runnable {
	private BFixApplication bFixAplication;
	private BFixOrderTableModel bFixOrderTableModel;
	private BFixOrder order;
	
	public BFixErrorOrder(BFixOrder order, BFixApplication bFixAplication, BFixOrderTableModel bFixOrderTableModel) {

		this.bFixOrderTableModel = bFixOrderTableModel;
		this.bFixAplication = bFixAplication;
		this.order = order;
	}

	@Override
	public void run() {
		
			CallableStatement cs = null;
			BFixDbOperation bfixDbOperation = new BFixDbOperation();
			Connection conn = bfixDbOperation.createConnection();
			try {

				if (conn == null) {
					System.out.println("Error Order Db Bağlantı Hatası! (1201)");
				} else {
					cs = conn.prepareCall("{? = call uop14.senkron_kanal.hata_yaz_bfix(?,?,?,?)}");
					cs.registerOutParameter(1, Types.INTEGER);
					cs.setInt(2, order.getOrdRejReason()); 
					cs.setString(3, order.getMessage());
					cs.setInt(4, Integer.valueOf(order.getClOrdID()));
					cs.setInt(5, BFix.pcNo); 
					cs.executeQuery();
					int result = cs.getInt(1);
					System.out.println("csErrorOrderBefore - " + cs.getInt(1) + " -- " + order.getClOrdID());
					if(result==0){
						System.out.println("ErrorCancelOrderBefore" + order.getExecID() + " -- " + order.getClOrdID());
						bFixOrderTableModel.updateOrderByClOrdID(order);
						System.out.println("ErrorCancelOrderAfter" + order.getExecID() + " -- " + order.getClOrdID());
						//bFixExecutionTableModel.updateOrderByClOrdID(order);
					}else
						System.out.println("Error Order emir iptal bilgisi hatası emir sıra no :" + order.getClOrdID() + " sonuc : " + result);
				}
		} catch (Exception e) {
			System.out.println("Error Order ExecuteQuery Hatası! (1202)");
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
