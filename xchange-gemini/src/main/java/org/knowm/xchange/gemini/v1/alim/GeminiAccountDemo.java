package org.knowm.xchange.gemini.v1.alim;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.trade.*;
import org.knowm.xchange.gemini.v1.GeminiExchange;
import org.knowm.xchange.gemini.v1.GeminiUtils;
import org.knowm.xchange.gemini.v1.dto.account.GeminiBalancesResponse;
import org.knowm.xchange.gemini.v1.dto.marketdata.GeminiTicker;
import org.knowm.xchange.gemini.v1.service.GeminiAccountServiceRaw;
import org.knowm.xchange.gemini.v1.service.GeminiMarketDataServiceRaw;
import org.knowm.xchange.gemini.v1.service.GeminiTradeService;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Example showing the following:
 *
 * <ul>
 * <li>Connect to Bitstamp exchange with authentication</li>
 * <li>View account balance</li>
 * <li>Get the bitcoin deposit address</li>
 * <li>List unconfirmed deposits (raw interface only)</li>
 * <li>List recent withdrawals (raw interface only)</li>
 * <li>Withdraw a small amount of BTC</li>
 * </ul>
 */
public class GeminiAccountDemo {

    public static void main(String[] args) throws IOException {

        //Exchange geminiExchange = ExchangeFactory.INSTANCE.createExchange(GeminiExchange.class.getName());
        ExchangeSpecification exchangeSpecification = new ExchangeSpecification(GeminiExchange.class.getName());
        exchangeSpecification.setExchangeSpecificParametersItem("Use_Sandbox", true);
        exchangeSpecification.setApiKey("DBkNROJ7x5tmMdhEtNLM");
        exchangeSpecification.setSecretKey("MBVNDKpc19Kw32xQ9Vo1zhAPFT8");

        Exchange geminiExchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
        AccountService accountService = geminiExchange.getAccountService();
        TradeService tradeService = geminiExchange.getTradeService();
        MarketDataService marketDataService = geminiExchange.getMarketDataService();

        ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor();

       /* Runnable periodicTask = new Runnable() {
            public void run() {
                try {
                    geminiTrading(tradeService, (GeminiMarketDataServiceRaw) marketDataService);
                    System.err.println("$$$$$$$$$$$$$$$ Trading $$$$$$$$$$$$$");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        executor.scheduleAtFixedRate(periodicTask, 0, 10, TimeUnit.SECONDS);*/

        //geminiAccountInfo(accountService);
        //geminiBalances((GeminiAccountServiceRaw) accountService);
        //geminiTrade(tradeService);
        //geminiOpenOrders(tradeService);
        geminiPlaceLimitOrder(tradeService);
        //geminiPlaceMarketOrder(tradeService, marketDataService);
        //geminiTrading(tradeService, (GeminiMarketDataServiceRaw) marketDataService);
        // geminiOpenOrders(tradeService);
        // geminiTradeHistory(tradeService);
        getGeneralmarket(marketDataService);
        getGeminiMarket((GeminiMarketDataServiceRaw) marketDataService);
    }


    private static Ticker getGeneralmarket(MarketDataService marketDataService) throws IOException {

        Ticker ticker = marketDataService.getTicker(CurrencyPair.BTC_USD);

        System.out.println(ticker.toString());
        return ticker;
    }

    private static GeminiTicker getGeminiMarket(GeminiMarketDataServiceRaw marketDataService) throws IOException {

        GeminiTicker geminiTicker = marketDataService.getGeminiTicker("btcusd");
        System.err.println("geminiTicker>>>>" + geminiTicker);

        return geminiTicker;
    }

    private static void geminiAccountInfo(AccountService accountService) throws IOException {

        // Get the account information
        AccountInfo accountInfo = accountService.getAccountInfo();
        System.out.println("AccountInfo as String: " + accountInfo.toString());

       /* String depositAddress = accountService.requestDepositAddress(Currency.BTC);
        System.out.println("Deposit address: " + depositAddress);

        String withdrawResult = accountService.withdrawFunds(Currency.BTC, new BigDecimal(1).movePointLeft(4), "XXX");
        System.out.println("withdrawResult = " + withdrawResult);*/
    }

    private static void geminiBalances(GeminiAccountServiceRaw accountService) throws IOException {

        // Get the account information
        GeminiBalancesResponse[] geminiBalancesResponses = accountService.getGeminiAccountInfo();
        if (geminiBalancesResponses != null && geminiBalancesResponses.length > 0) {
            for (GeminiBalancesResponse geminiBalancesResponse : geminiBalancesResponses) {
                System.out.println("GeminiBalance: " + geminiBalancesResponse);
            }
        }

    }

