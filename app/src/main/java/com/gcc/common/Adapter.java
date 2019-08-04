package com.gcc.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;



/**
 * A Generic class for Recycler Adapter
 * @param <M>   Model type
 * @param <H>   Holder type
 */
public class Adapter<M extends Model, H extends
        Holder<M>> extends RecyclerView.Adapter<H> {

    public static <M extends Model, H extends Holder<M>>
    Builder<M,H> newBuilder(Context context) {
        return new Builder<>(context);
    }

    private Context context;
    private ArrayList<M> models
            = new ArrayList<>();
    private OnItemClickListener<H> listener;
    private Map<Integer, Integer> layouts ;
    private Class<H> holderClass;
    private RecyclerView.AdapterDataObserver adapterObserver;
    private Boolean always;
    private Integer anim, old = -1;


    private Adapter(Builder<M,H> builder) {
        this.context = builder.context;
        this.listener = builder.listener;
        this.layouts = builder.layouts;
        this.holderClass = builder.holderClass;
        this.adapterObserver = builder.adapterObserver;
        this.always = builder.always;
        this.anim = builder.anim;

    }

    @Override
    public void onAttachedToRecyclerView(
            @NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (adapterObserver != null) {
            registerAdapterDataObserver(adapterObserver);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(
            @NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (adapterObserver != null) {
            unregisterAdapterDataObserver(adapterObserver);
        }
    }

    @NonNull
    @Override
    public H onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {

        Integer resId = layouts.get(type);

        if (resId == null) {
            throw new RuntimeException("No layout found for view type : " + type);
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(resId, viewGroup, false);
        final H holder;
        try {
            Constructor<H> mConstructor = holderClass.getConstructor(View.class);
            holder = mConstructor.newInstance(layout);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("NoSuchMethodException : \n"
                    + e.getMessage());
        } catch (
                IllegalAccessException |
                InstantiationException |
                InvocationTargetException e) {
            throw new RuntimeException("IllegalAccessException | " +
                    "InstantiationException | " +
                    "InvocationTargetException : \n"
                    + e.getMessage());
        }
        holder.initHolder(layout);
        if (listener != null) {
            for (View delegate : holder.getTargets()) {
                delegate.setOnClickListener(param->listener.onItemClick(holder, delegate));
            }
        }
        return holder;
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull H holder, int position) {
        M model = models.get(position);
        holder.updateModel(model);
        holder.updateHolder(model);

        if (anim != -1) {
            if (always) {
                Animation animation = AnimationUtils.loadAnimation(this.context, this.anim);
                holder.itemView.startAnimation(animation);
            } else {
                if (old < position) {
                    Animation animation = AnimationUtils.loadAnimation(this.context, this.anim);
                    View delegate = holder.getDelegate();
                    delegate.startAnimation(animation);
                    old = position;
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        M model = models.get(position);
        return model.type;
    }

    @Override
    public int getItemCount() {
        return models.size();
    }


    public void addModel(M model, int position) {
        models.add(position, model);
        notifyItemInserted(position);
    }

    public void addModel(M model) {
        int position = models.size();
        addModel(model, position);
    }

    public void addAll(Iterable<M> iterable) {
        for (M model : iterable) {
            addModel(model);
        }
    }

    public M getModel(int position) {
        return models.get(position);
    }

    public ArrayList<M> getModels() {
        return this.models;
    }

    public void removeModel(int position) {
        models.remove(position);
        notifyItemRemoved(position);
    }

    public void removeModel(M model) {
        int position = models.indexOf(model);
        if (position == -1) {
            throw new IndexOutOfBoundsException("trying to "
                    + "remove model which is not present");
        }
        removeModel(position);
    }

    public void removeAll() {
        int length = models.size();
        models.clear();
        notifyItemRangeRemoved(0, length);
    }

    public void sort(Comparator<M> comparator) {
        Collections.sort(models, comparator);
        notifyDataSetChanged();
    }

    public static class Builder<M extends Model,H extends Holder<M>> {

        private Context context;
        private OnItemClickListener<H> listener;
        private Map<Integer, Integer> layouts ;
        private Class<H> holderClass;
        private AdapterDataObserver adapterObserver;
        private Boolean always = false;
        private Integer anim = -1;

        private Builder(Context context){
            this.context = context;
        }

        public Builder<M,H> listener(OnItemClickListener<H> listener) {
            this.listener = listener;
            return this;
        }

        public Builder<M,H> layouts(Map<Integer, Integer> layouts) {
            this.layouts = layouts;
            return this;
        }

        public Builder<M,H> holderClass(Class<H> holderClass) {
            this.holderClass = holderClass;
            return this;
        }

        public Builder<M,H> observer(AdapterDataObserver adapterObserver) {
            this.adapterObserver = adapterObserver;
            return this;
        }

        public Builder<M,H> animation(Integer anim, boolean always) {
            this.anim = anim;
            this.always = always;
            return this;
        }


        public Adapter<M,H> build() {
            Util.notNull(context, "Context");
            Util.notNull(layouts, "layouts mapper");
            Util.notNull(holderClass, "Holder Class");
            return new Adapter<>(this);
        }
    }
}
