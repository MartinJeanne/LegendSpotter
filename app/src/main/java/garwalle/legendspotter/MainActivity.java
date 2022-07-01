package garwalle.legendspotter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView tvTitle;
    ListView listView;
    CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTitle = findViewById(R.id.tvTitle);
        listView = findViewById(R.id.lvChamps);

        ChampsListAsyncTask getChamps = new ChampsListAsyncTask();
        getChamps.execute();

        // When the user clicks on the ListItem
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                Champ champ = (Champ) o;
                //Toast.makeText(MainActivity.this, "Selected :" + " " + champ, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ChampionActivity.class);
                intent.putExtra("champName", champ.getName());
                startActivity(intent);
            }
        });
    }

    private class ChampsListAsyncTask extends AsyncTask<Void, Void, List<Champ>> {

        @Override
        protected List<Champ> doInBackground(Void... v) {

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
                return null;
            }

            List<Champ> champs = new ArrayList<Champ>();
            String url = "http://ddragon.leagueoflegends.com/cdn/12.12.1/data/en_US/champion.json";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Iterator<String> iteratorChamps = response.getJSONObject("data").keys();
                        while (iteratorChamps.hasNext()) {
                            champs.add(new Champ(iteratorChamps.next()));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.v("--error", "Error : " + e.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("--error", "Erreur requête images : " + error.toString());
                    tvTitle.setText("Erreur requête images : " + error.toString());
                }
            });

            SingletonRequestQueue.getInstance(MainActivity.this).addToRequestQueue(jsonObjectRequest);

            return champs;
        }

        @Override
        protected void onPostExecute(List<Champ> champs) {
            super.onPostExecute(champs);
            if (champs == null) tvTitle.setText("Permissions internet manquante.");
            else {
                adapter = new CustomListAdapter(MainActivity.this, champs);
                listView.setAdapter(adapter);
            }
        }
    }
}
