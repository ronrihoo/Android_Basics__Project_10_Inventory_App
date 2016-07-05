package rihoo.inventoryapp;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ProductAdapter extends ArrayAdapter<Product> {

    private int colorResourceId;
    private int product_ID;

    ListView listView;

    View listItemView;

    ArrayList<Product> products;

    /**
     * Constructor
     */
    public ProductAdapter(Context context, int colorResourceId, ArrayList<Product> products) {
        super(context, 0, products);
        this.colorResourceId = colorResourceId;
        this.products = products;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the color for the background
        int color = ContextCompat.getColor(getContext(), this.colorResourceId);

        // convertView
        listItemView = convertView;

        // Button
        Button saleButton;

        // Check whether convertView is null
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.layout_list_item, parent, false);

        }

        // View/TextViews
        View textContainer = listItemView.findViewById(R.id.LL_List_Item_Main);
        TextView idTextView = (TextView) listItemView.findViewById(R.id.TV_Product_ID);
        TextView nameTextView = (TextView) listItemView.findViewById(R.id.TV_Product_Name);
        TextView quantityTextView = (TextView) listItemView.findViewById(R.id.TV_Product_Quantity);
        TextView priceTextView = (TextView) listItemView.findViewById(R.id.TV_Product_Price);

        listView = (ListView) listItemView.findViewById(R.id.ListView);

        // Product object
        Product currentProduct = getItem(position);
        product_ID = currentProduct.product_ID;

        // Button
        saleButton = (Button) listItemView.findViewById(R.id.Button_Sale);
        saleButton.setTag(position);
        saleButton.setOnClickListener(saleButtonOnClickListener);

        // Set the background color of the text container View
        textContainer.setBackgroundColor(color);

        // Display object's information in ListView item
        idTextView.setText(toString().valueOf(currentProduct.product_ID));
        nameTextView.setText(currentProduct.name);
        quantityTextView.setText(toString().valueOf(currentProduct.quantity));
        priceTextView.setText(changeFloatPrecisionForString(toString().valueOf(currentProduct.price)));

        return listItemView;
    }

    private View.OnClickListener saleButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View parentRow = (View) v.getParent();
            ListView listView = (ListView) parentRow.getParent();
            final int position = listView.getPositionForView(parentRow);

            DatabaseHandler handler = new DatabaseHandler(getContext());
            Product product = handler.getProductById(getItem(position).product_ID);

            if (product.quantity > 0) {
                product.quantity = product.quantity - 1;
                handler.update(product);
                updateProductsInventory();
            }
        }
    };

    public void updateProductsInventory() {
        DatabaseHandler entry = new DatabaseHandler(getContext());
        ArrayList<Product> updatedList = entry.getProducts();

        this.products.clear();
        this.products.addAll(updatedList);
        this.notifyDataSetChanged();
    }

    // Quick-rigging the double value to avoid an unnecessary regex sauce
    public String changeFloatPrecisionForString(String str) {
        String[] tempStrArr = str.split(Pattern.quote("."));
        if (tempStrArr.length > 1) {
            if (tempStrArr[1].length() > 1) {
                return (tempStrArr[0] + "." + tempStrArr[1].substring(0, 2));
            } else {
                return str + "0";
            }
        } else {
            return str;
        }
    }
}
