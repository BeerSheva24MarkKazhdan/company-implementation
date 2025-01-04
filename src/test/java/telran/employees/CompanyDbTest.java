package telran.employees;

import org.junit.jupiter.api.*;

import telran.employees.db.*;

import java.util.*;


import static org.junit.jupiter.api.Assertions.*;


public class CompanyDbTest {
    private static final long ID1 = 123;
    private static final int SALARY1 = 1000;
    private static final String DEPARTMENT1 = "QA";
    private static final long ID2 = 120;
    private static final int SALARY2 = 2000;
    private static final long ID3 = 125;
    private static final int SALARY3 = 3000;
    private static final String DEPARTMENT2 = "Development";
    private static final long ID4 = 200;
    private static final String DEPARTMENT4 = "Audit";
    private static final int SALARY4 = 30000;
    private static final int WAGE1 = 100;
    private static final int HOURS1 = 10;
    private static final float FACTOR1 = 2;
    private static final float PERCENT1 = 0.01f;
    private static final long SALES1 = 10000;

    Employee wageEmployee = new WageEmployee(ID1, SALARY1, DEPARTMENT1, WAGE1, HOURS1);
    Employee manager = new Manager(ID2, SALARY2, DEPARTMENT1, FACTOR1);
    Employee salesPerson = new SalesPerson(ID3, SALARY3, DEPARTMENT2, WAGE1, HOURS1, PERCENT1, SALES1);
    Employee employee = new Employee(ID4, SALARY4, DEPARTMENT4);

    private CompanyRepository repository;
    private CompanyDbImpl companyDb;
    private List<Employee> employeesList;

    @BeforeEach
    public void setUp() {
        employeesList = new ArrayList<>();
        repository = () -> employeesList;
        companyDb = new CompanyDbImpl(repository);
    }

    @Test
    public void testAddEmployee() {
        companyDb.addEmployee(wageEmployee);
        companyDb.addEmployee(manager);
        companyDb.addEmployee(salesPerson);
        companyDb.addEmployee(employee);

        assertEquals(4, employeesList.size());
        assertEquals(wageEmployee.getId(), employeesList.get(0).getId());
        assertEquals(manager.getId(), employeesList.get(1).getId());
        assertEquals(salesPerson.getId(), employeesList.get(2).getId());
        assertEquals(employee.getId(), employeesList.get(3).getId());
    }

    @Test
    void testGetEmployee() {

        employeesList.add(employee);
        employeesList.add(salesPerson);
        employeesList.add(manager);
        employeesList.add(wageEmployee);

        Employee result = companyDb.getEmployee(ID4);
        assertNotNull(result);
        assertEquals(employee.getId(), result.getId());
        assertEquals(employee.getDepartment(), result.getDepartment());

        Employee result2 = companyDb.getEmployee(ID3);
        assertNotNull(result2);
        assertEquals(salesPerson.getId(), result2.getId());
        assertEquals(salesPerson.getDepartment(), result2.getDepartment());

        Employee result3 = companyDb.getEmployee(ID2);
        assertNotNull(result3);
        assertEquals(manager.getId(), result3.getId());
        assertEquals(manager.getDepartment(), result3.getDepartment());

        Employee result4 = companyDb.getEmployee(ID1);
        assertNotNull(result4);
        assertEquals(wageEmployee.getId(), result4.getId());
        assertEquals(wageEmployee.getDepartment(), result4.getDepartment());
    }

    @Test
    void testGetEmployeeNotFound() {
        Employee result = companyDb.getEmployee(999);
        assertNull(result);
    }

    @Test
    void testRemoveEmployee() {
        employeesList.add(employee);
        employeesList.add(salesPerson);
        employeesList.add(manager);
        employeesList.add(wageEmployee);

        Employee removed1 = companyDb.removeEmployee(ID1);
        Employee removed2 = companyDb.removeEmployee(ID2);
        Employee removed3 = companyDb.removeEmployee(ID3);
        Employee removed4 = companyDb.removeEmployee(ID4);

        assertNotNull(removed1);
        assertEquals(wageEmployee.getId(), removed1.getId());

        assertNotNull(removed2);
        assertEquals(manager.getId(), removed2.getId());

        assertNotNull(removed3);
        assertEquals(salesPerson.getId(), removed3.getId());

        assertNotNull(removed4);
        assertEquals(employee.getId(), removed4.getId());

        assertTrue(employeesList.isEmpty());
    }

    @Test
    void testRemoveEmployeeNotFound() {
        Employee removed = companyDb.removeEmployee(999);
        assertNull(removed);
    }

    @Test
    void testGetDepartmentBudget() {
        employeesList.add(employee);
        employeesList.add(salesPerson);
        employeesList.add(manager);
        employeesList.add(wageEmployee);

        int budget = companyDb.getDepartmentBudget("QA");
        assertEquals(6000, budget);

        int budget2 = companyDb.getDepartmentBudget("Development");
        assertEquals(4001, budget2);

        int budget3 = companyDb.getDepartmentBudget("Audit");
        assertEquals(30000, budget3);
    }

    @Test
    void testGetDepartments() {
        employeesList.add(employee);
        employeesList.add(salesPerson);
        employeesList.add(manager);
        employeesList.add(wageEmployee);

        String[] departments = companyDb.getDepartments();

        assertEquals(3, departments.length);
        assertArrayEquals(new String[]{"Audit", "Development", "QA"}, departments);
    }

    @Test
    void testGetManagersWithMostFactor() {
        Manager manager1 = new Manager(1, 7000, "Audit", 1.5f);
        Manager manager2 = new Manager(2, 8000, "Development", 2.0f);
        employeesList.add(manager1);
        employeesList.add(manager2);

        Manager[] managers = companyDb.getManagersWithMostFactor();

        assertEquals(1, managers.length);
        assertEquals(manager2.getId(), managers[0].getId());
        assertEquals(manager2.getFactor(), managers[0].getFactor());
    }

    @Test
    void testGetManagersWithMostFactorNoManagers() {
        employeesList.add(new Employee(999, 5000, "Development"));

        Manager[] managers = companyDb.getManagersWithMostFactor();

        assertEquals(0, managers.length);
    }

    @Test
    void testIterator() {
        employeesList.add(employee);
        employeesList.add(salesPerson);
        employeesList.add(manager);
        employeesList.add(wageEmployee);

        List<Employee> result = new ArrayList<>();
        companyDb.iterator().forEachRemaining(result::add);

        assertEquals(4, result.size());
        assertTrue(result.contains(employee));
        assertTrue(result.contains(salesPerson));
        assertTrue(result.contains(manager));
        assertTrue(result.contains(wageEmployee));
    }
}
