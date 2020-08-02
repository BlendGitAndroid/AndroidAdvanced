package com.blend.architecture.glide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blend.architecture.R;
import com.blend.architecture.glide.glide.Glide;

public class GlideSecondActivity extends AppCompatActivity {

    String[] url = {"https://tse3-mm.cn.bing.net/th/id/OIP.Gzze2RWjGPoKUivyJQvTrQHaE7?pid=Api&rs=1",
            "https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg",
            "https://wallpapercave.com/wp/S4qhtj3.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide_second);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ImageAdapter adapter = new ImageAdapter();
        recyclerView.setAdapter(adapter);
    }

    private final class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.glide_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.title.setText(
                    String.valueOf(position));
            holder.imageView.setTag(position);
            Glide.with(GlideSecondActivity.this)
                    .load(url[position % url.length])
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return 100;
        }

        public final class ViewHolder extends RecyclerView.ViewHolder {

            private final ImageView imageView;
            private final TextView title;

            ViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.text);
                imageView = itemView.findViewById(R.id.icon);
            }
        }
    }
}