package y9.autoconfiguration.elasticsearch;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.configuration.feature.elasticsearch.Y9ElasticProperties;

@Configuration
@EnableConfigurationProperties(Y9ElasticProperties.class)
@EnableElasticsearchRepositories(basePackages = {"${y9.feature.elasticsearch.packagesToScanRepositoryPublic}"},
    includeFilters = {
        @ComponentScan.Filter(classes = ElasticsearchRepository.class, type = FilterType.ASSIGNABLE_TYPE)})
@Slf4j
public class Y9ElasticsearchConfiguration {

    public Y9ElasticsearchConfiguration() {
        LOGGER.info("Y9ElasticsearchConfiguration init. ");
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }
}
