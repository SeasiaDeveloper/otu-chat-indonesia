package com.eklanku.otuChat.ui.views.banner;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yyydjk.library.BannerLayout;

public class GlideImageLoader implements BannerLayout.ImageLoader {
    @Override
    public void displayImage(Context context, String s, ImageView imageView) {
        Glide.with(context)
                .load(s)
                .into(imageView);
    }
}
