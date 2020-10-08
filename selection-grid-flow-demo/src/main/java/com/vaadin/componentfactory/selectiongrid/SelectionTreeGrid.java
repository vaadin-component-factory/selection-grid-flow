package com.vaadin.componentfactory.selectiongrid;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataCommunicator;
import com.vaadin.flow.data.provider.hierarchy.HierarchyMapper;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@JsModule("./src/selection-grid-connector.js")
@JsModule("./src/selection-tree-grid-connector.js")
public class SelectionTreeGrid<T> extends TreeGrid<T> {

    // todo jcg remove it if not needed
    @Override
    protected void initConnector() {
        super.initConnector();
        (getUI().orElseThrow(() -> new IllegalStateException("Connector can only be initialized for an attached Grid")))
            .getPage().executeJs("window.Vaadin.Flow.selectionTreeGridConnector.initLazy($0)", new Serializable[]{this.getElement()});
    }


    /**
     * Focus on the first cell on the row
     *
     * @param item item to scroll and focus
     */
    public void focusOnCell(T item) {
        focusOnCell(item, null);
    }

    /**
     * Focus on the specific column on the row
     *
     * @param item item to scroll and focus
     * @param column column to focus
     */
    public void focusOnCell(T item, Column<T> column) {
        expandAncestor(item);
        int index = getIndexForItem(item);
        if (index >= 0) {
            //String internalId = (column != null)?getColumnInternalId(column):"";
            int colIndex = (column != null)?getColumns().indexOf(column):0;
            this.getElement().executeJs("this.focusOnCellWhenReady($0, $1, true);", index, colIndex);
        }
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

    private List<T> expandAncestor(T item) {
        List<T> ancestors = new ArrayList<>();

        T parent = getTreeData().getParent(item);
        while (parent != null) {
            ancestors.add(parent);
            item = parent;
            parent = getTreeData().getParent(item);
        }
        if (!ancestors.isEmpty()) {
            expand(ancestors);
            /*for (int i = ancestors.size() - 1; i >= 0; i--) {
                getElement().executeJs("$0.refreshExpanded($1)", this.getElement(), getIndexForItem(ancestors.get(i)));
            }*/

        }
        return ancestors;
    }

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
        return 0;
    }

    @Override
    public void setDataProvider(DataProvider<T, ?> dataProvider) {
        if (!(dataProvider instanceof TreeDataProvider)) {
            throw new IllegalArgumentException("SelectionTreeGrid only accepts TreeDataProvider.");
        }
        super.setDataProvider(dataProvider);
    }

    private String getColumnInternalId(Column<T> column) {
        Method getInternalId ;
        try {
            getInternalId = Column.class.getDeclaredMethod("getInternalId");
            getInternalId.setAccessible(true);
            return (String) getInternalId.invoke(column);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        throw new IllegalArgumentException("getInternalId");
    }
}
