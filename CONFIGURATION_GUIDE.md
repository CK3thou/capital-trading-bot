# Trading Bot Configuration Guide

## Overview
This guide explains how to configure the RSI Trading Bot for your Capital.com account.

## Step 1: Obtain API Credentials

### Capital.com Account Setup
1. Log in to your [Capital.com Trading Account](https://capital.com/trading/platform)
2. Go to **Settings** → **API Integrations**
3. Click **Generate new key**
4. Fill in the form:
   - **Label**: "Trading Bot" (or your preferred name)
   - **Custom password**: Create a strong password (saved securely!)
   - **Expiration**: Set to your preferred duration
   - **2FA Code**: Enter your 2FA code
5. Save the following information:
   - **API Key**: `MDQRi4km1ODEzGeF` (example)
   - **Custom Password**: Your custom password
   - **Email**: Your login email

## Step 2: Update Application Configuration

### Option A: application.yml (Development)

Edit `src/main/resources/application.yml`:

```yaml
server:
  port: 8080
  servlet:
    context-path: /

capital.api:
  domain.URL: https://api-capital.backend-capital.com
capital.api.demo:
  domain.URL: https://demo-api-capital.backend-capital.com
  
capital.user:
  login: chishimba.kabwe@gmail.com
  password: -Cullbanga#1
  apiKey: MDQRi4km1ODEzGeF
  env: DEMO  # or LIVE for live trading

settings:
  epics: ["GOLD", "SILVER", "OIL_CRUDE"]  # Markets to monitor
  resolutions: ["HOUR"]
  RSIPeriod: 14  # Standard RSI period
```

### Option B: Environment Variables (Recommended for Production)

```bash
# Set environment variables instead of using application.yml
export CAPITAL_USER_LOGIN=your-email@example.com
export CAPITAL_USER_PASSWORD=your-custom-password
export CAPITAL_USER_APIKEY=MDQRi4km1ODEzGeF
export CAPITAL_USER_ENV=DEMO
```

## Step 3: Configure Trading Parameters

### Market Configuration

Update the `settings.epics` array with markets you want to trade:

```yaml
settings:
  epics:
    - "GOLD"          # Commodities
    - "SILVER"        # Commodities
    - "OIL_CRUDE"     # Commodities
    - "EURUSD"        # Forex
    - "BTCUSD"        # Cryptocurrencies
    - "AAPL"          # Stocks
    - "NVDA"          # Stocks
    - "SP500"         # Indices
```

### RSI Settings

```yaml
settings:
  RSIPeriod: 14  # Standard period for RSI calculation
  # Higher values = slower, smoother RSI
  # Lower values = faster, more responsive RSI
```

### Position Sizing

```yaml
settings:
  quantityPresents: 5     # Default position size as % of balance
  sltpPresents: 2         # Stop loss/Take profit as % of balance
  quantityManual: 5       # Manual trading position size
  sltpUsingPips: true     # Use pips for SL/TP calculation
  sltpPipsFromLowestPrice: 50  # Pips distance for SL/TP
```

## Step 4: Account Selection

### Demo Account Setup
- Best for testing and learning
- No real money required
- Same API as live account
- Good for strategy validation

### Live Account Setup
⚠️ **Use with caution!**
- Real money at risk
- Start with small position sizes
- Monitor closely during first trades
- Consider paper trading first

To use demo: Set `env: DEMO` in config
To use live: Set `env: LIVE` in config

## Step 5: Running the Application

### Build
```bash
cd /path/to/capital-trading-bot
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

Or run the JAR directly:
```bash
mvn clean package
java -jar target/api-java-samples-0.0.1-SNAPSHOT.jar
```

### Access Web Interface
Open your browser and navigate to:
```
http://localhost:8080
```

## Web Interface Usage

### Step-by-Step Trading Setup

1. **Account Selection**
   - Select account type (Demo/Live)
   - Choose specific account with available balance

2. **Market Category**
   - Commodities: Gold, Silver, Oil, Gas
   - Forex: Currency pairs (EURUSD, GBPUSD, etc.)
   - Indices: Stock market indices
   - Stocks/Shares: Individual company stocks
   - ETFs: Exchange-traded funds

3. **Market Selection**
   - Choose specific instrument from category
   - View current bid/ask prices
   - See 24-hour price change

4. **Monitor RSI**
   - Current RSI value displayed
   - Signal badge shows trading opportunity
   - See current position status

5. **Start Trading**
   - Click "Start Trading Bot" to enable automated trading
   - Bot will execute trades based on RSI every 30 minutes
   - Monitor signals in real-time

## Trading Signals Explained

### Signal Types

| Signal | RSI Level | Meaning | Action |
|--------|-----------|---------|--------|
| 🟢 **OVERSOLD** | RSI < 30 | Price unusually low | Potential BUY |
| 🔴 **OVERBOUGHT** | RSI > 70 | Price unusually high | Potential SELL |
| 🟡 **NEUTRAL** | RSI 30-70 | No extreme condition | Wait for signal |
| ⬆️ **INCREASING** | RSI rising | Momentum building up | Consider BUY |
| ⬇️ **DECREASING** | RSI falling | Momentum fading | Consider SELL |

### Trading Rules

```
1. Calculate RSI every 30 minutes
2. Compare with previous RSI
3. If RSI increasing (uptrend):
   → Close any SELL position
   → Open BUY position
4. If RSI decreasing (downtrend):
   → Close any BUY position
   → Open SELL position
5. If RSI unchanged:
   → Maintain current position
```

## Monitoring & Logs

### Log Levels
- **INFO**: General trading events, position opens/closes
- **WARN**: Missing data, delayed updates
- **ERROR**: Failed API calls, trading errors

View logs:
```bash
# Real-time logs
tail -f logs/trading-bot.log

# Filter for trading signals
grep "SIGNAL\|RSI" logs/trading-bot.log
```

### Key Events to Monitor

```
[INFO] Starting RSI trading strategy check at 2025-12-03T10:00:00
[INFO] Current RSI for GOLD: 65.23
[INFO] BUY signal for GOLD - RSI increased from 45.12 to 65.23
[INFO] Opening BUY position for GOLD
[INFO] Position created, deal reference: o_12345...
```

## Troubleshooting

### Issue: "No market selected"
**Solution**: Select market from dropdown before starting bot

### Issue: "Authentication failed"
**Solution**: 
- Verify API key is correct
- Check custom password matches
- Confirm 2FA is enabled
- Ensure account isn't suspended

### Issue: "Insufficient data for RSI calculation"
**Solution**:
- Wait for enough price candles to accumulate
- Use longer market history
- Check market is actively trading

### Issue: "Position creation failed"
**Solution**:
- Check account has sufficient balance
- Verify market supports CFD trading
- Ensure position size isn't too large
- Check trading hours for market

### Issue: No trades executing
**Solution**:
- Check if bot is running (green status)
- Verify RSI is changing (not static)
- Check logs for errors
- Ensure 30-minute scheduler has passed

## Risk Management

### Position Sizing
```
Conservative: quantityPresents = 1-2 (1-2% of balance)
Moderate: quantityPresents = 3-5 (3-5% of balance)
Aggressive: quantityPresents = 10+ (>10% of balance)
```

### Stop Loss / Take Profit
```
Automatic SL/TP based on:
- sltpPresents: Percentage of position
- sltpPipsFromLowestPrice: Fixed pip distance
```

### Daily Limits
- Demo account: Maximum £100,000 balance
- Demo deposits: Capped at specific amounts
- Live trading: Subject to your account terms

## Performance Optimization

### For Better Results

1. **Use Appropriate Timeframes**
   - Short-term (1H-4H): Faster signals, more trades
   - Medium-term (1D): Fewer signals, higher accuracy
   - Long-term (1W): Trend confirmation

2. **Combine with Other Indicators**
   - Use support/resistance levels
   - Monitor volume trends
   - Check market sentiment

3. **Adjust RSI Period**
   - Shorter period (9-12): More responsive
   - Standard period (14): Balanced
   - Longer period (21+): Smoother trends

4. **Market Selection**
   - Choose liquid markets (tight bid/ask)
   - Monitor trading hours
   - Avoid news events
   - Consider correlation

## Backup & Recovery

### Save Your Configuration
```bash
# Backup application.yml
cp src/main/resources/application.yml \
   src/main/resources/application.yml.backup
```

### Trading History
- All trades are logged in Capital.com account
- Position history preserved
- Performance metrics tracked

## Next Steps

1. **Test with Demo**
   - Start with demo account
   - Test different markets
   - Monitor performance
   - Adjust parameters

2. **Review Results**
   - Check win/loss ratio
   - Analyze RSI effectiveness
   - Identify patterns
   - Optimize settings

3. **Move to Live (Optional)**
   - Start with small amounts
   - Use position size limits
   - Monitor closely
   - Scale gradually

## Support Resources

- **Capital.com API Docs**: https://open-api.capital.com/
- **Technical Analysis**: https://www.investopedia.com/terms/r/rsi.asp
- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **GitHub Repository**: https://github.com/CK3thou/capital-trading-bot

---

**Last Updated**: December 2025
