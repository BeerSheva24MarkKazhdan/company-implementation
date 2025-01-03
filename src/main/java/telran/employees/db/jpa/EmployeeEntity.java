package telran.employees.db.jpa;

import org.json.JSONObject;

import jakarta.persistence.*;
import telran.employees.*;
@Table(name="employees")
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class EmployeeEntity {
    @Id
    private long id;
    @Column(name="basic_salary")
    private int basicSalary;
    private String department;
    protected void fromEmployeeDto(Employee empl) {
        //TODO
        //filling relevat fields, example: id = empl.getId();...
    }
    protected void toJsonObject(JSONObject jsonObj) {
        //TODO
        //put appropriate filds to JSONObject, example:jsonObj.put("id", id)
    }
}