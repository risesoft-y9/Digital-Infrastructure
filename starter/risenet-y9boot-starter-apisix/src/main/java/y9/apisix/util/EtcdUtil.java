package y9.apisix.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.protobuf.ByteString;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.api.RangeResponse;
import com.ibm.etcd.client.EtcdClient;
import com.ibm.etcd.client.KvStoreClient;
import com.ibm.etcd.client.kv.KvClient;

public class EtcdUtil {

    public static List<String> getKeyPrefix(String keyPrefix, String endpoints) throws IOException {
        try (KvStoreClient kvStoreClient = EtcdClient.forEndpoints(endpoints).withPlainText().build()) {
            List<String> response = new ArrayList<>();
            KvClient kvClient = kvStoreClient.getKvClient();
            RangeResponse rangeResponse = kvClient.get(ByteString.copyFromUtf8(keyPrefix)).asPrefix().keysOnly().sync();
            List<KeyValue> list = rangeResponse.getKvsList();
            response = list.stream().map(kv -> {
                String key = kv.getKey().toStringUtf8();
                return key.substring(key.lastIndexOf("/") + 1);
            }).collect(Collectors.toList());
            return response;
        }
    }
}
