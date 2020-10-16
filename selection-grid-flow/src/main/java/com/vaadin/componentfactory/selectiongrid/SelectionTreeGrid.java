package com.vaadin.componentfactory.selectiongrid;

/*
 * #%L
 * Selection Grid
 * %%
 * Copyright (C) 2020 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file license.html distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the CVALv3 along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.KeyMapper;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalArrayUpdater;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataCommunicator;
import com.vaadin.flow.data.provider.hierarchy.HierarchyMapper;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.internal.JsonUtils;
import elemental.json.Json;
import elemental.json.JsonObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@JsModule("./src/selection-grid-connector.js")
public class SelectionTreeGrid<T> extends TreeGrid<T> {
    /// TEMPORARY FIX FOR https://github.com/vaadin/vaadin-grid/issues/1820
    /// Remove this once this issue is closed and released
    {
        // Get fields through reflection
        final Field hierarchyMapperField;
        final Field expandedItemIdsField;
        final Field objectIdKeyMapField;
        try {
            objectIdKeyMapField = KeyMapper.class
                .getDeclaredField("objectIdKeyMap");
            hierarchyMapperField = HierarchicalDataCommunicator.class
                .getDeclaredField("mapper");
            expandedItemIdsField = HierarchyMapper.class
                .getDeclaredField("expandedItemIds");

            expandedItemIdsField.setAccessible(true);
            hierarchyMapperField.setAccessible(true);
            objectIdKeyMapField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        // Create attach listener
        addAttachListener(event -> {
            try {
                HierarchicalDataCommunicator<T> communicator = getDataCommunicator();
                HierarchyMapper mapper = (HierarchyMapper) hierarchyMapperField
                    .get(communicator);
                Set<Object> expandedItemIds = (Set<Object>) expandedItemIdsField
                    .get(mapper);
                HashMap<Object, String> objectIdKeyMap = (HashMap) objectIdKeyMapField
                    .get(communicator.getKeyMapper());

                HierarchicalArrayUpdater.HierarchicalUpdate update = (HierarchicalArrayUpdater.HierarchicalUpdate) getArrayUpdater()
                    .startUpdate(mapper.getRootSize());
                update.enqueue("$connector.expandItems",
                    expandedItemIds
                        .stream()
                        .map(objectIdKeyMap::get)
                        .map(key -> {
                            JsonObject json = Json.createObject();
                            json.put("key", key);
                            return json;
                        }).collect(
                        JsonUtils.asArray()));
                event.getUI().beforeClientResponse(this, ctx -> update.commit());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
    /// END TEMPORARY FIX
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
