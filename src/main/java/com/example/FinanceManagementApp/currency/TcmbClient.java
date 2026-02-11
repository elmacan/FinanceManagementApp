package com.example.FinanceManagementApp.currency;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class TcmbClient {

    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper = new XmlMapper();

    private static final String url = "https://www.tcmb.gov.tr/kurlar/today.xml";

    public TcmbResponse getRates() {
        try {
            String xml = restTemplate.getForObject(url, String.class);
            return xmlMapper.readValue(xml, TcmbResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("TCMB fetch error", e);
        }
    }
}