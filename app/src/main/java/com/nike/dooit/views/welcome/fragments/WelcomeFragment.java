package com.nike.dooit.views.welcome.fragments;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nike.dooit.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeFragment extends Fragment {
    private static final String ANIM_URI = "anim_uri";
    private static final String IMAGE_RESOURCE = "image_resource";
    private static final String TEXT_RESOURCE = "text_resource";

    @BindView(R.id.fragment_welcome_animation)
    SimpleDraweeView animView;

    @BindView(R.id.fragment_welcome_image)
    ImageView imageView;

    @BindView(R.id.fragment_welcome_text)
    TextView textView;

    private String animUri;

    @DrawableRes
    private int resource;

    private int textResource;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WelcomeFragment.
     */
    public static WelcomeFragment newInstance(String animUri, @DrawableRes int imageRes, int textRes) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        args.putString(ANIM_URI, animUri);
        args.putInt(IMAGE_RESOURCE, imageRes);
        args.putInt(TEXT_RESOURCE, textRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            animUri = getArguments().getString(ANIM_URI);
            resource = getArguments().getInt(IMAGE_RESOURCE);
            textResource = getArguments().getInt(TEXT_RESOURCE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.bind(this, view);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(animUri)
                .setAutoPlayAnimations(true)
                .build();
        animView.setController(controller);

        imageView.setImageResource(resource);
        imageView.setVisibility(View.GONE);

        textView.setText(textResource);

        return view;
    }

}
