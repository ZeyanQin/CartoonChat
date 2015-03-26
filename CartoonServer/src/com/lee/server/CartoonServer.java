package com.lee.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import com.lee.function.ClientBean;

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
		Socket socket = null;

		public ClientThread(Socket cs) {
			socket = cs;
		}

		public void run() {

		}

	}
}
