package com.example.sundo_project_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sundo_project_app.model.Project;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    private List<Project> projectList;

    public ProjectAdapter(List<Project> projectList) {
        this.projectList = projectList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Project project = projectList.get(position);
        holder.projectNameTextView.setText(project.getProjectName());
        holder.registrationDateTextView.setText(project.getRegistrationDate());
        holder.projectCheckBox.setChecked(project.isChecked());

        // CheckBox의 클릭 이벤트를 설정합니다.
        holder.projectCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            project.setChecked(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    // ViewHolder 클래스 정의
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView projectNameTextView;
        TextView registrationDateTextView;
        CheckBox projectCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            projectNameTextView = itemView.findViewById(R.id.projectName);
            registrationDateTextView = itemView.findViewById(R.id.registrationDate);
            projectCheckBox = itemView.findViewById(R.id.projectCheckBox);
        }
    }
}
