package com.vcorsi.rest_scheduler.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * POJO entity representing a task for the {@link TaskService} REST endpoint.
 * It is rendered as XML by {@link TaskService}.
 * 
 * @author vcorsi
 *
 */
@XmlRootElement
public class Task {
    private String taskId;
	private boolean terminated;
	private Boolean error;
	private String errorMessage;
	private String result;

	public void setTaskId(final String taskId) {
		this.taskId = taskId;
	}
	
	/**
	 * @return the task id
	 */
	public String getTaskId(){
		return taskId;
	}

	public void setTerminated(final boolean terminated) {
		this.terminated = terminated;
	}
	
	/**
	 * @return true if the task has terminated
	 */
	public boolean isTerminated(){
		return terminated;
	}

	public void setError(boolean error) {
		this.error = error;
	}
	
	/**
	 * @return for a terminated task, determines if an error occurred during execution
	 */
	public boolean isError(){
		return error;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	/**
	 * @return for a terminated task, the error message from the script engine, if any. 
	 * Can be null if no error message provided or if task has not terminated.
	 */
	public String getErrorMessage(){
		return errorMessage;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	/**
	 * @return for a terminated task, the result of the script evaluation. 
	 * Will be null if task has not terminated.
	 */
	public String getResult(){
		return result;
	}
}
