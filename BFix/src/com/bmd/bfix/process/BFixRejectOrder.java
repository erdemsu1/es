package com.bmd.bfix.process;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import com.bmd.bfix.BFix;
import com.bmd.bfix.BFixApplication;
import com.bmd.bfix.model.BFixExecutionTableModel;
import com.bmd.bfix.model.BFixOrder;

public class BFixRejectOrder implements Runnable {
	private BFixApplication bFixAplication;
	private BFixExecutionTableModel bFixExecutionTableModel;
	private BFixOrder order;
	
	public BFixRejectOrder(BFixOrder order, BFixApplication bFixAplication, BFixExecutionTableModel bFixExecutionTableModel) {

		this.bFixExecutionTableModel = bFixExecutionTableModel;
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
					System.out.println("Reject Order Db Bağlantı Hatası! (9001)");
				} else {
					cs = conn.prepareCall("{? = call uop14.asenkron_kanal.istek_red_bilgisi_bfix(?,?,?,?,?,?)}");
					cs.registerOutParameter(1, Types.INTEGER);
					cs.setString(2, ""); 
					cs.setString(3, null);
					cs.setInt(4, Integer.valueOf(order.getExecID()));
					cs.setInt(5, Integer.valueOf(order.getClOrdID()));
					cs.setString(6, order.getDescription());
					cs.setInt(11, BFix.pcNo); 
					cs.executeQuery();
					int result = cs.getInt(1);
					System.out.println("csaddCancelOrderBefore - " + cs.getInt(1) + " -- " + order.getOrderID());
					if(result==0){
						System.out.println("addCancelOrderBefore" + order.getExecID() + " -- " + order.getOrderID());
						bFixExecutionTableModel.addOrder(order);
						System.out.println("addCancelOrderAfter" + order.getExecID() + " -- " + order.getOrderID());
						//bFixExecutionTableModel.updateOrderByClOrdID(order);
					}else
						System.out.println("Reject Order emir iptal bilgisi hatası imkb emir no :" + order.getOrderID() + " sonuc : " + result);
						
							
				}
		} catch (Exception e) {
			System.out.println("Reject Order ExecuteQuery Hatası! (9002)");
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
