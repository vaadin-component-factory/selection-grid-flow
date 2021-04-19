package com.vaadin.componentfactory.selectiongrid;

import com.vaadin.componentfactory.selectiongrid.bean.Person;
import com.vaadin.componentfactory.selectiongrid.service.PersonService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

import java.util.List;

/**
 * Basic example with setItems
 */
@Route(value = "selectable-text", layout = MainLayout.class)
public class SelectableTextView extends Div {


    public SelectableTextView() {
        Span info = new Span("This grid has the theme variant \"SelectionGridVariant.SELECTABLE_TEXT\" set and allows selecting cell " +
                "text content. It will still deselect text when selecting multiple rows (the tiny selection flicker is normal and not removable due to technical " +
                "limitations of the browsers.");

        Div messageDiv = new Div();

        List<Person> personList = getItems();
        SelectionGrid<Person> grid = new SelectionGrid<>();

        grid.addThemeVariants(SelectionGridVariant.SELECTABLE_TEXT);

        grid.setItems(personList);

        grid.addColumn(Person::getFirstName).setHeader("First Name");
        grid.addColumn(Person::getAge).setHeader("Age");

        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid.asMultiSelect().addValueChangeListener(event -> {
            String message = String.format("Selection changed from %s to %s",
                event.getOldValue(), event.getValue());
            messageDiv.setText(message);
        });

        // You can pre-select items
        grid.asMultiSelect().select(personList.get(0), personList.get(1));
        add(info, grid, messageDiv);
    }

    private List<Person> getItems() {
        PersonService personService = new PersonService();
        return personService.fetchAll();
    }
}
