package io.erfan.llogger.model;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.converter.PropertyConverter;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static io.erfan.llogger.Utils.formatDistance;
import static io.erfan.llogger.Utils.formatDuration;
import org.greenrobot.greendao.DaoException;

import io.erfan.llogger.R;

@Entity(indexes = {
        @Index(value = "time DESC", unique = true)
})
public class Drive implements Parcelable {
    @Id(autoincrement = true)
    private Long id;
    private Date time;
    private Long dayDuration;
    private Long nightDuration;
    private Long distance;
    private String location;
    @Convert(converter = StringListConverter.class, columnType = String.class)
    private List<String> path;
    private Long driverId;
    @ToOne(joinProperty = "driverId")
    private Driver driver;
    private Long carId;
    @ToOne(joinProperty = "carId")
    private Car car;
    private Long supervisorId;
    @ToOne(joinProperty = "supervisorId")
    private Supervisor supervisor;
    @Convert(converter = LightConverter.class, columnType = String.class)
    private Light light;
    @Convert(converter = TrafficConverter.class, columnType = String.class)
    private Traffic traffic;
    @Convert(converter = WeatherConverter.class, columnType = String.class)
    private Weather weather;

    public enum Light { DAY, NIGHT }
    public enum Traffic { LIGHT, MEDIUM, HEAVY }
    public enum Weather { DRY, WET }

    //region format members
    public String getFormattedDuration() {
        return formatDuration(dayDuration + nightDuration);
    }

    public String getFormattedDistance() {
        return formatDistance(distance);
    }

    public String getFormattedTime() {
        // format date into Wed, 2 Jun 12 8:06 pm
        DateFormat df = new SimpleDateFormat("EEE, d MMM yy h:mm a", Locale.ENGLISH);
        return df.format(time);
    }

    public Integer getLightStringRes() {
        switch (light) {
            case DAY:
                return R.string.day;
            case NIGHT:
                return R.string.night;
        }
        return null;
    }

    public Integer getLightEmojiRes() {
        switch (light) {
            case DAY:
                return R.string.emoji_day;
            case NIGHT:
                return R.string.emoji_night;
        }
        return null;
    }

    public Integer getTrafficStringRes() {
        switch (traffic) {
            case LIGHT:
                return R.string.light;
            case MEDIUM:
                return R.string.medium;
            case HEAVY:
                return R.string.heavy;
        }
        return null;
    }

    public Integer getWeatherStringRes() {
        switch (weather) {
            case DRY:
                return R.string.dry;
            case WET:
                return R.string.wet;
        }
        return null;
    }

    public Integer getWeatherEmojiRes() {
        switch (weather) {
            case DRY:
                return R.string.emoji_dry;
            case WET:
                return R.string.emoji_wet;
        }
        return null;
    }
    //endregion

    //region greendao converters
    static class StringListConverter implements PropertyConverter<List<String>, String> {
        @Override
        public List<String> convertToEntityProperty(String databaseValue) {
            Gson gson = new Gson();
            Type stringListType = new TypeToken<List<String>>(){}.getType();
            return gson.fromJson(databaseValue, stringListType);
        }

        @Override
        public String convertToDatabaseValue(List<String> entityProperty) {
            Gson gson = new Gson();
            return gson.toJson(entityProperty);
        }
    }

    static class LightConverter implements PropertyConverter<Light, String> {
        @Override
        public Light convertToEntityProperty(String databaseValue) {
            return Light.valueOf(databaseValue);
        }

        @Override
        public String convertToDatabaseValue(Light entityProperty) {
            return entityProperty.name();
        }
    }

    static class TrafficConverter implements PropertyConverter<Traffic, String> {
        @Override
        public Traffic convertToEntityProperty(String databaseValue) {
            return Traffic.valueOf(databaseValue);
        }

        @Override
        public String convertToDatabaseValue(Traffic entityProperty) {
            return entityProperty.name();
        }
    }

    static class WeatherConverter implements PropertyConverter<Weather, String> {
        @Override
        public Weather convertToEntityProperty(String databaseValue) {
            return Weather.valueOf(databaseValue);
        }

        @Override
        public String convertToDatabaseValue(Weather entityProperty) {
            return entityProperty.name();
        }
    }
    //endregion

    //region parcelable stuff
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Drive> CREATOR = new Creator<Drive>() {
        @Override
        public Drive createFromParcel(Parcel in) {
            return new Drive(in);
        }

        @Override
        public Drive[] newArray(int size) {
            return new Drive[size];
        }
    };

