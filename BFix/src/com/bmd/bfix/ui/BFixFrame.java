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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.bmd.bfix.BFixApplication;
import com.bmd.bfix.BFix;

import com.bmd.bfix.model.BFixExecutionTableModel;
import com.bmd.bfix.model.BFixOrderTableModel;
import com.bmd.bfix.model.BFixQuoteOrderTableModel;



/**
 * Main application window
 */
public class BFixFrame extends JFrame {

    public BFixFrame(BFixOrderTableModel orderTableModel, BFixExecutionTableModel bFixExecutionTableModel,BFixQuoteOrderTableModel bFixQuoteOrderTableModel,BFixApplication application) {
        super();
        setResizable(false);
        setTitle("BFix");
        setSize(700, 573);

        if (System.getProperties().containsKey("openfix")) {
            //createMenuBar(application);
        }
        getContentPane().add(new BFixPanel(orderTableModel, bFixExecutionTableModel,bFixQuoteOrderTableModel,  application),
                BorderLayout.CENTER);
        setVisible(true);
    }

    private void createMenuBar(final BFixApplication application) {
        JMenuBar menubar = new JMenuBar();
   
        JMenu sessionMenu = new JMenu("Session");
        menubar.add(sessionMenu);

        JMenuItem logonItem = new JMenuItem("Logon");
        logonItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                BFix.get().logon();
            }
        });
        sessionMenu.add(logonItem);

        JMenuItem logoffItem = new JMenuItem("Logoff");
        logoffItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	BFix.get().logout();
            }
        });
        sessionMenu.add(logoffItem);

        JMenu appMenu = new JMenu("Application");
        menubar.add(appMenu);

        JMenuItem appAvailableItem = new JCheckBoxMenuItem("Available");
        appAvailableItem.setSelected(application.isAvailable());
        appAvailableItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                application.setAvailable(((JCheckBoxMenuItem) e.getSource()).isSelected());
            }
        });
        appMenu.add(appAvailableItem);

        JMenuItem sendMissingFieldRejectItem = new JCheckBoxMenuItem("Send Missing Field Reject");
        sendMissingFieldRejectItem.setSelected(application.isMissingField());
        sendMissingFieldRejectItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                application.setMissingField(((JCheckBoxMenuItem) e.getSource()).isSelected());
            }
        });
        appMenu.add(sendMissingFieldRejectItem);

        setJMenuBar(menubar);
    }
    
}