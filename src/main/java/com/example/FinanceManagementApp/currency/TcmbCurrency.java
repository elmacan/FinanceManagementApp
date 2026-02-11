package com.example.FinanceManagementApp.currency;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.math.BigDecimal;
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TcmbCurrency {

    @JacksonXmlProperty(isAttribute = true, localName = "CurrencyCode")
    private String code;

    @JacksonXmlProperty(localName = "ForexBuying")
    private BigDecimal forexBuying;

    @JacksonXmlProperty(localName = "ForexSelling")
    private BigDecimal forexSelling;
}