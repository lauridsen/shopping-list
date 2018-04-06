package org.projects.shoppinglist;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michael on 05-04-2018.
 */

public class Product implements Parcelable {

    String name;
    int quantity;
    String measurement;

    public Product() {} //Empty constructor we will need later!

    public Product(String name, int quantity, String measurement)  {
        this.name = name;
        this.quantity = quantity;
        this.measurement = measurement;
    }

    @Override
    public String toString() {
        return quantity + " " + measurement + " " + name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(quantity);
        dest.writeString(measurement);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    // "De-parcel object
    public Product(Parcel in) {
        name = in.readString();
        quantity = in.readInt();
        measurement = in.readString();
    }

}