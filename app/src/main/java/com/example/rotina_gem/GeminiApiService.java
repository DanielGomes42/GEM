package com.example.rotina_gem;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiApiService {
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-1.5-flash-latest:generateContent")
    Call<GenerateContentResponse> generateContent(@Query("key") String apiKey, @Body GenerateContentRequest request);
}