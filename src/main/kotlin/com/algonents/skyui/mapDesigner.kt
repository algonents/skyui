package com.algonents.skyui

import com.algonents.skyui.SkyUI
import com.algonents.skyui.controls.mapDesigner
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.style

fun TagConsumer<Appendable>.mapDesignerDiv(ui: SkyUI, searchEndpoint: String? = null) {
    div {
        attributes["id"] = "map-designer"
        style = "width:100%; height:100%;"
        mapDesigner(
            ui,
            width = "100%",
            height = "100%",
            lat = 46.00,
            lng = 6.0,
            zoom = 5,
            searchEndpoint = searchEndpoint,
        )
    }
    div {
        id = "air-toolbar-controls"
        attributes["hx-swap-oob"] = "innerHTML"
    }
}