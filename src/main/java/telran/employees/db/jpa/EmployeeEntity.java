package telran.employees.db.jpa;

import org.json.JSONObject;
import jakarta.persistence.*;
import telran.employees.*;

@Table(name = "employees")
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class EmployeeEntity {
    @Id
    private long id;
    @Column(name = "basic_salary")
    private int basicSalary;
    private String department;

    protected void fromEmployeeDto(Employee empl) {
        this.id = empl.getId();
        this.basicSalary = empl.getBasicSalary();
        this.department = empl.getDepartment();
    }

    protected void toJsonObject(JSONObject jsonObj) {
        jsonObj.put("id", id);
        jsonObj.put("basicSalary", basicSalary);
        jsonObj.put("department", department);
    }
}