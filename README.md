# Terminal Danger - Narrative Adventure Engine

A robust, text-based adventure game engine built in Java. **Terminal Danger** features a sophisticated system for managing environmental puzzles, branching narrative choices, and persistent world states using external data configurations.

---

## 👤 Author
* **Trong Hieu Tran**

---

## 🚀 Overview
This project implements an "Escape Room" style adventure where players navigate through various locations, interact with NPCs, and solve items-based puzzles to progress. The engine is built with a focus on clean code principles, utilizing the **Command Pattern** to handle player actions and **GSON** for data-driven world building.

---

## 🛠️ Technical Stack
* **Language:** Java 23 (Amazon Corretto)
* **Data Parsing:** Google GSON (JSON)
* **Testing Framework:** JUnit 5 (Jupiter)
* **Build Architecture:** Object-Oriented with Command Pattern

---

## ✨ Key Features
* **Dynamic World Loading:** Rooms, items, and NPCs are loaded dynamically from `gamedata.json`, allowing for easy world expansion without changing the source code.
* **Inventory Management:** Features a realistic 3-item carry limit. Includes a **Storage Room** mechanic where players can drop items and retrieve them later using the `Drop` and `Search` commands.
* **Complex Puzzles:** Includes multi-stage state machines, such as the **Apartment 102 Safe Puzzle** which requires specific items in a specific order.
* **Branching Narratives:** The game tracks moral choices (e.g., the final encounter in the Cellar) which lead to different ending statuses.
* **Robust Navigation:** Supports forward and backward movement with conditional blocking (e.g., password-protected doors).

---

## 📁 Project Structure
| Package | Description |
| :--- | :--- |
| **`Playuh`** | Core Data Models (`Player`, `Room`, `Item`, `GameData`). |
| **`Commands`** | Implementation of player actions (Move, Search, Interact, Drop, Help). |
| **`MainGame`** | Main entry point and global state management logic. |
| **`Testing`** | Comprehensive unit tests for navigation, puzzles, and inventory. |

---

## 🎮 How to Play
1. **Launch the Game:** Run the `Main` class (you may need to reinstall gson in the libraries if it doesn't work).
2. **Navigation:** - Use `n` to move to the **Next** room.
    - Use `p` to move to the **Previous** room.
3. **Exploration:** - Use `s` to **Search** for items or check the Storage Room.
    - Use `i` to **Interact** with NPCs or puzzle objects (like safes or water sources).
4. **Management:**
    - Use `d` to **Drop** an item (only available in the Storage Room).
    - Use `items` to view descriptions of your held items.

---

## 🧪 Testing
The project includes a full suite of JUnit tests to ensure stability.
- **`InteractTesting`**: Tests puzzle logic and narrative choices.
- **`MoveNextTesting`**: Validates navigation and password-protected boundaries.
- **`MovePrevTesting`**: Also validates navigation.
- **`SearchTesting`**: Confirms item transfer from the room to the players inventory.
- **`HelpTesting`**: Confirms loading and display of all the available commands.
- **`DropTesting`**: Confirms item transfer logic between players and storage.
- **`ItemInteractTesting`**: Ensures resource-based descriptions load correctly.

---
