package garwalle.legendspotter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChampionActivity extends AppCompatActivity {

    TextView tvChampName;
    TextView tvChampSummary;
    TextView tvNumSkin;
    ImageView ivChampImg;
    ProgressBar pbLoadSpinner;
    TextView tvInfoClickImg;
    ImageButton btAddToFavorite;

    String champName;
    ArrayList<Integer> skinsNumber = new ArrayList<Integer>();
    int skinsIterator = 0;
    boolean originalFavorite;
    boolean currentFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion);

        Intent intent = getIntent();

        tvChampName = findViewById(R.id.tvChampName);
        tvChampSummary = findViewById(R.id.tvChampSummary);
        pbLoadSpinner = findViewById(R.id.pbLoadSpinner);
        tvInfoClickImg = findViewById(R.id.tvInfoClickImg);
        tvNumSkin = findViewById(R.id.tvNumSkin);
        tvNumSkin.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) { switchSkin(); }
        });

        ivChampImg = findViewById(R.id.ivChampImg);
        ivChampImg.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) { switchSkin(); }
        });

        btAddToFavorite = findViewById(R.id.btAddToFavorite);
        btAddToFavorite.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbHelper dbHelper = new DbHelper(ChampionActivity.this);
                // On ajoute le champion aux favoris, si l'est déjà on l'enlève
                currentFavorite = dbHelper.switchFavorite(champName);
                // En fonction du résultat du "switch" précédent, on informe l'utilisateur
                if (currentFavorite) {
                    btAddToFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                    Toast.makeText(ChampionActivity.this, champName + " ajouté aux favoris !", Toast.LENGTH_SHORT).show();
                }
                else {
                    btAddToFavorite.setImageResource(android.R.drawable.btn_star_big_off);
                    Toast.makeText(ChampionActivity.this, champName + " supprimé des favoris !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        champName = intent.getStringExtra("champName");
        tvChampName.setText(champName);

        // Requêtes
        loadInfo();
        loadImg(skinsIterator);

        // Affiche une étoile rempli si le champion est en favoris
        DbHelper dbHelper = new DbHelper(ChampionActivity.this);
        originalFavorite = dbHelper.isChampFavorite(champName);
        if (originalFavorite) btAddToFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        else btAddToFavorite.setImageResource(android.R.drawable.btn_star_big_off);
    }

    private void loadInfo() {
        String champInfoUrl = "http://ddragon.leagueoflegends.com/cdn/12.12.1/data/fr_FR/champion/" + champName + ".json";

        JsonObjectRequest champInfo = new JsonObjectRequest(Request.Method.GET, champInfoUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String lore = "Pas de description.";
                try {
                    lore = response.getJSONObject("data").getJSONObject(champName).getString("lore");
                    JSONArray skins = response.getJSONObject("data").getJSONObject(champName).getJSONArray("skins");
                    for (int i=0; i < skins.length(); i++) {
                        int skin = skins.getJSONObject(i).getInt("num");
                        skinsNumber.add(skin);
                    }
                } catch (JSONException e) {
                    Log.v("--error", "Error : " + e);
                }
                tvChampSummary.setText(lore);
                tvNumSkin.setText("Style : 1/" + skinsNumber.size());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("--error", "Erreur requête info champion : " + error.toString());
            }
        });

        SingletonRequestQueue.getInstance(this).addToRequestQueue(champInfo);
    }

    private void loadImg(int skinNumber) {
        String champImageUrl = "http://ddragon.leagueoflegends.com/cdn/img/champion/splash/" + champName + "_" + skinNumber + ".jpg";

        ImageRequest champImage = new ImageRequest(champImageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                ivChampImg.setImageBitmap(response);
                pbLoadSpinner.setVisibility(View.INVISIBLE);
            }
        }, 2000, 2000, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("--error", "Erreur requête image : " + error.toString());
            }
        });

        SingletonRequestQueue.getInstance(this).addToRequestQueue(champImage);
    }

    private void switchSkin() {
        if (tvInfoClickImg.getVisibility() == View.VISIBLE) tvInfoClickImg.setVisibility(View.INVISIBLE);
        pbLoadSpinner.setVisibility(View.VISIBLE);
        if (skinsIterator + 1 < skinsNumber.size()) skinsIterator++;
        else skinsIterator = 0;
        loadImg(skinsNumber.get(skinsIterator));
        tvNumSkin.setText("Style : " + (skinsIterator + 1) + "/" + skinsNumber.size());
    }

    @Override
    public void finish() {
        Intent result = new Intent();

        // Si le champion était en favoris et qu'on l'a enlevé, on previent le mainActivity
        if (originalFavorite && !currentFavorite) result.putExtra("favoriteDeleted", true);

        setResult(RESULT_OK, result);
        super.finish();
    }
}