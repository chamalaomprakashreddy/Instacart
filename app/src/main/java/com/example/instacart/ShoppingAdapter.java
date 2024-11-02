package com.example.ecomerce;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.graphics.Bitmap;
import android.widget.ImageView;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class ShoppingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_EMPTY = 1;

    private List<Product> cartItemList;
    private Context context;

    public ShoppingAdapter(List<Product> cartItemList, Context context) {
        this.cartItemList = cartItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
            return new CartItemViewHolder(view);
        } else {
            View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_cart, parent, false);
            return new EmptyViewHolder(emptyView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CartItemViewHolder) {
            Product product = cartItemList.get(position);
            CartItemViewHolder itemViewHolder = (CartItemViewHolder) holder;
            itemViewHolder.productNameTextView.setText(product.getName());
            itemViewHolder.productPriceTextView.setText("$" + product.getPrice());
            new DownloadImageTask(itemViewHolder.productImageView).execute(product.getImageUrl());
        }
    }

    @Override
    public int getItemCount() {
        return cartItemList.isEmpty() ? 1 : cartItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return cartItemList.isEmpty() ? VIEW_TYPE_EMPTY : VIEW_TYPE_ITEM;
    }

    public static class CartItemViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView productPriceTextView;
        ImageView productImageView;
        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.product_name);
            productPriceTextView = itemView.findViewById(R.id.product_price);
            productImageView = itemView.findViewById(R.id.product_image);
        }
    }
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
    public static class EmptyViewHolder extends RecyclerView.ViewHolder {
        TextView emptyTextView;

        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
            emptyTextView = itemView.findViewById(R.id.empty_text);
        }
    }
}

