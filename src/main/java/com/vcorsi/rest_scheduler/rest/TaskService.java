package com.vcorsi.rest_scheduler.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;

import com.vcorsi.rest_scheduler.service.SchedulerException;
import com.vcorsi.rest_scheduler.service.SchedulerService;
import com.vcorsi.rest_scheduler.service.TaskDescription;
import com.vcorsi.rest_scheduler.service.TaskResult;

/**
 * <p>Represents the Jax-RS REST end point for the {@link SchedulerService} interface. Below the REST API is documented.</p>
 * 
 * <h1>/task</h1>
 *     <h2>Methods</h2>
 *         <h3>PUT</h3>    
 *         <p>Submit a task on the form of a Groovy script. This method expect an "application/x-www-form-urlencoded" PUT
 *         with a single "_script" parameter containing the Groovy script.</p>
 *         <h4>Responses</h4>
 *         <ul>
 *           <li>200 - text/plain if the script is correctly submitted. The script id is returned as the response content.</li>
 *           <li>400 - text/plain if the input is absent or the service cannot handle the request. An error message is returned as the response content.</li>
 *        </ul>
 *        <h3>GET</h3>
 *        <p>Lists all the accepted tasks.</p>
 *        <h4>Responses</h4>
 *        <ul>
 *          <li>200 - application/xml a XML representations of the tasks. For example:<pre>{@code
 <tasks>
   <task>
      <taskId>11</taskId>
      <error>false</error>
      <terminated>true</terminated>
      <result>Hello world!</result>
   </task>
   <task>
      <taskId>12</taskId>
      <error>false</error>
      <terminated>false</terminated>
   </task>
 </tasks>
}</pre></li>
 *        </ul>
 *        
 * <h1>/task/{id}</h1>
 *     <h2>Methods</h2>
 *         <h3>GET</h3>
 *         <p>Retrieves the result of the evaluation of the task with the identifier provided in as a path parameter, e.g. "GET /task/247".</p>
 *         <h4>Responses</h4>
 *         <ul>
 *           <li>200 - application/xml a XML representations of the task with the evaluation result. For example:<pre>{@code
<task>
   <taskId>11</taskId>
   <error>true</error>
   <errorMessage>Some Groovy error</errorMessage>
   <terminated>true</terminated>
   <result/>
</task>
 *           }</pre></li>
 *           <li>400 - text/plain if the task does not exist or has not terminated yet. An error message is returned as the response content.</li>
 *         </ul>
 *         <h3>DELETE</h3>
 *         <p>Removes from the list of accepted tasks the task with the identifier provided in as a path parameter, e.g. "DELETE /task/247".
 *         If the given task has already started executing, it won't be interrupted.</p>
 *         <h4>Responses</h4>
 *         <ul>
 *           <li>200 - text/plain with the content "true" if task has been removed or "false" if it has not because the task with the given id does not exist.</li>
 *           <li>400 - text/plain if the id parameter is absent. An error message is returned as the response content.</li>
 *         </ul>
 *          
 * @author vcorsi
 *
 */
@Path("task")
public class TaskService {

	@Context
	private ContextResolver<SchedulerService> serviceResolver;

	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@PUT
	@Path("/")
	public String submit(@FormParam("_script") String script) throws BadRequestException {
		if (script == null) {
			throw new BadRequestException("Absent script input");
		}
		try {
			return getService().submitTask(script);
		} catch (SchedulerException e) {
			throw new BadRequestException(e.getMessage(), e);
		}
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_XML)
	public List<Task> list() {
		final List<TaskDescription> descriptions = getService().listTasks();
		return Utils.adapt(descriptions);
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Task get(@PathParam("id") String taskId) throws BadRequestException {
		if (taskId == null) {
			throw new BadRequestException("Absent task id input");
		}
		TaskResult r;
		try {
			r = getService().getResult(taskId);
		} catch (SchedulerException e) {
			throw new BadRequestException(e.getMessage(), e);
		}
		return Utils.adapt(taskId, r);
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public String delete(@PathParam("id") String taskId) throws BadRequestException {
		if (taskId == null) {
			throw new BadRequestException("Absent task id input");
		}
		return String.valueOf(getService().removeTask(taskId));
	}

	private SchedulerService getService() {
		return serviceResolver.getContext(null);
	}

}
