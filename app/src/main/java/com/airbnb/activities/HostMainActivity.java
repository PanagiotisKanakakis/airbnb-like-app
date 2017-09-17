package com.airbnb.activities;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;

import com.airbnb.shared.dto.entity.Residence;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sourcey.activities.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by panagiotis on 16/9/2017.
 */

public class HostMainActivity extends FragmentActivity implements OnMapReadyCallback {

    private CheckedTextView _entire_place;
    private CheckedTextView _private_room;
    private CheckedTextView _shared_room;
    private CheckedTextView _shared_bath;
    private CheckedTextView _private_bath;
    private Button _first_btn;
    private Button _scd_btn;
    private Button _thr_btn;
    private Button _ft_btn;
    private GoogleMap mMap;
    private Residence residence = new Residence();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing_first_step);

        _entire_place = (CheckedTextView) findViewById(R.id.entire_place);
        _private_room = (CheckedTextView) findViewById(R.id.private_room);
        _shared_room = (CheckedTextView) findViewById(R.id.shared_room);

        _entire_place.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
                _private_room.setChecked(false);
                _shared_room.setChecked(false);
            }
        });

        _private_room.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
                _entire_place.setChecked(false);
                _shared_room.setChecked(false);
            }
        });

        _shared_room.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
                _private_room.setChecked(false);
                _entire_place.setChecked(false);
            }
        });

        _first_btn = (Button) findViewById(R.id.btn_first_next);
        _first_btn.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener =  new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            if (view.getId() == _first_btn.getId()){
                firstStepActions();
            }else if(view.getId() == _scd_btn.getId()){
                secondStepActions();
            }else if(view.getId() == _thr_btn.getId()){
                thirdStepActions();
            }else{
                fourthStepActions();
            }
        }
    };

    private void fourthStepActions() {
        residence.setAddress(((EditText)findViewById(R.id.street)).getText().toString());

        setContentView(R.layout.listing_fifth_step);
        MapView mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(null);
        mapView.getMapAsync(this);
    }

    private void thirdStepActions() {
        residence.setBathrooms(Integer.valueOf(((EditText)findViewById(R.id.bathrooms_for_guests)).getText().toString()));

        setContentView(R.layout.listing_fourth_step);
        _ft_btn = (Button) findViewById(R.id.btn_ft_next);
        _ft_btn.setOnClickListener(onClickListener);
    }

    private void secondStepActions() {
        
        residence.setCapacity(Integer.valueOf(((EditText)findViewById(R.id.guests)).getText().toString()));
        residence.setBedrooms(Integer.valueOf(((EditText)findViewById(R.id.bedrooms_for_guests)).getText().toString()));
        //residence.setBeds

        setContentView(R.layout.listing_third_step);
        _shared_bath = (CheckedTextView) findViewById(R.id.shared_bathroom);
        _private_bath = (CheckedTextView) findViewById(R.id.private_bathroom);
        _shared_bath.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
                _private_bath.setChecked(false);
            }
        });
        _private_bath.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
                _shared_bath.setChecked(false);
            }
        });
        _thr_btn = (Button) findViewById(R.id.btn_thr_next);
        _thr_btn.setOnClickListener(onClickListener);
    }

    private void firstStepActions() {

        if(_shared_room.isChecked())
            residence.setType("Shared room");
        else if(_private_room.isChecked())
            residence.setType("Private room");
        else
            residence.setType("Entire place");

        setContentView(R.layout.listing_second_step);
        _scd_btn = (Button) findViewById(R.id.btn_scd_next);
        _scd_btn.setOnClickListener(onClickListener);
    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        new DataLongOperationAsynchTask().execute(residence.getAddress());
    }

    private class DataLongOperationAsynchTask extends AsyncTask<String, Void, String[]> {
        ProgressDialog dialog = new ProgressDialog(HostMainActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            String response;
            try {
                response = getLatLongByURL("http://maps.google.com/maps/api/geocode/json?address="+params[0]+"&sensor=false");
                Log.d("response",""+response);
                return new String[]{response};
            } catch (Exception e) {
                return new String[]{"error"};
            }
        }

        @Override
        protected void onPostExecute(String... result) {
            try {
                JSONObject jsonObject = new JSONObject(result[0]);

                double lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");

                Log.d("latitude", "" + lat);
                Log.d("longitude", "" + lng);
                LatLng sydney = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }


    public String getLatLongByURL(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
