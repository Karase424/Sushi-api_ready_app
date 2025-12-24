package com.example.api_sushi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.api_sushi.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class SplashActivity extends AppCompatActivity {

    private CardView imageCard;
    private ImageView imageView;
    private TextView textView, loadingText;
    private LinearProgressIndicator progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download);

        // Инициализация элементов
        imageCard = findViewById(R.id.imageCard);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        loadingText = findViewById(R.id.loadingText);
        progressBar = findViewById(R.id.progressBar);

        // Скрываем элементы перед анимацией
        imageCard.setAlpha(0f);
        textView.setAlpha(0f);
        loadingText.setAlpha(0f);
        progressBar.setAlpha(0f);

        // Запускаем анимации
        startAnimations();

        // Запускаем симуляцию прогресса
        simulateProgress();

        // Задержка 3 секунды и переход на MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goToMainActivity();
            }
        }, 3000); // 3000 мс = 3 секунды
    }

    private void startAnimations() {
        // Анимация картинки с задержкой
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation bounceAnim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.bounce);
                imageCard.startAnimation(bounceAnim);
                imageCard.animate().alpha(1f).setDuration(600).start();
            }
        }, 200);

        // Анимация текста с задержкой
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation fadeInAnim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_in);
                textView.startAnimation(fadeInAnim);
                textView.animate().alpha(1f).setDuration(800).start();
            }
        }, 600);

        // Анимация прогресс-бара и текста загрузки
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation slideUpAnim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.slide_up);
                progressBar.startAnimation(slideUpAnim);
                loadingText.startAnimation(slideUpAnim);

                progressBar.animate().alpha(1f).setDuration(500).start();
                loadingText.animate().alpha(1f).setDuration(500).start();
            }
        }, 1000);
    }

    private void simulateProgress() {
        // Симуляция прогресса загрузки
        final Handler progressHandler = new Handler();

        Runnable progressRunnable = new Runnable() {
            int progress = 0;

            @Override
            public void run() {
                if (progress <= 100) {
                    progressBar.setProgress(progress);
                    progress += 2; // Увеличиваем на 2% каждые 50ms
                    progressHandler.postDelayed(this, 50);
                }
            }
        };

        // Запускаем через 1.2 секунды
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressHandler.post(progressRunnable);
            }
        }, 1200);
    }

    private void goToMainActivity() {
        // Анимация исчезновения перед переходом
        imageCard.animate()
                .alpha(0f)
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(300)
                .start();

        textView.animate()
                .alpha(0f)
                .translationY(20)
                .setDuration(300)
                .start();

        progressBar.animate()
                .alpha(0f)
                .setDuration(300)
                .start();

        loadingText.animate()
                .alpha(0f)
                .setDuration(300)
                .start();

        // Переход через 300ms
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 300);
    }
}
