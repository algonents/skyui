package com.algonents.skyui.controls

import com.algonents.skyui.SkyUI
import kotlinx.html.FlowContent
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.i
import kotlinx.html.id

data class TabDefinition(
    val id: String,
    val label: String,
    val url: String,
    val icon: String? = null,
)

fun FlowContent.tabContainer(
    ui: SkyUI,
    tabs: List<TabDefinition>,
    activeTab: String? = null,
    containerId: String = "skyui-tabs",
) {
    ui.requireCss(this, "/static/components/tabcontainer/tabs.css")
    ui.requireJs(this, "/static/components/tabcontainer/tabs.js")
    val activeId = activeTab ?: tabs.firstOrNull()?.id
    div("skyui-tab-container") {
        id = containerId
        div("skyui-tab-bar") {
            tabs.forEach { tab ->
                val isActive = tab.id == activeId
                button(classes = if (isActive) "skyui-tab active" else "skyui-tab") {
                    attributes["data-tab-id"] = tab.id
                    attributes["data-url"] = tab.url
                    attributes["data-target"] = "#${containerId}-panel"
                    if (isActive) {
                        attributes["data-autoload"] = "true"
                    }
                    tab.icon?.let { iconClass ->
                        i(classes = iconClass) {}
                        +" "
                    }
                    +tab.label
                }
            }
        }
        div("skyui-tab-panel") {
            id = "${containerId}-panel"
        }
    }
}
