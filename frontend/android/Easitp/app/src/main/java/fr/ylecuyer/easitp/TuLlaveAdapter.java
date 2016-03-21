package fr.ylecuyer.easitp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TuLlaveAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<TuLLave> puntos = new ArrayList<TuLLave>();

    TuLlaveAdapter(Context context) {
        this.context = context;
    }

    TuLlaveAdapter(Context context, ArrayList<TuLLave> puntos) {
        this.context = context;
        this.puntos = puntos;
    }

    public void setPuntos(ArrayList<TuLLave> puntos) {
        this.puntos = puntos;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return puntos.size();
    }

    @Override
    public Object getItem(int i) {
        return puntos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TuLLave punto = puntos.get(i);

        if (view == null) {
            view = inflater.inflate(android.R.layout.simple_list_item_2, viewGroup, false);
        }

        TextView text1 = (TextView)view.findViewById(android.R.id.text1);
        TextView text2 = (TextView)view.findViewById(android.R.id.text2);

        text1.setText(punto.getName());
        text2.setText(punto.getAddress());

        return view;
    }
}
