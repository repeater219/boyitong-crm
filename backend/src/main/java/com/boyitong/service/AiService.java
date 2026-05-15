package com.boyitong.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.boyitong.entity.Customer;
import com.boyitong.repository.CustomerRepository;
import com.boyitong.repository.ContractRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
public class AiService {

    private final CustomerRepository customerRepository;
    private final ContractRepository contractRepository;

    private String apiKey;
    private String apiUrl = "https://api.openai-proxy.org/v1/chat/completions";
    private String model = "deepseek-chat";

    public AiService(CustomerRepository customerRepository, ContractRepository contractRepository) {
        this.customerRepository = customerRepository;
        this.contractRepository = contractRepository;
    }

    @PostConstruct
    public void init() {
        // Read from .env file
        try {
            java.nio.file.Path envPath = java.nio.file.Paths.get(
                "/Users/Zhuanz/Desktop/internship/backend/.env");
            if (java.nio.file.Files.exists(envPath)) {
                for (String line : java.nio.file.Files.readAllLines(envPath)) {
                    if (line.startsWith("DEEPSEEK_API_KEY=")) apiKey = line.substring("DEEPSEEK_API_KEY=".length());
                    if (line.startsWith("DEEPSEEK_API_URL=")) apiUrl = line.substring("DEEPSEEK_API_URL=".length());
                    if (line.startsWith("DEEPSEEK_MODEL=")) model = line.substring("DEEPSEEK_MODEL=".length());
                }
            }
        } catch (Exception ignored) {}
        System.out.println("=== AI Init: key=" + (apiKey != null ? apiKey.substring(0, 10) + "..." : "NULL") + " url=" + apiUrl);
    }

    public String chat(String userMessage) {
        // Build context with system data
        long customerCount = customerRepository.count();
        long contractCount = contractRepository.count();
        List<Customer> recentCustomers = customerRepository.findAll().stream()
                .sorted(Comparator.comparing(Customer::getId).reversed())
                .limit(5).toList();

        StringBuilder context = new StringBuilder();
        context.append("你是一个CRM系统的AI销售助手。当前系统数据概况：\n");
        context.append("- 客户总数：").append(customerCount).append("\n");
        context.append("- 合同总数：").append(contractCount).append("\n");
        context.append("- 最近添加的客户：\n");
        for (Customer c : recentCustomers) {
            context.append("  • #").append(c.getId()).append(" ")
                    .append(c.getCity() != null ? c.getCity() : "未知城市").append(" ")
                    .append(c.getCategory() != null ? c.getCategory() : "").append(" ")
                    .append(c.getSize() != null ? c.getSize() + "m²" : "").append("\n");
        }
        context.append("\n请根据以上数据回答用户的问题。如果用户问的数据不在上述信息中，请如实告知并建议用户使用系统的筛选功能。请用中文回答。");

        return callDeepSeek(context.toString(), userMessage);
    }

    public String generateFollowUp(String keywords) {
        String prompt = "你是一个销售助理。请根据以下关键词生成一段专业的客户跟进记录（100字以内）：" + keywords
                + "\n要求：语气专业、包含具体行动、适合作为CRM系统的跟进记录。";
        return callDeepSeek("你是一个专业的销售跟进助手。", prompt);
    }

    private String callDeepSeek(String systemPrompt, String userMessage) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", systemPrompt));
            messages.add(Map.of("role", "user", "content", userMessage));
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1000);

            String json = mapper.writeValueAsString(requestBody);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Map result = mapper.readValue(response.body(), Map.class);
                List<Map> choices = (List<Map>) result.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map message = (Map) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
            return "抱歉，AI 暂时无法回复，请稍后重试。";
        } catch (Exception e) {
            return "AI 服务调用失败: " + e.getMessage();
        }
    }

    // Package-private setters for testing
    void setApiKeyForTest(String key) { this.apiKey = key; }
    void setApiUrlForTest(String url) { this.apiUrl = url; }
    void setModelForTest(String m) { this.model = m; }
}