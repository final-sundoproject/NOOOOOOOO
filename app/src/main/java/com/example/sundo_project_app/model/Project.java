package com.example.sundo_project_app.model;

public class Project {

    private int projectId;  // 프로젝트 ID
    private String projectName;
    private int companyCode;
    private String registrationDate;
    private boolean isChecked;  // 선택 상태를 관리하기 위한 필드

    public Project() {
        // 기본 생성자 (Retrofit과 같은 라이브러리에서 필요할 수 있음)
    }

    public Project(String projectName, int companyCode, String registrationDate) {
        this.projectName = projectName;
        this.companyCode = companyCode;
        this.registrationDate = registrationDate;
    }

    public Project(int projectId, String projectName, int companyCode, String registrationDate) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.companyCode = companyCode;
        this.registrationDate = registrationDate;
    }

    // Getter 및 Setter 메서드

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(int companyCode) {
        this.companyCode = companyCode;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
