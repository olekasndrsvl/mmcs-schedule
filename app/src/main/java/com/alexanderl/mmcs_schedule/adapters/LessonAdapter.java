package com.alexanderl.mmcs_schedule.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexanderl.mmcs_schedule.R;
import com.alexanderl.mmcs_schedule.dataTransferObjects.Lesson;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.ViewHolder> {
    private final Lesson[] lesson;
    private final LayoutInflater inflater;
    private OnLessonClickListener onLessonClickListener;

    // Интерфейс для обработки кликов
    public interface OnLessonClickListener {
        void onLessonClick(View view, Lesson lesson, int position);
    }

    public LessonAdapter(Context c, Lesson[] l) {
        lesson = l;
        this.inflater = LayoutInflater.from(c);
    }

    // Метод для установки слушателя кликов
    public void setOnLessonClickListener(OnLessonClickListener listener) {
        this.onLessonClickListener = listener;
    }

    @NonNull
    @Override
    public LessonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.lesson_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonAdapter.ViewHolder holder, int position) {
        Lesson currentLesson = lesson[position];

        holder.info.setText(currentLesson.getAdditional_info());
        holder.time_b.setText(currentLesson.getTbegin().toString());
        holder.time_e.setText(currentLesson.getTend().toString());
        holder.curriculaname.setText(currentLesson.getCurriculaName());

        // Добавляем обработчик клика на весь элемент
        holder.itemView.setOnClickListener(v -> {
            if (onLessonClickListener != null) {
                onLessonClickListener.onLessonClick(v, currentLesson, position);
            }
        });
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

        ViewHolder(View view) {
            super(view);
            time_b = view.findViewById(R.id.tv_begin_time);
            time_e = view.findViewById(R.id.tv_end_time);
            curriculaname = view.findViewById(R.id.tv_primary_text);
            info = view.findViewById(R.id.tv_info);
        }
    }
}