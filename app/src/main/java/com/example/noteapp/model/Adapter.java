package com.example.noteapp.model;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.note.NoteDetails;
import com.example.noteapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
   List<String> titles;
   List<String> content;

    public Adapter( List<String> titles,List<String> content ) {

        this.titles=titles;
        this.content=content;
    }

    //==================///////////====================////////////==================
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout,parent,false);


        return new ViewHolder(view);
    }
//////////========================//////////////////==================//////////
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.notetitle.setText(titles.get(position));
        holder.notecontent.setText(content.get(position));

         final Integer code=getRandomcoloer();

        holder.mcardView.setCardBackgroundColor(holder.view.getResources().getColor(code,null));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(view.getContext(), NoteDetails.class);
                i.putExtra("title",titles.get(position));
                i.putExtra("content",content.get(position));
                i.putExtra("color",code);

                view.getContext().startActivity(i);

            }
        });


    }
//=============================/////////////==============///////////==========
    private int getRandomcoloer() {
        List<Integer> colorcode=new ArrayList<>();
        colorcode.add(R.color.blue);
        colorcode.add(R.color.yellow);
        colorcode.add(R.color.lightepurple);
        colorcode.add(R.color.lightgreen);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.greenlighte);
        colorcode.add(R.color.notgreen);

        Random randomcolor=new Random();
        int number =randomcolor.nextInt(colorcode.size());
        return colorcode.get(number);
    }

    //==================/////////////////=//////////////////////==//////////
    @Override
    public int getItemCount() {
        return titles.size();
    }

 ////// //==================///////==============/////////////===========

    public class ViewHolder  extends RecyclerView.ViewHolder {
        TextView notetitle,notecontent;
        View view;
        CardView mcardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle =itemView.findViewById(R.id.titles);
            notecontent =itemView.findViewById(R.id.content);
            mcardView=itemView.findViewById(R.id.notecard);
            view=itemView;

        }
    }
}
