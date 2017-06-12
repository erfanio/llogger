package io.erfan.llogger;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import java.util.List;

import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Drive;
import io.erfan.llogger.model.DriveDao;

public class StatsUtils {
    private static ArrayMap<String, Float> mData;
    private static long mDriverId;
    private static int mEntriesCount;
    private static int mTotalHours;

    private static void updateData(Context context) {
        // get driver ID
        PreferenceManager prefMan = new PreferenceManager(context);
        Long driverId = prefMan.getUser();

        // get the drive DAO
        DaoSession daoSession = ((App) context.getApplicationContext()).getDaoSession();
        DriveDao driveDao = daoSession.getDriveDao();

        // see if data is stale (or not for the same driver)
        if (driverId == mDriverId && driveDao.count() == mEntriesCount) {
            // if no new entries, the old data is still fresh
            return;
        }

        // get the list of drives
        List<Drive> drives = driveDao.queryBuilder().where(DriveDao.Properties.DriverId.eq(driverId))
                .orderDesc(DriveDao.Properties.Time).list();

        // loop through drives and get the total drive times
        int day = 0;
        int night = 0;
        for (Drive drive : drives) {
            day += drive.getDayDuration();
            night += drive.getNightDuration();
        }

        // save data
        mTotalHours = (day + night) / 3600;
        mDriverId = driverId;
        mEntriesCount = drives.size();

        mData = new ArrayMap<>();
        // construct the data (convert second to hour)
        mData.put(context.getString(R.string.day), (float) day / 3600);
        mData.put(context.getString(R.string.night), (float) night / 3600);
    }

    public static ArrayMap<String, Float> getData(Context context) {
        updateData(context);
        return mData;
    }

    public static int getTotalHours(Context context) {
        updateData(context);
        return mTotalHours;
    }
}
