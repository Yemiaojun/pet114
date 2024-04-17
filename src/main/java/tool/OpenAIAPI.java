package tool;

import cn.hutool.core.convert.ConvertException;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import utils.JsonUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class OpenAIAPI {


    private static final String API_KEY = "fastgpt-FN7MFGTXhfY97fqiDepooQroCATDVImx3fq2A1eMlk4XgV042rFVDWFaIYNYIA6w";

    private static final String CHAT_BASE_URL = "https://ietowdbn.cloud.sealos.io/api";

    private static final String CHAT_URL = "/v1/chat/completions";


    public String chat(String txt) {
        Map<String, Object> paramMap = new HashMap<>();
        List<Map<String, String>> dataList = new ArrayList<>();
        dataList.add(new HashMap<String, String>(){{
            put("role", "user");
            put("content", txt);
        }});
        paramMap.put("messages", dataList);
        JSONObject message = null;

        RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory() {{
            setProxy(new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890)));
            setConnectTimeout(180000);
            setReadTimeout(180000);
        }});

        try {
            String body = HttpRequest.post(String.format(CHAT_BASE_URL, CHAT_URL))
                    .header("Authorization", API_KEY)
                    .header("Content-Type", "application/json")
                    .body(JsonUtils.toJson(paramMap))
                    .execute()
                    .body();
            JSONObject jsonObject = JSONUtil.parseObj(body);
            JSONArray choices = jsonObject.getJSONArray("choices");
            JSONObject result = choices.get(0, JSONObject.class, Boolean.TRUE);
            message = result.getJSONObject("message");
        } catch (Exception e) {
            return e.getMessage();
        }
        return message.getStr("content");
    }

    public static void main(String[] args) {
        System.out.println(chat("你好"));
    }
}
