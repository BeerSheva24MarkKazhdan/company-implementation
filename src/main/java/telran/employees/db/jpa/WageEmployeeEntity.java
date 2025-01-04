package telran.employees.db.jpa;

import jakarta.persistence.Entity;
import org.json.JSONObject;
import telran.employees.*;


@Entity
public class WageEmployeeEntity extends EmployeeEntity {
    private int wage;
    private int hours;

    @Override
    protected void fromEmployeeDto(Employee empl) {
        super.fromEmployeeDto(empl);
        if (!(empl instanceof WageEmployee)) {
            throw new IllegalArgumentException("Expected an instance of WageEmployee, but got: " + empl.getClass().getSimpleName());
        }
        this.wage = ((WageEmployee) empl).getWage();
        this.hours = ((WageEmployee) empl).getHours();
    }

    @Override
    protected void toJsonObject(JSONObject jsonObj) {
        super.toJsonObject(jsonObj);
        jsonObj.put("wage", wage);
        jsonObj.put("hours", hours);
    }
}