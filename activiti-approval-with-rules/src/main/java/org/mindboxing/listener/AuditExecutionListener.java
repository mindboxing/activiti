package org.mindboxing.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.mindboxing.pojo.AuthorizationAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditExecutionListener implements ExecutionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Logger log = LoggerFactory.getLogger(AuditExecutionListener.class);
	
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String eventName = execution.getEventName();
		String activityName = execution.getCurrentActivityName();
		AuthorizationAction action = (AuthorizationAction) execution.getVariable("action");
		log.debug(String.format("AUDIT eventName=%s, activityName=%s, action.decision=%s",
				eventName,
				activityName,
				action.getDecision()
				));
	}

}
