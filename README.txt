Design Patterns 2026 Term Project - Life Game
=============================================

Team: [team number]
Member: [student ID] [name]
GitHub: https://github.com/bdmanee/DesignPatterns-LifeGame-2026


1. Summary
----------
Allen Holub's Game of Life was extended with new features and its design
was improved with the design patterns from the course. All original
features (click to toggle cells, Clear, Load, Store, Exit, speed menu,
Play/Step) still work, and old .life save files still load.

New features
  E1  Rule selection (Conway, HighLife, Seeds, Day & Night, ...) and
      custom rules in B/S notation (e.g. B36/S23)
  E2  Pattern library (glider, LWSS, pulsar, Gosper glider gun, ...)
      with rotate / flip and stamping by dragging
  E3  Import patterns from .rle / .cells files (e.g. from the LifeWiki)
  E4  Statistics panel: generation, population, peak, cell age, size,
      population graph
  E5  Color cells by age
  E6  Undo / Redo (buttons, Edit menu, Ctrl+Z / Ctrl+Y)
  E7  Play / Pause / Step buttons with a status display
  E8  Draw / Erase drawing modes
  E9  Zoom (buttons and Ctrl + mouse wheel)

Patterns used
  Strategy        LifeRule, CellColorStrategy, PatternReader
  Builder         RuleBuilder -> BirthSurvivalRule
  Prototype       PatternRegistry / LifePattern.clone()
  Adapter         RlePatternAdapter (adapts com.holub.rle.RleDecoder)
  Visitor         CellVisitor, AliveCellCounter, StatisticsVisitor
  Composite       Cell / Neighborhood / Resident (instanceof checks removed)
  Observer        GameStateListener, StatisticsListener, ZoomListener,
                  CommandHistory.Listener, RunStateListener
  Command         GameCommand, UndoableCommand, CommandHistory
  Memento         BoardSnapshot (undo), Cell.Memento (load/store)
  State           GameState, PausedState, RunningState
  Facade          GameFacade
  MVC             LifeBoard (model), Universe (view + controller)
  Singleton       Universe, Clock, PatternRegistry.getDefault()


2. Files
--------
  README.txt                    this file
  README_HOLUB_ORIGINAL.TXT     README of the original Holub version
  pom.xml                       Maven build (compile, run tests, make jar)
  src/                          source code
    com/holub/life/             game classes
    com/holub/rle/              RLE decoder (used through the adapter)
    com/holub/ui, io, tools, asynch   original Holub library code
  test/                         JUnit 5 tests
  testdata/patterns/            sample .rle and .cells pattern files
  testdata/saved/               .life boards saved by the old version
  bin/                          compiled .class files
  docs/                         project document (PDF) and slides (PDF)
  video/                        demo video (or link)


3. How to run
-------------
  With Maven:
      mvn package
      java -jar target/life-game-2.0.jar

  Without Maven (from this folder):
      java -cp bin com.holub.life.Life

  In IntelliJ: run com.holub.life.Life. Mark "test" as Test Sources
  Root and run all tests in the "test" folder. The working directory
  must be the project folder because some tests read testdata/.


4. Tests
--------
  mvn test     (119 tests)

  LifeGameBaselineTest            original Conway behaviour of a cell
  LifeRuleTest, RuleBuilderTest   rules, builder, B/S notation
  LifeBoardTest                   model without Swing (blinker, block,
                                  glider crossing neighborhoods, load/store)
  SavedBoardCompatibilityTest     old .life files still load
  PatternFactoryTest, PatternRegistryTest   prototype registry, shapes
  PatternPlacementTest            Composite row/column access, visitors
  PatternImportTest               RLE adapter, .cells reader, real files
  StatisticsTest                  statistics visitor and tracker
  CellAgeTest                     cell age and color strategies
  UndoRedoTest                    commands, history, snapshots
  GameStateTest                   running / paused states
  ZoomModelTest                   zoom model and its listeners
