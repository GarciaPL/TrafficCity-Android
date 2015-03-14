package pl.garciapl.trafficcity;

/**
 * Created by lukasz on 03.12.14.
 */
public class MarkerRoute {

    private Double longitude;
    private Double latitude;
    private String transportType;

    public MarkerRoute() {
    }

    public MarkerRoute(Double longitude, Double latitude, String transportType) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.transportType = transportType;
    }

    public Double getLongitude() {
        return longitude;
    }

    public MarkerRoute setLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public Double getLatitude() {
        return latitude;
    }

    public MarkerRoute setLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public String getTransportType() {
        return transportType;
    }

    public MarkerRoute setTransportType(String transportType) {
        this.transportType = transportType;
        return this;
    }

    @Override
    public String toString() {
        return "MarkerRoute{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", transportType='" + transportType + '\'' +
                '}';
    }
}
