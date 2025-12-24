package com.example.api_sushi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.api_sushi.R;
import com.example.api_sushi.model.CartItem;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import java.util.Random;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private CartManager cartManager;
    private OnCartChangedListener cartChangedListener;

    public interface OnCartChangedListener {
        void onCartChanged();
    }

    public CartAdapter(Context context, CartManager cartManager) {
        this.context = context;
        this.cartManager = cartManager;
        this.cartItems = cartManager.getCartItems();
    }

    public void setOnCartChangedListener(OnCartChangedListener listener) {
        this.cartChangedListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        holder.productTitle.setText(cartItem.getProduct().getTitle());
        holder.productPrice.setText(cartItem.getProduct().getPrice() + " р.");
        holder.quantityText.setText(String.valueOf(cartItem.getQuantity()));

        // Загрузка изображения
        if (cartItem.getProduct().getImage() != null && !cartItem.getProduct().getImage().isEmpty()) {
            String imageUrl = cartItem.getProduct().getImage() + "?rand=" + new Random().nextInt(1000);
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.productImage);
        }

        // Кнопка увеличения количества
        holder.increaseButton.setOnClickListener(v -> {
            cartManager.updateQuantity(cartItem.getProduct().getId(), cartItem.getQuantity() + 1);
            updateCartItems(cartManager.getCartItems());
            if (cartChangedListener != null) {
                cartChangedListener.onCartChanged();
            }
        });

        // Кнопка уменьшения количества
        holder.decreaseButton.setOnClickListener(v -> {
            if (cartItem.getQuantity() > 1) {
                cartManager.updateQuantity(cartItem.getProduct().getId(), cartItem.getQuantity() - 1);
                updateCartItems(cartManager.getCartItems());
            } else {
                cartManager.removeFromCart(cartItem.getProduct().getId());
                updateCartItems(cartManager.getCartItems());
                Toast.makeText(context, "Товар удален из корзины", Toast.LENGTH_SHORT).show();
            }
            if (cartChangedListener != null) {
                cartChangedListener.onCartChanged();
            }
        });

        // Кнопка удаления
        holder.removeButton.setOnClickListener(v -> {
            cartManager.removeFromCart(cartItem.getProduct().getId());
            updateCartItems(cartManager.getCartItems());
            Toast.makeText(context, "Товар удален из корзины", Toast.LENGTH_SHORT).show();
            if (cartChangedListener != null) {
                cartChangedListener.onCartChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateCartItems(List<CartItem> newCartItems) {
        this.cartItems = newCartItems;
        notifyDataSetChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle, productPrice, quantityText;
        MaterialButton decreaseButton, increaseButton;
        ImageButton removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            productPrice = itemView.findViewById(R.id.productPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}