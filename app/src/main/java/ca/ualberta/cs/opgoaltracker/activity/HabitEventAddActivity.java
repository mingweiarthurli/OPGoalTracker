/*
 * Copyright 2017 Yongjia Huang, Dichong Song, Mingwei Li, Donglin Han, Long Ma,CMPUT301F17T25 CMPUT301, University of Alberta, All Rights Reserved.
 * You may use distribut, or modify this code under terms and conditions of the ode of Student Behavior at University of Alberta
 * You may find a copy of the license in this project. Otherwise please contact jajayongjia@gmail.com
 */

package ca.ualberta.cs.opgoaltracker.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.Manifest;

import ca.ualberta.cs.opgoaltracker.Controller.ElasticsearchController;
import ca.ualberta.cs.opgoaltracker.Controller.LatitudeAndLongitudeWithPincode;
import ca.ualberta.cs.opgoaltracker.R;
import ca.ualberta.cs.opgoaltracker.exception.CommentTooLongException;
import ca.ualberta.cs.opgoaltracker.exception.ImageTooLargeException;
import ca.ualberta.cs.opgoaltracker.models.Habit;
import ca.ualberta.cs.opgoaltracker.models.HabitEvent;
import ca.ualberta.cs.opgoaltracker.models.Photograph;
import ca.ualberta.cs.opgoaltracker.models.Restriction;

/**
 * This is the activity that allows users to create a new habit event
 * This page allows user to create a new habit event with custom comment and ability to select a picture
 * @author Long Ma
 * @version 2.0
 * @see AppCompatActivity
 * @since 2.0
 */
public class HabitEventAddActivity extends AppCompatActivity {
    HabitEvent newEvent;
    Boolean setPicture = false;
    Bitmap picture;
    View view;
    String habitType;
    String filePath;
    private Restriction restriction;
    private LatitudeAndLongitudeWithPincode convertedAddress;


    /**
     * Handles the activity in which new habitEvents are created
     * receive a string arraylist containing all the habit types the user has
     * returns a new habitevent object according to what the user enters
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);

        //gets the habit list
        ArrayList<String> arrayListHabit=getIntent().getStringArrayListExtra("hlist");
        restriction = getIntent().getParcelableExtra("RESTRICTION");

        final Spinner habitList = (Spinner) findViewById(R.id.habit_spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListHabit);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        habitList.setAdapter(dataAdapter);

        // for the all habit types are set to 'test'

        //Adds picture
        //Important, requires permission to add picture from storage
        //Currently requires permission to be given manually, will change to ask for permission later
        final ImageButton getImage = (ImageButton) findViewById(R.id.new_event_picture);
        getImage.setOnClickListener(new View.OnClickListener() {
            /**
             * implements the imageButton
             * get images from storage
             * @param view: the view
             */
            @Override
            public void onClick(View view) {
                /**
                 * TO SELECT PICTURE
                 */


                if (ContextCompat.checkSelfPermission(HabitEventAddActivity.this, android.Manifest.permission.
                        READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else {
                    //If permission to access storage not already given, request it
                    ActivityCompat.requestPermissions(HabitEventAddActivity.this, new String[]{
                            android.Manifest.permission.READ_EXTERNAL_STORAGE}, 51);

                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                }
            }
        });

