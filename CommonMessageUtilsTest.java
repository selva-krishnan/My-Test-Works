package com.aa.flighthub.mte.utils;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.integration.support.MessageBuilder;

import com.aa.flighthub.mte.testutil.AbstractTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class CommonMessageUtilsTest extends AbstractTestUtils {
	
	private LocalDateTime localDateTime;
	
	@Before
	public void setUp() throws Exception {
		localDateTime = LocalDateTime.now();
	}

	@Test
	public void testSerializable() {

		assertTrue(CommonMessageUtils.isSerializable(null));

		assertTrue(CommonMessageUtils.isSerializable(new String("")));

		assertFalse(CommonMessageUtils.isSerializable(new Object()));

		assertTrue(CommonMessageUtils.isPayloadSerializable(MessageBuilder.withPayload("Test").build()));

		assertFalse(CommonMessageUtils.isPayloadSerializable(MessageBuilder.withPayload(new Object()).build()));

		assertFalse(CommonMessageUtils.isPayloadSerializable(null));

	}

	@Test
	public void testGetTimeInUTCFormat() {
		assertEquals(getTimeInUTCFormat(localDateTime),
				CommonMessageUtils.getTimeInUTCFormat(localDateTime));
		assertTrue(null == CommonMessageUtils.getTimeInUTCFormat(null));
	}

	private LocalDateTime getTimeInUTCFormat(LocalDateTime currentTime) {
		ZonedDateTime zonedTime = currentTime.atZone(ZoneId.systemDefault());
		ZonedDateTime utcZonedTime = zonedTime.withZoneSameInstant(ZoneId.of("UTC"));
		return utcZonedTime.toLocalDateTime();
	}

}
