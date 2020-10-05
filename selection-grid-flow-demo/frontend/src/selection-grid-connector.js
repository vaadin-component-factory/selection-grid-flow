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
}