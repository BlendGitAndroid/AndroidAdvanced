package com.blend.ui.qq_header_scrollerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.blend.ui.R;

public class QQHeaderActivity extends AppCompatActivity {

    private QQHeaderScrollView mQQHeaderScrollView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_q_header);
        mQQHeaderScrollView = findViewById(R.id.qqHeaderScrollView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                new String[]{"星期一 	和马云洽谈",
                        "星期二	约见李彦宏",
                        "星期三 	约见乔布斯",
                        "星期四 	和Lance钓鱼",
                        "星期五 	和Jett洽谈",
                        "星期六 	和Jason洽谈",
                        "星期日 	和MZ洽谈",
                        "星期一 	和马云洽谈",
                        "星期二	约见李彦宏",
                        "星期三 	约见乔布斯",
                        "星期四 	和Ricky钓鱼",
                        "星期五 	和David洽谈",
                        "星期六 	和Jason洽谈",
                        "星期日 	和MZ洽谈",
                        "……"
                });
        View header = View.inflate(this, R.layout.qq_list_header, null);
        mImageView = header.findViewById(R.id.qqHeaderIv);
        mQQHeaderScrollView.setZoomImageView(mImageView);
        mQQHeaderScrollView.addHeaderView(header);
        mQQHeaderScrollView.setAdapter(adapter);
    }
}
