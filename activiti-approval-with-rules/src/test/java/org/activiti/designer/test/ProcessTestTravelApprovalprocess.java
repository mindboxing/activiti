package org.activiti.designer.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mindboxing.pojo.AuthorizationAction;
import org.mindboxing.schemas.itinerary.AirSegmentType;
import org.mindboxing.schemas.itinerary.FareAmountType;
import org.mindboxing.schemas.itinerary.Itinerary;
import org.mindboxing.schemas.itinerary.LowFaresType;
import org.mindboxing.schemas.itinerary.PNRType;
import org.mindboxing.schemas.itinerary.SegmentType;
import org.mindboxing.schemas.itinerary.TDCAuthorizerType;
import org.mindboxing.schemas.itinerary.TDCType;
import org.mindboxing.schemas.itinerary.TravelPointType;
import org.mindboxing.schemas.itinerary.UserDefinedFieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RunWith(JUnit4.class)
public class ProcessTestTravelApprovalprocess {

	Logger log = LoggerFactory.getLogger(ProcessTestTravelApprovalprocess.class);
	
	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	public static final String EMPLOYEE_BENEFIT = "U";
	public static final String EMPLOYEE_MISSION = "O";
	
	RuntimeService runtimeService = null;
	Map<String, Object> variableMap = null;
	
	@Before
	public void setUp(){		
		runtimeService = activitiRule.getRuntimeService();
		AuthorizationAction action = new AuthorizationAction();
		action.setFareOption(9400);
		variableMap = new HashMap<String, Object>();
		variableMap.put("pnr", buildTDIItinerray(EMPLOYEE_MISSION));
		variableMap.put("action", action);
	}
	
	@Test
	@Deployment(resources = {
			"diagrams/approval-process.bpmn",
			"diagrams/notification_process.bpmn",
			"drools/TravelPolicyRules.drl"			
		 } )		
	public void testTravelerTimeout() throws Exception {
		log.debug("\n>>>> TEST CASE :: testTravelerTimeout");
		variableMap.put("timeout", "PT3S");				
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("mb-approval-process", variableMap);
		Thread.sleep(10000);
	}

	@Test
	@Deployment(resources = {
			"diagrams/approval-process.bpmn",
			"diagrams/notification_process.bpmn",
			"drools/TravelPolicyRules.drl"			
		 } )		
	public void testTravelerDecision() throws Exception {
		log.debug("\n>>>> TEST CASE :: testTravelerDecision");
		variableMap.put("timeout", "PT3S");		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("mb-approval-process", variableMap);
		makeDecision("DECLINED");
		Thread.sleep(10000);		
	}

	@Test
	@Deployment(resources = {
			"diagrams/approval-process.bpmn",
			"diagrams/notification_process.bpmn",
			"drools/TravelPolicyRules.drl"			
		 } )		
	public void testManagerTimeout() throws Exception {
		log.debug("\n>>>> TEST CASE :: testManagerTimeout");
		variableMap.put("timeout", "PT3S");		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("mb-approval-process", variableMap);
		makeDecision("APPROVED");
		Thread.sleep(15000);		
	}
	
	@Test
	@Deployment(resources = {
			"diagrams/approval-process.bpmn",
			"diagrams/notification_process.bpmn",
			"drools/TravelPolicyRules.drl"			
		 } )	
	public void testManagerDecision() throws Exception {
		log.debug("\n>>>> TEST CASE :: testManagerDecision");
		variableMap.put("timeout", "PT5S");		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("mb-approval-process", variableMap);
		makeDecision("APPROVED");
		Thread.sleep(3000);		
		makeDecision("CHANGE_REQUIRED");
		Thread.sleep(15000);		
	}
	
