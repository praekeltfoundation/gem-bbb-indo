package com.nike.dooit.views.main.fragments.challenge.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nike.dooit.R;
import com.nike.dooit.models.Question;
import com.nike.dooit.views.main.fragments.challenge.dummy.DummyContent.DummyItem;
import com.nike.dooit.views.main.fragments.challenge.fragments.ChallengeQuizQuestionFragment;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link ChallengeQuizQuestionFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ChallengeQuestionRecyclerViewAdapter extends RecyclerView.Adapter<ChallengeQuestionRecyclerViewAdapter.ViewHolder> {

    private final List<Question> mValues;
    private final ChallengeQuizQuestionFragment.OnListFragmentInteractionListener mListener;

    public ChallengeQuestionRecyclerViewAdapter(List<Question> items, ChallengeQuizQuestionFragment.OnListFragmentInteractionListener listener) {
        System.out.println("Creating recycler");
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_challengequizquestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(holder.mItem.getText());
        holder.mProgressCounterView.setText(String.format("Question %d/%d", 1, getItemCount()));
//            mOptionListView = (LinearLayout) view.findViewById(R.id.fragment_challengequizquestion_list);
//            mCheckButtonView = (Button) view.findViewById(R.id.fragment_challengequizquestion_checkbutton);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mProgressCounterView;
        public final LinearLayout mOptionListView;
        public final Button mCheckButtonView;
        public Question mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mTitleView = (TextView) view.findViewById(R.id.fragment_challengequizquestion_title);
            mProgressCounterView = (TextView) view.findViewById(R.id.fragment_challengequizquestion_progresscounter);
            mOptionListView = (LinearLayout) view.findViewById(R.id.fragment_challengequizquestion_list);
            mCheckButtonView = (Button) view.findViewById(R.id.fragment_challengequizquestion_checkbutton);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
