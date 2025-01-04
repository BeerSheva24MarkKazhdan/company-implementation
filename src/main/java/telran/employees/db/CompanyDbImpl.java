package telran.employees.db;

import telran.employees.*;
import telran.employees.db.jpa.EmployeeEntity;
import telran.employees.db.jpa.EmployeesMapper;

import java.util.Comparator;
import java.util.Iterator;

public class CompanyDbImpl implements Company {
    private CompanyRepository repository;

    public CompanyDbImpl(CompanyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Iterator<Employee> iterator() {
        return repository.getEmployees().iterator();
    }

    @Override
    public void addEmployee(Employee empl) {
        EmployeeEntity entity = EmployeesMapper.toEmployeeEntityFromDto(empl);
        repository.getEmployees().add(EmployeesMapper.toEmployeeDtoFromEntity(entity));
    }

    @Override
    public Employee getEmployee(long id) {
        EmployeeEntity entity = repository.getEmployees().stream()
                .filter(e -> e.getId() == id)
                .map(EmployeesMapper::toEmployeeEntityFromDto)
                .findFirst()
                .orElse(null);
        return entity != null ? EmployeesMapper.toEmployeeDtoFromEntity(entity) : null;
    }

    @Override
    public Employee removeEmployee(long id) {
        EmployeeEntity entity = repository.getEmployees().stream()
                .filter(e -> e.getId() == id)
                .map(EmployeesMapper::toEmployeeEntityFromDto)
                .findFirst()
                .orElse(null);

        if (entity != null) {
            repository.getEmployees().removeIf(e -> e.getId() == id);
            return EmployeesMapper.toEmployeeDtoFromEntity(entity);
        }
        return null;
    }

    @Override
    public int getDepartmentBudget(String department) {
        return repository.getEmployees().stream()
                .filter(e -> e.getDepartment().equals(department))
                .mapToInt(Employee::computeSalary)
                .sum();
    }


    @Override
    public String[] getDepartments() {
        return repository.getEmployees().stream()
                .map(Employee::getDepartment)
                .distinct()
                .toArray(String[]::new);
    }

    @Override
    public Manager[] getManagersWithMostFactor() {
        return repository.getEmployees().stream()
                .filter(e -> e instanceof Manager)
                .map(e -> (Manager) e)
                .max(Comparator.comparing(Manager::getFactor))
                .map(m -> new Manager[]{m})
                .orElse(new Manager[0]);
    }
}