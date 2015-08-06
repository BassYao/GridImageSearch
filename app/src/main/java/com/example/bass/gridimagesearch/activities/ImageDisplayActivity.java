package com.example.bass.gridimagesearch.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.example.bass.gridimagesearch.R;
import com.example.bass.gridimagesearch.models.ImageResult;
import com.ortiz.touch.TouchImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ImageDisplayActivity extends Activity {

    private TouchImageView ivImageResult;
    private ShareActionProvider miShareAction;
    private ImageResult imageResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_display);
        imageResult = ((ImageResult)getIntent().getSerializableExtra("result"));
        String url = imageResult.fullUrl;
        ivImageResult = (TouchImageView) findViewById(R.id.ivImageResult);
        Picasso.with(this).load(url).placeholder(R.drawable.loading)
                .error(R.drawable.fail).into(ivImageResult, new Callback() {
            @Override
            public void onSuccess() {
                setupShareIntent();
            }

            @Override
            public void onError() {
                Toast.makeText(getBaseContext(), "Load Fail!", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_display, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        // Fetch reference to the share action provider
        miShareAction = (ShareActionProvider) item.getActionProvider();

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_zoom) {
            ImageView.ScaleType sType = ivImageResult.getScaleType();
            if(sType == ImageView.ScaleType.CENTER_INSIDE) {
                ivImageResult.setScaleType(ImageView.ScaleType.CENTER);
                item.setTitle(R.string.action_center_inside);
                item.setIcon(R.drawable.ic_action_fit);
            } else {
                ivImageResult.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                item.setTitle(R.string.action_zoom);
                item.setIcon(R.drawable.ic_action_zoom);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void setupShareIntent() {

        Uri bmpUri = getLocalBitmapUri(ivImageResult); // see previous remote images section
        if(bmpUri == null) {
            //Toast.makeText(getBaseContext(), "Image not Loaded!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create share intent as described above
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/*");
        // Attach share event to the menu item provider
        miShareAction.setShareIntent(shareIntent);
    }
    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getLocalBitmapUri(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;

    }
}
