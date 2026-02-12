package com.algonents.skyui.controls

import com.algonents.skyui.SkyUI
import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.input

fun FlowContent.latitudeInputWithValidation(ui: SkyUI, initialValue: String = "") {
    ui.requireCss(this, "/static/components/editors/editors.css")
    ui.requireJs(this, "/static/components/editors/inputmask.js")
    div("latitude-wrapper") {
        input(InputType.text) {
            name = "latitude_dms"
            value = initialValue
            placeholder = "__°__'__\"_"
            classes = setOf("latitude-dms")
            attributes["data-inputmask-alias"] = "latitudeDMS"
            attributes["hx-post"] = "/validate/latitude"
            attributes["hx-trigger"] = "input changed delay:300ms"
            attributes["hx-target"] = "next .validation-message"
            attributes["hx-swap"] = "outerHTML"
        }

        // Initial empty validation message placeholder
        div("validation-message") { }
    }
}
