package com.vaadin.componentfactory.selectiongrid;

import com.vaadin.componentfactory.selectiongrid.bean.Department;
import com.vaadin.componentfactory.selectiongrid.bean.DepartmentData;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * Basic example with setItems
 */
@Route(value = "focus-tree-grid", layout = MainLayout.class)
public class FocusTreeGridView extends VerticalLayout {

    private DepartmentData departmentData = new DepartmentData();


    public FocusTreeGridView() {

        SelectionTreeGrid<Department> grid = buildGrid();

        ComboBox<Department> personComboBox = new ComboBox<>("Focus");
        personComboBox.addValueChangeListener(item -> {
            if (item.getValue() != null) {
                grid.focusOnCell(item.getValue(),grid.getColumnByKey("name"));
            }
        });
        personComboBox.setItems(departmentData.getDepartments());

        add(personComboBox);

        ComboBox<Department> scrollCombobox = new ComboBox<>("Scroll");
        scrollCombobox.addValueChangeListener(item -> {
            if (item.getValue() != null) {
                grid.scrollToItem(item.getValue());
            }
        });
        scrollCombobox.setItems(departmentData.getDepartments());
        add(scrollCombobox);

        addAndExpand(grid);
        setPadding(false);
        setSizeFull();
        // grid.focusOnCell(departmentData.getDepartments().get(departmentData.getDepartments().size() - 1));
    }

    private SelectionTreeGrid<Department> buildGrid() {
        SelectionTreeGrid<Department> grid = new SelectionTreeGrid<>();
        grid.setItems(departmentData.getRootDepartments(),
            departmentData::getChildDepartments);
        grid.addHierarchyColumn(Department::getName).setHeader("Department Name").setKey("name");
        grid.setWidthFull();
        //grid.expand(departmentData.getRootDepartments());
        return grid;
    }
}
