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
/* eslint-disable no-invalid-this */

export function _selectionGridSelectRow(e) {
    const vaadinTreeToggle = e.composedPath().find((p) => p.nodeName === "VAADIN-GRID-TREE-TOGGLE");
    if (vaadinTreeToggle) {
        // don't select, it will expand/collapse the node
        // reset the last item
        this.rangeSelectRowFrom = -1;
    } else {
        const tr = e.composedPath().find((p) => p.nodeName === "TR");
        if (tr && typeof tr.index != 'undefined') {
            const item = tr._item;
            const index = tr.index;

            this._selectionGridSelectRowWithItem(e, item, index);
        }
    }
}
export function _debounce(func, wait, immediate) {
    var context = this,
        args = arguments;
    var later = function() {
        window.debounceFunction = null;
        if (!immediate) func.apply(context, args);
    };
    var callNow = immediate && !window.debounceFunction;
    clearTimeout(window.debounceFunction);
    window.debounceFunction = setTimeout(later, wait);
    if (callNow) {
		func.apply(context, args);
	}
};
export function _selectionGridSelectRowWithItem(e, item, index) {
    let ctrlKey = (e.metaKey)?e.metaKey:e.ctrlKey; //(this._ios)?e.metaKey:e.ctrlKey;
    if (!this.classicCheckboxSelection && e.srcElement && e.srcElement.parentNode && e.srcElement.parentNode.nodeName === 'VAADIN-CHECKBOX') {
        ctrlKey = true;
    }
    // if click select only this row
    if (!ctrlKey && !e.shiftKey) {
      if (this.$server?.selectionTreeGridToggleItem) {
        this.$server.selectionTreeGridToggleItem(item.key, true, { deselectOthers: true });
      } else if (this.$server) {
			this._debounce(() => {
				this.$server.selectRangeOnlyOnClick(index, index);
            }, 100);

        } else {
            this.selectedItems = [];
            this.selectItem(item);
        }
    }
    // if ctrl click
    if (e.shiftKey && this.rangeSelectRowFrom >= 0) {
        if((this.rangeSelectRowFrom - index) !== 0) { // clear text selection, if multiple rows are selected using shift
            const sel = window.getSelection ? window.getSelection() : document.selection;
            if (sel) {
                if (sel.removeAllRanges) {
                    sel.removeAllRanges();
                } else if (sel.empty) {
                    sel.empty();
                }
            }
        }

        if (!ctrlKey) {
          if (this.$server?.selectionTreeGridSelectRange) {
            this.$server.selectionTreeGridSelectRange(item.key, { deselectOthers: true });
          } else if (this.$server) {
				this._debounce(() => {
                	this.$server.selectRangeOnly(this.rangeSelectRowFrom, index);
            	}, 100);
            }
        } else {
            if (this.$server?.selectionTreeGridSelectRange) {
            this.$server.selectionTreeGridSelectRange(item.key, { deselectOthers: false });
          } else if (this.$server) {
				this._debounce(() => {
                	this.$server.selectRange(this.rangeSelectRowFrom, index);
                }, 100);
            }
        }
    } else {
        if (!ctrlKey) {
          if (this.$server?.selectionTreeGridToggleItem) {
            this.$server.selectionTreeGridToggleItem(item.key, true, { deselectOthers: true });
          } else if (this.$server) {
				this._debounce(() => {
					this.$server.selectRangeOnlyOnClick(index, index);
	            }, 100);
            }
        } else {
            if (this.selectedItems && this.selectedItems.some((i) => i.key === item.key)) {
                if (this.$server?.selectionTreeGridToggleItem) {
                  this.$server.selectionTreeGridToggleItem(item.key, false, { deselectOthers: false });
                } else if (this.$connector) {
                    this.$connector.doDeselection([item], true);
                } else {
                    this.deselectItem(item);
                }
            } else {
                if (this.$server?.selectionTreeGridToggleItem) {
                  this.$server.selectionTreeGridToggleItem(item.key, true, { deselectOthers: false });
                } else if (this.$server) {
					this._debounce(() => {
                    	this.$server.selectRange(index, index);
                    }, 100);
                }
            }
        }
        this.rangeSelectRowFrom = index;
    }
}
