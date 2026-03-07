package com.algonents.skyui.controls

import com.algonents.skyui.SkyUI
import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.div
import kotlinx.html.input

fun FlowContent.searchableSelect(
    ui: SkyUI,
    name: String,
    searchUrl: String,
    value: String = "",
    displayValue: String = "",
    placeholder: String = "Search...",
) {
    ui.requireCss(this, "/static/components/searchable-select/searchable-select.css")
    ui.requireJs(this, "/static/components/searchable-select/searchable-select.js")
    div("skyui-searchable-select") {
        input(classes = "skyui-searchable-select-input") {
            type = InputType.text
            this.name = "${name}_q"
            this.placeholder = placeholder
            this.value = displayValue
            attributes["hx-get"] = searchUrl
            attributes["hx-trigger"] = "input changed delay:300ms, focus"
            attributes["hx-target"] = "next .skyui-searchable-select-dropdown"
            attributes["autocomplete"] = "off"
        }
        input {
            type = InputType.hidden
            this.name = name
            this.value = value
        }
        div("skyui-searchable-select-dropdown") {}
    }
}
