package com.vaadin.componentfactory.selectiongrid;

/*
 * #%L
 * Selection Grid
 * %%
 * Copyright (C) 2020 Vaadin Ltd
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider.HierarchyFormat;
import com.vaadin.flow.data.selection.SelectionModel;

import tools.jackson.databind.node.ObjectNode;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @param <T> – the grid bean type
 */
@SuppressWarnings("serial")
@Tag("vaadin-selection-grid")
@CssImport(value = "./styles/grid.css", themeFor = "vaadin-selection-grid")
@JsModule("./src/vcf-selection-grid.js")
@JsModule("./src/selection-grid.js")
public class SelectionTreeGrid<T> extends TreeGrid<T> {

    private boolean multiSelectionColumnVisible = false;
    private boolean persistentCheckboxSelection = true;

    private T rangeStartItem;

    /**
     * @see TreeGrid#TreeGrid()
     */
    public SelectionTreeGrid() {
        super();
    }

    /**
     * @param beanType beanType – the bean type to use, not null
     * @see TreeGrid#TreeGrid(Class)
     */
    public SelectionTreeGrid(Class<T> beanType) {
        super(beanType);
    }

    /**
     * Creates a new instance using the given hierarchical data provider.
     * <p>
     * Please note, that when you want to use {@link #focusOnCell} or
     * {@link #scrollToItem}, the data provider
     * needs to implement
     * </p>
     *
     * @param dataProvider dataProvider – the data provider, not null
     * @see TreeGrid#TreeGrid(HierarchicalDataProvider)
     */
    public SelectionTreeGrid(HierarchicalDataProvider<T, ?> dataProvider) {
        super(dataProvider);
    }

    /**
     * Runs the super.onAttach and hides the multi selection column afterwards (if
     * necessary).
     *
     * @param attachEvent event
     */
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        initSelectionModel();
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
     * @param item   item to scroll and focus
     * @param column column to focus
     */
    public void focusOnCell(T item, Column<T> column) {
        scrollToItem(item);
        // int index = getIndexForItem(item);
        // if (index >= 0) {
        // // String internalId = (column != null)?getColumnInternalId(column):"";
        // int colIndex = (column != null) ? getColumns().indexOf(column) : 0;
        // this.getElement().executeJs("this.focusOnCellWhenReady($0, $1, true);",
        // index, colIndex);
        // }
    }

    /**
     * The method for scrolling to an item. Takes into account lazy loading nature
     * of grid and does the scroll operation only until the grid has finished
     * loading data
     *
     * @param item the item where to scroll to
     */
    public void scrollToItem(T item) {
        super.scrollToItem(item);
    }

    private void selectRange(T startItem, T endItem, boolean deselectOthers) {
        var items = fetchHierarchyRecursively(null);
        var startIndex = items.indexOf(startItem);
        var endIndex = items.indexOf(endItem);
        var range = items.subList(Math.min(startIndex, endIndex), Math.max(startIndex, endIndex) + 1);

        if (deselectOthers) {
            var oldSelectedItems = new HashSet<>(getSelectedItems());
            var newSelectedItems = new HashSet<>(range);
            oldSelectedItems.removeAll(newSelectedItems);
            asMultiSelect().updateSelection(newSelectedItems, oldSelectedItems);
            return;
        }

        asMultiSelect().select(range);
    }

    @Override
    protected void setSelectionModel(GridSelectionModel<T> model, SelectionMode selectionMode) {
        super.setSelectionModel(model, selectionMode);
        initSelectionModel();
    }

    private void initSelectionModel() {
        if (getSelectionModel() instanceof GridMultiSelectionModel<T>) {
            setMultiSelectionColumnVisible(multiSelectionColumnVisible);

            getElement().executeJs(
                    """
                            const grid = this;
                            const selectionColumn = this.querySelector('vaadin-grid-flow-selection-column');
                            selectionColumn.autoSelect = true;
                            selectionColumn._selectItem = function (item) {
                                grid.$server.selectionTreeGridToggleItem(
                                    grid.getItemId(item),
                                    true,
                                    this._activeModifierKeys
                                );
                            }
                            selectionColumn._deselectItem = function(item) {
                                grid.$server.selectionTreeGridToggleItem(
                                    grid.getItemId(item),
                                    false,
                                    this._activeModifierKeys
                                );
                            }
                            """);
        }
    }

