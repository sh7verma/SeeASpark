package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;


import com.seeaspark.CommunityDetailActivity;
import com.seeaspark.FullViewImageActivity;
import com.seeaspark.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import customviews.TouchImageView;

import models.ImageModel;
import models.PostModel;
import utils.Utils;


public class FullImageAdapter extends PagerAdapter {
    Context mContext;
    private ArrayList<PostModel.ResponseBean.ImagesBean> mData;
    private LayoutInflater inflater;
    Utils utils;
    int mWidth;
    int mPath;

    public FullImageAdapter(Context mContext, ArrayList<PostModel.ResponseBean.ImagesBean> mData, int mPath) {
        this.mContext = mContext;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        utils = new Utils(mContext);
        this.mData = mData;
        this.mPath = mPath;
        this.mWidth = utils.getInt("width", 0);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        View itemView = inflater.inflate(R.layout.item_fullview, container, false);
        final TouchImageView imgPic = (TouchImageView) itemView.findViewById(R.id.imgPic);

        if (mPath == 1)
            Picasso.with(mContext).load(mData.get(position).getImage_url()).
                    centerCrop().resize(mWidth, (int) mContext.getResources().getDimension(R.dimen._190sdp)).
                    into(imgPic);
        else
            imgPic.setImageBitmap(getBitmapFromURL(mData.get(position).getImage_url()));

        imgPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPath == 1) {
                    ((CommunityDetailActivity) mContext).moveToFullView(imgPic,position,mData);

                }
            }
        });

        ((ViewPager) container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap1 = BitmapFactory.decodeStream(input);
            return myBitmap1;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
