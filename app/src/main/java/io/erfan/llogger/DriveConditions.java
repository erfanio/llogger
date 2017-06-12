package io.erfan.llogger;


public class DriveConditions {
    private Long dayStart;
    private Long dayEnd;
    private String light;
    private Boolean wet;

    public Long getDayStart() {
        return dayStart;
    }

    public void setDayStart(Long dayStart) {
        this.dayStart = dayStart;
    }

    public Long getDayEnd() {
        return dayEnd;
    }

    public void setDayEnd(Long dayEnd) {
        this.dayEnd = dayEnd;
    }

    public String getLight() {
        return light;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public Boolean getWet() {
        return wet;
    }

    public void setWet(Boolean wet) {
        this.wet = wet;
    }
}
