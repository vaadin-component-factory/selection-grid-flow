/* eslint-disable no-invalid-this */

export function _onNavigationKeyDownOverridden(e, key) {
    const ctrlKey = (e.metaKey)?e.metaKey:e.ctrlKey;
    if (e.shiftKey || !ctrlKey) {
        // select on shift down on shift up
        if (key === 'ArrowDown' || key === 'ArrowUp') {
            const row = Array.from(this.$.items.children).filter(
                (child) => child.index === this._focusedItemIndex
            )[0];
            if (row) {
                this._selectionGridSelectRowWithItem(e, row._item, row.index);
            }
        }
    } // else do nothing
}

export function _onSpaceKeyDownOverriden(e) {
    const tr = e.composedPath().find((p) => p.nodeName === "TR");
    if (tr) {
        const item = tr._item;
        const index = tr.index;
        if (this.selectedItems && this.selectedItems.some((i) => i.key === item.key)) {
            if (this.$connector) {
                this.$connector.doDeselection([item], true);
            } else {
                this.deselectItem(item);
            }
        } else {
            if (this.$server) {
                this.$server.selectRangeOnly(index, index);
            } else {
                this.selectedItems = [];
                this.selectItem(item);
            }
        }
    }
}

export function _selectionGridSelectRow(e) {
    const vaadinTreeToggle = e.composedPath().find((p) => p.nodeName === "VAADIN-GRID-TREE-TOGGLE");
    if (vaadinTreeToggle) {
        // don't select, it will expand/collapse the node
        // reset the last item
        this.rangeSelectRowFrom = -1;
    } else {
        const tr = e.composedPath().find((p) => p.nodeName === "TR");
        if (tr) {
            const item = tr._item;
            const index = tr.index;

            this._selectionGridSelectRowWithItem(e, item, index);
        }
    }
}
export function _selectionGridSelectRowWithItem(e, item, index) {
    const ctrlKey = (e.metaKey)?e.metaKey:e.ctrlKey; //(this._ios)?e.metaKey:e.ctrlKey;
    // if click select only this row
    if (!ctrlKey && !e.shiftKey) {
        if (this.$server) {
            this.$server.selectRangeOnly(index, index);
        } else {
            this.selectedItems = [];
            this.selectItem(item);
        }
    }
    // if ctrl click
    if (e.shiftKey && this.rangeSelectRowFrom >= 0) {
        if (!ctrlKey) {
            if (this.$server) {
                this.$server.selectRangeOnly(this.rangeSelectRowFrom, index);
            }
        } else {
            if (this.$server) {
                this.$server.selectRange(this.rangeSelectRowFrom, index);
            }
        }
    } else {
        if (!ctrlKey) {
            if (this.$server) {
                this.$server.selectRangeOnly(index, index);
            }
        } else {
            if (this.selectedItems && this.selectedItems.some((i) => i.key === item.key)) {
                if (this.$connector) {
                    this.$connector.doDeselection([item], true);
                } else {
                    this.deselectItem(item);
                }
            } else {
                if (this.$server) {
                    this.$server.selectRange(index, index);
                }
            }
        }
        this.rangeSelectRowFrom = index;
    }
}

export function _loadPageOverriden(page, cache) {
    // make sure same page isn't requested multiple times.
    if (!cache.pendingRequests[page] && this.dataProvider) {
        this._setLoading(true);
        cache.pendingRequests[page] = true;
        const params = {
            page,
            pageSize: this.pageSize,
            sortOrders: this._mapSorters(),
            filters: this._mapFilters(),
            parentItem: cache.parentItem,
        };

        this.dataProvider(params, (items, size) => {
            if (size !== undefined) {
                cache.size = size;
            } else {
                if (params.parentItem) {
                    cache.size = items.length;
                }
            }

            const currentItems = Array.from(this.$.items.children).map(
                (row) => row._item
            );

            // Populate the cache with new items
            items.forEach((item, itemsIndex) => {
                const itemIndex = page * this.pageSize + itemsIndex;
                cache.items[itemIndex] = item;
                if (this._isExpanded(item) && currentItems.indexOf(item) > -1) {
                    // Force synchronous data request for expanded item sub-cache
                    cache.ensureSubCacheForScaledIndex(itemIndex);
                }
            });

            this._hasData = true;

            delete cache.pendingRequests[page];

            this._setLoading(false);
            this._cache.updateSize();
            this._effectiveSize = this._cache.effectiveSize;

            Array.from(this.$.items.children)
                .filter((row) => !row.hidden)
                .forEach((row) => {
                    const cachedItem = this._cache.getItemForIndex(row.index);
                    if (cachedItem) {
                        // fix to ensure that the children are loaded - See here
                        this._getItem(row.index, row);
                    }
                });

            this._increasePoolIfNeeded(0);

            this.__itemsReceived();
        });
    }
};

export function _getItemOverriden(index, el) {
    if (index >= this._effectiveSize) {
        return;
    }
    el.index = index;
    const { cache, scaledIndex } = this._cache.getCacheAndIndex(index);
    const item = cache.items[scaledIndex];
    if (item) {
        this._toggleAttribute("loading", false, el);
        this._updateItem(el, item);
        if (this._isExpanded(item)) {
            cache.ensureSubCacheForScaledIndex(scaledIndex);
        }
    } else {
        this._toggleAttribute("loading", true, el);
        this._loadPage(this._getPageForIndex(scaledIndex), cache);
    }
    /** focus when get item if there is an item to focus **/
    if (this._rowNumberToFocus > 0) {
        if (index === this._rowNumberToFocus) {
            const row = Array.from(this.$.items.children).filter(
                (child) => child.index === this._rowNumberToFocus
            )[0];
            if (row) {
                this._focus();
            }
        }
    }
};