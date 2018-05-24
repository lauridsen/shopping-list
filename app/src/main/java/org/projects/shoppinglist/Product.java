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

    //Getter and setters
    public String getName () {
        return this.name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public int getQuantity () {
        return this.quantity;
    }

    public void setQuantity (int quantity) {
        this.quantity = quantity;
    }

    public String getMeasurement () {
        return this.measurement;
    }

    public void setMeasurement (String measurement) {
        this.measurement = measurement;
    }

    public Product() {} //Empty constructor for Firebase

    public Product(String name, int quantity, String measurement)  {
        this.name = name;
        this.quantity = quantity;
        this.measurement = measurement;
    }

    @Override
    public String toString() {
        return "<b>" + name + "</b>" + " " + quantity + " " + measurement;
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