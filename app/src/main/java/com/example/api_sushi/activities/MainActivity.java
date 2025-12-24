package com.example.api_sushi.activities;

import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.api_sushi.R;
import com.example.api_sushi.adapter.Products_adapter;
import com.example.api_sushi.model.Products;
import com.example.api_sushi.network.ApiService;
import com.example.api_sushi.network.RetrofitClient;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView productResycle;
    Products_adapter products_adapter;
    List<Products> productsList = new ArrayList<>();
    List<Products> filteredList = new ArrayList<>();

    private EditText searchEditText;
    private ChipGroup chipGroup;
    private ImageButton cartButton, historyButton;

    private String currentSearchQuery = "";
    private String currentCategory = "all";

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов
        searchEditText = findViewById(R.id.search_edit_text);
        chipGroup = findViewById(R.id.chipGroup);
        cartButton = findViewById(R.id.icon_cart);
        historyButton = findViewById(R.id.icon_history);

        // Инициализация RecyclerView
        productResycle = findViewById(R.id.products_recycle);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        productResycle.setLayoutManager(layoutManager);

        products_adapter = new Products_adapter(this, filteredList);
        productResycle.setAdapter(products_adapter);

        // Загружаем данные с GitHub
        loadProductsFromGitHub();

        // Настройка обработчиков
        setupListeners();
    }

    private void setupListeners() {
        // Кнопка корзины
        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // Кнопка истории заказов
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OrderHistoryActivity.class);
            startActivity(intent);
        });

        // Поиск с поддержкой русского языка
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().toLowerCase().trim();
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Выбираем первый чип "Всё" по умолчанию
        Chip allChip = findViewById(R.id.filtredAll);
        allChip.setChecked(true);

        // Фильтры по категориям
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentCategory = "all";
            } else {
                int chipId = checkedIds.get(0);
                currentCategory = getCategoryFromChipId(chipId);
            }
            applyFilters();
        });
    }

    private String getCategoryFromChipId(int chipId) {
        if (chipId == R.id.filtredAll) {
            return "all";
        } else if (chipId == R.id.filtredSushi) {
            return "sushi";
        } else if (chipId == R.id.filtredRolls) {
            return "rolls";
        } else if (chipId == R.id.filtredDrinks) {
            return "drinks";
        } else if (chipId == R.id.filtredDesert) {
            return "dessert";
        }
        return "all";
    }

    private void applyFilters() {
        filteredList.clear();

        // Если список товаров еще не загружен, выходим
        if (productsList.isEmpty()) {
            return;
        }

        // Применяем фильтр по категории
        if (currentCategory.equals("all")) {
            filteredList.addAll(productsList);
        } else {
            for (Products product : productsList) {
                if (product.getCategory() != null &&
                        product.getCategory().equalsIgnoreCase(currentCategory)) {
                    filteredList.add(product);
                }
            }
        }

        // Применяем поиск
        if (!currentSearchQuery.isEmpty()) {
            List<Products> searchResults = new ArrayList<>();
            for (Products product : filteredList) {
                boolean matches = false;

                // Проверяем название (поддержка русского)
                if (product.getTitle() != null) {
                    String title = product.getTitle().toLowerCase();
                    if (title.contains(currentSearchQuery)) {
                        matches = true;
                    }
                }

                // Проверяем описание (поддержка русского)
                if (!matches && product.getDescription() != null) {
                    String description = product.getDescription().toLowerCase();
                    if (description.contains(currentSearchQuery)) {
                        matches = true;
                    }
                }

                if (matches) {
                    searchResults.add(product);
                }
            }
            filteredList = searchResults;
        }

        // Обновляем адаптер
        products_adapter.updateProducts(filteredList);

        // Показываем сообщение, если ничего не найдено
        if (filteredList.isEmpty()) {
            if (!currentSearchQuery.isEmpty()) {
                Toast.makeText(this, "Ничего не найдено по запросу: " + currentSearchQuery,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Товары в категории не найдены", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadProductsFromGitHub() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Нет интернет соединения", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Нет интернета на устройстве");
            return;
        }

        Log.d(TAG, "Начинаем загрузку данных...");

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Products>> call = apiService.getProducts();

        call.enqueue(new Callback<List<Products>>() {
            @Override
            public void onResponse(Call<List<Products>> call, Response<List<Products>> response) {
                Log.d(TAG, "Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    productsList = response.body();

                    // Выводим категории товаров
                    for (Products product : productsList) {
                        Log.d(TAG, "Product: " + product.getTitle() +
                                ", Category: " + product.getCategory());
                    }

                    // Применяем текущие фильтры
                    applyFilters();

                    Toast.makeText(MainActivity.this,
                            "Загружено " + productsList.size() + " товаров",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Ошибка загрузки данных", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Products>> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage());
                Toast.makeText(MainActivity.this,
                        "Ошибка сети: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}