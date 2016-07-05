package rihoo.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements android.view.View.OnClickListener {

    Button buttonAdd;

    TextView noProduct;
    TextView product_Id;

    ListView listView;

    ProductAdapter adapter;

    ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAdd = (Button) findViewById(R.id.Button_Add);
        buttonAdd.setOnClickListener(this);

        noProduct = (TextView) findViewById(R.id.TV_No_Products);

        listView = (ListView) findViewById(R.id.ListView);

        // Begin grabbing product information from the database
        DatabaseHandler entry = new DatabaseHandler(this);
        products = entry.getProducts();

        if (products.size() != 0) {
            listView.setVisibility(View.VISIBLE);
            noProduct.setVisibility(View.GONE);

            listView = (ListView) findViewById(R.id.ListView);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    product_Id = (TextView) view.findViewById(R.id.TV_Product_ID);
                    String productId = product_Id.getText().toString();
                    Intent goToDetail = new Intent(getApplicationContext(), ProductDetail.class);
                    goToDetail.putExtra("product_Id", Integer.parseInt(productId));
                    goToDetail.putExtra("state", 0);
                    startActivity(goToDetail);
                }
            });

            adapter = new ProductAdapter(MainActivity.this,
                    R.color.item_background_color, products);

            listView.setAdapter(adapter);

        } else {
            listView.setVisibility(View.GONE);
            noProduct.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View view) {
        if (view == findViewById(R.id.Button_Add)) {
            Intent goToAddProduct = new Intent(this, ProductDetail.class);
            goToAddProduct.putExtra("product_Id", 0);
            goToAddProduct.putExtra("state", 1);
            startActivityForResult(goToAddProduct, 0);
        }
        else {
            if (findViewById(R.id.ListView).getVisibility() == View.GONE) {
                Toast.makeText(this, "No products to display.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

