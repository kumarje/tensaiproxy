package com.hexaware.genai;


import com.fasterxml.jackson.databind.util.JSONPObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestTemplateAdapter;

import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ProxyController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyController.class);

    private RestTemplate restTemplate = null;

    private static String TENSAI_URL = "https://gwdocs-dev.azurewebsites.net/chat_stream/general";
    @GetMapping(value = "/prompt", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*")
    public String askTensai(@RequestParam("question") String question) throws Exception{
        LOGGER.info(" Prompt {}", question);
        HashMap<String, String> responseMap = new HashMap<>();
        responseMap.put("question", question);
        responseMap.put("answer", "Sample response from tensai");

        restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        List<Map> historyList = new ArrayList<Map>();

        HashMap historyMap = new HashMap();
        historyMap.put("user", question);
        historyList.add(historyMap);

        HashMap<String, String> overrideMap = new HashMap<>();
        overrideMap.put("semantic_ranker", "true");
        overrideMap.put("retrieval_mode", "hybrid");
        overrideMap.put("semantic_captions", "false");
        overrideMap.put("top","5");
        overrideMap.put("suggest_followup_questions", "false");
        overrideMap.put("use_oid_security_filter", "false");
        overrideMap.put( "use_groups_security_filter", "false");
        HashMap requestMap = new HashMap();
        requestMap.put("history", historyList);
        requestMap.put("approach", "rrr");
        requestMap.put("overrides", overrideMap);

        HttpEntity<Map> request = new HttpEntity<>(requestMap, httpHeaders);

        String responseFromTensai = restTemplate.postForObject(TENSAI_URL, request, String.class);

        LOGGER.info("Response from Tensai {}", responseFromTensai);

        return responseFromTensai;
    }
}
