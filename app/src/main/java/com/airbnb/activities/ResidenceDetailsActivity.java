package com.airbnb.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.airbnb.shared.dto.entity.Residence;
import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;
import com.sourcey.activities.R;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ResidenceDetailsActivity extends AppCompatActivity {


    @Bind(R.id.type) TextView _type;
    @Bind(R.id.hostedBy) TextView _hostedBy;
    @Bind(R.id.guests) TextView _guests;
    @Bind(R.id.bedrooms) TextView _bedrooms;
    @Bind(R.id.beds) TextView _beds;
    @Bind(R.id.bath) TextView _bath;
    @Bind(R.id.description) TextView _description;
    //@Bind(R.id.map) GoogleMap _map;
    @Bind(R.id.reviewList) ListView _reviewList;
    @Bind(R.id.checkAvailability) Button _checkAvailability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_residence_details);
        ButterKnife.bind(this);

        _checkAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if(getIntent()!=null && getIntent().getExtras() != null && getIntent().getExtras().get("residence")!=null){
            Bundle extras = getIntent().getExtras();
            Residence residence = new Gson().fromJson(extras.get("residence").toString(), Residence.class);
            if(residence != null) {
               _type.setText(residence.getType());
                //_hostedBy.setText(residence.);
                _guests.setText(String.valueOf(residence.getSize()));
                _bedrooms.setText(String.valueOf(residence.getBedrooms()));
                _beds.setText(String.valueOf(residence.getBeds()));
                _bath.setText(String.valueOf(residence.getBathrooms()));
                _description.setText(residence.getDescription());
            }
        }

    }
}
