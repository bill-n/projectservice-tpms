
CREATE TABLE employee (

	employee_id serial primary key,
	employee_firstName text not null,
	employee_lastName text not null,
	employee_phoneNumber text not null,
	employee_email text not null,
	employee_address text not null,
	employee_role text not null,
	employee_dev_level text not null,
	employee_hire_date DATE not null,
	employee_onLeave BOOLEAN not null,
	employee_gender text not null,
	employee_status text not null
);

CREATE TABLE project (
	project_id serial primary key,
	project_name text not null,
);




create table LoggedProject (

	project_date DATE,
	emp_id int references employees (emp_id),
	project_id int references projects (project_id),
	project_hours int,
	primary key (project_date,project_id)
	);

create table LoggedSick (

	sick_date DATE,
	emp_id int references employees(emp_id),
	primary key(sick_date, emp_id)
);

create table LoggedVacation (

	vacation_date DATE,
	emp_id int references employees (emp_id),
	primary key (vacation_date, emp_id)
);


CREATE OR REPLACE FUNCTION loggedChart (startdate DATE, enddate DATE)
RETURNS TABLE (
	logged_date DATE,
	project_name text,
	logged_Hours bigint
)
LANGUAGE plpgsql
AS $$
BEGIN
	RETURN QUERY
	 select * from (
	    select project_date, projects.title as project_name, sum(project_hours) from loggedProject
		    inner join projects on projects.project_id = loggedProject.project_id group by project_date, project_name
		union
	select sick_date,'sick' as project_name, sum(8) from loggedSick group by sick_date
 		union
	select vacation_date,'vacation' as project_name, sum(8) from loggedVacation group by vacation_date)
 		AS foo where project_date between startdate and enddate;

END;
$$;
