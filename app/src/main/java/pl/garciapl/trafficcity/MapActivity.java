package pl.garciapl.trafficcity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.garciapl.trafficcity.rest.AsyncResponse;
import pl.garciapl.trafficcity.rest.RequesterMarkers;
import pl.garciapl.trafficcity.rest.SingleMarker;

public class MapActivity extends FragmentActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap map;
    private static final LatLng WARSAW = new LatLng(52.237049, 21.017532);
    private static final int WARSAW_ZOOM = 12;
    private List<MarkerRoute> markers;
    boolean[] layers = new boolean[12];
    private SharedPreferences preferences;

    private Pattern pattern;
    private Matcher matcher;

    private final String APP_NAME = "TrafficCity";

    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (!checkInternetConnection()) {
            showNoInternetConnectionDialog();
        }

        setUpMapIfNeeded();

        markers = new ArrayList<MarkerRoute>();
        for (int i = 0; i < layers.length; i++) {
            layers[i] = false;
        }

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                    | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
        }
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
        }

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        pattern = Pattern.compile(IPADDRESS_PATTERN);

        try {
            AsyncResponse markersServer = new RequesterMarkers().getMarkers(preferences.getString("BS", null), preferences.getString("BL", null));
            if (markersServer != null) {
                for (SingleMarker singleMarker : markersServer.getMarkers()) {
                    addMarker(new LatLng(singleMarker.getLatitude(), singleMarker.getLongitude()), singleMarker.getTransportType()); //tutaj w latlng moze byc na odwrot
                }
            } else {
                errorGettingMarkers();
            }
        } catch (Exception e) {
            errorGettingMarkers();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #map} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(android.os.Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                setUpMap();
            } else {
                errorDialog();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #map} is not null.
     */
    private void setUpMap() {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(WARSAW, WARSAW_ZOOM));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        map.setOnMapClickListener(this);
        map.setOnMapLongClickListener(this);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.setOnInfoWindowClickListener(this);
    }

    private void errorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage(R.string.error_dialog).setNegativeButton(R.string.error_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.create();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {

        final CharSequence transportTypes[] = new CharSequence[]{getResources().getString(R.string.transport_dialog_car), getResources().getString(R.string.transport_dialog_bus)};
        if (markers.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.transport_dialog_label);
            builder.setItems(transportTypes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addMarker(latLng, transportTypes[which]);
                }
            });
            builder.show();
        } else {
            addMarker(latLng, (CharSequence) new String(markers.get(0).getTransportType()));
        }

    }

    private void addMarker(final LatLng latLng, CharSequence transportType) {
        MarkerOptions markerOption = new MarkerOptions().position(latLng);

        markerOption.title((markers.size() == 0) ? "START" : "Punkt : " + Integer.toString(markers.size()));
        markerOption.snippet(latLng.toString());

        if (transportType.equals(getResources().getString(R.string.transport_dialog_car))) {
            markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
        } else {
            markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus));
        }

        Marker marker = map.addMarker(markerOption);
        markers.add(new MarkerRoute().setLatitude(latLng.latitude).setLongitude(latLng.longitude).
                setTransportType(transportType.toString()));

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View view = getLayoutInflater().inflate(R.layout.marker_window, null);
                TextView pointView = (TextView) view.findViewById(R.id.marker_label);
                TextView latitudeView = (TextView) view.findViewById(R.id.marker_latitude);
                TextView longitudeView = (TextView) view.findViewById(R.id.marker_longitude);

                DecimalFormat df = new DecimalFormat("#.####");
                String markerNr = (marker.getTitle().contains("START")) ? "START" : marker.getTitle();
                String formatPoint = "<b>" + markerNr + "</b>";
                String formatLatitude = "<b>Latitude:</b> " + df.format(latLng.latitude);
                String formatLongitude = "<b>Longitude:</b> " + df.format(latLng.longitude);

                pointView.setText(Html.fromHtml(formatPoint));
                latitudeView.setText(Html.fromHtml(formatLatitude));
                longitudeView.setText(Html.fromHtml(formatLongitude));

                return view;
            }
        });

        marker.showInfoWindow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_buttons_release, menu);
        return Boolean.TRUE;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_clear_map):
                clearMap();
                break;
            case (R.id.action_settings):
                showSettingsDialog();
                break;
            case (R.id.action_about):
                showAboutDialog();
                break;
            case (R.id.action_layers):
                showCustomLayersDialog();
                break;
            case (R.id.action_send_markers):
                showSendMarkersDialog();
                break;
            case (R.id.action_draw_route):
                showCalculateRouteDialog();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showCalculateRouteDialog() {
        if (markers.size() > 0) {
            PolylineOptions geodesic = new PolylineOptions().geodesic(true).color(R.color.blue).width(4);
            for (MarkerRoute markerRoute : markers) {
                geodesic.add(new LatLng(markerRoute.getLatitude(), markerRoute.getLongitude()));
            }
            map.addPolyline(geodesic);
        } else {
            AlertDialog.Builder alertConn = new AlertDialog.Builder(MapActivity.this);
            alertConn.setMessage(R.string.options_button_draw_route_failure);
            AlertDialog alertConnDialog = alertConn.create();
            alertConnDialog.show();
        }
    }

    private void saveSettingsDialog() {
        Toast toast = Toast.makeText(getApplicationContext(), R.string.settings_saved, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    private void errorGettingMarkers() {
        Toast toast = Toast.makeText(getApplicationContext(), R.string.send_markers_dialog_server_failure, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    private void showSettingsDialog() {
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.settings_dialog, null);

        final EditText server_address = (EditText) promptsView
                .findViewById(R.id.settings_http);
        server_address.setText(preferences.getString("BS", "-"));

        final EditText bihapi_login = (EditText) promptsView
                .findViewById(R.id.settings_bihapi_login);
        bihapi_login.setText(preferences.getString("BL", "-"));

        final EditText bihapi_password = (EditText) promptsView
                .findViewById(R.id.settings_bihapi_password);
        bihapi_password.setText(preferences.getString("BP", "-"));

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.settings_button);
        alert.setView(promptsView);
        alert.setNeutralButton(R.string.settings_test, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String message;
                boolean validate = validateIPAddress(preferences.getString("BS", "-"));
                if (validate) {
                    RequesterMarkers requesterMarkers = new RequesterMarkers();
                    AsyncResponse response = requesterMarkers.test(preferences.getString("BS", null));
                    if (response == null) {
                        message = getResources().getString(R.string.settings_test_not_reachable);
                    } else if (response.getResponse().contains(APP_NAME)) {
                        message = getResources().getString(R.string.settings_test_reachable);
                    } else {
                        message = getResources().getString(R.string.settings_test_not_reachable);
                    }
                } else {
                    message = getResources().getString(R.string.settings_wrong_ip_address);
                }

                AlertDialog.Builder alertConn = new AlertDialog.Builder(MapActivity.this);
                alertConn.setMessage(message);
                AlertDialog alertConnDialog = alertConn.create();
                alertConnDialog.show();

            }
        }).setPositiveButton(R.string.settings_button_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("BL", bihapi_login.getText().toString());
                editor.putString("BP", bihapi_password.getText().toString());
                if (validateIPAddress(server_address.getText().toString()) && bihapi_login.getText().length() != 0) {
                    editor.putString("BS", server_address.getText().toString());
                    editor.apply();
                    saveSettingsDialog();
                } else {
                    AlertDialog.Builder alertIPAddress = new AlertDialog.Builder(MapActivity.this);
                    alertIPAddress.setMessage(R.string.settings_wrong_ip_address_discard);
                    AlertDialog alertIPAddressDialog = alertIPAddress.create();
                    alertIPAddressDialog.show();
                }
            }
        }).setNegativeButton(R.string.error_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private void showSendMarkersDialog() {

        if (markers.size() > 0) {
            AsyncResponse response = new RequesterMarkers().sendMarkers(preferences.getString("BS", null), preferences.getString("BL", null), markers);
            if (response == null) {
                AlertDialog.Builder alertConn = new AlertDialog.Builder(MapActivity.this);
                alertConn.setMessage(R.string.send_markers_dialog_failure);
                AlertDialog alertConnDialog = alertConn.create();
                alertConnDialog.show();
            } else {
                AlertDialog.Builder alertConn = new AlertDialog.Builder(MapActivity.this);
                alertConn.setMessage(R.string.send_markers_dialog_success);
                AlertDialog alertConnDialog = alertConn.create();
                alertConnDialog.show();
            }
        } else {
            AlertDialog.Builder alertConn = new AlertDialog.Builder(MapActivity.this);
            alertConn.setMessage(R.string.send_markers_dialog_message);
            AlertDialog alertConnDialog = alertConn.create();
            alertConnDialog.show();
        }
    }

    private void showCustomLayersDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.custom_layers_button).setMultiChoiceItems(R.array.custom_layers,
                this.layers,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        if (isChecked) {
                            layers[which] = Boolean.TRUE;
                        } else {
                            layers[which] = Boolean.FALSE;
                        }
                    }
                })
                .setPositiveButton(R.string.settings_button_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(R.string.error_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();

    }

    private void showAboutDialog() {
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.about_dialog, null);

        final ImageButton mailButton = (ImageButton) promptsView
                .findViewById(R.id.about_mail);
        mailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.about_mail)});
                email.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                email.putExtra(Intent.EXTRA_TEXT, "Text");
                startActivity(Intent.createChooser(email, "Send Email"));
            }
        });

        final ImageButton linkedInButton = (ImageButton) promptsView
                .findViewById(R.id.about_linkedin);
        linkedInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(getResources().getString(R.string.about_linked_in));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.about_button);
        alert.setView(promptsView);

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private void clearMap() {
        this.map.clear();
        markers.clear();
        try {
            new RequesterMarkers().clearMarkers(preferences.getString("BS", null), preferences.getString("BL", null));
        } catch (Exception ignored) {

        }
    }

    private boolean checkInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void showNoInternetConnectionDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.internet_connection_title)
                .setMessage(R.string.internet_connection_restart)
                .setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage(getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                })
                .setNegativeButton(R.string.confirm_exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MapActivity.this.finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
    }

    private boolean validateIPAddress(final String ip) {
        matcher = pattern.matcher(ip);
        return matcher.matches();
    }
}
