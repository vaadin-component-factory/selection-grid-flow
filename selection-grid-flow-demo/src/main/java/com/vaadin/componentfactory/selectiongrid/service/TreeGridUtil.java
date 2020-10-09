package com.vaadin.componentfactory.selectiongrid.service;

import com.vaadin.componentfactory.selectiongrid.SelectionTreeGrid;
import com.vaadin.componentfactory.selectiongrid.bean.DummyFile;
import com.vaadin.componentfactory.selectiongrid.service.DummyFileService;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

import java.util.stream.Stream;

public class TreeGridUtil {

    public static SelectionTreeGrid<DummyFile> getDummyFileTreeGrid(SelectionTreeGrid<DummyFile> grid) {
        HierarchicalDataProvider<DummyFile, Void> dataProvider =
                new AbstractBackEndHierarchicalDataProvider<DummyFile, Void>() {

                    @Override
                    public int getChildCount(HierarchicalQuery<DummyFile, Void> query) {
                        return DummyFileService.getChildCount(query.getParent());
                    }

                    @Override
                    public boolean hasChildren(DummyFile item) {
                        return DummyFileService.hasChildren(item);
                    }

                    @Override
                    protected Stream<DummyFile> fetchChildrenFromBackEnd(
                            HierarchicalQuery<DummyFile, Void> query) {
                        return DummyFileService.fetchChildren(query.getParent(), query.getOffset()).stream();
                    }
                };

        grid.setDataProvider(dataProvider);
        grid.setSizeFull();
        return grid;
    }
}
