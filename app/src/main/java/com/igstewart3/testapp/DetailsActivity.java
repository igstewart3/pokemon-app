package com.igstewart3.testapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.igstewart3.testapp.com.igstewart3.testapp.utilities.AppSingleton;
import com.igstewart3.testapp.com.igstewart3.testapp.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Activity to display details of selected Pokemon.
 *
 * Created by ianstewart on 24/10/2017.
 */
public class DetailsActivity extends Activity {

    private String mPokemonName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Retrieve name and check storage for previous details
        mPokemonName = getIntent().getStringExtra(Intent.EXTRA_TITLE);
        TextView nameText = findViewById(R.id.pokemon_name);
        nameText.setText(mPokemonName);

        if(getSharedPreferences(Constants.POKEMON_DETAILS_CACHE, 0).contains(mPokemonName)) {
            // Retrieve details from store
            String details = getSharedPreferences(Constants.POKEMON_DETAILS_CACHE, 0).getString(mPokemonName, Constants.EMPTY_JSON);
            try {
                JSONObject detailsObject = new JSONObject(details);
                handleDetailsJSON(detailsObject, true);
            } catch (JSONException e) {
                Log.d(Constants.TAG_ERROR, e.getLocalizedMessage());
            }
        } else {
            // Retrieve details URL from intent and retrieve details JSON
            String detailsURL = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, detailsURL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    handleDetailsJSON(response, false);

                    // Save details
                    SharedPreferences.Editor editor = getSharedPreferences(Constants.POKEMON_DETAILS_CACHE, 0).edit();
                    editor.putString(mPokemonName, response.toString());
                    editor.apply();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(Constants.TAG_ERROR, error.getLocalizedMessage());
                }
            });

            // Start request
            AppSingleton.getInstance(this).getRequestQueue().add(jsonObjectRequest);
        }
    }

    /**
     * Parse the details JSON to pull out the relevant details to display.
     *
     * @param detailsJSON The JSON to parse.
     * @param fromStore True if details are in persistent store.
     */
    protected void handleDetailsJSON(JSONObject detailsJSON, boolean fromStore) {
        // Get UI elements
        TextView weightText    = findViewById(R.id.pokemon_weight);
        TextView heightText    = findViewById(R.id.pokemon_height);

        // Set values
        try {
            String weightString = Constants.TEXT_WEIGHT + detailsJSON.getString(Constants.JSON_WEIGHT);
            String heightString = Constants.TEXT_HEIGHT + detailsJSON.getString(Constants.JSON_HEIGHT);
            weightText.setText(weightString);
            heightText.setText(heightString);

            // Parse moves list
            JSONArray movesArray = detailsJSON.getJSONArray(Constants.JSON_MOVES);
            parseMovesList(movesArray);
        }
        catch (JSONException e) {
            Log.d(Constants.TAG_ERROR, e.getLocalizedMessage());
        }

        if(fromStore) {
            String bitmapString = getSharedPreferences(Constants.POKEMON_IMAGE_CACHE, 0).getString(mPokemonName, "");
            byte[] bitmapBytes = Base64.decode(bitmapString, Base64.DEFAULT);
            ImageView pokemonImage = findViewById(R.id.pokemon_image);
            pokemonImage.setImageBitmap(BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length));
        }
        else {
            // Get image and convert for display
            String frontDesc;
            try {
                JSONObject sprites = detailsJSON.getJSONObject(Constants.JSON_SPRITES);
                frontDesc = sprites.getString(Constants.JSON_FRONT_DESC);
                retrieveImage(frontDesc);
            }
            catch (JSONException e) {
                Log.d(Constants.TAG_ERROR, e.getLocalizedMessage());
            }
        }
    }

    /**
     * Retrieves and image from the specified URL and displays it.
     *
     * @param imageUrl The URL to retrieve the image from.
     */
    private void retrieveImage(final String imageUrl) {
        // Setup image request
        ImageRequest imageRequest = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                // Display image
                ImageView pokemonImage = findViewById(R.id.pokemon_image);
                pokemonImage.setImageBitmap(response);

                // Save image to store
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                response.compress(Bitmap.CompressFormat.PNG, 100, baos);
                String base64Image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                SharedPreferences.Editor editor = getSharedPreferences(Constants.POKEMON_IMAGE_CACHE, 0).edit();
                editor.putString(mPokemonName, base64Image);
                editor.apply();
            }
        }, 1024, 1024, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log error
                Log.d(Constants.TAG_ERROR, "Error retrieving details: " + error.toString());
                if(error.getClass().equals(TimeoutError.class)) {
                    // Retry on timeout
                    retrieveImage(imageUrl);
                }
            }
        });

        // Start request
        AppSingleton.getInstance(this).getRequestQueue().add(imageRequest);
    }

    /**
     * Parses the moves array and populates the UI.
     *
     * @param moves Array containig the moves the Pokemon can make.
     *
     * @throws JSONException JSONException
     */
    protected void parseMovesList(JSONArray moves) throws JSONException {
        // Retrieve move names from array
        ArrayList<String> moveNames = new ArrayList<>();
        for(int i = 0; i < moves.length(); i++) {
            JSONObject topObject = moves.getJSONObject(i);
            JSONObject moveObject = topObject.getJSONObject(Constants.JSON_MOVE);
            String move = moveObject.getString(Constants.JSON_NAME);
            moveNames.add(move);
        }

        // Adapt pokemon array for list view
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, moveNames);
        ListView movesList = findViewById(R.id.pokemon_moves_list);
        movesList.setAdapter(adapter);
    }
}
