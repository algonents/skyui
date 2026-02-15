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
    ├── tabcontainer.kt         # HTMX-driven tab container
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
    ├── tabcontainer/
    ├── editors/
    ├── map-designer/
    └── sidebar/
```

## Architecture

- **Framework-agnostic:** No web framework dependency. All components are pure kotlinx-html extension functions on `FlowContent` or `TagConsumer`.
- **Asset management:** `SkyUI` class tracks required CSS/JS files via `AssetRegistry`. Components call `ui.requireCss()` / `ui.requireJs()` to declare their assets.
- **HTMX-driven:** Components emit `hx-*` attributes for dynamic behavior (swap, trigger, target). The consuming application serves the HTMX library.
- **Component decoupling:** Components never listen for each other's events. Inter-component wiring (e.g., datagrid row selection driving tab content) is the responsibility of the consuming application via app-level JS.
- **Custom events:** Components may dispatch custom DOM events for external consumption. For example, the datagrid dispatches `skyui:row-selected` (with the record UUID in `event.detail.id`) when a row is clicked. These events are informational — no SkyUI component reacts to them internally.
- **Static assets:** Served automatically via the classpath when the JAR is on the dependency path.

### Datagrid

`FlowContent.datagrid(ui, columns, data, baseUrl, pageInfo?, rowId)` renders a searchable data table with inline editing, add/delete, and optional server-side pagination. `rowId` is the trailing lambda.

**Columns:** `ColumnDefinition<T>(name, field, width, type, editable?, accessor, editor?)` defines each column. `ColumnType` determines display formatting and input types.

**Pagination:** When `pageInfo: PageInfo(page, pageSize, totalRows)` is provided:
- The pager footer shows "Showing X–Y of Z rows" with prev/next buttons
- Search input includes `hx-vals` with pageSize so the server knows the page size
- When `pageInfo` is null, the footer just shows "N rows" (no buttons)

**HTMX page responses:** `TagConsumer<*>.datagridPage(columns, data, baseUrl, rowId, pageInfo)` renders a page of rows plus an updated pager for HTMX responses. It uses **dual OOB swaps** — rows go into the tbody via `hx-swap-oob="innerHTML:#bodyId"` (wrapped in a hidden `<table>` for correct HTML parsing), and the pager div uses `hx-swap-oob="outerHTML"`. Callers must use `hx-swap="none"` on triggering elements (pager buttons and search input when paginated) so the leftover empty `<table>` wrapper doesn't clobber the tbody.

**Why the hidden table wrapper:** HTMX delegates HTML parsing to the browser. If `<tr>` is the first element, the browser parses in a table context where `<div>` is invalid — the OOB pager div gets silently stripped. Wrapping rows in `<table style="display:none"><tbody>` gives `<tr>` elements a valid parsing context while keeping the pager `<div>` as a parseable sibling.

**Server-side route pattern** (for paginated datagrids):
- `GET /` — initial full page render with `datagrid(..., pageInfo = PageInfo(1, PAGE_SIZE, total))`
- `GET /page?page=N&pageSize=M` — returns `datagridPage(...)` with limit/offset query
- `GET /search?search=...&pageSize=M` — blank search returns page 1 via `datagridPage()`; non-empty search filters and returns results via `datagridPage()` with result count as totalRows

### Tab Container

`FlowContent.tabContainer(ui, tabs, activeTab?, containerId?)` renders a tab bar and a content panel. Each tab is defined by a `TabDefinition(id, label, url, icon?)`. Clicking a tab loads its `url` into the panel via `htmx.ajax()`. The first tab auto-loads by default (via `data-autoload`). Tab URLs are stored in `data-url` attributes and can be updated dynamically by app-level JS.

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
