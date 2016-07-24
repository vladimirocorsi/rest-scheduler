package com.vcorsi.rest_scheduler.service;

import java.util.List;

/**
 * Interface for submitting, listing and deleting tasks to a generic scheduler.
 * Tasks are over the form of text scripts.
 * 
 * @author vcorsi
 *
 */
public interface SchedulerService {
	
	/**
	 * Asynchronously submit a task.
	 * 
	 * @param scriptText the script representing the task to submit
	 * @return the assigned task id
	 * @throws SchedulerException if the engine rejects the execution of the task, i.e. too many tasks waiting
	 */
	String submitTask(String scriptText) throws SchedulerException;
	
	/**
	 * List all the tasks accepted by the scheduler.
	 * 
	 * @return the descriptions of the tasks
	 */
	List<TaskDescription> listTasks();
	
    /**
     * Get the result of a terminated task.
     * 
     * @param taskId the identifier of the task for which we ask the result
     * @return the result of the task
     * @throws SchedulerException if the task is not terminated yet
     */
	
    TaskResult getResult(String taskId) throws SchedulerException;
    
    /**
     * Removes a task from the list of accepted tasks, if present. 
     * If the given task has already started executing, it won't be interrupted.
     *  
     * @param taskId the identifier of the task
     * @return true if the task has been removed
     */
    boolean removeTask(String taskId);
}
