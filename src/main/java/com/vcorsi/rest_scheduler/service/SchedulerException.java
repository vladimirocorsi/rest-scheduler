package com.vcorsi.rest_scheduler.service;

/**
 * Basic business exception for the {@link SchedulerService} interface.
 * 
 * @author vcorsi
 *
 */
public class SchedulerException extends Exception {

	private static final long serialVersionUID = -5545644334594038149L;

	public SchedulerException(String message, Exception e) {
		super(message, e);
	}

	public SchedulerException(String message) {
		super(message);
	}

}
