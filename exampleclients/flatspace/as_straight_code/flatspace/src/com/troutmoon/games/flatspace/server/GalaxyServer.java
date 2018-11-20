package com.troutmoon.games.flatspace.server;

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
import com.troutmoon.conncom.selector.InvalidContextType;
import com.troutmoon.conncom.selector.InvalidProtocolState;
import com.troutmoon.conncom.selector.InvalidProtocolUsage;
import com.troutmoon.conncom.selector.SelectorApplicationManager;

public class GalaxyServer extends AbstractSelectorApplication {

	public GalaxyServer(SelectorApplicationManager sam, int asaid, ExpirationOrder ex_ord, CCThreadPool wrkrs, ConnComProtocol proto) {
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
	protected int registerChannel(ChannelContext channel_context) throws IOException, InvalidContextType, InvalidProtocolUsage, InvalidProtocolState {
		// TODO Need to add stuff here (from existing examples otras), to register the channel with the selector
		System.out.println("channel register method...");
		
		if (channel_context.context_type == ChannelContext.Server_Socket_Type) {
			ServerSocketChannel ssockchan;
			InetSocketAddress endpoint;
			try {
				ssockchan = ServerSocketChannel.open();
				endpoint = new InetSocketAddress(InetAddress.getLocalHost(), channel_context.port);
				ssockchan.socket().bind(endpoint);
				channel_context.setChannel(ssockchan);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			channel_context.getServerChannel().configureBlocking(false);
			return	channel_context.protocol.nextInterestOps(0); 
		} else {
			if (channel_context.context_type == ChannelContext.Accepted_Client_Type) {
				channel_context.getChannel().configureBlocking(false);
				
				System.out.println("Accepted Client Type \n" + channel_context.toString());
				//NOTE:  A new socketchannel comes from serversocket.accept() with zero as 
				//       interest_ops.  If this ever changes our code may break so be advised!
				
				int temp = channel_context.protocol.nextInterestOps(0);   //FIXME
				System.out.println("next int_ops = " + temp);				//FIXME
				return	temp; //channel_context.protocol.nextInterestOps(0); FIXME
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
						// if OS runs out of buffers for new sockets
						if (ex.getMessage().indexOf("No buffer space available") >= 0) {
							System.out.println("No buffer space was available - huh?");
							// suspend processRegistration for a little while
							//TODO fixup - startRegistrationTime = System.currentTimeMillis() + 30000; 
							//throw new OutofSocketException(ex);
						}
						throw ex;
					}
					channel_context.setChannel(sockchan);
					return	channel_context.protocol.nextInterestOps(0); // zero int_ops for new
				} else {
					throw new IOException("invalid channel_context.type");
				}
			}
		}
	}
	
	@Override
	protected boolean connectChannel(ChannelContext sel_key_ctxt) throws IOException {
		System.out.println("GalaxyServer connectChannel");
		
		boolean result = sel_key_ctxt.getChannel().finishConnect();
		
		System.out.println("GalaxyServer connectChannel, result is " + result);

		return result;
	}


}
