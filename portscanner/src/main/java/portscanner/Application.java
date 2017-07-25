package portscanner;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Application {
	static final ExecutorService executor = Executors.newCachedThreadPool();
	
	public static Future<Boolean> isPortOpen(final String address, final int port) {
		final int CONN_TIME = 200;
		
		Future<Boolean> res = executor.submit(new Callable<Boolean>() {
			public Boolean call() {
				
				try {
					Socket socket = new Socket();
					socket.connect(new InetSocketAddress(address, port), CONN_TIME);
					System.out.format("Port no [%d] is %s.%n", port, "OPEN");
					return Boolean.TRUE;
				} catch (Exception e) {
					return Boolean.FALSE;
				}
			}
		});
		
		return res;
	}
	
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		ArrayList<Future<Boolean>> futures = new ArrayList<>();
		
		for (int i = 1; i < Short.MAX_VALUE; ++i) {
			futures.add(isPortOpen("127.0.0.1", i));
		}
		
		final int WORK_TIMEOUT = 1000000;
		executor.awaitTermination(WORK_TIMEOUT, TimeUnit.MILLISECONDS);
		
		int openPorts = 0;
		for (Future<Boolean> fr : futures) {
			if (fr.get().booleanValue() == true) 
				++openPorts;
		}
		
		System.out.format("There are %d ports.%n", openPorts);
		//System.out.format("Port no [%d] is %s.%n", i, (value.booleanValue() == true) ? "OPEN" : "CLOSED");
		
	}
}
