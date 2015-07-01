package com.unisoft.algotrader.series;

import com.google.common.base.Objects;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.hash.TLongIntHashMap;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by alex on 5/25/15.
 */
public class DoubleTimeSeries implements Iterable<DoubleTimeSeries.Tuple>{


    public static class Tuple{
        public final long dateTime;
        public final double data;

        Tuple(long dateTime , double data){
            this.dateTime = dateTime;
            this.data = data;
        }

        @Override
        public String toString() {
            return "Tuple{" +
                    "dateTime=" + dateTime +
                    ", data=" + data +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Tuple)) return false;
            Tuple tuple = (Tuple) o;
            return Objects.equal(dateTime, tuple.dateTime) &&
                    Objects.equal(data, tuple.data);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(dateTime, data);
        }
    }

    public static class DoubleTimeSeriesIterator implements Iterator<DoubleTimeSeries.Tuple>{
        private final long [] dateTimeSeries;
        private final double [] dataSeries;
        private int cursor;

        public DoubleTimeSeriesIterator(long [] dateTimeSeries, double [] dataSeries){
            this.dateTimeSeries = dateTimeSeries;
            this.dataSeries = dataSeries;
            cursor = 0;
        }

        @Override
        public boolean hasNext() {
            return cursor < dateTimeSeries.length;
        }

        @Override
        public Tuple next() {
            if (hasNext()) {
                Tuple tuple = new Tuple(dateTimeSeries[cursor], dataSeries[cursor]);
                cursor++;
                return tuple;
            }
            throw new NoSuchElementException();
        }
    }


    private TLongList datetimeSeries = new TLongArrayList();
    private TLongIntMap index = new TLongIntHashMap();
    private TDoubleList dataSeries = new TDoubleArrayList();


    private int currIdx = 0;
    private long currTime = Long.MIN_VALUE;

    public void add(Date date, double data){
        add(date.getTime(), data);
    }

    public void add(long date, double data){
        assert date >= currTime;
        currTime = date;
        index.put(date, currIdx++);
        dataSeries.add(data);
        datetimeSeries.add(date);

    }

    public int count(){
        return dataSeries.size();
    }


    public double getByIdx(int idx){
        return dataSeries.get(idx);
    }

    public double getByDate(Date date){
        return getByDate(date.getTime());
    }

    public double getByDate(long datetime){
        int index = getIndex(datetime);
        return index>=0 ? dataSeries.get(index) : 0.0;
    }

    protected int getIndex(long datetime){
        return index.containsKey(datetime) ? index.get(datetime) : -1;
    }

    public double ago(int ago){
        int index = lastIndex() - ago;
        return index>=0 ? dataSeries.get(index) : 0.0;
    }

    public long [] index(){
        long [] idx =index.keys();
        Arrays.sort(idx);
        return idx;
    }

    public long firstDateTime(){
        if (count() <= 0){
            throw new IllegalArgumentException("Time Series has no element");
        }
        return datetimeSeries.get(0);
    }

    public long lastDateTime(){
        if (count() <= 0){
            throw new IllegalArgumentException("Time Series has no element");
        }
        return datetimeSeries.get(count()-1);
    }

    public int firstIndex(){
        return 0;
    }

    public int lastIndex(){
        return dataSeries.size()-1;
    }

    public double first(){
        if (count() <= 0){
            throw new IllegalArgumentException("Time Series has no element");
        }
        return dataSeries.get(0);
    }

    public double last(){
        if (count() <= 0){
            throw new IllegalArgumentException("Time Series has no element");
        }
        return dataSeries.get(count()-1);
    }


    public Iterator<Tuple> iterator() {
        return new DoubleTimeSeriesIterator(datetimeSeries.toArray(), dataSeries.toArray());
    }
}