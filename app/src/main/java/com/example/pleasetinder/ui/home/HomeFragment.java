package com.example.pleasetinder.ui.home;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pleasetinder.R;
import com.example.pleasetinder.activities.BaseFragment;
import com.example.pleasetinder.activities.ChatActivity;
import com.example.pleasetinder.activities.SignInActivity;
import com.example.pleasetinder.adapters.RecentConversationsAdapter;
import com.example.pleasetinder.adapters.UsersAdapter;
import com.example.pleasetinder.adapters.imageAdapter;
import com.example.pleasetinder.databinding.FragmentHomeBinding;
import com.example.pleasetinder.listeners.ConversionListener;
import com.example.pleasetinder.listeners.OnItemClickListener;
import com.example.pleasetinder.models.ChatMessage;
import com.example.pleasetinder.models.SpacesItemDecoration;
import com.example.pleasetinder.models.User;
import com.example.pleasetinder.utilities.Constants;
import com.example.pleasetinder.utilities.PreferenceManager;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class HomeFragment extends BaseFragment implements ConversionListener {

    private FragmentHomeBinding binding;
    private String encodedImage;
    private PreferenceManager preferenceManager;
    imageAdapter adapter;
    List<Integer> imageResIds;
    FirebaseFirestore database;
    HashMap<String, String> hashMap = new HashMap<>();
    Integer imageChanged = 0;
    List<String> listString;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getActivity());
        listString = new ArrayList<>();
        imageResIds =  new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            imageResIds.add(R.drawable.ic_add);
        }
        getUsers();

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        binding.recyclerView.setLayoutManager(layoutManager);
        adapter = new imageAdapter(imageResIds, this);
        binding.recyclerView.setAdapter(adapter);
        int spacing = 10; // 10dp spacing
        binding.recyclerView.addItemDecoration(new SpacesItemDecoration(spacing));
        loadUserDetails();
        getToken();
        setListeners();

        // Optional: Scroll to the EditText if needed

        return root;
    }

    public void onItemClick(Integer position) {
        // Open the image library
        imageChanged = position;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            // Handle the selected image URI
            InputStream inputStream = null;

            try {
                inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                encodedImage = encodedImage(bitmap);
                hashMap.put(imageChanged.toString(), encodedImage);
                DocumentReference docRef = database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
                docRef.update(Collections.singletonMap(Constants.KEY_IMAGE_LIST, hashMap)).
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Success
                                    changeItem(imageChanged, encodedImage);
                                }
                            }
                        });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void getUsers() {
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                hashMap = (HashMap<String, String>) queryDocumentSnapshot.get(Constants.KEY_IMAGE_LIST);
                                try {
                                    if (hashMap == null) {
                                        hashMap = new HashMap<>();
                                    } else if (hashMap.size() > 0) {
                                        Integer z = 0;
                                        for (String key : hashMap.keySet()) {
                                            changeItem(Integer.parseInt(key), hashMap.get(key));
                                            z++;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (queryDocumentSnapshot.get(Constants.KEY_AGE) != null) {
                                    Integer age = (Integer) Integer.parseInt((String) queryDocumentSnapshot.get(Constants.KEY_AGE));
                                    if (age != null) {
                                        binding.inputAge.setText(age.toString());
                                        preferenceManager.putInteger(Constants.KEY_AGE, age);
                                    }
                                }
                                if (queryDocumentSnapshot.get(Constants.KEY_DESCRIPTION) != null) {
                                    String description = (String) queryDocumentSnapshot.get(Constants.KEY_DESCRIPTION);
                                    if (description != null) {
                                        binding.inputDescription.setText(description);
                                    }
                                }
                                if (queryDocumentSnapshot.get(Constants.KEY_AGE_FOR) != null) {
                                    List<Double> values = (List<Double>) queryDocumentSnapshot.get(Constants.KEY_AGE_FOR);
                                    if (values != null) {
                                        binding.RangeSlider.setValues(values.get(0).floatValue(), values.get(1).floatValue());
                                        binding.ageForText.setText(values.get(0).intValue() + "-" + values.get(1).intValue());
                                        preferenceManager.putInteger(Constants.KEY_AGE_FOR_MIN, values.get(0).intValue());
                                        preferenceManager.putInteger(Constants.KEY_AGE_FOR_MAX, values.get(1).intValue());
                                    }
                                }
                                if (queryDocumentSnapshot.get(Constants.KEY_AGE) != null && queryDocumentSnapshot.get(Constants.KEY_DESCRIPTION) != null && queryDocumentSnapshot.get(Constants.KEY_AGE_FOR) != null) {
                                    binding.imageSave.setBackgroundResource(R.drawable.background_icon);
                                }
                                getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
                                binding.progressBar1.setVisibility(View.INVISIBLE);
                                binding.constraint.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

    public void changeItem(Integer g, String image) {
        // Remove the last item
        RecyclerView.ViewHolder viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(g);
        if (viewHolder != null) {
            imageAdapter.MyViewHolder itemViewHolder = (imageAdapter.MyViewHolder) viewHolder;
            byte[] bytes = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            itemViewHolder.imageView.setImageBitmap(bitmap);
        }
    }

    private String encodedImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void setListeners() {
        binding.imageSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        binding.imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String age = binding.inputAge.getText().toString();
                String desc = binding.inputDescription.getText().toString();
                List<Float> values = binding.RangeSlider.getValues();
                if(isInteger(age)) {
                    HashMap<String, Object> user = new HashMap<>();
                    user.put(Constants.KEY_AGE, age);
                    user.put(Constants.KEY_DESCRIPTION, desc);
                    user.put(Constants.KEY_AGE_FOR, values);
                    DocumentReference docRef = database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
                    docRef.update(user).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        preferenceManager.putInteger(Constants.KEY_AGE_FOR_MIN, values.get(0).intValue());
                                        preferenceManager.putInteger(Constants.KEY_AGE_FOR_MAX, values.get(1).intValue());
                                        binding.imageSave.setBackgroundResource(R.drawable.background_icon);
                                    }
                                }
                            });
                } else {
                    binding.inputAge.setError("Must be a number");
                }
            }
        });
        binding.RangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                List<Float> values = binding.RangeSlider.getValues();
                binding.ageForText.setText(values.get(0).intValue() + "-" + values.get(1).intValue());
            }
        });

    }


    private void loadUserDetails() {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
    }





    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }

    private void signOut() {
        showToast("Signing out...");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getActivity(), SignInActivity.class));
                    getActivity().finish();
                })
                .addOnFailureListener(e -> showToast("Unable to sign out"));
    }

    @Override
    public void onConversionClicked(User user) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}