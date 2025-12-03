# RSI Trading Bot - Capital.com API Integration

An automated trading bot that uses RSI (Relative Strength Index) technical indicators to execute buy/sell positions on Capital.com markets.

## Features

### ✨ Core Functionality

1. **RSI-Based Trading Algorithm**
   - Calculates 14-period RSI every 30 minutes
   - Compares previous RSI with current RSI
   - Executes trades based on RSI trend direction
   - Automatically maintains positions

2. **Interactive Web Interface**
   - Account type selection (Demo/Live)
   - Account switching
   - Market category filtering (Commodities, Forex, Indices, ETF, Shares)
   - Real-time market selection with live prices
   - Live RSI calculation and signal display
   - Interactive price charts with technical indicators

3. **Trading Signals**
   - **BUY Signal**: RSI increases (trend reversal from lower values)
   - **SELL Signal**: RSI decreases (trend reversal from higher values)
   - **MAINTAIN**: RSI unchanged - keep current position
   - RSI Level Indicators:
     - **Overbought** (RSI > 70): Potential SELL opportunity
     - **Oversold** (RSI < 30): Potential BUY opportunity
     - **Neutral** (RSI 30-70): No strong signal

4. **Advanced Charting**
   - Real-time price charts with 4-hour candles
   - High/Low/Close price visualization
   - RSI indicator chart
   - Link to Capital.com's advanced charting platform

5. **Portfolio Management**
   - Switch between multiple accounts
   - Select different market categories and instruments
   - View current positions per market
   - Track RSI values per instrument

## Installation & Setup

### Prerequisites

- Java 21+
- Maven 3.9+
- Capital.com Trading Account
- API credentials with 2FA enabled

### Configuration

1. **Update `application.yml`**:
   ```yaml
   server:
     port: 8080
   
   capital.user:
     login: your-email@example.com
     password: your-custom-password
     apiKey: your-api-key
     env: DEMO  # or LIVE
   ```

2. **Replace credentials with your actual Capital.com API credentials**:
   - Login email
   - API key custom password
   - API key (obtained from Settings > API Integrations)

### Build & Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Trading Bot REST API

#### Account Management
- `GET /api/trading-bot/account-types` - Get available account types
- `GET /api/trading-bot/accounts?accountType={type}` - Get accounts by type
- `POST /api/trading-bot/switch-account?accountId={id}` - Switch active account

#### Market Data
- `GET /api/trading-bot/market-categories` - Get all market categories
- `GET /api/trading-bot/markets?categoryId={id}` - Get markets in category
- `GET /api/trading-bot/rsi/{epic}` - Get RSI value for market
- `GET /api/trading-bot/chart/{epic}?resolution={res}&max={num}` - Get chart data

#### Trading Control
- `POST /api/trading-bot/start-trading?epic={epic}` - Start trading on specific market
- `GET /api/trading-bot/` - Access web interface

## Web Interface

### Layout

**Left Panel - Trading Controls**
- Account type selector
- Account selector
- Market category selector
- Market selector
- RSI display
- Current position indicator
- Trading signal badge
- Start/Stop bot buttons

**Right Panel - Price Chart**
- Real-time candlestick chart
- High/Low/Close price visualization
- 4-hour candle resolution

**Bottom Panels**
- RSI Indicator Gauge
- Market Information (Bid/Ask/Change/Status)

### Usage

1. **Select Account**
   - Choose Demo or Live account type
   - Select specific account

2. **Choose Market**
   - Select market category (Commodities, Forex, etc.)
   - Select specific market from category
   - View real-time RSI and chart

3. **Monitor Trading**
   - Watch RSI changes every 30 minutes
   - See trading signals (Buy/Sell/Hold)
   - View current position status

4. **Advanced Charting**
   - Click "View Advanced Chart" to open Capital.com charting platform
   - Analyze multiple instruments simultaneously

## Trading Logic

### RSI Calculation

The bot uses the standard 14-period RSI formula:
```
RSI = 100 - (100 / (1 + RS))
where RS = Average Gain / Average Loss
```

### Trading Strategy

