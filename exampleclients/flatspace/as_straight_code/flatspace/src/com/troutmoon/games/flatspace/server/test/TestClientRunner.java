package com.troutmoon.games.flatspace.server.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;

public class TestClientRunner extends Thread {
	
	private String id;
	private char id1;
	private char id2;
	private String host;
	private int port;
	
	boolean should_run = true;

	public TestClientRunner(String clientId, String host, int port) {
		this.id = clientId;
		this.host = host;
		this.port = port;
		
		Character id1 = id.charAt(0);
		Character id2 = id.charAt(1);
		
	}

	@Override
	public void run() {
		
		InetSocketAddress addr = new InetSocketAddress (host, port);
		SocketChannel sc = null;
		try {
			sc = SocketChannel.open();
			sc.configureBlocking (true);  //change to false when using selectors (or highflow actually)
			System.out.println ("initiating connection");
			sc.connect (addr);
			while ( ! sc.finishConnect()) { doSomethingUseful(); }
			System.out.println ("connection established");
		} catch (IOException e1) {
			System.out.println("Runner " + id + " Failed connecting!");
			e1.printStackTrace();
		}

		// Do something with the connected socket
		// The SocketChannel is still non-blocking
		
		int proto_state = 0;
		ByteBuffer   bb = ByteBuffer.allocateDirect(16);

		while (should_run) {
			try {
				Thread.sleep(111);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			
//			System.out.print(".ps-" + proto_state);
			// temp hack as not doing readyness selection yet
			
			if (proto_state == 0) {
				System.out.println("proto_state was 0.");
				int count = 0;
				bb.clear();
				try {
					
					//while ((count = sc.read (bb)) > 0) {
					count = sc.read(bb);
					if (count > 0) {
						bb.flip();		// make buffer readable
						System.out.println(".rx-id = " + 	bb.getInt() + " for " + id + " with count of " + count);
						Thread.sleep(111);  // temp hack as not doing readyness selection yet
					} else { continue; }
					// send the data, may not go all at once
					bb.clear();
					bb.putInt(0);
					while (bb.hasRemaining()) {
						sc.write (bb);
					}  	// WARNING: the above loop is evil.  See comments in superclass.
					proto_state = 1; // recieved my 2 character id from the server, xmtted login, waiting acknowldegement
					continue;  //so as not to fall into next state check (although that would work too, just more obscurely)
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (proto_state == 1) {
				int count = 0;
				bb.clear();
				try {
					//while ((
					count = sc.read (bb);
					if (count > 0) {		//) > 0) {
						bb.flip();		// make buffer readable
						System.out.println(".rx-ak=" + bb.getInt() + " for " + id + " with count of " + count);
						Thread.sleep(111);  // temp hack as not doing readyness selection yet
					} else continue;
					// send the data, may not go all at once
					bb.clear();
					bb.putInt(1);
					while (bb.hasRemaining()) {
						sc.write (bb);
					}  	// WARNING: the above loop is evil.  See comments in superclass.
					proto_state = 2; 
					continue;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			if (proto_state == 2) {
				int count = 0;
				bb.clear();
				try {
					//while ((
							count = sc.read (bb);
							//) > 0) {
					if (count > 0) {
						bb.flip();		// make buffer readable
						System.out.println(".rx =" + bb.getInt() + " for " + id + " with count of " + count);
						Thread.sleep(111);  // temp hack as not doing readyness selection yet
					} else {continue;}
					// send the data, may not go all at once
					bb.clear();
					bb.putInt(2);
					while (bb.hasRemaining()) {
						sc.write (bb);
					}  	// WARNING: the above loop is evil.  See comments in superclass.
//					proto_state = 2; 
					continue;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			
			
		}
		
		
		try {
			sc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println ("connection closed");

	}
	
	private static void doSomethingUseful() {
		System.out.println ("doing something useless");
	}



}
