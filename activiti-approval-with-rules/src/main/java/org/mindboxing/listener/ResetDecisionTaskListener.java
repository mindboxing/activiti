package org.mindboxing.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.mindboxing.pojo.AuthorizationAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResetDecisionTaskListener implements ExecutionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger log = LoggerFactory.getLogger(ResetDecisionTaskListener.class);
	
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		AuthorizationAction action = (AuthorizationAction) execution.getVariable("action");
		log.debug("RESET decision "+action.getDecision());
		action.setDecision(null);
	}
}
