package com.zack83.mausam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private Context context;
    public ArrayList<WeatherRvModal> weatherRvModalArrayList;

    public WeatherAdapter(Context context, ArrayList<WeatherRvModal> weatherRvModalArrayList) {
        this.context = context;
        this.weatherRvModalArrayList = weatherRvModalArrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {
        WeatherRvModal modal = weatherRvModalArrayList.get(position);
        holder.temperature.setText(modal.getTemperature()+"°C");
        holder.windspeed.setText(modal.getWindspeed()+"KM/H");
        holder.time.setText(modal.getTime());
        Picasso.get().load("http".concat(modal.getIcon())).into((holder.condition));
    }

    @Override
    public int getItemCount() {
        return weatherRvModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView time,temperature,windspeed;
        private ImageView condition;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            time = itemView.findViewById(R.id.time);
            temperature = itemView.findViewById(R.id.temperature);
            windspeed = itemView.findViewById(R.id.windspeed);
            condition = itemView.findViewById(R.id.condition);

        }
    }
}
