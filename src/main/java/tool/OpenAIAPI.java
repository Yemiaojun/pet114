package tool;

import cn.hutool.core.convert.ConvertException;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.hutool.json.JSONUtil;
import lombok.experimental.UtilityClass;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import utils.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class OpenAIAPI {


    private static final String API_KEY = "";

    private static final String CHAT_BASE_URL = "https://ietowdbn.cloud.sealos.io/api";

    private static final String CHAT_URL = "/v1/chat/completions";


    public String chat(String txt,String id) {
        RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory() {{
            setProxy(new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890)));
            setConnectTimeout(180000);
            setReadTimeout(180000);
        }});
        ObjectMapper objectMapper = new ObjectMapper(); // Jackson的对象映射器

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", API_KEY);

        // 构建请求体
        Map<String, Object> body = new HashMap<>();
        body.put("chatId", id);
        body.put("stream", false);
        body.put("detail", false);
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", txt);
        messages.add(message);
        body.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            // Send request and receive response
            ResponseEntity<String> response = restTemplate.exchange(CHAT_BASE_URL + CHAT_URL, HttpMethod.POST, entity, String.class);
            System.out.println("Response Body: " + response.getBody()); // Print the full response body
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            // Extract the content from the message in the choices array
            JsonNode choicesArray = jsonResponse.path("choices");
            System.out.println("Choices Array: " + choicesArray); // Print the choices array
            if (choicesArray.isArray() && choicesArray.has(0)) {
                JsonNode firstChoice = choicesArray.get(0);
                JsonNode messageNode = firstChoice.path("message");
                System.out.println("Message Node: " + messageNode); // Print the message node
                String content = messageNode.path("content").asText(); // Correctly extract the content string
                return content;
            }
            return "No content found in response.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        System.out.println(chat("请问如何治疗宠物肠炎","11a"));
    }
}
