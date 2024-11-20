group = "net.risesoft"

val versionCatalog = versionCatalogs.named("libs")
val y9version = versionCatalog.findVersion("y9-version")
if(y9version.isPresent) {
    version = y9version.get().displayName
} else {
    version = "v9.7.0-SNAPSHOT"
}