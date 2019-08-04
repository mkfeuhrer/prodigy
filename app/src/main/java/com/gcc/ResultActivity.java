package com.gcc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gcc.common.Adapter;
import com.gcc.common.Callable;
import com.gcc.common.OnItemClickListener;
import com.gcc.common.Util;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Request;
import okhttp3.Response;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initialize();
    }

    private void initialize() {


        Util.setStatusBarResource(this, R.color.colorPrimaryDark);
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setNavigationOnClickListener(param -> finish());

        @SuppressLint("UseSparseArrays")
        HashMap<Integer, Integer> layouts = new HashMap<>();
        layouts.put(0, R.layout.item_result);
        OnItemClickListener<ResultHolder> listener
                = (holder, delegate) -> {
            ResultModel model = holder.getModel();
            BottomSheetDialog dialog = new BottomSheetDialog(this);
            dialog.setContentView(R.layout.sheet_result);

            CircleImageView image = dialog.findViewById(R.id.img);
            Util.glide(this, model.thumbnail, image);
            int [] ids = {R.id.title, R.id.country, R.id.brand, R.id.color, R.id.price, R.id.rating, R.id.url, R.id.description};
            String [] values = {model.title, model.country, model.brand, model.color, model.available_price + "", model.no_ratings + "", model.url, model.description};
            for (int count = 0; count < values.length ; count ++) {
                AppCompatTextView text = dialog.findViewById(ids[count]);
                assert text != null;
                text.setText(values[count]);
            }
            LinearLayoutCompat go = dialog.findViewById(R.id.go);
            assert go != null;
            go.setOnClickListener(param-> {
                try {
                    Uri uri = Uri.parse(model.url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "cannot open", Toast.LENGTH_SHORT).show();
                }

            });
            dialog.show();
        };

        Adapter<ResultModel, ResultHolder> adapter
                = Adapter.<ResultModel, ResultHolder>newBuilder(this)
                .holderClass(ResultHolder.class)
                .layouts(layouts)
                .listener(listener)
                .animation(R.anim.fade_in, false)
                .build();

        RecyclerView.LayoutManager layoutManager
                = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RecyclerView list = findViewById(R.id.results);
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);


        SwipyRefreshLayout swipe = findViewById(R.id.swipe);
