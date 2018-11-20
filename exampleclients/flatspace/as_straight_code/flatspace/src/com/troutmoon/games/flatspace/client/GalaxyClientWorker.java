package com.troutmoon.games.flatspace.client;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.troutmoon.conncom.selector.AbstractSelectorWorker;
import com.troutmoon.conncom.selector.CCThreadPool;
import com.troutmoon.conncom.selector.ChannelContext;

public class GalaxyClientWorker extends AbstractSelectorWorker {

	
	public GalaxyClientWorker(CCThreadPool pool) {
		super(pool);
		// TODO Auto-generated constructor stub
	}


	@Override
	public int readChannel(SelectionKey selected_key) throws IOException {
		SocketChannel channel = (SocketChannel) selected_key.channel();
		int count;

		buffer.clear();
		try {
			//while ((count = channel.read (buffer)) > 0) {
				count = channel.read(buffer);
				buffer.flip();		// make buffer readable
				System.out.println(".rx = " + buffer.asCharBuffer().toString());  //NOTE inefficient but convenient code
				
			//}
			if (count < 0) {
				// close channel on EOF, invalidates the key
				channel.close();
				return 0;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return SelectionKey.OP_WRITE;  //TODO the protocol plugin will deterimine what the next interest op is!
	}

	@Override
	public int writeChannel(SelectionKey selected_key) throws IOException {
		SocketChannel channel = (SocketChannel) selected_key.channel();
		int count;

		ChannelContext cc = (ChannelContext)selected_key.attachment();
		
		buffer.clear();
		try {
			// send the data, may not go all at once
			long threadId = this.getId();
			String prefix = String.valueOf(threadId);
			String message = prefix + " " + cc.getOutboundMessage();
			buffer.asCharBuffer().put(message.toCharArray());  //NOTE inefficient but convenient code
			
			while (buffer.hasRemaining()) {
				channel.write (buffer);
			}  	// WARNING: the above loop is evil.  See comments in superclass.
			// flip for the printout below // not a part of mainline logic!
			buffer.flip();
			System.out.println("Worker wrote buffer = " + buffer.asCharBuffer().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return SelectionKey.OP_READ;  //TODO the protocol plugin will deterimine what the next interest op is!
	}
}