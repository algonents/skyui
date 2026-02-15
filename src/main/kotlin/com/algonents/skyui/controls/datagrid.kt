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

data class PageInfo(val page: Int, val pageSize: Int, val totalRows: Long)

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
private fun pagerId(baseUrl: String) = "datagrid-pager-${baseUrl.trimStart('/').replace('/', '-')}"

fun <T> FlowContent.datagrid(
    ui: SkyUI,
    columns: List<ColumnDefinition<T>>,
    data: List<T>,
    baseUrl: String,
    pageInfo: PageInfo? = null,
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
                    if (pageInfo != null) {
                        attributes["hx-swap"] = "none"
                        attributes["hx-vals"] = """{"pageSize":"${pageInfo.pageSize}"}"""
                    } else {
                        attributes["hx-target"] = "#$bodyId"
                        attributes["hx-swap"] = "innerHTML"
                    }
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
        pagerFooter(baseUrl, data.size, pageInfo)
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

private fun FlowContent.pagerFooter(baseUrl: String, dataSize: Int, pageInfo: PageInfo?) {
    val id = pagerId(baseUrl)
    div("datagrid-pager") {
        this.id = id
        pagerContent(baseUrl, dataSize, pageInfo)
    }
}

private fun FlowContent.pagerContent(baseUrl: String, dataSize: Int, pageInfo: PageInfo?) {
    if (pageInfo == null || pageInfo.totalRows <= pageInfo.pageSize) {
        val count = pageInfo?.totalRows?.toInt() ?: dataSize
        span { +"${formatNumber(count.toLong())} rows" }
    } else {
        val start = (pageInfo.page - 1) * pageInfo.pageSize + 1
        val end = minOf(pageInfo.page.toLong() * pageInfo.pageSize, pageInfo.totalRows)
        val lastPage = ((pageInfo.totalRows + pageInfo.pageSize - 1) / pageInfo.pageSize).toInt()
        span { +"Showing ${formatNumber(start.toLong())}\u2013${formatNumber(end)} of ${formatNumber(pageInfo.totalRows)} rows" }
        button(classes = "pager-btn") {
            if (pageInfo.page <= 1) {
                disabled = true
            } else {
                attributes["hx-get"] = "$baseUrl/page?page=${pageInfo.page - 1}&pageSize=${pageInfo.pageSize}"
                attributes["hx-swap"] = "none"
            }
            i("fa-solid fa-chevron-left")
        }
        button(classes = "pager-btn") {
            if (pageInfo.page >= lastPage) {
                disabled = true
            } else {
                attributes["hx-get"] = "$baseUrl/page?page=${pageInfo.page + 1}&pageSize=${pageInfo.pageSize}"
                attributes["hx-swap"] = "none"
            }
            i("fa-solid fa-chevron-right")
        }
    }
}

fun <T> TagConsumer<*>.datagridPage(
    columns: List<ColumnDefinition<T>>,
    data: List<T>,
    baseUrl: String,
    rowId: (T) -> Any,
    pageInfo: PageInfo,
) {
    val bodyId = tbodyId(baseUrl)
    // Wrap rows in <table><tbody> so the HTML parser preserves <tr> elements.
    // Both tbody and pager use hx-swap-oob; callers should use hx-swap="none".
    table {
        style = "display:none"
        tbody {
            attributes["hx-swap-oob"] = "innerHTML:#$bodyId"
            data.forEach { item ->
                readOnlyRow(columns, item, baseUrl, rowId(item))
            }
        }
    }
    div("datagrid-pager") {
        id = pagerId(baseUrl)
        attributes["hx-swap-oob"] = "outerHTML"
        pagerContent(baseUrl, data.size, pageInfo)
    }
}

private fun formatNumber(n: Long): String {
    return String.format("%,d", n)
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
