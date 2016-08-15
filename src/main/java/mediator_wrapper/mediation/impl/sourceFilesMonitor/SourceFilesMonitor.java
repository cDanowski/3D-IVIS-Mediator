package mediator_wrapper.mediation.impl.sourceFilesMonitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import util.UrlConstants;

public class SourceFilesMonitor {

	private final WatchService watcher = FileSystems.getDefault().newWatchService();
	private final Map<WatchKey, Path> keyPaths = new ConcurrentHashMap<WatchKey, Path>();

	private volatile Thread fileChangeProcessingThread;
	private final String sourceFilesDirectory;

	@Autowired
	private SimpMessagingTemplate messageTemplate;

	public SourceFilesMonitor(String sourceFilesDirectory) throws IOException {
		this.sourceFilesDirectory = sourceFilesDirectory;

		File dir = new File(this.sourceFilesDirectory);

		Path p = dir.toPath();
		WatchKey key = p.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
		keyPaths.put(key, p);
	}

	private void processFileNotifications() throws InterruptedException {
		while (true) {
			WatchKey key = this.watcher.take();
			
			/*
			 * wait 1 second to prevent multiple events within short time distance!
			 */
			Thread.sleep(1000);

			Path dir = (Path) keyPaths.get(key);
			for (WatchEvent evt : key.pollEvents()) {
				WatchEvent.Kind eventType = evt.kind();
				if (eventType == StandardWatchEventKinds.OVERFLOW)
					continue;
				Object o = evt.context();
				if (o instanceof Path) {
					Path path = (Path) o;

					sendFileChangeEvent(dir, path, eventType);
				}
			}
			key.reset();
		}
	}

	private void sendFileChangeEvent(Path dir, Path file, Kind eventType) {

		System.out.println("File " + file + " has changed! Event: " + eventType.toString());

		/*
		 * send synchronization event to synchronization endpoint
		 */

		DataSourceChangeMessage dataSourceChangeMessage = new DataSourceChangeMessage();

		dataSourceChangeMessage.setDataSourceIdentifier(file.toString());

		messageTemplate.convertAndSend(UrlConstants.STOMP_CLIENT_DATA_SOURCE_CHANGE_ENDPOINT, dataSourceChangeMessage);
	}

	public void startListening() {
		fileChangeProcessingThread = new Thread() {
			public void run() {
				try {
					processFileNotifications();
				} catch (InterruptedException ex) {
					fileChangeProcessingThread = null;
				}
			}

		};

		fileChangeProcessingThread.start();

	}

	public void shutDownListener() {
		Thread thr = fileChangeProcessingThread;
		if (thr != null) {
			thr.interrupt();
		}
	}

}
