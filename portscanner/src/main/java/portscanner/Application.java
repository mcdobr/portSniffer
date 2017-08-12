package portscanner;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Application {
	private static final int nThreads = 512;
	private static final ExecutorService executor = Executors.newFixedThreadPool(nThreads);
	
	public static Future<Optional<Integer>> isPortOpen(final String address, final int port) {
		final int CONN_TIME = 50;
		
		Future<Optional<Integer>> res = executor.submit(new Callable<Optional<Integer>>() {
			public Optional<Integer> call() {
				
				try {
					Socket socket = new Socket();
					socket.connect(new InetSocketAddress(address, port), CONN_TIME);
					return Optional.of(Integer.valueOf(port));
				} catch (Exception e) {
					return Optional.empty();
				}
			}
		});
		
		return res;
	}
	
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		ArrayList<Future<Optional<Integer>>> futures = new ArrayList<>();
		
		for (int i = 1; i < Short.MAX_VALUE; ++i) {
			futures.add(isPortOpen("127.0.0.1", i));
		}
		
		final int WORK_TIMEOUT = 1_000;
		executor.awaitTermination(WORK_TIMEOUT, TimeUnit.MILLISECONDS);
		executor.shutdown();
		
		
		// Filter open ports
		List<Integer> openPorts = new ArrayList<>();
		for (Future<Optional<Integer>> f : futures) {
			Optional<Integer> scanResult = f.get();
			if (scanResult.isPresent())
				openPorts.add(scanResult.get());
		}
		
		Collections.sort(openPorts);
		
		for (Integer port : openPorts)
			System.out.format("Port no [%5d] is %s.%n", port.intValue(), "OPEN");
		
		System.out.println("Done");
	}
}
