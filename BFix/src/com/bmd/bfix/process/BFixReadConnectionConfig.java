package com.bmd.bfix.process;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.Matchers.equalTo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import quickfix.ConfigError;
import quickfix.Dictionary;
import quickfix.FieldConvertError;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.field.BeginString;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;

import com.bmd.bfix.BFix;
import com.bmd.bfix.BFixApplication;
import com.bmd.bfix.model.BFixConnectionConfig;
import com.bmd.bfix.ui.BFixPanel;

public class BFixReadConnectionConfig {



	public static List<BFixConnectionConfig> ConnectionConfigList;

	static {
		readConnectionConfig();
	}

	public static void readConnectionConfig() {

		PreparedStatement preStmt = null;
		BFixDbOperation bfixDbOperation = new BFixDbOperation();
		Connection conn = bfixDbOperation.createConnection();
		try {

			if (conn == null) {
				JOptionPane.showMessageDialog(null,"Db Bağlantı Hatası! (7001)");
			} else {
				ConnectionConfigList = new ArrayList<BFixConnectionConfig>();
				preStmt = conn.prepareStatement("Select * from CONNECTION_CONFIG");
				//  where STATUS=1 and PC_NO=?
				//preStmt.setInt(1, pcNo); // TODO: PC No applicationdan alınıp eklenecek
				
				ResultSet rs = preStmt.executeQuery();
				while (rs.next()) {
					BFixConnectionConfig bFixConnectionConfig = new BFixConnectionConfig();
					bFixConnectionConfig.setId(rs.getInt(1));
					bFixConnectionConfig.setKey(rs.getString(2));
					bFixConnectionConfig.setValue(rs.getString(3));
					bFixConnectionConfig.setTerminalNo(rs.getInt(4));
					bFixConnectionConfig.setType(rs.getInt(5));
					bFixConnectionConfig.setStatus(rs.getInt(6));
					ConnectionConfigList.add(bFixConnectionConfig);
				}

			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,"ExecuteQuery Hatası! (7002)");
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
	public static SessionSettings getSessioonSettings(int terminalNo){
		SessionSettings sessionSettings = new SessionSettings();
		try{
		BeginString beginString = new BeginString( ((BFixConnectionConfig) selectFirst(ConnectionConfigList,
				having(on(BFixConnectionConfig.class).getKey(), equalTo("BeginString")).and(
						having(on(BFixConnectionConfig.class).getTerminalNo(), equalTo(terminalNo)).and(
						having(on(BFixConnectionConfig.class).getStatus(),
								equalTo(1)))))).getValue());
		SenderCompID senderCompID = new SenderCompID( ((BFixConnectionConfig) selectFirst(ConnectionConfigList,
				having(on(BFixConnectionConfig.class).getKey(), equalTo("SenderCompID")).and(
						having(on(BFixConnectionConfig.class).getTerminalNo(), equalTo(terminalNo)).and(
						having(on(BFixConnectionConfig.class).getStatus(),
								equalTo(1)))))).getValue());
		TargetCompID targetCompID = new TargetCompID( ((BFixConnectionConfig) selectFirst(ConnectionConfigList,
				having(on(BFixConnectionConfig.class).getKey(), equalTo("TargetCompID")).and(
						having(on(BFixConnectionConfig.class).getTerminalNo(), equalTo(terminalNo)).and(
						having(on(BFixConnectionConfig.class).getStatus(),
								equalTo(1)))))).getValue());
		
		SessionID sessionID = new SessionID(beginString, senderCompID, targetCompID);
        Dictionary dictionary = new Dictionary();
		List<BFixConnectionConfig> tmpConnectionConfigList = select(ConnectionConfigList,
				having(on(BFixConnectionConfig.class).getTerminalNo(), equalTo(terminalNo)).and(
						having(on(BFixConnectionConfig.class).getStatus(),
								equalTo(1))));
		BFix.pcNo =Integer.valueOf(((BFixConnectionConfig) selectFirst(ConnectionConfigList,
				having(on(BFixConnectionConfig.class).getKey(), equalTo("PcNo")).and(
						having(on(BFixConnectionConfig.class).getTerminalNo(), equalTo(terminalNo)).and(
						having(on(BFixConnectionConfig.class).getStatus(),
								equalTo(1)))))).getValue());
		
		
		for (BFixConnectionConfig bfixConnectionConfig : tmpConnectionConfigList){
			dictionary.setString(bfixConnectionConfig.getKey(), bfixConnectionConfig.getValue());
		}
		
		
			sessionSettings.set(sessionID, dictionary);
			BFixApplication.activeSessionID = sessionID;
				BFixPanel.txbKullanici.setText(sessionSettings.getString(sessionID, "Username"));
				BFixPanel.txbServer.setText(sessionSettings.getString(sessionID, "SocketConnectHost"));
		}catch(NullPointerException e){
			JOptionPane.showMessageDialog(null,"Bu Terminal No İçin Gerekli Ayarlar Bulunamadı! (7003)");
			e.printStackTrace();
		}catch (ConfigError e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null,"getSessioonSettings set Hatası! (7004)");
			e.printStackTrace();
		}catch (FieldConvertError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sessionSettings;
	}
	public static SessionSettings getSessioonSettings(int terminalNo,String ip){
		SessionSettings sessionSettings = getSessioonSettings(terminalNo);
		sessionSettings.setString("SocketConnectHost", ip);
		return sessionSettings;
	}
	
	
}
