package portscanner;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Application {
	final ExecutorService executor = Executors.newCachedThreadPool();
	
	public Future<Boolean> isPortOpen(final String address, final int port) {
		final int CONN_TIME = 3000;
		
		Future<Boolean> res = executor.submit(new Callable<Boolean>() {
			public Boolean call() {
				
				try {
					Socket socket = new Socket();
					socket.connect(new InetSocketAddress(address, port), CONN_TIME);
					return Boolean.TRUE;
				} catch (Exception e) {
					return Boolean.FALSE;
				}
			}
		});
		
		return res;
	}
	
	
	public static void main(String[] args) {
		
		for (int i = 1; i < Short.MAX_VALUE; ++i) {
		}
		
	}
}
