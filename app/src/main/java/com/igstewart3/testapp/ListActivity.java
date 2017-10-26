package com.igstewart3.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.igstewart3.testapp.com.igstewart3.testapp.objects.PokemonItem;
import com.igstewart3.testapp.com.igstewart3.testapp.utilities.AppSingleton;
import com.igstewart3.testapp.com.igstewart3.testapp.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Activity to display list of Pokemon.
 */
public class ListActivity extends AppCompatActivity {

    ListView mPokemonList = null;
    ArrayList<PokemonItem> mPokemonArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Retrieve list view
        mPokemonList = (ListView) findViewById(R.id.pokemon_list_view);

        // Setup pokemon list
        setupPokemonRequest();
    }

    private void setupPokemonRequest() {
        // Setup request to retrieve list of pokemon
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.POKEMON_API_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handlePokeJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(Constants.TAG_ERROR, "Error retrieving API: " + error.toString());
                if(error.getClass().equals(TimeoutError.class)) {
                    // Retry request on timeout
                    setupPokemonRequest();
                }
            }
        });

        // Start request
        AppSingleton.getInstance(this).getRequestQueue().add(jsonObjectRequest);
    }

    /**
     * Method to parse the pokemon names JSON object.
     *
     * @param jsonObject JSON object containing all the Pokemon names.
     */
    private void handlePokeJSON(JSONObject jsonObject) {
        // Parse list into Pokemon array
        mPokemonArray = new ArrayList<>();
        try {
            JSONArray namesArray = jsonObject.getJSONArray(Constants.JSON_RESULTS);
            for(int i = 0; i < namesArray.length(); i++) {
                JSONObject nameObject = namesArray.getJSONObject(i);
                String name = nameObject.getString(Constants.JSON_NAME);
                String url = nameObject.getString(Constants.JSON_URL);
                PokemonItem pokemon = new PokemonItem(name, url);
                mPokemonArray.add(pokemon);
            }
        }
        catch (JSONException e) {
            Log.d(Constants.TAG_ERROR, e.getLocalizedMessage());
        }

        // Convert pokemon array for list view
        ArrayAdapter<PokemonItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mPokemonArray);
        mPokemonList.setAdapter(adapter);
        mPokemonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Launch details screen to display pokemon details
                Intent intent = new Intent(ListActivity.this, DetailsActivity.class);
                intent.putExtra(Intent.EXTRA_TITLE, mPokemonArray.get(position).getName());
                intent.putExtra(Intent.EXTRA_TEXT, mPokemonArray.get(position).getURL());
                startActivity(intent);
            }
        });
    }
}
