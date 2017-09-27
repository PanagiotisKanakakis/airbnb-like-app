package com.airbnb.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.airbnb.Utils.Util;
import com.airbnb.images.ImageModel;
import com.airbnb.rest.RestApi;
import com.airbnb.shared.dto.entity.Photo;
import com.airbnb.shared.dto.entity.Residence;
import com.airbnb.shared.dto.entity.User;
import com.airbnb.shared.dto.user.UserUtilsDto;
import com.google.gson.Gson;
import com.sourcey.activities.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_progress);
        ButterKnife.bind(this);

        if(getIntent()!=null && getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();
            User user = new Gson().fromJson(extras.get("user").toString(), User.class);
            if(user != null) {
                active_user = user;
                String user_json = new Gson().toJson(active_user);
                bundle.putString("user", user_json);
                new GetUserResidencesAsynchTask().execute();
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



    private class GetUserResidencesAsynchTask extends AsyncTask<String, Void, Residence[]> {
        private RestTemplate restTemplate =  new RestApi().getRestTemplate();

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
                    im.setCost("30");
                    im.setDescription(r.getDescription());
                    im.setGrade("0");
                    if(r.getPhotoPaths() != null && r.getPhotoPaths().size() > 0) {

                        /*try {
                            Bitmap captureBmp = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                    Uri.parse(r.getPhotoPaths().get(0).getPath()));
                            im.setImage(captureBmp);
                        } catch (IOException e) {
                            im.setImage(null);
                            e.printStackTrace();
                        }*/
                    }
                    im.setType(r.getType());
                    resultSet.add(im);
                }
                userResidences.setAdapter(new CustomAdapter(getApplicationContext(),resultSet));
            }
        }
    }
}
