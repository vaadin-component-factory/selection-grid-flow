package com.vaadin.componentfactory.selectiongrid;

import com.vaadin.componentfactory.selectiongrid.bean.Department;
import com.vaadin.componentfactory.selectiongrid.bean.DepartmentData;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * Basic example with setItems
 */
@Route(value = "selection-tree-grid", layout = MainLayout.class)
public class SelectionTreeGridView extends VerticalLayout {

    private DepartmentData departmentData = new DepartmentData();
    private Div messageDiv = new Div();
    Checkbox checkbox = new Checkbox("Case");


    public SelectionTreeGridView() {
        SelectionTreeGrid<Department> grid = buildGrid();
        Checkbox changeMultiselectionColumn = new Checkbox("Multiselection column");
		changeMultiselectionColumn.addValueChangeListener(event -> {
			grid.setMultiSelectionColumnVisible(event.getValue());
		});
		Checkbox persistentCheckboxSelection = new Checkbox("Persistent selection");
		persistentCheckboxSelection.setValue(true);
		persistentCheckboxSelection.addValueChangeListener(event -> {
			grid.setPersistentCheckboxSelection(event.getValue());
		});
		persistentCheckboxSelection.setTooltipText("When enabled, selecting a row via its checkbox will "
				+ "add or remove it from the current selection without clearing previously selected rows. "
				+ "This behavior allows users to manage selections manually using checkboxes, "
				+ "similar to how email clients like Gmail handle selection");
        addAndExpand(grid);
        add(new HorizontalLayout(checkbox, changeMultiselectionColumn, persistentCheckboxSelection), messageDiv);
        setPadding(false);
        setSizeFull();
    }

    private SelectionTreeGrid<Department> buildGrid() {
        SelectionTreeGrid<Department> grid = new SelectionTreeGrid<>();
        grid.setItems(departmentData.getRootDepartments(),
            departmentData::getChildDepartments);
        grid.addHierarchyColumn(dept -> checkbox.getValue() ? dept.getName().toLowerCase() : dept.getName().toUpperCase() ).setHeader("Department Name").setKey("name");
        grid.setWidthFull();

        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        checkbox.addValueChangeListener(e -> {
        	grid.getDataProvider().refreshAll();
        });
 
        grid.asMultiSelect().addValueChangeListener(event -> {
            String message = String.format("Selection changed from %s to %s",
                event.getOldValue(), event.getValue());
            messageDiv.setText(message);
        });
        return grid;
    }
}
