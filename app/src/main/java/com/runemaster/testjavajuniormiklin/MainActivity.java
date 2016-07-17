package com.runemaster.testjavajuniormiklin;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.HttpsURLConnection;



public class MainActivity extends AppCompatActivity {
    private  ArrayList<Point> resultPoints;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.loadpathbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                try
                {
                    resultPoints = new JSONparse().execute().get();
                    createMap(resultPoints);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                for(Point point: resultPoints)
                {
                    Log.i("point",point.toString());
                }
            }
        });
    }

    private class JSONparse extends AsyncTask<String, Void, ArrayList<Point>>
    {
        final String COORDS_STRING = "coords";
        final String LA = "la";
        final String LO = "lo";
        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;
        ArrayList<Point> resultPoints = new ArrayList<>();



        @Override
        protected ArrayList<Point> doInBackground(String... params) {
            try
            {
                URL url = new URL("https://dl.dropboxusercontent.com/u/5842089/route.txt");
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                StringBuilder buffer = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                reader.close();
                JSONObject jsonObject = new JSONObject(buffer.toString());
                JSONArray coords = jsonObject.getJSONArray(COORDS_STRING);
                for(int i = 0; i < coords.length(); i++)
                {
                    JSONObject jsonPoint = coords.getJSONObject(i);
                    resultPoints.add(new Point(
                            jsonPoint.getDouble(LA),
                            jsonPoint.getDouble(LO)
                    ));
                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }

            return resultPoints;

        }

        @Override
        protected void onPostExecute(ArrayList<Point> points) {
            super.onPostExecute(points);
            button.setEnabled(true);
        }
    }


    private void createMap(final ArrayList<Point> resultPoints)
    {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentmap);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                PolylineOptions polylineOptions = getPolylineOptions(resultPoints);
                polylineOptions.width(5);
                polylineOptions.color(Color.RED);
                googleMap.addPolyline(polylineOptions);
                LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
                List<LatLng> latLngList = polylineOptions.getPoints();
                for (int i = 0; i < latLngList.size(); i++) {
                    latLngBoundsBuilder.include(latLngList.get(i));
                }
                LatLngBounds latLngBounds = latLngBoundsBuilder.build();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
            }
        });
    }

    private PolylineOptions getPolylineOptions(ArrayList<Point> resultPoints)
    {
        PolylineOptions polylineOptions = new PolylineOptions();

        for(Point point : resultPoints)
        {
            polylineOptions.add(new LatLng(point.getX(),point.getY()));
        }

        return polylineOptions;
    }

}


