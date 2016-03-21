package fr.ylecuyer.easitp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LineAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Sitp> lines = new ArrayList<Sitp>();

    LineAdapter(Context context) {
        this.context = context;
    }

    LineAdapter(Context context, ArrayList<Sitp> lines) {
        this.context = context;
        this.lines = lines;
    }

    public void setLines(ArrayList<Sitp> lines) {
        this.lines = lines;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lines.size();
    }

    @Override
    public Object getItem(int i) {
        return lines.get(i);
    }

    @Override
    public long getItemId(int i) {
        return lines.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Sitp line = lines.get(i);

        if (view == null) {
            view = inflater.inflate(R.layout.sitp_cell, viewGroup, false);
        }

        TextView text1 = (TextView)view.findViewById(R.id.text1);
        TextView text2 = (TextView)view.findViewById(R.id.text2);

        text1.setText(line.getLine());
        text2.setText(line.getDestino());

        return view;
    }
}
