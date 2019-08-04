package com.gcc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.gcc.common.Adapter;
import com.gcc.common.Callable;
import com.gcc.common.Model;
import com.gcc.common.OnItemClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        initialize();
    }

    private void initialize() {

        SearchView search = findViewById(R.id.search);

        Intent intents = new Intent(this, ResultActivity.class);
        intents.putExtra("KEYWORD", "samsung");
        startActivity(intents);

        @SuppressLint("UseSparseArrays")
        HashMap<Integer, Integer> layouts = new HashMap<>();
        layouts.put(0, R.layout.item_hint);
        OnItemClickListener<HintHolder> listener
                = (holder, delegate) -> {
            HintModel model = holder.getModel();
            Toast.makeText(this, model.hint, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("KEYWORD", search.getQuery().toString());
            startActivity(intent);
        };

        Adapter<HintModel, HintHolder> adapter
                = Adapter.<HintModel, HintHolder>newBuilder(this)
                .holderClass(HintHolder.class)
                .layouts(layouts)
                .listener(listener)
                .animation(R.anim.bottom_in, false)
                .build();

        RecyclerView.LayoutManager layoutManager
                = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);

        String URL = "http://" + "10.0.2.2:8888" + "/";
        Client client = Client.getClient(this);
        Callable<String> callable = args -> {
            String HINT_URL = URL + "?key=" + args;
            Request request = new Request.Builder().url(HINT_URL).get().build();
            client.execute(request, (response, exp, event) -> {
//                Log.e("___", exp.getMessage());
                Gson gson = new Gson();
                Type type = new TypeToken<List<HintModel>>(){}.getType();
                List<HintModel> hints = gson.fromJson(response, type);
                adapter.removeAll();
                adapter.addAll(hints);
            });
        };


        Integer CHAR_THRESHOLD = 3;
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String input) {
                Integer length = input.length();
                if (length < CHAR_THRESHOLD) {
                    adapter.removeAll();
                    return false;
                }
                callable.call(input);
                return false;
            }
        });
    }

}
