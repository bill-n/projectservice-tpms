package io.turntabl.projectservice.Controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import io.turntabl.projectservice.Models.AddProject;
import io.turntabl.projectservice.Models.AssignedProjectTable;
import io.turntabl.projectservice.Transfers.EmployeeProject;
import io.turntabl.projectservice.Transfers.Project;
import io.turntabl.projectservice.Utilities.Parsor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Api
@RestController
public class ProjectController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    Parsor parsor = new Parsor();
    Date date = new Date();

    @ApiOperation("Add New Project")
    @CrossOrigin(origins = "*")
    @PostMapping("/v1/api/project")

    public Map<String, Object> addProject(@RequestBody AddProject requestData) {

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> request = new HashMap<>();
        request.put("project_name",requestData.getProject_name());

        try{

            List<String> requiredParams = Arrays.asList(
                    "project_name"
            );

            Map<String, Object> result = parsor.validate_params(request,requiredParams);
            if (result.get("code").equals("00")){
                SimpleJdbcInsert insertActor = new SimpleJdbcInsert(jdbcTemplate).withTableName("project").usingGeneratedKeyColumns("project_id");

                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("project_name",requestData.getProject_name());

                Number key = insertActor.executeAndReturnKey(parameters);
                if (key != null){
                       response.put("code","00");
                       response.put("msg","New project added successfully");
                }else {
                    response.put("code","01");
                    response.put("msg","Failed to add new project, try again later");
                }
            }else {
                response.put("code",result.get("code"));
                response.put("msg",result.get("msg"));
            }
        }catch (Exception e){
            e.printStackTrace();
            response.put("code","02");
            response.put("msg","Something went wrong, try again later");
        }
        return response;
    }

    @ApiOperation("List of Projects")
    @CrossOrigin(origins = "*")
    @GetMapping("/v1/api/projects")

    public Map<String, Object> getAllProjects(){
        Map<String, Object> response = new HashMap<>();

        try{
            List<Project> projectList =  jdbcTemplate.query(
                    "select * from project",
                    BeanPropertyRowMapper.newInstance(Project.class)
            );
            response.put("code","00");
            response.put("msg","Data retrieved successfully");
            response.put("data",projectList);
        }catch (Exception e){
            e.printStackTrace();
            response.put("code","02");
            response.put("msg","Something went wrong, try again later");
        }
        return response;
    }


    @ApiOperation("Assign Project to Employee")
    @CrossOrigin(origins = "*")
    @PostMapping("/v1/api/project/assign/employee")

    public Map<String, Object> assignProjects(@RequestBody AssignedProjectTable assignedProjectTable ){

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> request = new HashMap<>();
        request.put("project_id",assignedProjectTable.getProject_id());
        request.put("employee_id",assignedProjectTable.getEmployee_id());
        request.put("employee_firstname",assignedProjectTable.getEmployee_firstname());
        request.put("employee_lastname",assignedProjectTable.getEmployee_lastname());
        request.put("employee_email",assignedProjectTable.getEmployee_email());

        try{
            List<String> requiredParams = Arrays.asList(
                    "project_id",
                    "employee_id",
                    "employee_firstname",
                    "employee_lastname",
                    "employee_email"
            );
            Map<String, Object> result = parsor.validate_params(request,requiredParams);
            if (result.get("code").equals("00")){
                List<AssignedProjectTable> assignedProjectTables =  jdbcTemplate.query(
                        "select * from assignproject where employee_id = ?",
                        new Object[]{assignedProjectTable.getEmployee_id()},
                        BeanPropertyRowMapper.newInstance(AssignedProjectTable.class)
                );
                List<Integer> existingAssgnProjects = new ArrayList<>();
                for(AssignedProjectTable assignedProjectTable1 :assignedProjectTables){
                    if (assignedProjectTable1.getProject_id().equals(assignedProjectTable.getProject_id())){
                        existingAssgnProjects.add(assignedProjectTable1.getProject_id());
                    }
                }
                if (existingAssgnProjects.isEmpty()){

                    int resp = jdbcTemplate.update(
                            "insert into assignproject(employee_id,project_id,employee_firstname,employee_lastname,employee_email) values(?,?,?,?,?)",
                            new Object[]{
                                    assignedProjectTable.getEmployee_id(),
                                    assignedProjectTable.getProject_id(),
                                    assignedProjectTable.getEmployee_firstname(),
                                    assignedProjectTable.getEmployee_lastname(),
                                    assignedProjectTable.getEmployee_email()
                            }
                    );
                    if(resp > 0){
                        response.put("code","00");
                        response.put("msg","Project assigned successfully");
                    }else {
                        response.put("code","01");
                        response.put("msg","Failed to assign project to an employee");
                    }
                }else{
                    response.put("code","01");
                    response.put("msg","Employee has already been assigned to this project");
                }
            }else {
                response.put("code",result.get("code"));
                response.put("msg",result.get("msg"));
            }
        }catch (Exception e){
            e.printStackTrace();
            response.put("code","02");
            response.put("msg","Something went wrong, try again later");
        }
        return response;
    }

    @ApiOperation("Get Assigned Projects By Employee Id")
    @CrossOrigin(origins = "*")
    @GetMapping("/v1/api/projects/assigned/employee/{id}")
    public Map<String, Object> getEmployeeAssignedProjects(@PathVariable("id") Integer id){

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> request = new HashMap<>();
        request.put("id",id);

        try{
            List<String> requiredParams = Arrays.asList(
                    "id"
            );
            Map<String, Object> valid = parsor.validate_params(request,requiredParams);
            if (valid.get("code").equals("00")){

                    List<EmployeeProject> employeeProjects = jdbcTemplate.query(
                            "select project.project_name, project.project_id,assignproject.employee_id,assignproject.assignproject_id,assignproject.employee_firstname,assignproject.employee_lastname,assignproject.employee_email from project inner join assignproject on project.project_id = assignproject.project_id where assignproject.employee_id = ? ",
                            new Object[]{id},
                            BeanPropertyRowMapper.newInstance(EmployeeProject.class)
                    );
                    response.put("code","00");
                    response.put("msg","Data retrieved successfully");
                    response.put("data",employeeProjects);

            }else {
                response.put("code",valid.get("code"));
                response.put("msg",valid.get("msg"));
            }
        }catch (Exception e){
            e.printStackTrace();
            response.put("code","02");
            response.put("msg","Something went wrong, try again later");
        }
        return response;
    }