    @ClientCallable
    private void selectionTreeGridToggleItem(String itemKey, boolean selected, ObjectNode modifierKeys) {
        T item = getDataCommunicator().getKeyMapper().get(itemKey);
        if (item == null) {
            throw new IllegalArgumentException("Item with key %s not found".formatted(itemKey));
        }

        boolean ctrlKey = modifierKeys.get("ctrlKey").asBoolean(false);
        boolean metaKey = modifierKeys.get("metaKey").asBoolean(false);
        boolean shiftKey = modifierKeys.get("shiftKey").asBoolean(false);

        if (shiftKey) {
            selectRange(rangeStartItem, item, true);
            return;
        }

        if (ctrlKey || metaKey) {
            if (selected) {
                select(item);
            } else {
                deselect(item);
            }
            return;
        }

        selectRange(item, item, true);
        rangeStartItem = item;
    }

    /**
     * Runs a JavaScript snippet to hide the multi selection / checkbox column on
     * the client side. The column
     * is not removed, but set to "hidden" explicitly.
     */
    protected void hideMultiSelectionColumn() {
        this.setMultiSelectionColumnVisible(false);
    }

    /**
     * Adds theme variants to the component.
     *
     * @param variants theme variants to add
     */
    public void addThemeVariants(SelectionGridVariant... variants) {
        getThemeNames().addAll(Stream.of(variants)
                .map(SelectionGridVariant::getVariantName).collect(Collectors.toList()));
    }

    /**
     * Removes theme variants from the component.
     *
     * @param variants theme variants to remove
     */
    public void removeThemeVariants(SelectionGridVariant... variants) {
        getThemeNames().removeAll(Stream.of(variants)
                .map(SelectionGridVariant::getVariantName).collect(Collectors.toList()));
    }

    /**
     * Returns true if the multi selection column is visible, false otherwise.
     *
     * @return
     */
    public boolean isMultiSelectionColumnVisible() {
        return multiSelectionColumnVisible;
    }

    /**
     * Sets the visibility of the multi selection column.
     *
     * @param multiSelectionColumnVisible - true to show the multi selection column,
     *                                    false to hide it
     */
    public void setMultiSelectionColumnVisible(boolean multiSelectionColumnVisible) {
        if (this.getSelectionModel() instanceof SelectionModel.Multi) {
            getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, context -> {
                getElement().executeJs(
                        "if (this.querySelector('vaadin-grid-flow-selection-column')) {" +
                                " this.querySelector('vaadin-grid-flow-selection-column').hidden = $0 }",
                        !multiSelectionColumnVisible);
                this.recalculateColumnWidths();
            }));
        }
        this.multiSelectionColumnVisible = multiSelectionColumnVisible;
    }

    /**
     * Returns true if the checkbox selection is persistent, false otherwise.
     *
     * @return
     */
    public boolean isPersistentCheckboxSelection() {
        return persistentCheckboxSelection;
    }

    /**
     * Sets the checkbox selection to be persistent or not.
     *
     * @param persistentCheckboxSelection - true to make the checkbox selection
     *                                    persistent, false otherwise
     */
    public void setPersistentCheckboxSelection(boolean persistentCheckboxSelection) {
        this.getElement().executeJs("this.classicCheckboxSelection = $0", !persistentCheckboxSelection);
        this.persistentCheckboxSelection = persistentCheckboxSelection;
    }

    @SuppressWarnings("unchecked")
    private List<T> fetchHierarchyRecursively(T parent) {
        var dataCommunicator = getDataCommunicator();
        var dataProvider = (HierarchicalDataProvider<T, Object>) dataCommunicator.getDataProvider();
        var query = dataCommunicator.buildQuery(parent, 0, Integer.MAX_VALUE);

        var result = new ArrayList<T>();
        dataProvider.fetchChildren(query).forEach((child) -> {
            result.add(child);

            if (dataProvider.getHierarchyFormat().equals(HierarchyFormat.NESTED)
                    && dataCommunicator.isExpanded(child)) {
                result.addAll(fetchHierarchyRecursively(child));
            }
        });
        return result;
    }
}
