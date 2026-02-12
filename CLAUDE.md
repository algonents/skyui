# CLAUDE.md

## Project Overview

**SkyUI** is a framework-agnostic UI component library for aviation/ATM applications. It provides reusable server-side HTML components built with kotlinx-html DSL, along with companion CSS and JavaScript assets. Designed to work with any Kotlin web framework (Ktor, Spring Boot, etc.).

## Build

```bash
./gradlew build          # Build the library
```

## Tech Stack

- **Language:** Kotlin 2.2.20 (JVM)
- **Only dependency:** kotlinx-html 0.12.0

## Project Structure

```
src/main/kotlin/com/algonents/skyui/
├── SkyUI.kt                    # Asset registry & CSS/JS loader
├── mapDesigner.kt              # Map designer view
└── controls/                   # Reusable UI components
    ├── accordion.kt            # Collapsible accordion panels
    ├── airportDesignator.kt    # Airport ICAO designator input
    ├── datagrid.kt             # Data table with CRUD, search, inline editing
    ├── designators.kt          # Waypoint designator input
    ├── iconButton.kt           # Icon button component
    ├── latitudeinputmask.kt    # Latitude input with mask
    ├── mapdesigner.kt          # Leaflet map component
    └── layout/                 # Page layout components
        ├── layout.kt           # Main layout with sidebar
        ├── mainarea.kt         # Main content area
        └── sidebar.kt          # Collapsible sidebar

src/main/resources/static/
├── css/skyui-base.css          # Base styles
└── components/                 # Per-component CSS & JS
    ├── accordion/
    ├── datagrid/
    ├── editors/
    ├── map-designer/
    └── sidebar/
```

## Architecture

- **Framework-agnostic:** No web framework dependency. All components are pure kotlinx-html extension functions on `FlowContent` or `TagConsumer`.
- **Asset management:** `SkyUI` class tracks required CSS/JS files via `AssetRegistry`. Components call `ui.requireCss()` / `ui.requireJs()` to declare their assets.
- **HTMX-driven:** Components emit `hx-*` attributes for dynamic behavior (swap, trigger, target). The consuming application serves the HTMX library.
- **Static assets:** Served automatically via the classpath when the JAR is on the dependency path.

## Usage

SkyUI is consumed via Gradle composite build (`includeBuild`) or as a published artifact:

```kotlin
// settings.gradle.kts
includeBuild("../skyui")

// build.gradle.kts
dependencies {
    implementation("com.algonents.skyui:skyui:0.1.0")
}
```

## Code Conventions

- **UI components** are extension functions on `FlowContent` (e.g., `FlowContent.accordion(...)`)
- **File naming:** camelCase/lowercase for component files
- Commit messages prefixed with category (e.g., `prog:`, `fix:`)
