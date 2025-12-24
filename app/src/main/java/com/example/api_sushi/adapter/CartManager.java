package com.example.api_sushi.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.api_sushi.model.CartItem;
import com.example.api_sushi.model.Order;
import com.example.api_sushi.model.Products;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private SharedPreferences sharedPreferences;
    private List<CartItem> cartItems;

    private static final String CART_PREFS = "cart_prefs";
    private static final String CART_ITEMS_KEY = "cart_items";

    private CartManager(Context context) {
        sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        loadCart();
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    // Добавить товар в корзину
    public void addToCart(Products product) {
        boolean found = false;

        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            cartItems.add(new CartItem(product, 1));
        }

        saveCart();
    }

    // Удалить товар из корзины
    public void removeFromCart(int productId) {
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getProduct().getId() == productId) {
                cartItems.remove(i);
                saveCart();
                break;
            }
        }
    }

    // Изменить количество товара
    public void updateQuantity(int productId, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == productId) {
                if (quantity <= 0) {
                    removeFromCart(productId);
                } else {
                    item.setQuantity(quantity);
                }
                saveCart();
                break;
            }
        }
    }

    // Получить все товары в корзине
    public List<CartItem> getCartItems() {
        return cartItems;
    }

    // Очистить корзину
    public void clearCart() {
        cartItems.clear();
        saveCart();
    }

    // Получить общую сумму
    public int getTotalAmount() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    // Получить общее количество товаров
    public int getTotalItems() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getQuantity();
        }
        return total;
    }

    // Сохранить корзину
    private void saveCart() {
        Gson gson = new Gson();
        String json = gson.toJson(cartItems);
        sharedPreferences.edit().putString(CART_ITEMS_KEY, json).apply();
    }

    // Загрузить корзину
    private void loadCart() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(CART_ITEMS_KEY, null);
        Type type = new TypeToken<ArrayList<CartItem>>() {}.getType();

        if (json != null) {
            cartItems = gson.fromJson(json, type);
        } else {
            cartItems = new ArrayList<>();
        }
    }
    // Создать и сохранить заказ
    public Order checkout(String userId) {
        if (cartItems.isEmpty()) {
            return null;
        }

        // Создаем новый заказ
        Order order = new Order();
        order.setOrderId(generateOrderId());
        order.setDate(new Date());
        order.setItems(new ArrayList<>(cartItems)); // копируем текущую корзину
        order.setTotalAmount(getTotalAmount());
        order.setStatus("Оформлен");
        order.setUserId(userId);

        // Сохраняем заказ в историю
        saveOrderToHistory(order);

        // ОЧИЩАЕМ КОРЗИНУ ТОЛЬКО ПОСЛЕ УСПЕШНОГО СОХРАНЕНИЯ
        // НЕ ОЧИЩАЕМ ЗДЕСЬ, а в Activity после подтверждения

        return order;
    }

    private String generateOrderId() {
        return "ORD-" + System.currentTimeMillis();
    }

    private void saveOrderToHistory(Order order) {
        Gson gson = new Gson();
        String json = gson.toJson(order);
        sharedPreferences.edit()
                .putString("last_order", json)
                .apply();

        String historyJson = sharedPreferences.getString("order_history", "[]");
        Type type = new TypeToken<ArrayList<Order>>() {}.getType();
        List<Order> orders = gson.fromJson(historyJson, type);

        orders.add(order);

        String updatedJson = gson.toJson(orders);
        sharedPreferences.edit()
                .putString("order_history", updatedJson)
                .apply();
    }
}