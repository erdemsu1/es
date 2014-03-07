/*******************************************************************************
 * Copyright (c) quickfixengine.org  All rights reserved. 
 * 
 * This file is part of the QuickFIX FIX Engine 
 * 
 * This file may be distributed under the terms of the quickfixengine.org 
 * license as defined by quickfixengine.org and appearing in the file 
 * LICENSE included in the packaging of this file. 
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING 
 * THE WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 * 
 * See http://www.quickfixengine.org/LICENSE for licensing information. 
 * 
 * Contact ask@quickfixengine.org if any conditions of this licensing 
 * are not clear to you.
 ******************************************************************************/

package com.bmd.bfix.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Console;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.bmd.bfix.BFixApplication;
import com.bmd.bfix.BFix;
import com.bmd.bfix.BFixApplication.MessageProcessor;
import com.bmd.bfix.model.BFixExecutionTableModel;
import com.bmd.bfix.model.BFixOrderTableModel;
import com.bmd.bfix.model.BFixQuoteOrderTableModel;
import com.bmd.bfix.model.BFixTradingSessionStatus;
import com.bmd.bfix.observable.ObservableBFixTradingStatus;
import com.bmd.bfix.process.BFixReadTradingSessionStatus;
import com.bmd.bfix.process.BFixTradingSessionStatusChange;

import javax.swing.JTextPane;
import java.awt.CardLayout;
import java.awt.Button;
import javax.swing.JToggleButton;
import java.awt.GridLayout;
import javax.swing.SwingConstants;
import javax.swing.JInternalFrame;

import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;

/**
 * Main content panel
 */
public class BFixPanel extends JPanel implements Observer, ActionListener {

	private BFixOrderPanel orderPanel = null;
	private BFixExecutionPanel bFixExecutionPanel = null;
	private BFixQuotePanel bFixQuotePanel = null;
	private BFixOrderTableModel orderTableModel = null;
	private BFixExecutionTableModel bFixExecutionTableModel = null;
	private static JButton btnBaglan = new JButton("Bağlan");
	private static JLabel StatusIcon;
	public static JTextField txbKullanici = new JTextField();
	public static JTextField txbServer = new JTextField();
	private JPanel panel = new JPanel();
	public static JTextField txbTerminalNo;
	public static JButton btnPcNoUygula;
	public static JTextArea txaConsole;
	public static JLabel lblGonderilenSayisi;
	public static JLabel lblIletilenSayisi;
	public static JLabel lblReddedilenSayisi;
	public static JTextField txbPcNo;
	public static JPanel panel_5 = new JPanel();
	private JPanel panel_8 = new JPanel();
	private JPanel panel_2 = new JPanel();
	private JPanel panel_7 = new JPanel();
	private JPanel panel_6 = new JPanel();
	public BFixApplication application;
	private JInternalFrame frmManuelMesajAl = new JInternalFrame(
			"Manuel Mesaj Alma");
	private JTextArea txaManuelMesajAl = new JTextArea();

	public BFixPanel(BFixOrderTableModel orderTableModel,
			BFixExecutionTableModel bFixExecutionTableModel,
			BFixQuoteOrderTableModel bFixQuoteOrderTableModel,
			final BFixApplication application) {
		this.application = application;
		setName("BFixPanel");
		this.orderTableModel = orderTableModel;
		this.bFixExecutionTableModel = bFixExecutionTableModel;
		setLayout(null);
		application.observableBFixTradingStatus.addObserver(this);

		frmManuelMesajAl.setClosable(true);
		frmManuelMesajAl.setVisible(false);

		frmManuelMesajAl.setBounds(0, 0, 633, 172);
		add(frmManuelMesajAl);
		frmManuelMesajAl.getContentPane().setLayout(null);

		JButton btnMesajiIsle = new JButton("Mesajı İşle");
		btnMesajiIsle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SessionID sess = application.activeSessionID;
				String[] manualMessages = txaManuelMesajAl.getText()
						.split("\n");
				for (String messageText : manualMessages) {
					if (!messageText.isEmpty()) {
						Message message;
						try {
							message = new Message(messageText);
					
							application.fromApp(message, sess);
						} catch (FieldNotFound | IncorrectDataFormat |InvalidMessage
								| IncorrectTagValue | UnsupportedMessageType e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		});
		btnMesajiIsle.setBounds(518, 106, 89, 23);
		frmManuelMesajAl.getContentPane().add(btnMesajiIsle);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 597, 84);
		frmManuelMesajAl.getContentPane().add(scrollPane);

		scrollPane.setViewportView(txaManuelMesajAl);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 700, 21);
		add(menuBar);

		JMenu mnUygulama = new JMenu("Uygulama");
		menuBar.add(mnUygulama);

