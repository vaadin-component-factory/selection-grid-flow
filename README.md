# 


## Development instructions

Build the project and install the add-on locally:
```
mvn clean install
```
Starting the demo server:

Go to selection-grid-flow-demo and run:
```
mvn jetty:run
```

This deploys demo at http://localhost:8080

## Description 

The Selection grid component provides support to Vaadin Grid and TreeGrid to:
* select a range of rows with SHIFT/CTRL Click like Vaadin 7 Table in the default mode
* focus on a particular row and column and expand the hierarchy if needed

The dataprovider needs to be a in-memory dataprovider:
* ListDataProvider for a Grid
* TreeDataProvider for a TreeGrid


### Multi select mode 

In multiselect mode, a user can select multiple items by clicking them with left mouse button while holding the Ctrl key (or Meta key) pressed.
If Ctrl is not held, clicking an item will select it and other selected items are deselected.
The user can select a range by selecting an item, holding the Shift key pressed, and clicking another item, in which case all the items between the two are also selected.
Multiple ranges can be selected by first selecting a range, then selecting an item while holding Ctrl, and then selecting another item with both Ctrl and Shift pressed.
Click on a row will select it.
Space on a row will toggle the selection.
You can also use Arrow down and Arrow up with Shift/Ctrl. 

## How to use it

Create a new component SelectionGrid/SelectionTreeGrid and use it like a Grid. The API is quite similar.


## Examples

### Basic example for a multiselect grid

```
    public SimpleView() {
        Div messageDiv = new Div();

        List<Person> personList = getItems();
        Grid<Person> grid = new SelectionGrid<>();
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
        add(grid, messageDiv);
    }

    private List<Person> getItems() {
        PersonService personService = new PersonService();
        return personService.fetchAll();
    }
```

### Focus and scroll on a particular cell

To focus on a particular cell, a new function has been added:
focusOnCell that requires an item and a column.

```
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
```

### Focus and scroll on a particular cell in a Treegrid

To focus on a particular cell in a TreeGrid you can use the same function `focusOnCell`
It will expand the parent nodes if they are collapsed, the expand is done node by node and can be slow.

```

    private DepartmentData departmentData = new DepartmentData();


    public FocusTreeGridView() {

        SelectionTreeGrid<Department> grid = buildGrid();

        ComboBox<Department> personComboBox = new ComboBox<>("Focus");
        personComboBox.addValueChangeListener(item -> {
            if (item.getValue() != null) {
                grid.focusOnCell(item.getValue(),grid.getColumnByKey("name"));
            }
        });
        personComboBox.setItems(departmentData.getDepartments());

        add(personComboBox);

        addAndExpand(grid);
        setPadding(false);
        setSizeFull();
    }

    private SelectionTreeGrid<Department> buildGrid() {
        SelectionTreeGrid<Department> grid = new SelectionTreeGrid<>();
        grid.setItems(departmentData.getRootDepartments(),
            departmentData::getChildDepartments);
        grid.addHierarchyColumn(Department::getName).setHeader("Department Name").setKey("name");
        grid.setWidthFull();
        return grid;
    }
```

## Limitations

* The dataprovider needs to be a in-memory dataprovider. The selection with shift/ctrl functionality may be extracted from the focus to be used in a lazy dataprovider.

## Demo

You can check the demo here: https://incubator.app.fi/selection-grid-flow-demo/

## Missing features or bugs

You can report any issue or missing feature on github: https://github.com/vaadin-component-factory/selection-grid-flow
