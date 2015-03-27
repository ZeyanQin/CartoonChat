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
			ss = new ServerSocket(8888);
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
			Socket cs = ss.accept();
			new Thread(new ClientThread(cs)).start();
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
			while (true) {
				try {
					ois = new ObjectInputStream(socket.getInputStream());
					cartoonBean = (CartoonBean) ois.readObject();
					switch (cartoonBean.getType()) {
					case PRIVATE: // update list
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
					case UPDATE:

						break;
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
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
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
}
