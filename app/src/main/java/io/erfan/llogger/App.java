package io.erfan.llogger;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

import io.erfan.llogger.model.DaoMaster;
import io.erfan.llogger.model.DaoSession;


public class App extends Application {

    private DaoSession daoSession;
    public static int LOCATION_PERMISSION_REQUEST_CODE = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "logs-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
