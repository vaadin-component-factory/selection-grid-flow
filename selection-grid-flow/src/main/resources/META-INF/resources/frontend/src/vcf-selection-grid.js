
import { ThemableMixin } from '@vaadin/vaadin-themable-mixin';
import { ElementMixin } from '@vaadin/vaadin-element-mixin';
import { GridElement } from  '@vaadin/vaadin-grid/src/vaadin-grid.js';

import {
    _getItemOverriden,
    _loadPageOverriden,
    _onNavigationKeyDownOverridden,
    _onSpaceKeyDownOverriden,
    _selectionGridSelectRow,
    _selectionGridSelectRowWithItem
} from './helpers';

class VcfSelectionGridElement extends ElementMixin(ThemableMixin(GridElement)) {

    constructor() {
        super();

        this._getItemOverriden = _getItemOverriden.bind(this);
        this._loadPageOverriden = _loadPageOverriden.bind(this);
        this._onNavigationKeyDownOverridden = _onNavigationKeyDownOverridden.bind(this);
        this._onSpaceKeyDownOverriden = _onSpaceKeyDownOverriden.bind(this);
        this._selectionGridSelectRow = _selectionGridSelectRow.bind(this);
        this._selectionGridSelectRowWithItem = _selectionGridSelectRowWithItem.bind(this);
    }

    static get properties() {
        return {
            rangeSelectRowFrom: {
                type: Number,
                value: -1
            }
        };
    }

    ready() {
        super.ready();
        this._getItem = this._getItemOverriden;
        this._loadPage = this._loadPageOverriden;

        const old_onNavigationKeyDown = this._onNavigationKeyDown.bind(this);
        this._onNavigationKeyDown = function _keyDown(e) {
            old_onNavigationKeyDown(e);
            this._onNavigationKeyDownOverridden(e);
        };

        const old_onSpaceKeyDown = this._onSpaceKeyDown.bind(this);
        this._onSpaceKeyDown = function _keyDown(e) {
            old_onSpaceKeyDown(e);
            this._onSpaceKeyDownOverriden(e);
        };

        const old_onClick = this._onClick.bind(this);
        this._onClick = function _click(e) {
            old_onClick(e);
            this._selectionGridSelectRow(e);
        };


    }

    connectedCallback() {
        super.connectedCallback();

    }


    focusOnCell(rowNumber, cellNumber) {
        if (rowNumber < 0 || cellNumber < 0) {
            throw "index out of bound";
        }
        this.scrollToIndex(rowNumber);
        /** workaround when the expanded node opens children the index is outside the grid size
         * https://github.com/vaadin/vaadin-grid/issues/2060
         * Remove this once this is fixed
         **/
        if (rowNumber > this._effectiveSize) {
            const that = this;
            setTimeout(() => {
                that.scrollToIndex(rowNumber);
                that._startToFocus(rowNumber, cellNumber);
            }, 200);
        } else {
            this._startToFocus(rowNumber, cellNumber);
        }
        /** End of workaround **/
    };

    _startToFocus(rowNumber, cellNumber) {
        this._rowNumberToFocus = rowNumber;
        this._cellNumberToFocus = cellNumber;
        const row = Array.from(this.$.items.children).filter(
            (child) => child.index === rowNumber
        )[0];
        // if row is already
        if (row) {
            const cell = row.children[cellNumber];
            if (cell) {
                cell.focus();
            } else {
                throw "index out of bound";
            }
        }
    };

    _focus() {
        const rowNumber = this._rowNumberToFocus;
        const cellNumber = this._cellNumberToFocus;
        this._rowNumberToFocus = -1;
        this._cellNumberToFocus = -1;
        const row = Array.from(this.$.items.children).filter(
            (child) => child.index === rowNumber
        )[0];
        const cell = row.children[cellNumber];
        if (cell) {
            cell.focus();
        } else {
            throw "index out of bound";
        }
        this._rowNumberToFocus = -1;
        this._cellNumberToFocus = -1;
    };

    focusOnCellWhenReady(rowIndex, colId, firstCall) {
        if (this.loading || firstCall) {
            var that = this;
            setTimeout(function () {
                that.focusOnCellWhenReady(rowIndex, colId, false);
            }, 1);
        } else {
            this.focusOnCell(rowIndex, colId);
        }
    };

    scrollWhenReady(index, firstCall) {
        if (this.loading || firstCall) {
            var that = this;
            setTimeout(function () {
                that.scrollWhenReady(index, false);
            }, 200);
        } else {
            var that = this;
            setTimeout(function () {
                that.scrollToIndex(index);
            }, 200);
        }
    };


    static get is() {
        /** prefix with vaadin because grid column requires this **/
        return 'vaadin-selection-grid';
    }

    static get version() {
        return '0.1.0';
    }
}

customElements.define(VcfSelectionGridElement.is, VcfSelectionGridElement);

/**
 * @namespace Vaadin
 */
window.Vaadin.VcfSelectionGridElement = VcfSelectionGridElement;
