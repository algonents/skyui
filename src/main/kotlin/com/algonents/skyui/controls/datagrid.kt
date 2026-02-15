package com.algonents.skyui.controls

import com.algonents.skyui.SkyUI
import kotlinx.html.*


enum class ColumnType {
    STRING,
    BOOLEAN,
    FLOAT,
    DOUBLE,
    INT,
    UUID
}

data class ColumnDefinition<T>(
    val name: String,
    val field: String,
    val width: String,
    val type: ColumnType,
    val editable: Boolean = true,
    val accessor: (T) -> Any?,
    val editor: (FlowContent.(String) -> Unit)? = null
)

private fun tbodyId(baseUrl: String) = "datagrid-body-${baseUrl.trimStart('/').replace('/', '-')}"

fun <T> FlowContent.datagrid(
    ui: SkyUI,
    columns: List<ColumnDefinition<T>>,
    data: List<T>,
    baseUrl: String,
    rowId: (T) -> Any
) {
    val bodyId = tbodyId(baseUrl)
    ui.requireCss(this, "/static/components/datagrid/datagrid.css")
    ui.requireJs(this, "/static/components/datagrid/datagrid.js")
    div("skyui-datagrid") {
        div("datagrid-toolbar") {
            div("datagrid-search") {
                i("fa-solid fa-magnifying-glass")
                input {
                    type = InputType.text
                    name = "search"
                    placeholder = "Search..."
                    attributes["hx-get"] = "$baseUrl/search"
                    attributes["hx-trigger"] = "keyup changed delay:300ms"
                    attributes["hx-target"] = "#$bodyId"
                    attributes["hx-swap"] = "innerHTML"
                }
            }
            button(classes = "datagrid-add-btn") {
                attributes["hx-get"] = "$baseUrl/new"
                attributes["hx-target"] = "#$bodyId"
                attributes["hx-swap"] = "afterbegin"
                i("fa-solid fa-plus")
                +" ADD ROW"
            }
        }
        table {
            colGroup {
                columns.forEach { column ->
                    col {
                        style = "width: ${column.width};"
                    }
                }
                col { style = "width: 80px;" }
            }
            thead {
                tr {
                    columns.forEach { column ->
                        th {
                            +column.name
                            span("col-resize-handle") {}
                        }
                    }
                    th { +"Actions" }
                }
            }
            tbody {
                id = bodyId
                data.forEach { item ->
                    readOnlyRow(columns, item, baseUrl, rowId(item))
                }
            }
        }
    }
}

fun <T> TBODY.readOnlyRow(
    columns: List<ColumnDefinition<T>>,
    item: T,
    baseUrl: String,
    rowId: Any
) {
    tr {
        id = "row-$rowId"
        attributes["hx-on::load"] = "var tb=this.closest('tbody');if(tb&&tb.dataset.selectedRow===this.id&&!this.querySelector('td input'))this.classList.add('selected')"
        columns.forEach { column ->
            td(cellClass(column)) { +displayValue(column, item) }
        }
        td("datagrid-actions") {
            button(classes = "action-btn") {
                attributes["hx-get"] = "$baseUrl/$rowId/edit"
                attributes["hx-target"] = "closest tr"
                attributes["hx-swap"] = "outerHTML"
                i("fa-light fa-pen")
            }
            button(classes = "action-btn delete-btn") {
                attributes["hx-delete"] = "$baseUrl/$rowId"
                attributes["hx-target"] = "closest tr"
                attributes["hx-swap"] = "outerHTML swap:200ms"
                attributes["hx-confirm"] = "Are you sure you want to delete this row?"
                i("fa-light fa-trash")
            }
        }
    }
}

fun <T> TagConsumer<*>.readOnlyRow(
    columns: List<ColumnDefinition<T>>,
    item: T,
    baseUrl: String,
    rowId: Any
) {
    tr {
        id = "row-$rowId"
        attributes["hx-on::load"] = "var tb=this.closest('tbody');if(tb&&tb.dataset.selectedRow===this.id&&!this.querySelector('td input'))this.classList.add('selected')"
        columns.forEach { column ->
            td(cellClass(column)) { +displayValue(column, item) }
        }
        td("datagrid-actions") {
            button(classes = "action-btn") {
                attributes["hx-get"] = "$baseUrl/$rowId/edit"
                attributes["hx-target"] = "closest tr"
                attributes["hx-swap"] = "outerHTML"
                i("fa-solid fa-pen")
            }
            button(classes = "action-btn delete-btn") {
                attributes["hx-delete"] = "$baseUrl/$rowId"
                attributes["hx-target"] = "closest tr"
                attributes["hx-swap"] = "outerHTML swap:200ms"
                attributes["hx-confirm"] = "Are you sure you want to delete this row?"
                i("fa-solid fa-trash")
            }
        }
    }
}

