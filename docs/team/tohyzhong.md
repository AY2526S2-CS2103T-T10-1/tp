---
  layout: default.md
  title: "Yi Zhong's Project Portfolio Page"
---

## Project: Classroom Plus Plus

Classroom Plus Plus is a desktop teaching management application used to manage contacts and assignments. The user interacts with it using a CLI, and it has a GUI created with JavaFX. It is written in Java, and has about 20 kLoC. This is around 10 kLoC more than the original AddressBook-Level3 codebase, which is around 10 kLoC.

Of the 10385 lines of functional code in the project, I have contributed 6530 lines (inclusive of ~200 lines of reformatting lines).

Given below are my contributions to the project.

* **New Features**:

1. Added the ability to add assignments to the application.
   * Implemented the `add` command for assignments, which allows users to add new assignments with name and deadline.
1. Added the ability to allocate assignments to contacts and/or contacts in a class.
   * Implemented the `allocass` command, which allows users to allocate assignments to specific contacts and/or contacts in a class.
1. Added the ability to unallocate assignments from contacts and/or contacts in a class.
   * Implemented the `unallocass` command, which allows users to unallocate assignments from specific contacts and/or contacts in a class.
1. Added the ability to submit assignments for contacts and/or contacts in a class.
   * Implemented the `submit` command, which allows users to mark assignments as submitted for specific contacts and/or contacts in a class.
   * Ensured during submission, only allocated assignments can be submitted, and appropriate error messages are shown for invalid submission attempts.
1. Added the ability to unsubmit assignments for contacts and/or contacts in a class.
   * Implemented the `unsubmit` command, which allows users to mark assignments as not submitted for specific contacts and/or contacts in a class.
   * Ensured during unsubmission, all graded submissions are ungraded to maintain data integrity.
1. Added the ability to grade assignments for contacts and/or contacts in a class.
   * Implemented the `grade` command, which allows users to assign scores to submitted assignments for specific contacts and/or contacts in a class.
   * Ensured during grading, only submitted assignments can be graded, and appropriate error messages are shown for invalid grading attempts.
1. Added the ability to ungrade assignments for contacts and/or contacts in a class.
   * Implemented the `ungrade` command, which allows users to remove scores from graded assignments for specific contacts and/or contacts in a class.
1. Added the base structure of the viewbox in the application, which is used by all view commands (contacts, classes, assignments).
1. Implemented the `view ass/` command, which allows users to view the details of a specific assignment in the application.
   * Implemented the view for all contacts (and their submission and grading statuses) for the assignment being viewed.
1. Added detailed user feedback for allocation, unallocation, submission, unsubmission, grading, ungrading commands across class and assignment management.
   * Added tracking for different type of errors encountered during parsing and execution, to provide more informative error messages to users.
1. Added timezone tracking for deadlines, and dates used by submission and grading status tracking.
   * Enforced GMT datetime for storage files, and display application datetime based on the GMT offset in the `preferences.json` file.

* **Code contributed**: [RepoSense link](https://nus-cs2103-ay2526-s2.github.io/tp-dashboard/?search=&sort=totalCommits%20dsc&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=tohyzhong&tabRepo=AY2526S2-CS2103T-T10-1%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

* **Project management**:
  * Managed releases `v1.1` - `v1.6` (6 releases) on GitHub
  * Added GitHub project safeguards: enabled branch protection rules (PR protection)
  * Enabled the issue tracker with custom issues
  * Created milestones for release planning
  * Set up the GitHub team organisation
  * Set up the GitHub team repository
  * Renamed all addressbook references to cpp
  * Set up the project site using MarkBind and deployed it on GitHub Pages
  * Set up GitHub Actions for CI and Codecov for test coverage reporting
  * Set up repository-wide code formatting and markdown linting + formatting
  * Maintained bulk of issue trackers together with [Blerargh](./blerargh.md)
  * Updated all non-code documentation (e.g. README, project site) to reflect the new project name and features
  * Reviewed and merged [53 pull requests](https://github.com/AY2526S2-CS2103T-T10-1/tp/issues?q=is%3Apr%20state%3Aclosed%20reviewed-by%3Atohyzhong%20sort%3Acomments-desc) from other team members out of the 103 PRs closed
  * Went through all bug reports in PE-D with [Blerargh](./blerargh.md), and organised the bug reports by removing duplicates and assigning them
  * Added code coverage, CI, and deployment status badges to the README file and project site

* **Enhancements to existing features**:

1. Refactored the `Person` class to also include an id field, which is used for identifying contacts, even if their names or other details are changed by the `editct` command.
   * This is required for the assignment management features, as we need to be able to track which contacts are allocated to which assignments, even if their details are changed.
1. Enhanced current parsing logic to trim leading and trailing whitespaces, as well as any other unnecessary whitespace.
   * This is to improve the user experience, as users may accidentally add extra whitespace when entering commands, and the application should be able to handle such cases gracefully.
1. Added case-insensitive parsing for all commands and their parameters, to improve user experience and reduce errors due to case sensitivity.

* **Documentation**:
  * User Guide:
    * Added documentation for assignment management features (addass, allocass, unallocass, submit, unsubmit, grade, ungrade), including examples of how to use the new commands and their expected outputs.
    * Added documentation for the following non-feature changes:
      * Introduction, overview, and target users
      * Table of contents
      * Quick start (prerequisites, installation, quick tutorial)
      * Editing the preferences file
      * Planned features and future work
      * FAQ
      * Known issues and workarounds
      * Reordered the features in the user guide to be more intuitive for users, with contact management features first, followed by class management features, and then assignment management features.
      * Changed the colour scheme of the user guide to be more visually appealing and easier to read.
  * Developer Guide:
    * Added Acknowledgements section
    * Updated Model component documentation to include the new assignment management features, and the changes to the `Person` class.
      * Updated Overall UML of the Model component to include the new classes and changes to existing classes.
      * Added a separate detailed UML diagram for the assignment management structure, which includes the new classes and their relationships.
    * Updated Storage component documentation to include the new assignment management features.
      * Updated Overall UML of the Storage component to include the new assignment related classes (`JsonAdaptedAssignment` and `JsonAdaptedContactAssignment`).
    * Added Implementation section to explain the design choice of certain features:
      * ClassGroup management
      * Assignment management (including the design choice of having a separate `ContactAssignment` class to track the allocation, submission, and grading status of each contact for each assignment), and a UML diagram to illustrate the design.
    * Appendix (Use cases)
      * Use cases 1 - 13
      * use cases 17 - 25
    * Appendix (Instructions for manual testing)
      * Added instructions for manual testing of the new assignment management features, including test cases for each of the new commands and their expected outcomes.
    * Appendix (Effort)
    * Appendix (Planned Enhancements)

* **Community**:
  * PRs reviewed (there are too many to list, you may view them on [GitHub](https://github.com/AY2526S2-CS2103T-T10-1/tp/issues?q=is%3Apr%20state%3Aclosed%20reviewed-by%3Atohyzhong%20sort%3Acomments-desc))
  * Contributed to forum discussions (examples: [1](https://github.com/NUS-CS2103-AY2526-S2/forum/issues/1#issuecomment-3742184466), [2](https://github.com/NUS-CS2103-AY2526-S2/forum/issues/114#issuecomment-3828848465), [3](https://github.com/NUS-CS2103-AY2526-S2/forum/issues/133#issuecomment-3841878040))

* **Tools**:
  * Set up MarkBind page deployment for the project site
  * Integrated Codecov coverage reporting for test coverage metrics
