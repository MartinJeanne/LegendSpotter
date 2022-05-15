package garwalle.legendspotter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    TextView tv_joke;
    Button btn_joke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_joke = findViewById(R.id.tv_joke);
        btn_joke = findViewById(R.id.btn_joke);
        btn_joke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChuckNorrisJoke();
            }
        });
    }


    public void getChuckNorrisJoke() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ddragon.leagueoflegends.com/cdn/12.9.1/data/en_US/champion/Aatrox.json";
        Log.v("--volley", "index");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("--volley", "onResponse");
                tv_joke.setText("Response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("--volley", "onErrorResponse");
                tv_joke.setText("Error: " + error.toString());
            }
        });
        queue.add(jsonObjectRequest);
    }
}