//
//        Intent intent = getIntent();
//        String keyword = intent.getStringExtra("KEYWORD");

        Query query = new Query("");
        Filter filter = new Filter("ALL", "ALL", "ALL", 0, 0, 1000000);

        Callable<Query> callable = param -> {
            swipe.setRefreshing(true);
            String URL = "http://" + "10.0.2.2:8888" + "/query";
            Client client = Client.getClient(this);
            String RESULT_URL = URL;
            RESULT_URL = "http://192.168.43.236:5000/search/" + param.keyword + "?limit=" + param.limit + "&offset=" + param.offset;
            Log.e("REQUEST", RESULT_URL);
            Request request = new Request.Builder().url(RESULT_URL).get().build();
            client.execute(request, (response, exp, event) -> {
                swipe.setRefreshing(false);
                if (event == Client.Event.FAILURE) {
                    Toast.makeText(this, "request error", Toast.LENGTH_SHORT).show();
                    return;
                }
                Gson gson = new Gson();
                Type type = new TypeToken<List<ResultModel>>(){}.getType();
                List<ResultModel> results = gson.fromJson(response, type);
                if (param.remove) {
                    adapter.removeAll();
                    query.offset = query.limit;
                    query.limit = Query.COUNT_PER_PAGE;
                    param.remove = false;
                } else {
                    query.next();
                }
                for (ResultModel model : results) {
                    if (filter.pass(model)) {
                        adapter.addModel(model);
                        Log.d("TITLE", model.title);
                    }
                    Log.d("ITEM", model.title);
                }

                Log.e("COUNT", adapter.getItemCount() + "");

            });
        };

        SearchView search = findViewById(R.id.search);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String input) {
                adapter.removeAll();
                query.keyword = input;
                query.reset();
                callable.call(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        swipe.setOnRefreshListener(direction -> {
            callable.call(query);
        });
//        callable.call(query);

        LinearLayoutCompat llc = findViewById(R.id.filter);
        llc.setOnClickListener(param -> {

            BottomSheetDialog bottom = new BottomSheetDialog(this);
            bottom.setContentView(R.layout.sheet_filter);

            @SuppressLint("UseSparseArrays")
            HashMap<Integer, Integer> catLayouts = new HashMap<>();
            catLayouts.put(0, R.layout.item_category);
            OnItemClickListener<CatHolder> catListener
                    = (holder, delegate) -> {
                CatModel model = holder.getModel();
                AppCompatTextView cat = bottom.findViewById(R.id.category);
                assert cat != null;
                cat.setText(model.title);
                filter.cat = model.title;
            };

            Adapter<CatModel, CatHolder> catAdapter
                    = Adapter.<CatModel, CatHolder>newBuilder(this)
                    .holderClass(CatHolder.class)
                    .layouts(catLayouts)
                    .listener(catListener)
                    .animation(R.anim.bottom_in, false)
                    .build();

            RecyclerView.LayoutManager catManager
                    = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
            RecyclerView cats = bottom.findViewById(R.id.categories);
            assert cats != null;
            cats.setLayoutManager(catManager);
            cats.setAdapter(catAdapter);

            HashMap<Integer, Integer> brandLayouts = new HashMap<>();
            brandLayouts.put(0, R.layout.item_brand);
            OnItemClickListener<BrandHolder> brandListener
                    = (holder, delegate) -> {
                BrandModel model = holder.getModel();
                AppCompatTextView brand = bottom.findViewById(R.id.brand);
                assert brand != null;
                brand.setText(model.title);
                filter.brand = model.title;
            };

            Adapter<BrandModel, BrandHolder> brandAdapter
                    = Adapter.<BrandModel, BrandHolder>newBuilder(this)
                    .holderClass(BrandHolder.class)
                    .layouts(brandLayouts)
                    .listener(brandListener)
                    .animation(R.anim.bottom_in, false)
                    .build();

            RecyclerView.LayoutManager brandManager
                    = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
            RecyclerView brands = bottom.findViewById(R.id.brands);
            assert brands != null;
            brands.setLayoutManager(brandManager);
            brands.setAdapter(brandAdapter);

            HashMap<Integer, Integer> colorLayouts = new HashMap<>();
            colorLayouts.put(0, R.layout.item_color);
            OnItemClickListener<ColorHolder> colorListener
                    = (holder, delegate) -> {
                ColorModel model = holder.getModel();
                AppCompatTextView color = bottom.findViewById(R.id.color);
                assert color != null;
                color.setText(model.title);
                filter.color = model.title;
            };

            Adapter<ColorModel, ColorHolder> colorAdapter
                    = Adapter.<ColorModel, ColorHolder>newBuilder(this)
                    .holderClass(ColorHolder.class)
                    .layouts(colorLayouts)
                    .listener(colorListener)
                    .animation(R.anim.bottom_in, false)
                    .build();

            RecyclerView.LayoutManager colorManager
                    = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
            RecyclerView colors = bottom.findViewById(R.id.colors);
            assert colors != null;
            colors.setLayoutManager(colorManager);
            colors.setAdapter(colorAdapter);

            String url = "https://images.pexels.com/photos/1552224/pexels-photo-1552224.jpeg?crop=entropy&cs=srgb&dl=balloons-daylight-dji-1552224.jpg&fit=crop&fm=jpg&h=359&w=640";
            catAdapter.addModel(new CatModel("ALL", url));
            brandAdapter.addModel(new BrandModel("ALL", url));
            colorAdapter.addModel(new ColorModel("ALL", url));

            Set<String> catSet = new HashSet<>();
            Set<String> brandSet = new HashSet<>();
            Set<String> colorSet = new HashSet<>();

            for (ResultModel model : adapter.getModels()) {
                catSet.add(model.subcategory);
                brandSet.add(model.brand);
                colorSet.add(model.color);
            }

            for (String cat : catSet) {
                catAdapter.addModel(new CatModel(cat, "some url of title"));
            }
            for (String brand : brandSet) {
                brandAdapter.addModel(new BrandModel(brand, "some url of brand"));
            }
            for (String color : colorSet) {
                colorAdapter.addModel(new ColorModel(color, "some url of title"));
            }

            LinearLayoutCompat cancel = bottom.findViewById(R.id.cancel);
            LinearLayoutCompat submit = bottom.findViewById(R.id.submit);
            assert cancel != null;
            assert submit != null;
            cancel.setOnClickListener(params -> {
                bottom.dismiss();
            });

            submit.setOnClickListener(params -> {
                AppCompatRatingBar rating = bottom.findViewById(R.id.rating);
                AppCompatEditText lowInput = bottom.findViewById(R.id.low);
                AppCompatEditText highInput = bottom.findViewById(R.id.high);
                assert rating != null;
                assert lowInput != null;
                assert highInput != null;

                String lowT = lowInput.getText().toString();
                String highT = highInput.getText().toString();

                if (lowT.isEmpty() || highT.isEmpty()) {
                    Toast.makeText(this, "invalid price range", Toast.LENGTH_SHORT).show();
                    return;
                }

                filter.rate = (int) rating.getRating();

                int low = Integer.parseInt(lowT);
                int high = Integer.parseInt(highT);

                if (low > high) {
                    Toast.makeText(this, "invalid price range", Toast.LENGTH_SHORT).show();
                    return;
                }

                filter.low = low;
                filter.high = high;
                query.remove = true;
                Log.d("offset", query.offset + " " + query.limit);
                query.limit = query.offset;
                query.offset = 0;
                callable.call(query);
                bottom.dismiss();
            });

            bottom.show();
        });
        LinearLayoutCompat sort = findViewById(R.id.sort);
        sort.setOnClickListener(param -> {
            BottomSheetDialog dialog = new BottomSheetDialog(this);
            dialog.setContentView(R.layout.sheet_sort);
            RadioGroup fields = dialog.findViewById(R.id.fields);
            RadioGroup order = dialog.findViewById(R.id.order);
            LinearLayoutCompat cancel = dialog.findViewById(R.id.cancel);
            LinearLayoutCompat submit = dialog.findViewById(R.id.submit);
            assert fields != null;
            assert order != null;
            assert cancel != null;
            assert submit != null;
            cancel.setOnClickListener(params->dialog.dismiss());
            submit.setOnClickListener(params-> {
                int fid = fields.getCheckedRadioButtonId();
                int oid = order.getCheckedRadioButtonId();

                int oct = oid == R.id.ASC ? 1 : -1;
                if (fid == R.id.PRICE) {
                    Log.d("SORT", "PRICE");
                    adapter.sort(new Comparator<ResultModel>() {
                        @Override
                        public int compare(ResultModel a, ResultModel b) {
                            double ra = Double.parseDouble(a.mrp);
                            double rb = Double.parseDouble(b.mrp);
                            Log.d("a b", ra + " "  + rb);
                            int result = (int)(ra - rb);
                            Log.d("COMP", result + "");
                            return result * oct;
                        }
                    });
                } else {
                    Log.d("SORT", "RATING");
                    adapter.sort(new Comparator<ResultModel>() {
                        @Override
                        public int compare(ResultModel a, ResultModel b) {
                            int result = (int)(Double.parseDouble(a.no_ratings) - Double.parseDouble(b.no_ratings));
                            Log.d("COMP", result + "");
                            return result * oct;
                        }
                    });
                }
                dialog.dismiss();
            });
            dialog.show();
        });
    }

    public static class Query {

        public static int COUNT_PER_PAGE = 10;
        public String keyword;
        public int limit, offset;
        public boolean remove = false;
        public Query(String keyword) {
            this.keyword = keyword;
            this.limit = COUNT_PER_PAGE;
            this.offset = 0;
        }

        public void next() {
            this.offset = this.offset + COUNT_PER_PAGE;
        }

        public void reset() {
            this.limit = COUNT_PER_PAGE;
            this.offset = 0;
        }

    }

    public static class Filter {
        public String cat, brand, color;
        public int rate, low, high;

        public Filter(String cat, String brand, String color, int rate, int low, int high) {
            this.cat = cat;
            this.brand = brand;
            this.color = color;
            this.rate = rate;
            this.low = low;
            this.high = high;
        }

        public Filter() {
        }

        public boolean pass(ResultModel model) {
            Log.d("rating", this.rate + " " + model.mrp);
            return (this.cat.equals(model.subcategory) || this.cat.equals(model.bundle_subcategory) || this.cat.equals("ALL"))
            && (this.rate <= (int)Float.parseFloat(model.no_ratings) || this.rate == 0 )
            && (this.brand.equals(model.brand) || this.brand.equals("ALL"))
            && (this.color.equals(model.color) || this.color.equals("ALL"))
            && ((this.high >= (int)Float.parseFloat(model.available_price) && this.low <= (int)Float.parseFloat(model.available_price)) || (int)Float.parseFloat(model.available_price) == -1);
        }
    }

}
