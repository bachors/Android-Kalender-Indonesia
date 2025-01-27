package com.bachors.kalenderindonesia.apis;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
    https://github.com/bachors/Android-Kalender-Indonesia
*/

public class Apis {

    //https://github.com/bachors/apiapi?tab=readme-ov-file#hari-libur--cuti-nasional
    private static final String BASE_URL = "https://script.google.com/macros/s/AKfycbxTj7WY21RLM8RO6biCeobN5BGAzRUgvfzGxvGJ-Scww6rsgXZWIbK0WSpMjhac5vxs/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
