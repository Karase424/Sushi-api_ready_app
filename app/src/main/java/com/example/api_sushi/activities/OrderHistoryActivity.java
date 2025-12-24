package com.example.api_sushi.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.api_sushi.R;
import com.example.api_sushi.adapter.OrderHistoryAdapter;
import com.example.api_sushi.model.Order;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView ordersRecyclerView;
    private OrderHistoryAdapter orderHistoryAdapter;
    private View noOrdersLayout;
    private Button goToMenuButton;
    private TextView emptyHistoryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Находим элементы
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        noOrdersLayout = findViewById(R.id.noOrdersLayout);
        goToMenuButton = findViewById(R.id.goToMenuButton);
        emptyHistoryText = findViewById(R.id.emptyHistoryText);

        // Настройка RecyclerView
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Загружаем историю заказов
        loadAndDisplayOrderHistory();

        // Обработчики нажатий
        goToMenuButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayOrderHistory();
    }

    private void loadAndDisplayOrderHistory() {
        List<Order> orders = loadOrderHistory();

        if (orders.isEmpty()) {
            // Показываем заглушку
            noOrdersLayout.setVisibility(View.VISIBLE);
            ordersRecyclerView.setVisibility(View.GONE);
            emptyHistoryText.setText("История заказов пуста\nСовершите свой первый заказ!");
        } else {
            // Показываем список заказов
            noOrdersLayout.setVisibility(View.GONE);
            ordersRecyclerView.setVisibility(View.VISIBLE);

            // Сортируем по дате (новые сверху)
            orders.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));

            orderHistoryAdapter = new OrderHistoryAdapter(orders);
            ordersRecyclerView.setAdapter(orderHistoryAdapter);

            // Добавляем обработчик кликов
            orderHistoryAdapter.setOnOrderClickListener(order -> {
                showOrderDetails(order);
            });
        }
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

        // Если заказов нет, создаем тестовые для демонстрации
        if (orders.isEmpty()) {
            orders = createSampleOrders();
        }

        return orders;
    }

    private List<Order> createSampleOrders() {
        List<Order> sampleOrders = new ArrayList<>();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

            Order order1 = new Order();
            order1.setOrderId("ORD-" + (System.currentTimeMillis() - 86400000 * 2));
            order1.setDate(sdf.parse("15.01.2024 14:30"));
            order1.setTotalAmount(1450);
            order1.setStatus("Доставлен");

            Order order2 = new Order();
            order2.setOrderId("ORD-" + (System.currentTimeMillis() - 86400000));
            order2.setDate(sdf.parse("16.01.2024 18:15"));
            order2.setTotalAmount(890);
            order2.setStatus("В пути");

            sampleOrders.add(order1);
            sampleOrders.add(order2);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sampleOrders;
    }

    private void showOrderDetails(Order order) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String dateStr = order.getDate() != null ? sdf.format(order.getDate()) : "Неизвестно";

        StringBuilder itemsText = new StringBuilder();
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (com.example.api_sushi.model.CartItem item : order.getItems()) {
                itemsText.append("• ").append(item.getProduct().getTitle())
                        .append(" x").append(item.getQuantity())
                        .append(" - ").append(item.getTotalPrice()).append(" р.\n");
            }
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Детали заказа #" + order.getOrderId())
                .setMessage("Дата: " + dateStr + "\n" +
                        "Статус: " + order.getStatus() + "\n" +
                        "Сумма: " + order.getTotalAmount() + " р.\n\n" +
                        "Состав заказа:\n" + itemsText.toString())
                .setPositiveButton("Закрыть", null)
                .show();
    }
}
