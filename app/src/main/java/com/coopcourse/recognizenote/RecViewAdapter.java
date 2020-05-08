package com.coopcourse.recognizenote;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecViewAdapter extends RecyclerView.Adapter<RecViewAdapter.CustomViewHolder> {
    private ItemDataBase dataBase;
    private Context context;
    private RecyclerView recyclerView;

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

        public TextView getRecognizedText() {
            return recognizedText;
        }

        public TextView getDateTime() {
            return dateTime;
        }
    }

    RecViewAdapter(Context context, ItemDataBase dataBase) {
        this.dataBase = dataBase;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View itemView = layoutInflater.inflate(R.layout.recycle_view_layout_item, parent, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditTextActivity.class);

                int clickPosition = recyclerView.getChildLayoutPosition(itemView);
                String text = dataBase.itemDao().getItem(clickPosition).text;

                intent.putExtra("text", text);
                context.startActivity(intent);
            }
        });
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        RecViewItemTable t_item = dataBase.itemDao().getItem(position);
        holder.setRecognizedText(t_item.text);
        holder.setDateTime(t_item.dateTime);

        Log.d("DB_ID", Integer.toString(t_item.id));
    }

    @Override
    public int getItemCount() {
        return dataBase.itemDao().itemCount();
    }
}

