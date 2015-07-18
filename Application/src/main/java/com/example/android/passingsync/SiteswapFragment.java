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
import android.widget.TextView;


public class SiteswapFragment extends Fragment {
    private TextView mRHLabel;
    private TextView mLHLabel;

//    private OnFragmentInteractionListener mListener;

    public static SiteswapFragment newInstance() {
        SiteswapFragment fragment = new SiteswapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SiteswapFragment() {
        // Required empty public constructor
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

    public void resetSiteswap() {
        mRHLabel.setTextColor(Color.BLACK);
        mLHLabel.setTextColor(Color.BLACK);
        mRHLabel.setText("");
        mLHLabel.setText("");

    }

    public void setThrow(Character pass, int hand) {
        mRHLabel.setTextColor(Color.BLACK);
        mLHLabel.setTextColor(Color.BLACK);
        if (hand == 0) {
            mRHLabel.setText("" + pass);
            mRHLabel.setTextColor(Color.RED);
        }
        if (hand == 2) {
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
