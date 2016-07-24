package com.vcorsi.rest_scheduler.rest;

import com.vcorsi.rest_scheduler.service.SchedulerException;

/**
 * Represents a business exception (see {@link SchedulerException}) or parameter validation issue.
 * It is rendered as a HTTP bad request by {@link ExceptionHandler}.
 * 
 * @author vcorsi
 *
 */
class BadRequestException extends Exception {
	
	private static final long serialVersionUID = -4267714799372310407L;

	public BadRequestException(String message, Throwable cause){
		super(message, cause);
	}

	public BadRequestException(String message) {
		super(message);
	}

}
