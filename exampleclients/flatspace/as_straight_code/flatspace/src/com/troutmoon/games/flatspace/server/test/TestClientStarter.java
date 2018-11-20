package com.troutmoon.games.flatspace.server.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class TestClientStarter {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		String host = "localhost";
		int port = 1234;
		int numc = 1;

		if (args.length == 3) {
			host = args [0];
			port = Integer.parseInt (args [1]);
			numc = Integer.parseInt (args [2]);
		}

		for (int x=0; x < numc; x++) {
			String id = "c" + (x+1);
			new TestClientRunner(id, host, port).start();
		}
		
//		WrapperRunner tcr01 = new WrapperRunner("c1", host, port);
//		tcr01.setName("c1");
//		tcr01.start();
		
	}

}
