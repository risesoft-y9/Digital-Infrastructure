1、SpringBoot升级到2.3.0后,依赖的spring-data-elasticsearch版本是4.0.0,支持elasticsearch7.6以上
https://docs.spring.io/spring-data/elasticsearch/docs/4.0.0.RELEASE/reference/html/#new-features

2、elasticsearch7.x
  elasticsearch7.x需要jdk11以上，也可以使用elasticsearch包自带的jdk

3、elasticsearch的基础机安全认证,告别elasticsearch裸奔
  从6.8和7.1开始,x-pack默认集成到elasticsearch中，免费提供x-pack的部分功能，比较重要的是安全插件。
  配置
 切换到elasticsearch的目录，生成证书
 bin/elasticsearch-certutil cert -out config/elastic-certificates.p12 -pass ""
 在elasticsearch.yml添加以下配置
 http.cors.enabled: true
 http.cors.allow-origin: "*"
 http.cors.allow-headers: Authorization
 xpack.security.enabled: true
 xpack.security.transport.ssl.enabled: true
 xpack.security.transport.ssl.verification_mode: certificate
 xpack.security.transport.ssl.keystore.path: elastic-certificates.p12
 xpack.security.transport.ssl.truststore.path: elastic-certificates.p12