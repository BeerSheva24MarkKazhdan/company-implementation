package telran.employees.db.jpa;

import org.json.JSONObject;

import jakarta.persistence.*;
import telran.employees.*;

@Entity
public class ManagerEntity extends EmployeeEntity {
    private float factor;

    @Override
    protected void fromEmployeeDto(Employee empl) {
        super.fromEmployeeDto(empl);
        if (!(empl instanceof Manager)) {
            throw new IllegalArgumentException("Expected an instance of Manager, but got: " + empl.getClass().getSimpleName());
        }
        this.factor = ((Manager) empl).getFactor();
    }
        @Override
        protected void toJsonObject (JSONObject jsonObj){
            super.toJsonObject(jsonObj);
            jsonObj.put("factor", factor);
        }
    }