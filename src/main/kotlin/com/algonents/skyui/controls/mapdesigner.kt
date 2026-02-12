package com.algonents.skyui.controls

import com.algonents.skyui.SkyUI
import kotlinx.html.*

fun FlowContent.mapDesigner(
    ui: SkyUI,
    lat: Double,
    lng: Double,
    zoom: Int,
    width: String = "100%",
    height: String = "100%",
    waypointsJson: String? = null,
    src: String? = null,
    block: FlowContent.() -> Unit = {}
) {
    ui.requireJs(this, "/static/components/map-designer/map-designer.js")
    val attrs = mutableMapOf(
        "lat" to lat.toString(),
        "lng" to lng.toString(),
        "zoom" to zoom.toString(),
        "style" to "width: $width; height: $height; display: block;"
    )
    if (waypointsJson != null) attrs["data-waypoints"] = waypointsJson
    if (src != null) attrs["src"] = src

    val tag = object : HTMLTag(
        tagName = "map-designer",
        consumer = consumer,
        initialAttributes = attrs,
        inlineTag = false,
        emptyTag = false
    ), FlowContent {}

    tag.visit(block)
}
