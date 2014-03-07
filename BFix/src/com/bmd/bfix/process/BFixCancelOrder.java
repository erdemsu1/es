package com.bmd.bfix.process;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import com.bmd.bfix.BFix;
import com.bmd.bfix.BFixApplication;
import com.bmd.bfix.model.BFixExecutionTableModel;
import com.bmd.bfix.model.BFixOrder;

public class BFixCancelOrder implements Runnable {
	private BFixApplication bFixAplication;
	private BFixExecutionTableModel bFixExecutionTableModel;
	private BFixOrder order;
	
	public BFixCancelOrder(BFixOrder order, BFixApplication bFixAplication, BFixExecutionTableModel bFixExecutionTableModel) {

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
					System.out.println("Cancel Order Db Bağlantı Hatası! (8001)");
				} else {
					cs = conn.prepareCall("{? = call uop14.asenkron_kanal.emir_iptal_bilgisi_bfix(?,?,?,?,?,?)}");
					cs.registerOutParameter(1, Types.INTEGER);
					cs.setString(2, ""); 
					cs.setString(3, null);
					cs.setInt(4, Integer.valueOf(order.getExecID()));
					cs.setLong(5, Long.valueOf(order.getOrderID()));
					cs.setString(6, order.getDescription());
					cs.setInt(7, BFix.pcNo);
					cs.executeQuery();
					int result = cs.getInt(1);
					System.out.println("csaddCancelOrderBefore - " + cs.getInt(1) + " -- " + order.getOrderID());
					if(result==0){
						System.out.println("addCancelOrderBefore" + order.getExecID() + " -- " + order.getOrderID());
						bFixExecutionTableModel.addOrder(order);
						System.out.println("addCancelOrderAfter" + order.getExecID() + " -- " + order.getOrderID());
						//bFixExecutionTableModel.updateOrderByClOrdID(order);
					}else
						System.out.println("Cancel Order emir iptal bilgisi hatası imkb emir no :" + order.getOrderID() + " sonuc : " + result);
						
							
				}
		} catch (Exception e) {
			System.out.println("Cancel Order ExecuteQuery Hatası! (8002)");
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
