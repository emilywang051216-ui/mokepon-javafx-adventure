# Mokepon — JavaFX Turn-Based Adventure Game

A turn-based adventure game developed using **Java and JavaFX** as part of the University of Melbourne's SWEN20003 Object Oriented Software Development coursework.

The project combines map exploration, turn-based combat, character attributes, skills, status effects, and item management in a graphical desktop application.

## Tech Stack

- **Language:** Java 25
- **GUI:** JavaFX 25
- **Build Tool:** Gradle
- **Testing:** JUnit 6
- **Concepts:** Object-Oriented Programming, Modular Design, Game State Management

## Key Features

- **Character Selection:** Choose a Mokepon with its own attributes and abilities.
- **Map Exploration:** Navigate an overworld with different terrain types, encounters, and interactive objects.
- **Turn-Based Combat:** Use skills to attack opponents and manage battle outcomes.
- **Skills and Status Effects:** Support stat boosts, stun, blindness, bleeding, and other combat effects.
- **Items and Inventory:** Collect and use items during the adventure.
- **Graphical Interface:** JavaFX screens for character selection, exploration, and combat.

## Project Structure

```text
src/
├── main/java/
│   └── au/edu/unimelb/swen/oop/mokepon/
│       ├── model/    # Game rules and domain logic
│       ├── ui/       # JavaFX interface components
│       └── Main.java
└── test/java/        # JUnit tests

assets/                # Game images and visual resources
mokepon.game           # Game data
build.gradle.kts       # Gradle build configuration
```

## Getting Started

**Requirements:** JDK 25

On macOS or Linux:

```bash
./gradlew run
```

On Windows:

```powershell
.\gradlew.bat run
```

Run automated tests:

```bash
./gradlew test
```

## Testing

JUnit tests are included to check core game rules, including combat behaviour, attribute changes, and item interactions.

## Development Notes

This project was developed for SWEN20003 at the University of Melbourne.

Generative AI tools, including ChatGPT and Cursor, were used to discuss implementation approaches, assist with debugging, and draft parts of the implementation and tests. AI-generated suggestions were reviewed against the project requirements.

## Asset Credits

- [Openmon monster sprites](https://screensmith.itch.io/openmon-monster-sprites-set-5)
- [Fantasy GUI assets](https://opengameart.org/content/fantasy-gui-0)
- [Scroll UI container](https://opengameart.org/content/scroll-ui-container)
- [Grenze Gotisch font](https://www.omnibus-type.com/fonts/grenze-gotisch/)

Mokepon names and statistics include AI-generated content.

## Academic Integrity

This is a university coursework project. Source code should remain private unless public distribution is permitted by the course's academic integrity requirements.

## Author

Yiqing Wang
The University of Melbourne
