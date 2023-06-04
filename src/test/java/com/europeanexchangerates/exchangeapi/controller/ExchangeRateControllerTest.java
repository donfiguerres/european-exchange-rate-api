package com.europeanexchangerates.exchangeapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.europeanexchangerates.exchangeapi.dto.CurrencyAverageRate;
import com.europeanexchangerates.exchangeapi.dto.CurrencyConversion;
import com.europeanexchangerates.exchangeapi.dto.CurrencyHighestRate;
import com.europeanexchangerates.exchangeapi.dto.ExchangeRate;
import com.europeanexchangerates.exchangeapi.exception.InvalidDateRangeException;
import com.europeanexchangerates.exchangeapi.service.ExchangeRateService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ExchangeRateController.class)
public class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService service;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void testGetRatesEndpoint() throws Exception {
        Optional<ExchangeRate> exchangeRate = Optional.of(new ExchangeRate(
                new HashMap<>() {
                    {
                        put("USD", BigDecimal.valueOf(1.0744));
                        put("JPY", BigDecimal.valueOf(150.01));
                        put("BGN", BigDecimal.valueOf(1.9558));
                        put("GBP", BigDecimal.valueOf(0.86365));
                    }
                }));

        when(service.getRatesForDate(any())).thenReturn(exchangeRate);

        mockMvc.perform(get("/rates")
                .param("date", "2023-05-30")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rates").exists());
    }

    @Test
    public void testGetRatesEndpoint_NoContent() throws Exception {
        LocalDate date = LocalDate.parse("2022-01-01");

        when(service.getRatesForDate(date)).thenReturn(Optional.empty());

        mockMvc.perform(get("/rates")
                .param("date", "2022-01-01"))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    public void testConvertCurrencyEndpoint() throws Exception {
        Optional<CurrencyConversion> conversion = Optional.of(new CurrencyConversion(
                "USD",
                "EUR",
                BigDecimal.valueOf(100),
                LocalDate.of(2023, 5, 30),
                BigDecimal.valueOf(50)));

        when(service.convertCurrency(any(), any(), any(), any())).thenReturn(conversion);

        mockMvc.perform(get("/convert")
                .param("date", "2023-05-30")
                .param("source", "USD")
                .param("target", "EUR")
                .param("amount", "100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.convertedAmount").exists());
    }

    @Test
    public void testConvertCurrencyEndpoint_NoContent() throws Exception {
        LocalDate date = LocalDate.parse("2022-01-01");
        when(service.convertCurrency(date, "USD", "EUR", BigDecimal.valueOf(100))).thenReturn(Optional.empty());

        mockMvc.perform(get("/convert")
                .param("date", date.toString())
                .param("source", "USD")
                .param("target", "EUR")
                .param("amount", "100"))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    public void testGetHighestRateEndpoint() throws Exception {
        CurrencyHighestRate highestRate = new CurrencyHighestRate(
                "USD",
                LocalDate.of(2023, 5, 1),
                LocalDate.of(2023, 5, 30),
                BigDecimal.valueOf(1.0744));

        when(service.getHighestRate(any(), any(), any())).thenReturn(highestRate);

        mockMvc.perform(get("/highest_rate")
                .param("start_date", "2023-05-01")
                .param("end_date", "2023-05-30")
                .param("currency", "USD")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.highestRate").exists());
    }

    @Test
    public void testGetHighestRateEndpoint_NoContent() throws Exception {
        LocalDate startDate = LocalDate.parse("2022-01-01");
        LocalDate endDate = LocalDate.parse("2022-01-31");
        when(service.getHighestRate(startDate, endDate, "USD")).thenReturn(null);

        mockMvc.perform(get("/highest_rate")
                .param("start_date", startDate.toString())
                .param("end_date", endDate.toString())
                .param("currency", "USD"))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    public void getHighestRate_endDateBeforeStartDate_returnsUnprocessableEntity() throws Exception {
        when(service.getHighestRate(
            LocalDate.of(2023, 5, 30),
            LocalDate.of(2023, 5, 29),
            "USD")).thenThrow(new InvalidDateRangeException("End date cannot be before start date."));
        mockMvc.perform(get("/highest_rate")
                .param("start_date", "2023-05-30")
                .param("end_date", "2023-05-29")
                .param("currency", "USD"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testGetAverageRateEndpoint() throws Exception {
        CurrencyAverageRate averageRate = new CurrencyAverageRate(
                "USD",
                LocalDate.of(2023, 5, 1),
                LocalDate.of(2023, 5, 30),
                BigDecimal.valueOf(1.081));

        when(service.getAverageRate(any(), any(), any())).thenReturn(averageRate);

        mockMvc.perform(get("/average_rate")
                .param("start_date", "2023-05-01")
                .param("end_date", "2023-05-30")
                .param("currency", "USD")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRate").exists());
    }

    @Test
    public void testGetAverageRateEndpoint_NoContent() throws Exception {
        LocalDate startDate = LocalDate.parse("2022-01-01");
        LocalDate endDate = LocalDate.parse("2022-01-31");
        when(service.getAverageRate(startDate, endDate, "USD")).thenReturn(null);

        mockMvc.perform(get("/average_rate")
                .param("start_date", startDate.toString())
                .param("end_date", endDate.toString())
                .param("currency", "USD"))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    public void getAverageRate_endDateBeforeStartDate_returnsUnprocessableEntity() throws Exception {
        when(service.getAverageRate(
            LocalDate.of(2023, 5, 30),
            LocalDate.of(2023, 5, 29),
            "USD")).thenThrow(new InvalidDateRangeException("End date cannot be before start date."));
        mockMvc.perform(get("/average_rate")
                .param("start_date", "2023-05-30")
                .param("end_date", "2023-05-29")
                .param("currency", "USD"))
                .andExpect(status().isUnprocessableEntity());
    }
}
