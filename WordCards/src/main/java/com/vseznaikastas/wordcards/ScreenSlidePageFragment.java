package com.vseznaikastas.wordcards;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.vseznaikastas.wordcards.flipanimation.AnimationFactory;


/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 */
public class ScreenSlidePageFragment extends Fragment implements FragmentManager.OnBackStackChangedListener {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;

    /**
     * A handler object, used for deferring UI operations.
     */
    private Handler mHandler = new Handler();

    /**
     * Whether or not we're showing the back of the card (otherwise showing the front).
     */
    private boolean mShowingBack = false;
    private SQLiteAdapter mySQLiteAdapter;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */

    public static ScreenSlidePageFragment create(int pageNumber) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_screen_slide_page, container, false);

        final ViewAnimator viewAnimator1 = (ViewAnimator)rootView.findViewById(R.id.viewFlipper1);
        //set flip animation when click
        viewAnimator1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               AnimationFactory.flipTransition(viewAnimator1, AnimationFactory.FlipDirection.LEFT_RIGHT);
            }
        }
        );

        getFragmentManager().addOnBackStackChangedListener(this);

        // Set the title view to show the page number.
        ((TextView) rootView.findViewById(R.id.text_page_number)).setText(
                getString(R.string.title_template_step, mPageNumber + 1));

/*
        //Click to card
        View nv = rootView.findViewById(R.id.container);
        nv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //flipCard(view);
                ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
                anim.setDuration(1000);
                anim.start();
                AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(),
                        R.animator.card_flip_left_in);
                set.setTarget(view);
                set.start();
            }
        });
*/

        TextView listContent = (TextView)rootView.findViewById(R.id.fragment_text_1);
        TextView listContent1 = (TextView)rootView.findViewById(R.id.fragment_text_2);


        mySQLiteAdapter = new SQLiteAdapter(getActivity());
        mySQLiteAdapter.openToRead();
        // Cursor cursor = mySQLiteAdapter.getAll();
        Word word = mySQLiteAdapter.getItem(getPageNumber());

        //cursor.moveToPosition(getPageNumber());

        //String contentRead = cursor.getString(SQLiteAdapter.CONTENT_COLUMN);
        //String contentInt = cursor.getString(SQLiteAdapter.ID_COLUMN);
        mySQLiteAdapter.close();

        listContent.setText(word.getWord());
        listContent1.setText(word.getTranslate());
        return rootView;
    }


    @Override
    public void onBackStackChanged() {
        mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}

