package com.vcorsi.rest_scheduler.service;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents the state, i.e. terminated or not, of a submitted task. If the task is not terminated, a {@link TaskResult} property
 * is present.
 * 
 * @author vcorsi
 *
 */
public class TaskDescription {
	
	private final String taskId;
	private final boolean terminated;
	private final Optional<TaskResult> taskResult;
	
	public TaskDescription(final String taskId, final boolean terminated){
		this(taskId, terminated, Optional.empty());
	}
	
	public TaskDescription(final String taskId, final boolean terminated, final TaskResult tr){
		this(taskId, terminated, Optional.of(tr));
	}
	
	private TaskDescription(final String taskId, final boolean terminated, final Optional<TaskResult> tr){
		this.taskId = Objects.requireNonNull(taskId);
		this.terminated = terminated;
		taskResult = tr;
	}

	public String getTaskId() {
		return taskId;
	}

	public boolean isTerminated() {
		return terminated;
	}

	public Optional<TaskResult> getTaskResult() {
		return taskResult;
	}

}
