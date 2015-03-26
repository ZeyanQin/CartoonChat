package com.lee.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.lee.util.CartoonUtil;

public class ClientLogin extends JFrame {

	/**
	 * Just for canceling the warning
	 */
	private static final long serialVersionUID = 1L;
	private static final int LOGIN_WIDTH = 450;
	private static final int LOGIN_HEIGHT = 300;
	
	private JPanel contentPanel ;
	private JTextField textField;
	private JPasswordField passwordField;


	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new ClientLogin();
			}
		});
	}
	
	public ClientLogin() {
		setTitle("Cartoon chat       Author:Andylee");
		setBounds(350, 250, LOGIN_WIDTH, LOGIN_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon("images/loginscreen.jpg").getImage(), 0, 0, getWidth(), getHeight(), null);
			}			
		};
		contentPanel.setBorder(new EmptyBorder(5,5,5,5));
		setContentPane(contentPanel);
		contentPanel.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(135, 158, 104, 21);
		textField.setOpaque(false);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setForeground(Color.BLACK);
		passwordField.setEchoChar('*');
		passwordField.setBounds(135, 193, 104, 21);
		passwordField.setOpaque(false);
		contentPanel.add(passwordField);
		
		final JButton loginButton = new JButton();
		loginButton.setIcon(new ImageIcon("images/login.jpg"));
		loginButton.setBounds(236, 227, 50, 25);
		getRootPane().setDefaultButton(loginButton);
		contentPanel.add(loginButton);
		
		final JButton registerButton = new JButton();
		registerButton.setIcon(new ImageIcon("images/register1.jpg"));
		registerButton.setBounds(307, 227, 50, 25);
		contentPanel.add(registerButton);		
		
		final JLabel promptLabel = new JLabel();
		promptLabel.setBounds(110, 136, 250, 21);
		promptLabel.setForeground(Color.RED);
		//getContentPane().add(promptLabel);
		contentPanel.add(promptLabel);
		
		loginButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				Properties userProperties = new Properties();
				File file = new File("users.properties");
				CartoonUtil.loadPro(userProperties, file);
				String userName = textField.getText();
				if (file.length() != 0) {
					
				} else {
					promptLabel.setText("You enter user name doesn't exit!");
					textField.setText("");
					passwordField.setText("");
					textField.requestFocus();
				}
				
			}
		});
		
		registerButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {

				
			}
		});
		
		setVisible(true);
	}
}

