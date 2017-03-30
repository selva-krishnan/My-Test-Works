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
