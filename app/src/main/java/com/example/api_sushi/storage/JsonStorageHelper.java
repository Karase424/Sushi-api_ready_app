package com.example.api_sushi.storage;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.api_sushi.model.Order;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class JsonStorageHelper {

    private static final String PREFS_NAME = "app_storage";
    private static final String ORDERS_KEY = "user_orders";
    private static final String USER_KEY = "current_user";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public JsonStorageHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // ===== ЗАКАЗЫ =====

    public void saveOrder(Order order) {
        List<Order> orders = getOrders();
        orders.add(order);

        String json = gson.toJson(orders);
        sharedPreferences.edit().putString(ORDERS_KEY, json).apply();
    }

    public List<Order> getOrders() {
        String json = sharedPreferences.getString(ORDERS_KEY, null);
        Type type = new TypeToken<ArrayList<Order>>() {}.getType();

        if (json != null) {
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public void clearOrders() {
        sharedPreferences.edit().remove(ORDERS_KEY).apply();
    }

    // ===== ПОЛЬЗОВАТЕЛИ =====

    public void saveUser(String email, String name) {
        UserData userData = new UserData(email, name);
        String json = gson.toJson(userData);
        sharedPreferences.edit().putString(USER_KEY, json).apply();
    }

    public UserData getUser() {
        String json = sharedPreferences.getString(USER_KEY, null);
        if (json != null) {
            return gson.fromJson(json, UserData.class);
        }
        return null;
    }

    public void logout() {
        sharedPreferences.edit().remove(USER_KEY).apply();
    }

    public boolean isLoggedIn() {
        return getUser() != null;
    }

    // ===== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =====

    public String createOrderId() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Класс для хранения данных пользователя
    public static class UserData {
        public String email;
        public String name;
        public Date registrationDate;

        public UserData(String email, String name) {
            this.email = email;
            this.name = name;
            this.registrationDate = new Date();
        }
    }
}
