package com.algonents.skyui.controls

import com.algonents.skyui.SkyUI
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.i

fun FlowContent.accordion(
    ui: SkyUI,
    buttonLabel: String,
    links: List<Pair<String, String>>,
    iconClass: String = "fa fa-caret-down",
    accordionClass: String = "accordion-container",
    buttonClass: String = "accordion-btn"
) {
    ui.requireCss(this, "/static/components/accordion/accordion.css")
    ui.requireJs(this, "/static/components/accordion/accordion.js")
    button(classes = buttonClass) {
        +buttonLabel
        i(classes = iconClass) {}
    }
    div(classes = accordionClass) {
        links.forEach { (href, label) ->
            a(href = href) {
                attributes["hx-get"] = href
                attributes["hx-target"] = "#main-panel"
                +label
            }
        }
    }
}
