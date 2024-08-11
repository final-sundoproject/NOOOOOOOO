package com.example.sundo_project_app;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sundo_project_app.api.ProjectApi;
import com.example.sundo_project_app.model.Project;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddbusinessActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProjectAdapter projectAdapter;
    private List<Project> projectList = new ArrayList<>();
    private ProjectApi apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_business);  // 기존의 add_business.xml 레이아웃 파일 사용

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        projectAdapter = new ProjectAdapter(projectList);
        recyclerView.setAdapter(projectAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000") // 서버의 기본 URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ProjectApi.class);

        loadProjects();

        Button addProjectButton = findViewById(R.id.addProjectButton);
        Button deleteProjectButton = findViewById(R.id.deleteProjectButton);

        addProjectButton.setOnClickListener(v -> showAddProjectDialog());
        deleteProjectButton.setOnClickListener(v -> deleteSelectedProjects());
    }

    private void showAddProjectDialog() {
        // Dialog 생성
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.modal_dialog);  // modal_dialog.xml 레이아웃 파일 사용

        EditText projectNameInput = dialog.findViewById(R.id.projectNameInput);
        Button submitProjectButton = dialog.findViewById(R.id.submitProjectButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);

        // 추가 버튼 클릭 시
        submitProjectButton.setOnClickListener(v -> {
            String projectName = projectNameInput.getText().toString().trim();
            if (!projectName.isEmpty()) {
                addProject(projectName);
                dialog.dismiss();
            } else {
                Toast.makeText(AddbusinessActivity.this, "사업명을 입력하세요", Toast.LENGTH_SHORT).show();
            }
        });

        // 취소 버튼 클릭 시
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void loadProjects() {
        apiService.getProjects().enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                if (response.isSuccessful()) {
                    projectList.clear();
                    projectList.addAll(response.body());
                    projectAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                Toast.makeText(AddbusinessActivity.this, "Failed to load projects", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addProject(String projectName) {
        Project newProject = new Project();
        newProject.setProjectName(projectName);
        newProject.setRegistrationDate("2024-04-24 10:33:00"); // 예시로 고정된 날짜

        apiService.addProject(newProject).enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                if (response.isSuccessful()) {
                    projectList.add(response.body());
                    projectAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                Toast.makeText(AddbusinessActivity.this, "Failed to add project", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteSelectedProjects() {
        List<Project> toDelete = new ArrayList<>();
        for (Project project : projectList) {
            if (project.isChecked()) {
                toDelete.add(project);
            }
        }

        for (Project project : toDelete) {
            apiService.deleteProject(project.getProjectId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        projectList.remove(project);
                        projectAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(AddbusinessActivity.this, "Failed to delete project", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
