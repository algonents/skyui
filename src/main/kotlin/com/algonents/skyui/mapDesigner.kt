package com.algonents.skyui

import com.algonents.skyui.SkyUI
import com.algonents.skyui.controls.mapDesigner
import kotlinx.html.TagConsumer
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.style

fun TagConsumer<Appendable>.mapDesignerDiv(ui: SkyUI) {
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
        )
    }
    div {
        id = "air-toolbar-controls"
        attributes["hx-swap-oob"] = "innerHTML"
        button {
            style = "margin-right:5px;"
            classes = setOf("small-button")
            i(classes = "fa-solid fa-layer-plus") {

            }

        }
        div{
            button {
                classes = setOf("small-button")
                +"+"
                i(classes = "fa-solid fa-draw-polygon") {

                }
            }
        }

    }
}