    private static OpenOrders geminiOpenOrders(TradeService tradeService) throws IOException {
        OpenOrders openOrders = tradeService.getOpenOrders();
        System.out.println("Open Limit Order: " + openOrders.getOpenOrders());
        System.out.println("All Open Order: " + openOrders.getAllOpenOrders());
        return openOrders;
    }

    private static void geminiPlaceLimitOrder(TradeService tradeService) throws IOException {
        OpenOrders openOrders = tradeService.getOpenOrders();
        List<LimitOrder> limitOrderList = openOrders.getOpenOrders();
        List<Order> allOpenOrderList = openOrders.getAllOpenOrders();
        //System.err.println("limitOrderList>>>"+limitOrderList.toString());
        //System.err.println("allOpenOrderList>>>"+allOpenOrderList.toString());
        //openOrders.getHiddenOrders();
        Date myDate = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.DATE, 1);
        LimitOrder limitOrder = new LimitOrder(Order.OrderType.BID, new BigDecimal(0.5), CurrencyPair.BTC_USD, null, null, new BigDecimal(2501));
        if (openOrders != null && limitOrderList.size() == 0) {
            tradeService.placeLimitOrder(limitOrder);
            System.err.println("place limit order>>>>>");
        }
    }

    private static void geminiPlaceMarketOrder(TradeService tradeService, MarketDataService marketDataService) throws IOException {
        OpenOrders openOrders = tradeService.getOpenOrders();
        List<LimitOrder> limitOrderList = openOrders.getOpenOrders();
        List<Order> allOpenOrderList = openOrders.getAllOpenOrders();
        //System.err.println("limitOrderList>>>"+limitOrderList.toString());
        //System.err.println("allOpenOrderList>>>"+allOpenOrderList.toString());
        //openOrders.getHiddenOrders();
        Date myDate = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.DATE, 1);
        Ticker ticker = getGeneralmarket(marketDataService);
        MarketOrder marketOrder = new MarketOrder(Order.OrderType.BID, new BigDecimal(1), CurrencyPair.BTC_USD, null, cal.getTime(), ticker.getBid(), null, null, null);

        if (openOrders != null && limitOrderList.size() == 0) {
            tradeService.placeMarketOrder(marketOrder);
            System.err.println("place Market order>>>>>");
        }
    }

    private static void geminiTradeHistory(TradeService tradeService) throws IOException {
        Date myDate = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.DATE, -10);
        GeminiTradeService.GeminiTradeHistoryParams geminiTradeHistoryParams = new GeminiTradeService.GeminiTradeHistoryParams(CurrencyPair.BTC_USD, null, cal.getTime());

        UserTrades userTrades = tradeService.getTradeHistory(geminiTradeHistoryParams);
        System.err.println(userTrades.getUserTrades());
        System.err.println(userTrades.getTrades());

    }

    private static void geminiTrading(TradeService tradeService, GeminiMarketDataServiceRaw marketDataService) throws IOException {
        Date myDate = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.DATE, -10);

        GeminiTradeService.GeminiTradeHistoryParams geminiTradeHistoryParams = new GeminiTradeService.GeminiTradeHistoryParams(CurrencyPair.BTC_USD, null, cal.getTime());

        UserTrades userTrades = tradeService.getTradeHistory(geminiTradeHistoryParams);
        //System.err.println(userTrades);

        OpenOrders openOrders = tradeService.getOpenOrders();
        List<LimitOrder> limitOrderList = openOrders.getOpenOrders();
        List<Order> allOpenOrderList = openOrders.getAllOpenOrders();

        if (openOrders != null && limitOrderList.size() == 0) {
            GeminiTicker geminiTicker = marketDataService.getGeminiTicker("btcusd");

            if (userTrades != null && userTrades.getUserTrades() != null && userTrades.getUserTrades().size() > 0) {
                int tradeSize = userTrades.getUserTrades().size();
                UserTrade lastUserTrade = userTrades.getUserTrades().get(tradeSize - 1);


                if (lastUserTrade.getType() == Order.OrderType.ASK) {
                    BigDecimal lastUserTradePrice = lastUserTrade.getPrice();
                    BigDecimal lastUserTradeFee = lastUserTrade.getFeeAmount();
                    BigDecimal totalLastUserTradePrice = lastUserTradePrice.add(lastUserTradeFee);
                    BigDecimal tickerBidPrice = geminiTicker.getBid();
                    System.err.println("Buying>> totalLastUserTradePrice >>" + totalLastUserTradePrice);
                    System.err.println("Buying>> tickerBidPrice >>" + tickerBidPrice);
                    if (totalLastUserTradePrice.compareTo(tickerBidPrice) < 0) {
                        //BigDecimal limitOrderPrice = tickerBidPrice.divide(new BigDecimal(2));
                        LimitOrder limitOrder = new LimitOrder(Order.OrderType.BID, new BigDecimal(1), CurrencyPair.BTC_USD, null, null, tickerBidPrice);
                        tradeService.placeLimitOrder(limitOrder);
                        System.err.println("Buy Order Placed !!");
                    }
                } else if (lastUserTrade.getType() == Order.OrderType.BID) {
                    BigDecimal lastUserTradePrice = lastUserTrade.getPrice();
                    BigDecimal lastUserTradeFee = lastUserTrade.getFeeAmount();
                    BigDecimal totalLastUserTradePrice = lastUserTradePrice.add(lastUserTradeFee);
                    BigDecimal tickerAskPrice = geminiTicker.getAsk();
                    System.err.println("Selling>> totalLastUserTradePrice >>" + totalLastUserTradePrice);
                    System.err.println("Selling>> tickerAskPrice >>" + tickerAskPrice);
                    if (tickerAskPrice.compareTo(totalLastUserTradePrice) > 0) {
                        //BigDecimal limitOrderPrice = tickerAskPrice.divide(new BigDecimal(2));
                        LimitOrder limitOrder = new LimitOrder(Order.OrderType.ASK, new BigDecimal(1), CurrencyPair.BTC_USD, null, null, tickerAskPrice);
                        tradeService.placeLimitOrder(limitOrder);
                        System.err.println("Sell Order Placed !!");
                    }
                }
              /*  for (UserTrade userTrade : userTrades.getUserTrades()) {
                    BigDecimal lastUserTradePrice = userTrade.getPrice();
                    BigDecimal lastUserTradeFee = userTrade.getFeeAmount();
                    BigDecimal askPrice = lastUserTradePrice.add(lastUserTradeFee).add(new BigDecimal(-200));
                    System.err.println("lastUserTradePrice>>>>" + lastUserTradePrice);
                    LimitOrder limitOrder = new LimitOrder(Order.OrderType.BID, new BigDecimal(0.5), CurrencyPair.BTC_USD, null, null, new BigDecimal(130));
                    // tradeService.placeLimitOrder(limitOrder);
                    return;
                }*/
            }
        }

    }


    private static void geminiTrade(TradeService tradeService) throws IOException {

        Calendar futurecal = Calendar.getInstance();
        Date myDate = new Date(System.currentTimeMillis());
        futurecal.setTime(myDate);
        futurecal.add(Calendar.DATE, +1);

        //LimitOrder limitOrder = new LimitOrder(Order.OrderType.ASK, new BigDecimal(1), CurrencyPair.BTC_USD, null, futurecal.getTime(), new BigDecimal(6500));
        // System.err.println(tradeService.placeLimitOrder(limitOrder));

        // Get the account information
        OpenOrders openOrders = tradeService.getOpenOrders();
        System.out.println("AccountInfo as String: " + openOrders.toString());

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.DATE, -10);

        GeminiTradeService.GeminiTradeHistoryParams geminiTradeHistoryParams = new GeminiTradeService.GeminiTradeHistoryParams(CurrencyPair.BTC_USD, null, cal.getTime());

        UserTrades userTrades = tradeService.getTradeHistory(geminiTradeHistoryParams);
        System.err.println(userTrades);
        //UserTrade userTrade = userTrades.getUserTrades().get(0);
        //System.err.println(userTrade.getOrderId());
        //tradeService.cancelOrder("105226232");
        //tradeService.cancelOrder("105226227");
        //tradeService.cancelOrder("105226227");

        cal.add(Calendar.DATE, 10);

        LimitOrder limitOrder = new LimitOrder(Order.OrderType.BID, new BigDecimal(1), CurrencyPair.BTC_USD, null, cal.getTime(), new BigDecimal(6061));


        System.err.println(tradeService.placeLimitOrder(limitOrder));
       /* String depositAddress = accountService.requestDepositAddress(Currency.BTC);
        System.out.println("Deposit address: " + depositAddress);

        String withdrawResult = accountService.withdrawFunds(Currency.BTC, new BigDecimal(1).movePointLeft(4), "XXX");
        System.out.println("withdrawResult = " + withdrawResult);*/
    }


}
