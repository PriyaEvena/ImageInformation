package com.example.priya.imageinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Priya on 12-01-2018.
 */
public class TemplateAdapter extends ArrayAdapter<Template> {

    private String Name;
    private String Description;
    private int link;
    private Context t;

    public TemplateAdapter(Activity context, ArrayList<Template> arrayList) {
        super(context, 0, arrayList);
        t = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View l = convertView;

        if (l == null) {
            l = LayoutInflater.from(getContext()).inflate(
                    R.layout.templateui, parent, false);
        }
        final Template modelclass = getItem(position);
        Name = modelclass.getName();
        link = modelclass.getLink();
        Description = modelclass.getDescription();

        ImageView ModelImg = (ImageView) l.findViewById(R.id.ModelImg);
        TextView title = (TextView) l.findViewById(R.id.ModelTitle);
        TextView Desc = (TextView) l.findViewById(R.id.ModelDesc);

        ModelImg.setImageResource(link);
        title.setText(Name);
        Desc.setText(Description);

        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(t, SelectionActivity.class);
                i.putExtra("model", modelclass);
                t.startActivity(i);
            }
        });

        return l;
    }
}
