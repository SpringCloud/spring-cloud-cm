
package com.springcloud.cmserver.mgmt.cm.socket;

import com.springcloud.cmserver.mgmt.cm.socket.ChangeRequest;
import com.springcloud.cmserver.mgmt.cm.socket.EchoWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

/**
 * @author HanSenJ
 * @email 28707699@qq.com
 */
public class NioServer implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(NioServer.class);
	
	// The host:port combination to listen on
	private InetAddress hostAddress;
	private int port;
	
	// when current cm is master and alive ,true forever, otherwise false.
	public boolean acceptRequest = true;

	// The channel on which we'll accept connections
	private ServerSocketChannel serverChannel;

	// The selector we'll be monitoring
	private Selector selector;

	// The buffer into which we'll read data when it's available
	private ByteBuffer readBuffer = ByteBuffer.allocate(163840);

	private EchoWorker worker;

	// A list of PendingChange instances
	private List<ChangeRequest> pendingChanges = new LinkedList<ChangeRequest>();

	// Maps a SocketChannel to a list of ByteBuffer instances
	private Map<SocketChannel,List<ByteBuffer>> pendingData = new HashMap<SocketChannel,List<ByteBuffer>>();
	
	private static NioServer instance;

	public NioServer(InetAddress hostAddress, int port, EchoWorker worker) throws IOException {
		this.hostAddress = hostAddress;
		this.port = port;
		this.selector = this.initSelector();
		this.worker = worker;
	}
	
	public static synchronized void init(InetAddress hostAddress, int port, EchoWorker worker) throws IOException {
		instance = new NioServer(hostAddress,port,worker);
		(new Thread(instance)).start();
	}
	
	public static NioServer getInstance(){
		return instance;
	}

	public void send(SocketChannel socket, byte[] data) {
		synchronized (this.pendingChanges) {
			this.pendingChanges.add(new ChangeRequest(socket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

			// And queue the data we want written
			synchronized (this.pendingData) {
				//List queue = (List) this.pendingData.get(socket);
				List<ByteBuffer> queue = this.pendingData.get(socket);
				if (queue == null) {
					queue = new ArrayList<ByteBuffer>();
					this.pendingData.put(socket, queue);
				}
				queue.add(ByteBuffer.wrap(data));
			}
		}

		this.selector.wakeup();
	}

	public void run() {
		while (true) {
			try {
				synchronized (this.pendingChanges) {
					Iterator<ChangeRequest> changes = this.pendingChanges.iterator();
					while (changes.hasNext()) {
						ChangeRequest change = (ChangeRequest) changes.next();
						switch (change.type) {
						case ChangeRequest.CHANGEOPS:
							SelectionKey key = change.socket.keyFor(this.selector);
							key.interestOps(change.ops);
						}
					}
					this.pendingChanges.clear();
				}

				// Wait for an event one of the registered channels
				this.selector.select();

				// Iterate over the set of keys for which events are available
				Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();
					try{
						if (!key.isValid()) {
							continue;
						}

						// Check what event is available and deal with it
						if (key.isAcceptable()) {
							this.accept(key);
						} else if (key.isReadable()) {
							this.read(key);
						} else if (key.isWritable()) {
							this.write(key);
						}
					}catch(Exception e){
						key.cancel();
					}
				}
			} catch (Exception e) {
				logger.error("err : " + e.getMessage());
				this.pendingChanges.clear();
			}
		}
	}

	/**
	 * Deal socket client connect
	 * @param key
	 * @throws IOException
	 */
	private void accept(SelectionKey key) throws IOException {
		// For an accept to be pending the channel must be a server socket channel.
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

		// Accept the connection and make it non-blocking
		SocketChannel socketChannel = serverSocketChannel.accept();
		
		if (acceptRequest) {
			//Socket socket = socketChannel.socket();
			socketChannel.configureBlocking(false);
			// Register the new SocketChannel with our Selector, indicating
			// we'd like to be notified when there's data waiting to be read
			socketChannel.register(this.selector, SelectionKey.OP_READ);
		} else {
			socketChannel.close();
		}
	}

	/**
	 * Read socket client data
	 * @param key
	 * @throws IOException
	 */
	private void read(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		// Clear out our read buffer so it's ready for new data
		this.readBuffer.clear();

		// Attempt to read off the channel
		int numRead;
		try {
			numRead = socketChannel.read(this.readBuffer);
		} catch (IOException e) {
			// The remote forcibly closed the connection, cancel
			// the selection key and close the channel.
			key.cancel();
			socketChannel.close();
			return;
		}

		if (numRead == -1) {
			// Remote entity shut the socket down cleanly. Do the
			// same from our end and cancel the channel.
			key.channel().close();
			key.cancel();
			return;
		}
		//logger.debug("Received:" + new String(this.readBuffer.array()));
		// Hand the data off to our worker thread
		this.worker.processData(this, socketChannel, this.readBuffer.array(), numRead);
	}

	/**
	 * Send data to client
	 * @param key
	 * @throws IOException
	 */
	private void write(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		synchronized (this.pendingData) {
			List<ByteBuffer> queue = this.pendingData.get(socketChannel);

			// Write until there's not more data ...
			while (!queue.isEmpty()) {
				ByteBuffer buf = (ByteBuffer) queue.get(0);
				socketChannel.write(buf);
				//logger.debug("Send:" + new String(buf.array()));
				if (buf.remaining() > 0) {
					// ... or the socket's buffer fills up
					break;
				}
				queue.remove(0);
			}

			if (queue.isEmpty()) {
				// We wrote away all data, so we're no longer interested
				// in writing on this socket. Switch back to waiting for
				// data.
				key.interestOps(SelectionKey.OP_READ);
			}
		}
	}

	private Selector initSelector() throws IOException {
		// Create a new selector
		Selector socketSelector = SelectorProvider.provider().openSelector();

		// Create a new non-blocking server socket channel
		this.serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);

		// Bind the server socket to the specified address and port
		InetSocketAddress isa = new InetSocketAddress(this.hostAddress, this.port);
		serverChannel.socket().bind(isa);

		// Register the server socket channel, indicating an interest in 
		// accepting new connections
		serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

		return socketSelector;
	}

	public static void main(String[] args) {
		try {
			EchoWorker worker = new EchoWorker();
			new Thread(worker).start();
			new Thread(new NioServer(null, 9090, worker)).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
