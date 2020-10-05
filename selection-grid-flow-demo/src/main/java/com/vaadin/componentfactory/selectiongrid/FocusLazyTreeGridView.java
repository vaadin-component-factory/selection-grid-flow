package com.vaadin.componentfactory.selectiongrid;

import com.vaadin.componentfactory.selectiongrid.bean.DummyFile;
import com.vaadin.componentfactory.selectiongrid.service.DummyFileService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import static com.vaadin.componentfactory.selectiongrid.service.TreeGridUtil.getDummyFileTreeGrid;

/**
 * Basic example with setItems
 */
@Route(value = "focus-lazy-tree-grid", layout = MainLayout.class)
public class FocusLazyTreeGridView extends VerticalLayout {

    public FocusLazyTreeGridView() {

        SelectionTreeGrid<DummyFile> grid = buildGrid();

        ComboBox<DummyFile> dummyFileComboBox = new ComboBox<>();
        dummyFileComboBox.addValueChangeListener(item -> {
            if (item.getValue() != null) {
                grid.scrollToItem(item.getValue());
            }
        });
        //
        dummyFileComboBox.setItems(DummyFileService.fetchChildren(null,0));

        add(dummyFileComboBox);
        addAndExpand(grid);
        setPadding(false);
        setSizeFull();
    }

    private SelectionTreeGrid<DummyFile> buildGrid() {
        SelectionTreeGrid<DummyFile> grid = new SelectionTreeGrid<>();
        grid.addHierarchyColumn(DummyFile::getFilename).setHeader("File Name");

        return getDummyFileTreeGrid(grid);
    }
}
