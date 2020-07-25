package com.example.kreditpensiun;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL;

import de.hdodenhof.circleimageview.CircleImageView;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Sales> salesArrayList;

    public SalesAdapter(Context mContext, ArrayList<Sales> salesArrayList){
        this.mContext = mContext;
        this.salesArrayList = salesArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_sales, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Sales sales = salesArrayList.get(position);
        sales.setPosition(position);

        holder.tvNama.setText(sales.getNama());
        holder.tvNoTlp.setText(sales.getNo_tlp()+"");
        holder.tvRespon.setText(sales.getRespon());

        Glide.with(mContext)
                .load(Api.BASE + "img/" + sales.getPhoto())
                .apply(new RequestOptions().fitCenter())
                .into(holder.ivPhoto);

        holder.itemLayout.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra("sales", sales);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return salesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout;
        CircleImageView ivPhoto;
        TextView tvNama, tvNoTlp, tvRespon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPhoto = itemView.findViewById(R.id.iv_item_photo);
            tvNama = itemView.findViewById(R.id.iv_item_nama);
            tvNoTlp = itemView.findViewById(R.id.iv_item_notlp);
            tvRespon = itemView.findViewById(R.id.iv_item_respon);
            itemLayout = itemView.findViewById(R.id.item_layout_sales);

        }
    }
}
