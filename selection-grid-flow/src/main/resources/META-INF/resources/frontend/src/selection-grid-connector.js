customElements.whenDefined("vaadin-grid").then(() => {
  const Grid = customElements.get("vaadin-grid");
  if (Grid) {
    Grid.prototype.focusOnCell = function (rowNumber, cellNumber) {
      if (rowNumber < 0 || cellNumber < 0) {
        throw "index out of bound";
      }
      this.scrollToIndex(rowNumber);
      /** workaround when the expanded node opens children the index is outside the grid size **/
      if (rowNumber > this._effectiveSize) {
        const that = this;
        setTimeout( () => {
          that.scrollToIndex(rowNumber);
          that._startToFocus(rowNumber, cellNumber);
          }, 200);
      } else {
        this._startToFocus(rowNumber, cellNumber);
      }
      /** End of workaround **/
    };

    Grid.prototype._startToFocus = function (rowNumber, cellNumber) {
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
    }

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

    /**
     * This function overloads the current _loadPage to path this issue
     * https://github.com/vaadin/vaadin-grid/issues/2055
     */
    Grid.prototype._loadPage = function(page, cache) {
      // make sure same page isn't requested multiple times.
      if (!cache.pendingRequests[page] && this.dataProvider) {
        this._setLoading(true);
        cache.pendingRequests[page] = true;
        const params = {
          page,
          pageSize: this.pageSize,
          sortOrders: this._mapSorters(),
          filters: this._mapFilters(),
          parentItem: cache.parentItem
        };

        this.dataProvider(params, (items, size) => {
          if (size !== undefined) {
            cache.size = size;
          } else {
            if (params.parentItem) {
              cache.size = items.length;
            }
          }

          const currentItems = Array.from(this.$.items.children).map(row => row._item);

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
              .filter(row => !row.hidden)
              .forEach(row => {
                const cachedItem = this._cache.getItemForIndex(row.index);
                if (cachedItem) { // fix to ensure that the children are loaded - See here
                  this._getItem(row.index, row);
                }
              });

          this._increasePoolIfNeeded(0);

          this.__itemsReceived();
        });
      }
    }
  }
});
