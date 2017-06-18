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
    public void calculateDayNightDurationToday() throws Exception {
        // give the start and end time for the same day
        long dayStart = 1497733200; // 7AM
        long dayEnd = 1497776400; // 7PM
        calculateDayNightDuration(dayStart, dayEnd);
    }

    @Test
    public void calculateDayNightDurationYesterday() throws Exception {
        // give start and end time for the day after to simulate drive starting the day before
        long dayStart = 1497733200 + 86400; // 7AM
        long dayEnd = 1497776400 + 86400; // 7PM
        calculateDayNightDuration(dayStart, dayEnd);
    }

    public void calculateDayNightDuration(long am7, long pm7) throws Exception {
        // constants to use
        final long am5 = 1497726000;
        final long am6 = 1497729600;
        final long am8 = 1497736800;
        final long pm5 = 1497769200;
        final long pm6 = 1497772800;
        final long pm8 = 1497780000;
        final long pm9 = 1497783600;
        final long anHour = 3600;

        DriveConditions driveConditions = new DriveConditions(am7, pm7, "Day", true);
        List<Utils.Timespan> timespans;
        Map<Integer, Long> durations;

        // all day
        timespans = Arrays.asList(new Utils.Timespan(pm5, pm6));
        durations = Utils.calculateDayNightDuration(timespans, driveConditions);

        assertEquals(Long.valueOf(anHour), durations.get(Utils.DAY));
        assertEquals(Long.valueOf(0L), durations.get(Utils.NIGHT));

        // all night (after runset)
        timespans = Arrays.asList(new Utils.Timespan(pm8, pm9));
        durations = Utils.calculateDayNightDuration(timespans, driveConditions);

        assertEquals(Long.valueOf(0L), durations.get(Utils.DAY));
        assertEquals(Long.valueOf(anHour), durations.get(Utils.NIGHT));

        // all night (before sunrise)
        timespans = Arrays.asList(new Utils.Timespan(am5, am6));
        durations = Utils.calculateDayNightDuration(timespans, driveConditions);

        assertEquals(Long.valueOf(0L), durations.get(Utils.DAY));
        assertEquals(Long.valueOf(anHour), durations.get(Utils.NIGHT));

        // one day timespan, and one night timespan
        timespans = Arrays.asList(new Utils.Timespan(pm5, pm6), new Utils.Timespan(pm8, pm9));
        durations = Utils.calculateDayNightDuration(timespans, driveConditions);
        assertEquals(Long.valueOf(anHour), durations.get(Utils.DAY));
        assertEquals(Long.valueOf(anHour), durations.get(Utils.NIGHT));

        // going over the day/night boundary (sunset)
        timespans = Arrays.asList(new Utils.Timespan(pm6, pm8));
        durations = Utils.calculateDayNightDuration(timespans, driveConditions);
        assertEquals(Long.valueOf(anHour), durations.get(Utils.DAY));
        assertEquals(Long.valueOf(anHour), durations.get(Utils.NIGHT));

        // going over the day/night boundary (sunrise)
        timespans = Arrays.asList(new Utils.Timespan(am6, am8));
        durations = Utils.calculateDayNightDuration(timespans, driveConditions);
        assertEquals(Long.valueOf(anHour), durations.get(Utils.DAY));
        assertEquals(Long.valueOf(anHour), durations.get(Utils.NIGHT));
    }

}