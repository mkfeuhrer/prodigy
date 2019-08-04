package com.gcc;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.gcc.common.Holder;

public class HintHolder extends Holder<HintModel> {

    private AppCompatTextView hint;
    public HintHolder(@NonNull View delegate) {
        super(delegate);
    }

    @Override
    public void initHolder(View delegate) {
        hint = delegate.findViewById(R.id.hint);
    }

    @Override
    public void updateHolder(HintModel model) {
        hint.setText(model.hint);
    }
}
