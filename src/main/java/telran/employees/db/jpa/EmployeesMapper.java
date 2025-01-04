package telran.employees.db.jpa;

import org.json.JSONObject;

import telran.employees.Employee;


public class EmployeesMapper {
    private static final String PACKAGE = "telran.employees.";
    private static final String CLASS_NAME = "className";

    public static Employee toEmployeeDtoFromEntity(EmployeeEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("EmployeeEntity cannot be null");
        }
        String entityClassName = entity.getClass().getSimpleName();
        if (!entityClassName.endsWith("Entity")) {
            throw new IllegalArgumentException("Class name does not follow expected format: " + entityClassName);
        }
        String dtoClassName = PACKAGE + entityClassName.replaceAll("Entity", "");
        try {
            Class.forName(dtoClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("DTO class not found: " + dtoClassName, e);
        }
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(CLASS_NAME, dtoClassName);
        entity.toJsonObject(jsonObj);
        try {
            return Employee.getEmployeeFromJSON(jsonObj.toString());
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to Employee DTO", e);
        }
    }

    public static EmployeeEntity toEmployeeEntityFromDto(Employee empl) {
        if (empl == null) {
            throw new IllegalArgumentException("Employee DTO cannot be null");
        }
        String simpleClassName = empl.getClass().getSimpleName();
        String classNameWithEntity = simpleClassName + "Entity";
        String fullClassName = PACKAGE + "db.jpa." + classNameWithEntity;
        try {
            Class<?> clazz = Class.forName(fullClassName);
            if (!EmployeeEntity.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException("Class " + fullClassName + " is not a subclass of EmployeeEntity");
            }
            EmployeeEntity entity = (EmployeeEntity) clazz.getDeclaredConstructor().newInstance();
            entity.fromEmployeeDto(empl);
            return entity;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + simpleClassName, e);
        } catch (Exception e) {
            throw new RuntimeException("Error converting DTO to entity" + simpleClassName, e);
        }
    }
}