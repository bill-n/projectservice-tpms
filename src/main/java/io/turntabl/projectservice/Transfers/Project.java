package io.turntabl.projectservice.Transfers;

import org.springframework.stereotype.Component;

import java.sql.Date;


public class Project {
    private Integer project_id;
    private String project_name;


    public Project() {
    }

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }
}
