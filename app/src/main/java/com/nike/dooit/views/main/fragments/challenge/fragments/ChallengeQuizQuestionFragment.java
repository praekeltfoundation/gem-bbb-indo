package com.nike.dooit.views.main.fragments.challenge.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nike.dooit.R;
import com.nike.dooit.models.Option;
import com.nike.dooit.models.Question;
import com.nike.dooit.views.main.fragments.challenge.adapters.ChallengeQuizOptionsListAdapter;
import com.nike.dooit.views.main.fragments.challenge.adapters.ChallengeQuizPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChallengeQuizQuestionFragment extends ListFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_QUESTION = "question";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private Question mQuestion = null;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView.Adapter mAdapter;

    @BindView(R.id.fragment_challengequizquestion_title)
    TextView title;

    @BindView(R.id.option_recycler_view)
    RecyclerView optionList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChallengeQuizQuestionFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ChallengeQuizQuestionFragment newInstance() {
        ChallengeQuizQuestionFragment fragment = new ChallengeQuizQuestionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static ChallengeQuizQuestionFragment newInstance(Question question) {
        ChallengeQuizQuestionFragment fragment = new ChallengeQuizQuestionFragment();
        Bundle args = new Bundle();
        args.putParcelable("question", question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mQuestion = getArguments().getParcelable(ARG_QUESTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challengequizquestion, container, false);
        ButterKnife.bind(this, view);
        title.setText(mQuestion.getText());
        optionList.setAdapter(new ChallengeQuizOptionsListAdapter(mQuestion, mListener));
        optionList.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Option item);
    }
}
