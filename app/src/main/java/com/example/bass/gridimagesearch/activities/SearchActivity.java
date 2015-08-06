package com.example.bass.gridimagesearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.example.bass.gridimagesearch.R;
import com.example.bass.gridimagesearch.adapters.ImageResultsAdapter;
import com.example.bass.gridimagesearch.listener.EndlessScrollListener;
import com.example.bass.gridimagesearch.models.ImageResult;
import com.example.bass.gridimagesearch.models.SearchFilter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class SearchActivity extends ActionBarActivity implements EditFilterFragment.OnFilterSaveListener {

    private EditText etQuery;
    private StaggeredGridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;
    private SearchFilter filter;
    private final static int PIC_PER_PAGE = 8;
    private final static int VISIBLE_THRESHOLD = 2;
    private EndlessScrollListener esListener;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        imageResults = new ArrayList<ImageResult>();
        aImageResults = new ImageResultsAdapter(this, imageResults);
        filter = new SearchFilter();

        esListener = new EndlessScrollListener(VISIBLE_THRESHOLD) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                filter.start = page * PIC_PER_PAGE;
                searchImage();
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        };

        setupViews();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                // perform query here
                filter.query = query;
                filter.start = 0;
                aImageResults.clear();
                esListener.resetStartPage(0);
                searchImage();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private void setupViews() {

        gvResults = (StaggeredGridView) findViewById(R.id.gvResults);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);
                ImageResult result = imageResults.get(position);
                i.putExtra("result", result);
                startActivity(i);
            }
        });

        gvResults.setAdapter(aImageResults);
        gvResults.setOnScrollListener(esListener);

    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
    public void OnSearchFail() {
        Toast.makeText(getBaseContext(),"Please Check your network status!",Toast.LENGTH_SHORT).show();
        filter.start = (filter.start >= PIC_PER_PAGE)? (filter.start - PIC_PER_PAGE) :0;
        esListener.resetStartPage(filter.start / PIC_PER_PAGE);


    }
    public void searchImage() {
        if(!isNetworkAvailable()) {
            OnSearchFail();
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        String site = "";
        try {
            site = URLEncoder.encode(filter.site,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String searchUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + filter.query
                + "&imgcolor=" + filter.color
                + "&imgsz=" + filter.size
                + "&imgtype=" + filter.type
                + "&as_sitesearch=" + site
                + "&start=" + Integer.toString(filter.start)
                + "&rsz=8";
        Toast.makeText(getBaseContext(),"Loading page "+ Integer.toString((filter.start/8) +1),Toast.LENGTH_SHORT).show();
        try {
            client.get(searchUrl, null, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONArray imageResultsJson = null;
                    try {
                        imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                        aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));
                    } catch (JSONException e) {
                        OnSearchFail();
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    OnSearchFail();
                }
            });
        }catch(Exception e) {
            OnSearchFail();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showEditDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        EditFilterFragment editFilterDialog = EditFilterFragment.newInstance(filter);
        editFilterDialog.show(fm,"SEARCH_FILTER");
    }

    @Override
    public void onFilterSave(SearchFilter newFilter) {
        filter = newFilter;
        filter.start = 0;
        aImageResults.clear();
        esListener.resetStartPage(0);
        searchImage();
    }
}
