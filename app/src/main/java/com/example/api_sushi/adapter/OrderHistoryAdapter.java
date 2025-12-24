package com.example.api_sushi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.api_sushi.R;
import com.example.api_sushi.model.Order;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<Order> orders;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    private OnOrderClickListener onOrderClickListener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderHistoryAdapter(List<Order> orders) {
        this.orders = orders;
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.onOrderClickListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.orderIdText.setText("Заказ #" + order.getOrderId().substring(0, 8) + "...");

        if (order.getDate() != null) {
            holder.orderDateText.setText(dateFormat.format(order.getDate()));
        }

        holder.orderAmountText.setText(String.format("%.0f р.", order.getTotalAmount()));
        holder.orderStatusText.setText(order.getStatus());

        // Цвет статуса
        int statusColor = R.color.gray;
        switch (order.getStatus().toLowerCase()) {
            case "оформлен":
            case "доставлен":
                statusColor = R.color.green;
                break;
            case "в пути":
            case "готовится":
                statusColor = R.color.blue;
                break;
            case "отменен":
                statusColor = R.color.red;
                break;
        }
        holder.orderStatusText.setTextColor(holder.itemView.getContext()
                .getResources().getColor(statusColor));

        // Обработчик клика
        holder.itemView.setOnClickListener(v -> {
            if (onOrderClickListener != null) {
                onOrderClickListener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdText, orderDateText, orderAmountText, orderStatusText;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.orderIdText);
            orderDateText = itemView.findViewById(R.id.orderDateText);
            orderAmountText = itemView.findViewById(R.id.orderAmountText);
            orderStatusText = itemView.findViewById(R.id.orderStatusText);
        }
    }
}
