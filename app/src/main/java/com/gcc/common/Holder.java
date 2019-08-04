package com.gcc.common;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class Holder<M extends Model>
        extends RecyclerView.ViewHolder {

    private M model;
    public Holder(@NonNull View delegate) {
        super(delegate);
    }

    public abstract void initHolder(View delegate);

    public View [] getTargets() {
        return Arguments.args(itemView);
    }

    public final void updateModel(M model) {
        this.model = model;
    }

    public abstract void updateHolder(M model);

    public M getModel() {
        return this.model;
    }

    public View getDelegate() {
        return this.itemView;
    }
}
