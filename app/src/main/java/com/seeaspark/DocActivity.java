package com.seeaspark;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;

/**
 * Created by dev on 28/8/18.
 */

public class DocActivity extends BaseActivity {

    String docUrl, name, mPic;
    LinearLayout llOuterDoc;
    ImageView imgBack, imgProfileAvatar;
    TextView txtName;
    WebView wvDoc;
    ProgressBar pbHeaderProgress;

    @Override
    public int getContentView() {
        return R.layout.activity_doc;
    }

    @Override
    public void initUI() {
        docUrl = getIntent().getStringExtra("url");
        name = getIntent().getStringExtra("name");
        mPic = getIntent().getStringExtra("pic");

        llOuterDoc = (LinearLayout) findViewById(R.id.llOuterDoc);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgProfileAvatar = (ImageView) findViewById(R.id.imgProfileAvatar);
        txtName = (TextView) findViewById(R.id.txtName);
        wvDoc = (WebView) findViewById(R.id.wvDoc);
        pbHeaderProgress = (ProgressBar) findViewById(R.id.pbHeaderProgress);

        txtName.setText(name);
        Picasso.with(this).load(mPic).placeholder(R.drawable.placeholder_image).into(imgProfileAvatar);
    }

    @Override
    public void displayDayMode() {
        llOuterDoc.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color));
        txtName.setTextColor(ContextCompat.getColor(this, R.color.black_color));
    }

    @Override
    public void displayNightMode() {
        llOuterDoc.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color));
        txtName.setTextColor(ContextCompat.getColor(this, R.color.white_color));
    }

    @Override
    public void onCreateStuff() {
        try {
            wvDoc.getSettings().setJavaScriptEnabled(true);
            wvDoc.getSettings().setAllowFileAccess(true);
            wvDoc.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            wvDoc.setWebViewClient(new MyBrowser());
//            docUrl = URLEncoder.encode(docUrl, "UTF-8");
            wvDoc.loadUrl("http://docs.google.com/gview?embedded=true&url=" + docUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initListener() {
        imgBack.setOnClickListener(this);
    }

    @NotNull
    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                moveBack();
                break;
        }
    }

    private void moveBack() {
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        moveBack();
    }

    private class MyBrowser extends WebViewClient {

        public MyBrowser() {

        }

        @Override
        public void onLoadResource(WebView view, String url) {

            super.onLoadResource(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            pbHeaderProgress.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            pbHeaderProgress.setVisibility(View.GONE);
        }
    }
}
