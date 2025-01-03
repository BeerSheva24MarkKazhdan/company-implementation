package telran.employees.db.jpa;

import org.json.JSONObject;

import jakarta.persistence.*;
import telran.employees.Employee;
@Entity
public class ManagerEntity extends EmployeeEntity {
    private float factor;
    @Override
    protected void fromEmployeeDto(Employee empl) {
        //TODO
        //filling relevat fields,
    }
    @Override
    protected void toJsonObject(JSONObject jsonObj) {
        //TODO
        //put appropriate filds to JSONObject,
    }
}