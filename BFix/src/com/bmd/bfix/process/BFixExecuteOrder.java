package com.bmd.bfix.process;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;

import com.bmd.bfix.BFix;
import com.bmd.bfix.BFixApplication;
import com.bmd.bfix.model.BFixExecutionTableModel;
import com.bmd.bfix.model.BFixOrder;

public class BFixExecuteOrder implements Runnable {
	private BFixApplication bFixAplication;
	private BFixExecutionTableModel bFixExecutionTableModel;
	private BFixOrder order;
	
	public BFixExecuteOrder(BFixOrder order, BFixApplication bFixAplication, BFixExecutionTableModel bFixExecutionTableModel) {

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
					System.out.println("Execute Order Db Bağlantı Hatası! (5001)");
				} else {
					cs = conn.prepareCall("{? = call uop14.asenkron_kanal.islem_bilgisi_bfix(?,?,?,?,?,?,?,?,?,?)}");
					cs.registerOutParameter(1, Types.INTEGER);
					cs.setString(2, ""); 
					cs.setInt(3, Integer.valueOf(order.getExecID()));
					cs.setLong(4, Long.valueOf(order.getOrderID()));
					cs.setString(5, new SimpleDateFormat("HH:mm:ss").format(order.getTransactTime()));
					cs.setString(6, null);
					cs.setInt(7, order.getMsgSeqNum());
					cs.setDouble(8,order.getLastQty());
					cs.setDouble(9, order.getLastPx());
					cs.setString(10, order.getMessage());
					cs.setInt(11, BFix.pcNo); // TODO: PC No applicationdan alınıp eklenecek
					
					System.out.println("pcNo - " + BFix.pcNo);
					cs.executeQuery();
					int result = cs.getInt(1);
					System.out.println("csaddOrderBefore - " + cs.getInt(1) + " -- " + order.getOrderID());
					if(result==0){
						System.out.println("addOrderBefore" + order.getExecID() + " -- " + order.getOrderID());
						bFixExecutionTableModel.addOrder(order);
						System.out.println("addOrderAfter" + order.getExecID() + " -- " + order.getOrderID());
						//bFixExecutionTableModel.updateOrderByClOrdID(order);
					}else
						System.out.println("Execute Order islem bilgisi hatası imkb emir no :" + order.getOrderID() + " sonuc : " + result);
						
							
				}
		} catch (Exception e) {
			System.out.println("Execute Order ExecuteQuery Hatası! (5002)");
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
