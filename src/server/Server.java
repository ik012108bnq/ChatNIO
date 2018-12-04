package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {

	private final int port = 8080;
	private Selector selector;
	private ServerSocketChannel serverChannel;
	private SelectionKey serverKey;
	static HashMap<SelectionKey, ClientSession> clientMap = new HashMap<SelectionKey, ClientSession>();
	
	public void run()
	{
		try {
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverKey = serverChannel.register(selector = Selector.open(), SelectionKey.OP_ACCEPT);
			serverChannel.bind(new InetSocketAddress("localhost", port));			
		
			Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
				loop();
			}, 0, 50, TimeUnit.MILLISECONDS); 
			
			System.out.println("Server listening on: " + port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loop()
	{
		try {
			selector.selectNow();			
			
			for (SelectionKey key : selector.selectedKeys()) {
                try {
                        if (!key.isValid())
                                continue;

                        if (key == serverKey) {
                                SocketChannel acceptedChannel = serverChannel.accept();

                                if (acceptedChannel == null)
                                        continue;

                                acceptedChannel.configureBlocking(false);
                                SelectionKey readKey = acceptedChannel.register(selector, SelectionKey.OP_READ);
                                clientMap.put(readKey, new ClientSession(readKey, acceptedChannel));

                                System.out.println("New client " + acceptedChannel.getRemoteAddress());
                        }

                        if (key.isReadable()) {
                        	ClientSession sesh = clientMap.get(key);

                                if (sesh == null)
                                        continue;

                                sesh.read();
                        }

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        selector.selectedKeys().clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