```
Every 30 minutes:
1. Calculate current RSI from latest price data
2. Compare with previous RSI value
3. If current RSI > previous RSI:
   - Close any SELL position
   - Open BUY position
4. If current RSI < previous RSI:
   - Close any BUY position
   - Open SELL position
5. If current RSI == previous RSI:
   - Maintain current position
   - No new trades
```

### Position Management

- **One position per market**: Prevents multiple conflicting positions
- **Automatic hedging**: Closes opposing positions before new trades
- **Risk management**: Uses default stop loss and take profit levels

## Architecture

### Service Components

#### TradingBotService
- Scheduled task that runs every 30 minutes
- RSI calculation and storage
- Trading signal generation
- Position management

#### TradingBotController
- REST API endpoints for UI
- Account and market data retrieval
- RSI and chart data APIs
- Trading control endpoints

#### RSI Service
- 14-period RSI calculation
- Historical price analysis
- Technical indicator computations

### Database/Storage

Currently uses in-memory storage:
- `previousRsiValues`: Map of RSI history by market
- `currentPositions`: Map of current positions by market

For production, consider:
- PostgreSQL/MySQL for persistence
- Redis for high-frequency data caching
- Time-series database for historical price data

## Security Considerations

⚠️ **Important**: The current implementation stores credentials in `application.yml`. For production:

1. **Environment Variables**:
   ```bash
   export CAPITAL_USER_LOGIN=your-email@example.com
   export CAPITAL_USER_PASSWORD=your-password
   export CAPITAL_USER_APIKEY=your-api-key
   ```

2. **Spring Cloud Config Server**
   - Centralized configuration management
   - Encryption at rest

3. **HashiCorp Vault**
   - Secure credential storage
   - Dynamic secrets

4. **AWS Secrets Manager / Azure Key Vault**
   - Cloud-native secret management

## Performance & Scaling

### Current Limitations
- Single-threaded RSI calculation
- In-memory position storage
- No persistence between restarts

### Improvements for Production

1. **Caching**
   - Cache market categories
   - Cache historical prices
   - Use Redis for session data

2. **Database**
   - Persist trading history
   - Store RSI calculations
   - Track performance metrics

3. **Async Processing**
   - Non-blocking API calls
   - Parallel market analysis
   - Queue-based order execution

4. **Monitoring & Alerting**
   - Prometheus metrics
   - Alert on failed trades
   - Performance dashboards

## Error Handling

The bot includes comprehensive error handling:
- API connection failures
- Invalid market data
- Missing price history
- Trading execution errors

All errors are logged and don't crash the application.

## Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn integration-test

# Coverage report
mvn jacoco:report
```

## Troubleshooting

### Bot not connecting
- Verify API credentials in `application.yml`
- Check Capital.com API status
- Ensure 2FA is enabled on account
- Check firewall/network connectivity

### No trading signals
- Ensure market is selected
- Verify 30-minute scheduler interval
- Check RSI calculation logs
- Confirm sufficient price history data

### Chart not loading
- Verify market epic is correct
- Check browser console for errors
- Ensure API server is responding
- Try different market category

## Future Enhancements

1. **Multiple Indicators**
   - Bollinger Bands
   - MACD
   - Moving Averages
   - Stochastic Oscillator

2. **Advanced Trading**
   - Stop loss / Take profit customization
   - Position sizing rules
   - Risk management controls
   - Portfolio hedging

3. **Backtesting**
   - Historical data analysis
   - Strategy optimization
   - Performance metrics
   - Win/loss ratio tracking

4. **Mobile App**
   - React Native application
   - Real-time push notifications
   - Portfolio mobile view

5. **Machine Learning**
   - Price prediction models
   - Pattern recognition
   - Adaptive strategy parameters

## Support & Documentation

- [Capital.com API Docs](https://open-api.capital.com/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Technical Analysis Guide](https://en.wikipedia.org/wiki/Relative_strength_index)

## License

MIT License - See LICENSE file for details

## Disclaimer

⚠️ **Trading Disclaimer**: This bot is for educational purposes. Cryptocurrency and trading involve significant risks. Past performance doesn't guarantee future results. Always do your own research and consider consulting with a financial advisor before trading.

---

**Last Updated**: December 2025
**Java Version**: 21 LTS
**Spring Boot Version**: 2.7.18
