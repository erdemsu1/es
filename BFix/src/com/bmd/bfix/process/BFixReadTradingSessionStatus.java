package com.bmd.bfix.process;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;

import quickfix.fix50.TradingSessionStatus;

import com.bmd.bfix.BFix;
import com.bmd.bfix.BFixApplication;
import com.bmd.bfix.model.BFixOrder;
import com.bmd.bfix.model.BFixOrderTableModel;
import com.bmd.bfix.model.BFixQuoteOrder;
import com.bmd.bfix.model.BFixQuoteOrderTableModel;
import com.bmd.bfix.model.BFixTradingSessionStatus;
import com.bmd.bfix.observable.ObservableBFixTradingStatus;

public class BFixReadTradingSessionStatus implements Runnable {
	private BFixApplication bFixAplication;
	
	public BFixReadTradingSessionStatus(BFixApplication bFixAplication) {
		this.bFixAplication = bFixAplication;
	}

	@Override
	public void run() {
		
			CallableStatement cs = null;
			BFixDbOperation bfixDbOperation = new BFixDbOperation();
			Connection conn = bfixDbOperation.createConnection();
			try {

				if (conn == null) {
					System.out.println("Trading Session Status Read Db Bağlantı Hatası! (1601)");
				} else {
					cs = conn.prepareCall("Select * from uop14.EXAPI_PC where PC_NO = ?");
					cs.setInt(1, BFix.pcNo); 
					ResultSet rs = cs.executeQuery();
					
					LinkedList<BFixTradingSessionStatus> bFixTradingSessionStatusList = new LinkedList<BFixTradingSessionStatus>();
					
					while (rs.next()) {
						
						BFixTradingSessionStatus bFixTradingSessionStatus = new BFixTradingSessionStatus();
						
						bFixTradingSessionStatus.setMarketSegmentID("P");
						bFixTradingSessionStatus.setTradSesStatus(rs.getString("ACILIS_MODU_ACIK").equals("H")?101:2);
						bFixTradingSessionStatus.setMarketName("Açılış Seansı Modu");
						bFixTradingSessionStatusList.add(bFixTradingSessionStatus.clone());
						
						
						bFixTradingSessionStatus.setMarketSegmentID("N");
						bFixTradingSessionStatus.setTradSesStatus(rs.getString("ULUSAL_PAZAR_ACIK").equals("H")?101:2);
						bFixTradingSessionStatus.setMarketName("Ulusal Pazar");
						bFixTradingSessionStatusList.add(bFixTradingSessionStatus.clone());
						
						bFixTradingSessionStatus.setMarketSegmentID("L");
						bFixTradingSessionStatus.setTradSesStatus(rs.getString("IKINCI_ULUSAL_PAZAR_ACIK").equals("H")?101:2);
						bFixTradingSessionStatus.setMarketName("İkinci Ulusal Pazar");
						bFixTradingSessionStatusList.add(bFixTradingSessionStatus.clone());
						
						bFixTradingSessionStatus.setMarketSegmentID("W");
						bFixTradingSessionStatus.setTradSesStatus(rs.getString("GOZALTI_PAZARI_ACIK").equals("H")?101:2);
						bFixTradingSessionStatus.setMarketName("Gözaltı Pazarı");
						bFixTradingSessionStatusList.add(bFixTradingSessionStatus.clone());
						
						bFixTradingSessionStatus.setMarketSegmentID("B");
						bFixTradingSessionStatus.setTradSesStatus(rs.getString("BIRINCIL_PIYASA_ACIK").equals("H")?101:2);
						bFixTradingSessionStatus.setMarketName("Birincil Piyasa");
						bFixTradingSessionStatusList.add(bFixTradingSessionStatus.clone());
						
						bFixTradingSessionStatus.setMarketSegmentID("K");
						bFixTradingSessionStatus.setTradSesStatus(rs.getString("KURUMSAL_URUNLER_PAZARI_ACIK").equals("H")?101:2);
						bFixTradingSessionStatus.setMarketName("Kurumsal Ürünler Pazarı");
						bFixTradingSessionStatusList.add(bFixTradingSessionStatus.clone());
						
						bFixTradingSessionStatus.setMarketSegmentID("G");
						bFixTradingSessionStatus.setTradSesStatus(rs.getString("GELISEN_ISLEMLER_PIYASASI_ACIK").equals("H")?101:2);
						bFixTradingSessionStatus.setMarketName("Gelişen İşletmeler Piyasası");
						bFixTradingSessionStatusList.add(bFixTradingSessionStatus.clone());
						
						bFixTradingSessionStatus.setMarketSegmentID("C");
						bFixTradingSessionStatus.setTradSesStatus(rs.getString("TEK_FIYAT_MODU_ACIK").equals("H")?104:22);
						bFixTradingSessionStatus.setMarketName("Tek Fiyat Modu");
						bFixTradingSessionStatusList.add(bFixTradingSessionStatus.clone());
						
						bFixTradingSessionStatus.setMarketSegmentID("S");
						bFixTradingSessionStatus.setTradSesStatus(rs.getString("SERBEST_ISL_PLATFORMU_ACIK").equals("H")?101:2);
						bFixTradingSessionStatus.setMarketName("Serbest İşlem Platformu");
						bFixTradingSessionStatusList.add(bFixTradingSessionStatus.clone());
						
						bFixTradingSessionStatus.setMarketSegmentID("Y");
						bFixTradingSessionStatus.setTradSesStatus(rs.getString("KAPANIS_MODU_ACIK").equals("H")?101:2);
						bFixTradingSessionStatus.setMarketName("Kapanış Seansı Modu");
						bFixTradingSessionStatusList.add(bFixTradingSessionStatus.clone());
						
						bFixTradingSessionStatus.setMarketSegmentID("Q");
						bFixTradingSessionStatus.setTradSesStatus(rs.getString("KOTASYON_EI_MODU_ACIK").equals("H")?101:2);
						bFixTradingSessionStatus.setMarketName("Kotasyon İletimi");
						bFixTradingSessionStatusList.add(bFixTradingSessionStatus.clone());
						
						bFixTradingSessionStatus.setMarketSegmentID("NLWKGS");
						bFixTradingSessionStatus.setTradSesStatus(rs.getString("KAPANIS_FIYAT_ISL_MODU_ACIK").equals("H")?111:110);
						bFixTradingSessionStatus.setMarketName("Kapanış Fiyatından İşlemler");
						bFixTradingSessionStatusList.add(bFixTradingSessionStatus.clone());
						
						bFixTradingSessionStatus.setMarketSegmentID("?");
						bFixTradingSessionStatus.setTradSesStatus(rs.getString("KAPANIS_SEANSI_EMIR_AKTARIM").equals("H")?111:110);
						bFixTradingSessionStatus.setMarketName("Kapanış Seansı Fiyat Aktarım Modu");
						bFixTradingSessionStatusList.add(bFixTradingSessionStatus.clone());
						
						
					}
					
					bFixAplication.bFixTradingSessionStatusList = bFixTradingSessionStatusList;
					bFixAplication.observableBFixTradingStatus.changeStatus();
					
					
					
				}
		} catch (Exception e) {
			System.out.println("Trading Session Status Read ExecuteQuery Hatası! (1602)");
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
