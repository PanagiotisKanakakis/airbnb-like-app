package com.airbnb.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.Utils.Util;
import com.airbnb.rest.RestApi;
import com.airbnb.shared.dto.entity.Residence;
import com.airbnb.shared.dto.entity.User;
import com.airbnb.shared.dto.residence.AddResidenceRequestDto;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.sourcey.activities.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.R.attr.bitmap;

/**
 * Created by panagiotis on 16/9/2017.
 */

public class NewResidenceListingActivity extends FragmentActivity implements OnMapReadyCallback {

    private CheckedTextView _entire_place;
    private CheckedTextView _private_room;
    private CheckedTextView _shared_room;
    private CheckedTextView _shared_bath;
    private CheckedTextView _private_bath;
    private Button _kind_of_place_btn;
    private Button _guest_bedrooms_btn;
    private Button _guest_bathrooms_btn;
    private Button _place_location_btn;
    private Button _map_btn;
    private Button _add_photos;
    private Button _come_back_later_btn;
    private Button _btn_description;
    private Button _btn_title;
    private Button _photo_preview_next_btn = null;
    private GoogleMap mMap;
    private AddResidenceRequestDto residence = new AddResidenceRequestDto();
    private static final String TAG = "NewResidenceListingActivity";
    private static final int SELECT_SINGLE_PICTURE = 101;
    public static final String IMAGE_TYPE = "image/*";
    private ImageView selectedImagePreview;

