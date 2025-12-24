package com.example.api_sushi.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.api_sushi.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private TextInputLayout emailLayout, passwordLayout;
    private MaterialButton loginButton;
    private TextView registerText;
    private MaterialButton continueWithoutLoginButton;

    private boolean fromCart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Получаем информацию, откуда пришли
        fromCart = getIntent().getBooleanExtra("from_cart", false);

        // Инициализация элементов
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);
        continueWithoutLoginButton = findViewById(R.id.continueWithoutLoginButton);

        // Обработчики нажатий
        loginButton.setOnClickListener(v -> login());
        registerText.setOnClickListener(v -> register());
        continueWithoutLoginButton.setOnClickListener(v -> continueWithoutLogin());
    }

    private void login() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Валидация
        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Введите email");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Введите корректный email");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Введите пароль");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Пароль должен быть не менее 6 символов");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }

        if (!isValid) {
            return;
        }

        // Простая имитация входа
        if (email.equals("test@test.com") && password.equals("123456")) {
            // Сохраняем данные пользователя
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            prefs.edit()
                    .putBoolean("is_logged_in", true)
                    .putString("user_email", email)
                    .putString("user_name", "Тестовый Пользователь")
                    .apply();

            Toast.makeText(this, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show();

            // Возвращаемся на нужный экран
            if (fromCart) {
                // Возвращаемся в корзину для оформления заказа
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
            } else {
                // Возвращаемся на главный экран
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            finish();
        } else {
            Toast.makeText(this, "Неверный email или пароль", Toast.LENGTH_SHORT).show();
        }
    }

    private void register() {
        // Переход на экран регистрации
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Сохраняем данные пользователя
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit()
                .putBoolean("is_logged_in", true)
                .putString("user_email", email)
                .putString("user_name", "Новый Пользователь")
                .apply();

        Toast.makeText(this, "Регистрация выполнена успешно!", Toast.LENGTH_SHORT).show();

        // Возвращаемся на нужный экран
        if (fromCart) {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }

    private void continueWithoutLogin() {
        // Пользователь может оформить заказ без регистрации
        if (fromCart) {
            // Переходим к оформлению заказа
            Toast.makeText(this, "Продолжаем без регистрации", Toast.LENGTH_SHORT).show();

            // Имитируем оформление заказа
            Toast.makeText(this, "Заказ оформлен! Спасибо за покупку!", Toast.LENGTH_LONG).show();

            // Очищаем корзину
            // cartManager.clearCart();

            // Возвращаемся на главный экран
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            // Просто возвращаемся на главный экран
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
