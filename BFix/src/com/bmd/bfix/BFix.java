package com.bmd.bfix;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.quickfixj.jmx.JmxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.Dictionary;
import quickfix.FileStoreFactory;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.ScreenLogFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.field.BeginString;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;

import com.bmd.bfix.model.BFixExecutionTableModel;
import com.bmd.bfix.model.BFixOrderTableModel;
import com.bmd.bfix.model.BFixQuoteOrderTableModel;
import com.bmd.bfix.process.BFixReadConnectionConfig;
import com.bmd.bfix.ui.BFixFrame;
import com.bmd.bfix.ui.BFixPanel;



public class BFix implements Observer {

	private static final CountDownLatch shutdownLatch = new CountDownLatch(1);
	private static Logger log = LoggerFactory.getLogger(BFix.class);
	private static BFix bfix;
	private boolean initiatorStarted = false;
	private static Initiator initiator = null;
	public static SessionSettings settings = null;
	public BFixApplication application;
	private JFrame frame = null;

	public static int terminalNo=0;
	public static int pcNo=0;
	public JmxExporter exporter = new JmxExporter();
	
	public BFix(String[] args) throws Exception {
		if (args.length >0) {
			terminalNo = Integer.valueOf(args[0]);
		}
		else{
			JOptionPane.showMessageDialog(null,"Terminal no parametresi verilmelidir!");
			return;
		}
		//SessionSettings settings = new SessionSettings(inputStream);
        
        if(!createSettings(terminalNo)){
        	JOptionPane.showMessageDialog(null,"Veritabanından ayarlar alınamadı!");
        	return;
        }
        	
        //settings.setString(SessionSettings.SENDERCOMPID, "1628");
        
        BFixOrderTableModel orderTableModel = new BFixOrderTableModel();
        BFixQuoteOrderTableModel bFixQuoteOrderTableModel = new BFixQuoteOrderTableModel();
        BFixExecutionTableModel bFixExecutionTableModel = new BFixExecutionTableModel();
        application = new BFixApplication(orderTableModel,bFixExecutionTableModel,bFixQuoteOrderTableModel);
        
        application.addBFixLogonObserver(this);
        
        frame = new BFixFrame(orderTableModel, bFixExecutionTableModel, bFixQuoteOrderTableModel, application);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BFixPanel.txbTerminalNo.setText(""+terminalNo);
        BFixPanel.txbPcNo.setText(""+pcNo);
	}

	public static int getTerminalNo() {
		return terminalNo;
	}

	public static void setTerminalNo(int terminalNo) {
		BFix.terminalNo = terminalNo;
	}

	public synchronized void logon() {
		if (!initiatorStarted) {
			try {
				boolean logHeartbeats = Boolean.valueOf(System.getProperty("logHeartbeats", "true")).booleanValue();
				MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
				LogFactory logFactory = new ScreenLogFactory(true, true, true, logHeartbeats);
		        MessageFactory messageFactory = new DefaultMessageFactory();
		      
		        if(initiator!=null)
				exporter.export(initiator);
				initiator =  new SocketInitiator(application, messageStoreFactory, settings, logFactory,
		                messageFactory);
				
		        exporter.register(initiator);
		        
				initiator.start();
				initiatorStarted = true;
			} catch (Exception e) {
				log.error("Logon failed", e);
			}
		} else {
			Session.lookupSession(application.activeSessionID).logon();
			/*
			Iterator<SessionID> sessionIds = initiator.getSessions().iterator();
			while (sessionIds.hasNext()) {
				SessionID sessionId = (SessionID) sessionIds.next();
				Session.lookupSession(sessionId).logon();
			}
			*/
		}
	}

	public static BFix get() {
		return bfix;
	}
    
	public void logout() {
		Session.lookupSession(application.activeSessionID).logout();
		initiator.stop();
		initiatorStarted=false;
		 shutdownLatch.countDown();
		/*
        Iterator<SessionID> sessionIds = initiator.getSessions().iterator();
        while (sessionIds.hasNext()) {
            SessionID sessionId = (SessionID) sessionIds.next();
            Session.lookupSession(sessionId).logout("user requested");
        }
        */
    }
	public void logonBreak() {
		initiator.stop(true);
		initiatorStarted = false;
		
	}

    public void stop() {
        //shutdownLatch.countDown();
    }

    public JFrame getFrame() {
        return frame;
    }

	public static void main(String args[]) throws Exception {
		try {
			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		bfix = new BFix(args);
		//if (!System.getProperties().containsKey("openfix")) {
			//bfix.logon();
		//}
		//shutdownLatch.await();
		/*while(true){
			(new Thread(new BfixReadRequest(initiator.getSessions().get(0),getConn() ))).start();
		}*/
	}
	
	
	
	public void update(Observable o, Object arg) {
		if (BFixApplication.connectionStatus == 0)
			logon();
		else if (BFixApplication.connectionStatus == -1)
			logonBreak();
		else 
			logout();
		
		
	}
	
	public boolean createSettings(int terminalNo){
		settings = BFixReadConnectionConfig.getSessioonSettings(terminalNo);
        return true;
	}


	

	
	
}
