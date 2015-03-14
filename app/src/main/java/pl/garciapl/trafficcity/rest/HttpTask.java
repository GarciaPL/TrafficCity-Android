package pl.garciapl.trafficcity.rest;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by lukasz on 12.12.14.
 */
public final class HttpTask
        extends
        AsyncTask<AsyncParameter/* Param */, Boolean /* Progress */, AsyncResponse /* Result */> {

    private HttpClient httpClient = new DefaultHttpClient();
    private Gson gson = new Gson();

    private final int TIMEOUT = 5000;
    private final String GET_MARKERS_URL = "/trafficcity/mongoapi/getusermarkers/";
    private final String SEND_MARKERS_URL = "/trafficcity/mongoapi/addusermarkers/";
    private final String CLEAR_MARKERS_URL = "/trafficcity/mongoapi/clearusermarkers/";
    private final String TEST_URL = "/trafficcity/home/simple";

    @Override
    protected void onProgressUpdate(Boolean... progress) {
    }

    @Override
    protected AsyncResponse doInBackground(AsyncParameter... asyncParameters) {

        AsyncParameter asyncParameter = asyncParameters[0];

        if (!Reachable.checkConnection(asyncParameter.getServerUrl())) {
            return null;
        }

        try {
            String finalUrl;
            if (asyncParameter.getRequestType().getRequestType().equals(RequestType.TEST.getRequestType())) {
                finalUrl = "http://" + asyncParameter.getServerUrl() + ":" + asyncParameter.getPort() + TEST_URL;
            } else if (asyncParameter.getRequestType().getRequestType().equals(RequestType.GET_MARKERS.getRequestType())) {
                finalUrl = "http://" + asyncParameter.getServerUrl() + ":" + asyncParameter.getPort() + GET_MARKERS_URL + asyncParameter.getLogin();
            } else if (asyncParameter.getRequestType().getRequestType().equals(RequestType.SEND_MARKERS.getRequestType())) {
                finalUrl = "http://" + asyncParameter.getServerUrl() + ":" + asyncParameter.getPort() + SEND_MARKERS_URL + asyncParameter.getLogin();
            } else if (asyncParameter.getRequestType().getRequestType().equals(RequestType.CLEAR_MARKERS.getRequestType())) {
                finalUrl = "http://" + asyncParameter.getServerUrl() + ":" + asyncParameter.getPort() + CLEAR_MARKERS_URL + asyncParameter.getLogin();
            } else {
                return null;
            }

            HttpParams httpParams = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT);

            String markersListPOST;
            StringEntity stringEntity;
            HttpPost httpPost = null;
            if (asyncParameter.getMarkersList() != null) {
                markersListPOST = gson.toJson(asyncParameter.getMarkersList());
                stringEntity = new StringEntity(markersListPOST);
                stringEntity.setContentType("application/json");
                httpPost = new HttpPost(finalUrl);
                httpPost.setEntity(stringEntity);
            }

            HttpResponse execute = httpClient.execute((asyncParameter.getRequestType().getRequestType().equals(RequestType.SEND_MARKERS.getRequestType())) ? httpPost : new HttpGet(finalUrl));
            StatusLine statusLine = execute.getStatusLine();

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                execute.getEntity().writeTo(out);
                out.close();
                String responseString = out.toString();

                if (asyncParameter.getRequestType().getRequestType().equals(RequestType.GET_MARKERS.getRequestType())) {
                    List<SingleMarker> markerList = gson.fromJson(responseString, new TypeToken<List<SingleMarker>>() {
                    }.getType());
                    return new AsyncResponse("", markerList);
                } else if (asyncParameter.getRequestType().getRequestType().equals(RequestType.SEND_MARKERS.getRequestType())) {
                    return new AsyncResponse(responseString);
                } else if (asyncParameter.getRequestType().getRequestType().equals(RequestType.CLEAR_MARKERS.getRequestType())) {
                    return new AsyncResponse(responseString);
                } else if (asyncParameter.getRequestType().getRequestType().equals(RequestType.TEST.getRequestType())) {
                    return new AsyncResponse(responseString);
                } else {
                    return null;
                }
            } else {
                execute.getEntity().getContent().close();
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

}