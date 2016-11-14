package com.nike.dooit.views.main.fragments.challenge.fragments;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.interfaces.DraweeHierarchy;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nike.dooit.R;
import com.nike.dooit.models.challenge.BaseChallenge;
import com.nike.dooit.models.challenge.FreeformChallenge;
import com.nike.dooit.models.challenge.QuizChallenge;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChallengeRegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeRegisterFragment extends Fragment {
    private static final String ARG_CHALLENGE = "challenge";

    private BaseChallenge challenge;
    private Unbinder unbinder = null;

    @BindView(R.id.fragment_challenge_register_image)
    SimpleDraweeView topImage;

    @BindView(R.id.fragment_challenge_sub_title_text_view)
    TextView title;

    @BindView(R.id.fragment_challenge_name_text_view)
    TextView name;

    @BindView(R.id.fragment_challenge_expire_date_text_view)
    TextView date;

    @BindView(R.id.fragment_challenge_instruction_text_vew)
    TextView instruction;

    @BindView(R.id.fragment_challenge_t_c_text_view)
    TextView tc;

    @BindView(R.id.fragment_challenge_register_button)
    Button register;

    public ChallengeRegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChallengeRegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChallengeRegisterFragment newInstance() {
        ChallengeRegisterFragment fragment = new ChallengeRegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            challenge = getArguments().getParcelable(ARG_CHALLENGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_register, container, false);
        unbinder = ButterKnife.bind(this, view);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        name.setText(challenge.getName());
        date.setText(challenge.getDeactivationDate().toLocalDateTime().toString("yyyy-MM-dd HH:mm"));
        topImage.setImageURI(challenge.getImageURL());
    }

    @OnClick(R.id.fragment_challenge_register_button)
    void startChallenge() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment;

        if (challenge instanceof QuizChallenge) {
            if (((QuizChallenge) challenge).getQuestions() != null &&
                    ((QuizChallenge) challenge).getQuestions().size() > 0) {
                fragment = ChallengeQuizFragment.newInstance((QuizChallenge) challenge);
            } else {
                fragment = ChallengeNoneFragment.newInstance(getString(R.string.challenge_quiz_no_questions));
            }
        } else if (challenge instanceof FreeformChallenge) {
            fragment = ChallengeFreeformFragment.newInstance((FreeformChallenge) challenge);
        } else {
            throw new RuntimeException("Invalid challenge type provided");
        }

        //ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.replace(R.id.fragment_challenge_container, fragment);

        ft.commit();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
