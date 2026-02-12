package com.algonents.skyui.controls.layout

import com.algonents.skyui.SkyUI
import kotlinx.html.FlowContent
import kotlinx.html.aside
import kotlinx.html.div
import kotlinx.html.id

fun FlowContent.sidebar(ui: SkyUI, sidebarContent: FlowContent.()->Unit) {
    ui.requireCss(this, "/static/components/sidebar/sidebar.css")
    ui.requireJs(this, "/static/components/sidebar/sidebar.js")
    aside() {
        div(){
            id ="sidebar"
            sidebarContent()
        }
        div(){
            id="floating-sidebar"
            sidebarContent()
        }
    }
}
