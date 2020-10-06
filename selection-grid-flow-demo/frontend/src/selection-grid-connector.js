
customElements.whenDefined('vaadin-grid').then(() => {
    const Grid = customElements.get('vaadin-grid');
    if (Grid) {
        Grid.prototype.focusOnCell = function (rowNumber, cellNumber) {
            // const cellNumber = this.getColumnIndexByFlowId(colId);
            if (rowNumber < 0 || cellNumber < 0) {
                throw 'index out of bound';
            }
            const cell = this.$.items.children[rowNumber].children[cellNumber];
            if (cell) {
                this.scrollToIndex(rowNumber);
                cell.focus();
            } else {
                throw 'index out of bound';
            }
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
                }, 1);
            } else {
                console.log("scrollWhenReady scrollToIndex");
                this.scrollToIndex(index);
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

    }
})