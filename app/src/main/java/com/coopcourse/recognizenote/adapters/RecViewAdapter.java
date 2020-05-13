package com.coopcourse.recognizenote.adapters;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.coopcourse.recognizenote.R;
import com.coopcourse.recognizenote.activities.EditTextActivity;
import com.coopcourse.recognizenote.database.ItemDataBase;
import com.coopcourse.recognizenote.database.RecViewItemTable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RecViewAdapter extends RecyclerView.Adapter<RecViewAdapter.CustomViewHolder> {


    private int EDIT_TEXT_ACTIVITY_FOR_RESULT_REQUEST_CODE = 2;
    private ItemDataBase dataBase;
    private AppCompatActivity context;
    private RecyclerView recyclerView;

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        SimpleDateFormat dateFormatOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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

        void setDateTime(Date _dateTime) {
            String formattedDate = dateFormatOut.format(_dateTime);
            dateTime.setText(formattedDate);
        }

        public TextView getRecognizedText() {
            return recognizedText;
        }

        public TextView getDateTime() {
            return dateTime;
        }


    }

    public RecViewAdapter(AppCompatActivity context, ItemDataBase dataBase) {
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

                Integer id = dataBase.itemDao().getItem(clickPosition).id;
                String text = dataBase.itemDao().getItem(clickPosition).text;

                intent.putExtra("text", text);
                intent.putExtra("id", id);
                context.startActivityForResult(intent, EDIT_TEXT_ACTIVITY_FOR_RESULT_REQUEST_CODE, ActivityOptions.makeSceneTransitionAnimation(context).toBundle());
            }

        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int clickPosition = recyclerView.getChildLayoutPosition(itemView);

                /*Choose menu*/
                final CharSequence[] items = {"Edit", "Delete", "Nothing"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Select The Action");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                Intent intent = new Intent(context, EditTextActivity.class);

                                Integer id = dataBase.itemDao().getItem(clickPosition).id;
                                String text = dataBase.itemDao().getItem(clickPosition).text;

                                intent.putExtra("text", text);
                                intent.putExtra("id", id);
                                context.startActivityForResult(intent, EDIT_TEXT_ACTIVITY_FOR_RESULT_REQUEST_CODE);
                                break;
                            case 1:
                                dataBase.itemDao().deleteItem(dataBase.itemDao().getItem(clickPosition));
                                recyclerView.getAdapter().notifyDataSetChanged();
                                break;
                            case 2:
                                //do nothing
                                break;
                        }
                    }
                });
                builder.show();
                return true;
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

