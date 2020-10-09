package com.vaadin.componentfactory.selectiongrid;

import com.vaadin.componentfactory.selectiongrid.bean.Department;
import com.vaadin.componentfactory.selectiongrid.bean.DepartmentData;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic example with setItems
 */
@Route(value = "focus-htree-grid", layout = MainLayout.class)
public class FocusTreeHorizontalScrollGridView extends VerticalLayout {

    private DepartmentData departmentData = new DepartmentData();

    public FocusTreeHorizontalScrollGridView() {

        ComboBox<Department> departmentComboBox = new ComboBox<>();
        SelectionTreeGrid<Department> grid = buildGrid();

        ComboBox<String> columnsCombobox = new ComboBox<>();
        departmentComboBox.addValueChangeListener(item -> {
            if (item.getValue() != null) {
                Grid.Column<Department> column = (columnsCombobox.getValue() != null)?grid.getColumnByKey(columnsCombobox.getValue()): null;
                grid.focusOnCell(item.getValue(), column);
            }
        });
        departmentComboBox.setItems(departmentData.getDepartments());

        columnsCombobox.addValueChangeListener(item -> {
            if (departmentComboBox.getValue() != null) {
                Grid.Column<Department> column = (columnsCombobox.getValue() != null)?grid.getColumnByKey(columnsCombobox.getValue()): null;
                grid.focusOnCell(departmentComboBox.getValue(), column);
            }
        });
        columnsCombobox.setItems("name","id","parent","manager","manager2","manager3","manager4","manager5","manager6","manager7");
        columnsCombobox.setValue("name");
        add(new HorizontalLayout(departmentComboBox, columnsCombobox));
        addAndExpand(grid);
        setPadding(false);
        setSizeFull();

    }

    private SelectionTreeGrid<Department> buildGrid() {
        SelectionTreeGrid<Department> grid = new SelectionTreeGrid<>();
        grid.setItems(departmentData.getRootDepartments(),
            departmentData::getChildDepartments);
        List<Grid.Column<Department>> columns = new ArrayList<>();
        columns.add(grid.addHierarchyColumn(Department::getName).setHeader("Department Name").setWidth("300px").setKey("name"));
        columns.add(grid.addColumn(Department::getParent).setHeader("Department Parent").setKey("parent"));
        columns.add(grid.addColumn(Department::getId).setHeader("Department Id").setKey("id"));
        columns.add(grid.addColumn(Department::getManager).setHeader("Department Manager").setKey("manager"));
        columns.add(grid.addColumn(Department::getManager).setHeader("Department Manager col 2").setKey("manager2"));
        columns.add(grid.addColumn(Department::getManager).setHeader("Department Manager col 3").setKey("manager3"));
        columns.add(grid.addColumn(Department::getManager).setHeader("Department Manager col 4").setWidth("300px").setKey("manager4"));
        columns.add(grid.addColumn(Department::getManager).setHeader("Department Manager col 5").setWidth("300px").setKey("manager5"));
        Grid.Column<Department> manager6 = grid.addColumn(Department::getManager).setHeader("Department Manager col 6").setWidth("300px").setKey(
            "manager6");
        columns.add(grid.addColumn(Department::getManager).setHeader("Department Manager col 7").setWidth("300px").setKey("manager7"));
        columns.add(manager6);
        grid.setWidthFull();
        grid.setColumnReorderingAllowed(true);
        grid.setColumnOrder(columns);
        return grid;
    }
}
