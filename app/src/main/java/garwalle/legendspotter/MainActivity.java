package garwalle.legendspotter;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
    Button showFavorites;

    boolean favoritesShowed = false;
    int positionItemClicked;

    ActivityResultLauncher<Intent> launchChampionActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        // Si les favoris sont affichés et qu'un favoris a été enlevé, on supprime le favoris de la listView
                        if (favoritesShowed) {
                            boolean favoriteDeleted = result.getData().getBooleanExtra("favoriteDeleted", false);
                            if (favoriteDeleted) {
                                adapter.deleteItem(positionItemClicked);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } else if(result.getResultCode() == RESULT_CANCELED) {
                        Toast.makeText(MainActivity.this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTitle = findViewById(R.id.tvTitle);
        listView = findViewById(R.id.lvChamps);
        showFavorites = findViewById(R.id.showFavorites);
        showFavorites.setOnClickListener(view -> {
            if (!favoritesShowed) {
                DbHelper dbHelper = new DbHelper(MainActivity.this);
                List<Champ> champs = dbHelper.getFavoritesChamp();
                if (champs.size() > 0) {
                    adapter = new CustomListAdapter(MainActivity.this, champs);
                    listView.setAdapter(adapter);
                    showFavorites.setText("Montrer tous les champions");
                    favoritesShowed = true;
                }
                else {
                    Toast.makeText(MainActivity.this, "Pas de favoris !", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                ChampsListAsyncTask getChamps = new ChampsListAsyncTask();
                getChamps.execute();
                showFavorites.setText("Montrer les favoris");
                favoritesShowed = false;
            }
        });

        listView.setOnItemClickListener((a, v, position, id) -> {
            positionItemClicked = position;
            Object o = listView.getItemAtPosition(position);
            Champ champ = (Champ) o;
            Intent intent = new Intent(MainActivity.this, ChampionActivity.class);
            intent.putExtra("champName", champ.getName());
            launchChampionActivity.launch(intent);
        });

        // Affiche tous les champions
        ChampsListAsyncTask getChamps = new ChampsListAsyncTask();
        getChamps.execute();
    }

    private class ChampsListAsyncTask extends AsyncTask<Void, Void, List<Champ>> {

        @Override
        protected List<Champ> doInBackground(Void... v) {

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
                return null;
            }

            List<Champ> champs = new ArrayList<>();
            String url = "http://ddragon.leagueoflegends.com/cdn/12.12.1/data/fr_FR/champion.json";

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
