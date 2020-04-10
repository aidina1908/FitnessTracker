package com.example.android.fitnesstracker.direction;

import android.graphics.Color;
import android.os.AsyncTask;

import com.example.android.fitnesstracker.direction.DataParser;
import com.example.android.fitnesstracker.direction.DownloadURL;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;

public class GetDirections extends AsyncTask<Object,String,String> {

    GoogleMap googleMap;
    String url;
    String googledirections;
    String distance,duration;
    LatLng latLng;

    @Override
    protected String doInBackground(Object... objects) {
        googleMap = (GoogleMap)objects[0];
        url = (String)objects[1];
        latLng = (LatLng)objects[2];


        DownloadURL downloadUrl = new DownloadURL();
        try{
            googledirections = downloadUrl.readUrl(url);
        } catch (IOException e){
            e.printStackTrace();
        }
        return googledirections;
    }

    @Override
    protected void onPostExecute(String s){
        HashMap<String,String> directionList = null;
       // String [] directionList;
        DataParser parser = new DataParser();
        directionList = parser.parseDirections(s);
        //displayDirection(directionList);
        //directionList = parser.parseDirections(s);
        duration = directionList.get("duration");
        distance = directionList.get("distance");

        googleMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(true);
        markerOptions.title("Duration ="+duration);
        markerOptions.snippet("Distance = "+distance);

        googleMap.addMarker(markerOptions);
    }

    public void displayDirection(String[] directionList){
        int count = directionList.length;
        for(int i = 0; i < count; i++){
            PolylineOptions options = new PolylineOptions();
            options.color(Color.BLUE);
            options.width(10);
            options.addAll(PolyUtil.decode(directionList[i]));

            googleMap.addPolyline(options);
        }
    }
}
