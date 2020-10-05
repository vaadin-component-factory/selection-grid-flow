package com.vaadin.componentfactory.selectiongrid;

/*
 * #%L
 * selection-grid-flow
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
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;

@JsModule("./src/selection-grid-connector.js")
public class SelectionGrid<T> extends Grid<T> {

    @Override
    public void scrollToIndex(int rowIndex) {
        super.scrollToIndex(rowIndex);
    }

    /**
     * Find the correct index and scroll to the row
     * If it's a tree grid
     *
     * @param item
     * @param colKey
     */
    public void scrollAndFocus(T item, String colKey) {
        // todo
        /*DataProvider<T, ?> dataProvider = getDataProvider();
        if (dataProvider instanceof ListDataProvider) {
            ListDataProvider<T> listDataProvider = (ListDataProvider<T>) getDataProvider();
            listDataProvider.getItems().stream().filter(T::equals).findFirst();
        }*/
    }
/*
    public int getRowIndex(Grid<T> grid, T row) {
        List<T> items = grid.getDataCommunicator().fetchItemsWithRange(0,grid.getDataCommunicator().getDataProviderSize());
        return items.indexOf(row);
    }*/
}
