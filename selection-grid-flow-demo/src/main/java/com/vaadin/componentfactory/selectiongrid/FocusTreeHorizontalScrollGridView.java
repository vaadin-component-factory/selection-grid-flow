package com.vaadin.componentfactory.selectiongrid;

import com.vaadin.componentfactory.selectiongrid.bean.Department;
import com.vaadin.componentfactory.selectiongrid.bean.DepartmentData;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

        ComboBox<Department> departmentComboBox = new ComboBox<>();
        ComboBox<String> columnsCombobox = new ComboBox<>();
        departmentComboBox.addValueChangeListener(item -> {
            if (item.getValue() != null) {
                grid.focusOnCell(item.getValue(), columnsCombobox.getValue());
            }
        });
        departmentComboBox.setItems(departmentData.getDepartments());

        columnsCombobox.addValueChangeListener(item -> {
            if (departmentComboBox.getValue() != null) {
                grid.focusOnCell(departmentComboBox.getValue(), columnsCombobox.getValue());
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
        grid.addHierarchyColumn(Department::getName).setHeader("Department Name").setWidth("300px").setKey("name");
        grid.addColumn(Department::getParent).setHeader("Department Parent").setKey("parent");
        grid.addColumn(Department::getId).setHeader("Department Id").setKey("id");
        grid.addColumn(Department::getManager).setHeader("Department Manager").setKey("manager");
        grid.addColumn(Department::getManager).setHeader("Department Manager col 2").setKey("manager2");
        grid.addColumn(Department::getManager).setHeader("Department Manager col 3").setKey("manager3");
        grid.addColumn(Department::getManager).setHeader("Department Manager col 4").setWidth("300px").setKey("manager4");
        grid.addColumn(Department::getManager).setHeader("Department Manager col 5").setWidth("300px").setKey("manager5");
        grid.addColumn(Department::getManager).setHeader("Department Manager col 6").setWidth("300px").setKey("manager6");
        grid.addColumn(Department::getManager).setHeader("Department Manager col 7").setWidth("300px").setKey("manager7");
        grid.setWidthFull();
        return grid;
    }
}
