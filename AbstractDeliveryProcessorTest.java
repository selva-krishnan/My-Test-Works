import static org.junit.Assert.*;

import java.time.Clock;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.aa.flighthub.mte.EngineCacheKey;
import com.aa.flighthub.mte.MasterController;
import com.aa.flighthub.mte.MasterControllerException;
import com.aa.flighthub.mte.MessageHeader;
import com.aa.flighthub.mte.MessageKey;

/**
 * Junit Test Class for AbstractDeliveryProcessor
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/spring/integration/mte-test-core-context.xml" })
public class AbstractDeliveryProcessorTest {

	@Autowired
	@InjectMocks
	FakeAbstractDeliveryProcessor fakeAbstractDeliveryProcessor;

	@Autowired
	@InjectMocks
	MasterController masterController;

	private Message<?> testMessage;
	private MessageKey key;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		testMessage = MessageBuilder.withPayload("Test Message").build();
		key = new MessageKey(this.hashCode(), "TEST123");
		masterController.registerCache(key);
		masterController.addEngineProcessStartTime(key, LocalDateTime.now(Clock.systemUTC()),
				EngineCacheKey.ENGINEMESSAGEOUT);
		masterController.addEngineProcessStartTime(key, LocalDateTime.now(), EngineCacheKey.ENGINEMESSAGEIN);
	}

	/**
	 * Test method for
	 * {@link com.aa.flighthub.mte.processor.AbstractDeliveryProcessor#addMTEMessageTimeHeaders(org.springframework.messaging.Message)}.
	 */
	@Test
	public void testAddMTEMessageTimeHeaders() {
		try {
			Message<?> testMessageBuilder = MessageBuilder.fromMessage(testMessage)
					.setHeader(MessageHeader.KEY.getKey(), key).build();
			testMessage = fakeAbstractDeliveryProcessor.addMTEMessageTimeHeaders(testMessageBuilder);
			assertNotNull(testMessage.getHeaders());
			assertNotNull(testMessage.getHeaders().get(MessageHeader.MTEMESSAGEGETTTIME.getKey()));
			assertNotNull(testMessage.getHeaders().get(MessageHeader.MTEMESSAGESENDTIME.getKey()));
		} catch (HeaderDataNotFoundException | MasterControllerException e) {
			fail("Error running the testAddMTEMessageTimeHeaders() " + e.getMessage());
		}
	}

}
