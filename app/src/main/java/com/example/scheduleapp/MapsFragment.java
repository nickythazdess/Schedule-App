package com.example.scheduleapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private String nextActivity;
    private String info;
    private String place;
    private LatLng nextActivityDest;
    private String destName;

    ListDate listDate = ListDate.getInstance();

    private GoogleMap mMap;
    private LatLng curLocation;
    private String curLocationName;

    HashMap<String, Marker> hashMapMarker = new HashMap<>();
    private String sourceMarkerKey = "S";
    private String destMarkerKey = "D";

    private ViewGroup myViewGroup;
    private Context myContext;

    private Polyline newPolyline;
    private LatLngBounds latlngBounds;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.myViewGroup = container;
        this.myContext = container.getContext();
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        loadData();
        loadSearchBar();
        if (mMap != null) {
            if (newPolyline != null) newPolyline.remove();
            Marker marker = hashMapMarker.get(destMarkerKey);
            if (marker != null) {
                marker.remove();
                hashMapMarker.remove(destMarkerKey);
            }
            getCurrentLocation();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void loadSearchBar() {
        Places.initialize(myContext, getString(R.string.google_api_key));
        PlacesClient placesClient = Places.createClient(myContext);

        AutocompleteSupportFragment autocompleteFragmentSource = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragmentsource);

        autocompleteFragmentSource.setCountries("VN");
        autocompleteFragmentSource.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.ID, Place.Field.NAME));

        autocompleteFragmentSource.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                curLocationName = place.getName();
                curLocation = place.getLatLng();
                if (curLocation != null) {
                    setCurrentMarker();
                    if (nextActivityDest != null) drawRoute();
                    else setCamera(curLocation);
                } else getCurrentLocation();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });

        AutocompleteSupportFragment autocompleteFragmentDest = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragmentdest);


        autocompleteFragmentDest.setCountries("VN");
        autocompleteFragmentDest.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.ID, Place.Field.NAME));

        autocompleteFragmentDest.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                destName = place.getName();
                nextActivityDest = place.getLatLng();
                if (nextActivityDest != null) {
                    drawRoute();
                    setDestMarker();
                } else {
                    loadData();
                    setCamera(curLocation);
                }

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });
    }

    private void setCamera(LatLng location) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location)      // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (nextActivityDest != null) setDestMarker();
        getCurrentLocation();
    }

    private void setCurrentMarker() {
        Marker marker = hashMapMarker.get(sourceMarkerKey);
        if (marker != null) {
            marker.remove();
            hashMapMarker.remove(sourceMarkerKey);
        }

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.target);
        bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth()/4, bmp.getHeight()/4, false);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bmp);
        Marker mMarker = mMap.addMarker(new MarkerOptions()
                        .position(curLocation)
                        .title("Current Location")
                        .snippet("You're here")
                        .icon(bitmapDescriptor)
                        .anchor((float) 0.5, (float) 0.5)
                //.rotation()
        );
        hashMapMarker.put(sourceMarkerKey, mMarker);
    }

    private void setDestMarker() {
        Marker marker = hashMapMarker.get(destMarkerKey);
        if (marker != null) {
            marker.remove();
            hashMapMarker.remove(destMarkerKey);
        }

        Marker mMarker = mMap.addMarker(new MarkerOptions()
                        .position(nextActivityDest)
                        .title(destName)
                        .snippet(nextActivity + ": " + info)
                //.icon(bitmapDescriptor)
        );
        hashMapMarker.put(destMarkerKey, mMarker);
    }

    private void getCurrentLocation()
    {
        if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        FusedLocationProviderClient mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(myContext);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener((android.app.Activity) myContext, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) return;
                        curLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        if (nextActivityDest != null) {
                            drawRoute();
                            setDestMarker();
                        }
                        else setCamera(curLocation);
                        setCurrentMarker();
                    }
                });
    }

    public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
        PolylineOptions rectLine = new PolylineOptions().width(5).color(
                Color.RED);

        for (int i = 0; i < directionPoints.size(); i++) {
            rectLine.add(directionPoints.get(i));
        }
        if (newPolyline != null) newPolyline.remove();
        newPolyline = mMap.addPolyline(rectLine);
        latlngBounds = createLatLngBoundsObject(curLocation, nextActivityDest);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds, 150));
    }
    private LatLngBounds createLatLngBoundsObject(LatLng firstLocation,
                                                  LatLng secondLocation) {
        if (firstLocation != null && secondLocation != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(firstLocation).include(secondLocation);
            return builder.build();
        }
        return null;
    }

    public void findDirections(double fromPositionDoubleLat,
                               double fromPositionDoubleLong, double toPositionDoubleLat,
                               double toPositionDoubleLong, String mode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT,
                String.valueOf(fromPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG,
                String.valueOf(fromPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DESTINATION_LAT,
                String.valueOf(toPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.DESTINATION_LONG,
                String.valueOf(toPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

        GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
        asyncTask.execute(map);
        // asyncTask.cancel(true);
    }

    public void drawRoute() {
        final GMapV2Direction md = new GMapV2Direction();
        //OnGetDataCompleteListener completeListener = null;
        findDirections(curLocation.latitude, curLocation.longitude,
                nextActivityDest.latitude, nextActivityDest.longitude,
                GMapV2Direction.MODE_DRIVING);
        //mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds, 150));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadData() {
        Activity yourNextActivity = listDate.getNextActivity();
        if (yourNextActivity != null) {
            nextActivity = yourNextActivity.get_title();
            info = yourNextActivity.get_info();
            place = yourNextActivity.get_place();
            destName = yourNextActivity.get_GGplace();
            if (!destName.equals("")) {
                yourNextActivity.set_location(new LatLng(Double.valueOf(yourNextActivity.get_lati()), Double.valueOf(yourNextActivity.get_longi())));
                nextActivityDest = yourNextActivity.get_location();
            } else nextActivityDest = null;
        } else {
            nextActivity = null;
            info = null;
            place = null;
            destName = null;
            nextActivityDest = null;
        }
    }
}