const Grid = customElements.get('vaadin-grid');
if (Grid) {
    const oldClickHandler = Grid.prototype._onClick;

    Grid.prototype._onClick = function _click(e) {
        const boundOldClickHandler = oldClickHandler.bind(this);
        boundOldClickHandler(e);
        const eventTarget = e.target;
        if (e.shiftKey && eventTarget.nodeName.toLowerCase() === 'vaadin-checkbox') {
            // the click happened on a checkbox
            if (eventTarget.checked) {
                const orderedSelectedKeys = this.selectedItems.map(i => parseInt(i.key)).sort((a, b) => a - b);
                const selectedKey = this.selectedItems[this.selectedItems.length - 1].key;
                const selectedKeyIndex = orderedSelectedKeys.indexOf(parseInt(selectedKey));
                if (selectedKeyIndex > 0) {
                    const previousItemKey = orderedSelectedKeys[selectedKeyIndex - 1];
                    const selectedItemKey = parseInt(selectedKey);
                    Array.from(this.$.items.children).forEach(row => {
                        const rowItem = row._item;
                        const itemKey = parseInt(row._item.key);
                        if (itemKey < selectedItemKey && itemKey > previousItemKey) {
                            this.selectItem(rowItem);
                        }
                    });
                }
            }
        }
    }
}