package com.unisoft.algotrader.provider.execution.simulation;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.unisoft.algotrader.event.SampleEventFactory;
import com.unisoft.algotrader.event.bus.RingBufferMarketDataEventBus;
import com.unisoft.algotrader.model.event.data.MarketDataContainer;
import com.unisoft.algotrader.model.refdata.Instrument;
import com.unisoft.algotrader.model.trading.Portfolio;
import com.unisoft.algotrader.persistence.InMemoryTradingDataStore;
import com.unisoft.algotrader.persistence.TradingDataStore;
import com.unisoft.algotrader.provider.ProviderManager;
import com.unisoft.algotrader.provider.data.DummyDataProvider;
import com.unisoft.algotrader.provider.data.HistoricalSubscriptionKey;
import com.unisoft.algotrader.trading.Strategy;
import com.unisoft.algotrader.utils.threading.disruptor.waitstrategy.NoWaitStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by alex on 6/8/15.
 */
public class CSVPublishTest {


    private static Instrument testInstrument = SampleEventFactory.TEST_HKD_INSTRUMENT;

    private static final Logger LOG = LogManager.getLogger(CSVPublishTest.class);

    static class CountDownStrategy extends Strategy {
        private CountDownLatch latch;
        private int exp;
        private int count = 0;

        public CountDownStrategy(int strategyId, TradingDataStore tradingDataStore, int portfolioId, CountDownLatch latch, int exp, RingBuffer... providers){
            super(strategyId, tradingDataStore, portfolioId, providers);
            this.latch = latch;
            this.exp = exp;
        }

        @Override
        public void onMarketDataContainer(MarketDataContainer data){
            LOG.info("instId {}, onMarketDataContainer {}", strategyId, data);
            count ++;
            if (count==exp){
                latch.countDown();
            }
        }
    }


    public static void main(String [] args) throws Exception {

        RingBuffer<MarketDataContainer> marketDataRB
                = RingBuffer.createSingleProducer(MarketDataContainer.FACTORY, 1024, new NoWaitStrategy());

        ProviderManager providerManager = new ProviderManager();
        DummyDataProvider provider = new DummyDataProvider(providerManager, new RingBufferMarketDataEventBus(marketDataRB));

        Portfolio portfolio = new Portfolio(1, TradingDataStore.DEFAULT_ACCOUNT.accountId());
        InMemoryTradingDataStore tradingDataStore = new InMemoryTradingDataStore();

        tradingDataStore.savePortfolio(portfolio);


        CountDownLatch latch = new CountDownLatch(5);
        CountDownStrategy strategy1 = new CountDownStrategy(1, tradingDataStore, portfolio.portfolioId(), latch, 10, marketDataRB);
        CountDownStrategy strategy2 = new CountDownStrategy(2, tradingDataStore, portfolio.portfolioId(), latch, 10, marketDataRB);
        CountDownStrategy strategy3 = new CountDownStrategy(3, tradingDataStore, portfolio.portfolioId(), latch, 10, marketDataRB);
        CountDownStrategy strategy4 = new CountDownStrategy(4, tradingDataStore, portfolio.portfolioId(), latch, 10, marketDataRB);
        CountDownStrategy strategy5 = new CountDownStrategy(5, tradingDataStore, portfolio.portfolioId(), latch, 10, marketDataRB);

        ExecutorService executor = Executors.newFixedThreadPool(8, DaemonThreadFactory.INSTANCE);
        executor.submit(strategy1);
        executor.submit(strategy2);

        executor.submit(strategy3);
        executor.submit(strategy4);
        executor.submit(strategy5);

        LOG.info("sleep");
        Thread.sleep(5000);

        LOG.info("start");
        provider.subscribeHistoricalData(HistoricalSubscriptionKey.createDailySubscriptionKey(provider.providerId().id, testInstrument.getInstId(), 20110101, 20141231));

        latch.await();

        LOG.info("done");
    }
}
