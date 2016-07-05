package rihoo.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.regex.Pattern;


public class ProductDetail extends ActionBarActivity implements android.view.View.OnClickListener {

    Button buttonSave;
    Button buttonDelete;
    Button buttonClose;
    Button buttonConfirmDeletion;
    Button buttonCancelDeletion;
    Button buttonOrderMore;
    Button buttonRecievedOne;
    Button buttonSoldOne;
    Button buttonImage;

    EditText editTextName;
    EditText editTextQuantity;
    EditText editTextPrice;
    EditText editTextPhone;

    ImageView imageView;

    Bitmap image;

    private int _Product_Id = 0;
    private int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        buttonSave = (Button) findViewById(R.id.Button_Save);
        buttonDelete = (Button) findViewById(R.id.Button_Delete);
        buttonClose = (Button) findViewById(R.id.Button_Close);
        buttonConfirmDeletion = (Button) findViewById(R.id.Button_Confirm_Deletion);
        buttonCancelDeletion = (Button) findViewById(R.id.Button_Cancel_Deletion);
        buttonOrderMore = (Button) findViewById(R.id.Button_Order_More);
        buttonRecievedOne = (Button) findViewById(R.id.Button_Recieved_One);
        buttonSoldOne = (Button) findViewById(R.id.Button_Sold_One);
        buttonImage = (Button) findViewById(R.id.Button_Image);

        editTextName = (EditText) findViewById(R.id.ET_Name);
        editTextQuantity = (EditText) findViewById(R.id.ET_Quantity);
        editTextPrice = (EditText) findViewById(R.id.ET_Price);
        editTextPhone = (EditText) findViewById(R.id.ET_Phone);

        buttonSave.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
        buttonClose.setOnClickListener(this);
        buttonConfirmDeletion.setOnClickListener(this);
        buttonCancelDeletion.setOnClickListener(this);
        buttonOrderMore.setOnClickListener(this);
        buttonRecievedOne.setOnClickListener(this);
        buttonSoldOne.setOnClickListener(this);
        buttonImage.setOnClickListener(this);

        imageView = (ImageView) findViewById(R.id.ImageView);

        Intent intent = getIntent();
        _Product_Id = intent.getIntExtra("product_Id", 0);
        state = intent.getIntExtra("state", 0);

        if (state == 1) {
            setRequiredButtonVisibility(1);
        } else {
            setRequiredButtonVisibility(0);
        }

        DatabaseHandler entry = new DatabaseHandler(this);
        Product product = entry.getProductById(_Product_Id);

        editTextName.setText(product.name);
        editTextQuantity.setText(toString().valueOf(product.quantity));
        editTextPrice.setText(changeFloatPrecisionForString(toString().valueOf(product.price)));

        if (product.supplierPhoneNumber != null) {
            editTextPhone.setText(product.supplierPhoneNumber);
        }

