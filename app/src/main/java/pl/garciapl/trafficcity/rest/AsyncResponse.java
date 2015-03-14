package pl.garciapl.trafficcity.rest;

import java.util.List;

/**
 * Created by lukasz on 14.12.14.
 */
public class AsyncResponse {

    private String response;
    private List<SingleMarker> markers;

    public AsyncResponse() {
    }

    public AsyncResponse(String response) {
        this.response = response;
    }

    public AsyncResponse(String response, List<SingleMarker> markers) {
        this.response = response;
        this.markers = markers;
    }

    public String getResponse() {
        return response;
    }

    public AsyncResponse setResponse(String response) {
        this.response = response;
        return this;
    }

    public List<SingleMarker> getMarkers() {
        return markers;
    }

    public AsyncResponse setMarkers(List<SingleMarker> markers) {
        this.markers = markers;
        return this;
    }

    @Override
    public String toString() {
        return "AsyncResponse{" +
                "response='" + response + '\'' +
                ", markers=" + markers +
                '}';
    }
}
