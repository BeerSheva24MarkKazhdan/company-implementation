package telran.employees.db.jpa;

import org.json.JSONObject;

import telran.employees.Employee;

public class EmployeesMapper {
    private static final String PACKAGE = "telran.employees.";
    private static final String CLASS_NAME = "className";

    public static Employee toEmployeeDtoFromEntity(EmployeeEntity entity) {
        String entityClassName = entity.getClass().getSimpleName();
        String dtoClassName = PACKAGE + entityClassName.replaceAll("Entity", "");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(CLASS_NAME, dtoClassName);
        entity.toJsonObject(jsonObj);
        return Employee.getEmployeeFromJSON(jsonObj.toString());
    }
    public static EmployeeEntity toEmployeeEntityFromDto(Employee empl) {
        //TODO
        return null;
    }
}