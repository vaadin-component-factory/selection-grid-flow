/*-
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

import { ThemableMixin } from '@vaadin/vaadin-themable-mixin';
import { ElementMixin } from '@vaadin/component-base/src/element-mixin';
import { Grid as GridElement } from  '@vaadin/grid/src/vaadin-grid.js';

import {
    _selectionGridSelectRow,
    _selectionGridSelectRowWithItem,
    _debounce

} from './helpers';

class VcfSelectionGridElement extends ElementMixin(ThemableMixin(GridElement)) {

    constructor() {
        super();
        this._selectionGridSelectRow = _selectionGridSelectRow.bind(this);
        this._selectionGridSelectRowWithItem = _selectionGridSelectRowWithItem.bind(this);
        this._debounce = _debounce.bind(this);
    }

    static get properties() {
        return {
            rangeSelectRowFrom: {
                type: Number,
                value: -1
            }
        };
    }

    /** @override */
    _scrollToFlatIndex(rowIndex) {
      super._scrollToFlatIndex(rowIndex);

      const cellIndex = this.__focusOnCellAfterScroll;
      if (typeof cellIndex === 'number') {
        this.focusOnCell(rowIndex, cellIndex);
      }
      this.__focusOnCellAfterScroll = null;
    }

    focusOnCell(rowIndex, cellIndex) {
      const row = [...this.$.items.children].find((row) => row.index === rowIndex);
      if (row) {
        const cell = row.children[cellIndex];
        if (cell) {
          cell.focus();
        } else {
          throw "index out of bound";
        }
      }
    }

    focusOnCellAfterScroll(cellIndex) {
      this.__focusOnCellAfterScroll = cellIndex;
    };

    static get is() {
        /** prefix with vaadin because grid column requires this **/
        return 'vaadin-selection-grid';
    }

    static get version() {
        return '4.0.0';
    }

    static get lumoInjector() {
      return { ...super.lumoInjector, is: 'vaadin-grid' }
    }
}

customElements.define(VcfSelectionGridElement.is, VcfSelectionGridElement);

/**
 * @namespace Vaadin
 */
window.Vaadin.VcfSelectionGridElement = VcfSelectionGridElement;
