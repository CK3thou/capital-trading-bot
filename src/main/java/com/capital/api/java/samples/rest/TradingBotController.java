package com.capital.api.java.samples.rest;

import com.capital.api.java.samples.common.CapitalUserConfig;
import com.capital.api.java.samples.rest.dto.market.MarketNode;
import com.capital.api.java.samples.rest.dto.prices.PriceSnapshot;
import com.capital.api.java.samples.service.TradingBotService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller for Trading Bot UI
 * Handles account selection, market selection, RSI calculations, and chart data
 */
@Slf4j
@RestController
@RequestMapping("/api/trading-bot")
@CrossOrigin(origins = "*")
public class TradingBotController {

    @Autowired
    private ApiClient apiClient;

    @Autowired
    private TradingBotService tradingBotService;

    @Autowired
    private CapitalUserConfig userConfig;

    @Data
    public static class AccountTypeResponse {
        private List<String> accountTypes;

        public AccountTypeResponse(List<String> accountTypes) {
            this.accountTypes = accountTypes;
        }
    }

    @Data
    public static class AccountResponse {
        private String accountId;
        private String accountName;
        private String accountType;
        private String currency;
        private Double balance;
    }

    @Data
    public static class MarketCategoryResponse {
        private String id;
        private String name;
    }

    @Data
    public static class MarketResponse {
        private String epic;
        private String name;
        private String symbol;
        private String type;
        private Double bid;
        private Double offer;
        private Double percentageChange;
    }

    @Data
    public static class RSIResponse {
        private String epic;
        private Double rsiValue;
        private String signal;
        private String currentPosition;
    }

    @Data
    public static class ChartDataResponse {
        private String epic;
        private String name;
        private List<ChartPoint> prices;
        private Double currentRSI;
    }

    @Data
    public static class ChartPoint {
        private Long timestamp;
        private Double open;
        private Double high;
        private Double low;
        private Double close;
    }

    /**
     * Get available account types (Demo, Live)
     */
    @GetMapping("/account-types")
    public AccountTypeResponse getAccountTypes() {
        List<String> types = Arrays.asList("Demo", "Live");
        return new AccountTypeResponse(types);
    }

    /**
     * Get accounts for selected account type
     */
    @GetMapping("/accounts")
    public List<AccountResponse> getAccounts(@RequestParam String accountType) {
        try {
            List<AccountResponse> accounts = new ArrayList<>();
            
            // Get all accounts from session
            // This would retrieve from the API based on account type
            Map<String, Object> sessionInfo = apiClient.getSessionInfo();
            
            // Filter accounts by type (Demo/Live)
            // Implementation based on API response structure
            
            return accounts;
        } catch (Exception e) {
            log.error("Error fetching accounts", e);
            return new ArrayList<>();
        }
    }

    /**
     * Switch to selected account
     */
    @PostMapping("/switch-account")
    public Map<String, String> switchAccount(@RequestParam String accountId) {
        try {
            apiClient.switchAccount(accountId);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("accountId", accountId);
            return response;
        } catch (Exception e) {
            log.error("Error switching account", e);
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return response;
        }
    }

