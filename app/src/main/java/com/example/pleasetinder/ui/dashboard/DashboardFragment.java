package com.example.pleasetinder.ui.dashboard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

import com.example.pleasetinder.CardStackAdapter;
import com.example.pleasetinder.CardStackCallback;
import com.example.pleasetinder.ItemModel;
import com.example.pleasetinder.R;
import com.example.pleasetinder.databinding.FragmentDashboardBinding;
import com.example.pleasetinder.utilities.Constants;
import com.example.pleasetinder.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.N;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    FirebaseFirestore db;
    private static final String TAG = "MainActivity";
    private CardStackLayoutManager manager;
    CardStackView cardStackView;
    private CardStackAdapter adapter;
    private PreferenceManager preferenceManager;
    List<Bitmap> Images = new ArrayList<>();
    List<String> Names = new ArrayList<>();
    List<String> Descriptions = new ArrayList<>();
    List<String> Ages = new ArrayList<>();
    Integer atUserNumber = 0;
    List<ItemModel> list;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashbordViewModel dashboardViewModel = new ViewModelProvider(this).get(DashbordViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        cardStackView = root.findViewById(R.id.card_stack_view);
        preferenceManager = new PreferenceManager(getActivity());
        db = FirebaseFirestore.getInstance();
        setUpCards();

        return root;
    }

    private void setUpCards() {
        manager = new CardStackLayoutManager(getActivity(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                if (Images.size() > atUserNumber) {
                    list.remove(0);
                    list.add(new ItemModel(Images.get(atUserNumber), Names.get(atUserNumber), Ages.get(atUserNumber), Descriptions.get(atUserNumber)));
                    adapter.notifyDataSetChanged();
                }
                if (direction == Direction.Right){
                    Toast.makeText(getActivity(), "Direction Right", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Top){
                    Toast.makeText(getActivity(), "Direction Top", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Left){
                    Toast.makeText(getActivity(), "Direction Left", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Bottom){
                    Toast.makeText(getActivity(), "Direction Bottom", Toast.LENGTH_SHORT).show();
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
        manager.setDirections(Direction.FREEDOM);
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
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Access the document data
                                String age = (String) document.get(Constants.KEY_AGE);
                                if (isBetween(Integer.parseInt(age), ageMin, ageMax)) {
                                    Ages.add(age);
                                        // Open an InputStream from the Uri
                                    String string = (String) document.get(Constants.KEY_IMAGE_BIG);
                                    byte[] bytes = Base64.decode(string, Base64.DEFAULT);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    Images.add(bitmap);
                                    // Close the InputStream
                                    String description = (String) document.get(Constants.KEY_DESCRIPTION);
                                    Descriptions.add(description);
                                    String name = (String) document.get(Constants.KEY_NAME);
                                    Names.add(name);
                                    Log.d("chopping", Names.toString());
                                }
                            }
                            Log.d("chopping", Images.toString());
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