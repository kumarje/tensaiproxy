package com.hexaware.genai;


import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
public class ProxyController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyController.class);

    private RestTemplate restTemplate = null;

    private static String TENSAI_URL = "https://gwdocs-dev.azurewebsites.net/chat_stream/guidewire";
    @GetMapping(value = "/prompt", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*")
    public Map askTensai(@RequestParam("question") String question) throws Exception{
        LOGGER.info(" Prompt {}", question);
        HashMap responseMap = new HashMap<>();
        responseMap.put("question", question);




        restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        List<Map> historyList = new ArrayList<Map>();

        HashMap historyMap = new HashMap();
        historyMap.put("user", question);
        historyList.add(historyMap);

        HashMap overrideMap = new HashMap<>();
        overrideMap.put("semantic_ranker", true);
        overrideMap.put("retrieval_mode", "hybrid");
        overrideMap.put("semantic_captions", false);
        overrideMap.put("top",5);
        overrideMap.put("suggest_followup_questions", false);
        overrideMap.put("use_oid_security_filter", false);
        overrideMap.put( "use_groups_security_filter", false);
        HashMap requestMap = new HashMap();
        requestMap.put("history", historyList);
        requestMap.put("approach", "rrr");
        requestMap.put("overrides", overrideMap);

        JSONObject jsonObject = new JSONObject(requestMap);
        String jsonStr = jsonObject.toString();
        LOGGER.info("Json {}", jsonStr);

        HttpEntity<String> request = new HttpEntity<>(jsonStr, httpHeaders);

        String responseFromTensai = restTemplate.postForObject(TENSAI_URL, request, String.class);

        String[] splitStringArray = responseFromTensai.split("\\n");
        List<String> responseArray = new ArrayList<>();

        int count =0;
        for(String token: splitStringArray){
            if(count != 0){
                LOGGER.info("Gen AI Response {}", token);
                responseArray.add(token);
            }

            count++;
        }
        responseMap.put("answer", responseArray);
        return responseMap;
    }
}