        Button createEvent = (Button) findViewById(R.id.create_event);
        createEvent.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles if the user believes all the information about the new habit event has been entered
             * creates a new habit event and ends the activity
             * @param view: the view
             */
            @Override
            public void onClick(View view) {
                Boolean good = Boolean.TRUE;
                Intent intent = new Intent();
                EditText comment = (EditText) findViewById(R.id.new_event_comment);
                EditText address = (EditText) findViewById(R.id.address);

                try {
                    String eventComment = comment.getText().toString();
                    String selectedHabit = habitList.getSelectedItem().toString();

                    //for some reason eventComment triggers commenttoolongexception
                    newEvent = new HabitEvent(selectedHabit, eventComment, new Date(), restriction.getCommentSize());

                    String eventAddress = address.getText().toString();
                    if (!eventAddress.isEmpty()) {
                        //handles if the user enters an address to add to the habit event
                        Geocoder geocoder = new Geocoder(HabitEventAddActivity.this);
                        try {
                            //Gets the location of the address entered
                            List<Address> ad = geocoder.getFromLocationName(eventAddress, 1);
                            Address a = ad.get(0);
                            String lat = Double.toString(a.getLatitude());
                            String lng = Double.toString(a.getLongitude());
                            newEvent.setLocation(lat,lng);
                            String place = eventAddress +" in "+ a.getLocality();
                            Toast.makeText(HabitEventAddActivity.this,place,Toast.LENGTH_LONG).show();
                            String p = lat + " " + lng;
                            Toast.makeText(HabitEventAddActivity.this, p, Toast.LENGTH_LONG).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }




                    }else{
                        //If the enter location is empty, check is the upload current location is checked
                        CheckBox gpsLocation = (CheckBox)findViewById(R.id.gps_checkbox);
                        if (gpsLocation.isChecked()){
                            //If checked, check if permission are given to get the phone's location
                            if (ContextCompat.checkSelfPermission(HabitEventAddActivity.this,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION)!=
                                    PackageManager.PERMISSION_GRANTED ){
                                good = Boolean.FALSE;
                                ActivityCompat.requestPermissions(HabitEventAddActivity.this, new String[]{
                                        android.Manifest.permission.ACCESS_FINE_LOCATION}, 52);
                            }else if (ContextCompat.checkSelfPermission(HabitEventAddActivity.this,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION)!=
                                    PackageManager.PERMISSION_GRANTED ){
                                good = Boolean.FALSE;
                                ActivityCompat.requestPermissions(HabitEventAddActivity.this, new String[]{
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION}, 53);
                            }

                            else {
                                //If permissions given, get the location
                                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                //handles is the location manager has not already obtained the location
                                //requests a check on location
                                if (location==null) {
                                    final LocationListener locationListener = new LocationListener() {
                                        @Override
                                        public void onLocationChanged(final Location location) {

                                            // getting location of user
                                            final double latitude = location.getLatitude();
                                            final double longitude = location.getLongitude();
                                        }

                                        @Override
                                        public void onStatusChanged(String provider, int status, Bundle extras) {
                                        }

                                        @Override
                                        public void onProviderEnabled(String provider) {
                                            //when user enables the GPS setting, this method is triggered.
                                        }

                                        @Override
                                        public void onProviderDisabled(String provider) {
                                            //when no provider is available in this case GPS provider, trigger your gpsDialog here.
                                        }
                                    };
                                    // Used https://stackoverflow.com/questions/42218419/how-do-i-implement-the-locationlistener as reference
                                    //2017-12-01

                                    //update location every 10sec in 500m radius with both provider GPS and Network.

                                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 500, locationListener);
                                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 500, locationListener);
                                    Toast.makeText(HabitEventAddActivity.this,"Please wait while we retrieve your location",Toast.LENGTH_LONG).show();
                                    Thread.sleep(1000);

                                    location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                }
                                if (location==null){
                                    Toast.makeText(HabitEventAddActivity.this,"cannot get your current location",Toast.LENGTH_LONG);
                                    return;
                                }
                                // transform the longitude and latitude to string and save into the event
                                String lat = Double.toString(location.getLatitude());
                                String lng = Double.toString(location.getLongitude());
                                newEvent.setLocation(lat, lng);
                                String place = lat + " " + lng;
                                Toast.makeText(HabitEventAddActivity.this, place, Toast.LENGTH_LONG).show();
                            }
                            // taken from https://stackoverflow.com/questions/2227292/how-to-get-latitude-and-longitude-of-the-mobile-device-in-android
                            // 2017-11-22
                        }
                    }