		JMenuItem mnCikis = new JMenuItem("Çıkış");
		mnUygulama.add(mnCikis);

		JMenu mnAyarlar = new JMenu("Ayarlar");
		menuBar.add(mnAyarlar);

		JMenu mnAraclar = new JMenu("Araclar");
		menuBar.add(mnAraclar);

		JMenuItem mnMesajGnder = new JMenuItem("Manuel Mesaj Gönder");
		mnAraclar.add(mnMesajGnder);

		JMenuItem mnManuelMesajAl = new JMenuItem("Manuel Mesaj Al");
		mnManuelMesajAl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// frmManuelMesajAl.setVisible(true);
				frmManuelMesajAl.show();
			}
		});
		mnAraclar.add(mnManuelMesajAl);

		JMenu mnYardm = new JMenu("Yardım");
		menuBar.add(mnYardm);

		panel.setBorder(new TitledBorder(null, "Ba\u011Flant\u0131",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 26, 676, 78);
		add(panel);
		panel.setLayout(null);
		btnBaglan.setFont(new Font("SansSerif", Font.PLAIN, 11));

		btnBaglan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (BFixApplication.connectionStatus == 0) {
					application.bFixLogon();

				} else if (BFixApplication.connectionStatus == -1) {
					application.bFixLogoff();
					application.onLogonBreak();

				} else {
					application.bFixLogoff();

				}
				// btnBaglan.setEnabled(false);
				// StatusIcon.removeAll();
				// StatusIcon = new JLabel( new
				// ImageIcon("C:\\shoppingmall\\workspace\\BFix\\Resources\\Images\\led_green_32.png"));

				// StatusIcon.setVisible(false);
				// StatusIcon = new JLabel( new
				// ImageIcon("C:\\shoppingmall\\workspace\\BFix\\Resources\\Images\\led_green_32.png"));
				// StatusIcon.setBounds(484, 10, 32, 32);

				// panel.add(StatusIcon);
				// StatusIcon.setVisible(true);
				// JOptionPane.showMessageDialog(null, "Welcome to Java!",
				// "test",0);
			}
		});
		btnBaglan.setBounds(566, 16, 100, 23);
		panel.add(btnBaglan);

		StatusIcon = new JLabel(new ImageIcon(BFixPanel.class.getClass()
				.getResource("/led_red_32.png")));

		StatusIcon.setBounds(524, 13, 32, 32);
		panel.add(StatusIcon);

		JLabel lblKullanici = new JLabel("Kullanıcı :");
		lblKullanici.setFont(new Font("SansSerif", Font.PLAIN, 11));
		lblKullanici.setBounds(12, 17, 56, 14);
		panel.add(lblKullanici);

		txbKullanici.setBounds(73, 14, 110, 20);
		txbKullanici.setEnabled(false);
		panel.add(txbKullanici);

		JLabel lblServer = new JLabel("Server :");
		lblServer.setFont(new Font("SansSerif", Font.PLAIN, 11));
		lblServer.setBounds(207, 16, 46, 14);
		panel.add(lblServer);
		txbServer.setEnabled(false);
		txbServer.setBounds(262, 13, 108, 20);
		panel.add(txbServer);

		JLabel lblMsgNoReset = new JLabel("Mesaj No Reset :");
		lblMsgNoReset.setFont(new Font("SansSerif", Font.PLAIN, 11));
		lblMsgNoReset.setBounds(406, 14, 98, 14);
		panel.add(lblMsgNoReset);

		JCheckBox chkReset = new JCheckBox();
		chkReset.setBounds(497, 11, 21, 20);
		chkReset.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				application.setResetSeqNumFlag(((JCheckBox) e.getSource())
						.isSelected());

			}
		});
		panel.add(chkReset);

		JLabel lblTerminal = new JLabel("Terminal :");
		lblTerminal.setFont(new Font("SansSerif", Font.PLAIN, 11));
		lblTerminal.setBounds(10, 46, 62, 14);
		panel.add(lblTerminal);

		txbTerminalNo = new JTextField();
		txbTerminalNo.setBounds(73, 43, 32, 20);
		panel.add(txbTerminalNo);

		btnPcNoUygula = new JButton("Uygula");
		btnPcNoUygula.setFont(new Font("SansSerif", Font.PLAIN, 9));
		btnPcNoUygula.setBounds(114, 41, 70, 23);
		btnPcNoUygula.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					BFix.get().createSettings(
							Integer.valueOf(txbTerminalNo.getText()));
					txbPcNo.setText(String.valueOf(BFix.pcNo));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panel.add(btnPcNoUygula);

		txbPcNo = new JTextField();
		txbPcNo.setEnabled(false);
		txbPcNo.setBounds(262, 43, 32, 20);
		panel.add(txbPcNo);

		JLabel lblPcno = new JLabel("PcNo :");
		lblPcno.setFont(new Font("SansSerif", Font.PLAIN, 11));
		lblPcno.setBounds(216, 46, 39, 14);
		panel.add(lblPcno);

		JButton btnKontrolPanel = new JButton("Pazar Durumunu Gör");
		btnKontrolPanel.setFont(new Font("SansSerif", Font.PLAIN, 10));
		btnKontrolPanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) (panel_5.getLayout());
				cl.next(panel_5);
				JButton btnPazarDurum = (JButton) e.getSource();
				if (btnPazarDurum.getText().equals("Pazar Durumunu Gör"))
					btnPazarDurum.setText("Emir Durumunu Gör");
				else
					btnPazarDurum.setText("Pazar Durumunu Gör");
				// cl.show(panel_5, (String)evt.getItem());
			}
		});
		btnKontrolPanel.setBounds(525, 45, 141, 23);
		panel.add(btnKontrolPanel);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(14, 520, 672, 20);
		add(panel_1);
		panel_1.setBorder(UIManager.getBorder("FileChooser.listViewBorder"));
		panel_1.setLayout(null);

		JLabel lblGidenEmirSays = new JLabel("Gönderilen Emir Sayısı");
		lblGidenEmirSays.setFont(new Font("SansSerif", Font.PLAIN, 11));
		lblGidenEmirSays.setBounds(3, 1, 107, 14);
		panel_1.add(lblGidenEmirSays);

		JLabel label = new JLabel(":");
		label.setBounds(114, 1, 11, 14);
		panel_1.add(label);

		JLabel lblIletilenEmirSayisi = new JLabel("İletilen Emir Sayısı");
		lblIletilenEmirSayisi.setFont(new Font("SansSerif", Font.PLAIN, 11));
		lblIletilenEmirSayisi.setBounds(257, 1, 85, 14);
		panel_1.add(lblIletilenEmirSayisi);

		JLabel label_1 = new JLabel(":");
		label_1.setBounds(346, 1, 11, 14);
		panel_1.add(label_1);

		JLabel lblReddedilenEmirSayisi = new JLabel("Reddedilen Emir Sayısı");
		lblReddedilenEmirSayisi.setFont(new Font("SansSerif", Font.PLAIN, 11));
		lblReddedilenEmirSayisi.setBounds(487, 1, 111, 14);
		panel_1.add(lblReddedilenEmirSayisi);

		JLabel label_2 = new JLabel(":");
		label_2.setBounds(599, 1, 13, 14);
		panel_1.add(label_2);

		lblGonderilenSayisi = new JLabel("0");
		lblGonderilenSayisi.setFont(new Font("SansSerif", Font.PLAIN, 11));
		lblGonderilenSayisi.setBounds(120, 2, 46, 14);
		panel_1.add(lblGonderilenSayisi);

		lblIletilenSayisi = new JLabel("0");
		lblIletilenSayisi.setFont(new Font("SansSerif", Font.PLAIN, 11));
		lblIletilenSayisi.setBounds(352, 2, 46, 14);
		panel_1.add(lblIletilenSayisi);

		lblReddedilenSayisi = new JLabel("0");
		lblReddedilenSayisi.setFont(new Font("SansSerif", Font.PLAIN, 11));
		lblReddedilenSayisi.setBounds(608, 2, 46, 14);
		panel_1.add(lblReddedilenSayisi);

		panel_5.setBounds(9, 109, 677, 401);
		add(panel_5);
		panel_5.setLayout(new CardLayout(0, 0));

		JPanel panel_3 = new JPanel();
		panel_5.add(panel_3, "name_804493839244155");
		panel_3.setLayout(null);

		JButton btnTemizle = new JButton("Temizle");
		btnTemizle.setBounds(597, 89, 76, 23);
		panel_3.add(btnTemizle);
		btnTemizle.setFont(new Font("SansSerif", Font.PLAIN, 9));
		btnTemizle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txaConsole.setText("");

			}
		});

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(1, 95, 674, 301);
		panel_3.add(tabbedPane);
		orderPanel = new BFixOrderPanel(orderTableModel);
		bFixExecutionPanel = new BFixExecutionPanel(bFixExecutionTableModel);
		bFixQuotePanel = new BFixQuotePanel(bFixQuoteOrderTableModel);
		tabbedPane.add("Gönderilenler", orderPanel);

		tabbedPane.add("Gerçekleşenler", bFixExecutionPanel);
		tabbedPane.add("Kotasyonlar", bFixQuotePanel);

		txaConsole = new JTextArea();
		txaConsole.setBounds(16, 101, 620, 84);

		JScrollPane jScrollPane = new JScrollPane();
		jScrollPane.setBounds(0, 2, 674, 84);
		panel_3.add(jScrollPane);
		jScrollPane.setViewportView(txaConsole);

		JPanel panel_4 = new JPanel();
		panel_5.add(panel_4, "name_804493908912620");
		panel_4.setLayout(null);

		panel_2.setBounds(5, 18, 164, 230);
		panel_2.setLayout(new GridLayout(9, 1, 0, 10));
		panel_4.add(panel_2);

		panel_6.setBounds(175, 18, 73, 230);
		panel_4.add(panel_6);
		panel_6.setLayout(new GridLayout(9, 1, 0, 10));

		panel_7.setBounds(339, 18, 213, 230);
		panel_4.add(panel_7);
		panel_7.setLayout(new GridLayout(9, 1, 0, 10));

		panel_8.setBounds(558, 18, 73, 230);
		panel_4.add(panel_8);
		panel_8.setLayout(new GridLayout(9, 1, 0, 10));

		JButton btnYenile = new JButton("Yenile");
		btnYenile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BFixReadTradingSessionStatus bFixReadTradingSessionStatus = new BFixReadTradingSessionStatus(
						application);
				bFixReadTradingSessionStatus.run();

			}
		});
		btnYenile.setBounds(258, 22, 71, 23);
		panel_4.add(btnYenile);

		BFixReadTradingSessionStatus bFixReadTradingSessionStatus = new BFixReadTradingSessionStatus(
				application);
		bFixReadTradingSessionStatus.run();

	}

	public static void logonStatus(int status) {
		if (status == -1) {
			btnBaglan.setText("Bağlanıyor");
			StatusIcon.setIcon(new ImageIcon(BFixPanel.class.getClass()
					.getResource("/led_yellow_32.png")));
			setPcNoState(false);
		} else if (status == 1) {
			btnBaglan.setText("Bağlantıyı Kes");
			StatusIcon.setIcon(new ImageIcon(BFixPanel.class.getClass()
					.getResource("/led_green_32.png")));
			setPcNoState(false);
		} else {
			btnBaglan.setText("Bağlan");
			StatusIcon.setIcon(new ImageIcon(BFixPanel.class.getClass()
					.getResource("/led_red_32.png")));
			setPcNoState(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	@Override
	public synchronized void update(Observable o, Object arg) {

		panel_2.removeAll();
		panel_6.removeAll();
		panel_7.removeAll();
		panel_8.removeAll();
		int labelCount = 0;
		for (BFixTradingSessionStatus bFixTradingSessionStatus : application.bFixTradingSessionStatusList) {

			JLabel label_3 = new JLabel(
					bFixTradingSessionStatus.getMarketName());
			label_3.setHorizontalAlignment(SwingConstants.RIGHT);
			label_3.setFont(new Font("SansSerif", Font.PLAIN, 11));

			OnOffButton tglAcilisModu = new OnOffButton();
			tglAcilisModu.setToolTipText(bFixTradingSessionStatus
					.getMarketName());
			tglAcilisModu
					.setText(bFixTradingSessionStatus.getMarketSegmentID());
			tglAcilisModu.setName("tgl"
					+ bFixTradingSessionStatus.getMarketName());
			if (bFixTradingSessionStatus.getTradSesStatus() == 101
					|| bFixTradingSessionStatus.getTradSesStatus() == 111
					|| bFixTradingSessionStatus.getTradSesStatus() == 104) {
				tglAcilisModu.setSelected(false);
			} else
				tglAcilisModu.setSelected(true);
			if (bFixTradingSessionStatus.getTradSesStatus() != 2
					&& bFixTradingSessionStatus.getTradSesStatus() != 101) {
				tglAcilisModu.setEnabled(false);

			} else {
				tglAcilisModu.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						OnOffButton tBtn = (OnOffButton) e.getSource();
						BFixTradingSessionStatus bFixTradingSessionStatus = new BFixTradingSessionStatus();
						bFixTradingSessionStatus.setMarketSegmentID(tBtn
								.getText());
						if (tBtn.isSelected()) {
							bFixTradingSessionStatus.setTradSesStatus(101);
						} else {
							bFixTradingSessionStatus.setTradSesStatus(2);
						}
						(new Thread(new BFixTradingSessionStatusChange(
								bFixTradingSessionStatus, application)))
								.start();
					}
				});
			}
			if (labelCount < 9) {
				panel_2.add(label_3);
				panel_6.add(tglAcilisModu);
			} else {
				panel_7.add(label_3);
				panel_8.add(tglAcilisModu);
			}
			labelCount++;
		}
		CardLayout cl = (CardLayout) (panel_5.getLayout());
		cl.next(panel_5);
		cl.next(panel_5);
	}

	public static void setPcNoState(boolean durum) {
		txbTerminalNo.setEnabled(durum);
		btnPcNoUygula.setEnabled(durum);
	}
}