        if (product.imagePath != null && product.imagePath != "") {
            Bitmap image = BitmapFactory.decodeFile(product.imagePath);
            imageView.setImageBitmap(image);
        }

    }


    public void onClick(View view) {
        if (view == findViewById(R.id.Button_Save)) {
            DatabaseHandler entry = new DatabaseHandler(this);
            Product product = new Product();

            String nameString = editTextName.getText().toString();
            String quantityString = editTextQuantity.getText().toString();
            String priceString = editTextPrice.getText().toString();
            String phoneString = editTextPhone.getText().toString();

            if (((nameString.length() != 0) && (quantityString.length() != 0) &&
                    (priceString.length() != 0)) && Integer.parseInt(quantityString) >= 0 &&
                    Double.parseDouble(priceString) >= 0) {
                product.product_ID = _Product_Id;
                product.name = nameString;
                product.quantity = Integer.parseInt(quantityString);
                product.price = Double.parseDouble(priceString);

                if (phoneString.length() > 9)
                {
                    product.supplierPhoneNumber = phoneString;
                }

                if (_Product_Id == 0) {
                    _Product_Id = entry.insert(product);

                    Toast.makeText(this, "New product has been inserted",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    entry.update(product);
                    Toast.makeText(this, "Product record has been updated",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                if ((nameString.length() == 0) || (quantityString.length() == 0) ||
                        (priceString.length() == 0)) {
                    Toast.makeText(this, "The name, quantity, and price fields must all contain "
                            + "a value.", Toast.LENGTH_SHORT).show();
                } else if (!(Integer.parseInt(quantityString) >= 0) ||
                        !(Integer.parseInt(priceString) >= 0)) {
                    Toast.makeText(this, "Quantity and price values must be positive.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else if (view == findViewById(R.id.Button_Delete)) {
            findViewById(R.id.LL_Confirm_Deletion).setVisibility(View.VISIBLE);
            findViewById(R.id.LL_Edit_Buttons).setVisibility(View.GONE);
        } else if (view == findViewById(R.id.Button_Close)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (view == findViewById(R.id.Button_Confirm_Deletion)) {
            DatabaseHandler entry = new DatabaseHandler(this);
            entry.delete(_Product_Id);
            Toast.makeText(this, "Product record has been deleted", Toast.LENGTH_SHORT);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (view == findViewById(R.id.Button_Cancel_Deletion)) {
            findViewById(R.id.LL_Confirm_Deletion).setVisibility(View.GONE);
            findViewById(R.id.LL_Edit_Buttons).setVisibility(View.VISIBLE);
        } else if (view == findViewById(R.id.Button_Order_More)) {
            DatabaseHandler entry = new DatabaseHandler(this);
            Product product = entry.getProductById(_Product_Id);

            if (product.supplierPhoneNumber != null) {
                Intent goToDialer = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + product.supplierPhoneNumber));
                startActivity(goToDialer);
            } else {
                Toast.makeText(this, "No phone number provided for this supplier.",
                        Toast.LENGTH_SHORT).show();
            }

        } else if (view == findViewById(R.id.Button_Recieved_One)) {
            DatabaseHandler entry = new DatabaseHandler(this);
            Product product;
            product = entry.getProductById(_Product_Id);

            product.quantity = product.quantity + 1;
            entry.update(product);

            editTextQuantity.setText(toString().valueOf(product.quantity));
        } else if (view == findViewById(R.id.Button_Sold_One)) {
            DatabaseHandler entry = new DatabaseHandler(this);
            Product product = entry.getProductById(_Product_Id);

            if (product.quantity != 0) {
                product.quantity = product.quantity - 1;
                entry.update(product);
                editTextQuantity.setText(toString().valueOf(product.quantity));
            }
        } else if (view == findViewById(R.id.Button_Image)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            DatabaseHandler entry = new DatabaseHandler(this);
            Product product = entry.getProductById(_Product_Id);

            product.imagePath = picturePath;

            try {
                Bitmap image = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(selectedImage));
                entry.updateImage(_Product_Id, image, this);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRequiredButtonVisibility (int state) {
        if (state == 1) {
            findViewById(R.id.Button_Order_More).setVisibility(View.INVISIBLE);
            findViewById(R.id.Button_Recieved_One).setVisibility(View.INVISIBLE);
            findViewById(R.id.Button_Sold_One).setVisibility(View.INVISIBLE);
            findViewById(R.id.Button_Delete).setVisibility(View.GONE);
        } else {
            findViewById(R.id.Button_Order_More).setVisibility(View.VISIBLE);
            findViewById(R.id.Button_Recieved_One).setVisibility(View.VISIBLE);
            findViewById(R.id.Button_Sold_One).setVisibility(View.VISIBLE);
            findViewById(R.id.Button_Delete).setVisibility(View.VISIBLE);
        }

    }

    // Quick-rigging the double value to avoid a regex sauce
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