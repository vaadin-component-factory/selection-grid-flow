package com.vaadin.componentfactory.selectiongrid;

import com.vaadin.componentfactory.selectiongrid.bean.Person;
import com.vaadin.componentfactory.selectiongrid.service.PersonService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.Route;

import java.util.stream.Stream;

@Route(value = "lazy-focus", layout = MainLayout.class)
public class LazyFocusView extends VerticalLayout
{
    private static final long serialVersionUID = 7905375746719730227L;

    private PersonDataProvider personDataProvider = new PersonDataProvider();

    public LazyFocusView()
    {

        SelectionGrid<Person> grid = new SelectionGrid<>();
        grid.setDataProvider(personDataProvider);

        Grid.Column<Person> personColumn = grid.addColumn(Person::getFirstName).setHeader("First Name");
        grid.addColumn(Person::getAge).setHeader("Age");

        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        ComboBox<Person> personComboBox = new ComboBox<>();
        personComboBox.addValueChangeListener(item -> {
            if (item.getValue() != null) {
                grid.focusOnCell(item.getValue(), personColumn);
            }
        });
        personComboBox.setDataProvider(personDataProvider,null);

        add(personComboBox);
        addAndExpand(grid);
        add(grid);
    }

    public static class PersonDataProvider extends AbstractBackEndDataProvider<Person, String> {

        private PersonService personService = new PersonService();

        @Override
        protected Stream<Person> fetchFromBackEnd(Query<Person, String> query) {
            return personService.fetch(query.getOffset(), query.getLimit()).stream();
        }

        @Override
        protected int sizeInBackEnd(Query<Person, String> query) {
            return personService.count();
        }
    }
}