package com.vaadin.componentfactory.selectiongrid;

import com.vaadin.componentfactory.selectiongrid.bean.Person;
import com.vaadin.componentfactory.selectiongrid.service.PersonService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;

/**
 * Basic example with setItems
 */
@Route(value = "focus-col-group-grid", layout = MainLayout.class)
public class FocusGridMultipleColGroupView extends VerticalLayout {


    public FocusGridMultipleColGroupView() {

        List<Person> personList = getItems();
        SelectionGrid<Person> grid = new SelectionGrid<>();
        grid.setItems(personList);

        Grid.Column<Person> firstNameColumn = grid.addColumn(Person::getFirstName).setHeader("First Name").setKey("name");
        Grid.Column<Person> lastNameColumn = grid.addColumn(Person::getLastName).setHeader("Last Name").setKey("lastName");
        grid.addColumn(Person::getAge).setHeader("Age").setKey("age");

        ComboBox<Person> personComboBox = new ComboBox<>();
        personComboBox.addValueChangeListener(item -> {
            if (item.getValue() != null) {
                grid.focusOnCell(item.getValue(), lastNameColumn);
            }
        });
        personComboBox.setItems(personList);

        HeaderRow halfheaderRow = grid.prependHeaderRow();
        halfheaderRow.join(firstNameColumn, lastNameColumn).setText("Names");

        add(personComboBox);
        addAndExpand(grid);
        setPadding(false);
        grid.focusOnCell(personList.get(0), lastNameColumn);

    }

    private List<Person> getItems() {
        PersonService personService = new PersonService();
        return personService.fetchAll();
    }
}
