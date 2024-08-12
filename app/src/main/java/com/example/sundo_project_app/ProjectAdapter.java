package com.example.sundo_project_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sundo_project_app.model.Project;
import java.util.ArrayList;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    private List<Project> projectList = new ArrayList<>();
    private List<Project> filteredProjectList = new ArrayList<>(); // 필터링된 리스트

    public ProjectAdapter(List<Project> projectList) {
        this.projectList = new ArrayList<>(projectList);
        this.filteredProjectList = new ArrayList<>(projectList); // 초기에는 원본 데이터와 동일
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Project project = filteredProjectList.get(position);
        holder.projectNameTextView.setText(project.getProjectName());
        holder.registrationDateTextView.setText(project.getRegistrationDate());
        holder.projectCheckBox.setChecked(project.isChecked());

        // CheckBox의 클릭 이벤트를 설정합니다.
        holder.projectCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            project.setChecked(isChecked);
        });

        // 전체 레이아웃 클릭 시 CheckBox의 상태 변경
        holder.itemView.setOnClickListener(v -> {
            holder.projectCheckBox.setChecked(!holder.projectCheckBox.isChecked());
        });
    }

    @Override
    public int getItemCount() {
        return filteredProjectList.size();
    }

    public void filter(String text) {
        filteredProjectList.clear();
        if (text.isEmpty()) {
            filteredProjectList.addAll(projectList); // 검색어가 없을 때는 원본 리스트 전체를 보여줌
        } else {
            for (Project project : projectList) {
                if (project.getProjectName().toLowerCase().contains(text.toLowerCase())) {
                    filteredProjectList.add(project); // 검색어가 포함된 항목을 추가
                }
            }
        }
        notifyDataSetChanged(); // RecyclerView 업데이트
    }

    public void updateProjectList(List<Project> newProjectList) {
        this.projectList.clear();
        this.projectList.addAll(newProjectList);
        filter("");  // 전체 데이터를 반영하여 RecyclerView를 갱신
    }

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
