package com.bmd.bfix.process;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class BFixDbOperation {
	
	@SuppressWarnings("finally")
	public Connection createConnection() {
		String url = "jdbc:oracle:thin:@10.58.5.7:1521:ORCL";

		Properties props = new Properties();
		props.setProperty("user", "BFIX");
		props.setProperty("password", "caddebostan");
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, props);
		} catch (Exception e) {
			e.printStackTrace();
			System.out
					.println("Read Request Db Bağlantı Oluşturma Hatası! (1001)");
		} finally {
			return conn;
		}
	}

}
