package org.activiti.designer.test;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mindboxing.pojo.AuthorizationAction;
@RunWith(JUnit4.class)
public class ProcessTestNotificationprocess {

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	@Test
	@Deployment(resources={"diagrams/notification_process.bpmn"})
	public void startProcess() throws Exception {
		RuntimeService runtimeService = activitiRule.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("timeout", "PT5S");
		variableMap.put("approverLevel", 1);		
		AuthorizationAction action = new AuthorizationAction();
		variableMap.put("action", action);

		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("notification-process", variableMap);
		assertNotNull(processInstance.getId());
		System.out.println("id " + processInstance.getId() + " "
				+ processInstance.getProcessDefinitionId());
		
		Thread.sleep(7000);
	}
}