package garwalle.legendspotter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class ChampionActivity extends AppCompatActivity {

    TextView tvChampName;
    TextView tvChampSummary;
    ImageView ivChampImg;
    String champName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion);

        tvChampName = findViewById(R.id.tvChampName);
        tvChampSummary = findViewById(R.id.tvChampSummary);
        ivChampImg = findViewById(R.id.ivChampImg);

        Intent intent = getIntent();
        champName = intent.getStringExtra("champName");
        tvChampName.setText(champName);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
            return;
        }

        String champImageUrl = "http://ddragon.leagueoflegends.com/cdn/img/champion/splash/" + champName + "_0.jpg";

        ImageRequest champImage = new ImageRequest(champImageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                ivChampImg.setImageBitmap(response);
            }
        }, 3000, 3000, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("--error", "Erreur requête images : " + error.toString());
            }
        });


        String champInfoUrl = "http://ddragon.leagueoflegends.com/cdn/12.12.1/data/fr_FR/champion/" + champName + ".json";

        JsonObjectRequest champInfo = new JsonObjectRequest(Request.Method.GET, champInfoUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String lore = "Vide";
                try {
                     lore = response.getJSONObject("data").getJSONObject(champName).getString("lore");
                    Log.v("--lore", lore);
                } catch (JSONException e) {
                    Log.v("--error", "Error : " + e);
                }
                tvChampSummary.setText(lore);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        SingletonRequestQueue.getInstance(this).addToRequestQueue(champImage);
        SingletonRequestQueue.getInstance(this).addToRequestQueue(champInfo);
    }
}