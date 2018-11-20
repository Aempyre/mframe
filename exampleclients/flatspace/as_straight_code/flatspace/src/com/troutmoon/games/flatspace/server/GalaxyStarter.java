package com.troutmoon.games.flatspace.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.ServerSocketChannel;

import com.troutmoon.games.flatspace.client.GalaxyClient;
import com.troutmoon.games.flatspace.client.GalaxyClientWorker;
import com.troutmoon.conncom.selector.CCThreadPool;
import com.troutmoon.conncom.selector.ChannelContext;
import com.troutmoon.conncom.selector.SelectorApplicationManager;
import com.troutmoon.conncom.selector.AbstractSelectorApplication;
import com.troutmoon.util.IPAddress;

public class GalaxyStarter {

	//TODO this is the application startup class that will kick off an HF util to load properties to runtime classes
	//     then create the SAM passing in the config information and the GalaxyServer (ASA concrete class), and the
	//     Abstract worker concrete class too (GalaxyClientWorker).
	
	private int asa_id_maker = 0;

	
	public GalaxyStarter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Clean this up for presentation to customers as an example!
		
		int asa_id = 1;									//application id (not worker pool id)
		int ssId 	= (int) System.currentTimeMillis();  	//context id (one per ctxt), just one ctxt for now
		//FIXME  need util class for unique ids

		int number_of_workers_desired = 1;   //TODO from config!

		String srvr_addr 	= "127.0.0.1";
		int    srvr_port 	= 1234; 
		
		if (args.length >= 2) {
			asa_id = Integer.parseInt (args[0]);
			number_of_workers_desired = Integer.parseInt (args[1]);
			
			if (args.length >= 3) {
				srvr_addr = args[2];
			
				if (args.length == 4) {
					srvr_port = Integer.parseInt (args [3]);
				}
			}
		}
		
		SelectorApplicationManager sam 	= new SelectorApplicationManager();
		Thread samThread 				= new Thread(sam);

		CCThreadPool workers = new CCThreadPool(number_of_workers_desired);
		for (int i=0; i<number_of_workers_desired; i++) {
			workers.addWorker(new GalaxyClientWorker(workers), ("wpid-" + i));
		}
		
		GalaxyProtocol protocol = new GalaxyProtocol(ChannelContext.Server_Socket_Type);
		
		GalaxyServer galaxy_server = new GalaxyServer(sam, asa_id, AbstractSelectorApplication.ExpirationOrder.ACCESS_ORDER, workers, protocol);
		
		ChannelContext channel_context = new ChannelContext(ssId, srvr_addr, srvr_port, ChannelContext.Server_Socket_Type, protocol);
		channel_context.setOutboundMessage("[Server Galaxy Data Blocks]");
		
		galaxy_server.assign(channel_context);

		samThread.setName("SAM");
		samThread.start();
		
	}
}