    private User active_user;
    private Bundle bundle = new Bundle();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing_kind_of_place);

        if(getIntent()!=null && getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();
            User user = new Gson().fromJson(extras.get("user").toString(), User.class);
            if(user != null) {
                active_user = user;
                String user_json = new Gson().toJson(active_user);
                bundle.putString("user", user_json);
            }
        }


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

        _kind_of_place_btn = (Button) findViewById(R.id.btn_kind_of_place);
        _kind_of_place_btn.setOnClickListener(onClickListener);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_SINGLE_PICTURE) {
                Uri selectedImageUri = data.getData();

                setContentView(R.layout.photo_preview);
                selectedImagePreview = (ImageView)findViewById(R.id.cover_photo);

                OutputStream out;
                String root = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
                File createDir = new File(root+"airbnb-images"+File.separator);
                if(!createDir.exists()) {
                    createDir.mkdir();
                }
                String photoPath = selectedImageUri.toString();
                String filename = photoPath.substring(photoPath.lastIndexOf("/")+1,photoPath.length());
                File file = new File(root + "airbnb-images" + File.separator + filename);
                System.out.println("Path -> " + root + "airbnb-images" + File.separator + filename);
                System.out.println("Path -> " + root + "airbnb-images" + File.separator + filename);
                try {
                    file.createNewFile();
                    out = new FileOutputStream(file);
                    out.write(convertImageToByte(selectedImageUri));
                    out.close();

                    residence.getPhotoPaths().add(root + "airbnb-images" + File.separator + filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Uri uri = Uri.fromFile(new File(residence.getPhotoPaths().get(0)));
                Picasso.with(this).load(uri)
                        .resize(96, 96).centerCrop().into(selectedImagePreview);

                _photo_preview_next_btn = (Button) findViewById(R.id.btn_photo_preview_next);
                _photo_preview_next_btn.setOnClickListener(onClickListener);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get intent data", Toast.LENGTH_LONG).show();
            Log.d(NewResidenceListingActivity.class.getSimpleName(), "Failed to get intent data, result code is " + resultCode);
        }
    }

    public byte[] convertImageToByte(Uri uri){
        byte[] data = null;
        try {
            ContentResolver cr = getBaseContext().getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    private View.OnClickListener onClickListener =  new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            if (view.getId() == _kind_of_place_btn.getId()) {
                storeRoomDetails();
            } else if (view.getId() == _guest_bedrooms_btn.getId()) {
                storeBedroomDetails();
            } else if (view.getId() == _guest_bathrooms_btn.getId()) {
                storeBathroomDetails();
            } else if (view.getId() == _place_location_btn.getId()) {
                storeLocationDetails();
            } else if (view.getId() == _map_btn.getId()) {
                checkMapLocation();
            } else if (view.getId() == _add_photos.getId()) {
                addPhotosActions();
            } else if (view.getId() == _come_back_later_btn.getId()) {
                comeBackLaterActions();
            } else if (_photo_preview_next_btn != null && view.getId() == _photo_preview_next_btn.getId()){
                comeBackLaterActions();
            }else if(view.getId() == _btn_description.getId()){
                storeDescription();
            }else if(view.getId() == _btn_title.getId()){
                storeTitle();
            }
        }
    };

    private void storeTitle() {
        residence.setTitle(((EditText)findViewById(R.id.place_title)).getText().toString());
        new RegisterResidence().execute();
    }

    private void storeDescription() {
        residence.setDescription(((EditText)findViewById(R.id.place_description)).getText().toString());
        setContentView(R.layout.listing_place_title);
        _btn_title = (Button) findViewById(R.id.btn_place_title);
        _btn_title.setOnClickListener(onClickListener);
    }

    private void comeBackLaterActions() {
        setContentView(R.layout.listing_place_description);
        _btn_description = (Button) findViewById(R.id.btn_description);
        _btn_description.setOnClickListener(onClickListener);
    }

    private void addPhotosActions() {

        Intent intent = new Intent();
        intent.setType(IMAGE_TYPE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_SINGLE_PICTURE);

    }

    private void checkMapLocation() {

        setContentView(R.layout.listing_place_images);
        _add_photos = (Button) findViewById(R.id.btn_add_photos);
        _add_photos.setOnClickListener(onClickListener);
        _come_back_later_btn = (Button) findViewById(R.id.btn_come_back_later);
        _come_back_later_btn.setOnClickListener(onClickListener);

    }

    private void storeLocationDetails() {
        residence.setAddress(((EditText)findViewById(R.id.street)).getText().toString());
        residence.setLocation(((EditText)findViewById(R.id.country)).getText().toString());


        setContentView(R.layout.listing_place_map);
        MapView mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(null);
        mapView.getMapAsync(this);

        setContentView(R.layout.listing_place_map);
        _map_btn = (Button) findViewById(R.id.btn_place_map);
        _map_btn.setOnClickListener(onClickListener);
    }

    private void storeBathroomDetails() {
        residence.setBathrooms(Integer.valueOf(((EditText)findViewById(R.id.bathrooms_for_guests)).getText().toString()));

        setContentView(R.layout.listing_place_location);
        _place_location_btn = (Button) findViewById(R.id.btn_place_location);
        _place_location_btn.setOnClickListener(onClickListener);
    }

    private void storeBedroomDetails() {

        residence.setCapacity(Integer.valueOf(((EditText)findViewById(R.id.total_guests)).getText().toString()));
        residence.setBedrooms(Integer.valueOf(((EditText)findViewById(R.id.bedrooms_for_guests)).getText().toString()));
        residence.setBeds(Integer.valueOf(((EditText)findViewById(R.id.beds_for_guests)).getText().toString()));

        setContentView(R.layout.listing_guest_bathrooms);
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
        _guest_bathrooms_btn = (Button) findViewById(R.id.btn_guest_bathrooms);
        _guest_bathrooms_btn.setOnClickListener(onClickListener);
    }

    private void storeRoomDetails() {

        if(_shared_room.isChecked())
            residence.setType("Shared room");
        else if(_private_room.isChecked())
            residence.setType("Private room");
        else
            residence.setType("Entire place");

        setContentView(R.layout.listing_guest_bedrooms);
        _guest_bedrooms_btn = (Button) findViewById(R.id.btn_guest_bedrooms);
        _guest_bedrooms_btn.setOnClickListener(onClickListener);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        new DataLongOperationAsynchTask().execute(residence.getAddress());
    }

    private class DataLongOperationAsynchTask extends AsyncTask<String, Void, String[]> {
        ProgressDialog dialog = new ProgressDialog(NewResidenceListingActivity.this);
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
                LatLng latLng = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions().position(latLng));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));

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

    private class RegisterResidence extends AsyncTask<String, Void, Residence> {
        ProgressDialog dialog = new ProgressDialog(NewResidenceListingActivity.this);
        private RestTemplate restTemplate =  new RestApi().getRestTemplate();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected Residence doInBackground(String... params) {
            String uri = "";
            try {
                uri = Util.getProperty("baseAddress",getApplicationContext()) +  "/residence";
            } catch (IOException e) {
                e.printStackTrace();
            }
            Residence result = null;
            residence.setUsername(active_user.getUsername());
            try {
                HttpEntity<AddResidenceRequestDto> request = new HttpEntity<>(residence);
                result = restTemplate.postForObject(uri,request, Residence.class);
                return result;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Residence result) {

            if (result != null){
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                onRegistrationSucced();
                                dialog.dismiss();
                            }
                        }, 1000);
            }else{
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                onRegistrationFailed();
                                dialog.dismiss();
                            }
                        }, 1000);
            }

        }
    }

    private void onRegistrationSucced() {
        Intent intent = new Intent(getApplicationContext(), HostMainActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void onRegistrationFailed() {
        Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_LONG).show();
    }
}
