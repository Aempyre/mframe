package com.troutmoon.games.flatspace.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.troutmoon.conncom.selector.ASAAddException;
import com.troutmoon.conncom.selector.ConnComProtocol;
import com.troutmoon.conncom.selector.AbstractSelectorApplication;
import com.troutmoon.conncom.selector.CCThreadPool;
import com.troutmoon.conncom.selector.ChannelContext;
import com.troutmoon.conncom.selector.SelectorApplicationManager;

public class GalaxyClient extends AbstractSelectorApplication {

	public GalaxyClient(SelectorApplicationManager sam, int asaid, ExpirationOrder ex_ord, CCThreadPool wrkrs, ConnComProtocol proto) {
		super(sam, asaid, ex_ord, wrkrs, proto);
		try {
			sam.addSelectorApplication(this);
		} catch (ASAAddException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected long getSelectorTimeout() {
		return 100;  //TODO this needs to variable and configurable (statically or dynamically)
	}

	@Override
	protected int registerChannel(ChannelContext channel_context) throws IOException {
		System.out.println("channel register method...");
		
		if (channel_context.context_type == ChannelContext.Server_Socket_Type) {
			ServerSocketChannel ssockchan;
			InetSocketAddress endpoint;
			try {
				ssockchan = ServerSocketChannel.open();
				/*TODO:  server socket address needs to be external configuration!
				 *       ...probably upstream from here, so pick it out of channel_context also  */  
				endpoint = new InetSocketAddress(InetAddress.getLocalHost(), channel_context.port);
				ssockchan.socket().bind(endpoint);
				channel_context.setChannel(ssockchan);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			channel_context.getServerChannel().configureBlocking(false);
			return SelectionKey.OP_ACCEPT;
		} else {
			if (channel_context.context_type == ChannelContext.Accepted_Client_Type) {
				SocketChannel csockchan;
				InetSocketAddress endpoint;
				
				csockchan = SocketChannel.open();
				
				channel_context.getChannel().configureBlocking(false);
				
				//endpoint = new InetSocketAddress(channel_context.address, channel_context.port);
				
//				try {
//					sockchan.connect(endpoint);
//				} catch (SocketException ex) {
//					// if OS runs out of buffers for sockets
//					if (ex.getMessage().indexOf("No buffer space available") >= 0) {
//						System.out.println("No buffer space was available - huh?");
//						// suspend processRegistration for a little while
//						//TODO fixup - startRegistrationTime = System.currentTimeMillis() + 30000; 
//						//throw new OutofSocketException(ex);
//					}
//					throw ex;
//				}
				
//				channel_context.setChannel(sockchan);
				return SelectionKey.OP_WRITE;   //TODO need to make this protocol plugin controlled
				                                // as here we are hardcoding that server sends ID first
			} else {
				if (channel_context.context_type == ChannelContext.Originating_Client_Type) {
					SocketChannel sockchan;
					InetSocketAddress endpoint;
					
					sockchan = SocketChannel.open();
					sockchan.configureBlocking(false);
					
					endpoint = new InetSocketAddress(channel_context.address, channel_context.port);
					
					try {
						sockchan.connect(endpoint);
					} catch (SocketException ex) {
						// if OS runs out of buffers for sockets
						if (ex.getMessage().indexOf("No buffer space available") >= 0) {
							System.out.println("No buffer space was available - huh?");
							// suspend processRegistration for a little while
							//TODO fixup - startRegistrationTime = System.currentTimeMillis() + 30000; 
							//throw new OutofSocketException(ex);
						}
						throw ex;
					}
					channel_context.setChannel(sockchan);
					return SelectionKey.OP_CONNECT;   
					//NOTE assuming need to connect supercedes any protocol plugin activity
				} else {
					throw new IOException("invalid channel_context.type");
				}
			}
		}
	}
	
	@Override
	protected boolean connectChannel(ChannelContext sel_key_ctxt) throws IOException {
		System.out.println("GalaxyClient connectChannel");
		
		boolean result = sel_key_ctxt.getChannel().finishConnect();
		
		System.out.println("GalaxyClient connectChannel, result is " + result);

		return result;
	}


}
