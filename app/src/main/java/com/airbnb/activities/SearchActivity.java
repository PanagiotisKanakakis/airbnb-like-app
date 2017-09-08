package com.airbnb.activities;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.rest.RestApi;
import com.airbnb.shared.dto.entity.Residence;
import com.airbnb.shared.dto.residence.SearchResidenceDto;
import com.sourcey.activities.R;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    @Bind(R.id.location)
    EditText _location;
    @Bind(R.id.arrival)
    EditText _arrival_date;
    @Bind(R.id.departure)
    EditText _departure_date;
    @Bind(R.id.guests)
    EditText _guests;
    @Bind(R.id.btn_search)
    Button _btn_search;
    final Calendar calendar = Calendar.getInstance();
    EditText selectedDate;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(calendar, selectedDate);
            }

        };

        _arrival_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate = _arrival_date;
                new DatePickerDialog(SearchActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        _departure_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate = _departure_date;
                new DatePickerDialog(SearchActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        _btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });

    }

    private void updateLabel(Calendar calendar, EditText text) {
        String myFormat = "yy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        text.setText(sdf.format(calendar.getTime()));
    }

    private Date convert(String date) throws ParseException {
        return new SimpleDateFormat("yy-MM-dd").parse(date);
    }

    private void search() {

        if (!validate()) {
            onSearchFailed();
            return;
        }
        String location = _location.getText().toString();
        String guests = _guests.getText().toString();
        String arrival_date = _arrival_date.getText().toString();
        String departure_date = _departure_date.getText().toString();

        new Search().execute(location, guests, arrival_date, departure_date);

    }

    private void onSearchFailed() {
        Toast.makeText(getBaseContext(), "Search failed", Toast.LENGTH_LONG).show();
    }

    private boolean validate() {
        String location = _location.getText().toString();
        String arrival_date = _arrival_date.getText().toString();
        String departure_date = _departure_date.getText().toString();

        if (location.isEmpty()) {
            _location.setError("Enter valid location");
            return false;
        } else {
            _location.setError(null);
        }

        if (_guests.getText().toString().isEmpty()) {
            _guests.setError("Enter valid number of guests");
            return false;
        } else {
            _guests.setError(null);
        }

        if (arrival_date.isEmpty()) {
            _arrival_date.setError("Enter valid date");
            return false;
        } else {
            _arrival_date.setError(null);
        }


        if (departure_date.isEmpty()) {
            _arrival_date.setError("Enter valid date");
            return false;
        } else {
            _arrival_date.setError(null);
        }

        return true;
    }

    private class Search extends AsyncTask<String, Void, Residence[] > {

        private RestTemplate restTemplate = new RestApi().getRestTemplate();

        @Override
        protected Residence[] doInBackground(String... params) {
            final String uri = "http://192.168.1.6:8080/searchResidence";

            SearchResidenceDto searchResidence = new SearchResidenceDto();
            searchResidence.setLocation(params[0]);
            searchResidence.setCapacity(Integer.parseInt(params[1]));
            try {
                searchResidence.setArrivalDate(convert(params[2]));
                searchResidence.setDepartureDate(convert(params[3]));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            searchResidence.setUsername("chris");


            Residence[] result = null;
            try {
                HttpEntity<SearchResidenceDto> request = new HttpEntity<>(searchResidence);
                result = restTemplate.postForObject(uri,request, Residence[].class);
                return result;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Residence[]  resultSet) {
            if(resultSet != null){

            }else
                onSearchFailed();
        }
    }
}