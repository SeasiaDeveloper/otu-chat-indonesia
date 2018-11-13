package com.eklanku.otuChat.ui.views.banner;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.utils.Utils;
import com.yyydjk.library.BannerLayout;

public class GlideImageLoader implements BannerLayout.ImageLoader {

    @Override
    public void displayImage(Context context, String s, ImageView imageView) {
        /*Glide.with(context)
                .load(s)
                .into(imageView);*/
        //Glide.with(context).load(s).error(context.getResources().getDrawable(R.drawable.ic_image_broken)).centerCrop().into(imageView);
        if(context == null || context instanceof Activity && Utils.isActivityFinishedOrDestroyed(((Activity)context))){
            return;
        }
        Glide.with(context).load(s).error(context.getResources().getDrawable(R.drawable.otu_ad)).centerCrop().into(imageView);


    }
}
