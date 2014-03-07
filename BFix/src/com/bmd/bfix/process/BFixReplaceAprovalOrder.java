package com.bmd.bfix.process;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import com.bmd.bfix.BFix;
import com.bmd.bfix.BFixApplication;
import com.bmd.bfix.model.BFixOrder;
import com.bmd.bfix.model.BFixOrderTableModel;

public class BFixReplaceAprovalOrder implements Runnable {
	private BFixApplication bFixAplication;
	private BFixOrderTableModel orderTableModel;
	private BFixOrder order;
	
	public BFixReplaceAprovalOrder(BFixOrder order, BFixApplication bFixAplication, BFixOrderTableModel orderTableModel) {

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
					System.out.println("Replace Aproval Order Db Bağlantı Hatası! (1301)");
				} else {
					cs = conn.prepareCall("{? = call uop14.senkron_kanal.emir_degistir_onayi_bfix(?,?,?,?,?,?,?)}");
					cs.registerOutParameter(1, Types.INTEGER);
					cs.setString(2, ""); 
					cs.setInt(3, Integer.valueOf(order.getExecID()));
					cs.setString(4, order.getMessage());
					cs.setString(5, null);
					cs.setString(6, null);
					cs.setInt(7, Integer.valueOf(order.getClOrdID()));
					cs.setInt(8, BFix.pcNo); 
					//cs.setInt(10, 1);
					
					cs.executeQuery();
					int result = cs.getInt(1);
					if(result==0){
						orderTableModel.updateOrderByClOrdID(order);
					}else
						System.out.println("Replace Aproval Order yeni emir onayı hatası emir sira no :" + order.getClOrdID() + " sonuc : " + result);
						
							
				}
		} catch (Exception e) {
			System.out.println("Replace Aproval Order ExecuteQuery Hatası! (1302)");
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
