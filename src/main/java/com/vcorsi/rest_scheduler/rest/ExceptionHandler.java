package com.vcorsi.rest_scheduler.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Allows Jax-RS implementation to map exceptions occurring in resource methods. 
 * In case of business exceptions and input validation failures the exception provided is of type
 * {@link BadRequestException}, which is rendered as a HTTP error 400. Otherwise a code 500 is returned.
 * 
 * @author vcorsi
 *
 */
@Provider
public final class ExceptionHandler implements
	ExceptionMapper<Exception> {
	
	private static final Logger LOGGER = Logger.getLogger(ExceptionHandler.class.getName());

	@Override
	public Response toResponse(final Exception exception) {
		if (exception instanceof BadRequestException) {
			return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).type(MediaType.TEXT_PLAIN)
					.build();
		}else{
			LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
			
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
	}	
}