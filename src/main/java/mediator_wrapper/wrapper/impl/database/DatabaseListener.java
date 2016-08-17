package mediator_wrapper.wrapper.impl.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.postgresql.PGNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import mediator_wrapper.mediation.impl.sourceFilesMonitor.DataSourceChangeMessage;
import util.UrlConstants;

public class DatabaseListener extends Thread {

	private Connection conn;
	private org.postgresql.PGConnection pgconn;
	private String channelName = "ivisnotification";
	
	private String wrapperReference;

	@Autowired
	private SimpMessagingTemplate messageTemplate;

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
		this.pgconn = (org.postgresql.PGConnection)conn;
	}

	public org.postgresql.PGConnection getPgconn() {
		return pgconn;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getWrapperReference() {
		return wrapperReference;
	}

	public void setWrapperReference(String wrapperReference) {
		this.wrapperReference = wrapperReference;
	}

	public void run() {
		
		Statement listenStatement;
		try {
			listenStatement = conn.createStatement();
			listenStatement.execute("LISTEN " + channelName);
			listenStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		while (true) {
			try {
				// issue a dummy query to contact the backend
				// and receive any pending notifications.
				Statement dummyStatement = conn.createStatement();
				ResultSet rs = dummyStatement.executeQuery("SELECT 1");
				rs.close();
				dummyStatement.close();

				org.postgresql.PGNotification notifications[] = pgconn.getNotifications();
				if (notifications != null) {
					for (int i=0; i<notifications.length; i++) {
						PGNotification pgNotification = notifications[i];
						String idOfModifiedRecord = pgNotification.getParameter();
						
						/*
						 * send synchronization event to synchronization endpoint
						 */

						DataSourceChangeMessage dataSourceChangeMessage = new DataSourceChangeMessage();

						dataSourceChangeMessage.setDataSourceIdentifier(wrapperReference);
						
						dataSourceChangeMessage.setRecordId(idOfModifiedRecord);

						messageTemplate.convertAndSend(UrlConstants.STOMP_CLIENT_DATA_SOURCE_CHANGE_ENDPOINT, dataSourceChangeMessage);
						
					}
				}

				// wait a while before checking again for new
				// notifications
				Thread.sleep(1000);
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}

}
