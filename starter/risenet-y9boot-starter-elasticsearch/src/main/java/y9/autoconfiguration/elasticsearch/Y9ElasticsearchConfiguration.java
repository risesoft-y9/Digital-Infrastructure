package y9.autoconfiguration.elasticsearch;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = {"${y9.feature.elasticsearch.packagesToScanRepositoryPublic}"},
    includeFilters = {
        @ComponentScan.Filter(classes = ElasticsearchRepository.class, type = FilterType.ASSIGNABLE_TYPE)})
public class Y9ElasticsearchConfiguration {
	@Autowired
	private Environment env;
	
	public Y9ElasticsearchConfiguration() {
		System.setProperty("es.set.netty.runtime.available.processors", "false");
	}

	@Override
	@Bean
	@Primary
	public ClientConfiguration clientConfiguration() {
		String uri = env.getProperty("spring.elasticsearch.rest.uris");
		String username = env.getProperty("spring.elasticsearch.rest.username");
        String password = env.getProperty("spring.elasticsearch.rest.password");
		return ClientConfiguration.builder()
			.connectedTo(uri)
			.withConnectTimeout(Duration.ofMinutes(5))
			.withSocketTimeout(Duration.ofMinutes(5))
			.withBasicAuth(username, password)
			.build();
	}
}
