package com.vcorsi.rest_scheduler.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.vcorsi.rest_scheduler.service.SchedulerException;
import com.vcorsi.rest_scheduler.service.SchedulerService;
import com.vcorsi.rest_scheduler.service.TaskDescription;
import com.vcorsi.rest_scheduler.service.TaskResult;

/**
 * An implementation of {@link SchedulerService} accepting tasks on the form of Groovy scripts.
 * 
 * @author vcorsi
 *
 */
class SchedulerServiceImpl implements SchedulerService{
	
	private static final Logger LOGGER = Logger.getLogger(SchedulerServiceImpl.class.getName());
	
	private final ScriptEngine engine;
	private final ExecutorService executor;
	private final Map<String, Future<TaskResult>> taskMap;
	private final AtomicLong idGenerator;

	/**
	 * @param queueCapacity the maximum number of tasks in queue before being executed. Must be > 0.
	 * @param minThreads initial number of threads processing the tasks. Must be > 0.
	 * @param maxThreads maximum number of threads processing the tasks. Must be > 0.
	 */
	SchedulerServiceImpl(int queueCapacity, int minThreads, int maxThreads){
		LOGGER.info("Creating " + SchedulerServiceImpl.class.getName() 
				+ " with queue capacity: " + queueCapacity 
				+ ", minimum threads: " + minThreads
				+ ", maximum threads: " + maxThreads);
		
		assert (queueCapacity > 0 && minThreads > 0 && maxThreads > 0);
		engine = new ScriptEngineManager().getEngineByName("Groovy");
		assert engine != null;
		final BlockingQueue<Runnable> q = new ArrayBlockingQueue<>(queueCapacity);
		executor = new ThreadPoolExecutor(minThreads, maxThreads, 20, TimeUnit.SECONDS, q);
		taskMap = new ConcurrentHashMap<>();
		idGenerator = new AtomicLong();
	}

	@Override
	public String submitTask(final String scriptText) throws SchedulerException {
		Objects.requireNonNull(scriptText);
		LOGGER.info("A new task is being submitted");
		
		final Future<TaskResult> resultFuture;
		final String taskId = String.valueOf(idGenerator.incrementAndGet());
		try{
		    resultFuture = executor.submit(new SchedulerCallable(engine, taskId, scriptText));
		}catch(RejectedExecutionException e){
			LOGGER.warning("Task has been rejected");
			LOGGER.log(Level.FINE, e.getMessage(), e);
			
			throw new SchedulerException(e.getMessage(), e);
		}
		
		taskMap.put(taskId, resultFuture);
		return taskId;
	}

	@Override
	public List<TaskDescription> listTasks() {
		LOGGER.info("Tasks are being listed");
		if(taskMap.isEmpty()){
			return Collections.emptyList();
		}
		final List<TaskDescription> retVal = new ArrayList<>();
		for(Map.Entry<String, Future<TaskResult>> e : taskMap.entrySet()){
		    final Future<TaskResult> future = e.getValue();
			final boolean terminated = future.isDone();
		    final String taskId = e.getKey();
			if(terminated){
				try {
					final TaskResult taskResult = future.get();
					retVal.add(new TaskDescription(taskId, terminated, taskResult));
				} catch (final ExecutionException | InterruptedException ee) {
					//should never happen
					LOGGER.log(Level.SEVERE, ee.getMessage(), ee);
				}
		    }else{
		        retVal.add(new TaskDescription(taskId, terminated));
		    }
		}
		return retVal;
	}

	@Override
	public TaskResult getResult(final String taskId) throws SchedulerException {
		Objects.requireNonNull(taskId);
		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.info("A result is asked for task with id: " + taskId);
		}
		final Future<TaskResult> f = taskMap.get(taskId);
		if(f == null){
			if (LOGGER.isLoggable(Level.WARNING)) {
			    LOGGER.warning("Task with id " + taskId + " not found");
			}
			throw new SchedulerException("Task not found");
		}
		if(!f.isDone()){
			if (LOGGER.isLoggable(Level.WARNING)) {
			    LOGGER.warning("Task with id " + taskId + " not terminated");
			}
			throw new SchedulerException("Task not terminated");
		}
		try {
			return f.get();
		} catch (InterruptedException e) {
			LOGGER.severe("Received an InterruptedException, should not happen");
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			
			throw new SchedulerException(e.getMessage(), e);
		} catch (ExecutionException e) {
			LOGGER.severe("Received an ExecutionException");
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			
			throw new SchedulerException(e.getMessage(), e);
		}
	}

	@Override
	public boolean removeTask(final String taskId) {
		Objects.requireNonNull(taskId);
		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.info("Task with id: " + taskId + " is going to be removed");
		}
		final Future<TaskResult> tr = taskMap.remove(taskId);
		if(tr == null){
			if (LOGGER.isLoggable(Level.WARNING)) {
			    LOGGER.warning("Task with id: " + taskId + " not found");
			}
			return false;
		}
		tr.cancel(false);
		return true;
	}

}