//    @ApiOperation("Remove Project on Employee")
//    @CrossOrigin(origins = "*")
//    @GetMapping("/v1/api/project/{project_id}/remove/employee/{employee_id}")
//    @Override
//    public Map<String, Object> removeProject(@PathVariable("project_id") Integer project_id, @PathVariable("employee_id") Integer employee_id){
//
//        Map<String, Object> response = new HashMap<>();
//        Map<String, Object> request = new HashMap<>();
//        request.put("project_id",project_id);
//        request.put("employee_id",employee_id);
//        try{
//            List<String> requiredParams = Arrays.asList(
//                    "project_id",
//                    "employee_id"
//            );
//            Map<String, Object> result = parsor.validate_params(request,requiredParams);
//            if (result.get("code").equals("00")){
//                List<AssignedProjectTable> assignedProjectTables =  jdbcTemplate.query(
//                        "select * from assignedproject where employee_id = ?",
//                        new Object[]{employee_id},
//                        BeanPropertyRowMapper.newInstance(AssignedProjectTable.class)
//                );
//                List<Integer> existingAssgnProjects = new ArrayList<>();
//                for(AssignedProjectTable assignedProjectTable :assignedProjectTables){
//                    if (assignedProjectTable.getProject_id().equals(project_id)){
//                        existingAssgnProjects.add(assignedProjectTable.getProject_id());
//                    }
//                }
//                if (!existingAssgnProjects.isEmpty()){
//
//                    int resp = jdbcTemplate.update(
//                            "delete from assignedproject where employee_id = ? and project_id = ? ",
//                            new Object[]{
//                                    employee_id,
//                                    project_id
//                            }
//                    );
//                    if(resp > 0){
//                        response.put("code","00");
//                        response.put("msg","Project removed on employee successfully");
//                    }else {
//                        response.put("code","01");
//                        response.put("msg","Failed to remove project on employee");
//                    }
//                }else{
//                    response.put("code","01");
//                    response.put("msg","Employee has not yet been assigned to this project");
//                }
//            }else {
//                response.put("code",result.get("code"));
//                response.put("msg",result.get("msg"));
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            response.put("code","02");
//            response.put("msg","Something went wrong, try again later");
//        }
//        return response;
//    }
//

