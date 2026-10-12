# SWEN20003 Semester 2, 2026
# Project 2
# Mokepon

## Assumptions

* Aim, Swole, Nimble, and Bunker report the points actually gained after the statistic reaches 100.
* Stun and blindness last three of the affected Mokepon's own turns. The count decreases after that Mokepon acts, so damage already dealt earlier in the turn is left as it was.
* Bleeding ticks once at the end of a round, after both Mokepon have acted, and lasts for two such ticks. The tick uses the attacker's current strength. Applying the same status again resets its duration.
* If multiple overworld interactions share a tile, a Mokepon marker takes precedence. If no Mokepon marker is present, one mystery box is collected before the exit is resolved.
* Run away, the main menu, a loss, and the exit door all leave the battle copy behind, so the persistent Mokepon returns to its base statistics. Choosing a Mokepon from the select screen starts a new overworld run.

## AI Statement

I used ChatGPT (OpenAI) as a supporting tool during this project. It was used to clarify parts of the project specification, discuss implementation approaches, assist with identifying and understanding bugs, and review parts of the implementation against the stated requirements.

I used Cursor Grok 4.7, a generative AI coding assistant, while extending the Project 1 game to the Project 2 requirements. It was used to compare the specification with the existing code and to draft the overworld, the additional skills, battle items, and the related tests. I reviewed the resulting behaviour against the specification and decided which assumptions to keep.

All suggestions were critically reviewed before use. I made the final implementation decisions and tested the completed program to ensure that it met the project requirements.

## Code References

* None
