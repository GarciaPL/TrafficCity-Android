package pl.garciapl.trafficcity.rest;

/**
 * Created by lukasz on 14.12.14.
 */
public class SingleMarker {

    private Double longitude;
    private Double latitude;
    private String transportType;

    public SingleMarker(Double longitude, Double latitude, String transportType) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.transportType = transportType;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    @Override
    public String toString() {
        return "SingleMarker{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", transportType='" + transportType + '\'' +
                '}';
    }
}
