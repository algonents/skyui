plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

group = "com.algonents.skyui"
version = "0.2.0-SNAPSHOT"

dependencies {
    implementation(libs.kotlinx.html)
}
