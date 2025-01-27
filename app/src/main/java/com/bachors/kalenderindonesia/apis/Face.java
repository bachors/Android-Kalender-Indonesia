package com.bachors.kalenderindonesia.apis;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/*
    https://github.com/bachors/Android-Kalender-Indonesia
*/

public interface Face {
    @GET("exec")
    Call<ResponseBody> getSurat(@Query("tahun") String tahun);
}
