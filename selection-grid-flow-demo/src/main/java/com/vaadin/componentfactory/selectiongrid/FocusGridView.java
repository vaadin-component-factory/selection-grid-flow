package com.vaadin.componentfactory.selectiongrid;

import com.vaadin.componentfactory.selectiongrid.bean.Person;
import com.vaadin.componentfactory.selectiongrid.service.PersonService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;

/**
 * Basic example with setItems
 */
@Route(value = "focus-grid", layout = MainLayout.class)
public class FocusGridView extends VerticalLayout {


    public FocusGridView() {

        List<Person> personList = getItems();
        SelectionGrid<Person> grid = new SelectionGrid<>();
        grid.setItems(personList);

        grid.addColumn(Person::getFirstName).setHeader("First Name").setKey("name");
        grid.addColumn(Person::getAge).setHeader("Age").setKey("age");
        ComboBox<Person> personComboBox = new ComboBox<>();
        personComboBox.addValueChangeListener(item -> {
            if (item.getValue() != null) {
                grid.focusOnCell(item.getValue(), "name");
            }
        });
        personComboBox.setItems(personList);

        add(personComboBox);
        addAndExpand(grid);
        setPadding(false);

    }

    private List<Person> getItems() {
        PersonService personService = new PersonService();
        return personService.fetchAll();
    }
}
