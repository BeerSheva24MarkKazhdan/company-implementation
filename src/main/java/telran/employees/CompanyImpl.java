package telran.employees;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.*;
import java.util.function.Supplier;

import telran.io.Persistable;

public class CompanyImpl implements Company, Persistable {
    //FIXME introduce synchroniztion policy with the maximal concurreny
    //Operations of not updating should run simultaniously
    private TreeMap<Long, Employee> employees = new TreeMap<>();
    private HashMap<String, List<Employee>> employeesDepartment = new HashMap<>();
    private TreeMap<Float, List<Manager>> managersFactor = new TreeMap<>();
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    private class CompanyIterator implements Iterator<Employee> {
        Iterator<Employee> iterator = employees.values().iterator();
        Employee lastIterated;

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Employee next() {
            lastIterated = iterator.next();
            return lastIterated;
        }

        @Override
        public void remove() {
            performWriteOperation(writeLock, () -> {
                iterator.remove();
                removeFromIndexMaps(lastIterated);
            });
        }
    }

    @Override
    public Iterator<Employee> iterator() {
        return new CompanyIterator();
    }

    @Override
    public void addEmployee(Employee empl) {
        performWriteOperation(writeLock, () -> {
            long id = empl.getId();
            if (employees.putIfAbsent(id, empl) != null) {
                throw new IllegalStateException("Already exists employee " + id);
            }
            addIndexMaps(empl);
        });
    }

    private void addIndexMaps(Employee empl) {
        employeesDepartment.computeIfAbsent(empl.getDepartment(), k -> new ArrayList<>()).add(empl);
        if (empl instanceof Manager manager) {
            managersFactor.computeIfAbsent(manager.getFactor(), k -> new ArrayList<>()).add(manager);
        }
    }

    @Override
    public Employee getEmployee(long id) {
        return performReadOperation(readLock, () -> employees.get(id));
    }

    @Override
    public Employee removeEmployee(long id) {
        AtomicReference<Employee> result = new AtomicReference<>();
        performWriteOperation(writeLock, () -> {
            Employee empl = employees.remove(id);
            if (empl == null) {
                throw new NoSuchElementException("Not found employee " + id);
            }
            removeFromIndexMaps(empl);
            result.set(empl);
        });
        return result.get();
    }


    private void removeFromIndexMaps(Employee empl) {
        removeIndexMap(empl.getDepartment(), employeesDepartment, empl);
        if (empl instanceof Manager manager) {
            removeIndexMap(manager.getFactor(), managersFactor, manager);
        }
    }

    private <K, V extends Employee> void removeIndexMap(K key, Map<K, List<V>> map, V empl) {
        List<V> list = map.get(key);
        list.remove(empl);
        if (list.isEmpty()) {
            map.remove(key);
        }
    }

    @Override
    public int getDepartmentBudget(String department) {
        return performReadOperation(readLock, () -> employeesDepartment.getOrDefault(department, Collections.emptyList())
                .stream().mapToInt(Employee::computeSalary).sum());
    }

    @Override
    public String[] getDepartments() {
        return performReadOperation(readLock, () -> employeesDepartment.keySet()
                .stream().sorted().toArray(String[]::new));
    }

    @Override
    public Manager[] getManagersWithMostFactor() {
        AtomicReference<Manager[]> result = new AtomicReference<>(new Manager[0]);
        performReadOperation(readLock, () -> {
            if (!managersFactor.isEmpty()) {
                result.set(managersFactor.lastEntry().getValue().toArray(new Manager[0]));
            }
        });
        return result.get();
    }

    @Override
    public void saveToFile(String fileName) {
        performReadOperation(readLock, () -> {
            try (PrintWriter writer = new PrintWriter(fileName)) {
                forEach(writer::println);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void restoreFromFile(String fileName) {
        performWriteOperation(writeLock, () -> {
            try (BufferedReader reader = Files.newBufferedReader(Path.of(fileName))) {
                reader.lines().map(Employee::getEmployeeFromJSON).forEach(this::addEmployee);
            } catch (NoSuchFileException e) {
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static <T> T performReadOperation(Lock readLock, Supplier<T> readTask) {
        readLock.lock();
        try {
            return readTask.get();
        } finally {
            readLock.unlock();
        }
    }

    private static void performReadOperation(Lock readLock, Runnable readTask) {
        readLock.lock();
        try {
            readTask.run();
        } finally {
            readLock.unlock();
        }
    }

    private static void performWriteOperation(Lock writeLock, Runnable writeTask) {
        writeLock.lock();
        try {
            writeTask.run();
        } finally {
            writeLock.unlock();
        }
    }
}