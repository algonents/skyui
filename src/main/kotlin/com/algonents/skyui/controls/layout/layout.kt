package com.algonents.skyui.controls.layout

import com.algonents.skyui.SkyUI
import kotlinx.html.BODY
import kotlinx.html.FlowContent
import kotlinx.html.div

fun BODY.layoutWithSidebar(ui: SkyUI, sidebarContent: FlowContent.() -> Unit, mainContainerContent: FlowContent.()->Unit){
    div("root-container"){
        sidebar(ui, sidebarContent)
        mainContainerContent()
    }
}
