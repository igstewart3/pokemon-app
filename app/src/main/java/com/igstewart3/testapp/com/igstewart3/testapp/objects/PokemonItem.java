package com.igstewart3.testapp.com.igstewart3.testapp.objects;

/**
 * Class to hold pokemon details.
 *
 * Created by ianstewart on 23/10/2017.
 */

public class PokemonItem {

    private String mName;
    private String mUrl;

    /**
     * Constructor.
     *
     * @param name Pokemon name.
     * @param url URL containing Pokemon details.
     */
    public PokemonItem(String name, String url) {
        mName = name;
        mUrl = url;
    }

    /**
     * Returns Pokemon name.
     *
     * @return Pokemon name.
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns Pokemon URL.
     *
     * @return Pokemon URL.
     */
    public String getURL() {
        return mUrl;
    }

    @Override
    public String toString() {
        return mName;
    }
}
