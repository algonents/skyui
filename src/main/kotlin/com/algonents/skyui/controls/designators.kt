package com.algonents.skyui.controls

import com.algonents.skyui.SkyUI
import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.input
import kotlinx.html.style

fun FlowContent.waypointDesignator(ui: SkyUI, inputName: String = "designator", validationUrl: String) {
    ui.requireCss(this, "/static/components/editors/editors.css")
    ui.requireJs(this, "/static/components/editors/waypoint-designator.js")
    div("waypoint-designator") {
        style = "display:flex;flex-direction:column;"
        input {
            name = inputName
            attributes["hx-put"] = validationUrl
            attributes["hx-trigger"] = "input changed delay:300ms, blur"
            attributes["hx-target"] = "next .waypoint-designator-validation-result"
            attributes["hx-swap"] = "innerHTML"
            attributes["autocomplete"] = "off"
        }
        div("waypoint-designator-validation-result") {
            style="margin-top: 5px; max-width:120px"
        }
    }
}
