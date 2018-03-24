package project.cmpt276.model.walkingschoolbus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Hawkan Zheng on 3/23/2018.
 */
/*
Lat and lng coordinates for last gps location of a user
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class lastGpsLocation {

    Double lat;
    Double lng;
    String timestamp;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
