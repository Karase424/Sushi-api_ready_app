package com.example.api_sushi.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.api_sushi.R;
import com.example.api_sushi.adapter.CartManager;
import com.example.api_sushi.model.Products;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productTitle, productDescription, productPrice, productCategory;
    private Button addToCartButton;
    private Products product;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail_activity);

        cartManager = CartManager.getInstance(this);

        // Получаем продукт из Intent
        product = (Products) getIntent().getSerializableExtra("product");

        if (product == null) {
            finish();
            return;
        }

        // Инициализация элементов
        productImage = findViewById(R.id.productView);
        productTitle = findViewById(R.id.productTitle);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.productPrice);
        addToCartButton = findViewById(R.id.addToCartButton);

        // Заполняем данные
        productTitle.setText(product.getTitle());
        productDescription.setText(product.getDescription());
        productPrice.setText(product.getPrice() + " р.");
        productCategory.setText("Категория: " + product.getCategory());

        // Загрузка изображения
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            Glide.with(this)
                    .load(product.getImage())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(productImage);
        }

        // Проверяем, есть ли товар в корзине
        updateAddToCartButton();

        // Обработчик нажатия на кнопку
        addToCartButton.setOnClickListener(v -> {
            cartManager.addToCart(product);
            Toast.makeText(this, product.getTitle() + " добавлен в корзину",
                    Toast.LENGTH_SHORT).show();
            updateAddToCartButton();
        });
    }

    // В методе updateAddToCartButton():
    private void updateAddToCartButton() {
        boolean isInCart = false;

        for (com.example.api_sushi.model.CartItem item : cartManager.getCartItems()) {
            if (item.getProduct().getId() == product.getId()) {
                isInCart = true;
                break;
            }
        }

        if (isInCart) {
            addToCartButton.setText("В корзине ✓");
            addToCartButton.setBackgroundColor(getResources().getColor(R.color.green));
            addToCartButton.setTextColor(getResources().getColor(R.color.white));
            addToCartButton.setEnabled(false);
        } else {
            addToCartButton.setText("Добавить в корзину");
            addToCartButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            addToCartButton.setTextColor(getResources().getColor(R.color.white));
            addToCartButton.setEnabled(true);
        }
    }
}
