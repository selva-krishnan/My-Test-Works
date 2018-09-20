/**
 * 
 */
package com.aa.flighthub.mte;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.connection.CachingConnectionFactory;

/**
 * @author Selva
 * Test Comment
 */
public class MTECachingConnectionFactory extends CachingConnectionFactory implements InvocationHandler {

	// protected static HorizonLogger logger = new
	// HorizonLogger("MQDeliveryOutboundAdapter", true, false);
	protected final Log logger = LogFactory.getLog(MTECachingConnectionFactory.class);

	private Map<Integer, LocalDateTime> cachedSessionsUsage = new HashMap<>();
	private int sessionIdleHours = 1;
	private Session cachedSession;

	/**
	 * 
	 */
	public MTECachingConnectionFactory() {
		super();
	}

	/**
	 * @param targetConnectionFactory
	 */
	public MTECachingConnectionFactory(ConnectionFactory targetConnectionFactory) {
		super(targetConnectionFactory);
		logger.info("Created a connection in MTECachingConnectionFactory");
	}

	/**
	 * @return the sessionIdleHours
	 */
	public int getSessionIdleHours() {
		return sessionIdleHours;
	}

	/**
	 * @param sessionIdleHours
	 *            the sessionIdleHours to set
	 */
	public void setSessionIdleHours(int sessionIdleHours) {
		this.sessionIdleHours = sessionIdleHours;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.jms.connection.CachingConnectionFactory#getSession(
	 * javax.jms.Connection, java.lang.Integer)
	 */
	@Override
	protected Session getSession(Connection connection, Integer mode) throws JMSException {
		// Get the session from Spring CachingConnectionFactory
		this.cachedSession = super.getSession(connection, mode);
		logger.info("Session Returned from CachingConnectionFactory " + this.cachedSession);
		if (checkForActiveSession()) {
			logger.info("Validated the active Session and adding it to Usage Map " + this.cachedSession);
			// Add the active session to the session usage map
			cachedSessionsUsage.put(this.cachedSession.getClass().hashCode(), LocalDateTime.now());
			logger.info("CacheUsageMap Keys --> " + cachedSessionsUsage.keySet().toString());
			logger.info("CacheUsageMap Values --> " + cachedSessionsUsage.values().toString());
		} else {
			logger.info("The session is null or idle for more than specififed time " + this.cachedSession);
			// Close the session as it's been idle for more than specified time
			this.cachedSession.close();
			// Call the get session method to get another session
			getSession(connection, mode);
		}
		logger.info("Active session is being returned for the use " + this.cachedSession);
		return this.cachedSession;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().equals("close")) {
			this.cachedSession.close();
		}
		return null;
	}

	/**
	 * @param this.cachedSession
	 * @return
	 */
	private boolean checkForActiveSession() {
		boolean isSessionActive = false;
		long idleHoursFromNow = 0;
		// Get the last used time from session usage map
		LocalDateTime sessionLastUsedTime = cachedSessionsUsage.get(this.cachedSession.getClass().hashCode());
		if (sessionLastUsedTime != null) {
			// Calculate the session idle time
			idleHoursFromNow = sessionLastUsedTime.until(LocalDateTime.now(), ChronoUnit.MINUTES);
			logger.info("The session " + this.cachedSession + " is idle for " + idleHoursFromNow + "Minutes");
			// Compare the idle time against the specified limit
			if (idleHoursFromNow < sessionIdleHours) {
				isSessionActive = true;
			}
		} else {
			isSessionActive = true;
		}
		return isSessionActive;
	}
}
