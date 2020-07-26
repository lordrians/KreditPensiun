package com.example.kreditpensiun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static RecyclerView rvSales;
    public static ArrayList<Sales> salesArrayList;
    private FloatingActionButton fab_add;
    private SalesAdapter salesAdapter;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }


    private void init() {
        rvSales = findViewById(R.id.rv_sales);
        fab_add = findViewById(R.id.fab_add);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);


        salesArrayList = new ArrayList<>();
        rvSales.setHasFixedSize(true);
        rvSales.setLayoutManager(new LinearLayoutManager(this));

        fab_add.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), CreateSalesActivity.class));
        });


        loadData("");

    }


    private void loadData(String query) {

        dialog.setMessage("Loading...");
        dialog.show();
        StringRequest request = new StringRequest(StringRequest.Method.POST, Api.SHOW_ITEM, response -> {
            try {
                JSONArray salesArr = new JSONArray(response);
                if (salesArrayList.size() != 0){
                    salesArrayList.clear();
                }
                for (int i = 0; i < salesArr.length(); i++){
                    JSONObject object = salesArr.getJSONObject(i);

                    Sales sales = new Sales();
                    sales.setId(object.getInt("id"));
                    sales.setNik(object.getString("nik"));
                    sales.setNo_tlp(object.getString("no_tlp"));
                    sales.setTgl_lahir(object.getString("tgl_lahir"));
                    sales.setGaji(object.getLong("gaji"));
                    sales.setNama(object.getString("nama"));
                    sales.setAlamat(object.getString("alamat"));
                    sales.setStatus(object.getString("status"));
                    sales.setPembayaran(object.getString("pembayaran"));
                    sales.setRespon(object.getString("respon"));
                    sales.setPhoto(object.getString("photo"));
                    salesArrayList.add(sales);
                }
                salesAdapter = new SalesAdapter(getApplicationContext(),salesArrayList);
                rvSales.setAdapter(salesAdapter);
                salesAdapter.notifyDataSetChanged();
                dialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
                dialog.dismiss();
            }
        },error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                if (query != null){
                    map.put("search", query);
                }
                return map;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.item_toolbar,menu);
        MenuItem searchItem = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadData(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_search:
                SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
                SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        loadData(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        loadData(newText);
                        return false;
                    }
                });
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

}