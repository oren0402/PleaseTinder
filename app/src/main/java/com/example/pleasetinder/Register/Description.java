package com.example.pleasetinder.Register;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.pleasetinder.R;
import com.example.pleasetinder.RegisterActivity;
import com.example.pleasetinder.databinding.FragmentDescriptionBinding;
import com.example.pleasetinder.databinding.FragmentHomeBinding;
import com.example.pleasetinder.ui.home.HomeViewModel;


public class Description extends Fragment {

    private FragmentDescriptionBinding binding;
    ImageButton imageButton;
    EditText editTextName;
    EditText editTextPlace;
    EditText editTextProf;
    EditText editTextDesc;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDescriptionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imageButton = root.findViewById(R.id.imgButton1);
        editTextName = root.findViewById(R.id.fullname);
        editTextPlace = root.findViewById(R.id.place);
        editTextProf = root.findViewById(R.id.profession);
        editTextDesc = root.findViewById(R.id.Description);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString();
                String Place = editTextPlace.getText().toString();
                String Prof = editTextProf.getText().toString();
                String Desc = editTextDesc.getText().toString();
                if (name.isEmpty()) {
                    editTextName.setError("Name must be mentioned");
                    return;
                }
                if (Prof.isEmpty()) {
                    editTextProf.setError("Profession must be mentioned");
                    return;
                }
                if (Place.isEmpty()) {
                    editTextPlace.setError("Place must be mentioned");
                    return;
                }
                if (Desc.length() < 50) {
                    editTextDesc.setError("Description must be more than 50 characters");
                    return;
                }
                ((RegisterActivity) getActivity()).newActivity();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}