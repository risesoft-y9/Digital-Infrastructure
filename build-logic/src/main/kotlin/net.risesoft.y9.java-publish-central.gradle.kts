plugins {
    id("net.risesoft.y9.java-publish")
    id("tech.yanand.maven-central-publish")
}

mavenCentral {
    // 从 Sonatype 官方获取的 Publisher API 调用的 token，应为 Base64 编码后的 username:password
    authToken = findProperty("mavenCentralAuthToken").toString()
    // 上传是否应该自动发布。如果您希望手动执行此操作，请使用 'USER_MANAGED'。
    // 该属性是可选的，默认为 'AUTOMATIC'。
    publishingType = "AUTOMATIC"
    // 当发布类型为 'AUTOMATIC' 时，状态API获取 'PUBLISHING' 或 'PUBLISHED' 状态的最大等待时间
    // 或者当发布类型为 'USER_MANAGED' 时，获取 'VALIDATED' 状态。
    // 该属性是可选的，默认为60秒。
    maxWait = 60
}
