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

public class Sniffer {
	private static final int NO_THREADS = 512;
	private static final int NO_PORTS = Short.MAX_VALUE;
	
	private final String address;
	private final ExecutorService executor;
		
	public Sniffer(String address) {
		this.address = address;
		this.executor = Executors.newFixedThreadPool(NO_THREADS);
	}
	/**
	 * Function that finds all open ports on the specified address.
	 * @return List of open ports
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public List<Integer> sniff() throws InterruptedException, ExecutionException {
		
		ArrayList<Future<Optional<Integer>>> futures = new ArrayList<>();
		
		for (int i = 1; i < NO_PORTS; ++i) {
			futures.add(isPortOpen(i));
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
		return openPorts;
		/*
		for (Integer port : openPorts)
			System.out.format("Port no [%5d] is %s.%n", port.intValue(), "OPEN");
		
		System.out.println("Done");*/
	}
	
	/**
	 * Function that checks if the port is open by connecting to it.
	 * No fancy sniffing techniques like in Nmap.
	 * @param port
	 * @return An Integer if it is open. An empty optional otherwise.
	 */
	private Future<Optional<Integer>> isPortOpen(final int port) {
		final int CONN_TIME = 50;
		
		Future<Optional<Integer>> res = executor.submit(new Callable<Optional<Integer>>() {
			public Optional<Integer> call() {
				
				try (Socket socket = new Socket()){
					socket.connect(new InetSocketAddress(address, port), CONN_TIME);
					return Optional.of(Integer.valueOf(port));
				} catch (Exception e) {
					return Optional.empty();
				}
			}
		});
		
		return res;
	}
}
