package com.gcc;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.gcc.common.Holder;

import de.hdodenhof.circleimageview.CircleImageView;

public class BrandHolder extends Holder<BrandModel> {

    private CircleImageView img;
    private AppCompatTextView title;
    public BrandHolder(@NonNull View delegate) {
        super(delegate);
    }

    @Override
    public void initHolder(View delegate) {
        img = delegate.findViewById(R.id.img);
        title = delegate.findViewById(R.id.title);
    }

    @Override
    public void updateHolder(BrandModel model) {
//        Util.glide(img.getContext(), model.url, img);
        title.setText(model.title);
    }
}
