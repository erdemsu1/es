package com.bmd.bfix.process;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import com.bmd.bfix.BFix;
import com.bmd.bfix.BFixApplication;
import com.bmd.bfix.model.BFixOrder;
import com.bmd.bfix.model.BFixOrderTableModel;
import com.bmd.bfix.model.BFixQuoteOrder;
import com.bmd.bfix.model.BFixQuoteOrderTableModel;

public class BFixQuoteAprovalOrder implements Runnable {
	private BFixApplication bFixAplication;
	private BFixQuoteOrderTableModel quoteOrderTableModel;
	private BFixQuoteOrder quoteOrder;
	
	public BFixQuoteAprovalOrder(BFixQuoteOrder quoteOrder, BFixApplication bFixAplication, BFixQuoteOrderTableModel quoteOrderTableModel) {

		this.quoteOrderTableModel = quoteOrderTableModel;
		this.bFixAplication = bFixAplication;
		this.quoteOrder = quoteOrder;
	}

	@Override
	public void run() {
		
			CallableStatement cs = null;
			BFixDbOperation bfixDbOperation = new BFixDbOperation();
			Connection conn = bfixDbOperation.createConnection();
			try {

				if (conn == null) {
					System.out.println("Aproval Order Db Bağlantı Hatası! (1401)");
				} else {
					cs = conn.prepareCall("{? = call uop14.senkron_kanal.kotasyon_emir_onayi_bfix(?,?,?,?,?,?,?,?,?,?)}");
					cs.registerOutParameter(1, Types.INTEGER);
					cs.setString(2, ""); 
					cs.setString(3, null);
					//cs.setInt(3, Integer.valueOf(quoteOrder.getExecID()));
					cs.setLong(4, Long.valueOf(quoteOrder.getQuoteID()));
					cs.setString(5, quoteOrder.getOrderIDBuy()); 
					cs.setString(6, quoteOrder.getOrderIDSell());
					cs.setString(7, quoteOrder.getMessage());
					cs.setString(8, null);
					cs.setString(9, null);
					cs.setInt(10, Integer.valueOf(quoteOrder.getClOrdID()));
					cs.setInt(11, BFix.pcNo); // TODO: PC No applicationdan alınıp eklenecek
					//cs.setInt(10, 1);
					
					cs.executeQuery();
					int result = cs.getInt(1);
					System.out.println("csapprovalOrderBefore - " + cs.getInt(1) + " -- " + quoteOrder.getClOrdID());
					if(result==0){
						System.out.println("getOrderIDBuy " + quoteOrder.getExecID() + " -- " + quoteOrder.getOrderIDBuy());
						quoteOrderTableModel.updateOrderByClOrdID(quoteOrder);
						System.out.println("getOrderIDSell " + quoteOrder.getExecID() + " -- " + quoteOrder.getOrderIDSell());
					}else
						System.out.println("Aproval Quote Order yeni emir onayı hatası emir sira no :" + quoteOrder.getClOrdID() + " sonuc : " + result);
						
							
				}
		} catch (Exception e) {
			System.out.println("Aproval Quote Order ExecuteQuery Hatası! (1402)");
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
