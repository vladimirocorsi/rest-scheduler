package com.vcorsi.rest_scheduler.service.impl;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import com.vcorsi.rest_scheduler.service.TaskResult;

/**
 * A {@link Callable} to perform the evaluation of a script.
 * 
 * @author vcorsi
 *
 */
class SchedulerCallable implements Callable<TaskResult> {

	private static final Logger LOGGER = Logger.getLogger(SchedulerCallable.class.getName());

	private final ScriptEngine engine;
	private final String scriptText;
	private final String taskId;

	/**
	 * @param engine
	 *            a {@link ScriptEngine} for evaluating scripts
	 * @param scriptText
	 *            the script to evaluate
	 */
	SchedulerCallable(final ScriptEngine engine, final String taskId, final String scriptText) {
		assert engine != null && scriptText != null && taskId != null;
		this.engine = engine;
		this.scriptText = scriptText;
		this.taskId = taskId;
	}

	@Override
	public TaskResult call() throws Exception {
		try {
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("Task with id: " + taskId + " is going to be evaluated");
			}
			final Object o = engine.eval(scriptText, new SimpleScriptContext());
			final String result = o == null ? "" : o.toString();
			if (result.isEmpty()) {
				if (LOGGER.isLoggable(Level.WARNING)) {
					LOGGER.warning("Empty result for task with id: " + taskId);
				}
			}
			return new TaskResult(result, false);
		} catch (ScriptException e) {
			if (LOGGER.isLoggable(Level.WARNING)) {
				LOGGER.warning("Error during script evaluation for task with id: " + taskId);
				LOGGER.log(Level.FINE, e.getMessage(), e);
			}
			return new TaskResult("", true, e.getMessage());
		}
	}

}
