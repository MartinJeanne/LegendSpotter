package garwalle.legendspotter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChampionActivity extends AppCompatActivity {

    TextView tvChampName;
    TextView tvChampSummary;
    TextView tvNumSkin;
    ImageView ivChampImg;
    ProgressBar pbLoadSpinner;
    String champName;
    ArrayList<Integer> skinsNumber = new ArrayList<Integer>();
    int skinsIterator = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion);

        tvChampName = findViewById(R.id.tvChampName);
        tvChampSummary = findViewById(R.id.tvChampSummary);
        pbLoadSpinner = findViewById(R.id.pbLoadSpinner);
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

        Intent intent = getIntent();
        champName = intent.getStringExtra("champName");
        tvChampName.setText(champName);

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

            }
        });

        SingletonRequestQueue.getInstance(this).addToRequestQueue(champInfo);
        loadImg(skinsIterator);
    }

    private void loadImg(int skinNumber) {
        String champImageUrl = "http://ddragon.leagueoflegends.com/cdn/img/champion/splash/" + champName + "_" + skinNumber + ".jpg";

        ImageRequest champImage = new ImageRequest(champImageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                ivChampImg.setImageBitmap(response);
                pbLoadSpinner.setVisibility(View.INVISIBLE);
            }
        }, 3000, 3000, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("--error", "Erreur requête images : " + error.toString());
            }
        });

        SingletonRequestQueue.getInstance(this).addToRequestQueue(champImage);
    }

    private void switchSkin() {
        pbLoadSpinner.setVisibility(View.VISIBLE);
        if (skinsIterator + 1 < skinsNumber.size()) skinsIterator++;
        else skinsIterator = 0;
        loadImg(skinsNumber.get(skinsIterator));
        tvNumSkin.setText("Style : " + (skinsIterator + 1) + "/" + skinsNumber.size());
    }
}