package org.mindboxing.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessExecutionListener implements ExecutionListener  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger log = LoggerFactory.getLogger(ProcessExecutionListener.class);
	
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		log.debug("ExecutionListener Variables = "+execution.getVariables());
	}

}
