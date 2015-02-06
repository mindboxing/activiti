package org.mindboxing;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
public class TestRule {

	Logger log = LoggerFactory.getLogger(TestRule.class);
	
	StatefulKnowledgeSession ksession = null;
	KnowledgeRuntimeLogger kLogger = null;
	private UserDefinedFieldType udefEmployeeType;
	private FareAmountType tripFare;
	private FareAmountType o1Fare;
	private FareAmountType o2Fare;
	private Itinerary itinerary;
	
	@Before
	public void setUp(){
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("drools/TravelPolicyRules.drl"), ResourceType.DRL);
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error: errors) {
                System.err.println(error);
            }
            throw new IllegalArgumentException("Could not parse knowledge.");
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        ksession = kbase.newStatefulKnowledgeSession();
        kLogger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");	
        
		PNRType pnr = new PNRType();
		pnr.setId("TEST");
		
		TDCType tdc = new TDCType();
		udefEmployeeType = new UserDefinedFieldType();
		udefEmployeeType.setName("U60");
		// Mission	O, Y, A, B, 6, Q, I, S
		// Benefit	U, 4, 3, 5, W
		// Groups	G, N, E
		// Training T
		udefEmployeeType.setValue("M");
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
		
		itinerary = new Itinerary();		
		itinerary.setPNR(pnr);
		
		TDCAuthorizerType authorizer = new TDCAuthorizerType();
		tripFare = new FareAmountType();
		tripFare.setFareCurrencyCode("USD");
		tripFare.setFare("10000"); // aka co
		authorizer.setTripFare(tripFare );
		pnr.setAuthorizer(authorizer);
		
		LowFaresType o1 = new LowFaresType();
		o1Fare = new FareAmountType();
		o1Fare.setFare("9000");
		o1Fare.setFareCurrencyCode("USD");
		o1.setFare(o1Fare);
		authorizer.getLowFares().add(o1);
		LowFaresType o2 = new LowFaresType();
		o2Fare = new FareAmountType();
		o2Fare.setFare("9400");
		o2Fare.setFareCurrencyCode("USD");
		o2.setFare(o2Fare );
		authorizer.getLowFares().add(o2);
        
	}

	@After
	public void tearDown(){
        kLogger.close();		
	}

	
	@Test
	public void testLowestFareSelected(){
		log.debug(">>> testLowestFareSelected");
        AuthorizationAction action = new AuthorizationAction();
        action.setFareOption(9000);
        
        ksession.insert(action);
        ksession.insert(itinerary);
        ksession.fireAllRules();        
        Assert.assertEquals(false, action.isManagerApproverRequired());
	}
	
	@Test
	public void testMissingAirSaving(){
		log.debug(">>> testMissingAirSaving");
        AuthorizationAction action = new AuthorizationAction();
        action.setFareOption(9600);
        tripFare.setFare("10000");
        o2Fare.setFare("9600");
        o1Fare.setFare("9000");
        
        ksession.insert(action);
        ksession.insert(itinerary);
        ksession.fireAllRules();        
        Assert.assertEquals(false, action.isManagerApproverRequired());
	}

	
	@Test
	public void testTravelPlanCost(){
		log.debug(">>> testTravelPlanCost");
        AuthorizationAction action = new AuthorizationAction();
        action.setFareOption(499);
        tripFare.setFare("499");
        o2Fare.setFare("440");
        o1Fare.setFare("405");
        
        ksession.insert(action);
        ksession.insert(itinerary);
        ksession.fireAllRules();        
        Assert.assertEquals(false, action.isManagerApproverRequired());		
	}

	
	@Test
	public void testTravelDeptRule(){
		log.debug(">>> testTravelDeptRule");
        AuthorizationAction action = new AuthorizationAction();
        udefEmployeeType.setValue("O");
        action.setFareOption(10000);
        tripFare.setFare("10000");
        o2Fare.setFare("9400");
        o1Fare.setFare("9000");
        
        ksession.insert(action);
        ksession.insert(itinerary);
        ksession.fireAllRules();        
        Assert.assertEquals(false, action.isManagerApproverRequired());		
	}

	@Test
	public void testNoLLF() {
		log.debug(">>> testNoLLF");
        AuthorizationAction action = new AuthorizationAction();
        action.setFareOption(10000);
        itinerary.getPNR().getAuthorizer().getLowFares().clear();
        
        ksession.insert(action);
        ksession.insert(itinerary);
        ksession.fireAllRules();        
        Assert.assertEquals(true, action.isAutoApproved());				
	}
	
}
