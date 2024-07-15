package net.risesoft;

import net.risesoft.util.Config;

public class IdCode {
    public static void init(String apiCode, String apiKey, String idCodeUrl, String mainCode, String analyzeUrl,
        String gotoUrl, String sampleUrl) {
        Config.API_CODE = apiCode;
        Config.API_KEY = apiKey;
        Config.IDCODE_URL = idCodeUrl;
        Config.MAIN_CODE = mainCode;
        Config.ANALYZE_URL = analyzeUrl;
        Config.GOTO_URL = gotoUrl;
        Config.SAMPLE_URL = sampleUrl;
    }
}
