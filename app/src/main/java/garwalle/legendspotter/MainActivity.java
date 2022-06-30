package garwalle.legendspotter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

        List<Champ> champs = getListData();
        tvTitle = findViewById(R.id.tvTitle);
        listView = findViewById(R.id.lvChamps);
        adapter = new CustomListAdapter(this, champs);
        listView.setAdapter(adapter);

        // When the user clicks on the ListItem
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                Champ champ = (Champ) o;
                Toast.makeText(MainActivity.this, "Selected :" + " " + champ, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ChampionActivity.class);
                intent.putExtra("champName", champ.getName());
                startActivity(intent);
            }
        });
    }

    public List<Champ> getListData() {
        List<Champ> list = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
            list.add(new Champ("Kayn"));
            return list;
        }

        String url = "http://ddragon.leagueoflegends.com/cdn/12.12.1/data/en_US/champion.json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    response = response.getJSONObject("data");
                    Iterator<String> iteratorChamps = response.keys();
                    while (iteratorChamps.hasNext()) {
                        list.add(new Champ(iteratorChamps.next()));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.v("--error", "Error : " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("--error", "Erreur requête images : " + error.toString());
                tvTitle.setText("Erreur requête images : " + error);
            }
        });

        SingletonRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);

        return list;
    }
}
