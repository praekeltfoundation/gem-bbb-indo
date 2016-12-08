package org.gem.indo.dooit.views.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.icu.util.Measure;
import android.support.annotation.StyleRes;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.gem.indo.dooit.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Rudolph Jacobs on 2016-12-08.
 */

public class WigglyEditText extends ConstraintLayout {
    /*************
     * Variables *
     *************/

    boolean labelShown = true;
    boolean messageShown = true;
    boolean placeholderShown = true;


    /************
     * Bindings *
     ************/

    Unbinder unbinder = null;

    @BindView(R.id.label)
    TextView labelView;

    @BindView(R.id.placeholder)
    TextView placeholderView;

    @BindView(R.id.edit_box)
    EditText editBox;

    @BindView(R.id.message)
    TextView messageView;


    /****************
     * Constructors *
     ****************/

    public WigglyEditText(Context context) {
        super(context);
        initViews(context);
        checkVisibility();
    }

    public WigglyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context, attrs);
        checkVisibility();
    }

    public WigglyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context, attrs);
        checkVisibility();
    }

    private void initViews(Context context) {
        View v = View.inflate(context, R.layout.wiggly_edit_text, this);
        unbinder = ButterKnife.bind(this, v);
    }

    private void initViews(Context context, AttributeSet attrs) {
        initViews(context);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WigglyEditText);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.WigglyEditText_label_style:
                    @StyleRes int labelStyle = a.getResourceId(attr, R.style.AppTheme_TextView_Form_Label_Yellow);
                    labelView.setTextAppearance(context, labelStyle);
                    break;
                case R.styleable.WigglyEditText_label_text:
                    String labelText = a.getString(attr);
                    if (!TextUtils.isEmpty(labelText)) {
                        labelView.setText(labelText);
                    }
                    break;
                case R.styleable.WigglyEditText_edit_style:
                    @StyleRes int editStyle = a.getResourceId(attr, R.style.AppTheme_TextView_Regular);
                    editBox.setTextAppearance(context, editStyle);
                    break;
                case R.styleable.WigglyEditText_edit_text:
                    String editText = a.getString(attr);
                    if (!TextUtils.isEmpty(editText)) {
                        editBox.setText(editText);
                    }
                    break;
                case R.styleable.WigglyEditText_placeholder_style:
                    @StyleRes int placeholderStyle = a.getResourceId(attr, R.style.AppTheme_TextView_Regular);
                    placeholderView.setTextAppearance(context, placeholderStyle);
                    break;
                case R.styleable.WigglyEditText_placeholder_text:
                    String placeholderText = a.getString(attr);
                    if (!TextUtils.isEmpty(placeholderText)) {
                        placeholderView.setText(placeholderText);
                    }
                    break;
                case R.styleable.WigglyEditText_message_style:
                    @StyleRes int messageStyle = a.getResourceId(attr, R.style.AppTheme_TextView_Regular);
                    messageView.setTextAppearance(context, messageStyle);
                    break;
                case R.styleable.WigglyEditText_message_text:
                    String messageText = a.getString(attr);
                    if (!TextUtils.isEmpty(messageText)) {
                        messageView.setText(messageText);
                    }
                    break;

                // Visibility
                case R.styleable.WigglyEditText_label_shown:
                    labelShown = a.getBoolean(attr, true);
                    break;
                case R.styleable.WigglyEditText_message_shown:
                    messageShown = a.getBoolean(attr, true);
                    break;
                case R.styleable.WigglyEditText_placeholder_shown:
                    placeholderShown = a.getBoolean(attr, true);
                    break;
            }
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void checkVisibility() {
        updateLabelVisibility();
        updateMessageVisibility();
        updatePlaceholderVisibility();
        requestLayout();
    }

    @OnTextChanged(R.id.edit_box)
    protected void onEditChanged(CharSequence text) {
        updatePlaceholderVisibility();
        requestLayout();
    }


    /***********
     * Getters *
     ***********/

    public EditText getEditBox() {
        return editBox;
    }

    public boolean isLabelShown() {
        return labelShown;
    }

    public TextView getLabelView() {
        return labelView;
    }

    public boolean isMessageShown() {
        return messageShown;
    }

    public TextView getMessageView() {
        return messageView;
    }

    public boolean isPlaceholderShown() {
        return placeholderShown;
    }

    public TextView getPlaceholderView() {
        return placeholderView;
    }

    public String getEditText() {
        return editBox.getText().toString();
    }

    public String getLabelText() {
        return (String) labelView.getText();
    }

    public String getMessageText() {
        return (String) messageView.getText();
    }

    public String getPlaceholderText() {
        return (String) placeholderView.getText();
    }


    /***********
     * Setters *
     ***********/

    public void setEditBox(EditText editBox) {
        this.editBox = editBox;
    }

    public void setLabelShown(boolean labelShown) {
        this.labelShown = labelShown;
        updateLabelVisibility();
    }

    public void setLabelView(TextView labelView) {
        this.labelView = labelView;
    }

    public void setMessageShown(boolean messageShown) {
        this.messageShown = messageShown;
        updateMessageVisibility();
    }

    public void setMessageView(TextView messageView) {
        this.messageView = messageView;
    }

    public void setPlaceholderShown(boolean placeholderShown) {
        this.placeholderShown = placeholderShown;
        updatePlaceholderVisibility();
    }

    public void setPlaceholderView(TextView placeholderView) {
        this.placeholderView = placeholderView;
    }

    public void setEditText(String text) {
        editBox.setText(text);
        updatePlaceholderVisibility();
    }

    public void setLabelText(String text) {
        labelView.setText(text);
        updateLabelVisibility();
    }

    public void setMessageText(String text) {
        messageView.setText(text);
        updateMessageVisibility();
    }

    public void setPlaceholderText(String text) {
        placeholderView.setText(text);
        updatePlaceholderVisibility();
    }


    /******************
     * Update methods *
     ******************/


    @OnTextChanged(R.id.label)
    protected void updateLabelVisibility() {
        if (labelShown && !TextUtils.isEmpty(labelView.getText())) {
            labelView.setVisibility(VISIBLE);
        } else {
            labelView.setVisibility(GONE);
        }
    }


    @OnTextChanged(R.id.message)
    protected void updateMessageVisibility() {
        if (messageShown && !TextUtils.isEmpty(messageView.getText())) {
            messageView.setVisibility(VISIBLE);
        } else {
            messageView.setVisibility(GONE);
        }
    }

    @OnTextChanged({R.id.edit_box, R.id.placeholder})
    protected void updatePlaceholderVisibility() {
        if (placeholderShown && !TextUtils.isEmpty(placeholderView.getText()) &&
                TextUtils.isEmpty(editBox.getText())) {
            placeholderView.setVisibility(VISIBLE);
        } else {
            placeholderView.setVisibility(GONE);
        }
    }
}
