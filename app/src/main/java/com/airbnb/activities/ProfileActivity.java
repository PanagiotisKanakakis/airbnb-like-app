package com.airbnb.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.Utils.Util;
import com.airbnb.images.ImageModel;
import com.airbnb.rest.RestApi;
import com.airbnb.shared.dto.entity.Comment;
import com.airbnb.shared.dto.entity.Residence;
import com.airbnb.shared.dto.entity.User;
import com.airbnb.shared.dto.user.UserUpdateProfileDto;
import com.airbnb.shared.dto.user.UserUtilsDto;
import com.google.gson.Gson;
import com.sourcey.activities.R;
import com.squareup.picasso.Picasso;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by panagiotis on 16/9/2017.
 */

public class ProfileActivity extends AppCompatActivity {

    private User active_user;
    private Bundle bundle = new Bundle();
    public static final String IMAGE_TYPE = "image/*";
    private static final int SELECT_SINGLE_PICTURE = 101;
    private static final String TAG = "ProfileActivity";
    private ProgressDialog progressDialog ;
    @Bind(R.id.imgView) ImageView _img;
    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_surname) EditText _surnameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_mobile) EditText _mobileText;
    @Bind(R.id.btn_update) Button _updateButton;
    private String path;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            User user = new Gson().fromJson(extras.get("user").toString(), User.class);
            if (user != null) {
                active_user = user;
                String user_json = new Gson().toJson(active_user);
                bundle.putString("user", user_json);

                _nameText.setText(active_user.getName());
                _surnameText.setText(active_user.getSurname());
                _emailText.setText(active_user.getEmail());
                _mobileText.setText(active_user.getPhoneNumber());

                if(active_user.getProfilePhoto() != null && active_user.getProfilePhoto().getPath() != null){
                    Uri uri = Uri.fromFile(new File(active_user.getProfilePhoto().getPath()));
                    path = active_user.getProfilePhoto().getPath();
                    Picasso.with(this).load(uri)
                            .transform(new CircleTransform())
                            .resize(150,150).into(_img);
                }
            }
        }

        _img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType(IMAGE_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_SINGLE_PICTURE);
            }
        });

        _updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = _nameText.getText().toString();
                String email = _emailText.getText().toString();
                String mobile = _mobileText.getText().toString();
                String surname = _surnameText.getText().toString();

                active_user.setName(name);
                active_user.setSurname(surname);
                active_user.setEmail(email);
                active_user.setPhoneNumber(mobile);
                if(active_user.getProfilePhoto() != null)
                    active_user.getProfilePhoto().setPath(path);
                new UpdateUserAsyncTask(getApplicationContext()).execute();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_SINGLE_PICTURE) {
                Uri selectedImageUri = data.getData();

                OutputStream out;
                String root = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
                File createDir = new File(root+"airbnb-images"+File.separator);
                if(!createDir.exists()) {
                    createDir.mkdir();
                }
                String photoPath = selectedImageUri.toString();
                String filename = photoPath.substring(photoPath.lastIndexOf("/")+1,photoPath.length());
                File file = new File(root + "airbnb-images" + File.separator + filename);
                try {
                    file.createNewFile();
                    out = new FileOutputStream(file);
                    out.write(convertImageToByte(selectedImageUri));
                    out.close();

                    path = root + "airbnb-images" + File.separator + filename;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Uri uri = Uri.fromFile(new File(path));
                Picasso.with(this).load(uri)
                        .transform(new CircleTransform())
                        .resize(150,150).into(_img);

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

    private class UpdateUserAsyncTask extends AsyncTask<String, Void, User> {
        private RestTemplate restTemplate =  new RestApi().getRestTemplate();
        private Context context;

        public UpdateUserAsyncTask(Context applicationContext) {
            this.context = applicationContext;
        }

        @Override
        protected User doInBackground(String... params) {

            String uri = "";
            try {
                uri = Util.getProperty("baseAddress",getApplicationContext()) +  "/updateProfile";
            } catch (IOException e) {
                e.printStackTrace();
            }

            UserUpdateProfileDto userUpdateProfileDto = new UserUpdateProfileDto();
            System.out.println("name -> " + active_user.getName());
            userUpdateProfileDto.setUsername(active_user.getUsername());
            userUpdateProfileDto.setName(active_user.getName());
            userUpdateProfileDto.setSurname(active_user.getSurname());
            userUpdateProfileDto.setEmail(active_user.getEmail());
            userUpdateProfileDto.setPhoneNumber(active_user.getPhoneNumber());
            userUpdateProfileDto.setPhoto(path);

            User result = null;
            try {
                HttpEntity<UserUpdateProfileDto> request = new HttpEntity<>(userUpdateProfileDto);
                result = restTemplate.postForObject(uri, request,User.class);
                return result;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {

            if(user != null){
                Intent intent = new Intent(getApplicationContext(), MainLoggedInActivity.class);
                String user_json = new Gson().toJson(user);
                bundle.putString("user",user_json);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }else{
                Toast.makeText(getBaseContext(), "Update failed", Toast.LENGTH_LONG).show();
            }

        }
    }

}
