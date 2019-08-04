package com.gcc.common;

import android.view.View;

/**
 * Function Interface for listening click event
 * @param <H> type of Holder Class
 */
@FunctionalInterface
public interface OnItemClickListener<H extends Holder<?>>  {

    /**
     * CallBack function when a view is clicked
     * @param holder holder object
     * @param delegate target view which was clicked
     */
    public void onItemClick(H holder, View delegate);
}
