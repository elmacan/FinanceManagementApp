package com.example.FinanceManagementApp.currency;

import com.example.FinanceManagementApp.exception.ApiException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "TCMB service unavailable");
        }
    }
}