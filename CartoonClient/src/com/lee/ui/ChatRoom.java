package com.lee.ui;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.TextArea;
import java.io.File;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ProgressBarUI;

import com.lee.function.CartoonBean;
import com.lee.function.ClientRequst;
import com.lee.util.CartoonUtil;
import com.lee.util.MyCellRender;
import com.lee.util.OnlineListModel;

public class ChatRoom  extends JFrame{

	private static final long serialVersionUID = 1L;
	private final static int ROOM_WIDTH = 700;
	private final static int ROOM_HEIGHT = 550;
	
	private static JPanel contentPanel;
	private static Socket socket;
	private static String userName;
	private static Vector<String> onlines;
	private static JTextArea disArea;
	private static AbstractListModel<String> onlineList;
	private static JList<String> list;
	private static JProgressBar progressBarUI;
	private static JLabel promptSendUI;
	private static ObjectOutputStream oos;
	private static File file;
	private static URI url1,url2;
	private static AudioClip audio1,audio2;
	private static ObjectInputStream ois;

	

	public ChatRoom(String userName, Socket socket) {
		this.userName = userName;
		this.socket = socket;
		onlines = new Vector<String>();
		SwingUtilities.updateComponentTreeUI(this);

		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());			
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}	
		
		setTitle(userName);
		setResizable(false);
		setBounds(200, 200, ROOM_WIDTH, ROOM_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon("images/chatroom.jpg").getImage(), 0, 0, getWidth(), getHeight(), null);
			}			
		};

		setContentPane(contentPanel);
		contentPanel.setLayout(null);
		
		/*chat message area*/
		JScrollPane disPane = new JScrollPane();
		disPane.setBounds(10,10,410,300);
		getContentPane().add(disPane);
		
		disArea = new JTextArea();
		disArea.setEditable(false);
		disArea.setLineWrap(true); 
		disArea.setWrapStyleWord(true);
		disArea.setFont(new Font("Andylee", Font.BOLD, 13));
		disPane.setViewportView(disArea);
		
		/*input box*/
		JScrollPane inputPane = new JScrollPane();
		inputPane.setBounds(10,347,411,97);
		contentPanel.add(inputPane);
		
		final JTextArea inputArea = new JTextArea();
		inputArea.setLineWrap(true);
		inputArea.setWrapStyleWord(true);
		inputPane.setViewportView(inputArea);
		
		/*close button*/
		final JButton closeButton = new JButton("close");
		closeButton.setBounds(214, 448, 80, 30);
		contentPanel.add(closeButton);
		
		/*send button*/
		final JButton sendButton = new JButton("send");
		sendButton.setBounds(313, 448, 80, 30);
		contentPanel.add(sendButton);
		
		/*user list*/
		onlineList = new OnlineListModel(onlines);
		list = new JList<>(onlineList);
		list.setCellRenderer(new MyCellRender());
		list.setOpaque(false);
		Border etchdBorder = BorderFactory.createEtchedBorder();
		list.setBorder(BorderFactory.createTitledBorder(etchdBorder,  "<"+userName+">"
                + "online:", TitledBorder.LEADING, TitledBorder.TOP, new Font("andylee", Font.BOLD, 20), Color.green));
		JScrollPane listPane = new JScrollPane(list);
		listPane.setBounds(430, 10, 245, 375);
		listPane.setOpaque(false);
		listPane.getViewport().setOpaque(false);
		contentPanel.add(listPane);
		
		/*files sending bar*/
		progressBarUI = new JProgressBar();
		progressBarUI.setBounds(430, 390, 245, 15);
		progressBarUI.setMinimum(1);
		progressBarUI.setMaximum(100);
		contentPanel.add(progressBarUI);
		
		/*files sending prompt*/
		promptSendUI = new JLabel("file sending:");
		promptSendUI.setFont(new Font("andylee", Font.PLAIN, 12));
		promptSendUI.setBackground(Color.WHITE);
		promptSendUI.setBounds(430, 410, 245, 15);
		contentPanel.add(progressBarUI);
		
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			CartoonBean cartoonBean = new CartoonBean();
			cartoonBean.setType(ClientRequst.PRIVATE);
			cartoonBean.setTimer(CartoonUtil.getTimer());
			oos.writeObject(cartoonBean);
			oos.flush();
			
			/*msg prompt*/
			file = new File("sounds/ding.wav");
			url1  = file.toURI();
			audio1 = Applet.newAudioClip(url1.toURL());
			
			/*online prompt*/
			file = new File("sounds/neo.wav");
			url2 = file.toURI();
			audio2 = Applet.newAudioClip(url2.toURL());
					
			new Thread(new InputThread()).start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		setVisible(true);
	}
	
	
	class InputThread implements Runnable {

		public void run() {
			while(true) {
				try {
					ois = new ObjectInputStream(socket.getInputStream());
					final CartoonBean cartoonBean = (CartoonBean) ois.readObject();
					switch (cartoonBean.getType()) {
					case PRIVATE: // update list
						onlines.clear();
						HashSet<String> clients = cartoonBean.getClients();
						Iterator<String> iterator = clients.iterator();
						while (iterator.hasNext()) {
							String name = iterator.next();
							if (userName.equals(name)) {
								onlines.add(name+"local");
							} else {
								onlines.add(name);
							}
						}
						
						onlineList = new OnlineListModel(onlines);
						list.setModel(onlineList);
						audio2.play();
						disArea.append(cartoonBean.getInfo()+"\r\n");
						disArea.selectAll();
						
						break;
					case UPDATE:
						return;
						//break;
					case OFFLINE:
						
						break;
					case SEND:
						
						break;
					case RECIVE:
						
						break;
	
					default:
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("get input stream failed");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}

}
