package com.vaadin.componentfactory.selectiongrid;

import com.vaadin.componentfactory.selectiongrid.bean.Department;
import com.vaadin.componentfactory.selectiongrid.bean.DepartmentData;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * Basic example with setItems
 */
@Route(value = "focus-htree-grid", layout = MainLayout.class)
public class FocusTreeHorizontalScrollGridView extends VerticalLayout {

    private DepartmentData departmentData = new DepartmentData();

    public FocusTreeHorizontalScrollGridView() {

        SelectionTreeGrid<Department> grid = buildGrid();

        ComboBox<Department> personComboBox = new ComboBox<>();
        personComboBox.addValueChangeListener(item -> {
            if (item.getValue() != null) {
                grid.focusOnCell(item.getValue());
            }
        });
        personComboBox.setItems(departmentData.getDepartments());

        add(personComboBox);
        addAndExpand(grid);
        setPadding(false);
        setSizeFull();

    }

    private SelectionTreeGrid<Department> buildGrid() {
        SelectionTreeGrid<Department> grid = new SelectionTreeGrid<>();
        grid.setItems(departmentData.getRootDepartments(),
            departmentData::getChildDepartments);
        grid.addHierarchyColumn(Department::getName).setHeader("Department Name").setWidth("300px");
        grid.addColumn(Department::getParent).setHeader("Department Parent");
        grid.addColumn(Department::getId).setHeader("Department Id");
        grid.addColumn(Department::getManager).setHeader("Department Manager");
        grid.addColumn(Department::getManager).setHeader("Department Manager col 2");
        grid.addColumn(Department::getManager).setHeader("Department Manager col 3");
        grid.addColumn(Department::getManager).setHeader("Department Manager col 4").setWidth("300px");
        grid.addColumn(Department::getManager).setHeader("Department Manager col 5").setWidth("300px");
        grid.addColumn(Department::getManager).setHeader("Department Manager col 6").setWidth("300px");
        grid.addColumn(Department::getManager).setHeader("Department Manager col 7").setWidth("300px");
        grid.setWidthFull();
        return grid;
    }
}
