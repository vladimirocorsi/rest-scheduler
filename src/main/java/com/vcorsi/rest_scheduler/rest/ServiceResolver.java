package com.vcorsi.rest_scheduler.rest;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.vcorsi.rest_scheduler.service.SchedulerService;
import com.vcorsi.rest_scheduler.service.impl.SchedulerServiceBuilder;

/**
 * Allows to inject a {@link SchedulerService} instance in {@link TaskService}.
 * 
 * @author vcorsi
 *
 */
@Provider
public class ServiceResolver implements ContextResolver<SchedulerService>{
	
	private final SchedulerService service;

	public ServiceResolver(){
		service = new SchedulerServiceBuilder().build();
	}

	@Override
	public SchedulerService getContext(Class<?> type) {
		return service;
	}

}
