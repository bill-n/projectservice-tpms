package io.turntabl.projectservice.Transfers;

public class EmployeeProject {
    private Integer assignproject_id;
    private Integer project_id;
    private Integer employee_id;
    private String project_name;
    private String employee_firstname;
    private String employee_lastname;
    private String employee_email;

    public EmployeeProject() {
    }

    public Integer getAssignproject_id() {
        return assignproject_id;
    }

    public void setAssignproject_id(Integer assignproject_id) {
        this.assignproject_id = assignproject_id;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public Integer getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(Integer employee_id) {
        this.employee_id = employee_id;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getEmployee_firstname() {
        return employee_firstname;
    }

    public void setEmployee_firstname(String employee_firstname) {
        this.employee_firstname = employee_firstname;
    }

    public String getEmployee_lastname() {
        return employee_lastname;
    }

    public void setEmployee_lastname(String employee_lastname) {
        this.employee_lastname = employee_lastname;
    }

    public String getEmployee_email() {
        return employee_email;
    }

    public void setEmployee_email(String employee_email) {
        this.employee_email = employee_email;
    }
}
