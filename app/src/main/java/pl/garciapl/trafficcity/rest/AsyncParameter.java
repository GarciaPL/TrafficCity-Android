package pl.garciapl.trafficcity.rest;

import java.util.List;

import pl.garciapl.trafficcity.MarkerRoute;

/**
 * Created by lukasz on 14.12.14.
 */
public class AsyncParameter {

    private String serverUrl;
    private String login;
    private int port;
    private List<MarkerRoute> markersList;
    private RequestType requestType;

    public AsyncParameter(String serverUrl, String login, int port, List<MarkerRoute> markersList, RequestType requestType) {
        this.serverUrl = serverUrl;
        this.login = login;
        this.port = port;
        this.markersList = markersList;
        this.requestType = requestType;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<MarkerRoute> getMarkersList() {
        return markersList;
    }

    public void setMarkersList(List<MarkerRoute> markersList) {
        this.markersList = markersList;
    }

    @Override
    public String toString() {
        return "AsyncParameter{" +
                "serverUrl='" + serverUrl + '\'' +
                ", login='" + login + '\'' +
                ", port=" + port +
                ", markersList=" + markersList +
                ", requestType=" + requestType +
                '}';
    }
}
