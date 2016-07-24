package com.vcorsi.rest_scheduler.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.vcorsi.rest_scheduler.service.TaskDescription;
import com.vcorsi.rest_scheduler.service.TaskResult;

/**
 * Some utilitary methods for type adaptations.
 * 
 * @author vcorsi
 *
 */
class Utils {

	public static List<Task> adapt(final List<TaskDescription> descriptions) {
		Objects.requireNonNull(descriptions);
		if(descriptions.isEmpty()){
			return Collections.emptyList();
		}
		final List<Task> retVal = new ArrayList<>();
		for(final TaskDescription d : descriptions){
			final Task t = new Task();
			fillDescription(t, d);
			retVal.add(t);
		}
		return retVal;
	}

	public static Task adapt(final String taskId, final TaskResult r) {
		Objects.requireNonNull(taskId);
		Objects.requireNonNull(r);
		final Task retVal = new Task();
		fillDescription(retVal, new TaskDescription(taskId,true, r));
		return retVal;
	}
	
	private static void fillDescription(final Task t, final TaskDescription taskDescription){
		t.setTaskId(taskDescription.getTaskId());
		t.setTerminated(taskDescription.isTerminated());
		if(taskDescription.getTaskResult().isPresent()){
			fillResult(t, taskDescription.getTaskResult().get());
		}
	}

	private static void fillResult(final Task t, final TaskResult taskResult) {
		t.setError(taskResult.isError());
		t.setErrorMessage(taskResult.getErrorMessage().orElse(null));
		t.setResult(taskResult.getResult());
	}

}
