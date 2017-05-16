package io.erfan.llogger.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.converter.PropertyConverter;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static io.erfan.llogger.Utils.formatDuration;

@Entity(indexes = {
        @Index(value = "time DESC", unique = true)
})
public class Drive implements Parcelable {
    @Id(autoincrement = true)
    public Long id;
    public Long duration;
    public Long distance;
    public String location;
    @Convert(converter = StringListConverter.class, columnType = String.class)
    public List<String> path;
    public String car;
    public String supervisor;
    public Date time;
    @Convert(converter = LightConverter.class, columnType = String.class)
    public Light light;
    @Convert(converter = TrafficConverter.class, columnType = String.class)
    public Traffic traffic;
    @Convert(converter = WeatherConverter.class, columnType = String.class)
    public Weather weather;

    public enum Light { DAY, NIGHT }
    public enum Traffic { LIGHT, MEDIUM, HEAVY }
    public enum Weather { DRY, WET }

    @Generated(hash = 1376662561)
    public Drive(Long id, Long duration, Long distance, String location, List<String> path, String car, String supervisor, Date time, Light light, Traffic traffic,
            Weather weather) {
        this.id = id;
        this.duration = duration;
        this.distance = distance;
        this.location = location;
        this.path = path;
        this.car = car;
        this.supervisor = supervisor;
        this.time = time;
        this.light = light;
        this.traffic = traffic;
        this.weather = weather;
    }

    @Generated(hash = 1022087461)
    public Drive() {
    }

    public Drive(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.duration = (Long) in.readValue(Long.class.getClassLoader());
        this.distance = (Long) in.readValue(Long.class.getClassLoader());
        this.location = (String) in.readValue(String.class.getClassLoader());
        this.path = new ArrayList<>();
        in.readList(this.path, List.class.getClassLoader());
        this.car = (String) in.readValue(String.class.getClassLoader());
        this.supervisor = (String) in.readValue(String.class.getClassLoader());
        this.time = (Date) in.readSerializable();
        this.light = (Light) in.readSerializable();
        this.traffic = (Traffic) in.readSerializable();
        this.weather = (Weather) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(duration);
        dest.writeValue(distance);
        dest.writeValue(location);
        dest.writeList(path);
        dest.writeValue(car);
        dest.writeValue(supervisor);
        dest.writeSerializable(time);
        dest.writeSerializable(light);
        dest.writeSerializable(traffic);
        dest.writeSerializable(weather);
    }

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

    // format members
    public String getFormattedDuration() {
        return formatDuration(duration);
    }

    public String getFormattedTime() {
        // format date into Wed, 2 Jun 12 8:06 pm
        DateFormat df = new SimpleDateFormat("EEE, d MMM yy h:mm a", Locale.ENGLISH);
        return df.format(time);
    }

    public String getLightString() {
        switch (light) {
            case DAY:
                return "Day";
            case NIGHT:
                return  "Night";
        }
        return null;
    }

    public String getTrafficString() {
        switch (traffic) {
            case LIGHT:
                return "Light";
            case MEDIUM:
                return "Medium";
            case HEAVY:
                return "Heavy";
        }
        return null;
    }

    public String getWeatherString() {
        switch (weather) {
            case DRY:
                return "Dry";
            case WET:
                return "Wet";
        }
        return null;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDuration() {
        return this.duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
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

    public String getCar() {
        return this.car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getSupervisor() {
        return this.supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
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
}
