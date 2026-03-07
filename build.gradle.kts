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
version = "0.1.0"

dependencies {
    implementation(libs.kotlinx.html)
}
