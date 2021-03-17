package com.blend.ui.judge_nested_scroll;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blend.ui.R;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private static final String TAG = "ArticleAdapter";

    private List<ArticleBean> mArticleBeans;

    public ArticleAdapter(List<ArticleBean> arrayList) {
        mArticleBeans = arrayList;
    }


    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_article_item, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder holder, int position) {
        ArticleBean articleBean = mArticleBeans.get(position);
        holder.mTextView.setText(articleBean.getContent());
    }

    @Override
    public int getItemCount() {
        return mArticleBeans.size();
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.articleTitleTv);
        }
    }
}
