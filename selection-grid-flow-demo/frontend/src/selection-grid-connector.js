
customElements.whenDefined('vaadin-grid').then(() => {
    const Grid = customElements.get('vaadin-grid');
    if (Grid) {
        Grid.prototype.focusOnCell = function (rowNumber, cellNumber) {
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

        Grid.prototype.focusOnCellWhenReady = function(rowIndex, colIndex, firstCall) {
            if(this.loading || firstCall) {
                var that = this;
                setTimeout(function(){
                    that.focusOnCellWhenReady(rowIndex, colIndex, false);
                }, 1);
            } else {
                this.focusOnCell(rowIndex, colIndex);
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

    }
})