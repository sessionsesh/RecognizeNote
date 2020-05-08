package com.coopcourse.recognizenote;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecViewAdapter extends RecyclerView.Adapter<RecViewAdapter.CustomViewHolder> {
    private ItemDataBase dataBase;   //replace on dataBase

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView recognizedText;
        TextView dateTime;

        CustomViewHolder(View v) {
            super(v);
            recognizedText = v.findViewById(R.id.recognized_text);
            dateTime = v.findViewById(R.id.datetime);
        }

        void setRecognizedText(String text) {
            recognizedText.setText(text);
        }

        void setDateTime(String text) {
            dateTime.setText(text);
        }
    }

    public RecViewAdapter(ItemDataBase dataBase) {
        this.dataBase = dataBase;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.recycle_view_layout_item, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        RecViewItem t_item = dataBase.itemDAO().getItem(position);
        holder.setRecognizedText(t_item.text);
        holder.setDateTime(t_item.dateTime);

        Log.d("DB_ID", Integer.toString(t_item.id));
    }

    @Override
    public int getItemCount() {
        return dataBase.itemDAO().itemCount();
    }
}

