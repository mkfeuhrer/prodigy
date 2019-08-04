package com.gcc;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.AppCompatTextView;

import com.gcc.common.Arguments;
import com.gcc.common.Holder;
import com.gcc.common.Util;

import de.hdodenhof.circleimageview.CircleImageView;

public class ResultHolder extends Holder<ResultModel> {

    private AppCompatImageView image;
    private AppCompatTextView brand, title, price;
    private AppCompatRatingBar rating;
    public ResultHolder(@NonNull View delegate) {
        super(delegate);
    }

    @Override
    public void initHolder(View delegate) {
        image = delegate.findViewById(R.id.img);
        brand = delegate.findViewById(R.id.brand);
        title = delegate.findViewById(R.id.title);
        price = delegate.findViewById(R.id.price);
        rating = delegate.findViewById(R.id.rating);
    }

    @Override
    public void updateHolder(ResultModel model) {
        Context context = image.getContext();
        Util.glide(context, model.thumbnail, image);
        title.setText(model.title);
        brand.setText(model.brand);
        price.setText(model.mrp + "");
        rating.setRating((int)Float.parseFloat(model.no_ratings));
    }
}
