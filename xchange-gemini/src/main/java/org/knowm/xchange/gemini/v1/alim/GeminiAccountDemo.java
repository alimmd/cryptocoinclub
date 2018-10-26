package org.knowm.xchange.gemini.v1.alim;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.trade.*;
import org.knowm.xchange.gemini.v1.GeminiExchange;
import org.knowm.xchange.gemini.v1.GeminiUtils;
import org.knowm.xchange.gemini.v1.dto.account.GeminiBalancesResponse;
import org.knowm.xchange.gemini.v1.service.GeminiAccountServiceRaw;
import org.knowm.xchange.gemini.v1.service.GeminiTradeService;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

        generic(accountService);
        raw((GeminiAccountServiceRaw) accountService);
        genericTrade(tradeService);
    }

    private static void generic(AccountService accountService) throws IOException {

        // Get the account information
        AccountInfo accountInfo = accountService.getAccountInfo();
        System.out.println("AccountInfo as String: " + accountInfo.toString());

       /* String depositAddress = accountService.requestDepositAddress(Currency.BTC);
        System.out.println("Deposit address: " + depositAddress);

        String withdrawResult = accountService.withdrawFunds(Currency.BTC, new BigDecimal(1).movePointLeft(4), "XXX");
        System.out.println("withdrawResult = " + withdrawResult);*/
    }

    private static void raw(GeminiAccountServiceRaw accountService) throws IOException {

        // Get the account information
        GeminiBalancesResponse[] bitstampBalance = accountService.getGeminiAccountInfo();
        System.out.println("BitstampBalance: " + bitstampBalance);


    }

    private static void genericTrade(TradeService tradeService) throws IOException {

        Calendar futurecal = Calendar.getInstance();
        Date myDate = new Date(System.currentTimeMillis());
        futurecal.setTime(myDate);
        futurecal.add(Calendar.DATE, +1);

        LimitOrder limitOrder = new LimitOrder(Order.OrderType.ASK, new BigDecimal(1), CurrencyPair.BTC_USD, null, futurecal.getTime(), new BigDecimal(6500));
       // System.err.println(tradeService.placeLimitOrder(limitOrder));

        // Get the account information
        OpenOrders openOrders = tradeService.getOpenOrders();
        System.out.println("AccountInfo as String: " + openOrders.toString());

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.DATE, -10);
        System.out.println(dateFormat.format(cal.getTime()));

        GeminiTradeService.GeminiTradeHistoryParams geminiTradeHistoryParams = new GeminiTradeService.GeminiTradeHistoryParams(CurrencyPair.BTC_USD, null,cal.getTime());

        UserTrades userTrades = tradeService.getTradeHistory(geminiTradeHistoryParams);
        System.err.println(userTrades);
        //UserTrade userTrade = userTrades.getUserTrades().get(0);
        //System.err.println(userTrade.getOrderId());
        //tradeService.cancelOrder("105226232");
        //tradeService.cancelOrder("105226227");
        //tradeService.cancelOrder("105226227");



      //  LimitOrder limitOrder = new LimitOrder(Order.OrderType.BID, new BigDecimal(1), CurrencyPair.BTC_USD, null, null, new BigDecimal(7000));


    //    System.err.println(tradeService.placeLimitOrder(limitOrder));
       /* String depositAddress = accountService.requestDepositAddress(Currency.BTC);
        System.out.println("Deposit address: " + depositAddress);

        String withdrawResult = accountService.withdrawFunds(Currency.BTC, new BigDecimal(1).movePointLeft(4), "XXX");
        System.out.println("withdrawResult = " + withdrawResult);*/
    }


}
