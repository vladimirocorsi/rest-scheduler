package com.vcorsi.rest_scheduler.service.impl;

import com.vcorsi.rest_scheduler.service.SchedulerService;

/**
 * A Builder for creating {@link SchedulerService} instances.
 * 
 * @author vcorsi
 *
 */
public class SchedulerServiceBuilder {

	private int capacity;
	private int minThreads;
	private int maxThreads;

	public SchedulerServiceBuilder(){
		capacity = 20;
		minThreads = 4;
		maxThreads = 20;
	}
	
	/**
	 * @param capacity the capacity of the queue of the scheduler. Must be > 0.
	 * @return this Builder object
	 */
	SchedulerServiceBuilder queueCapacity(final int capacity){
		if(capacity <= 0){
			throw new IllegalArgumentException();
		}
		this.capacity = capacity;
		return this;
	}
	
	/**
	 * @param minThreads initial and minimum number threads of the scheduler. Must be > 0. Default is 4.
	 * @return this Builder object
	 */
	SchedulerServiceBuilder minThreads(final int minThreads){
		if(minThreads <= 0){
			throw new IllegalArgumentException();
		}
		this.minThreads = minThreads;
		return this;
	}
	
	/**
	 * @param maxThreads maximum number of threads of the scheduler. Must be > 0 and >= minThreads. Default is 20.
	 * @return this Builder object
	 */
	SchedulerServiceBuilder maxThreads(final int maxThreads){
		if(maxThreads <= 0){
			throw new IllegalArgumentException();
		}
		this.maxThreads = maxThreads;
		return this;
	}

	/**
	 * @return a {@link SchedulerService} instance
	 */
	public SchedulerService build(){
		if(maxThreads < minThreads){
			throw new IllegalArgumentException("maxThreads must be greater or equal to minThreads");
		}
		return new SchedulerServiceImpl(capacity, minThreads, maxThreads);
	}

}
