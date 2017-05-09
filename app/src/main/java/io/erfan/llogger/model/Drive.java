package io.erfan.llogger.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.converter.PropertyConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity
public class Drive implements Parcelable {
    @Id(autoincrement = true)
    long mId;
    long mDuration;
    String mLocation;
    String mCar;
    String mSupervisor;
    Date mTime;
    @Convert(converter = LightConverter.class, columnType = String.class)
    Light mLight;
    @Convert(converter = TrafficConverter.class, columnType = String.class)
    Traffic mTraffic;
    @Convert(converter = WeatherConverter.class, columnType = String.class)
    Weather mWeather;

    public enum Light { DAY, NIGHT }
    public enum Traffic { LIGHT, MEDIUM, HEAVY }
    public enum Weather { DRY, WET }

    @Generated(hash = 645993903)
    public Drive(long mId, long mDuration, String mLocation, String mCar, String mSupervisor,
            Date mTime, Light mLight, Traffic mTraffic, Weather mWeather) {
        this.mId = mId;
        this.mDuration = mDuration;
        this.mLocation = mLocation;
        this.mCar = mCar;
        this.mSupervisor = mSupervisor;
        this.mTime = mTime;
        this.mLight = mLight;
        this.mTraffic = mTraffic;
        this.mWeather = mWeather;
    }

    @Generated(hash = 1022087461)
    public Drive() {
    }

    public Drive(Parcel in) {
        mId = in.readLong();
        mDuration = in.readLong();
        mLocation = in.readString();
        mCar = in.readString();
        mSupervisor = in.readString();
        mTime = (Date) in.readSerializable();
        mLight = (Light) in.readSerializable();
        mTraffic = (Traffic) in.readSerializable();
        mWeather = (Weather) in.readSerializable();
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeLong(mDuration);
        dest.writeString(mLocation);
        dest.writeString(mCar);
        dest.writeString(mSupervisor);
        dest.writeSerializable(mTime);
        dest.writeSerializable(mLight);
        dest.writeSerializable(mTraffic);
        dest.writeSerializable(mWeather);
    }

    public String getFormattedDuration() {
        if (mDuration < 3600) {
            return String.format(Locale.ENGLISH, "%dm", mDuration / 60);
        } else if (mDuration % 3600 < 60) {
            return String.format(Locale.ENGLISH, "%dh", mDuration / 3600);
        } else {
            return String.format(Locale.ENGLISH, "%dh %dm", mDuration / 3600, (mDuration % 3600) / 60);
        }
    }

    public String getFormattedTime() {
        // format date into Wed, 2 Jun 12 8:06 pm
        DateFormat df = new SimpleDateFormat("EEE, d MMM yy h:mm a", Locale.ENGLISH);
        return df.format(mTime);
    }

    // getters and setter
    public String getSupervisor() {
        return mSupervisor;
    }

    public void setSupervisor(String supervisor) {
        mSupervisor = supervisor;
    }

    public Light getLight() {
        return mLight;
    }

    public String getLightString() {
        switch (mLight) {
            case DAY:
                return "Day";
            case NIGHT:
                return  "Night";
        }
        return null;
    }

    public void setLight(Light light) {
        mLight = light;
    }

    public Traffic getTraffic() {
        return mTraffic;
    }

    public String getTrafficString() {
        switch (mTraffic) {
            case LIGHT:
                return "Light";
            case MEDIUM:
                return "Medium";
            case HEAVY:
                return "Heavy";
        }
        return null;
    }

    public void setTraffic(Traffic traffic) {
        mTraffic = traffic;
    }

    public Weather getWeather() {
        return mWeather;
    }

    public String getWeatherString() {
        switch (mWeather) {
            case DRY:
                return "Dry";
            case WET:
                return "Wet";
        }
        return null;
    }

    public void setWeather(Weather weather) {
        mWeather = weather;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getCar() {
        return mCar;
    }

    public void setCar(String car) {
        mCar = car;
    }

    public Date getTime() {
        return mTime;
    }

    public void setTime(Date time) {
        mTime = time;
    }

    public long getMId() {
        return this.mId;
    }

    public void setMId(long mId) {
        this.mId = mId;
    }

    public long getMDuration() {
        return this.mDuration;
    }

    public void setMDuration(long mDuration) {
        this.mDuration = mDuration;
    }

    public String getMLocation() {
        return this.mLocation;
    }

    public void setMLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public String getMCar() {
        return this.mCar;
    }

    public void setMCar(String mCar) {
        this.mCar = mCar;
    }

    public String getMSupervisor() {
        return this.mSupervisor;
    }

    public void setMSupervisor(String mSupervisor) {
        this.mSupervisor = mSupervisor;
    }

    public Date getMTime() {
        return this.mTime;
    }

    public void setMTime(Date mTime) {
        this.mTime = mTime;
    }

    public Light getMLight() {
        return this.mLight;
    }

    public void setMLight(Light mLight) {
        this.mLight = mLight;
    }

    public Traffic getMTraffic() {
        return this.mTraffic;
    }

    public void setMTraffic(Traffic mTraffic) {
        this.mTraffic = mTraffic;
    }

    public Weather getMWeather() {
        return this.mWeather;
    }

    public void setMWeather(Weather mWeather) {
        this.mWeather = mWeather;
    }
}
