window.Vaadin.Flow.selectionTreeGridConnector = {
    initLazy: function (c) {
        // Check whether the connector was already initialized
        if (c.$selectionTreeGridconnector) {
            return;
        }
        c.$selectionTreeGridconnector = {};
/*
        c.scrollWhenReady = function(index, firstCall) {
            if(c.loading || firstCall) {
                var that = this;
                console.log("scrollWhenReady loading");
                setTimeout(function(){
                    that.scrollWhenReady(index, false);
                    }, 1);
            } else {
                console.log("scrollWhenReady scrollToIndex");
               c.scrollToIndex(index);
            }
        };*/
/*
        c.focusOnCellWhenReady = function(rowIndex, colIndex, firstCall) {
            if(c.loading || firstCall) {
                var that = this;
                console.log("focusOnCellWhenReady loading");
                setTimeout(function(){
                    that.focusOnCellWhenReady(rowIndex, colIndex, false);
                }, 1);
            } else {
                console.log("focusOnCellWhenReady scrollToIndex");
                c.focusOnCell(rowIndex, colIndex);
            }
        };
*/

    }
}