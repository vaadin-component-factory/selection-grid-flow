package com.vaadin.componentfactory.selectiongrid;

import com.vaadin.componentfactory.selectiongrid.bean.Person;
import com.vaadin.componentfactory.selectiongrid.service.PersonService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.Route;

import java.util.stream.Stream;

@Route(value = "lazy", layout = MainLayout.class)
public class LazyDataView extends VerticalLayout
{
    private static final long serialVersionUID = 7905375746719730227L;

    private PersonDataProvider personDataProvider = new PersonDataProvider();

    public LazyDataView()
    {
        Div messageDiv = new Div();

        Grid<Person> grid = new SelectionGrid<>();
        grid.setDataProvider(personDataProvider);

        grid.addColumn(Person::getFirstName).setHeader("First Name");
        grid.addColumn(Person::getAge).setHeader("Age");

        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid.asMultiSelect().addValueChangeListener(event -> {
            String message = String.format("Selection changed from %s to %s",
                event.getOldValue(), event.getValue());
            messageDiv.setText(message);
        });

        // You can pre-select items
        add(grid, messageDiv);
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