package com.vcorsi.rest_scheduler.service;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents the result of the execution of a task.
 * 
 * @author vcorsi
 *
 */
public class TaskResult {
	
	private final String result;
	private final boolean isError;
	private Optional<String> errorMessage;
	
	public TaskResult(final String result, final boolean isError){
		this(result, isError, Optional.empty());
	}
	
	public TaskResult(final String result, final boolean isError, final String errorMessage){
		this(result, isError, Optional.of(errorMessage));
	}
	
	private TaskResult(String result, boolean isError, Optional<String> errorMessage) {
		this.result = Objects.requireNonNull(result);
		this.isError = isError;
		this.errorMessage = errorMessage;
	}

	public Optional<String> getErrorMessage() {
		return errorMessage;
	}
	
	public String getResult() {
		return result;
	}
	public boolean isError() {
		return isError;
	}
}
