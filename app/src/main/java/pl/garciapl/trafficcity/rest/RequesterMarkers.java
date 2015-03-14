package pl.garciapl.trafficcity.rest;

import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

import pl.garciapl.trafficcity.MarkerRoute;

/**
 * Created by lukasz on 10.12.14.
 */
public class RequesterMarkers {

    private final int PORT = 8080;

    public AsyncResponse sendMarkers(String serverUrl, String login, List<MarkerRoute> markersList) {

        if (serverUrl != null && login != null && markersList.size() > 0) {
            AsyncTask<AsyncParameter, Boolean, AsyncResponse> execute = new HttpTask().execute(new AsyncParameter(serverUrl, login, PORT, markersList, RequestType.SEND_MARKERS));
            try {
                return execute.get();
            } catch (InterruptedException e) {
                return null;
            } catch (ExecutionException e) {
                return null;
            }
        } else {
            return null;
        }

    }

    public AsyncResponse getMarkers(String serverUrl, String login) {

        if (serverUrl != null && login != null) {
            AsyncTask<AsyncParameter, Boolean, AsyncResponse> execute = new HttpTask().execute(new AsyncParameter(serverUrl, login, PORT, null, RequestType.GET_MARKERS));
            try {
                return execute.get();
            } catch (InterruptedException e) {
                return null;
            } catch (ExecutionException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public AsyncResponse clearMarkers(String serverUrl, String login) {

        if (serverUrl != null && login != null) {
            AsyncTask<AsyncParameter, Boolean, AsyncResponse> execute = new HttpTask().execute(new AsyncParameter(serverUrl, login, PORT, null, RequestType.CLEAR_MARKERS));
            try {
                return execute.get();
            } catch (InterruptedException e) {
                return null;
            } catch (ExecutionException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public AsyncResponse test(String serverUrl) {

        if (serverUrl != null) {
            AsyncTask<AsyncParameter, Boolean, AsyncResponse> execute = new HttpTask().execute(new AsyncParameter(serverUrl, null, PORT, null, RequestType.TEST));
            try {
                return execute.get();
            } catch (InterruptedException e) {
                return null;
            } catch (ExecutionException e) {
                return null;
            }
        } else {
            return null;
        }

    }
}
