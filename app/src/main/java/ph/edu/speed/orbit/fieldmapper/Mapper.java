package ph.edu.speed.orbit.fieldmapper;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.google.maps.android.SphericalUtil.computeArea;


public class Mapper extends ActionBarActivity {

    GPSTracker gpsTracker;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int Count;
    Double[] lat = new Double[99];
    Double[] lng = new Double[99];
    JSONObject jCoor;
    JSONArray ArrayCoor;
    JsonArray farmArray;
    double Area;
    int Plots;

    ArrayList<LatLng> coor = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapper);

        Button start = (Button) findViewById(R.id.btStart);
        Button stop = (Button) findViewById(R.id.btStop);

        Count = 0;
        jCoor = new JSONObject();
        ArrayCoor = new JSONArray();
        coor = new ArrayList<>();

        pref = this.getSharedPreferences("MyFarm", Context.MODE_PRIVATE);
        String FarmString = pref.getString("farm", "[]");
        JsonParser parser = new JsonParser();
        JsonElement elem = parser.parse(FarmString);
        farmArray = elem.getAsJsonArray();
        Plots = farmArray.size();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapping_start();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapping_stop();
            }
        });


    }

    private void mapping_stop() {

            Area = computeArea(coor);

        if(Area != 0){
            Gson gson = new GsonBuilder().create();
            JsonArray coorArray = gson.toJsonTree(coor).getAsJsonArray();

            Plots++;
            JsonObject coorObject = new JsonObject();
            coorObject.addProperty("ID",Plots);
            coorObject.addProperty("Area",Area);
            coorObject.addProperty("Coordinates", String.valueOf(coorArray));

            farmArray.add(coorObject);
            editor = pref.edit();
            editor.putString("farm", String.valueOf(farmArray));
            editor.commit();

            coor.clear();
        }

        TextView a = (TextView) findViewById(R.id.tvMstat);
        a.setText(String.valueOf(farmArray));

        Intent i = new Intent(this,MapsActivity.class);
        startActivity(i);
    }

    private void mapping_start() {

            gpsTracker = new GPSTracker(Mapper.this);
            lat[Count] = gpsTracker.getLatitude();
            lng[Count] = gpsTracker.getLongitude();
            LatLng c = new LatLng(lat[Count],lng[Count]);
            coor.add(c);
        TextView a = (TextView) findViewById(R.id.tvMstat);
        a.setText(String.valueOf(c));
            Count++;
    }
}

