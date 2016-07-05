package rihoo.inventoryapp;


public class Product {
    // Constants
    public static final String KEY_ID = "id";
    public static final String KEY_name = "name";
    public static final String KEY_quantity = "quantity";
    public static final String KEY_price = "price";
    public static final String KEY_supplier_phone = "supplier_phone";
    public static final String KEY_image_path = "image_path";

    // Variables
    public int product_ID;
    public String name;
    public int quantity;
    public double price;
    public String supplierPhoneNumber;
    public String imagePath;

    public Product() {

    }

    public Product(int product_ID,
                   String name,
                   int quantity,
                   double price,
                   String supplierPhoneNumber,
                   String imagePath) {
        this.product_ID = product_ID;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.supplierPhoneNumber = supplierPhoneNumber;
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Product{" +
                "product_ID=" + product_ID +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", supplierPhoneNumber=" + supplierPhoneNumber +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
