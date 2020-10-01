const Grid = customElements.get('vaadin-grid');
if (Grid) {
    // store the original method
    const selectedItemsChanged = Grid.prototype._selectedItemsChanged;
    Grid.prototype._selectedItemsChanged = function  _selectedItemsChanged(e) {
        console.log("test overload");
        if (this.$.items.children.length && (e.path === 'selectedItems' || e.path === 'selectedItems.splices')) {
            Array.from(this.$.items.children).forEach(row => {
                this._updateItem(row, row._item);
            });
        }
    };
}