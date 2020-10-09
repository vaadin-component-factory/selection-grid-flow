package com.vaadin.componentfactory.selectiongrid.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DepartmentData {
    private static final List<Department> DEPARTMENT_LIST = createDepartmentList();

    private static List<Department> createDepartmentList() {
        List<Department> departmentList = new ArrayList<>();

        departmentList
                .add(new Department(1, "Product Development", null, "Päivi"));
        departmentList.add(
                new Department(11, "Flow", departmentList.get(0), "Pekka"));
        departmentList.add(new Department(111, "Flow Core",
                departmentList.get(1), "Pekka"));
        departmentList.add(new Department(112, "Flow Components",
                departmentList.get(1), "Gilberto"));
        departmentList.add(
                new Department(12, "Design", departmentList.get(0), "Pekka"));
        departmentList.add(
                new Department(13, "DJO", departmentList.get(0), "Thomas"));
        departmentList.add(
                new Department(14, "Component", departmentList.get(0), "Tomi"));
        departmentList.add(new Department(2, "HR", null, "Anne"));
        departmentList.add(
                new Department(21, "Office", departmentList.get(7), "Anu"));
        departmentList.add(
                new Department(22, "Employee", departmentList.get(7), "Minna"));
        departmentList.add(new Department(3, "Marketing", null, "Niko"));
        departmentList.add(
                new Department(31, "Growth", departmentList.get(10), "Ömer"));
        departmentList.add(new Department(32, "Demand Generation",
                departmentList.get(10), "Marcus"));
        departmentList.add(new Department(33, "Product Marketing",
                departmentList.get(10), "Pekka"));
        departmentList.add(new Department(34, "Brand Experience",
                departmentList.get(10), "Eero"));
        departmentList.add(
                new Department(41, "Sub-Office ", departmentList.get(9), "Anu1"));
        departmentList.add(
                new Department(52, "Sub-Office 2", departmentList.get(15), "Anu2"));
        for (int i = 0; i < 59; i++) {
            departmentList
                    .add(new Department(200+i, "T "+i, null, "Test"));
        }
        Department department = new Department(300, "T 300", null, "Test");
        departmentList.add(department);
        addChildren(departmentList, department);

        departmentList
            .add(new Department(500, "Last", null, "Last"));
        return departmentList;

    }

    private static void addChildren(List<Department> departmentList, Department parent) {
        for (int i = 0; i < 60; i++) {
            departmentList
                .add(new Department(301+i, "T 300-"+i, parent, "Test"));
        }
    }

    public List<Department> getDepartments() {
        return DEPARTMENT_LIST;
    }

    public List<Department> getRootDepartments() {
        return DEPARTMENT_LIST.stream()
                .filter(department -> department.getParent() == null)
                .collect(Collectors.toList());
    }

    public List<Department> getChildDepartments(Department parent) {
        return getChildDepartments(parent, 0);
    }
    public List<Department> getChildDepartments(Department parent, int offset) {
        List<Department> departmentList = DEPARTMENT_LIST.stream().filter(
                department -> Objects.equals(department.getParent(), parent))
                .collect(Collectors.toList());
        return departmentList.subList(offset, departmentList.size());
    }

    public boolean hasChildren(Department parent) {
        return DEPARTMENT_LIST.stream().anyMatch(
                department -> Objects.equals(department.getParent(), parent));
    }
}