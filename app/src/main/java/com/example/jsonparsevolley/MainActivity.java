package com.example.jsonparsevolley;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    Button button,button1;
    double userLat = 22.189332896412672;
    // Replace with user's actual latitude
    double userLng = 91.97400560674593;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView1);
        button = findViewById(R.id.button);
        button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://abdominous-balls.000webhostapp.com/police.json", null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                JSONArray policeDepartments = null;
                                try {
                                    policeDepartments = response.getJSONArray("police_departments");

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                double minDistance = Double.MAX_VALUE;
                                String nearestPoliceDeptName = "";
                                for(int i=0;i<policeDepartments.length();i++){
                                    try {
                                        JSONObject policeDepartment = policeDepartments.getJSONObject(i);
                                        String phoneNumber = policeDepartment.getString("number");
                                        JSONObject locationObject = policeDepartment.getJSONObject("location");
                                        double latitude = locationObject.getDouble("lat");
                                        double longitude = locationObject.getDouble("lng");
                                        String name = policeDepartment.getString("name");
                                        double distance = calculateDistance(userLat, userLng, longitude, longitude);

                                        System.out.println("Police Dept: " + name);
                                        System.out.println("Distance: " + distance);
                                        if (distance < minDistance) {
                                            minDistance = distance;
                                            nearestPoliceDeptName = name;
                                        }
                                        textView.append("Phone: " + phoneNumber + "\n");
                                        textView.append("Latitude: " + latitude + "\n");
                                        textView.append("Longitude: " + longitude + "\n");
                                        textView.append("Distance: " + distance + " km\n\n");

//                                        textView.append(latitude+"\n");
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

                                }
                                textView.append("Nearest Police Department: " + nearestPoliceDeptName + "\n");

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }
                );

                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                requestQueue.add(request);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonParse();
            }
        });
    }
    private void JsonParse(){
        String url = "https://jsonplaceholder.typicode.com/users";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
//                        JSONArray jsonArray = response.getJSONArray()
                        for(int i=0;i<response.length();i++)
                        {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                String name = object.getString("name");
                                // Access other properties as needed
                                String email = object.getString("email");

                                // Append the data to your TextView or perform other actions
                                textView.append(name + "\n" + email + "\n\n");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(request);
    }

    private double calculateDistance(double userLat, double userLng, double policeLat, double policeLng) {
        int R = 6371; // Radius of the Earth in kilometers
        double latDistance = Math.toRadians(policeLat - userLat);
        double lngDistance = Math.toRadians(policeLng - userLng);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(policeLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in kilometers
    }
}