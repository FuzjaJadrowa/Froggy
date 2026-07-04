plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.21.1-neoforge"

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("mod.version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
    constants["release"] = property("mod.id") != "template"
}