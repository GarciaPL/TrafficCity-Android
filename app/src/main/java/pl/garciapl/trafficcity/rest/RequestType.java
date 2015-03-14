package pl.garciapl.trafficcity.rest;

/**
 * Created by lukasz on 14.12.14.
 */
public enum RequestType {
    TEST("test"), GET_MARKERS("getMarkers"), SEND_MARKERS("sendMarkers"), CLEAR_MARKERS("clearMarkers");

    private String requestType;

    RequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestType() {
        return requestType;
    }
}
