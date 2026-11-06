package net.risesoft.y9.elasticsearch;

import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

/**
 * 统一将业务索引名转换为当前环境的 Elasticsearch 索引名。
 */
public class Y9IndexNameResolver {

    private final String prefix;

    public Y9IndexNameResolver(String prefix) {
        this.prefix = prefix == null ? "" : prefix;
        if (!this.prefix.isEmpty() && !this.prefix.matches("[a-z0-9][a-z0-9._-]*")) {
            throw new IllegalArgumentException(
                "y9.feature.elasticsearch.index-prefix must start with a lowercase letter or digit "
                    + "and contain only lowercase letters, digits, '.', '_' or '-'");
        }
    }

    public String resolve(String indexName) {
        return prefix + indexName;
    }

    public IndexCoordinates coordinates(String... indexNames) {
        String[] names = new String[indexNames.length];
        for (int i = 0; i < indexNames.length; i++) {
            names[i] = resolve(indexNames[i]);
        }
        return IndexCoordinates.of(names);
    }
}
