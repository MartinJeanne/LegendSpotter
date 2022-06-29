package garwalle.legendspotter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

public class CustomListAdapter  extends BaseAdapter {

    private List<Champ> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomListAdapter(Context aContext,  List<Champ> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.iconView = (ImageView) convertView.findViewById(R.id.imgChamp);
            holder.nameView = (TextView) convertView.findViewById(R.id.tvChamp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Champ champ = this.listData.get(position);
        setChampImgByName(holder.iconView, champ.getName());
        holder.nameView.setText(champ.getName());

        return convertView;
    }

    private void setChampImgByName(ImageView iconView, String name)  {
        String url = "http://ddragon.leagueoflegends.com/cdn/12.12.1/img/champion/"+ name +".png";
        int maxWidth = 500;
        int maxHeight = 500;

        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                iconView.setImageBitmap(response);
            }
        }, maxWidth, maxHeight, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(MainActivity.this, ""+error, Toast.LENGTH_LONG).show();
                Log.v("--error", "Error sur la reponse pour l'image");
            }
        });
        SingletonRequestQueue.getInstance(this.context).addToRequestQueue(imageRequest);
    }

    static class ViewHolder {
        ImageView iconView;
        TextView nameView;
    }
}