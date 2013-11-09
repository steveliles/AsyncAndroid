package com.packt.androidconcurrency.chapter6.example3;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class NasaRSS implements Parcelable {

    public static class Item {
        public String url;
        public String title;
        public Item(String url, String title) {
            this.url = url;
            this.title = title;
        }
    }

    private List<Item> items;

    public NasaRSS(Parcel parcel) {
        int len = parcel.readInt();
        for (int i=0; i<len; i++)
            items.add(new Item(
                parcel.readString(),
                parcel.readString()));
    }

    public NasaRSS(){
        items = new ArrayList<Item>();
    }

    public void add(String url, String title) {
        items.add(new Item(url, title));
    }

    public int size() {
        return items.size();
    }

    public Item get(int i){
        return items.get(i);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(items.size());
        for (Item i : items) {
            dest.writeString(i.url);
            dest.writeString(i.title);
        }
    }
}
