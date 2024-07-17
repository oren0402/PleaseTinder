package com.example.pleasetinder.Register;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.pleasetinder.R;
import com.example.pleasetinder.RegisterActivity;
import com.example.pleasetinder.databinding.FragmentDescriptionBinding;
import com.example.pleasetinder.databinding.FragmentEmailAndPassBinding;


public class EmailAndPass extends Fragment {

    private FragmentEmailAndPassBinding binding;
    ImageButton imageButton;
    EditText editTextEmail;
    EditText editTextPass;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEmailAndPassBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imageButton = root.findViewById(R.id.imgButton);
        editTextEmail = root.findViewById(R.id.email_edittext);
        editTextPass = root.findViewById(R.id.enterpass_edittext);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = editTextEmail.getText().toString();
                String Pass = editTextPass.getText().toString();

                if (Email.isEmpty()) {
                    editTextEmail.setError("Must set an email");
                }
                if (!(Pass.length() > 7)) {
                    editTextPass.setError("Password must be 8 characters");
                    return;
                }
                ((RegisterActivity) getActivity()).switchFragment();
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