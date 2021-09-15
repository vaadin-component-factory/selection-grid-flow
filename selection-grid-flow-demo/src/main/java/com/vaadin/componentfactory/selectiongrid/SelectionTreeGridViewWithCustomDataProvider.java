package com.vaadin.componentfactory.selectiongrid;

import com.vaadin.componentfactory.selectiongrid.bean.Department;
import com.vaadin.componentfactory.selectiongrid.bean.DepartmentData;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.router.Route;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Basic example with setItems
 */
@Route(value = "selection-tree-grid-with-custom-data-provider", layout = MainLayout.class)
public class SelectionTreeGridViewWithCustomDataProvider extends VerticalLayout {

    private final DepartmentData departmentData = new DepartmentData();

    public SelectionTreeGridViewWithCustomDataProvider() {

        SelectionTreeGrid<Department> grid = buildGrid();

        ComboBox<Department> personComboBox = new ComboBox<>("Focus");
        personComboBox.addValueChangeListener(item -> {
            if (item.getValue() != null) {
                grid.focusOnCell(item.getValue(),grid.getColumnByKey("name"));
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
        grid.setDataProvider(new DepartmentsDataProvider(departmentData));

        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid.asMultiSelect().addValueChangeListener(event -> {
            String message = String.format("Selection changed from %s to %s",
                    event.getOldValue(), event.getValue());
            Notification.show(message);
        });

        grid.addHierarchyColumn(Department::getName).setHeader("Department Name").setKey("name");
        grid.setWidthFull();
        return grid;
    }

    public static class DepartmentsDataProvider extends AbstractBackEndHierarchicalDataProvider<Department, Void> implements ParentItemProvider<Department> {
        private final DepartmentData departmentData;

        public DepartmentsDataProvider(DepartmentData departmentData) {
            this.departmentData = departmentData;
        }

        @Override
        public Optional<Department> getParent(Department item) {
            return Optional.ofNullable(item.getParent());
        }

        @Override
        protected Stream<Department> fetchChildrenFromBackEnd(HierarchicalQuery<Department, Void> query) {
            return departmentData.streamDepartments(query.getParent(), query.getOffset(), query.getLimit());
        }

        @Override
        public int getChildCount(HierarchicalQuery<Department, Void> query) {
            return (int) fetchChildrenFromBackEnd(query).count();
        }

        @Override
        public boolean hasChildren(Department item) {
            return departmentData.hasChildren(item);
        }
    }

}
