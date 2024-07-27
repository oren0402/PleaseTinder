package com.example.pleasetinder.ui.dashboard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.example.pleasetinder.CardStackAdapter;
import com.example.pleasetinder.ItemModel;
import com.example.pleasetinder.R;
import com.example.pleasetinder.databinding.FragmentDashboardBinding;
import com.example.pleasetinder.utilities.Constants;
import com.example.pleasetinder.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    FirebaseFirestore db;
    private static final String TAG = "MainActivity";
    private CardStackLayoutManager manager;
    CardStackView cardStackView;
    ImageView imageView;
    private CardStackAdapter adapter;
    private PreferenceManager preferenceManager;
    List<Bitmap> Images = new ArrayList<>();
    List<String> Names = new ArrayList<>();
    List<String> Descriptions = new ArrayList<>();
    List<String> Ages = new ArrayList<>();
    List<String> UsersID = new ArrayList<>();
    Integer atUserNumber = 0;
    Integer cardsSeen = 0;
    List<ItemModel> list;
    Integer count = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashbordViewModel dashboardViewModel = new ViewModelProvider(this).get(DashbordViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        cardStackView = root.findViewById(R.id.card_stack_view);
        preferenceManager = new PreferenceManager(getActivity());
        db = FirebaseFirestore.getInstance();
        imageView = binding.imageView;
        setUpCards();

        return root;
    }

    private void setUpCards() {
        manager = new CardStackLayoutManager(getActivity(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                if (ratio == 0) {
                    imageView.setVisibility(View.GONE);
                    imageView.setAlpha(ratio);
                }
                if (direction == Direction.Right) {
                    imageView.setImageResource(R.drawable.check);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setAlpha(ratio);

                }
                if (direction == Direction.Left) {
                    imageView.setImageResource(R.drawable.close);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setAlpha(ratio);
                }
            }

            @Override
            public void onCardSwiped(Direction direction) {
                imageView.setVisibility(View.GONE);
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                String id = preferenceManager.getString(Constants.KEY_USER_ID) + UsersID.get(cardsSeen);
                if (direction == Direction.Right){
                    HashMap<String, Object> user = new HashMap<>();
                    user.put(Constants.KEY_NAME, Names.get(cardsSeen));
                    user.put(Constants.KEY_USER_SEEN, preferenceManager.getString(Constants.KEY_NAME));
                    db.collection(Constants.KEY_COLLECTION_USERS_SEEN).document(id).set(user);
                    db.collection(Constants.KEY_COLLECTION_USERS_REQUESTED).
                            whereEqualTo(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME))
                            .whereEqualTo(Constants.KEY_USER_SEEN , Names.get(cardsSeen))
                            .get()
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful() && task.getResult() != null
                                        && task.getResult().getDocuments().size() > 0) {
                                    db.collection(Constants.KEY_COLLECTION_USERS_REQUESTED).document(task.getResult().getDocuments().get(0).getId()).delete();
                                    HashMap<String, Object> usersID = new HashMap<>();
                                    usersID.put(Constants.KEY_USER_REQUESTED, UsersID.get(cardsSeen));
                                    usersID.put(Constants.KEY_USER_ACCEPTED, preferenceManager.getString(Constants.KEY_USER_ID));
                                    db.collection(Constants.KEY_COLLECTION_USERS_ACCEPTED).document(id).set(usersID);
                                } else if (task.isSuccessful() && task.getResult().getDocuments().size() == 0) {
                                    db.collection(Constants.KEY_COLLECTION_USERS_REQUESTED).document(id).set(user);
                                }
                                cardsSeen++;
                                if (Images.size() > atUserNumber) {
                                    list.remove(0);
                                    list.add(new ItemModel(Images.get(atUserNumber), Names.get(atUserNumber), Ages.get(atUserNumber), Descriptions.get(atUserNumber)));
                                    atUserNumber++;
                                    adapter.notifyDataSetChanged();
                                }
                            });

                }
                if (direction == Direction.Left){
                    HashMap<String, Object> user = new HashMap<>();
                    user.put(Constants.KEY_NAME, Names.get(cardsSeen));
                    user.put(Constants.KEY_USER_SEEN, preferenceManager.getString(Constants.KEY_NAME));
                    db.collection(Constants.KEY_COLLECTION_USERS_SEEN).document(id).set(user);
                    db.collection(Constants.KEY_COLLECTION_USERS_REQUESTED).
                            whereEqualTo(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME))
                            .whereEqualTo(Constants.KEY_USER_SEEN , Names.get(cardsSeen))
                            .get()
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful() && task.getResult() != null
                                        && task.getResult().getDocuments().size() > 0) {
                                    db.collection(Constants.KEY_COLLECTION_USERS_REQUESTED).document(task.getResult().getDocuments().get(0).getId()).delete();
                                }
                                cardsSeen++;
                                if (Images.size() > atUserNumber) {
                                    list.remove(0);
                                    list.add(new ItemModel(Images.get(atUserNumber), Names.get(atUserNumber), Ages.get(atUserNumber), Descriptions.get(atUserNumber)));
                                    atUserNumber++;
                                    adapter.notifyDataSetChanged();
                                }
                            });

                }

                // Paginating

            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
                imageView.setVisibility(View.GONE);
            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {

            }
        });
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        addList();
    }


    private void addList() {
        List<ItemModel> items = new ArrayList<>();
        Integer ageMin = preferenceManager.getInteger(Constants.KEY_AGE_FOR_MIN);
        Integer ageMax = preferenceManager.getInteger(Constants.KEY_AGE_FOR_MAX);
        db.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Integer size = task.getResult().size();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Access the document data
                                List<Double> values = (List<Double>) document.get(Constants.KEY_AGE_FOR);
                                String age = (String) document.get(Constants.KEY_AGE);
                                String string = (String) document.get(Constants.KEY_IMAGE_BIG);
                                String description = (String) document.get(Constants.KEY_DESCRIPTION);
                                String name = (String) document.get(Constants.KEY_NAME);
                                String otherUserID = document.getId();
                                db.collection(Constants.KEY_COLLECTION_USERS_SEEN)
                                        .whereEqualTo(Constants.KEY_NAME, name)
                                        .whereEqualTo(Constants.KEY_USER_SEEN, preferenceManager.getString(Constants.KEY_NAME))
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            count++;
                                            if (task1.isSuccessful() && task1.getResult() != null
                                                    && task1.getResult().getDocuments().size() > 0) {

                                            } else {
                                                if (age != null && string != null && description != null && name != null) {
                                                    if (isBetween(Integer.parseInt(age), ageMin, ageMax)) {
                                                        if (values != null) {
                                                            if (isBetween(preferenceManager.getInteger(Constants.KEY_AGE), values.get(0).intValue(), values.get(1).intValue())) {
                                                                if (document.getId().equals(preferenceManager.getString(Constants.KEY_USER_ID))) {

                                                                } else {
                                                                    Ages.add(age);
                                                                    byte[] bytes = Base64.decode(string, Base64.DEFAULT);
                                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                                    Images.add(bitmap);
                                                                    Descriptions.add(description);
                                                                    Names.add(name);
                                                                    UsersID.add(otherUserID);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (count == size) {
                                                if (Images.size() >= 10) {
                                                    for (int i = 0; i < 10; i++) {
                                                        items.add(new ItemModel(Images.get(i), Names.get(i), Ages.get(i), Descriptions.get(i)));
                                                        atUserNumber++;
                                                    }
                                                } else {
                                                    for (int i = 0; i < Images.size(); i++) {
                                                        items.add(new ItemModel(Images.get(i), Names.get(i), Ages.get(i), Descriptions.get(i)));
                                                        atUserNumber++;
                                                    }
                                                }
                                                list = items;
                                                adapter = new CardStackAdapter(list);
                                                cardStackView.setLayoutManager(manager);
                                                cardStackView.setAdapter(adapter);
                                                cardStackView.setItemAnimator(new DefaultItemAnimator());
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private static boolean isBetween(int number, int lower, int upper) {
        return number > lower && number < upper;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}