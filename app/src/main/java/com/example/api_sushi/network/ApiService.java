package com.example.api_sushi.network;

import com.example.api_sushi.model.Products;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    //@GET("https://jsonplaceholder.typicode.com/posts") // Тестовый URL
    @GET("https://raw.githubusercontent.com/Karase424/sushi-api/main/products.json")
    Call<List<Products>> getProducts();
}