                    //handles picture
                    if (setPicture) {
                        try {
                            //try to ge tthe picture
                            Drawable draw =getImage.getDrawable();
                            Bitmap p = ((BitmapDrawable)draw).getBitmap();
                            newEvent.setPhoto(new Photograph(filePath, restriction.getPictureSize()));
                        } catch (ImageTooLargeException e) {
                            Log.d("exception","catch first");
                            String imageLimit = String.valueOf(restriction.getPictureSize());
                            Toast.makeText(HabitEventAddActivity.this, "Image should be under " + imageLimit + " bytes",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if (good == Boolean.TRUE) {
                        //If the program determins that the habit event is created without any problem
                        Intent data = new Intent();

                        ElasticsearchController.AddHabitEventsTask addEvent= new ElasticsearchController.AddHabitEventsTask();
                        addEvent.execute(newEvent);

                        data.putExtra("event", newEvent);
                        setResult(AppCompatActivity.RESULT_OK, data);
                        Log.d("last",newEvent.getComment());
                        //parse the new habit event back to the fragment where it will be uploaded
                        finish();
                    }
                } catch (CommentTooLongException e) {
                    //catches the habitevent comment too long exception
                    Log.d("failed", "null event");
                    Toast.makeText(HabitEventAddActivity.this,"Your comment is too long",Toast.LENGTH_SHORT);
                    return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    /**
     * When returning from requesting a permission from the user
     * @param requestCode : request code given to determine which permission were asked for
     * @param permission: String array
     * @param grantedResults: array to store if permission granted or not
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantedResults) {
        switch (requestCode){
            case 51:
                //if given permission to check storage
                if (grantedResults.length > 0 && grantedResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }

            case 52:
                // if given permission to check
                if (grantedResults.length > 0 && grantedResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(HabitEventAddActivity.this,"FINE_LOCATION permission granted",Toast.LENGTH_LONG).show();
                }
            case 53:
                if (grantedResults.length > 0 && grantedResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(HabitEventAddActivity.this,"COARSE_LOCATION permission granted",Toast.LENGTH_LONG).show();
                }
        }
    }

    /**
     * handles return from activity for result
     * activity created from this activity is to get photo
     * @param requestCode: a code to determine which activity returning from
     * @param resultCode: code to determine if results are good or not
     * @param data: data returned from the activity
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 2:

                //GET RESULT FROM SELECT PICTURE
                if (resultCode == RESULT_OK && null != data) {
                    try {
                        setPicture = true;
                        Uri uri = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver().query(
                                uri, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        filePath = cursor.getString(columnIndex);
                        cursor.close();

                        //taken from https://stackoverflow.com/questions/2507898/how-to-pick-an-image-from-gallery-sd-card-for-my-app
                        //2017-11-13

                        Bitmap picture = BitmapFactory.decodeFile(filePath);
                        ImageButton getImage = (ImageButton) findViewById(R.id.new_event_picture);
                        getImage.setImageBitmap(picture);


                    } catch (Exception e) {
                    }
                }
        }
    }

    //Old version of the code left incase the new version does not work

/**
    private class GetCoordinates extends AsyncTask<String,Void,String> {
        ProgressDialog dialog = new ProgressDialog(HabitEventAddActivity.this);

        String[] location = new String[] {"",""};


        @Override
        protected void onPreExecute(){

            super.onPreExecute();
//            dialog.setMessage("Please wait....");
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            String response;
            try{
                String address = strings[0];
                LatitudeAndLongitudeWithPincode http = new LatitudeAndLongitudeWithPincode();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s",address);
                response = http.getHTTPDate(url);
                return response;
            }catch (Exception ex){

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s){
            try{
                JSONObject jsonObject = new JSONObject(s);
                String lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lat").toString();
                String lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lng").toString();
                newEvent.setLocation(lat,lng);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (newEvent!=null) {
//                    //handles if upload location
//                    if (location.isChecked()){
//                        newEvent.setLocation("somewhere");
//                    }
                //handles picture
                if (setPicture) {
                    try {
                        newEvent.setPhoto(new Photograph(33, 33));
                    } catch (ImageTooLargeException e) {
                        Toast.makeText(getApplicationContext(), "Image should be under 65536 bytes",
                                Toast.LENGTH_SHORT).show();

                    }
                }


                Intent data = new Intent();
                data.putExtra("event", (Parcelable)newEvent);
                setResult(AppCompatActivity.RESULT_OK,data);
                finish();
            }
            else{
                Log.d("failed","null event");
                setResult(AppCompatActivity.RESULT_CANCELED);
                finish();
            }
        }
    }*/
}