//    private SingleEmployeeAssignedProjectTO SingleEmployeeAssignedProjectTOrowMappper(Employee employee, List<EmployeeProject> projectTOS ) throws SQLException {
//        SingleEmployeeAssignedProjectTO  singleEmployeeAssignedProjectTO = new SingleEmployeeAssignedProjectTO();
//        singleEmployeeAssignedProjectTO.setEmployee(employee);
//        singleEmployeeAssignedProjectTO.setProjects(projectTOS);
//        return singleEmployeeAssignedProjectTO;
//    }
//
//    @ApiOperation("Activate Project on Employee")
//    @CrossOrigin(origins = "*")
//    @GetMapping("/v1/api/project/{project_id}/active/employee/{employee_id}")
//    @Override
//    public Map<String, Object> activateEmployeeProjects(@PathVariable("project_id") Integer project_id, @PathVariable("employee_id") Integer employee_id){
//
//        Map<String, Object> response = new HashMap<>();
//        Map<String, Object> request = new HashMap<>();
//        request.put("project_id",project_id);
//        request.put("employee_id",employee_id);
//        try{
//            List<String> requiredParams = Arrays.asList(
//                    "project_id",
//                    "employee_id"
//            );
//            Map<String, Object> result = parsor.validate_params(request,requiredParams);
//            if (result.get("code").equals("00")){
//                int resp = jdbcTemplate.update(
//                        "update assignedproject set isworkingon = ? where employee_id = ? and project_id = ?",
//                        new Object[]{
//                                true,
//                                employee_id,
//                                project_id,
//                        }
//                );
//                if(resp > 0){
//                    response.put("code","00");
//                    response.put("msg","Project activated successfully on employee");
//                }else {
//                    response.put("code","01");
//                    response.put("msg","Failed to activate project on an employee");
//                }
//            }else {
//                response.put("code",result.get("code"));
//                response.put("msg",result.get("msg"));
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            response.put("code","02");
//            response.put("msg","Something went wrong, try again later");
//        }
//        return response;
//    }
//    @ApiOperation("Deactivate Project on Employee")
//    @CrossOrigin(origins = "*")
//    @GetMapping("/v1/api/project/{project_id}/deactive/employee/{employee_id}")
//    @Override
//    public Map<String, Object> deActivateEmployeeProjects(@PathVariable("project_id") Integer project_id, @PathVariable("employee_id") Integer employee_id){
//
//        Map<String, Object> response = new HashMap<>();
//        Map<String, Object> request = new HashMap<>();
//        request.put("project_id",project_id);
//        request.put("employee_id",employee_id);
//        try{
//            List<String> requiredParams = Arrays.asList(
//                    "project_id",
//                    "employee_id"
//            );
//            Map<String, Object> result = parsor.validate_params(request,requiredParams);
//            if (result.get("code").equals("00")){
//                int resp = jdbcTemplate.update(
//                        "update assignedproject set isworkingon = ? where employee_id = ? and project_id = ?",
//                        new Object[]{
//                                false,
//                                employee_id,
//                                project_id,
//                        }
//                );
//                if(resp > 0){
//                    response.put("code","00");
//                    response.put("msg","Project deactivated successfully on employee");
//                }else {
//                    response.put("code","01");
//                    response.put("msg","Failed to deactivate project on an employee");
//                }
//            }else {
//                response.put("code",result.get("code"));
//                response.put("msg",result.get("msg"));
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            response.put("code","02");
//            response.put("msg","Something went wrong, try again later");
//        }
//        return response;
//    }
//
//    @Override
//    @ApiOperation("Edit Project Details")
//    @CrossOrigin(origins = "*")
//    @PutMapping("/v1/api/project")
//    public Map<String, Object> updateProjectDetails(@RequestBody EditProject editProject){
//        List<SingleProfileTO> result = new ArrayList<>();
//        Map<String, Object> response = new HashMap<>();
//        Map<String, Object> request = new HashMap<>();
//        request.put("project_id",editProject.getProject_id());
//        request.put("project_name",editProject.getProject_name());
//        request.put("project_description",editProject.getProject_description());
//        request.put("project_start_date",editProject.getProject_start_date());
//        request.put("project_end_date",editProject.getProject_end_date());
//
//        try{
//            List<String> requiredParams = Arrays.asList(
//                    "project_id"
//            );
//            Map<String, Object> valid = parsor.validate_params(request,requiredParams);
//            if (valid.get("code").equals("00")){
//                Map<String, Object> updated_params = this.check_updated_params(editProject);
//                if (updated_params.get("code").equals("00")){
//                    UpdateProject updateProject = (UpdateProject) updated_params.get("data");
//                    jdbcTemplate.update(
//                            "update project set project_name = ?, project_description = ?, project_start_date = ?, project_end_date = ? where project_id = ?",
//                            new Object[]{
//                                    updateProject.getProject_name(),
//                                    updateProject.getProject_description(),
//                                    updateProject.getProject_start_date(),
//                                    updateProject.getProject_end_date(),
//                                    editProject.getProject_id()
//                            }
//                    );
//                    List<Integer> project_current_tech = updateProject.getProject_tech_stack();
//
//                    jdbcTemplate.update(
//                            "delete from techproject where project_id = ?",
//                            new Object[]{
//                                    editProject.getProject_id()
//                            }
//                    );
//
//                    for(Integer tech: project_current_tech){
//                        jdbcTemplate.update(
//                                "insert into techproject(tech_id,project_id) values(?,?)",
//                                new Object[]{
//                                        tech,
//                                        editProject.getProject_id()
//                                }
//                        );
//                    }
//
//                    response.put("code","00");
//                    response.put("msg","Project details updated successfully");
//
//                }else {
//                    response.put("code",valid.get("code"));
//                    response.put("msg",valid.get("msg"));
//                }
//            }else {
//                response.put("code",valid.get("code"));
//                response.put("msg",valid.get("msg"));
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            response.put("code","02");
//            response.put("msg","Something went wrong, try again later");
//        }
//        return response;
//    }
//
//    private Map<String, Object> check_updated_params(EditProject editProject){
//        Map<String, Object> response = new HashMap<>();
//        UpdateProject updateProject = new UpdateProject();
//        try{
//            Integer project_id = editProject.getProject_id();
//            List<Project> project = jdbcTemplate.query(
//                    "select * from project where project_id = ?",
//                    new Object[]{project_id},
//                    BeanPropertyRowMapper.newInstance(Project.class)
//            );
//            List<Tech> techStack = jdbcTemplate.query(
//
//                    "select * from tech inner join techproject on tech.tech_id = techproject.tech_id inner join project on techproject.project_id = project.project_id where project.project_id = ? ",
//                    new Object[]{project_id},
//                    BeanPropertyRowMapper.newInstance(Tech.class)
//            );
//            List<Integer> techs = techStack.stream()
//                    .map(tech -> tech.getTech_id())
//                    .collect(Collectors.toList());
//
//            if (!project.isEmpty()){
//                Project projectDetails = project.get(0);
//                if(editProject.getProject_name().isEmpty()){
//                    updateProject.setProject_name(projectDetails.getProject_name());
//                }else {
//                    updateProject.setProject_name(editProject.getProject_name());
//                }
//                if(editProject.getProject_description().isEmpty()){
//                    updateProject.setProject_description(projectDetails.getProject_description());
//                }else {
//                    updateProject.setProject_description(editProject.getProject_description());
//                }
//                if(editProject.getProject_start_date().isEmpty()){
//                    updateProject.setProject_start_date(projectDetails.getProject_start_date());
//                }else {
//                    java.sql.Date start_date = java.sql.Date.valueOf(editProject.getProject_start_date());
//                    updateProject.setProject_start_date(start_date);
//                }
//                if(editProject.getProject_end_date().isEmpty()){
//                    updateProject.setProject_end_date(projectDetails.getProject_end_date());
//                }else {
//                    java.sql.Date end_date = java.sql.Date.valueOf(editProject.getProject_end_date());
//                    updateProject.setProject_end_date(end_date);
//                }
//                if(editProject.getProject_tech_stack().isEmpty()){
//                    updateProject.setProject_tech_stack(techs);
//                }else {
//                    updateProject.setProject_tech_stack(editProject.getProject_tech_stack());
//                }
//                response.put("code","00");
//                response.put("msg","Data retrieved successfully");
//                response.put("data",updateProject);
//
//            }else {
//                response.put("code","01");
//                response.put("msg","Project data with this ID ["+project_id+"] doesn't exist");
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            response.put("code","02");
//            response.put("msg","Something went wrong, try again later");
//        }
//
//        return response;
//    }
//
//
//
//
//    private SingleProjectTO SingleProjectTOrowMappper(Project project, List<Tech> techStack ) throws SQLException {
//        SingleProjectTO singleProjectTO = new SingleProjectTO();
//        singleProjectTO.setProject(project);
//        singleProjectTO.setTech_stack(techStack);
//        return singleProjectTO;
//    }
//
//    @Override
//    public void deleteProjectRow(Long id){
//        String sql = "delete from project where project_id = ? ";
//        jdbcTemplate.update(
//                sql,
//                new Object[]{id}
//        );
//    }
}
