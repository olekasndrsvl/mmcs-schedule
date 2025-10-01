package com.alexanderl.mmcs_schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexanderl.mmcs_schedule.dataTransferObjects.Lesson;
public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.ViewHolder>
{
    private final Lesson[] lesson;
    private final LayoutInflater inflater;

    public LessonAdapter(Context c, Lesson[] l)
    {
        lesson=l;
        this.inflater = LayoutInflater.from(c);
    }

    @NonNull
    @Override
    public LessonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.lesson_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonAdapter.ViewHolder holder, int position) {

        holder.info.setText(lesson[position].getAdditional_info());
        holder.time_b.setText(lesson[position].getTbegin().toString());
        holder.time_e.setText(lesson[position].getTend().toString());
        holder.curriculaname.setText(lesson[position].getCurriculaName());
    }

    @Override
    public int getItemCount() {
        return lesson.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView time_b;
        public final TextView time_e;
        public final TextView curriculaname;
        public final TextView info;

        ViewHolder(View view)
        {
            super(view);
            time_b=view.findViewById(R.id.tv_begin_time);
            time_e=view.findViewById(R.id.tv_end_time);
            curriculaname=view.findViewById(R.id.tv_primary_text);
            info=view.findViewById(R.id.tv_info);
        }
    }
}
