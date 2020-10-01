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

@JsModule("./src/selection-grid-connector.js")
public class SelectionGrid<T> extends Grid<T> {

}
