
customElements.whenDefined('vaadin-grid').then(() => {
    const Grid = customElements.get('vaadin-grid');
    if (Grid) {
        Grid.prototype.focusOnCell = function (rowNumber, cellNumber) {
            // const cellNumber = this.getColumnIndexByFlowId(colId);
            if (rowNumber < 0 || cellNumber < 0) {
                throw 'index out of bound';
            }
            this.scrollToIndex(rowNumber);
            // This is a very hacky way of doing stuff
            setTimeout(() => {
                const cell = this.$.items.children[rowNumber % (this.$.items.children.length - 1)].children[cellNumber];
                if (cell) {
                    console.log(cell);
                    cell.focus();
                } else {
                    throw 'index out of bound';
                }
            }, 100);
        }

        Grid.prototype.focusOnCellWhenReady = function(rowIndex, colId, firstCall) {
            if(this.loading || firstCall) {
                var that = this;
                setTimeout(function(){
                    that.focusOnCellWhenReady(rowIndex, colId, false);
                }, 1);
            } else {
                this.focusOnCell(rowIndex, colId);
            }
        };

        Grid.prototype.scrollWhenReady = function(index, firstCall) {
            if(this.loading || firstCall) {
                var that = this;
                console.log("scrollWhenReady loading");
                setTimeout(function(){
                    that.scrollWhenReady(index, false);
                }, 200);
            } else {
                console.log("scrollWhenReady scrollToIndex " + index);

                var that = this;
                setTimeout(function(){
                    that.scrollToIndex(index);
                }, 200);
            }
        };

        // TODO delete it if unused
        Grid.prototype.getColumnIndexByFlowId = function(flowId) {
            const index = this._columnTree.slice(0).pop()
                .filter(c => c._flowId)
                //.sort((b, a) => (b._order - a._order))
                .map(c => c._flowId).indexOf(flowId);
            return index;
        }

        Grid.prototype._getItem = function(index, el) {
            if (index >= this._effectiveSize) {
                return;
            }

            el.index = index;
            const {cache, scaledIndex} = this._cache.getCacheAndIndex(index);
            const item = cache.items[scaledIndex];
            if (item) {
                this._toggleAttribute('loading', false, el);
                this._updateItem(el, item);
                if (this._isExpanded(item)) {
                    cache.ensureSubCacheForScaledIndex(scaledIndex);
                }
            } else {
                this._toggleAttribute('loading', true, el);
                this._loadPage(this._getPageForIndex(scaledIndex), cache);
            }

        }

/*
        Grid.prototype.refreshExpanded = function(index) {
            console.log("refreshExpanded - " + index);
            const {cache, scaledIndex} = this._cache.getCacheAndIndex(index);
            const item = cache.items[scaledIndex];
            debugger;
            if (item) {
                if (this._isExpanded(item)) {
                    console.log("_isExpanded");
                    cache.ensureSubCacheForScaledIndex(scaledIndex);
                } else {

                    console.log("not expanded");
                }
            } else {
                console.log("load page");
               this._loadPage(this._getPageForIndex(scaledIndex), cache);
            }
        }*/

    }
})