fun <T> TagConsumer<*>.editableRow(
    columns: List<ColumnDefinition<T>>,
    item: T,
    baseUrl: String,
    rowId: Any
) {
    tr {
        id = "row-$rowId"
        attributes["hx-on::load"] = "var tb=this.closest('tbody');if(tb&&tb.dataset.selectedRow===this.id&&!this.querySelector('td input'))this.classList.add('selected')"
        columns.forEach { column ->
            td(cellClass(column)) {
                if (column.editable) {
                    val editorFn = column.editor
                    if (editorFn != null) {
                        editorFn(this, displayValue(column, item))
                    } else {
                        input {
                            name = column.field
                            value = displayValue(column, item)
                            type = when (column.type) {
                                ColumnType.FLOAT, ColumnType.DOUBLE -> InputType.number
                                ColumnType.INT -> InputType.number
                                else -> InputType.text
                            }
                            if (column.type == ColumnType.FLOAT || column.type == ColumnType.DOUBLE) {
                                attributes["step"] = "any"
                            }
                        }
                    }
                } else {
                    +displayValue(column, item)
                }
            }
        }
        td("datagrid-actions") {
            button(classes = "action-btn save-btn") {
                attributes["hx-put"] = "$baseUrl/$rowId"
                attributes["hx-target"] = "closest tr"
                attributes["hx-swap"] = "outerHTML"
                attributes["hx-include"] = "closest tr"
                i("fa-solid fa-floppy-disk")
            }
            button(classes = "action-btn cancel-btn") {
                attributes["hx-get"] = "$baseUrl/$rowId"
                attributes["hx-target"] = "closest tr"
                attributes["hx-swap"] = "outerHTML"
                i("fa-solid fa-xmark")
            }
        }
    }
}

fun <T> TagConsumer<*>.newEditableRow(
    columns: List<ColumnDefinition<T>>,
    item: T,
    baseUrl: String,
) {
    tr {
        columns.forEach { column ->
            td(cellClass(column)) {
                if (column.editable) {
                    val editorFn = column.editor
                    if (editorFn != null) {
                        editorFn(this, displayValue(column, item))
                    } else {
                        input {
                            name = column.field
                            value = displayValue(column, item)
                            type = when (column.type) {
                                ColumnType.FLOAT, ColumnType.DOUBLE -> InputType.number
                                ColumnType.INT -> InputType.number
                                else -> InputType.text
                            }
                            if (column.type == ColumnType.FLOAT || column.type == ColumnType.DOUBLE) {
                                attributes["step"] = "any"
                            }
                        }
                    }
                } else {
                    +displayValue(column, item)
                }
            }
        }
        td("datagrid-actions") {
            button(classes = "action-btn save-btn") {
                attributes["hx-post"] = baseUrl
                attributes["hx-target"] = "closest tr"
                attributes["hx-swap"] = "outerHTML"
                attributes["hx-include"] = "closest tr"
                i("fa-solid fa-floppy-disk")
            }
            button(classes = "action-btn cancel-btn") {
                attributes["hx-on:click"] = "this.closest('tr').remove()"
                i("fa-solid fa-xmark")
            }
        }
    }
}

private fun <T> cellClass(column: ColumnDefinition<T>): String? = when (column.type) {
    ColumnType.UUID -> "uuid"
    else -> null
}

fun <T> displayValue(column: ColumnDefinition<T>, item: T): String {
    val rawValue = column.accessor(item)
    return when (column.type) {
        ColumnType.STRING -> rawValue?.toString() ?: ""
        ColumnType.BOOLEAN -> if (rawValue as? Boolean == true) "✔" else "✘"
        ColumnType.FLOAT -> String.format("%.2f", rawValue as? Float ?: 0f)
        ColumnType.DOUBLE -> String.format("%.2f", rawValue as? Double ?: 0.0)
        ColumnType.INT -> (rawValue as? Int)?.toString() ?: "0"
        ColumnType.UUID -> rawValue?.toString() ?: ""
    }
}
