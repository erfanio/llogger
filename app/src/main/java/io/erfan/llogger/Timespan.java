package io.erfan.llogger;

import android.os.Parcel;
import android.os.Parcelable;

public class Timespan implements Parcelable {
    public long start;
    public long end;

    public Timespan(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public Timespan(Parcel in) {
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

    public static final Parcelable.Creator<Timespan> CREATOR = new Parcelable.Creator<Timespan>() {
        public Timespan createFromParcel(Parcel in) {
            return new Timespan(in);
        }

        public Timespan[] newArray(int size) {
            return new Timespan[size];
        }
    };
}