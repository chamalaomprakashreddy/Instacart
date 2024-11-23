package com.example.ecomerce;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<OrderItem> orderItems;

    public OrderAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems != null ? orderItems : new ArrayList<>();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orderItems.get(position));
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderItemNameTextView;
        TextView orderItemPriceTextView;
        ImageView productImageView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderItemNameTextView = itemView.findViewById(R.id.textViewOrderItemName);
            orderItemPriceTextView = itemView.findViewById(R.id.textViewOrderItemPrice);
            productImageView = itemView.findViewById(R.id.imageViewProduct);
        }

        public void bind(OrderItem orderItem) {
            orderItemNameTextView.setText(orderItem.getName());
            orderItemPriceTextView.setText(String.valueOf(orderItem.getPrice()));
            // Assuming you have a method to load image from URL into the ImageView
            // You can use libraries like Picasso, Glide, or Coil for this purpose
            // For example, using Picasso:
             Picasso.get().load(orderItem.getImageUrl()).into(productImageView);
        }
    }
}
