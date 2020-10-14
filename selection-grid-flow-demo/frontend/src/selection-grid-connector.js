customElements.whenDefined("vaadin-grid").then(() => {
  const Grid = customElements.get("vaadin-grid");
  if (Grid) {
    Grid.prototype.focusOnCell = function (rowNumber, cellNumber) {
      if (rowNumber < 0 || cellNumber < 0) {
        throw "index out of bound";
      }
      this.scrollToIndex(rowNumber);
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

    Grid.prototype._focus = function () {
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

    Grid.prototype.focusOnCellWhenReady = function (
      rowIndex,
      colId,
      firstCall
    ) {
      if (this.loading || firstCall) {
        var that = this;
        setTimeout(function () {
          that.focusOnCellWhenReady(rowIndex, colId, false);
        }, 1);
      } else {
        this.focusOnCell(rowIndex, colId);
      }
    };

    Grid.prototype.scrollWhenReady = function (index, firstCall) {
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

    // TODO delete it if unused
    Grid.prototype.getColumnIndexByFlowId = function (flowId) {
      const index = this._columnTree
        .slice(0)
        .pop()
        .filter((c) => c._flowId)
        .map((c) => c._flowId)
        .indexOf(flowId);
      return index;
    };

    Grid.prototype._getItem = function (index, el) {
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

    const oldClickHandler = Grid.prototype._onClick;
    Grid.prototype._onClick = function _click(e) {
      const boundOldClickHandler = oldClickHandler.bind(this);
      boundOldClickHandler(e);
      const eventTarget = e.target;
      if (
        e.shiftKey &&
        eventTarget.nodeName.toLowerCase() === "vaadin-checkbox"
      ) {
        // the click happened on a checkbox
        if (eventTarget.checked) {
          const orderedSelectedKeys = this.selectedItems
            .map((i) => parseInt(i.key))
            .sort((a, b) => a - b);
          const selectedKey = this.selectedItems[this.selectedItems.length - 1]
            .key;
          const selectedKeyIndex = orderedSelectedKeys.indexOf(
            parseInt(selectedKey)
          );
          if (selectedKeyIndex > 0) {
            const previousItemKey = orderedSelectedKeys[selectedKeyIndex - 1];
            const selectedItemKey = parseInt(selectedKey);

            let maxInspected = previousItemKey;
            let scrollTargetIndex = 0;
            debugger
            console.log('gonna select', previousItemKey, selectedItemKey)
            while (maxInspected < selectedItemKey) {
              this.scrollToIndex(maxInspected);
              Array.from(this.$.items.children).forEach((row) => {
                const rowKey = parseInt(row._item.key);
                if (rowKey < selectedItemKey && rowKey > previousItemKey) {
                  this.selectItem(row._item);
                  scrollTargetIndex = row.index;
                }
              });
              maxInspected = parseInt(Array.from(this.$.items.children).sort((a, b) => parseInt(a._item.key) - parseInt(b._item.key))[this.$.items.children.length - 1]._item.key);
            }
            this.scrollToIndex(scrollTargetIndex);
            // Array.from(this.$.items.children).forEach((row) => {
            //   const rowItem = row._item;
            //   console.log(rowItem);
            //   const itemKey = parseInt(row._item.key);
            //   if (itemKey < selectedItemKey && itemKey > previousItemKey) {
            //     this.selectItem(rowItem);
            //   }
            // });
          }
        }
      }
    };
  }
});
