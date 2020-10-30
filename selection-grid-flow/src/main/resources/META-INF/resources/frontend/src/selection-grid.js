customElements.whenDefined("vaadin-selection-grid").then(() => {
    const Grid = customElements.get("vaadin-selection-grid");
    if (Grid) {
        const oldClickHandler = Grid.prototype._onClick;
        Grid.prototype._onClick = function _click(e) {
            const boundOldClickHandler = oldClickHandler.bind(this);
            boundOldClickHandler(e);

            this._selectionGridSelectRow(e);
        };
        Grid.prototype.old_onNavigationKeyDown = Grid.prototype._onNavigationKeyDown;
        Grid.prototype._onNavigationKeyDown = function _onNavigationKeyDownOverridden(e, key) {
            this.old_onNavigationKeyDown(e,key);
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

        Grid.prototype.old_onSpaceKeyDown = Grid.prototype._onSpaceKeyDown;
        Grid.prototype._onSpaceKeyDown = function _onSpaceKeyDownOverriden(e) {
            this.old_onSpaceKeyDown(e);
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
    }
});
