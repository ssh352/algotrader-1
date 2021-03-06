package com.unisoft.algotrader.persistence.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.unisoft.algotrader.model.refdata.Currency;
import com.unisoft.algotrader.model.refdata.Exchange;
import com.unisoft.algotrader.model.refdata.Instrument;
import com.unisoft.algotrader.persistence.RefDataStore;
import com.unisoft.algotrader.persistence.cassandra.accessor.CurrencyAccessor;
import com.unisoft.algotrader.persistence.cassandra.accessor.ExchangeAccessor;
import com.unisoft.algotrader.persistence.cassandra.accessor.InstrumentAccessor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by alex on 7/7/15.
 */
@Singleton
public class CassandraRefDataStore implements RefDataStore {

    private Cluster cluster;
    private String keySpace;
    private Session session;
    private MappingManager mappingManager;
    private CurrencyAccessor currencyAccessor;
    private ExchangeAccessor exchangeAccessor;
    private InstrumentAccessor instrumentAccessor;
    private CassandraIdSupplier idSupplier;

    @Inject
    public CassandraRefDataStore(CassandraRefDataStoreConfig config) {
        this(Cluster.builder().withProtocolVersion(ProtocolVersion.V3).addContactPoint(config.host).build(), config.keyspace);
    }

    public CassandraRefDataStore() {
        this(Cluster.builder().withProtocolVersion(ProtocolVersion.V3).addContactPoint("localhost").build(), "refdata");
    }

    public CassandraRefDataStore(Cluster cluster, String keySpace) {
        this.cluster = cluster;
        this.keySpace = keySpace;

    }

    public void connect() {
        this.session = cluster.connect(keySpace);
        this.mappingManager = new MappingManager(session);
        this.currencyAccessor = mappingManager.createAccessor(CurrencyAccessor.class);
        this.exchangeAccessor = mappingManager.createAccessor(ExchangeAccessor.class);
        this.instrumentAccessor = mappingManager.createAccessor(InstrumentAccessor.class);
        this.idSupplier = new CassandraIdSupplier(session, keySpace);
    }

    @Override
    public void saveCurrency(Currency currency) {
        Mapper<Currency> mapper = mappingManager.mapper(Currency.class);
        mapper.save(currency);
    }

    @Override
    public Currency getCurrency(String ccyId) {
        Mapper<Currency> mapper = mappingManager.mapper(Currency.class);
        return mapper.get(ccyId);
        //return currencyAccessor.get(ccyId);
    }

    @Override
    public List<Currency> getAllCurrencies() {
        return currencyAccessor.getAll().all();
    }


    @Override
    public void saveExchange(Exchange exchange) {
        Mapper<Exchange> mapper = mappingManager.mapper(Exchange.class);
        mapper.save(exchange);
    }

    @Override
    public Exchange getExchange(String exchId) {
        //return exchangeAccessor.get(exchId);
        Mapper<Exchange> mapper = mappingManager.mapper(Exchange.class);
        return mapper.get(exchId);
    }

    @Override
    public List<Exchange> getAllExchanges() {
        return exchangeAccessor.getAll().all();
    }


    @Override
    public void saveInstrument(Instrument instrument) {
        Mapper<Instrument> mapper = mappingManager.mapper(Instrument.class);
        mapper.save(instrument);
    }

    @Override
    public Instrument getInstrument(long instId) {
        //return instrumentAccessor.get(instId);
        Mapper<Instrument> mapper = mappingManager.mapper(Instrument.class);
        return mapper.get(instId);
    }
    @Override
    public List<Instrument> getAllInstruments() {
        return instrumentAccessor.getAll().all();
    }

    @Override
    public Instrument getInstrumentBySymbolAndExchange(String symbol, String exchId){
        List<Instrument> result = instrumentAccessor.getBySymbolAndExchange(symbol, exchId).all();
        return  result!=null && result.size()>0 ? result.get(0) : null;
    }

    @Override
    public Instrument getInstrumentBySymbolAndExchange(String providerId, String symbol, String exchId){
        return getAllInstruments().stream().filter(i -> symbol != null && symbol.equals(i.getSymbol(providerId)) && exchId != null && exchId.equals(i.getExchId(providerId))).findAny().orElse(null);
    }


    public long nextId(){
        return idSupplier.next();
    }

}