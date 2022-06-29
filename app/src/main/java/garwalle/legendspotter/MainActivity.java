package garwalle.legendspotter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    TextView tv_joke;
    Button btn_joke;
    ImageView image_view;
    Iterator<String> champs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_joke = findViewById(R.id.tv_joke);
        btn_joke = findViewById(R.id.btn_joke);
        image_view = findViewById(R.id.imageView);
        btn_joke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getJSON();
                //getImg();
            }
        });
        //Log.v("--volley", "Coucou");
    }

    public void getJSON() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
            return;
        }


        String url = "http://ddragon.leagueoflegends.com/cdn/12.12.1/data/en_US/champion.json";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    response = response.getJSONObject("data");
                    champs = response.keys();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                while (champs.hasNext()) {
                    Log.v("--champs", champs.next());
                }
                tv_joke.setText(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tv_joke.setText("Error: " + error.toString());
            }
        });
        queue.add(jsonObjectRequest);
    }

    public void getImg() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
            return;
        }

        String url = "http://ddragon.leagueoflegends.com/cdn/12.12.1/img/champion/Aatrox.png";
        RequestQueue queue = Volley.newRequestQueue(this);
        int maxWidth = 1000;
        int maxHeight = 1000;

        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                image_view.setImageBitmap(response);
            }
        }, maxWidth, maxHeight, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, ""+error, Toast.LENGTH_LONG).show();
            }
        });

        queue.add(imageRequest);
    }
}
