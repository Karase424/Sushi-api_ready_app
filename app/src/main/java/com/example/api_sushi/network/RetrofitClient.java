package com.example.api_sushi.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            // Создаем Gson с lenient режимом
            Gson gson = new GsonBuilder()
                    .setLenient()  // Разрешаем нестрогий парсинг JSON
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://raw.githubusercontent.com/")
                    .addConverterFactory(GsonConverterFactory.create(gson))  // Передаем наш Gson
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
