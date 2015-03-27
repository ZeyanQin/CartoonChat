package com.lee.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

public class ClientRegister extends JFrame{

	/**
	 * cancel the warning
	 */
	private static final long serialVersionUID = 1L;
	private static final int REGISTER_WIDTH = 450;
	private static final int REGISTER_HEIGHT = 300;
	
	private JPanel contentPanel ;
	private JTextField textField ;
	private JPasswordField passwordField1;
	private JPasswordField passwordField2;
	private JLabel promptLabel;
	
	public ClientRegister() {
		setTitle("Register Account");
		setBounds(350, 250, REGISTER_WIDTH, REGISTER_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPanel =new JPanel() {
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon("images/registerscreen.jpg").getImage(), 0, 0, getWidth(), getHeight(), null);
			};			
		};
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		contentPanel.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(150, 42, 104, 21);
		textField.setOpaque(false);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		passwordField1 = new JPasswordField();
		passwordField1.setForeground(Color.BLACK);
		passwordField1.setEchoChar('*');
		passwordField1.setBounds(190, 98, 104, 21);
		passwordField1.setOpaque(false);
		contentPanel.add(passwordField1);
		
		passwordField2 = new JPasswordField();
		passwordField2.setForeground(Color.BLACK);
		passwordField2.setEchoChar('*');
		passwordField2.setBounds(192, 152, 104, 21);
		passwordField2.setOpaque(false);
		contentPanel.add(passwordField2);
		
		final JButton backButton = new JButton();
		backButton.setIcon(new ImageIcon("images/back.jpg"));
		backButton.setBounds(320, 198, 80, 40);
		getRootPane().setDefaultButton(backButton);
		contentPanel.add(backButton);
		
		final JButton registerButton = new JButton();
		registerButton.setIcon(new ImageIcon("images/register2.png"));
		registerButton.setBounds(230, 198, 80, 40);
		contentPanel.add(registerButton);		
		
		promptLabel = new JLabel();
		promptLabel.setBounds(55, 218, 185, 20);
		promptLabel.setForeground(Color.RED);
		contentPanel.add(promptLabel);
		
		backButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				backButton.setEnabled(false);
				new ClientLogin();
				setVisible(false);
			}
		});
		
		registerButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				Properties userProperties = new Properties();
				File file = new File("users.properties");
				CartoonUtil.loadPro(userProperties, file);
				
				String userName = textField.getText();
				String password1 = new String(passwordField1.getPassword());
				String password2 = new String(passwordField2.getPassword());
				
				if (userName.length() != 0 ) {
					if (userProperties.containsKey(userName)) {
						promptLabel.setText("Username alread exists");
					}else {
						if (password1.equals(password2)) {
							userProperties.setProperty(userName, password1);
							try {
								userProperties.store(new FileOutputStream(file), "Copyright (c) Andy lee");
							} catch (FileNotFoundException e1) {
								e1.printStackTrace();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							new ClientLogin();
							setVisible(false);
						} else {
							promptLabel.setText("Twice PassWord are different!");
						}
					}
				} else {
					promptLabel.setText("Username can't be empty!");
				}
			}
		});
		
		setVisible(true);
	}

}
