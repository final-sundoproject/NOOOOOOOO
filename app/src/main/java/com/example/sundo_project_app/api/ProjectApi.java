package com.example.sundo_project_app.api;

import com.example.sundo_project_app.model.Project;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ProjectApi {

    @GET("projects")
    Call<List<Project>> getProjects();

    @POST("projects")
    Call<Project> addProject(@Body Project project);

    @DELETE("projects/{id}")
    Call<Void> deleteProject(@Path("id") int projectId);
}
