package com.bmd.bfix.process;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import com.bmd.bfix.BFix;
import com.bmd.bfix.BFixApplication;
import com.bmd.bfix.model.BFixOrder;
import com.bmd.bfix.model.BFixOrderTableModel;

public class BFixAprovalOrder implements Runnable {
	private BFixApplication bFixAplication;
	private BFixOrderTableModel orderTableModel;
	private BFixOrder order;
	
	public BFixAprovalOrder(BFixOrder order, BFixApplication bFixAplication, BFixOrderTableModel orderTableModel) {

		this.orderTableModel = orderTableModel;
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
					System.out.println("Aproval Order Db Bağlantı Hatası! (4001)");
				} else {
					cs = conn.prepareCall("{? = call uop14.senkron_kanal.yeni_emir_onayi_bfix(?,?,?,?,?,?,?,?)}");
					cs.registerOutParameter(1, Types.INTEGER);
					cs.setString(2, ""); 
					cs.setInt(3, Integer.valueOf(order.getExecID()));
					cs.setLong(4, Long.valueOf(order.getOrderID()));
					cs.setString(5, "");
					cs.setString(6, null);
					cs.setString(7, null);
					cs.setInt(8, Integer.valueOf(order.getClOrdID()));
					cs.setInt(9, BFix.pcNo); // TODO: PC No applicationdan alınıp eklenecek
					//cs.setInt(10, 1);
					
					cs.executeQuery();
					int result = cs.getInt(1);
					System.out.println("csapprovalOrderBefore - " + cs.getInt(1) + " -- " + order.getClOrdID());
					if(result==0){
						System.out.println("approvalOrderBefore" + order.getExecID() + " -- " + order.getOrderID());
						orderTableModel.updateOrderByClOrdID(order);
						System.out.println("approvalOrderAfter" + order.getExecID() + " -- " + order.getOrderID());
					}else
						System.out.println("Aproval Order yeni emir onayı hatası emir sira no :" + order.getClOrdID() + " sonuc : " + result);
						
							
				}
		} catch (Exception e) {
			System.out.println("Aproval Order ExecuteQuery Hatası! (4002)");
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
