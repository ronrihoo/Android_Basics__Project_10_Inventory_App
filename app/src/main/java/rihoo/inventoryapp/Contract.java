package rihoo.inventoryapp;

import android.content.Context;


public class Contract {
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "INVENTORY.db";

    public Contract(Context context) {

    }

    public class ProductTable {
        // Constants
        public static final String TABLE_name = "products";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_name + "("
                + Product.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Product.KEY_name + " TEXT, "
                + Product.KEY_quantity + " INTEGER, "
                + Product.KEY_price + " REAL, "
                + Product.KEY_supplier_phone + " TEXT, "
                + Product.KEY_image_path + " TEXT ) ";
    }
}
