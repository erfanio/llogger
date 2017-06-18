package io.erfan.llogger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 24)
public class UtilsTest {
    @Test
    public void calculateDayNightDuration() throws Exception {
        DriveConditions driveConditions = new DriveConditions(1497733520L, 1497775315L, "Day", true);

        List<Utils.Timespan> timespans = Arrays.asList(new Utils.Timespan(1497773265, 1497773745));
        Map<Integer, Long> durations = Utils.calculateDayNightDuration(timespans, driveConditions);

        assertEquals(Long.valueOf(1497773745 - 1497773265), durations.get(Utils.DAY));
        assertEquals(Long.valueOf(0L), durations.get(Utils.NIGHT));
    }

}