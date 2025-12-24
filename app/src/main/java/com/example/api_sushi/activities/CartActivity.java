package com.example.api_sushi.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.api_sushi.R;
import com.example.api_sushi.adapter.CartAdapter;
import com.example.api_sushi.adapter.CartManager;
import com.example.api_sushi.model.CartItem;
import com.example.api_sushi.model.Order;
import com.example.api_sushi.storage.JsonStorageHelper;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private TextView totalAmountText, totalItemsText;
    private MaterialButton checkoutButton, clearCartButton;
    private View emptyCartLayout;
    private Button goToMenuButton;

    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Инициализация менеджера корзины
        cartManager = CartManager.getInstance(this);

        // Находим все элементы
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalAmountText = findViewById(R.id.totalAmountText);
        totalItemsText = findViewById(R.id.totalItemsText);
        checkoutButton = findViewById(R.id.checkoutButton);
        clearCartButton = findViewById(R.id.clearCartButton);
        emptyCartLayout = findViewById(R.id.emptyCartLayout);
        goToMenuButton = findViewById(R.id.goToMenuButton);

        // Настройка RecyclerView
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartManager);
        cartRecyclerView.setAdapter(cartAdapter);

        // Обновляем отображение
        updateCartDisplay();

        // Обработчики нажатий
        checkoutButton.setOnClickListener(v -> checkout());
        clearCartButton.setOnClickListener(v -> clearCart());
        goToMenuButton.setOnClickListener(v -> goToMenu());

        // Слушатель изменений в корзине
        cartAdapter.setOnCartChangedListener(this::updateCartDisplay);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartDisplay();
    }

    private void updateCartDisplay() {
        List<CartItem> items = cartManager.getCartItems();

        if (items.isEmpty()) {
            // Показываем заглушку для пустой корзины
            emptyCartLayout.setVisibility(View.VISIBLE);
            cartRecyclerView.setVisibility(View.GONE);
            totalItemsText.setText("Корзина пуста");
        } else {
            // Показываем список товаров
            emptyCartLayout.setVisibility(View.GONE);
            cartRecyclerView.setVisibility(View.VISIBLE);

            // Обновляем информацию
            totalItemsText.setText(cartManager.getTotalItems() + " товаров");
            totalAmountText.setText(cartManager.getTotalAmount() + " р.");

            // Обновляем адаптер
            cartAdapter.updateCartItems(items);
        }
    }

    private void checkout() {
        if (cartManager.getCartItems().isEmpty()) {
            Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверяем, авторизован ли пользователь
        if (isUserLoggedIn()) {
            // Если авторизован - оформляем заказ
            placeOrder();
        } else {
            // Если не авторизован - переходим на экран корзины
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("from_cart", true);
            startActivity(intent);
        }
    }

    private void clearCart() {
        if (cartManager.getCartItems().isEmpty()) {
            Toast.makeText(this, "Корзина уже пуста", Toast.LENGTH_SHORT).show();
            return;
        }

        cartManager.clearCart();
        updateCartDisplay();
        Toast.makeText(this, "Корзина очищена", Toast.LENGTH_SHORT).show();
    }

    private void goToMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isUserLoggedIn() {
        // Проверяем, есть ли сохраненные данные пользователя
        return getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getBoolean("is_logged_in", false);
    }

    private void placeOrder() {
        if (cartManager.getCartItems().isEmpty()) {
            Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show();
            return;
        }

        // Получаем email пользователя или используем "guest"
        SharedPreferences userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userEmail = userPrefs.getString("user_email", "guest");
        String userName = userPrefs.getString("user_name", "Гость");

        // Создаем заказ
        Order order = createOrder(userEmail, userName);

        // Сохраняем заказ в историю
        saveOrderToHistory(order);

        // Очищаем корзину
        cartManager.clearCart();

        // Показываем уведомление
        showOrderSuccessDialog(order);
    }

    private Order createOrder(String userEmail, String userName) {
        Order order = new Order();
        order.setOrderId("ORD-" + System.currentTimeMillis());
        order.setDate(new Date());
        order.setItems(new ArrayList<>(cartManager.getCartItems()));
        order.setTotalAmount(cartManager.getTotalAmount());
        order.setStatus("Оформлен");
        order.setUserId(userEmail);
        return order;
    }

    private void saveOrderToHistory(Order order) {
        // Загружаем существующие заказы
        List<Order> orders = loadOrderHistory();

        // Добавляем новый заказ
        orders.add(order);

        // Сохраняем обратно
        saveOrdersToStorage(orders);

        Log.d("CartActivity", "Заказ сохранен в историю: " + order.getOrderId());
    }

    private List<Order> loadOrderHistory() {
        SharedPreferences prefs = getSharedPreferences("order_history", MODE_PRIVATE);
        String ordersJson = prefs.getString("orders", "[]");

        Gson gson = new Gson();
        Type type = new TypeToken<List<Order>>() {}.getType();
        List<Order> orders = gson.fromJson(ordersJson, type);

        if (orders == null) {
            orders = new ArrayList<>();
        }

        return orders;
    }

    private void saveOrdersToStorage(List<Order> orders) {
        SharedPreferences prefs = getSharedPreferences("order_history", MODE_PRIVATE);
        Gson gson = new Gson();
        String ordersJson = gson.toJson(orders);

        prefs.edit()
                .putString("orders", ordersJson)
                .apply();
    }

    private void showOrderSuccessDialog(Order order) {
        new AlertDialog.Builder(this)
                .setTitle("Заказ оформлен!")
                .setMessage("Номер заказа: " + order.getOrderId() + "\n\n" +
                        "Сумма: " + order.getTotalAmount() + " р.\n" +
                        "Статус: " + order.getStatus() + "\n" +
                        "Дата: " + new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                        .format(order.getDate()) + "\n\n" +
                        "Заказ сохранен в историю.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Возвращаемся на главный экран
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}
