package io.erfan.llogger;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Utils {
    /**
     * Format duration to display to user
     * Format:
     *   0sec
     *   0min 0sec
     *   0hr
     *   0hr 0min
     * depending on which one suites the duration
     *
     * @param duration the duration to format
     * @return a string with the formatted duration
     */
    public static String formatDuration(Long duration) {
        if (duration < 60) {
            return String.format(Locale.ENGLISH, "%dsec", duration);
        } else if (duration < 3600) {
            return String.format(Locale.ENGLISH, "%dmin %dsec", duration / 60, duration % 60);
        } else if (duration % 3600 < 60) {
            return String.format(Locale.ENGLISH, "%dhr", duration / 3600);
        } else {
            return String.format(Locale.ENGLISH, "%dhr %dmin", duration / 3600, (duration % 3600) / 60);
        }
    }


    /**
     * Format distance to display to user
     * Format:
     *   0m
     *   0.0km
     * depending on which on suites the distance
     *
     * @param distance the distance to be formatted
     * @return a string with the formatted distance
     */
    public static String formatDistance(Long distance) {
        if (distance < 1000) {
            return String.format(Locale.ENGLISH, "%dm", distance);
        } else {
            return String.format(Locale.ENGLISH, "%.1fkm", ((float) distance)/1000);
        }
    }

    /**
     * add the duration of a list of timespans
     *
     * @param timespans a list of timespans {@link Timespan}
     * @return the sum duration of the timespans
     */
    public static long calculateTimespansDuration(List<Timespan> timespans) {
        long result = 0;
        for (Utils.Timespan timespan : timespans) {
            result += timespan.end - timespan.start;
        }
        return result;
    }

    /**
     * The key of the day value in the map returned by calculateDayNightDuration
     */
    public static final int DAY = 0;
    /**
     * The key of the night value in the map returned by calculateDayNightDuration
     */
    public static final int NIGHT = 1;

    /**
     * calculate the amount driven during day/night from timespans and drive conditions
     *
     * @param timespans a list of timespans {@link Timespan} to get duration from
     * @param driveConditions drive condition (containing be timestamp for start/end of today)
     * @return a map containing two value one for day with key {@link #DAY} and one for night with key {@link #NIGHT}
     */
    public static Map<Integer, Long> calculateDayNightDuration(List<Timespan> timespans, DriveConditions driveConditions) {
        long day = 0;
        long night = 0;
        for (Utils.Timespan timespan : timespans) {
            // calculate the duration of day in this timespan
            long thisDay = findOverlap(timespan.start, timespan.end,
                    driveConditions.getDayStart(), driveConditions.getDayEnd());
            thisDay += findOverlap(timespan.start, timespan.end,
                    driveConditions.getDayStart() - 86400, driveConditions.getDayEnd() - 86400); // yesterday

            // add anything that's not day to night
            night += (timespan.end - timespan.start) - thisDay;
            day += thisDay;
        }

        Map<Integer, Long> result = new ArrayMap<>();
        result.put(DAY, day);
        result.put(NIGHT, night);
        return result;
    }

    /**
     * A parcelable class to represent timespans (ranges of time)
     */
    public static class Timespan implements Parcelable {
        public long start;
        public long end;

        public Timespan(long start, long end) {
            this.start = start;
            this.end = end;
        }

        private Timespan(Parcel in) {
            start = in.readLong();
            end = in.readLong();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(start);
            dest.writeLong(end);
        }

        public static final Creator<Timespan> CREATOR = new Creator<Timespan>() {
            public Timespan createFromParcel(Parcel in) {
                return new Timespan(in);
            }

            public Timespan[] newArray(int size) {
                return new Timespan[size];
            }
        };
    }

    /**
     * Find the overlap of two ranges
     *
     * @param r1Start start range 1
     * @param r1End end range 1
     * @param r2Start start range 2
     * @param r2End end of range 2
     * @return the overlap of the two ranges
     */
    public static long findOverlap(long r1Start, long r1End, long r2Start, long r2End) {
        // |------------|
        //    |-----|
        if (r1Start <= r2Start && r1End >= r2End) {
            return r2End - r2Start;
        //    |-----|
        // |------------|
        } else if (r2Start <= r1Start && r2End >= r1End) {
            return r1End - r1Start;
        // |----|
        //    |-----|
        } else if (r2Start <= r1End && r2End >= r1End) {
            return r1End - r2Start;
            //    |-----|
            // |----|
        } else if (r1Start <= r2End && r1End >= r2End) {
            return r2End - r1Start;
            //          |-----|
            // |----|
            // ----------------
            // |----|
            //          |-----|
        } else {
            return 0L;
        }
    }
}
