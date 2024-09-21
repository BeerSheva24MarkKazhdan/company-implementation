package telran.employees;

public class Employee {
    private long id;
    private int basicSalary;
    private String department;
    public Employee(long id, int basicSalary, String department) {
        this.id = id;
        this.basicSalary = basicSalary;
        this.department = department;
    }
    public int computeSalary() {
        return basicSalary;
    }
    public long getId() {
        return id;
    }
    public String getDepartment() {
        return department;
    }
    @Override
    public boolean equals(Object obj) {
        boolean checker = false;
        if (obj != null && getClass() == obj.getClass()){
            Employee other = (Employee) obj;
            checker = this.id == other.id;
        }
        
        return checker;
    }
}
