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

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.KeyMapper;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalArrayUpdater;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataCommunicator;
import com.vaadin.flow.data.provider.hierarchy.HierarchyMapper;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.internal.JsonUtils;
import com.vaadin.flow.internal.Range;
import elemental.json.Json;
import elemental.json.JsonObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Tag("vaadin-selection-grid")
@CssImport(value = "./styles/grid.css", themeFor = "vaadin-selection-grid")
@JsModule("./src/vcf-selection-grid.js")
@JsModule("./src/selection-grid.js")
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
        }
        return ancestors;
    }

    private int getIndexForItem(T item) {
        HierarchicalDataCommunicator<T> dataCommunicator = super.getDataCommunicator();
        return dataCommunicator.getIndex(item);
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

    @ClientCallable
    private void selectRange(int fromIndex, int toIndex) {
        int from = Math.min(fromIndex, toIndex);
        int to = Math.max(fromIndex, toIndex);

        HierarchicalDataCommunicator<T> dataCommunicator = super.getDataCommunicator();
        Method getHierarchyMapper ;
        try {
            getHierarchyMapper = HierarchicalDataCommunicator.class.getDeclaredMethod("getHierarchyMapper");
            getHierarchyMapper.setAccessible(true);
            HierarchyMapper<T, ?> mapper = (HierarchyMapper<T, ?>) getHierarchyMapper.invoke(dataCommunicator);
            asMultiSelect().select((mapper.fetchHierarchyItems(Range.withLength(from, to - from + 1))).collect(Collectors.toSet()));
        } catch (Exception ignored) {
        }
    }

    /**
     * Select the range and deselect the other items
     * @param fromIndex
     * @param toIndex
     */
    @ClientCallable
    private void selectRangeOnly(int fromIndex, int toIndex) {
        GridSelectionModel<T> model = getSelectionModel();
        if (model instanceof GridMultiSelectionModel) {
            int from = Math.min(fromIndex, toIndex);
            int to = Math.max(fromIndex, toIndex);
            HierarchicalDataCommunicator<T> dataCommunicator = super.getDataCommunicator();
            Method getHierarchyMapper ;
            try {
                getHierarchyMapper = HierarchicalDataCommunicator.class.getDeclaredMethod("getHierarchyMapper");
                getHierarchyMapper.setAccessible(true);
                HierarchyMapper<T, ?> mapper = (HierarchyMapper<T, ?>) getHierarchyMapper.invoke(dataCommunicator);
                Set<T> newSelectedItems = (mapper.fetchHierarchyItems(Range.withLength(from, to - from + 1))).collect(Collectors.toSet());
                HashSet<T> oldSelectedItems = new HashSet<>(getSelectedItems());
                oldSelectedItems.removeAll(newSelectedItems);
                asMultiSelect().updateSelection(newSelectedItems, oldSelectedItems);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    protected void setSelectionModel(GridSelectionModel<T> model, SelectionMode selectionMode) {
        getElement().executeJs("if (this.querySelector('vaadin-grid-flow-selection-column')) { this.querySelector('vaadin-grid-flow-selection-column').hidden = true }");
        super.setSelectionModel(model, selectionMode);
    }

    @Override
    public Column<T> addHierarchyColumn(ValueProvider<T, ?> valueProvider) {
        Column<T> column = addColumn(TemplateRenderer
            .<T> of("<vaadin-grid-tree-toggle "
                + "leaf='[[item.leaf]]' expanded='{{expanded}}' level='[[level]]'>"
                + "</vaadin-grid-tree-toggle>[[item.name]]")
            .withProperty("leaf",
                item -> !getDataCommunicator().hasChildren(item))
            .withProperty("name",
                value -> String.valueOf(valueProvider.apply(value))));
        final SerializableComparator<T> comparator =
            (a, b) -> compareMaybeComparables(valueProvider.apply(a),
                valueProvider.apply(b));
        column.setComparator(comparator);

        return column;
    }
}
