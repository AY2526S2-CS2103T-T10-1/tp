---
  layout: default.md
  title: "Yi Zhong's Project Portfolio Page"
---

### Project: Classroom Plus Plus

Classroom Plus Plus is a desktop teaching management application used to manage contacts and assignments. The user interacts with it using a CLI, and it has a GUI created with JavaFX. It is written in Java, and has about 10 kLoC.

Given below are my contributions to the project.

* **New Feature**: Added the ability to undo/redo previous commands.
  * What it does: allows the user to undo all previous commands one at a time. Preceding undo commands can be reversed by using the redo command.
  * Justification: This feature improves the product significantly because a user can make mistakes in commands and the app should provide a convenient way to rectify them.
  * Highlights: This enhancement affects existing commands and commands to be added in future. It required an in-depth analysis of design alternatives. The implementation too was challenging as it required changes to existing commands.
  * Credits: *{mention here if you reused any code/ideas from elsewhere or if a third-party library is heavily used in the feature so that a reader can make a more accurate judgement of how much effort went into the feature}*

* **Code contributed**: [RepoSense link]()

* **Project management**:
  * Managed releases `v1.1` - `v1.6` (6 releases) on GitHub
  * Added GitHub project safeguards: enabled branch protection rules (PR protection)
  * Enabled the issue tracker with custom issues
  * Created milestones for release planning

* **Enhancements to existing features**:
  * e.g. Updated the GUI color scheme (Pull requests [\#33](), [\#34]())

* **Documentation**:
  * User Guide:
    * e.g. Added documentation for the features `delete` and `find` [\#72]()
  * Developer Guide:
    * e.g. Added implementation details of the `delete` feature.

* **Community**:
  * PRs reviewed (with non-trivial review comments): [\#12](), [\#32](), [\#19](), [\#42]()
  * Contributed to forum discussions (examples: [1](https://github.com/NUS-CS2103-AY2526-S2/forum/issues/1#issuecomment-3742184466), [2](https://github.com/NUS-CS2103-AY2526-S2/forum/issues/114#issuecomment-3828848465), [3](https://github.com/NUS-CS2103-AY2526-S2/forum/issues/133#issuecomment-3841878040))
  * Reported bugs and suggestions for other teams in the class (examples: [1](), [2](), [3]())

* **Tools**:
  * Integrated a third-party Java CI using GitHub Actions for building and testing
  * Set up MarkBind page deployment for the project site
  * Integrated Codecov coverage reporting for test coverage metrics
