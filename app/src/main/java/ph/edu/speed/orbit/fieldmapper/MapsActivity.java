package ph.edu.speed.orbit.fieldmapper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    int Count;
    double[] lat;
    double[] lng;
    SharedPreferences pref;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    JSONObject[] FarmPlotObjects;
    JSONArray FarmArray, PlotArray;
    JSONObject plotpolygonArray;
    String[] plotpolygonArrayString, plotpolygonAreaString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Count = 0;
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        viewmap();

        Button create = (Button)findViewById(R.id.btSave);
        create.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPolygon();
            }
        });

    }



    //Creates Polygon on other Activity
    private void createPolygon() {
        AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
        alertDialog.setTitle("Create New Polygon");
        alertDialog.setMessage("Are you sure?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(MapsActivity.this,Mapper.class);
                        startActivity(i);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }



    //Setting up the polygons if available from sharedpref
    private void viewmap() {

        pref = getApplicationContext().getSharedPreferences("MyFarm", MODE_PRIVATE);
        String info = pref.getString("Area","No information");
        TextView stat = (TextView) findViewById(R.id.tvMapStat);
        stat.setText("Area: " + info);

        String str = pref.getString("farm", "[]");

        if(!(str == "[]")) {
            JSONObject[] coor;
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            try {
                FarmArray = new JSONArray(str);
                FarmPlotObjects = new JSONObject[FarmArray.length()];
                plotpolygonArrayString = new String[FarmArray.length()];
                plotpolygonAreaString = new String[FarmArray.length()];

                for (int i = 0; i < FarmArray.length(); i++) {
                    FarmPlotObjects[i] = FarmArray.getJSONObject(i);
                    plotpolygonArrayString[i] = FarmPlotObjects[i].getString("Coordinates");
                    plotpolygonAreaString[i] = FarmPlotObjects[i].getString("Area");
                }


                for (int i = 0; i < FarmArray.length(); i++) {
                    PlotArray = new JSONArray(plotpolygonArrayString[i]);
                    coor = new JSONObject[PlotArray.length()];

                    for (int j = 0; j < PlotArray.length(); j++) {
                        coor[j] = PlotArray.getJSONObject(j);
                    }

                    lat = new double[coor.length];
                    lng = new double[coor.length];

                    for (int j = 0; j < coor.length; j++) {
                        lat[j] = coor[j].getDouble("latitude");
                        lng[j] = coor[j].getDouble("longitude");
                    }

                    setUpMap(lat, lng, coor.length,i);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            stat.setText(Arrays.toString(lat));

        }
    }



    //Setting up the polygons on the map
    private void setUpMap(double[] lat, double[] lng, int length, int plotnumber) {
        PolygonOptions setup = new PolygonOptions().strokeColor(Color.RED).fillColor(Color.BLUE);
        for (int i = 0; i < length ; i++) {
            setup.add(new LatLng(lat[i],lng[i]));
        }

        MarkerOptions options = new MarkerOptions().position(new LatLng(lat[0],lng[0]));
        options.title("Plot: " + String.valueOf(plotnumber));
        options.snippet("Area: " + plotpolygonAreaString[plotnumber]);
        mMap.addMarker(options);

        mMap.addPolygon(setup);

        //zoom to location
        LatLng coordinate = new LatLng(lat[0], lng[0]);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 20);
        mMap.animateCamera(yourLocation);

    }



    @Override
    protected void onResume() {
        super.onResume();
        viewmap();
    }
}
