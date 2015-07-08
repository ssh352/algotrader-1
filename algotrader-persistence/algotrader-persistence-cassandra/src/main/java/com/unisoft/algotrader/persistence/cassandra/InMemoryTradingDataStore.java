package com.unisoft.algotrader.persistence.cassandra;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.unisoft.algotrader.model.event.execution.ExecutionReport;
import com.unisoft.algotrader.model.event.execution.Order;
import com.unisoft.algotrader.model.trading.Account;
import com.unisoft.algotrader.model.trading.Portfolio;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by alex on 7/9/15.
 */
public class InMemoryTradingDataStore implements TradingDataStore {

    private final TradingDataStore delegateDataStore;

    private LoadingCache<String, Account> accountCache = CacheBuilder.newBuilder()
            .build(
                    new CacheLoader<String, Account>() {
                        public Account load(String accountId) {
                            return delegateDataStore.getAccount(accountId);
                        }
                    });

    private LoadingCache<String, Portfolio> portfolioCache = CacheBuilder.newBuilder()
            .build(
                    new CacheLoader<String, Portfolio>() {
                        public Portfolio load(String portfolioId) {
                            return delegateDataStore.getPortfolio(portfolioId);
                        }
                    });


    private LoadingCache<Long, ExecutionReport> executionReportCache = CacheBuilder.newBuilder()
            .build(
                    new CacheLoader<Long, ExecutionReport>() {
                        public ExecutionReport load(Long execId) {
                            return delegateDataStore.getExecutionReport(execId);
                        }
                    });


    private LoadingCache<Long, Order> orderCache = CacheBuilder.newBuilder()
            .build(
                    new CacheLoader<Long, Order>() {
                        public Order load(Long orderId) {
                            return delegateDataStore.getOrder(orderId);
                        }
                    });


    public InMemoryTradingDataStore(TradingDataStore delegateDataStore){
        this.delegateDataStore = delegateDataStore;
    }


    @Override
    public void saveAccount(Account account) {
        delegateDataStore.saveAccount(account);
        accountCache.put(account.getAccountId(), account);

    }

    @Override
    public Account getAccount(String accountId) {
        return accountCache.getUnchecked(accountId);
    }

    @Override
    public List<Account> getAllAccounts() {
        return Lists.newArrayList(accountCache.asMap().values());
    }

    @Override
    public void savePortfolio(Portfolio portfolio) {
        delegateDataStore.savePortfolio(portfolio);
        portfolioCache.put(portfolio.getPortfolioId(), portfolio);
    }

    @Override
    public Portfolio getPortfolio(String portfolioId) {
        return portfolioCache.getUnchecked(portfolioId);
    }

    @Override
    public List<Portfolio> getAllPortfolios() {
        return Lists.newArrayList(portfolioCache.asMap().values());
    }

    @Override
    public void saveExecutionReport(ExecutionReport er) {
        delegateDataStore.saveExecutionReport(er);
        executionReportCache.put(er.getExecId(), er);
    }

    @Override
    public ExecutionReport getExecutionReport(long execId) {
        return executionReportCache.getUnchecked(execId);
    }

    @Override
    public List<ExecutionReport> getAllExecutionReports() {
        return Lists.newArrayList(executionReportCache.asMap().values());
    }

    @Override
    public List<ExecutionReport> getExecutionReportsByInstId(int instId) {
        return executionReportCache.asMap().values().stream().filter(er -> er.getInstId() == instId).collect(toList());
    }

    @Override
    public List<ExecutionReport> getExecutionReportsByOrderId(long orderId) {
        return executionReportCache.asMap().values().stream().filter(er -> er.getOrderId() == orderId).collect(toList());
    }

    @Override
    public void saveOrder(Order order) {
        delegateDataStore.saveOrder(order);
        orderCache.put(order.getOrderId(), order);
    }

    @Override
    public Order getOrder(long orderId) {
        return orderCache.getUnchecked(orderId);
    }

    @Override
    public List<Order> getAllOrders() {
        return Lists.newArrayList(orderCache.asMap().values());
    }

    @Override
    public List<Order> getOrdersByInstId(int instId) {
        return orderCache.asMap().values().stream().filter(order -> order.getInstId() == instId).collect(toList());
    }

    @Override
    public List<Order> getOrdersByPortfolioId(String portfolioId) {
        if (portfolioId == null){
            return Lists.newArrayList();
        }
        return orderCache.asMap().values().stream().filter(order -> portfolioId.equals(order.getPortfolioId())).collect(toList());
    }

    @Override
    public List<Order> getOrdersByStrategyId(String strategyId) {
        if (strategyId == null){
            return Lists.newArrayList();
        }
        return orderCache.asMap().values().stream().filter(order -> strategyId.equals(order.getStrategyId())).collect(toList());
    }
}
