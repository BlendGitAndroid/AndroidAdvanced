package com.blend.ui.judge_nested_scroll;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blend.ui.R;

import java.util.List;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureHolder> {

    private List<PictureBean> mPictureBeans;

    public PictureAdapter(List<PictureBean> beans) {
        mPictureBeans = beans;
    }

    @Override
    public PictureHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_picture_item, parent, false);
        return new PictureHolder(view);
    }

    @Override
    public void onBindViewHolder(PictureHolder holder, int position) {
        PictureBean pictureBean = mPictureBeans.get(position);
        holder.mImageView.setImageResource(pictureBean.picture);
        holder.mTextView.setText(pictureBean.content);
    }

    @Override
    public int getItemCount() {
        return mPictureBeans.size();
    }

    static class PictureHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private TextView mTextView;

        public PictureHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.pictureIv);
            mTextView = itemView.findViewById(R.id.pictureTitleTv);
        }
    }

}
