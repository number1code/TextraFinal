 LibGDX Emotive Text Suite (with TextraTypist)

A powerful demonstration of advanced, animated text rendering in a Java desktop application using the **LibGDX** framework. This project showcases the capabilities of the **`freetypist`** and **`textra`** libraries to create dynamic, emotionally expressive text effects, similar to those found in text-heavy games.

The application displays a series of text phrases, one at a time, each featuring unique, animated effects powered by TextraTypist's markup language.

![Screenshot of the Emotive Text Suite in action](textshowcase.gif)

---

### ✨ Features

*   **Dynamic Text Animation:** Utilizes the `TextraLabel` widget to render text with a typewriter effect and a wide range of special effects.
*   **High-Quality Font Rendering:** Leverages the `gdx-freetype` extension to load and render crisp, scalable TrueType Fonts (`.ttf`) with proper texture filtering for a smooth, anti-aliased look at any size.
*   **Simple, Interactive Controls:** Users can cycle through a predefined list of animated phrases by pressing the **Spacebar**.
*   **Organized Code Structure:** Built using the LibGDX `Game` and `Screen` pattern for clean, maintainable, and scalable code.

### 🛠️ Technologies Used

*   **Language:** Java
*   **Framework:** LibGDX
*   **Build System:** Gradle
*   **Key Libraries / Extensions:**
    *   **`freetypist`:** The core extension that seamlessly integrates `gdx-freetype` with `textratypist`.
    *   **`textratypist`:** The powerful text rendering and animation library by tommyettinger.
    *   **`gdx-freetype`:** For loading and generating high-quality bitmap fonts from `.ttf` files.
    *   **`Scene2D.ui`:** LibGDX's UI framework for layout and widget management.

### 🚀 How to Run This Project

1.  **Prerequisites:** You must have a compatible Java Development Kit (JDK) installed. This project is configured and tested with **OpenJDK 17**.
2.  **Clone the repository.**
3.  **Open the project** in a compatible IDE (IntelliJ IDEA is recommended).
4.  The IDE should automatically detect and sync the Gradle project.
5.  Add a Gradle Run Configuration of lwjgl3:run to your IDE or use lwjgl3:run in the terminal.
6.  Working example in VfxProductionScreen.java, a libgdx screen with video background and controls to cycle marked up text on screen.

### 🎮 Controls

*   **`SPACEBAR`**: Press the Spacebar to immediately advance to the next phrase in the sequence. The phrases will loop back to the beginning after the last one is shown.

---

### 🔧 Customization Guide

This project is designed to be easily customized.

#### How to Add Your Own Phrases

Modifying the text is simple. All the phrases are stored in an `Array<String>` inside the `LyricsScreen.java` file.

1.  Open the file: `core/src/com/your/package/name/LyricsScreen.java`.
2.  Locate the `show()` method.
3.  Find the `lyrics` array initialization block (around line 70).
4.  Add, edit, or remove strings from this array. Each string is one screen of text.

```java
// Example:
lyrics = new Array<>();
lyrics.add("[SHAKE;FADE]This is the first phrase...[/FADE][/SHAKE]");
lyrics.add("[*]This is a new phrase[*] with bold text!"); // Add your new line here
lyrics.add("Press [BUMP;RED]SPACEBAR[/BUMP] to cycle through the lyrics.");
You can use any of the effects documented in the TextraTypist Wiki.
How to Use a Different Font
You can use almost any TrueType Font (.ttf) with this project to achieve a flawless, smooth upscale.
Add the Font File: Place your custom .ttf file (e.g., MyCoolFont.ttf) into the core/assets/ directory of the project.
Update the Code: Open LyricsScreen.java and go to the show() method. Find the line that loads the font (around line 55).
Change the Filename: Update the string to match the name of your new font file.
code
Java
// Before:
FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));

// After:
FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("MyCoolFont.ttf"));
Configure Filtering (Already Done!): The crucial step for "flawless upscaling" is setting the texture filter. This code is already in place, ensuring your custom font will also be rendered smoothly.
code
Java
// This code ensures any font you load will be smooth
parameter.magFilter = Texture.TextureFilter.Linear;
parameter.minFilter = Texture.TextureFilter.Linear;
Re-run the application. Your project will now display all the text using your new custom font!

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and a main class extending `Game` that sets the first screen.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.
