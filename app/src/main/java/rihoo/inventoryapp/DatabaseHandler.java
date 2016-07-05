package rihoo.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class DatabaseHandler {

    private DatabaseHelper dbHelper;

    public DatabaseHandler(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public int insert(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Product.KEY_name, product.name);
        values.put(Product.KEY_quantity, product.quantity);
        values.put(Product.KEY_price, product.price);

        if (product.supplierPhoneNumber != null) {
            values.put(Product.KEY_supplier_phone, product.supplierPhoneNumber);
        }

        if (product.imagePath != null) {
            values.put(Product.KEY_image_path, product.imagePath);
        }

        long product_Id = db.insert(Contract.ProductTable.TABLE_name, null, values);

        db.close();

        return (int) product_Id;
    }

    public void delete(int product_Id) {
        // Remove associated image from internal storage
        String imagePath = getImagePath(product_Id); // See above
        if (imagePath != null && imagePath.length() != 0) {
            File reportFilePath = new File(imagePath);
            reportFilePath.delete();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(Contract.ProductTable.TABLE_name, Product.KEY_ID + "= ?",
                new String[]{String.valueOf(product_Id)});

        db.close();
    }

    public void update(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Product.KEY_name, product.name);
        values.put(Product.KEY_quantity, product.quantity);
        values.put(Product.KEY_price, product.price);

        if (product.supplierPhoneNumber != null) {
            values.put(Product.KEY_supplier_phone, product.supplierPhoneNumber);
        }

        if (product.imagePath != null) {
            values.put(Product.KEY_image_path, product.imagePath);
        }

        db.update(Contract.ProductTable.TABLE_name, values, Product.KEY_ID + "= ?",
                new String[]{String.valueOf(product.product_ID)});

        db.close();
    }

    public ArrayList<HashMap<String, String>> getProductList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ArrayList<HashMap<String, String>> productList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT " +
                Product.KEY_ID + "," +
                Product.KEY_name + "," +
                Product.KEY_quantity + ", " +
                Product.KEY_price + ", " +
                Product.KEY_supplier_phone + ", " +
                Product.KEY_image_path +
                " FROM " + Contract.ProductTable.TABLE_name;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> product = new HashMap<String, String>();
                product.put("id", cursor.getString(cursor.getColumnIndex(Product.KEY_ID)));
                product.put("name", cursor.getString(cursor.getColumnIndex(Product.KEY_name)));
                product.put("quantity", cursor.getString(cursor.getColumnIndex(Product.KEY_quantity)));
                product.put("price", cursor.getString(cursor.getColumnIndex(Product.KEY_price)));
                product.put("supplier_phone", cursor.getString(cursor.getColumnIndex(Product.KEY_supplier_phone)));
                product.put("imagePath", cursor.getString(cursor.getColumnIndex(Product.KEY_image_path)));
                productList.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return productList;
    }

    public ArrayList<Product> getProducts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ArrayList<Product> products = new ArrayList<>();

        String selectQuery = "SELECT " +
                Product.KEY_ID + "," +
                Product.KEY_name + "," +
                Product.KEY_quantity + ", " +
                Product.KEY_price + ", " +
                Product.KEY_supplier_phone + ", " +
                Product.KEY_image_path +
                " FROM " + Contract.ProductTable.TABLE_name;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                products.add(new Product(cursor.getInt(cursor.getColumnIndex(Product.KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(Product.KEY_name)),
                        cursor.getInt(cursor.getColumnIndex(Product.KEY_quantity)),
                        cursor.getDouble(cursor.getColumnIndex(Product.KEY_price)),
                        cursor.getString(cursor.getColumnIndex(Product.KEY_supplier_phone)),
                        cursor.getString(cursor.getColumnIndex(Product.KEY_image_path))));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return products;
    }

    public Product getProductById(int Id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  " +
                Product.KEY_ID + "," +
                Product.KEY_name + "," +
                Product.KEY_quantity + ", " +
                Product.KEY_price + ", " +
                Product.KEY_supplier_phone + ", " +
                Product.KEY_image_path +
                " FROM " + Contract.ProductTable.TABLE_name
                + " WHERE " + Product.KEY_ID + "=?";

        Product product = new Product();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(Id)});

        if (cursor.moveToFirst()) {
            do {
                product.product_ID = cursor.getInt(cursor.getColumnIndex(Product.KEY_ID));
                product.name = cursor.getString(cursor.getColumnIndex(Product.KEY_name));
                product.quantity = cursor.getInt(cursor.getColumnIndex(Product.KEY_quantity));
                product.price = cursor.getFloat(cursor.getColumnIndex(Product.KEY_price));
                product.supplierPhoneNumber = cursor.getString(cursor.getColumnIndex(Product.KEY_supplier_phone));
                product.imagePath = cursor.getString(cursor.getColumnIndex(Product.KEY_image_path));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return product;
    }

    public void updateImage(long Product_Id, Bitmap image, Context context) {
        String imagePath = "";
        File internalStorage = context.getDir("Images", Context.MODE_PRIVATE);
        File imageFilePath = new File(internalStorage, Product_Id + ".png");
        imagePath = imageFilePath.toString();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFilePath);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception ex) {
            Log.i("DATABASE", "Problem updating image", ex);
            imagePath = "";
        }

        // Updates the database entry for the report to point to the image
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues content = new ContentValues();
        content.put(Product.KEY_image_path,
                imagePath);

        db.update(Contract.ProductTable.TABLE_name,
                content,
                Product.KEY_ID + "=?",
                new String[]{String.valueOf(Product_Id)});
    }

    public Bitmap getImage(int product_Id) {
        String imagePath = getImagePath(product_Id);

        if (imagePath == null || imagePath.length() == 0)
            return (null);

        Bitmap image = BitmapFactory.decodeFile(imagePath);

        return image;
    }

    private String getImagePath(long product_Id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(Contract.ProductTable.TABLE_name,
                null,
                Product.KEY_ID + "=?",
                new String[]{String.valueOf(product_Id)},
                null,
                null,
                null);
        cursor.moveToNext();

        String imagePath = cursor.getString(cursor.getColumnIndex(Product.KEY_image_path));

        return imagePath;
    }
}