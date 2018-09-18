package adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.seeaspark.R;
import com.seeaspark.VideoDisplayActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import customviews.TouchImageView;

public class FullViewPagerAdapter extends PagerAdapter {

    Context context;
    int count = 0;
    ArrayList<String> paths;
    String mName, mpic;
    Bitmap bm;
    int mScreenWidth;
    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(false).cacheOnDisk(true)
            .resetViewBeforeLoading(true).considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    public FullViewPagerAdapter(Context con, int count, ArrayList<String> paths, int width, String name, String pic) {
        context = con;
        this.count = count;
        this.paths = paths;
        mScreenWidth = width;
        mName = name;
        mpic = pic;
    }

    @Override
    public View instantiateItem(final ViewGroup container, final int index) {

        if (paths.get(index).toLowerCase(Locale.US).contains(".jpg")
                || paths.get(index).toLowerCase(Locale.US).contains(".jpeg")
                || paths.get(index).toLowerCase(Locale.US).contains(".png")
                || paths.get(index).toLowerCase(Locale.US).contains(".bmp")) {

            TouchImageView img = new TouchImageView(container.getContext());
            Uri imageUri = Uri.fromFile(new File(paths.get(index)));
            if (imageUri != null) {
                imageLoader.displayImage(imageUri.toString(), img, options,
                        new ImageLoadingListener() {

                            @Override
                            public void onLoadingStarted(String arg0, View arg1) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onLoadingFailed(String arg0, View arg1,
                                                        FailReason arg2) {
                                // TODO Auto-generated method stub
                                Toast.makeText(context, context.getResources().getString(R.string.unable_to_load),
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onLoadingComplete(String arg0,
                                                          View arg1, Bitmap arg2) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onLoadingCancelled(String arg0,
                                                           View arg1) {
                                // TODO Auto-generated method stub

                            }
                        });
            }
            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            return img;
        } else {

            View rootView = LayoutInflater.from(context).inflate(R.layout.item_video_view, container, false);

            ImageView image = (ImageView) rootView.findViewById(R.id.imageView);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mScreenWidth / 6, mScreenWidth / 6);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            LinearLayout llVideo = (LinearLayout) rootView.findViewById(R.id.llVideo);
            llVideo.setLayoutParams(lp);

            ImageView video = (ImageView) rootView.findViewById(R.id.imgVideo);

            try {
                bm = ThumbnailUtils.createVideoThumbnail(paths.get(index), MediaStore.Video.Thumbnails.MINI_KIND);
                image.setImageBitmap(bm);
                llVideo.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent in = new Intent(context, VideoDisplayActivity.class);
                        in.putExtra("video_path", paths.get(index));
                        in.putExtra("pic", "" + mpic);
                        in.putExtra("name", "" + mName);
                        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(in);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            container.addView(rootView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            return rootView;
        }
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return count;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
