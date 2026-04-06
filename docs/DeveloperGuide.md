---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# Classroom Plus Plus (CPP) Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

1. SE-EDU's [AddressBook-Level3](https://github.com/se-edu/addressbook-level3) was used as a base for our project, and we have adapted and extended it to suit our needs.
1. GitHub Copilot was heavily used to assist in writing test code across all new components.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The _**Architecture Diagram**_ given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S2-CS2103T-T10-1/tp/tree/master/src/main/java/cpp/Main.java) and [`MainApp`](https://github.com/AY2526S2-CS2103T-T10-1/tp/tree/master/src/main/java/cpp/MainApp.java)) is in charge of the app launch and shut down.

* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The _Sequence Diagram_ below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its _API_ in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S2-CS2103T-T10-1/tp/tree/master/src/main/java/cpp/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `ContactListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S2-CS2103T-T10-1/tp/tree/master/src/main/java/cpp/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S2-CS2103T-T10-1/tp/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Contact` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2526S2-CS2103T-T10-1/tp/tree/master/src/main/java/cpp/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a contact).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:

* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddContactCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddContactCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddContactCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component

**API** : [`Model.java`](https://github.com/AY2526S2-CS2103T-T10-1/tp/tree/master/src/main/java/cpp/model/Model.java)

#### Model — current design

<puml src="diagrams/ModelClassDiagram.puml" width="1100" />

**Model (current design):** shows `AddressBook` and its relations to the 3 main entities: `Contact`, `ClassGroup`, and `Assignment` through the `Unique{Entity}List` counterparts. The `Model` component also includes a `UserPref` class to store user preferences (e.g., file path of the address book data, GUI settings).

--------------------------------------------------------------------------------------------------------------------

#### Contacts view

<puml src="diagrams/ModelClassDiagramContacts.puml" width="450" />

**Contacts view:** highlights classes and contact-related entities.

--------------------------------------------------------------------------------------------------------------------

#### Classes view

<puml src="diagrams/ModelClassDiagramClassGroups.puml" width="500" />

**ClassGroup view:** highlights classes and class group-related entities.

--------------------------------------------------------------------------------------------------------------------

#### Assignments view

<puml src="diagrams/ModelClassDiagramAssignments.puml" width="450" />

**Assignments view:** highlights classes and assignment-related entities and their relations to `Contact`.

--------------------------------------------------------------------------------------------------------------------

The `Model` component,

* stores the address book data (with 3 main entities: `Contact`, `ClassGroup`, and `Assignment`).
* each entity has a corresponding `Unique{Entity}List` (e.g., `UniqueContactList`, `UniqueClassGroupList`, `UniqueAssignmentList`) to manage the list of that entity and enforce uniqueness constraints.
* exposes the currently selected/filtered entities as an unmodifiable `ObservableList<{Entity}>` for UI binding.
* stores a `UserPref` object exposed as a `ReadOnlyUserPref`.
* is self-contained and does not depend on `UI`, `Logic` or `Storage` implementations.

### Storage component

**API** : [`Storage.java`](https://github.com/AY2526S2-CS2103T-T10-1/tp/tree/master/src/main/java/cpp/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="1100" />

The `Storage` component,

* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `cpp.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### ClassGroup management

The `Model` component manages `ClassGroup` entities using `UniqueClassGroupList`. This enforces uniqueness constraints and provides methods to add, delete, and update the entities.

Within the `ClassGroup` entities, the `Contact` entities that belong to the class are stored as a set of `String`s representing the `Contact`'s `id` field. This design allows us to easily make edits to contacts without having to worry about updating the `ClassGroup` entities, improving the efficiency of `edit` operations.

### Assignment management

The `Model` component manages `Assignment` entities using `UniqueAssignmentList`. This enforces uniqueness constraints and provides methods to add, delete, and update the entities.

`Assignment` entities adopt a different approach, by introducing a separate association class `ContactAssignment` to represent the association between a `Contact` and an `Assignment`. This allows us to easily manage the submission status and grading details including the score of an assignment for each contact, without having to modify the `Contact` or `Assignment` entities themselves. These functionalities are abstracted out into the `AssignmentManager` class, which manages the `ContactAssignment` entities, and provides methods to add, delete, update, and retrieve the entities efficiently.

Similar to the `ClassGroup` design, the `ContactAssignment` class also stores the `Contact`'s `id` field instead of a reference to the `Contact` object itself, for easier management of edits to `Contact`s. `ContactAssignment` objects also store the `Assignment`'s `id` field so that any edits to the `Assignment` can be easily managed as well.

A separate `UniqueContactAssignmentList` is used to manage the list of `ContactAssignment`s, and enforce uniqueness constraints for them.

<puml src="diagrams/AssignmentsCombinedDiagram.puml" width="600" />

As you might already have noticed, `ContactAssignment` objects do not store any information about classes. We chose this design to allow users to have greater control over the contacts who are allocated assignments. For example, a user can choose to assign an assignment to the entire class initially and choose to unallocate it from certain contacts later on, without having to worry about the class information being stored in the `ContactAssignment` objects.

As a result of this design, any class group operations will not affect the `ContactAssignment` entities. Hence, users will have to manually allocate or unallocate assignments to contacts when they allocate or unallocate contacts from class groups. We believe this is a reasonable trade-off to give users more control over the assignment allocation.

### Delete enhancements

The `delete` command now supports deleting contacts, assignments, and class groups through a single command word. `DeleteCommandParser` acts as a dispatcher: it inspects which prefix is present in the user input (`ct/` for contacts, `ass/` for assignments, `c/` for class groups) and constructs the corresponding subcommand (`DeleteContactCommand`, `DeleteAssignmentCommand`, or `DeleteClassGroupCommand`).

The sequence diagram below illustrates the interactions within the `Logic` component for the command `delete c/CS2103T10`:

<puml src="diagrams/DeleteClassGroupSequenceDiagram.puml" width="900"/>

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.

</box>

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th contact in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `addcontact n/David ...` to add a new contact. The `addcontact` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the contact was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `addcontact n/David ...​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.

  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the contact, class, or assignment being deleted).

  * Cons: We must ensure that the implementation of each individual command are correct.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* for educators
* has a need to manage a significant number of student, parent or fellow colleague contacts
* has a need to manage class groups and their associated contacts
* has a need to manage a significant number of tasks and assignments
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**:

* manage students, parents, colleagues, supervisors, bosses, etc. faster than a typical mouse/GUI driven app
* provides easy access and management of class groups and assignments in one place

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a ...                                    | I want to ...                                      | So that I can ...                                                          |
| -------- | ------------------------------------------- | -------------------------------------------------- | -------------------------------------------------------------------------- |
| `* * *`  | new user                                    | see usage instructions                             | refer to instructions when I forget how to use the App                     |
| `* * *`  | user                                        | delete a contact                                   | remove entries that I no longer need                                       |
| `* * *`  | user                                        | find a contact by name                             | locate details of contacts without having to go through the entire list    |
| `* * *`  | user                                        | find contacts by a specific field                  |                                                                            |
| `* * *`  | user                                        | add contacts                                       | easily retrieve them                                                       |
| `* * *`  | user                                        | see all the assignments/contacts/classes           | keep track of what has been created                                        |
| `* * *`  | busy user                                   | add deadlines/events                               | keep track of my things easily                                             |
| `* * *`  | teacher managing many students              | organize my students into separate groups by class | easily identify which students belong to which class                       |
| `* * *`  | teacher                                     | see all assignment status for each student         | keep track of every individual's performance                               |
| `* * *`  | teacher changing classes every year         | easily delete/archive multiple old contacts        | keep only the contacts I need accessible                                   |
| `* * *`  | CLI user                                    | quickly exit the program                           | don't have to waste time clicking the close button                         |
| `* *`    | user                                        | hide private contact details                       | minimize chance of someone else seeing them by accident                    |
| `* *`    | user pursuing efficiency                    | sort contacts by date accessed                     | easily find the most recently contacted contacts                           |
| `* *`    | forgetful teacher                           | find students even when I mistype their names      | easily find them even if I don't remember the exact spelling of their name |
| `* *`    | user with multiple devices                  | export and import selected contacts                | easily switch between devices                                              |
| `* *`    | meticulous teacher                          | add private notes (e.g., allergies)                | recall critical student welfare details                                    |
| `* *`    | user                                        | retrieve my input history                          | don't have to retype the entire command when I make a small typo           |
| `* *`    | careless user                               | undo my actions                                    | correct mistakes without losing my work                                    |
| `* *`    | form teacher                                | retrieve emails of a specific group                | blast announcements via email without manual entry                         |
| `* *`    | teacher managing committees                 | assign custom tags (e.g., "Prefect")               | filter students by extra-curricular roles                                  |
| `*`      | user with many contacts in the address book | sort contacts by name                              | locate a contact easily                                                    |
| `*`      | normal user                                 | edit contact details                               | keep my contact information up to date                                     |
| `*`      | forgetful user                              | have reminders for my deadlines                    | do not forget my tasks                                                     |
| `*`      | busy user                                   | view all events/deadlines in a calendar view       | see my schedule at a glance                                                |

### Use cases

(For all use cases below, the **System** is `ClassroomPlusPlus` and the **Actor** is the `user` (a teacher), unless specified otherwise)

#### Use Case 1: Add a New Contact

**MSS**

1. User requests to `addcontact` a contact with fields (Name, Phone, Email, Address, Class name, Assignment name).
1. System <u>[shows updated contact list (Use Case 2)](#use-case-2-list-contacts)</u>.
1. Use case ends.

**Extensions**

* 1a. User left optional fields blank (e.g. Class name, Assignment name).
  * 1a1. System accepts the input and proceeds.
* 1b. A contact with the same name and phone number exists in contact list.
  * 1b1. System shows an error message.
  * 1b2. Use case ends.
* 1c. Invalid or missing fields.
  * 1c1. System shows an error message.
  * 1c2. Use case ends.

#### Use Case 2: List Contacts

**MSS**

1. User requests to list contacts.
1. System shows the list of contacts.
1. Use case ends.

**Extensions**

* 2a. No contacts exist in the list.
  * 2a1. System shows an empty list with a message indicating that no contacts exist.

#### Use Case 3: Add a New Class

**MSS**

1. User requests to add a class with fields (Class name).
1. System <u>[shows class list (Use Case 4)](#use-case-4-list-classes)</u>.
1. Use case ends.

**Extensions**

* 1a. A class with the same name exists in class list.
  * 1a1. System shows an error message.
  * 1a2. Use case ends.
* 1b. Invalid or missing fields.
  * 1b1. System shows an error message.
  * 1b2. Use case ends.

#### Use Case 4: List Classes

**MSS**

1. User requests to list classes.
1. System shows the list of classes.
1. Use case ends.

**Extensions**

* 2a. No classes exist in the list.
  * 2a1. System shows an empty list with a message indicating that no classes exist.
  * 2a2. Use case ends.

#### Use Case 5: Assign a Contact to a Class

**MSS**

1. User requests to assign a contact to a class.
1. System assigns the contact to the class.
1. Use case ends.

**Extensions**

* 1a. Invalid contact or class.
  * 1a1. System shows an error message.
  * 1a2. Use case ends.
* 1b. Contact is already assigned to the class.
  * 1b1. System shows an error message.
  * 1b2. Use case ends.

#### Use Case 6: Remove a Contact from a Class

**MSS**

1. User <u>[views the list of contacts assigned to a class (Use Case 18)](#use-case-18-view-a-class)</u>.
1. User requests to remove a contact from the class.
1. System removes the contact from the class.
1. Use case ends.

**Extensions**

* 2a. Invalid contact or class.
  * 2a1. System shows an error message.
  * 2a2. Use case ends.
* 2b. Contact is not assigned to the class.
  * 2b1. System shows an error message.
  * 2b2. Use case ends.

#### Use Case 7: Add a New Assignment

**MSS**

1. User requests to add an assignment with fields (Assignment name, Deadline, Class name, Contact indices).
1. System <u>[shows updated assignment list (Use Case 8)](#use-case-8-list-assignments)</u>.
1. Use case ends.

**Extensions**

* 1a. A assignment with the same name exists in assignment list.
  * 1a1. System shows an error message.
  * 1a2. Use case ends.
* 1b. Invalid or missing fields.
  * 1b1. System shows an error message.
  * 1b2. Use case ends.
* 1c. User left optional fields blank (e.g. Class name, Contact indices).
  * 1c1. System accepts the input and proceeds.

#### Use Case 8: List Assignments

**MSS**

1. User requests to list assignments.
1. System shows the list of assignments.
1. Use case ends.

**Extensions**

* 2a. No assignments exist in the list.
  * 2a1. System shows an empty list with a message indicating that no assignments exist.
  * 2a2. Use case ends.

#### Use Case 9: Assign an Assignment

**MSS**

1. User requests to assign an assignment with fields (Assignment name, Class name, Contact indices).
1. System assigns the assignment to the contacts.
1. Use case ends.

**Extensions**

* 1a. Invalid assignment or contacts.
  * 1a1. System shows an error message.
  * 1a2. Use case ends.
* 1b. Assignment is already assigned to one of the contacts.
  * 1b1. System proceeds with Step 3.
* 1c. User left Class name field blank.
  * 1c1. System proceeds with Step 3, assigning only to contacts specified.
* 1d. User left Contact indices field blank.
  * 1d1. System proceeds with Step 3, assigning to all contacts in the specified class.

#### Use Case 10: Unassign an Assignment

**MSS**

1. User <u>[views the list of contacts assigned to an assignment (Use Case 19)](#use-case-19-view-an-assignment)</u>.
1. User requests to unassign an assignment with fields (Assignment name, Class name, Contact indices).
1. System unassigns the assignment from the contacts.
1. Use case ends.

**Extensions**

* 2a. Invalid assignment, classname, or contacts.
  * 2a1. System shows an error message.
  * 2a2. Use case ends.
* 2b. Assignment is not assigned to one of the contacts.
  * 2b1. System proceeds with Step 3 without the contact.
* 2c. User left Class name field blank.
  * 2c1. System proceeds with Step 3, unassigning only from contacts specified.
* 2d. User left Contact indices field blank.
  * 2d1. System proceeds with Step 3, unassigning from all contacts in the specified class.
* 2e. User left both Class name and Contact indices fields blank.
  * 2e1. System proceeds with Step 3, unassigning from all contacts assigned to the assignment.
* 2f. No contacts are assigned to the assignment.
  * 2f1. System shows an error message.
  * 2f2. Use case ends.

#### Use Case 11: Delete a Contact

**MSS**

1. User requests to delete a contact.
1. System removes the contact from the contact list, any classes and assignments it is assigned to.
1. Use case ends.

**Extensions**

* 1a. Invalid contact.
  * 1a1. System shows an error message.
  * 1a2. Use case ends.
* 1b. User specified multiple contacts to delete.
  * 1b1. System repeats Steps 2 and 3 for all specified contacts.

#### Use Case 12: Delete a Class

**MSS**

1. User requests to delete a class.
1. System removes the class from the class list, and from any students belonging to the class.
1. Use case ends.

**Extensions**

* 1a. Invalid class.
  * 1a1. System shows an error message.
  * 1a2. Use case ends.
* 1b. User specified multiple classes to delete.
  * 1b1. System repeats Steps 2 and 3 for all classes.

#### Use Case 13: Delete an Assignment

**MSS**

1. User requests to delete an assignment.
1. System removes the assignment from the assignment list, and from any students who have it assigned.
1. Use case ends.

**Extensions**

* 1a. Invalid assignment.
  * 1a1. System shows an error message.
  * 1a2. Use case ends.
* 1b. User specified multiple assignments to delete.
  * 1b1. System repeats Steps 2 and 3 for all assignments.

<!-- Start of non MVP feature use cases -->

**The use cases below are for features that are not part of the MVP, but are proposed to be implemented if time permits.**

#### Use Case 14: Find a Contact

**MSS**

1. User requests to find contacts by name, phone, class, or email.
1. System displays the matching contacts.
1. Use case ends.

**Extensions**

* 1a. No contacts match the search query.
  * 1a1. System shows an empty list with a message indicating no matches found.
  * 1a2. Use case ends.
* 1b. Invalid search query (e.g. invalid field, or missing search keyword).
  * 1b1. System shows an error message.
  * 1b2. Use case ends.

#### Use Case 15: Find a Class

**MSS**

1. User requests to find a class by name.
1. System displays the matching classes.
1. Use case ends.

**Extensions**

* 1a. No classes match the search query.
  * 1a1. System shows an empty list with a message indicating no matches found.
  * 1a2. Use case ends.

#### Use Case 16: Find an Assignment

**MSS**

1. User requests to find an assignment by name or deadline.
1. System displays the matching assignments.
1. Use case ends.

**Extensions**

* 1a. No assignments match the search query.
  * 1a1. System shows an empty list with a message indicating no matches found.
  * 1a2. Use case ends.
* 1b. Invalid search query (e.g. invalid field, or missing search keyword).
  * 1b1. System shows an error message.
  * 1b2. Use case ends.

#### Use Case 17: View a Contact

**MSS**

1. User <u>[views the list of contacts (Use Case 2)](#use-case-2-list-contacts)</u>.
1. User requests to view a contact.
1. System displays the contact details, including any assignments and their deadlines assigned to the contact.
1. Use case ends.

**Extensions**

* 2a. Invalid contact.
  * 2a1. System shows an error message.
  * 2a2. Use case ends.

#### Use Case 18: View a Class

**MSS**

1. User <u>[views the list of classes (Use Case 4)](#use-case-4-list-classes)</u>.
1. User requests to view a class.
1. System displays the class details and associated contacts.
1. Use case ends.

**Extensions**

* 2a. Invalid class.
  * 2a1. System shows an error message.
  * 2a2. Use case ends.

#### Use Case 19: View an Assignment

**MSS**

1. User <u>[views the list of assignments (Use Case 8)](#use-case-8-list-assignments)</u>.
1. User requests to view an assignment.
1. System displays the assignment details and associated contacts.
1. Use case ends.

**Extensions**

* 2a. Invalid assignment.
  * 2a1. System shows an error message.
  * 2a2. Use case ends.

#### Use Case 20: Update Submission Status of an Assignment

**MSS**

1. User requests to update a contact's submission status for an assignment.
1. System updates the assignment's submission status for the contact.
1. Use case ends.

**Extensions**

* 1a. Invalid contact or assignment.
  * 1a1. System shows an error message.
  * 1a2. Use case ends.
* 1b. Contact is not assigned the assignment.
  * 1b1. System shows an error message.
  * 1b2. Use case ends.

#### Use Case 21: Mark an Assignment

**MSS**

1. User requests to mark an assignment for a contact with a score.
1. System marks the assignment as graded for the contact and sets the score.
1. Use case ends.

**Extensions**

* 1a. Invalid assignment or contact.
  * 1a1. System shows an error message.
  * 1a2. Use case ends.
* 1b. Assignment is already graded for the contact.
  * 1b1. System continues with Step 3, overwriting the previous score.
* 1c. Invalid score.
  * 1c1. System shows an error message.
  * 1c2. Use case ends.

#### Use Case 22: Unmark an Assignment

**MSS**

1. User requests to unmark an assignment for a contact.
1. System ungrades the assignment for the contact and removes the score.
1. Use case ends.

**Extensions**

* 1a. Invalid assignment or contact.
  * 1a1. System shows an error message.
  * 1a2. Use case ends.
* 1b. Assignment is not marked for the contact.
  * 1b1. System shows an error message.
  * 1b2. Use case ends.

### Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
1. Should be able to hold up to 1000 contacts, 500 classes, and 500 assignments without a noticeable performance degradation or sluggishness during in performance for typical usage.
1. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
1. Should respond to user commands within 2 seconds to ensure a smooth user experience.
1. Should launch and load existing data within 5 seconds.
1. Should handle invalid inputs by highlighting the command input in red with an error message describing the issue and, where applicable, the correct format or usage.
1. Should work well for standard screen resolutions of 1920x1080 and higher (at 100% and 125% screen scales), and must remain fully usable for resolutions of 1280x720 and higher (at 150% screen scale).
1. Data should be stored locally in a human-editable text file (JSON) without the use of any Database Management System.
1. Should automatically save data to the hard disk after any command that modifies the state to prevent data loss.

### Glossary

* **Assignment**: A task created by the user containing minimally a deadline, and may include submission status and grading. Can be assigned to _Classes_ and _Contacts_.
* **Class**: A user-defined group of contacts.
* **CLI (Command Line Interface)**: The text-based interface through which users interact with the system by typing command.
* **Command**: A text-based instruction entered into the CLI that triggers a system action.
* **Contact**: An individual entry of a student or teacher, containing minimally a name, phone number, email address, and may include _Classes_ and _Assignments_.
* **Deadline**: Time and date specifying the due date of an _Assignment_.
* **Filtered List**: A dynamically updated subset of Contacts currently displayed in the GUI.
* **GUI (Graphical User Interface)**: The visual interface of the application that displays tabs, lists, and feedback messages.
* **Index**:  The number shown beside an item in the currently displayed list.
* **Invalid**: Violates one or more specified formats or constraints defined by the system.
* **JSON (JavaScript Object Notation)**: The human-readable file format used to automatically save and load application data locally.
* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **Submission status**: The state of an _Assignment_ for a specified _Contact_ (e.g. not submitted, submitted).
* **Tags**: A user-defined, optional label attached to a _Contact_ for categorization and filtering (e.g. "Prefect").
* **Valid**: Satisfies all specified format and constraints defined by the system.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more _exploratory_ testing.

</box>

### Adding classes

1. Adding a class group and checking that it is added successfully

    * Prerequisites: The class group "Class 1" does not exist in the address book, and the user is currently on the Classes tab in the GUI.

    * Test case: `addclass c/Class 1`

        Expected outcome:

        ```text
        New class group added: Class 1
        ```

1. Adding a class group that already exists

    * Prerequisites: The class group "Class 1" already exists in the address book.

    * Test case: `addclass c/Class 1`

        Expected outcome:

        ```text
        This class group already exists in the address book
        ```

### Adding assignments

1. Adding an assignment and checking that it is added successfully

    * Prerequisites: The assignment "Assignment 1" does not exist in the address book, and the user is currently on the Assignments tab in the GUI.

    * Test case: `addass ass/Assignment 1 d/12-12-2026 23:59`

        Expected outcome:

        ```text
        New assignment added: Assignment 1; Deadline: 12-12-2026 23:59
        ```

1. Adding an assignment that already exists

    * Prerequisites: The assignment "Assignment 1" already exists in the address book.

    * Test case: `addass ass/Assignment 1 d/12-12-2026 23:59`

        Expected outcome:

        ```text
        This assignment already exists in the address book
        ```

### Grading assignments

1. Grading an assignment before it is submitted should fail

   * Prerequisites: The assignment "Assignment 1" exists in the address book, and the contact with index 1 (Alex Yeoh) is allocated the assignment but has not submitted it.

   * Test case: `grade ass/Assignment 1 ct/1 s/90.654`

        Expected outcome:

        ```text
        Failed to grade any contacts for the assignment.
        Contacts not graded (already graded): None
        Contacts not graded (not submitted yet): Alex Yeoh
        Contacts not graded (not allocated the assignment): None
        Contacts not graded (grade time before submission time): None
        ```

1. Grading a submitted assignment

    * Prerequisites: The assignment "Assignment 1" exists in the address book, and the contact with index 1 (Alex Yeoh) is allocated the assignment and has submitted it.

    * Test case: `grade ass/Assignment 1 ct/1 s/90.654`

        Expected outcome:

        ```text
        Graded assignment: Assignment 1; Deadline: 12-12-2026 23:59 on <Date and time at which command is executed> for 1 contact(s) with score 90.7.
        Contacts graded: Alex Yeoh
        Contacts not graded (already graded): None
        Contacts not graded (not submitted yet): None
        Contacts not graded (not allocated the assignment): None
        Contacts not graded (grade time before submission time): None
        ```

## **Appendix: Effort**

The difficulty level of CPP is estimated to be around a moderate level. Extensive features have been introduced to CPP in a short amount of time.

Some achievements of our team include the introduction of 2 entirely new entities (Classes and Assignments) to the address book, which took a significant amount of effort to implement, given the complexity of integrating them into the existing system which resulted in many areas of development.

We also introduced various features related to them, such as the ability to allocate and unallocate classes and assignments, update submission status and grading details for each contact's assignment submission, and view the details of each contact, class, and assignment.

Other features that modified to fit our new design include the `edit` command, which now allows editing of class and assignment details, and the `delete` command, which now also deletes the associated classes and assignments for a contact.

The GUI had to be modified to accommodate the new entities, to be able to display the details of each contact, class, and assignment, and to provide a way for users to navigate between the different tabs. The `list` and `find` (now `findcontact`, `findclass`, `findass`) commands also had a rework to be able to list and filter the different entities based on the current tab.

To make the user experience smoother, we also implemented various features such as allocation options during contact, class, and assignment creation.

Overall, the introduction of the new entities and their associated features required a significant amount of effort in terms of design, implementation, and testing, covering a wide range of areas in the codebase and requiring 21091 lines of code changes (accurate as of 29-03-2026).

## **Appendix: Planned Enhancements**

Team size: 5

1. **Filtering contacts by their submission and grading status for a specific assignment**

   * **Description**: Allow users to filter contacts based on their submission and grading status for a specific assignment. For example, users can filter to see only those contacts who have submitted an assignment but have not been graded yet.

   * **Proposed implementation**: Introduce new filter options in the `view` command that allow users to specify the assignment and the desired submission/grading status. The system will then display the filtered list of contacts accordingly.

   * **Example usage**:

       * Command: `view ass/Assignment 1 sub/true grade/false`

       * Expected outcome: List of contacts who have submitted the assignment but have not been graded yet.

1. **Sorting contacts by their scores for a specific assignment**

    * **Description**: Allow users to sort contacts based on their scores for a specific assignment.

    * **Proposed implementation**: Introduce a new sort option in the `view` command that allows users to specify the assignment and the sorting criteria (e.g., ascending or descending order of scores). The system will then display the list of contacts accordingly.

    * **Example usage**:

        * Command: `view ass/Assignment 1 sort/score asc`

        * Expected outcome: List of contacts sorted by their scores for the specified assignment in ascending order.

1. **Archiving contacts, classes, and assignments**

    * **Description**: Allows users to archive unused contacts, classes, and assignments, so that they can focus on the currently relevant ones.

    * **Proposed implementation**: Introduce a new `archive` command that allows users to move contacts, classes, and assignments to an archive state, making them hidden from the default view.

    * **Example usage**:

        * Command: `archive ct/1` or `archive c/Class 1` or `archive ass/Assignment 1`

        * Expected outcome: The specified contact, class, or assignment is moved to the archive state and hidden from the default view.

1. **Taking attendance for classes**

    * **Description**: Allows users to mark the attendance of contacts for specific classes.

    * **Proposed implementation**: Introduce a new `attend` and `absent` command that allows users to mark the attendance of contacts for a specific class.

    * **Example usage**:

        * Command: `attend c/Class 1 ct/1 2 3 4 5`

        * Expected outcome: The specified contacts with index 1, 2, 3, 4, 5 are marked as present for the given class.

        * Command: `absent c/Class 1 ct/6 7 8 9 10`

        * Expected outcome: The specified contacts with index 6, 7, 8, 9, 10 are marked as absent for the given class.

1. **Exporting data as a CSV file**

    * **Description**: Allows users to export the data in the address book to a file.

    * **Proposed implementation**: Introduce a new `export` command that allows users to export the data in the address book to a CSV file.

    * **Example usage**:

        * Command: `export f/contacts.csv`

        * Expected outcome: The data in the address book is exported to a file named `contacts.csv`, with the contacts, classes, and assignments organized in a structured format.
