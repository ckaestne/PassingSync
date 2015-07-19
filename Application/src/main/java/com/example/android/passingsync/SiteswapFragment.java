package com.example.android.passingsync;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.android.passingsync.pattern.AbstractPatternGenerator;

import java.util.ArrayList;
import java.util.List;


public class SiteswapFragment extends Fragment {
    private final List<TextView> textViewsA = new ArrayList<>();
    private final List<TextView> textViewsB = new ArrayList<>();
    private TextView mRHLabel;
    private TextView mLHLabel;
    private TableLayout patternDisplay;
    private TableRow passerBRow;

//    private OnFragmentInteractionListener mListener;
private TableRow passerARow;
    private TextView startText;
    private int displaySize = -1;


    public SiteswapFragment() {
        // Required empty public constructor
    }

    public static SiteswapFragment newInstance() {
        SiteswapFragment fragment = new SiteswapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);

//        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRHLabel = (TextView) view.findViewById(R.id.fr_rhLabel);
        mLHLabel = (TextView) view.findViewById(R.id.fr_lhLabel);
        patternDisplay = (TableLayout) view.findViewById(R.id.patternDisplayLayout);
        passerARow = (TableRow) view.findViewById(R.id.passerARow);
        passerBRow = (TableRow) view.findViewById(R.id.passerBRow);
        startText = (TextView) view.findViewById(R.id.startText);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_siteswap, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void setDisplay(AbstractPatternGenerator.Display display, AbstractPatternGenerator.Passer who) {
        if (displaySize != display.seqA.size())
            initDisplay(display);

        for (int i = 0; i < displaySize; i++) {
            textViewsA.get(i).setText("" + display.seqA.get(i));
            textViewsB.get(i).setText("" + display.seqB.get(i));

            int color = Color.BLACK;
            if (i == display.highlight)
                color = Color.RED;

            textViewsA.get(i).setTextColor(color);
            textViewsB.get(i).setTextColor(color);
        }

        if (who == AbstractPatternGenerator.Passer.A) {
            passerARow.setBackgroundColor(Color.GRAY);
            passerBRow.setBackgroundColor(Color.WHITE);
        } else {
            passerARow.setBackgroundColor(Color.WHITE);
            passerBRow.setBackgroundColor(Color.GRAY);
        }

    }

    public void setStart(AbstractPatternGenerator.StartPos start) {
        startText.setText(start.toString());
    }

    public void setStart(String start) {
        startText.setText(start);
    }

    private void initDisplay(AbstractPatternGenerator.Display display) {
        displaySize = display.seqA.size();
        passerARow.removeAllViews();
        passerBRow.removeAllViews();
        textViewsA.clear();
        textViewsB.clear();
        for (Character a : display.seqA) {
            TextView t = new TextView(getActivity());
            textViewsA.add(t);
            passerARow.addView(t);
            t = new TextView(getActivity());
            textViewsB.add(t);
            passerBRow.addView(t);
        }
    }

    public void resetSiteswap() {
        mRHLabel.setTextColor(Color.BLACK);
        mLHLabel.setTextColor(Color.BLACK);
        mRHLabel.setText("");
        mLHLabel.setText("");

    }

    public void setThrow(Character pass, AbstractPatternGenerator.Side hand) {
        mRHLabel.setTextColor(Color.BLACK);
        mLHLabel.setTextColor(Color.BLACK);
        if (hand == AbstractPatternGenerator.Side.RIGHT) {
            mRHLabel.setText("" + pass);
            mRHLabel.setTextColor(Color.RED);
        }
        if (hand == AbstractPatternGenerator.Side.LEFT) {
            mLHLabel.setText("" + pass);
            mLHLabel.setTextColor(Color.RED);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
