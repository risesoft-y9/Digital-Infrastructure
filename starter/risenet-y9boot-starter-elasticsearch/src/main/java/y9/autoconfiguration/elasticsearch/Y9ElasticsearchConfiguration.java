package y9.autoconfiguration.elasticsearch;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = { "${y9.feature.elasticsearch.packagesToScanRepositoryPublic}" }, includeFilters = { @ComponentScan.Filter(classes = ElasticsearchRepository.class, type = FilterType.ASSIGNABLE_TYPE) })
public class Y9ElasticsearchConfiguration {
	public Y9ElasticsearchConfiguration() {
		System.setProperty("es.set.netty.runtime.available.processors", "false");
	}
}
