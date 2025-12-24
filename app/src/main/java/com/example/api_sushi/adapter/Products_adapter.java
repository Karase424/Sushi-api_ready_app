package com.example.api_sushi.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.api_sushi.R;
import com.example.api_sushi.activities.ProductDetailActivity;
import com.example.api_sushi.model.Products;
import java.util.List;
import java.util.Random;

public class Products_adapter extends RecyclerView.Adapter<Products_adapter.ProductsViewHolder> {

    Context context;
    List<Products> products;
    private CartManager cartManager;

    public Products_adapter(Context context, List<Products> products) {
        this.context = context;
        this.products = products;
        this.cartManager = CartManager.getInstance(context);
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productItems = LayoutInflater.from(context).inflate(R.layout.product_detail_activity, parent, false);
        return new ProductsViewHolder(productItems);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
        Products product = products.get(position);

        // Проверяем, есть ли товар в корзине
        boolean isInCart = isProductInCart(product);

        holder.productTitle.setText(product.getTitle());
        holder.productDescription.setText(product.getDescription());
        holder.productPrice.setText(String.valueOf(product.getPrice()) + " р.");

        // Загрузка изображения
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            String imageUrl = product.getImage() + "?rand=" + new Random().nextInt(1000);
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(holder.productImage);
        }

        // Обновляем вид кнопки "Добавить в корзину"
        updateCartButton(holder.addToCartButton, isInCart);

        // Обработчик нажатия на карточку
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product", product);
            context.startActivity(intent);
        });

        // Обработчик нажатия на кнопку "Добавить в корзину"
        holder.addToCartButton.setOnClickListener(v -> {
            if (!isInCart) {
                cartManager.addToCart(product);
                updateCartButton(holder.addToCartButton, true);
                Toast.makeText(context, product.getTitle() + " добавлен в корзину",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Товар уже в корзине", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isProductInCart(Products product) {
        for (com.example.api_sushi.model.CartItem item : cartManager.getCartItems()) {
            if (item.getProduct().getId() == product.getId()) {
                return true;
            }
        }
        return false;
    }

    private void updateCartButton(ImageButton button, boolean isInCart) {
        if (isInCart) {
            button.setImageResource(R.drawable.ic_cart_added);
            button.setBackgroundResource(R.drawable.buy_button_added);
            button.setEnabled(false);
        } else {
            button.setImageResource(R.drawable.ic_cart2);
            button.setBackgroundResource(R.drawable.buy_button);
            button.setEnabled(true);
        }
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public void updateProducts(List<Products> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    public static final class ProductsViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout productBg;
        ImageView productImage;
        TextView productTitle, productDescription, productPrice;
        ImageButton addToCartButton;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            productBg = itemView.findViewById(R.id.product_bg);
            productImage = itemView.findViewById(R.id.productView);
            productTitle = itemView.findViewById(R.id.productTitle);
            productDescription = itemView.findViewById(R.id.product_description);
            productPrice = itemView.findViewById(R.id.productPrice);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }
}