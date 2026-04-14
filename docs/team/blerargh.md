---
  layout: default.md
  title: "Terry's Project Portfolio Page"
---

# Project: Classroom Plus Plus

**Developer's name:** Terry

**Role(s):** Developer, UG Manager, PE-D Bug Triage Lead

Classroom Plus Plus is a desktop teaching management application used to manage contacts and assignments. The user interacts with it using a CLI, and it has a GUI created with JavaFX. It is written in Java, and has about 20 kLoC. This is around 10 kLoC more than the original AddressBook-Level3 codebase, which had around 10 kLoC.

Given below are my contributions to the project:

## New Features Implemented

1. Implemented the functionality to manage class groups in the application.
   * Implemented the `addclass` command (with alias `addc`), which allows users to add new class groups with a name.
   * Implemented the `allocclass` command (with alias `allocc`), which allows users to allocate contacts to specific class groups.
   * Implemented the `unallocclass` command (with alias `unallocc`), which allows users to unallocate contacts from specific class groups.
1. Co-implemented the `view` command with [tohyzhong](./tohyzhong.md) and [underscoregt](./underscoregt.md), which allows users to view the details of contacts, classes, and assignments in the application.
   * Implemented the `view class/CLASS_NAME` command, which allows users to view the details of a specific class group in the application.
   * Implemented the view for all allocated contacts in the class group being viewed.

## Code contributed

[RepoSense link](https://nus-cs2103-ay2526-s2.github.io/tp-dashboard/?search=&sort=totalCommits%20dsc&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=Blerargh&tabRepo=AY2526S2-CS2103T-T10-1%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

## Project Management

* Managed release `v1.5` on GitHub
* Co-managed the main bulk of the issue tracker and releases from milestones `v1.1` - `v1.4` and `v1.6` on GitHub together with [tohyzhong](./tohyzhong.md)
* Updated milestone deadlines based on project progress and team availability
* Reviewed all PE-D bug reports with [tohyzhong](./tohyzhong.md), and organised the bug reports by assigning labels based on report type and severity.
* Reviewed and merged [49 pull requests](https://github.com/AY2526S2-CS2103T-T10-1/tp/issues?q=state%3Aclosed%20is%3Apr%20reviewed-by%3ABlerargh%20sort%3Acomments-desc) from other team members out of the 108 PRs closed, and provided detailed review comments for improvements where necessary.

## Enhancements to existing features

1. Enhanced the weaker notion of equality checks for `Contact` to ensure that contacts with the same name but different tags are not considered equal in the application.
   * This is to account for the real-world scenario where different students may have the same name but have different attributes (e.g. different facial features, different families, etc.) that can be captured by tags in the application.
1. Enhanced the success result message for `addcontact` command to provide a warning message when a contact with the same name (but different tags) already exists in the application, to alert users of potential duplicates in the contact list.
1. Enhanced the error handling and user feedback for multiple commands involving index-based parameters (e.g. `edit INDEX ...`, `ct/` parameters, etc.), to provide more informative error messages (specifically the allowable indices) to users when invalid indices are provided.
1. Integrated fuzzy matching for command matching recommendations, to provide users with more accurate command suggestions when they input an invalid command.
   * Fuzzy matching logic is credited to and reused from [tohyzhong](./tohyzhong.md)'s implementation for search functionality in his [iP](https://github.com/tohyzhong/ip/blob/master/src/main/java/patrick/task/TaskList.java), and adapted for command matching in the `AddressBookParser` class.

## Documentation

* User Guide:
  * Added documentation for class group management features (e.g. `addclass`, `allocclass`, `unallocclass` commands)
  * Added documentation for `view` command
  * Main reviewer for User Guide PRs, providing detailed review comments for improvements and ensuring that the documentation is clear and free of errors.
  * Managed multiple rounds of proofreading of the User Guide to ensure that the documentation is clear and free of errors, together with [tohyzhong](./tohyzhong.md).
  * Managed the final round of UG bugfixes and improvements in the final iteration based on user testing feedback from PE-D bug reports as well as based on new features added in the final iteration
* Developer Guide:
  * Updated relevant UML diagrams to reflect the new class group management features
  * Defined the product scope and target user profile for the application in the Developer Guide, to provide clearer guidance for future developers on the intended use cases and user base of the application.

## Community

* Provided feedback in 156 review comments across [49 pull requests](https://github.com/AY2526S2-CS2103T-T10-1/tp/issues?q=state%3Aclosed%20is%3Apr%20reviewed-by%3ABlerargh%20sort%3Acomments-desc) from other team members out of the 108 PRs closed.
