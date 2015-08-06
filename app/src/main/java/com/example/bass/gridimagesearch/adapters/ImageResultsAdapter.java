package com.example.bass.gridimagesearch.adapters;

import android.content.Context;
import android.text.Html;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.example.bass.gridimagesearch.R;
import com.example.bass.gridimagesearch.models.ImageResult;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

/**
 * Created by bass on 2015/7/31.
 */
public class ImageResultsAdapter extends ArrayAdapter<ImageResult> {
    private static class ViewHolder {
        DynamicHeightImageView ivImage;
        TextView  tvTitle;


    }
    private final Random mRandom;
    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();

    public ImageResultsAdapter(Context context, List<ImageResult> images) {
        super(context, android.R.layout.simple_list_item_1,images);
        this.mRandom = new Random();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
// Get the data item for this position
        ImageResult imageInfo = getItem(position);
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_image_result, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivImage = (DynamicHeightImageView)convertView.findViewById(R.id.ivImage);
            viewHolder.tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvTitle.setText(Html.fromHtml(imageInfo.title));

        viewHolder.ivImage.setImageResource(0);
        double positionHeight = getPositionRatio(position);
        viewHolder.ivImage.setHeightRatio(positionHeight);

        Picasso.with(getContext()).load(imageInfo.thumbUrl).fit().centerInside().placeholder(R.drawable.loading).error(R.drawable.fail).into(viewHolder.ivImage);
        return convertView;

    }
    private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);
        // if not yet done generate and stash the columns height
        // in our real world scenario this will be determined by
        // some match based on the known height and width of the image
        // and maybe a helpful way to get the column height!
        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);

        }
        return ratio;
    }

    private double getRandomHeightRatio() {
        return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5
        // the width
    }
}
