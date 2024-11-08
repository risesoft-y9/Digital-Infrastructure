plugins {
    id("com.ly.smart-doc")
}

smartdoc {
    configFile = file("src/main/resources/smart-doc.json")
    include("net.risesoft*")
}