    /**
     * Get market categories (Commodities, Forex, Indices, ETF, Shares)
     */
    @GetMapping("/market-categories")
    public List<MarketCategoryResponse> getMarketCategories() {
        try {
            List<MarketCategoryResponse> categories = new ArrayList<>();
            
            // Get market navigation nodes
            Map<String, Object> navigation = apiClient.getMarketNavigation();
            
            // Map navigation nodes to categories
            // Expected categories: Commodities, Forex, Indices, ETF, Shares
            if (navigation != null && navigation.containsKey("nodes")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> nodes = (List<Map<String, Object>>) navigation.get("nodes");
                
                for (Map<String, Object> node : nodes) {
                    String nodeId = (String) node.get("id");
                    String name = (String) node.get("name");
                    
                    // Filter for relevant categories
                    if (isRelevantCategory(name)) {
                        categories.add(new MarketCategoryResponse(nodeId, name));
                    }
                }
            }
            
            return categories;
        } catch (Exception e) {
            log.error("Error fetching market categories", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get markets for selected category
     */
    @GetMapping("/markets")
    public List<MarketResponse> getMarkets(@RequestParam String categoryId) {
        try {
            List<MarketResponse> markets = new ArrayList<>();
            
            // Get markets for the category
            Map<String, Object> categoryData = apiClient.getMarketNavigationNode(categoryId);
            
            if (categoryData != null && categoryData.containsKey("nodes")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> nodes = (List<Map<String, Object>>) categoryData.get("nodes");
                
                for (Map<String, Object> node : nodes) {
                    String epic = (String) node.get("id");
                    String name = (String) node.get("name");
                    
                    // Get market details
                    Map<String, Object> marketDetails = apiClient.getMarketDetails(epic);
                    if (marketDetails != null) {
                        MarketResponse market = new MarketResponse();
                        market.setEpic(epic);
                        market.setName(name);
                        
                        @SuppressWarnings("unchecked")
                        Map<String, Object> snapshot = (Map<String, Object>) marketDetails.get("snapshot");
                        if (snapshot != null) {
                            market.setBid((Double) snapshot.get("bid"));
                            market.setOffer((Double) snapshot.get("offer"));
                            market.setPercentageChange((Double) snapshot.get("percentageChange"));
                        }
                        
                        markets.add(market);
                    }
                }
            }
            
            return markets;
        } catch (Exception e) {
            log.error("Error fetching markets for category {}", categoryId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Get RSI value for selected market
     */
    @GetMapping("/rsi/{epic}")
    public RSIResponse getRSIValue(@PathVariable String epic) {
        try {
            double rsiValue = tradingBotService.getRSIValue(epic);
            String signal = determineSignal(rsiValue);
            String currentPosition = tradingBotService.getCurrentPosition(epic);
            
            RSIResponse response = new RSIResponse();
            response.setEpic(epic);
            response.setRsiValue(rsiValue);
            response.setSignal(signal);
            response.setCurrentPosition(currentPosition);
            
            return response;
        } catch (Exception e) {
            log.error("Error calculating RSI for {}", epic, e);
            RSIResponse response = new RSIResponse();
            response.setEpic(epic);
            response.setRsiValue(Double.NaN);
            response.setSignal("ERROR");
            return response;
        }
    }

    /**
     * Get interactive chart data for selected market
     */
    @GetMapping("/chart/{epic}")
    public ChartDataResponse getChartData(@PathVariable String epic, 
                                         @RequestParam(defaultValue = "HOUR_4") String resolution,
                                         @RequestParam(defaultValue = "100") int max) {
        try {
            List<PriceSnapshot> prices = apiClient.getHistoricalPrices(epic, resolution, max);
            
            // Convert to chart format
            List<ChartPoint> chartData = new ArrayList<>();
            if (prices != null) {
                for (PriceSnapshot price : prices) {
                    ChartPoint point = new ChartPoint();
                    point.setTimestamp(price.getSnapshotTime());
                    point.setOpen(price.getOpenPrice());
                    point.setHigh(price.getHigh());
                    point.setLow(price.getLow());
                    point.setClose(price.getClosePrice());
                    chartData.add(point);
                }
            }
            
            ChartDataResponse response = new ChartDataResponse();
            response.setEpic(epic);
            response.setPrices(chartData);
            response.setCurrentRSI(tradingBotService.getRSIValue(epic));
            
            return response;
        } catch (Exception e) {
            log.error("Error fetching chart data for {}", epic, e);
            return new ChartDataResponse();
        }
    }

    /**
     * Start trading bot for selected market
     */
    @PostMapping("/start-trading")
    public Map<String, String> startTrading(@RequestParam String epic) {
        try {
            tradingBotService.setSelectedMarket(epic);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Trading bot started for " + epic);
            return response;
        } catch (Exception e) {
            log.error("Error starting trading", e);
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return response;
        }
    }

    /**
     * Determine trading signal based on RSI value
     * Traditional RSI interpretation:
     * - RSI > 70: Overbought (SELL signal)
     * - RSI < 30: Oversold (BUY signal)
     * - 30-70: Neutral
     */
    private String determineSignal(double rsiValue) {
        if (rsiValue > 70) return "OVERBOUGHT";
        if (rsiValue < 30) return "OVERSOLD";
        return "NEUTRAL";
    }

    /**
     * Check if category is relevant for trading
     */
    private boolean isRelevantCategory(String categoryName) {
        return categoryName != null && (
            categoryName.toLowerCase().contains("commodities") ||
            categoryName.toLowerCase().contains("forex") ||
            categoryName.toLowerCase().contains("indices") ||
            categoryName.toLowerCase().contains("etf") ||
            categoryName.toLowerCase().contains("shares") ||
            categoryName.toLowerCase().contains("index") ||
            categoryName.toLowerCase().contains("commodity") ||
            categoryName.toLowerCase().contains("currencies")
        );
    }
}
