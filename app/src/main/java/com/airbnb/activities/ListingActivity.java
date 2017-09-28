package com.airbnb.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.airbnb.Utils.Util;
import com.airbnb.images.ImageModel;
import com.airbnb.rest.RestApi;
import com.airbnb.shared.dto.entity.Residence;
import com.airbnb.shared.dto.entity.User;
import com.airbnb.shared.dto.user.UserUtilsDto;
import com.google.gson.Gson;
import com.sourcey.activities.R;
import com.squareup.picasso.Picasso;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListingActivity extends AppCompatActivity {

    private static final String TAG = "1";
    private User active_user;
    private Bundle bundle = new Bundle();
    @Bind(R.id.userResidences) ListView userResidences;
    @Bind(R.id.addResidence) ImageButton addResidence;
    private Residence[] residences ;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_progress);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }

        if(getIntent()!=null && getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();
            User user = new Gson().fromJson(extras.get("user").toString(), User.class);
            if(user != null) {
                active_user = user;
                String user_json = new Gson().toJson(active_user);
                bundle.putString("user", user_json);
                new GetUserResidencesAsyncTask(this).execute();
            }
        }

        addResidence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewResidenceListingActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


    }

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    },
                    1052);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1052: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(ListingActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                    new GetUserResidencesAsyncTask(this).execute();
                } else {
                    Toast.makeText(ListingActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    private class GetUserResidencesAsyncTask extends AsyncTask<String, Void, Residence[]> {
        private RestTemplate restTemplate =  new RestApi().getRestTemplate();
        private Context context;

        public GetUserResidencesAsyncTask(Context applicationContext) {
            this.context = applicationContext;
        }

        @Override
        protected Residence[] doInBackground(String... params) {

            String uri = "";
            try {
                uri = Util.getProperty("baseAddress",getApplicationContext()) +  "/getUserResidences";
            } catch (IOException e) {
                e.printStackTrace();
            }

            UserUtilsDto userUtilsDto = new UserUtilsDto();
            userUtilsDto.setUsername(active_user.getUsername());

            Residence[] result = null;
            try {
                HttpEntity<UserUtilsDto> request = new HttpEntity<>(userUtilsDto);
                result = restTemplate.postForObject(uri, request,Residence[].class);
                return result;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Residence[] result) {
            if(result != null){
                ArrayList<ImageModel> resultSet = new ArrayList<>();
                for(Residence r : result){
                    ImageModel im = new ImageModel();
                    im.setResidenceId(r.getResidenceId());
                    im.setCost("30");
                    im.setDescription(r.getDescription());
                    im.setGrade("0");

                    if(r.getPhotoPaths() != null && r.getPhotoPaths().size() > 0)
                        im.setPath(r.getPhotoPaths().get(0).getPath());

                    im.setType(r.getType());
                    resultSet.add(im);
                }
                userResidences.setAdapter(new CustomAdapter(getApplicationContext(),resultSet));
            }
        }
    }
}
