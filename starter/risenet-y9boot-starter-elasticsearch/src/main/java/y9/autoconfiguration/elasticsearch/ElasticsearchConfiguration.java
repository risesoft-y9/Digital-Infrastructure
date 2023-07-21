package y9.autoconfiguration.elasticsearch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.util.StringUtils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
@EnableElasticsearchRepositories(basePackages = {"${y9.feature.elasticsearch.packagesToScanRepositoryPublic}"}, includeFilters = {@ComponentScan.Filter(classes = ElasticsearchRepository.class, type = FilterType.ASSIGNABLE_TYPE)})
public class ElasticsearchConfiguration extends AbstractElasticsearchConfiguration {

    @Autowired
    private Environment environment;

    public ElasticsearchConfiguration() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    @Override
    @Bean
    @Primary
    public RestHighLevelClient elasticsearchClient() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        String uri = environment.getProperty("spring.elasticsearch.rest.uris");
        String username = environment.getProperty("spring.elasticsearch.rest.username");
        String password = environment.getProperty("spring.elasticsearch.rest.password");
        String connectionTimeout = environment.getProperty("spring.elasticsearch.rest.connection-timeout");
        String readTimeout = environment.getProperty("spring.elasticsearch.rest.read-timeout");
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo(uri).withConnectTimeout(Duration.ofMinutes(5)).withSocketTimeout(Duration.ofMinutes(5)).withBasicAuth(username, password).build();
            return RestClients.create(clientConfiguration).rest();
        } else {
            ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo(uri).withConnectTimeout(Duration.ofMinutes(5)).withSocketTimeout(Duration.ofMinutes(5)).build();
            return RestClients.create(clientConfiguration).rest();
        }
    }

    @Override
    protected Collection<String> getMappingBasePackages() {
        List<String> list = new ArrayList<>();
        String documentEntityPath = environment.getProperty("y9.feature.elasticsearch.packagesToScanEntityPublic");
        if (!StringUtils.hasText(documentEntityPath)) {
            documentEntityPath = "net.risesoft.nosql.elastic.entity";
        }
        list.add(documentEntityPath);
        return list;
    }
    
    @Bean
    public ElasticsearchClient client(){
    	String[] uri = environment.getProperty("spring.elasticsearch.rest.uris").split(":");
    	String username = environment.getProperty("spring.elasticsearch.rest.username");
        String password = environment.getProperty("spring.elasticsearch.rest.password");
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
        	BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

            RestClient httpClient = RestClient.builder(new HttpHost(uri[0], Integer.valueOf(uri[1])))
                .setHttpClientConfigCallback(hc -> hc.setDefaultCredentialsProvider(credentialsProvider))
                .build();

            ElasticsearchTransport transport = new RestClientTransport(httpClient, new JacksonJsonpMapper());
            return new ElasticsearchClient(transport);
        }else {
        	RestClient client = RestClient.builder(new HttpHost(uri[0], Integer.valueOf(uri[1]))).build();
            ElasticsearchTransport transport = new RestClientTransport(client,new JacksonJsonpMapper());
            return new ElasticsearchClient(transport);
        }
    }
}
