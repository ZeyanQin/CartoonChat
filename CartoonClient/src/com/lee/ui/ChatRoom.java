package com.lee.ui;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.lee.function.CartoonBean;
import com.lee.function.ClientRequst;
import com.lee.util.CartoonUtil;
import com.lee.util.MyCellRender;
import com.lee.util.OnlineListModel;

public class ChatRoom extends JFrame {

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
	private static URI url1, url2;
	private static AudioClip audio1, audio2;
	private static ObjectInputStream ois;
	protected boolean isSending;
	protected boolean isReciving;
	protected String filePath;

	public ChatRoom(final String userName, Socket socket) {
		this.userName = userName;
		this.socket = socket;
		onlines = new Vector<String>();
		SwingUtilities.updateComponentTreeUI(this);

		try {
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
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
				g.drawImage(new ImageIcon("images/chatroom.jpg").getImage(), 0,
						0, getWidth(), getHeight(), null);
			}
		};

		setContentPane(contentPanel);
		contentPanel.setLayout(null);

		/* chat message area */
		JScrollPane disPane = new JScrollPane();
		disPane.setBounds(10, 10, 410, 300);
		getContentPane().add(disPane);

		disArea = new JTextArea();
		disArea.setEditable(false);
		disArea.setLineWrap(true);
		disArea.setWrapStyleWord(true);
		disArea.setFont(new Font("Andylee", Font.BOLD, 13));
		disPane.setViewportView(disArea);

		/* input box */
		JScrollPane inputPane = new JScrollPane();
		inputPane.setBounds(10, 347, 411, 97);
		contentPanel.add(inputPane);

		final JTextArea inputArea = new JTextArea();
		inputArea.setLineWrap(true);
		inputArea.setWrapStyleWord(true);
		inputPane.setViewportView(inputArea);

		/* close button */
		final JButton closeButton = new JButton("close");
		closeButton.setBounds(214, 448, 80, 30);
		contentPanel.add(closeButton);

		/* send button */
		final JButton sendButton = new JButton("send");
		sendButton.setBounds(313, 448, 80, 30);
		contentPanel.add(sendButton);

		/* user list */
		onlineList = new OnlineListModel(onlines);
		list = new JList<>(onlineList);
		list.setCellRenderer(new MyCellRender());
		list.setOpaque(false);
		Border etchdBorder = BorderFactory.createEtchedBorder();
		list.setBorder(BorderFactory.createTitledBorder(etchdBorder, "["
				+ userName + "]" + "onlines:", TitledBorder.LEADING,
				TitledBorder.TOP, new Font("andylee", Font.BOLD, 20),
				Color.green));
		JScrollPane listPane = new JScrollPane(list);
		listPane.setBounds(430, 10, 245, 375);
		listPane.setOpaque(false);
		listPane.getViewport().setOpaque(false);
		contentPanel.add(listPane);

		/* files sending bar */
		progressBarUI = new JProgressBar();
		progressBarUI.setBounds(430, 390, 245, 15);
		progressBarUI.setMinimum(1);
		progressBarUI.setMaximum(100);
		contentPanel.add(progressBarUI);

		/* files sending prompt */
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

			/* msg prompt */
			file = new File("sounds/ding.wav");
			url1 = file.toURI();
			audio1 = Applet.newAudioClip(url1.toURL());

			/* online prompt */
			file = new File("sounds/neo.wav");
			url2 = file.toURI();
			audio2 = Applet.newAudioClip(url2.toURL());

