package y9.autoconfiguration.elasticsearch;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableElasticsearchRepositories(basePackages = {"${y9.feature.elasticsearch.packagesToScanRepositoryPublic}"},
    includeFilters = {
        @ComponentScan.Filter(classes = ElasticsearchRepository.class, type = FilterType.ASSIGNABLE_TYPE)})
@Slf4j
public class Y9ElasticsearchConfiguration extends ElasticsearchConfiguration {

    @Autowired
    private Environment environment;

    public Y9ElasticsearchConfiguration() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    @Override
    @Bean
    @Primary
    public ClientConfiguration clientConfiguration() {
        String[] uri = environment.getProperty("spring.elasticsearch.rest.uris").split(":");
        String username = environment.getProperty("spring.elasticsearch.rest.username");
        String password = environment.getProperty("spring.elasticsearch.rest.password");
        LOGGER.info("Y9ElasticsearchConfiguration init. uris:{} username:{} password:{}", uri, username, password);
        return ClientConfiguration.builder().connectedTo(uri).withConnectTimeout(Duration.ofMinutes(5))
            .withSocketTimeout(Duration.ofMinutes(5)).withBasicAuth(username, password).build();
    }
}
