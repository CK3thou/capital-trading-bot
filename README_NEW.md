# Capital.com RSI Trading Bot

This repository contains an advanced RSI trading bot that demonstrates the [Capital.com Open API](https://open-api.capital.com/) with an interactive web interface.

## 🎯 Features

### Core Trading Bot
- **RSI-Based Algorithm**: Automatic buy/sell signals based on 14-period RSI indicator
- **30-Minute Updates**: Scheduled price checks and trade execution
- **Smart Position Management**: Automatic hedging of opposing positions
- **Multi-Account Support**: Trade across demo and live accounts
- **Real-Time Charts**: Interactive price visualization with technical indicators

### Web Interface
- 🎨 **Modern Dashboard**: Intuitive controls and real-time data display
- 📊 **Live Charts**: Candlestick charts with OHLC data
- 📈 **RSI Indicator**: Visual RSI gauge with trading signals
- 🔄 **Account Switching**: Easy switching between demo/live accounts
- 📋 **Market Categories**: Filter markets by type (Commodities, Forex, Indices, ETF, Shares)
- 🎯 **Trading Signals**: Clear BUY/SELL/HOLD indicators
- 🔗 **Advanced Charting**: Links to Capital.com's professional charting platform

## 📋 Prerequisites

- **Java 21 LTS** (upgraded for latest features and security)
- **Maven 3.9+**
- **Capital.com Trading Account** (Demo or Live)
- **API Credentials** (with 2FA enabled)
- Docker (optional, for containerized deployment)

## 🚀 Quick Start

### 1. Get API Credentials

1. Log into [Capital.com Trading Platform](https://capital.com/trading/platform)
2. Go to **Settings > API Integrations**
3. Click **Generate new key**
4. Fill in details and note your:
   - **API Key**: `MDQRi4km1ODEzGeF`
   - **Custom Password**: Your secure password
   - **Email**: Your login email

### 2. Clone & Configure

```bash
git clone https://github.com/capital-com-sv/capital-trading-bot.git
cd capital-trading-bot
```

Edit `src/main/resources/application.yml`:

```yaml
server:
  port: 8080

capital.user:
  login: your-email@example.com
  password: your-custom-password
  apiKey: MDQRi4km1ODEzGeF
  env: DEMO  # or LIVE

settings:
  RSIPeriod: 14
  epics: ["GOLD", "SILVER", "OIL_CRUDE", "EURUSD"]
```

### 3. Build & Run

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

**Access the web interface**: Open browser to `http://localhost:8080`

## 📖 Documentation

| Document | Purpose |
|----------|---------|
| [TRADING_BOT_README.md](./TRADING_BOT_README.md) | Complete trading bot features & architecture |
| [CONFIGURATION_GUIDE.md](./CONFIGURATION_GUIDE.md) | Setup and configuration instructions |
| [API Documentation](https://open-api.capital.com/) | Capital.com API reference |

## 🎮 Using the Web Interface

### Step 1: Select Account
```
Account Type: Demo or Live
Account: Choose from available accounts
```

### Step 2: Choose Market
```
Market Category: Commodities, Forex, Indices, ETF, Shares
Market: Select specific instrument (GOLD, EURUSD, etc.)
```

### Step 3: Monitor Trading
```
RSI Value: Current 14-period RSI
Signal: BUY (Oversold), SELL (Overbought), NEUTRAL
Position: Current open position status
```

### Step 4: Start Trading
```
Click "Start Trading Bot" to enable automated trading
Bot runs every 30 minutes based on RSI signals
```

## 💡 Trading Strategy

### RSI Logic
```
RSI < 30  → Oversold (BUY Signal)
RSI > 70  → Overbought (SELL Signal)
RSI 30-70 → Neutral (Wait)
```

### Trading Rules
```
1. Calculate 14-period RSI every 30 minutes
2. Compare with previous RSI value
3. If RSI increasing → Open BUY position
4. If RSI decreasing → Open SELL position
5. If RSI unchanged → Maintain current position
```

## 📊 API Endpoints

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/trading-bot/account-types` | List account types |
| GET | `/api/trading-bot/accounts` | Get accounts by type |
| POST | `/api/trading-bot/switch-account` | Switch active account |
| GET | `/api/trading-bot/market-categories` | Get market categories |
| GET | `/api/trading-bot/markets` | Get markets in category |
| GET | `/api/trading-bot/rsi/{epic}` | Get RSI for market |
| GET | `/api/trading-bot/chart/{epic}` | Get chart data |
| POST | `/api/trading-bot/start-trading` | Start trading bot |

## 🔒 Security

⚠️ **Important**: Protect your API credentials!

### For Development
- Use `application.yml` for local testing only
- Never commit credentials to version control

### For Production
- Use **environment variables**:
  ```bash
  export CAPITAL_USER_LOGIN=your-email@example.com
  export CAPITAL_USER_PASSWORD=your-password
  export CAPITAL_USER_APIKEY=your-api-key
  ```
- Use **Spring Cloud Config** or **HashiCorp Vault**
- Use **AWS Secrets Manager** or **Azure Key Vault**

## 🛠 Building & Deployment

### Build JAR
```bash
mvn clean package
java -jar target/api-java-samples-0.0.1-SNAPSHOT.jar
```

### Docker Deployment
```bash
# Build image
docker build -t trading-bot:latest .

# Run container
docker run -e CAPITAL_USER_LOGIN=email \
           -e CAPITAL_USER_PASSWORD=pass \
           -e CAPITAL_USER_APIKEY=key \
           -p 8080:8080 trading-bot:latest
```

### Docker Compose
```bash
docker-compose up -d
```

Access at `http://localhost:8080`

## 📈 Performance & Monitoring

### Logs
```bash
# View live logs
tail -f logs/trading-bot.log

# Filter for trading events
grep "SIGNAL\|RSI\|Position" logs/trading-bot.log
```

### Key Metrics
- RSI values per market
- Win/loss ratio (from trading history)
- Position duration
- Profit/loss per trade

## ⚠️ Risk Management

### Conservative Settings
```yaml
quantityPresents: 2      # 2% of balance per trade
sltpPresents: 1          # 1% stop loss
```

### Moderate Settings
```yaml
quantityPresents: 5      # 5% of balance per trade
sltpPresents: 2          # 2% stop loss
```

### Rules to Follow
1. **Always use demo account first** to test strategy
2. **Never risk more than you can afford to lose**
3. **Start small** with 1-2% position sizing
4. **Monitor regularly** during first trades
5. **Have stop losses** on all positions

## 🤝 Contributing

Contributions welcome! Please:
1. Fork the repository
2. Create feature branch: `git checkout -b feature/AmazingFeature`
3. Commit changes: `git commit -m 'Add AmazingFeature'`
4. Push to branch: `git push origin feature/AmazingFeature`
5. Open Pull Request

## 📝 License

MIT License - See [LICENSE](./LICENSE) file

## 🆘 Support

- **Issues**: [GitHub Issues](https://github.com/CK3thou/capital-trading-bot/issues)
- **Email**: support@capital.com
- **Documentation**: [Capital.com API Docs](https://open-api.capital.com/)

## 📚 Additional Resources

- [Capital.com API Reference](https://open-api.capital.com/)
- [RSI Technical Analysis](https://www.investopedia.com/terms/r/rsi.asp)
- [Spring Boot Guides](https://spring.io/guides)
- [Maven Documentation](https://maven.apache.org/)

## 💬 Disclaimer

⚠️ **Important Disclaimer**:
- This bot is for **educational purposes only**
- **Trading involves substantial risk** of loss
- **Past performance** doesn't guarantee future results
- **Always do your own research** before trading
- **Consult a financial advisor** if needed
- Start with **demo accounts** and small amounts
- **Monitor your trades** carefully

---

**Last Updated**: December 2025  
**Java Version**: 21 LTS  
**Spring Boot**: 2.7.18  
**Status**: Production Ready  

Made with ❤️ for the trading community
