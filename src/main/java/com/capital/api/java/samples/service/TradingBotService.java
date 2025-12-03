package com.capital.api.java.samples.service;

import com.capital.api.java.samples.common.CapitalUserConfig;
import com.capital.api.java.samples.rest.dto.prices.PriceSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Trading Bot Service - Handles RSI-based trading signals
 * Fetches prices every 30 minutes and executes trades based on RSI comparison
 */
@Slf4j
@Service
public class TradingBotService {

    @Autowired
    private Core core;

    @Autowired
    private RSI rsiCalculator;

    @Autowired
    private CapitalUserConfig userConfig;

    // Store previous RSI values for comparison
    private Map<String, Double> previousRsiValues = new HashMap<>();
    
    // Store current positions to prevent duplicate trades
    private Map<String, String> currentPositions = new HashMap<>();

    /**
     * Scheduled task that runs every 30 minutes to check RSI and execute trades
     */
    @Scheduled(fixedDelay = 1800000, initialDelay = 60000) // 30 minutes
    public void executeRsiTradingStrategy() {
        try {
            log.info("Starting RSI trading strategy check at {}", LocalDateTime.now());
            
            String selectedMarket = userConfig.getSelectedMarketEpic();
            if (selectedMarket == null || selectedMarket.isEmpty()) {
                log.warn("No market selected for trading");
                return;
            }

            // Calculate current RSI
            double currentRsi = calculateCurrentRSI(selectedMarket);
            log.info("Current RSI for {}: {}", selectedMarket, currentRsi);

            // Get previous RSI value
            Double previousRsi = previousRsiValues.getOrDefault(selectedMarket, Double.NaN);
            
            if (Double.isNaN(previousRsi)) {
                log.info("First calculation, storing RSI value for next comparison");
                previousRsiValues.put(selectedMarket, currentRsi);
                return;
            }

            // Execute trading logic based on RSI comparison
            executeTradingLogic(selectedMarket, previousRsi, currentRsi);

            // Store current RSI for next comparison
            previousRsiValues.put(selectedMarket, currentRsi);

        } catch (Exception e) {
            log.error("Error in RSI trading strategy execution", e);
        }
    }

    /**
     * Calculate RSI for a given market
     */
    private double calculateCurrentRSI(String epic) throws Exception {
        // Get historical prices for RSI calculation
        // Using 14-period RSI (standard)
        java.util.List<PriceSnapshot> prices = core.getHistoricalPrices(epic, "MINUTE_5", 100);
        
        if (prices == null || prices.size() < 14) {
            log.warn("Insufficient data for RSI calculation for {}", epic);
            return Double.NaN;
        }

        // Calculate RSI using closing prices
        double[] closePrices = new double[prices.size()];
        for (int i = 0; i < prices.size(); i++) {
            closePrices[i] = prices.get(i).getSnapshotTime(); // Using bid as close price
        }

        return rsiCalculator.calculateRSI(closePrices, 14);
    }

    /**
     * Execute trading logic based on RSI comparison
     * - If new RSI > old RSI: BUY
     * - If new RSI < old RSI: SELL
     * - If new RSI == old RSI: MAINTAIN
     */
    private void executeTradingLogic(String epic, double previousRsi, double currentRsi) {
        try {
            String positionKey = epic;
            String currentPosition = currentPositions.get(positionKey);

            if (currentRsi > previousRsi) {
                // RSI is increasing - BUY signal
                log.info("BUY signal for {} - RSI increased from {} to {}", epic, previousRsi, currentRsi);
                
                if (currentPosition == null || !currentPosition.equals("BUY")) {
                    // Close existing SELL position if any
                    if (currentPosition != null && currentPosition.equals("SELL")) {
                        closePosition(epic);
                    }
                    // Open BUY position
                    openPosition(epic, "BUY");
                    currentPositions.put(positionKey, "BUY");
                }
            } else if (currentRsi < previousRsi) {
                // RSI is decreasing - SELL signal
                log.info("SELL signal for {} - RSI decreased from {} to {}", epic, previousRsi, currentRsi);
                
                if (currentPosition == null || !currentPosition.equals("SELL")) {
                    // Close existing BUY position if any
                    if (currentPosition != null && currentPosition.equals("BUY")) {
                        closePosition(epic);
                    }
                    // Open SELL position
                    openPosition(epic, "SELL");
                    currentPositions.put(positionKey, "SELL");
                }
            } else {
                // RSI is same - maintain current position
                log.info("No RSI change for {} - RSI: {}", epic, currentRsi);
            }
        } catch (Exception e) {
            log.error("Error executing trading logic for {}", epic, e);
        }
    }

    /**
     * Open a position with the specified direction
     */
    private void openPosition(String epic, String direction) {
        try {
            log.info("Opening {} position for {}", direction, epic);
            // Implementation will use Core class to open positions
            // Placeholder - actual implementation in Core class
        } catch (Exception e) {
            log.error("Error opening {} position for {}", direction, epic, e);
        }
    }

    /**
     * Close an existing position
     */
    private void closePosition(String epic) {
        try {
            log.info("Closing position for {}", epic);
            // Implementation will use Core class to close positions
            // Placeholder - actual implementation in Core class
        } catch (Exception e) {
            log.error("Error closing position for {}", epic, e);
        }
    }

    /**
     * Get the current RSI for a market
     */
    public double getRSIValue(String epic) throws Exception {
        return calculateCurrentRSI(epic);
    }

    /**
     * Set the selected market for trading
     */
    public void setSelectedMarket(String epic) {
        userConfig.setSelectedMarketEpic(epic);
        log.info("Selected market for trading: {}", epic);
    }

    /**
     * Get current position for a market
     */
    public String getCurrentPosition(String epic) {
        return currentPositions.getOrDefault(epic, "NONE");
    }
}
