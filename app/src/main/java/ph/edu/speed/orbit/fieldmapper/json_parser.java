package ph.edu.speed.orbit.fieldmapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jaredsalazar on 5/29/15.
 */
public class json_parser {

    static String[] Name;
    static String[] Var_ID;
    static Integer Count;
    JSONObject jObject;
    JSONArray jArray;

    static JSONArray CArray;
    static JSONObject[] variable,Cvariable;
    static Double[] Lat;
    static Double[] Lng;

    public json_parser(String response) {

        try {
            jObject = new JSONObject(response);
            Count = jObject.getInt("count");

            //setting arrays with count
            variable = new JSONObject[Count];
            Name = new String[Count];
            Var_ID = new String[Count];

            jArray = jObject.getJSONArray("results");

            for (int i = 0; i < Count; i++){
                variable[i] = jArray.getJSONObject(i);
                Name[i] = variable[i].getString("name");
                Var_ID[i] = variable[i].getString("id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class coordinate_parser {
        public coordinate_parser(String str, Integer num) {

            Count = num;

                try {
                    CArray = new JSONArray(str);
                    Cvariable = new JSONObject[Count];
                    Lat = new Double[Count];
                    Lng = new Double[Count];


                    for(int i = 0; i < Count  ;i++){
                        Cvariable[i] = CArray.getJSONObject(i);
                        Lat[i] = Cvariable[i].getDouble("Latitude");
                        Lng[i] = Cvariable[i].getDouble("Longitude");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }
    }
}