			new Thread(new InputThread()).start();

		} catch (IOException e) {
			e.printStackTrace();
		}
		setVisible(true);

		// send button
		sendButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String msg = inputArea.getText();
				List<String> chatList = list.getSelectedValuesList();

				if (chatList.size() < 1) {
					JOptionPane.showMessageDialog(contentPanel,
							"please choose one people to chat!");
				} else if (chatList.toString().contains(userName + "(I)")) {
					JOptionPane.showConfirmDialog(contentPanel,
							"Please don't send message to yourself!");
				} else if (msg.equals("")) {
					JOptionPane.showMessageDialog(contentPanel,
							"Can't sent blank message!");
					return;
				}

				CartoonBean cartoonBean = new CartoonBean();
				cartoonBean.setType(ClientRequst.UPDATE);
				cartoonBean.setName(userName);
				String currentTime = CartoonUtil.getTimer();
				cartoonBean.setTimer(currentTime);
				cartoonBean.setInfo(msg);
				HashSet<String> hSet = new HashSet<>();
				hSet.add(msg);
				cartoonBean.setClients(hSet);

				disArea.append("I say to" + chatList + "\n" + currentTime);
				sendMessage(cartoonBean);
				inputArea.setText("");
				inputArea.requestFocus();
			}
		});

		// close button
		closeButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (isSending || isReciving) {
					JOptionPane.showMessageDialog(contentPanel,
							"File is transfering!", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
						closeButton.setEnabled(false);
						CartoonBean cartoonBean = new CartoonBean();
						cartoonBean.setType(ClientRequst.OFFLINE);
						cartoonBean.setName(userName);
						cartoonBean.setTimer(CartoonUtil.getTimer());
						sendMessage(cartoonBean);			
				}
			}
		});

		// close window
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				if (isSending || isReciving) {
					JOptionPane.showMessageDialog(contentPanel,
							"File is transfering!", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					int ret = JOptionPane.showConfirmDialog(closeButton, "Are you sure leaving the chat room?");
					if (0==ret) {
						CartoonBean cartoonBean = new CartoonBean();
						cartoonBean.setType(ClientRequst.OFFLINE);
						cartoonBean.setName(userName);
						cartoonBean.setTimer(CartoonUtil.getTimer());
						sendMessage(cartoonBean);			
					}
				}
			}
		});

		list.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				List<String> chat = list.getSelectedValuesList();
				if (2 == e.getClickCount()) {
					if (chat.toString().contains(userName + "(I)")) {
						JOptionPane.showMessageDialog(contentPanel,
								"can't chat with yourslef!");
						return;
					}
				}

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("choose file");
				chooser.showDialog(contentPanel, "Choose"); // button name

				if (chooser.getSelectedFile() != null) {
					filePath = chooser.getSelectedFile().getPath();
					File file = new File(filePath);

					if (file.length() == 0) {
						JOptionPane.showMessageDialog(contentPanel,
								"File can't be null");
						return;
					}

					CartoonBean cartoonBean = new CartoonBean();
					cartoonBean.setType(ClientRequst.TRANSFERREQUST);
					cartoonBean.setSize(new Long(file.length()).intValue());
					cartoonBean.setTimer(CartoonUtil.getTimer());
					cartoonBean.setFileName(file.getName());
					cartoonBean.setInfo("File Sending requesting");

					HashSet<String> set = new HashSet<>();
					set.addAll(list.getSelectedValuesList());
					cartoonBean.setClients(set);
					sendMessage(cartoonBean);
				}
			}

		});
	}

	/**
	 * 
	 * @param cartoonBean
	 *            local client information
	 */
	protected void sendMessage(CartoonBean cartoonBean) {
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(cartoonBean);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	class InputThread implements Runnable {

		public void run() {
			while (true) {
				try {
					ois = new ObjectInputStream(socket.getInputStream());
					final CartoonBean cartoonBean = (CartoonBean) ois
							.readObject();
					
					switch (cartoonBean.getType()) {
					case PRIVATE: {
						String msg = cartoonBean.getTimer() + " "
								+ cartoonBean.getName() + "say to "
								+ cartoonBean.getClients();
						if (msg.contains(userName)) {
							msg = msg.replace(userName, "I");
						}
						audio1.play();
						disArea.append(msg+cartoonBean.getInfo()+"\n");
						disArea.selectAll();
						break;
					}
					case UPDATE: {// update list					
						onlines.clear();
						HashSet<String> clients = cartoonBean.getClients();
						Iterator<String> iterator = clients.iterator();
						while (iterator.hasNext()) {
							String name = iterator.next();
							if (userName.equals(name)) {
								onlines.add(name + "local");
							} else {
								onlines.add(name);
							}
						}

						onlineList = new OnlineListModel(onlines);
						list.setModel(onlineList);
						audio2.play();
						disArea.append(cartoonBean.getInfo() + "\n");
						disArea.selectAll();
						break;
					}
					case OFFLINE:{
						return;
						//break;
					}
					case TRANSFERREQUST: {// blocking, so create thread
						new Thread(){

							public void run() {
								int ret = JOptionPane.showConfirmDialog(contentPanel, cartoonBean.getInfo()); // confirm send
								
								if (0 == ret) { // send
									JFileChooser chooser = new JFileChooser();
									chooser.setDialogTitle("Save the file");
									chooser.setSelectedFile(new File(cartoonBean.getFileName())); // default name and current directory
									chooser.showDialog(contentPanel, "Save");
									String savePath = chooser.getSelectedFile().toString();
									
									//create client cartoonBean
									CartoonBean bean = new CartoonBean();
									bean.setType(ClientRequst.TRANSFERREQUST);
									bean.setTimer(CartoonUtil.getTimer());
									bean.setFileName(savePath);
									bean.setInfo("confirm to receive file");
									
									//choose the receiver
									HashSet<String> set = new HashSet<>();
									set.add(cartoonBean.getName());
									bean.setClients(set);//File source
									bean.setTo(cartoonBean.getClients());//File destination
									
									try {
										ServerSocket ss = new ServerSocket(0);// 0 - get idle port
										bean.setIp(socket.getInetAddress().getHostAddress());
										bean.setPort(ss.getLocalPort());
										sendMessage(bean);
										
										isReciving = true;
										Socket s = ss.accept();
										disArea.append(CartoonUtil.getTimer() + " " + cartoonBean.getFileName()+"File is saving...");
										
										DataInputStream dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
										DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
										
										int count = 0;
										int num = cartoonBean.getSize()/100;
										int index = 0;
										while (count < bean.getSize()) {
											int temp = dis.read();
											dos.write(temp);
											count++;
											
											if (num > 0) {
												if (count%num == 0 && index < 100) {
													progressBarUI.setValue(++index);
												}
												promptSendUI.setText("Progress:"+count+"/"+cartoonBean.getSize() +"  " + index + "%");
											} else {
												promptSendUI.setText("Progress:"+count+"/"+cartoonBean.getSize() +"  " 
														+ new Double(new Double(count).doubleValue()/new Double(cartoonBean.getSize()).doubleValue()*100).intValue()+"%");
												if (count == cartoonBean.getSize()) {
													progressBarUI.setValue(100);
												}
											}
										}
											
										/*send msg to sender*/
										PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
										pw.println(CartoonUtil.getTimer()+"send to "+userName+" ["+cartoonBean.getFileName()+"]"+"file saves over!\n");
										pw.flush();
										dos.flush();
										dos.close();
										pw.close();
										dis.close();
										s.close();
										ss.close();
										disArea.append(CartoonUtil.getTimer()+" "+cartoonBean.getFileName()+"File receiving complete,save as:"+savePath+"\n");
										isReciving = false;
										
									} catch (IOException e) {
										e.printStackTrace();
									} 					
								} else { // receive
									CartoonBean bean = new CartoonBean();
									bean.setType(ClientRequst.TRANSFER);
									bean.setTimer(CartoonUtil.getTimer());
									bean.setName(userName);
									bean.setFileName(cartoonBean.getFileName());
									bean.setInfo(CartoonUtil.getTimer()+" "+userName+"cancel receiving file["+cartoonBean.getFileName()+"]");
									
									//choose receiver
									HashSet<String> set = new HashSet<>();
									set.add(cartoonBean.getName());
									bean.setClients(set);
									bean.setTo(cartoonBean.getClients());
									
									sendMessage(bean);
								}
							}
							
						}.start();
						break;
					}
					case TRANSFER: {
						disArea.append(cartoonBean.getTimer()+" "+cartoonBean.getName()+"file is transfering\n");
						new Thread(){
							public void run() {
								isSending = true;
								
								try {
									Socket s = new Socket(cartoonBean.getIp(),cartoonBean.getPort());
									DataInputStream dis = new DataInputStream(new FileInputStream(filePath));
									DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
									
									int size = dis.available();
									int count = 0;
									int num = size/100;
									int index = 0;
									while (count < size) {
										int temp = dis.read();
										dos.write(temp);
										count++;
										
										if (num>0) {
											if (count%num == 0 && index < 100) {
												progressBarUI.setValue(++index);
											}
											promptSendUI.setText("upload progress:"+count+"/"+size+" "+index+"%");
										} else {
											promptSendUI.setText("upload progress:"+count+"/"+size+" "
														+new Double(new Double(count).doubleValue()/new Double(size).doubleValue()*100).intValue()+"%"	);
											if(count==size){
												progressBarUI.setValue(100);
											}
										}
									}
									
									dos.flush();
									dis.close();
									BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
									disArea.append(br.readLine()+"\n");
									isSending = false;
									br.close();
									s.close();
								} catch (UnknownHostException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							
						}.start();
						break;
					}
					case CANCELTHANSTER: {
						disArea.append(cartoonBean.getInfo()+"\n");
						break;
					}
					default:
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("get input stream failed");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					System.exit(0);//exit chat room
				}
			}

		}
	}

}
