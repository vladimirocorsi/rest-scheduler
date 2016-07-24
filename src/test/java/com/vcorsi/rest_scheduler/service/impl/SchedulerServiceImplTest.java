package com.vcorsi.rest_scheduler.service.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vcorsi.rest_scheduler.service.SchedulerException;
import com.vcorsi.rest_scheduler.service.SchedulerService;
import com.vcorsi.rest_scheduler.service.TaskDescription;
import com.vcorsi.rest_scheduler.service.TaskResult;
import com.vcorsi.rest_scheduler.service.impl.SchedulerServiceBuilder;
import com.vcorsi.rest_scheduler.service.impl.SchedulerServiceImpl;

import junit.framework.TestCase;

public class SchedulerServiceImplTest extends TestCase {

	@Test
	public void testSubmitTaskBad() throws Exception {
		SchedulerService s = new SchedulerServiceImpl(10, 4, 20);
		final String id = s.submitTask("erroneous script");
		Thread.sleep(1000);
		final TaskResult tr = s.getResult(id);
		Assert.assertTrue(tr.isError());
		final TaskDescription td = s.listTasks().get(0);
		Assert.assertTrue(td.isTerminated());
		Assert.assertTrue(td.getTaskResult().isPresent());
	}

	@Test
	public void testSubmitTaskGood() throws Exception {
		SchedulerService s = new SchedulerServiceImpl(10, 4, 20);
		final String id = s.submitTask("a = 1 \n" + "b = 3 * 4\n" + "'hello ' + a * b \n");
		Thread.sleep(2000);
		final TaskResult tr = s.getResult(id);
		Assert.assertFalse(tr.isError());
		Assert.assertEquals("hello 12", tr.getResult());
		final TaskDescription td = s.listTasks().get(0);
		Assert.assertTrue(td.isTerminated());
	}

	@Test
	public void testSubmitTaskLong() throws Exception {
		SchedulerService s = new SchedulerServiceBuilder()
				.minThreads(4).
				maxThreads(4).
				queueCapacity(20).build();
		final String id = s.submitTask(
				"a = 1 \n" 
				+ "b = 3 * 4 \n" 
				+ "sleep(2000) \n" 
				+ "'hello ' "
				+ "+ a * b \n");
		Thread.sleep(500);
		final TaskDescription td = s.listTasks().get(0);
		Assert.assertFalse(td.isTerminated());
		Assert.assertFalse(td.getTaskResult().isPresent());
		try {
			s.getResult(id);
			fail();
		} catch (SchedulerException e) {
			// ok
		}
		Thread.sleep(3000);
		final TaskResult tr = s.getResult(id);
		Assert.assertFalse(tr.isError());
		Assert.assertEquals("hello 12", tr.getResult());
	}

	@Test
	public void testSubmitSeveralTasksWithSameVarNames() throws Exception {
		SchedulerService s = new SchedulerServiceImpl(10, 4, 20);
		// task1
		final String id1 = s.submitTask(
			"a = 1 \n" 
			 + "sleep(200) \n" 
			 + "b = 3 * 4 \n"
	         + "'hello ' + a * b \n");
		// task 2
		final String id2 = s.submitTask(
		  "a = 2 \n" 
		+ "b = 4 * a \n" 
		+ "sleep(100) \n" 
		+ "'hello ' + a * b \n");
		// task 3
		final String id3 = s.submitTask(
		"a = 1 \n" 
		+ "b = a * 2 \n" 
		+ "sleep(150) \n" 
		+ "'hello ' + a * b \n");

		Thread.sleep(10);
		final List<TaskDescription> listTasks = s.listTasks();
		int i = 0;
		for (TaskDescription td : listTasks) {
			Assert.assertFalse(td.isTerminated());
			i++;
		}
		try {
			s.getResult(id1);
			fail();
		} catch (SchedulerException e) {
			// ok
		}
		Assert.assertEquals(3, i);
		Thread.sleep(1000);
		// task 1
		final TaskResult tr1 = s.getResult(id1);
		Assert.assertFalse(tr1.isError());
		Assert.assertEquals("hello 12", tr1.getResult());

		// task 2
		final TaskResult tr2 = s.getResult(id2);
		Assert.assertFalse(tr2.isError());
		Assert.assertEquals("hello 16", tr2.getResult());

		// task 3
		final TaskResult tr3 = s.getResult(id3);
		Assert.assertFalse(tr3.isError());
		Assert.assertEquals("hello 2", tr3.getResult());
	}
	
	@Test
	public void testFillUpTaskQueue() throws Exception{
		SchedulerService s = new SchedulerServiceBuilder()
				.minThreads(2).
				maxThreads(2).
				queueCapacity(2).build();
		//feed 4 tasks for 2 threads + 2 queued
		for(int i = 0; i < 4; i++){
			s.submitTask(
					"a = 1 \n" 
					 + "sleep(200) \n" 
					 + "b = 3 * 4 \n"
			         + "'hello ' + a * b \n");
		}
		//try another one
		try{
		s.submitTask(
				"a = 1 \n" 
				 + "sleep(200) \n" 
				 + "b = 3 * 4 \n"
		         + "'hello ' + a * b \n");
		fail();
		}catch(SchedulerException e){
			//ok
		}
	}
	
	@Test
	public void testRemoveTask() throws Exception{
		SchedulerService s = new SchedulerServiceImpl(1, 1, 1);
		//submit task 1
		final String id1 = s.submitTask(
				"a = 1 \n" 
				 + "sleep(200) \n" 
				 + "b = 3 * 4 \n"
		         + "'hello ' + a * b \n");
		//submit task 2, will be queued
		final String id2 = s.submitTask(
				"a = 1 \n" 
				 + "sleep(200) \n" 
				 + "b = 3 * 4 \n"
		         + "'hello ' + a * b \n");
		
		Assert.assertFalse(s.removeTask("fooId"));
		for(final TaskDescription d : s.listTasks()){
			Assert.assertTrue(d.getTaskId().equals(id1) || d.getTaskId().equals(id2));
		}
		Assert.assertTrue(s.removeTask(id2));
		Assert.assertTrue(s.listTasks().size() == 1);
		Assert.assertTrue(s.listTasks().get(0).getTaskId().equals(id1));
		
		Assert.assertTrue(s.removeTask(id1));
		Assert.assertTrue(s.listTasks().size() == 0);
	}

}
