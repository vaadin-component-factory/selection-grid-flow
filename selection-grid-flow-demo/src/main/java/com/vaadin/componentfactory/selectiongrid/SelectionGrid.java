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
import com.vaadin.flow.data.provider.DataCommunicator;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.lang.reflect.Method;

@JsModule("./src/selection-grid-connector.js")
public class SelectionGrid<T> extends Grid<T> {

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
        String index = getIndexForItem(item);
        if (index != null) {
            int rowIndex = Integer.parseInt(index);
            rowIndex--;
            String internalId = (column != null)?getColumnInternalId(column):"";
            // delay the call of focus on cell if it's used on the same round trip (grid creation + focusCell)
            this.getElement().executeJs("setTimeout(function() { $0.focusOnCell($1, $2) });", getElement(), rowIndex, internalId);
        }
    }


    private String getIndexForItem(T item) {

        DataCommunicator<T> dataCommunicator = super.getDataCommunicator();
        return dataCommunicator.getKeyMapper().key(item);
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

    @Override
    public void setDataProvider(DataProvider<T, ?> dataProvider) {
        if (!(dataProvider instanceof ListDataProvider)) {
            throw new IllegalArgumentException(
                "SelectionGrid only accepts ListDataProvider.");
        }
        super.setDataProvider(dataProvider);
    }

}
