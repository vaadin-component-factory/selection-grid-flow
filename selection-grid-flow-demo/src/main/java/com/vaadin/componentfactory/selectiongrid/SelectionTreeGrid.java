package com.vaadin.componentfactory.selectiongrid;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataCommunicator;
import com.vaadin.flow.data.provider.hierarchy.HierarchyMapper;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@JsModule("./src/selection-tree-grid-connector.js")
public class SelectionTreeGrid<T> extends TreeGrid<T> {

    @Override
    protected void initConnector() {
        super.initConnector();
        (getUI().orElseThrow(() -> new IllegalStateException("Connector can only be initialized for an attached Grid")))
            .getPage().executeJs("window.Vaadin.Flow.selectionTreeGridConnector.initLazy($0)", new Serializable[]{this.getElement()});
    }


    /**
     * The method for scrolling to an item. Takes into account lazy loading nature
     * of grid and does the scroll operation only until the grid has finished
     * loading data
     *
     * @param item the item where to scroll to
     */
    public void scrollToItem(T item) {
        expandAncestor(item);
        int index = getIndexForItem(item);
        if (index >= 0) {
            this.getElement().executeJs("this.scrollWhenReady($0, true);", index);
        }
    }

    private void expandAncestor(T item) {
        List<T> ancestors = new ArrayList<>();

        T parent = getTreeData().getParent(item);
        while (parent != null) {
            ancestors.add(parent);
            item = parent;
            parent = getTreeData().getParent(item);
        }
        if (!ancestors.isEmpty()) {
            expand(ancestors);
        }

    }

    /**
     * This is a method for getting the row index of an item in a treegrid.
     *
     * Does not work for Lazy data provider
     *
     * @param item the item where to scroll to
     * @return
     */
    private int getIndexForItem(T item) {
        HierarchicalDataCommunicator<T> dataCommunicator = super.getDataCommunicator();
        Method getHierarchyMapper ;
        try {
            getHierarchyMapper = HierarchicalDataCommunicator.class.getDeclaredMethod("getHierarchyMapper");
            getHierarchyMapper.setAccessible(true);
            HierarchyMapper<T, ?> mapper = (HierarchyMapper<T, ?>) getHierarchyMapper.invoke(dataCommunicator);
            int rowIndex = mapper.getIndex(item);
            if (rowIndex > 0) {
                return rowIndex;
            }
        } catch (Exception ignored) {
        }
        throw new IllegalStateException("Item is not found in the HierarchicalDataCommunicator");
    }

}
