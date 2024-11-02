package com.example.ecomerce;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;
    private OnAddItemClickListener mListener;
    private RecyclerView recyclerView; // Store RecyclerView instance

    public interface OnAddItemClickListener {
        void onAddItemClick(int position);
    }

    // Constructor
    public ProductAdapter(List<Product> productList, Context context, RecyclerView recyclerView) {
        this.productList = productList;
        this.context = context;
        this.recyclerView = recyclerView; // Store RecyclerView instance
    }

    // Method to set click listener
    public void setOnAddItemClickListener(OnAddItemClickListener listener) {
        mListener = listener;
    }

    public void updateButtonVisibility(int position, boolean addedToCart) {
        productList.get(position).setAddedToCart(addedToCart);
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                .error(R.drawable.error_image) // Image to display in case of error
                .into(holder.productImageView); // ImageView to load the image into
        // ImageView to load the image into

        holder.btnDecrease.setVisibility(product.isAddedToCart() ? View.VISIBLE : View.GONE);
        holder.btnIncrease.setVisibility(product.isAddedToCart() ? View.VISIBLE : View.GONE);
        holder.tvQuantity.setVisibility(product.isAddedToCart() ? View.VISIBLE : View.GONE);
        holder.btnAddToCart.setVisibility(product.isAddedToCart() ? View.GONE : View.VISIBLE);
        holder.tvQuantity.setText(String.valueOf(product.getQuantity())); // Update quantity text

        holder.btnDecrease.setOnClickListener(view -> {
            int quantity = product.getQuantity();
            if (quantity > 0) {
                product.setQuantity(quantity - 1);
                holder.tvQuantity.setText(String.valueOf(quantity - 1)); // Update quantity text
                notifyDataSetChanged();
            }
        });

        holder.btnIncrease.setOnClickListener(view -> {
            int quantity = product.getQuantity();
            product.setQuantity(quantity + 1);
            holder.tvQuantity.setText(String.valueOf(quantity + 1)); // Update quantity text
            notifyDataSetChanged();
            if (mListener != null) {
                mListener.onAddItemClick(position); // Notify listener to add item to cart
            }
        });

        holder.btnAddToCart.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onAddItemClick(position); // Notify listener to add item to cart
                holder.btnAddToCart.setVisibility(View.GONE); // Hide the "Add to Cart" button
                holder.btnDecrease.setVisibility(View.VISIBLE); // Show the decrease button
                holder.btnIncrease.setVisibility(View.VISIBLE); // Show the increase button
                holder.tvQuantity.setVisibility(View.VISIBLE); // Show the quantity text
                holder.tvQuantity.setText("1"); // Set initial quantity to 1
                product.setQuantity(1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ViewHolder class
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvPrice;
        TextView tvDescription;
        Button btnDecrease;
        TextView tvQuantity;
        Button btnIncrease;
        Button btnAddToCart; // Add this button
        ImageView productImageView;
        public ProductViewHolder(@NonNull View itemView, OnAddItemClickListener listener) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.product_order_price);
            tvDescription = itemView.findViewById(R.id.product_description);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart); // Initialize the button
            productImageView = itemView.findViewById(R.id.product_image);

            // Set click listener for the "Add to Cart" button
            btnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onAddItemClick(position);
                    }
                }
            });
        }

        public void bind(Product product) {
            tvName.setText(product.getName());
            tvPrice.setText(String.valueOf(product.getPrice()));
            tvDescription.setText(product.getDescription());
            tvQuantity.setText(String.valueOf(product.getQuantity()));
        }

    }
}