    private Drive(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.time = (Date) in.readSerializable();
        this.dayDuration = (Long) in.readValue(Long.class.getClassLoader());
        this.nightDuration = (Long) in.readValue(Long.class.getClassLoader());
        this.distance = (Long) in.readValue(Long.class.getClassLoader());
        this.location = (String) in.readValue(String.class.getClassLoader());
        this.path = new ArrayList<>();
        in.readList(path, String.class.getClassLoader());
        this.driverId = (Long) in.readValue(Long.class.getClassLoader());
        this.carId = (Long) in.readValue(Long.class.getClassLoader());
        this.supervisorId = (Long) in.readValue(Long.class.getClassLoader());
        this.light = (Light) in.readSerializable();
        this.traffic = (Traffic) in.readSerializable();
        this.weather = (Weather) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeSerializable(time);
        dest.writeValue(dayDuration);
        dest.writeValue(nightDuration);
        dest.writeValue(distance);
        dest.writeValue(location);
        dest.writeList(path);
        dest.writeValue(driverId);
        dest.writeValue(carId);
        dest.writeValue(supervisorId);
        dest.writeSerializable(light);
        dest.writeSerializable(traffic);
        dest.writeSerializable(weather);
    }
    //endregion

    //region greendao stuff


    @Generated(hash = 650349961)
    public Drive(Long id, Date time, Long dayDuration, Long nightDuration, Long distance, String location, List<String> path, Long driverId, Long carId, Long supervisorId,
                 Light light, Traffic traffic, Weather weather) {
        this.id = id;
        this.time = time;
        this.dayDuration = dayDuration;
        this.nightDuration = nightDuration;
        this.distance = distance;
        this.location = location;
        this.path = path;
        this.driverId = driverId;
        this.carId = carId;
        this.supervisorId = supervisorId;
        this.light = light;
        this.traffic = traffic;
        this.weather = weather;
    }

    @Generated(hash = 1022087461)
    public Drive() {
    }

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 36176704)
    private transient DriveDao myDao;
    @Generated(hash = 655189286)
    private transient Long driver__resolvedKey;
    @Generated(hash = 440805916)
    private transient Long car__resolvedKey;
    @Generated(hash = 1350296901)
    private transient Long supervisor__resolvedKey;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDayDuration() {
        return this.dayDuration;
    }

    public void setDayDuration(Long dayDuration) {
        this.dayDuration = dayDuration;
    }

    public Long getDistance() {
        return this.distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getPath() {
        return this.path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Light getLight() {
        return this.light;
    }

    public void setLight(Light light) {
        this.light = light;
    }

    public Traffic getTraffic() {
        return this.traffic;
    }

    public void setTraffic(Traffic traffic) {
        this.traffic = traffic;
    }

    public Weather getWeather() {
        return this.weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public Long getNightDuration() {
        return this.nightDuration;
    }

    public void setNightDuration(Long nightDuration) {
        this.nightDuration = nightDuration;
    }

    public Long getDriverId() {
        return this.driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getCarId() {
        return this.carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Long getSupervisorId() {
        return this.supervisorId;
    }

    public void setSupervisorId(Long supervisorId) {
        this.supervisorId = supervisorId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1022113171)
    public Driver getDriver() {
        Long __key = this.driverId;
        if (driver__resolvedKey == null || !driver__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DriverDao targetDao = daoSession.getDriverDao();
            Driver driverNew = targetDao.load(__key);
            synchronized (this) {
                driver = driverNew;
                driver__resolvedKey = __key;
            }
        }
        return driver;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1644720982)
    public void setDriver(Driver driver) {
        synchronized (this) {
            this.driver = driver;
            driverId = driver == null ? null : driver.getId();
            driver__resolvedKey = driverId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 2145005461)
    public Car getCar() {
        Long __key = this.carId;
        if (car__resolvedKey == null || !car__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CarDao targetDao = daoSession.getCarDao();
            Car carNew = targetDao.load(__key);
            synchronized (this) {
                car = carNew;
                car__resolvedKey = __key;
            }
        }
        return car;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1613335416)
    public void setCar(Car car) {
        synchronized (this) {
            this.car = car;
            carId = car == null ? null : car.getId();
            car__resolvedKey = carId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 643927743)
    public Supervisor getSupervisor() {
        Long __key = this.supervisorId;
        if (supervisor__resolvedKey == null || !supervisor__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SupervisorDao targetDao = daoSession.getSupervisorDao();
            Supervisor supervisorNew = targetDao.load(__key);
            synchronized (this) {
                supervisor = supervisorNew;
                supervisor__resolvedKey = __key;
            }
        }
        return supervisor;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1492433334)
    public void setSupervisor(Supervisor supervisor) {
        synchronized (this) {
            this.supervisor = supervisor;
            supervisorId = supervisor == null ? null : supervisor.getId();
            supervisor__resolvedKey = supervisorId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 274871058)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDriveDao() : null;
    }

    //endregion
}
