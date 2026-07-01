plugins {
    id("me.modmuss50.mod-publish-plugin")
}

tasks.register("clean") {
    delete(layout.buildDirectory)
}