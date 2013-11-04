package com.packt.androidconcurrency.chapter6.example4;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NasaRSS implements Iterable<NasaRSS.Item>, Parcelable {

    public static class Item {
        public String url;
        public Item(String url) {
            this.url = url;
        }
    }

    private List<Item> items;

    public NasaRSS(Parcel parcel) {
        int len = parcel.readInt();
        for (int i=0; i<len; i++) {
            items.add(new Item(parcel.readString()));
        }
    }

    public NasaRSS(){
        items = new ArrayList<Item>();
    }

    public void add(String url) {
        items.add(new Item(url));
    }

    public int size() {
        return items.size();
    }

    public Iterator<Item> iterator(){
        return items.iterator();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(items.size());
        for (Item i : items)
            dest.writeString(i.url);
    }
}