	@Test
	@Deployment(resources = {
			"diagrams/approval-process.bpmn",
			"diagrams/notification_process.bpmn",
			"drools/TravelPolicyRules.drl"			
		 } )
	public void testCancelApproval() throws Exception {
		log.debug("\n>>>> TEST CASE :: testCancelApproval");
		variableMap.put("timeout", "PT5S");		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("mb-approval-process", variableMap);
		Thread.sleep(2000);		
		List<Execution> executions = runtimeService.createExecutionQuery().messageEventSubscriptionName("CANCEL").list();
		Execution current = null;
		for(Execution exe : executions) {			
			log.debug(String.format("ProcessInstanceId=%s, ActivityId=%s, Id=%s", 
					exe.getProcessInstanceId(),
					exe.getActivityId(),
					exe.getId()
					));
			current= exe;
		}
		// Simulate decision made START
		log.debug("CANCEL");
		runtimeService.messageEventReceived("CANCEL", current.getId());			
	}

	
	
	private void makeDecision(String decision) {
		//System.out.println("id " + processInstance.getId() + " "+ processInstance.getProcessDefinitionId());
		List<Execution> executions = runtimeService.createExecutionQuery().messageEventSubscriptionName("DECISION_MADE").list();
		Execution current = null;
		for(Execution exe : executions) {
			log.debug(String.format("ProcessInstanceId=%s, ActivityId=%s, Id=%s", 
					exe.getProcessInstanceId(),
					exe.getActivityId(),
					exe.getId()
					));
			current= exe;
		}
		// Simulate decision made START
		log.debug("DECISION MADE");
		Map<String, Object> processVariables = new HashMap<String, Object>();
		if(current != null) {
			AuthorizationAction actionDecline = (AuthorizationAction) runtimeService.getVariable(current.getId(), "action");
			actionDecline.setDecision(decision);
			runtimeService.setVariable(current.getId(), "action", actionDecline);		
			runtimeService.messageEventReceived("DECISION_MADE", current.getId(), processVariables);						
		} else {
			log.debug("NO LISTENER TO DECISION MADE");			
		}
	}


	/**
	 * 
	 * @return
	 */
	private Itinerary buildTDIItinerray(String employeeType) {
		PNRType pnr = new PNRType();
		pnr.setId("TEST");
		
		TDCType tdc = new TDCType();
		UserDefinedFieldType udefEmployeeType = new UserDefinedFieldType();
		udefEmployeeType.setName("U60");
		// Mission	O, Y, A, B, 6, Q, I, S
		// Benefit	U, 4, 3, 5, W
		// Groups	G, N, E
		// Training T
		udefEmployeeType.setValue(employeeType);
		tdc.getUserDefinedFields().add(udefEmployeeType);
		tdc.setCompanyId("Trondent");
		
		pnr.setTDC(tdc);
		
		SegmentType segment1 = new SegmentType();
		TravelPointType org = new TravelPointType();
		org.setCountryCode("US");
		segment1.setOrigination(org);
		TravelPointType dest = new TravelPointType();
		dest.setCountryCode("US");
		segment1.setDestination(dest);
		segment1.setAir( new AirSegmentType() );
		pnr.getSegments().add( segment1 );
		
		Itinerary itinerary = new Itinerary();		
		itinerary.setPNR(pnr);
		
		TDCAuthorizerType authorizer = new TDCAuthorizerType();
		FareAmountType tripFare = new FareAmountType();
		tripFare.setFareCurrencyCode("USD");
		tripFare.setFare("10000"); // aka co
		authorizer.setTripFare(tripFare );
		pnr.setAuthorizer(authorizer);
		
		LowFaresType o1 = new LowFaresType();
		FareAmountType o1Fare = new FareAmountType();
		o1Fare.setFare("9000");
		o1Fare.setFareCurrencyCode("USD");
		o1.setFare(o1Fare);
		authorizer.getLowFares().add(o1);
		LowFaresType o2 = new LowFaresType();
		FareAmountType o2Fare = new FareAmountType();
		o2Fare.setFare("9400");
		o2Fare.setFareCurrencyCode("USD");
		o2.setFare(o2Fare );
		authorizer.getLowFares().add(o2);
		
		return itinerary;
	}	
}