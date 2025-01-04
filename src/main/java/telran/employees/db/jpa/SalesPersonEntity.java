package telran.employees.db.jpa;

import jakarta.persistence.Entity;
import org.json.JSONObject;
import telran.employees.*;

@Entity
public class SalesPersonEntity extends WageEmployeeEntity {
    private float percent;
    private long sales;

    @Override
    protected void fromEmployeeDto(Employee empl) {
        super.fromEmployeeDto(empl);
        if (!(empl instanceof SalesPerson)) {
            throw new IllegalArgumentException("Expected an instance of SalesPerson, but got: " + empl.getClass().getSimpleName());
        }
        this.percent = ((SalesPerson) empl).getPercent();
        this.sales = ((SalesPerson) empl).getSales();
    }

    @Override
    protected void toJsonObject(JSONObject jsonObj) {
        super.toJsonObject(jsonObj);
        jsonObj.put("percent", percent);
        jsonObj.put("sales", sales);
    }
}