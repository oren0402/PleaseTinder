package com.example.pleasetinder.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pleasetinder.activities.BaseFragment;
import com.example.pleasetinder.activities.ChatActivity;
import com.example.pleasetinder.adapters.UsersAdapter;
import com.example.pleasetinder.databinding.FragmentNotificationsBinding;
import com.example.pleasetinder.listeners.UserListener;
import com.example.pleasetinder.models.User;
import com.example.pleasetinder.utilities.Constants;
import com.example.pleasetinder.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends BaseFragment implements UserListener {

    private FragmentNotificationsBinding binding;
    private PreferenceManager preferenceManager;
    Integer count = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        preferenceManager = new PreferenceManager(getActivity());
        getUsers();

        return root;
    }

    private void getUsers() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS_ACCEPTED)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();
                        Integer size = task.getResult().size();
                        if (size == 0) {
                            showErrorMessage();
                        }
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            count++;
                            if (queryDocumentSnapshot.getString(Constants.KEY_USER_REQUESTED).equals(preferenceManager.getString(Constants.KEY_USER_ID))) {
                                String otherUserID = queryDocumentSnapshot.getString(Constants.KEY_USER_ACCEPTED);
                                database.collection(Constants.KEY_COLLECTION_USERS).document(otherUserID).get().addOnCompleteListener(task1 -> {
                                    User user = new User();
                                    user.name = task1.getResult().getString(Constants.KEY_NAME);
                                    user.email = task1.getResult().getString(Constants.KEY_EMAIL);
                                    user.image = task1.getResult().getString(Constants.KEY_IMAGE);
                                    user.token = task1.getResult().getString(Constants.KEY_FCM_TOKEN);
                                    user.id = task1.getResult().getId();
                                    users.add(user);
                                    if (count == size) {
                                        if (users.size() > 0) {
                                            UsersAdapter usersAdapter = new UsersAdapter(users, this);
                                            binding.usersRecyclerView.setAdapter(usersAdapter);
                                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                                        } else {
                                            showErrorMessage();
                                        }
                                    }
                                });
                            } else if (queryDocumentSnapshot.getString(Constants.KEY_USER_ACCEPTED).equals(preferenceManager.getString(Constants.KEY_USER_ID))) {
                                String otherUserID = queryDocumentSnapshot.getString(Constants.KEY_USER_REQUESTED);
                                database.collection(Constants.KEY_COLLECTION_USERS).document(otherUserID).get().addOnCompleteListener(task1 -> {
                                    User user = new User();
                                    user.name = task1.getResult().getString(Constants.KEY_NAME);
                                    user.email = task1.getResult().getString(Constants.KEY_EMAIL);
                                    user.image = task1.getResult().getString(Constants.KEY_IMAGE);
                                    user.token = task1.getResult().getString(Constants.KEY_FCM_TOKEN);
                                    user.id = task1.getResult().getId();
                                    users.add(user);
                                    if (count == size) {
                                        if (users.size() > 0) {
                                            UsersAdapter usersAdapter = new UsersAdapter(users, this);
                                            binding.usersRecyclerView.setAdapter(usersAdapter);
                                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                                        } else {
                                            showErrorMessage();
                                        }
                                    }
                                });
                            } else {
                                if (count == size) {
                                    if (users.size() > 0) {
                                        UsersAdapter usersAdapter = new UsersAdapter(users, this);
                                        binding.usersRecyclerView.setAdapter(usersAdapter);
                                        binding.usersRecyclerView.setVisibility(View.VISIBLE);
                                    } else {
                                        showErrorMessage();
                                    }
                                }
                            }
                        }
                    } else {
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading) {
        if(isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void onUserClicked(User user) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}