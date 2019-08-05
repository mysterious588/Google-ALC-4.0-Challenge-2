package com.google.pluralsigh.travelmantics;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {
    DatabaseReference mDatabaseReference;
    FirebaseDatabase mFirebaseDatabase;
    EditText txtTitle, txtDescription, txtPrice;
    TravelDeal deal;
    Button addImageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUtil.openFbReferecne("travel deals", this);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtPrice = findViewById(R.id.txtPrice);
        final Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("deal");
        if (deal == null) {
            deal = new TravelDeal();
        }
        this.deal = deal;
        txtTitle.setText(deal.getTitle());
        txtPrice.setText(deal.getPrice());
        txtDescription.setText(deal.getDescription());
        addImageButton = findViewById(R.id.buttonImage);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(intent.createChooser(intent,"intent pictures"),32); //any number that wasn't used before
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        if(FirebaseUtil.isAdmin){
            menu.findItem(R.id.saveMenu).setVisible(true);
            menu.findItem(R.id.deleteMenu).setVisible(true);
            enableEditTexts(true);
        }
        else {
            menu.findItem(R.id.saveMenu).setVisible(false);
            menu.findItem(R.id.deleteMenu).setVisible(false);
            enableEditTexts(false);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveMenu:
                saveDeal();
                Toast.makeText(this, "Deal Saved", Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return true;
            case R.id.deleteMenu:
                deleteDeal();
                Toast.makeText(this, "Deal deleted", Toast.LENGTH_LONG).show();
                backToList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveDeal() {
        String title = txtTitle.getText().toString(), description = txtDescription.getText().toString(), price = txtPrice.getText().toString();
        deal.setTitle(title);
        deal.setPrice(price);
        deal.setDescription(description);
        if (deal.getId() == null) mDatabaseReference.push().setValue(deal);
        else mDatabaseReference.child(deal.getId()).setValue(deal);

    }

    private void deleteDeal() {
        if (deal == null) Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        mDatabaseReference.child(deal.getId()).removeValue();
    }

    private void backToList() {
        startActivity(new Intent(this, ListActivity.class));
    }

    private void clean() {
        txtPrice.setText("");
        txtDescription.setText("");
        txtTitle.setText("");
        txtTitle.requestFocus();
    }
    private void enableEditTexts(boolean isEnabled){
        txtTitle.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 32 && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();
            StorageReference ref = FirebaseUtil.mStorage.getReference().child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            deal.setImageUrl(uri.toString());
                            ImageView imageView = findViewById(R.id.dealImage);
                            Picasso.get().load(uri.toString()).into(imageView);
                        }
                    });

                }
            });
        }
    }
}
