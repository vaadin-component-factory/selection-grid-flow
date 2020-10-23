package com.vaadin.componentfactory.selectiongrid;

import com.vaadin.componentfactory.selectiongrid.bean.Department;
import com.vaadin.componentfactory.selectiongrid.bean.DepartmentData;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * Basic example with setItems
 */
@Route(value = "selection-tree-grid", layout = MainLayout.class)
public class SelectionTreeGridView extends VerticalLayout {

    private DepartmentData departmentData = new DepartmentData();
    private Div messageDiv = new Div();


    public SelectionTreeGridView() {
        SelectionTreeGrid<Department> grid = buildGrid();
        addAndExpand(grid);
        add(messageDiv);
        setPadding(false);
        setSizeFull();
    }

    private SelectionTreeGrid<Department> buildGrid() {
        SelectionTreeGrid<Department> grid = new SelectionTreeGrid<>();
        grid.setItems(departmentData.getRootDepartments(),
            departmentData::getChildDepartments);
        grid.addHierarchyColumn(Department::getName).setHeader("Department Name").setKey("name");
        grid.setWidthFull();

        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid.asMultiSelect().addValueChangeListener(event -> {
            String message = String.format("Selection changed from %s to %s",
                event.getOldValue(), event.getValue());
            messageDiv.setText(message);
        });
        return grid;
    }
}
