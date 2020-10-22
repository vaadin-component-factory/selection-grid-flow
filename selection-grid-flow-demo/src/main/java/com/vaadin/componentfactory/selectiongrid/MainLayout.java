package com.vaadin.componentfactory.selectiongrid;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        final DrawerToggle drawerToggle = new DrawerToggle();
        final RouterLink simple = new RouterLink("Selection Grid", SimpleView.class);
        final RouterLink selectionTreeGrid = new RouterLink("Selection TreeGrid", SelectionTreeGridView.class);
        final RouterLink focus = new RouterLink("Focus Grid view", FocusGridView.class);
        final RouterLink focusHeader = new RouterLink("Multiple headers Grid view", FocusGridMultipleColGroupView.class);
        final RouterLink focusTree = new RouterLink("Focus TreeGrid view", FocusTreeGridView.class);
        final RouterLink focusHTree = new RouterLink("HorizontalScrolling TreeGrid view", FocusTreeHorizontalScrollGridView.class);
        final VerticalLayout menuLayout = new VerticalLayout(simple, selectionTreeGrid, focus, focusHeader, focusTree, focusHTree);
        addToDrawer(menuLayout);
        addToNavbar(drawerToggle);
    }

}