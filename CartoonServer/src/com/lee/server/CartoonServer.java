package com.lee.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import com.lee.function.*;

public class CartoonServer {
	/**
	 * 
	 */
	private static ServerSocket ss = null;
	/**
	 * store the online clients by hashmap
	 */
	public static HashMap<String, ClientBean> onlines = null;
	static {
		try {
			ss = new ServerSocket(6666);
			onlines = new HashMap<>();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ServerSocket created fail");
		}
	}

	public static void main(String[] args) {
		new CartoonServer().start();
	}

	private void start() {
		try {
			while (true) {
				Socket cs = ss.accept();
				new Thread(new ClientThread(cs)).start();			
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private class ClientThread implements Runnable {
		Socket socket;
		CartoonBean cartoonBean;
		ObjectOutputStream oos;
		ObjectInputStream  ois;

		public ClientThread(Socket cs) {
			socket = cs;
		}

		public void run() {			
			try {  // if put "try" in while loop, it will report "socket is closed",
				while (true) {
					ois = new ObjectInputStream(socket.getInputStream());
					cartoonBean = (CartoonBean) ois.readObject();
					
					switch (cartoonBean.getType()) {
					case PRIVATE: {
						CartoonBean bean = new CartoonBean();

						bean.setType(ClientRequst.UPDATE);
						bean.setClients(bean.getClients());
						bean.setInfo(bean.getInfo());
						bean.setName(bean.getName());
						bean.setTimer(bean.getTimer());
						sendMsg(bean);
						break;
					}
					case UPDATE: {// update client list
						ClientBean clientBean = new ClientBean();
						clientBean.setClientName(cartoonBean.getName());
						clientBean.setClientSocket(socket);
						
						/*add online client*/
						onlines.put(clientBean.getClientName(), clientBean);
						CartoonBean bean = new CartoonBean();
						bean.setType(ClientRequst.PRIVATE);
						
						/*note new client to all client*/
						bean.setInfo(cartoonBean.getTimer()+""+cartoonBean.getName()+"online");
						HashSet<String> set = new HashSet<>();
						set.addAll(onlines.keySet());
						broadCast(bean);
						break;
					}
					case OFFLINE: {
						CartoonBean bean = new CartoonBean();
						bean.setType(ClientRequst.OFFLINE);
						
						try {
							oos = new ObjectOutputStream(socket.getOutputStream());
							oos.writeObject(bean);
							oos.flush();					
						} catch (IOException e) {
							e.printStackTrace();
						}

						onlines.remove(bean.getName());
						
						CartoonBean bean2 = new CartoonBean();
						bean2.setInfo(cartoonBean.getTimer()+" "+cartoonBean.getName()+" offline");
						bean2.setType(ClientRequst.UPDATE);
						HashSet<String> set = new HashSet<>();
						set.addAll(onlines.keySet());
						bean2.setClients(set);
						broadCast(bean2);
						return;
					}
					case TRANSFERREQUST: {
						CartoonBean bean = new CartoonBean();
						String msg = cartoonBean.getTimer()+" " + cartoonBean.getName()+"send file to you, receive?";
						
						bean.setType(ClientRequst.TRANSFERREQUST);
						bean.setClients(cartoonBean.getClients());
						bean.setFileName(cartoonBean.getFileName());
						bean.setSize(cartoonBean.getSize());
						bean.setInfo(msg);
						bean.setName(cartoonBean.getName());
						bean.setTimer(cartoonBean.getTimer());
						sendMsg(bean);
						break;
					}
					case TRANSFER:{
						CartoonBean bean = new CartoonBean();
						
						bean.setType(ClientRequst.TRANSFER);
						bean.setClients(cartoonBean.getClients());
						bean.setTo(cartoonBean.getTo());
						bean.setFileName(cartoonBean.getFileName());
						bean.setIp(cartoonBean.getIp());
						bean.setPort(cartoonBean.getPort());
						bean.setName(cartoonBean.getName());
						bean.setTimer(cartoonBean.getTimer());
						sendMsg(bean);
						break;
					}
					case CANCELTHANSTER:{
						CartoonBean bean = new CartoonBean();
						
						bean.setType(ClientRequst.CANCELTHANSTER);
						bean.setClients(cartoonBean.getClients());
						bean.setTo(cartoonBean.getTo());
						bean.setFileName(cartoonBean.getFileName());
						bean.setIp(cartoonBean.getIp());
						bean.setPort(cartoonBean.getPort());
						bean.setName(cartoonBean.getName());
						bean.setTimer(cartoonBean.getTimer());
						sendMsg(bean);
						break;
					}
					default:
						break;
					}
				}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						if (oos!=null) {
							oos.close();
						}
						if (ois!=null) {
							ois.close();
						}
						if (socket!=null) {
							socket.close();
						}
						
					} catch (IOException e2) {
						e2.printStackTrace();
					}
				}			
		}

	}

	
	
	public void broadCast(CartoonBean bean) {
		Collection<ClientBean> clients = onlines.values();
		Iterator<ClientBean> iterator = clients.iterator();
		ObjectOutputStream oos;
		while (iterator.hasNext()) {
			Socket socket = iterator.next().getClientSocket();
			try {
				oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject(bean);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendMsg(CartoonBean bean) {
		Set<String> set = onlines.keySet();
		Iterator<String> iterator = set.iterator();
		HashSet<String> clients = bean.getClients();
		while (iterator.hasNext()) {
			String client = (String) iterator.next();
			if (clients.contains(client)) {
				Socket s = onlines.get(client).getClientSocket();
				ObjectOutputStream oos;
				try {
					oos = new ObjectOutputStream(s.getOutputStream());
					oos.writeObject(bean);
					oos.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
}
