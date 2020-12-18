package com.vaadin.componentfactory.selectiongrid;

/*
 * #%L
 * selection-grid-flow
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

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.data.provider.DataCommunicator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Tag("vaadin-selection-grid")
@CssImport(value = "./styles/grid.css", themeFor = "vaadin-selection-grid")
@JsModule("./src/vcf-selection-grid.js")
@JsModule("./src/selection-grid.js")
public class SelectionGrid<T> extends Grid<T> {

    /**
     * @see Grid#Grid()
     */
    public SelectionGrid() {
        super();
    }

    /**
     *
     * @see Grid#Grid(int)
     *
     * @param pageSize - the page size. Must be greater than zero.
     */
    public SelectionGrid(int pageSize) {
        super(pageSize);
    }

    /**
     *
     * @see Grid#Grid(Class, boolean)
     *
     * @param beanType - the bean type to use, not null
     * @param autoCreateColumns – when true, columns are created automatically for the properties of the beanType
     */
    public SelectionGrid(Class<T> beanType, boolean autoCreateColumns) {
        super(beanType, autoCreateColumns);
    }

    /**
     *
     * @see Grid#Grid(Class)
     *
     * @param beanType - the bean type to use, not null
     */
    public SelectionGrid(Class<T> beanType) {
        super(beanType);
    }

    @Override
    public void scrollToIndex(int rowIndex) {
        super.scrollToIndex(rowIndex);
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
        int index = getIndexForItem(item);
        if (index > 0) {
            int colIndex = (column != null)?getColumns().indexOf(column):0;
            // delay the call of focus on cell if it's used on the same round trip (grid creation + focusCell)
            this.getElement().executeJs("setTimeout(function() { $0.focusOnCell($1, $2) });", getElement(), index, colIndex);
        }
    }


    private int getIndexForItem(T item) {
        return getItemsInOrder().indexOf(item);
    }

    private List<T> getItemsInOrder() {
        DataCommunicator<T> dataCommunicator = super.getDataCommunicator();
        Method fetchFromProvider;
        Method getDataProviderSize;
        try {
            fetchFromProvider = DataCommunicator.class.getDeclaredMethod("fetchFromProvider", int.class, int.class);
            getDataProviderSize = DataCommunicator.class.getDeclaredMethod("getDataProviderSize");
            fetchFromProvider.setAccessible(true);
            getDataProviderSize.setAccessible(true);
            int size = (Integer) getDataProviderSize.invoke(dataCommunicator);
            return ((Stream<T>) fetchFromProvider.invoke(dataCommunicator, 0, size)).collect(Collectors.toList());
        } catch (Exception ignored) {
        }
        return new ArrayList<>();
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

    /**
     * Select the range and keep the other items selected
     *
     * @param fromIndex
     * @param toIndex
     */
    @ClientCallable
    private void selectRange(int fromIndex, int toIndex) {
        GridSelectionModel<T> model = getSelectionModel();
        if (model instanceof GridMultiSelectionModel) {
            DataCommunicator<T> dataCommunicator = super.getDataCommunicator();
            Method fetchFromProvider;
            try {
                fetchFromProvider = DataCommunicator.class.getDeclaredMethod("fetchFromProvider", int.class, int.class);
                fetchFromProvider.setAccessible(true);
                asMultiSelect().select(((Stream<T>) fetchFromProvider.invoke(dataCommunicator, Math.min(fromIndex, toIndex), Math.max(fromIndex,
                    toIndex) - Math.min(fromIndex, toIndex) + 1)).collect(Collectors.toList()));
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
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
            DataCommunicator<T> dataCommunicator = super.getDataCommunicator();
            Method fetchFromProvider;
            try {
                fetchFromProvider = DataCommunicator.class.getDeclaredMethod("fetchFromProvider", int.class, int.class);
                fetchFromProvider.setAccessible(true);
                Set<T> newSelectedItems = ((Stream<T>) fetchFromProvider.invoke(dataCommunicator, from, to - from + 1)).collect(Collectors.toSet());
                HashSet<T> oldSelectedItems = new HashSet<>(getSelectedItems());
                oldSelectedItems.removeAll(newSelectedItems);
                asMultiSelect().updateSelection(newSelectedItems, oldSelectedItems);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }

    @Override
    protected void setSelectionModel(GridSelectionModel<T> model, SelectionMode selectionMode) {
        getElement().executeJs("if (this.querySelector('vaadin-grid-flow-selection-column')) { this.querySelector('vaadin-grid-flow-selection-column').hidden = true }");
        super.setSelectionModel(model, selectionMode);
    }
}
