package com.bangalorecomputers.datetimeedit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DateTimeEdit extends FrameLayout {
    private LinearLayout mRootView;
    private TextView tvHour;
    private TextView tvMinute;

    private EditText edHour;
    private EditText edMinute;
    private EditText edSecond;

    private OnChangeListener mOnChangeListener;
    private boolean bAllowChanges= true;
    private boolean mRestrictedMode= true;

    private int mColorAccent= 0;
    private int mColorPrimary= 0;

    private int mLabelViewID= 0;
    private int mPrevViewID= 0;
    private int mNextViewID= 0;

    private boolean showHours= true;
    private boolean showMinutes= true;
    private boolean showSeconds= false;

    private float textSize;
    private int textColor;
    private boolean textBold;
    private int textWidth;
    private int textHeight;

    public interface OnChangeListener {
        void onChange(View view);
    }

    @TargetApi(21)
    public DateTimeEdit(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs, defStyleAttr, defStyleRes);
    }
    public DateTimeEdit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr, 0);
    }

    public DateTimeEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0, 0);
    }

    public DateTimeEdit(Context context) {
        super(context);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        if(direction==FOCUS_UP) {
            edSecond.requestFocus();
        } else {
            edHour.requestFocus();
        }
        return true;
    }

    public void setValues(int hours, int minutes, int seconds) {
        try {
            bAllowChanges= false;
            edHour.setText(""+hours);
            edMinute.setText(""+minutes);
            edSecond.setText(""+seconds);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bAllowChanges= true;
        }
    }

    public int getHour() {
        try {
            String text= edHour.getText().toString().trim();
            text= TextUtils.isEmpty(text) || TextUtils.equals(text, ".")?"0":text;
            return Integer.parseInt(text);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getMinute() {
        try {
            String text= edMinute.getText().toString().trim();
            text= TextUtils.isEmpty(text) || TextUtils.equals(text, ".")?"0":text;
            return Integer.parseInt(text);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getSecond() {
        try {
            String text= edSecond.getText().toString().trim();
            text= TextUtils.isEmpty(text) || TextUtils.equals(text, ".")?"0":text;
            return Integer.parseInt(text);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setVisibility(boolean hours, boolean minutes, boolean seconds) {
        showHours= hours;
        showMinutes= minutes;
        showSeconds= seconds;
        resetVisibility();
    }

    public void setRestrictedMode(boolean restrictedMode) {
        mRestrictedMode= restrictedMode;
    }

    public void setWidthDP(int width) {
        if(width== ViewGroup.LayoutParams.MATCH_PARENT||width== ViewGroup.LayoutParams.WRAP_CONTENT) {
            LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) edHour.getLayoutParams();
            params.width= width;
            edHour.setLayoutParams(params);

            params= (LinearLayout.LayoutParams) edMinute.getLayoutParams();
            params.width= width;
            edMinute.setLayoutParams(params);

            params= (LinearLayout.LayoutParams) edSecond.getLayoutParams();
            params.width= width;
            edSecond.setLayoutParams(params);
        } else {
            width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources().getDisplayMetrics());
            edHour.setWidth(width);
            edMinute.setWidth(width);
            edSecond.setWidth(width);
        }
    }

    public void setGravity(int gravity) {
        edHour.setGravity(gravity);
        edMinute.setGravity(gravity);
        edSecond.setGravity(gravity);
    }

    public void setPadding(int left, int top, int right, int bottom) {
        edHour.setPadding(left, top, right, bottom);
        edMinute.setPadding(left, top, right, bottom);
        edSecond.setPadding(left, top, right, bottom);
    }

    private void initColors(Context context) {
        try {
            TypedValue typedValue= new TypedValue();
            TypedArray a= context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
            mColorAccent= a.getColor(0, 0);
            a.recycle();

            a= context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimaryDark});
            mColorPrimary= a.getColor(0, 0);
            a.recycle();
        } catch (Exception e) {
            //ignore
        }
    }

    private void setLabelColor(boolean set) {
        try {
            if(mLabelViewID == 0 || mColorAccent == 0) {
                return;
            }
            TextView mLabelView= ((Activity) getContext()).getWindow().getDecorView().findViewById(mLabelViewID);
            if(mLabelView!=null) {
                mLabelView.setTextColor(set ? mColorAccent : mColorPrimary);
            }
        } catch (Exception e) {
            //ignore
        }
    }
    private void initView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setFocusable(true);
        mRootView = (LinearLayout) inflate(getContext(), R.layout.datetimeedit, null);
        addView(mRootView);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DateTimeEdit, defStyleAttr, defStyleRes);
        mLabelViewID= a.getResourceId(R.styleable.DateTimeEdit_label, 0);
        mPrevViewID= a.getResourceId(R.styleable.DateTimeEdit_previousFocus, 0);
        mNextViewID= a.getResourceId(R.styleable.DateTimeEdit_nextFocus, 0);

        showHours= a.getBoolean(R.styleable.DateTimeEdit_showHours, true);
        showMinutes= a.getBoolean(R.styleable.DateTimeEdit_showMinutes, true);
        showSeconds= a.getBoolean(R.styleable.DateTimeEdit_showSeconds, false);

        textSize= a.getDimensionPixelSize(R.styleable.DateTimeEdit_textSize, 0);
        textColor= a.getColor(R.styleable.DateTimeEdit_textColor, 0);
        textBold= a.getBoolean(R.styleable.DateTimeEdit_textBold, false);
        textWidth= a.getDimensionPixelSize(R.styleable.DateTimeEdit_textWidth, 0);
        textHeight= a.getDimensionPixelSize(R.styleable.DateTimeEdit_textHeight, 0);

        a.recycle();

        initColors(context);

        for(int i=0; i<mRootView.getChildCount(); i++) {
            View view= mRootView.getChildAt(i);
            if (view instanceof EditText) {
                if(edHour==null) {
                    edHour= (EditText) view;
                } else if(edMinute==null) {
                    edMinute= (EditText) view;
                } else if(edSecond==null) {
                    edSecond= (EditText) view;
                }
            } else if (view instanceof TextView) {
                if(tvHour==null) {
                    tvHour= (TextView) view;
                } else if(tvMinute==null) {
                    tvMinute= (TextView) view;
                }
            }
        }

        resetVisibility();

        if(textSize > 0) {
            edHour.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            edMinute.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            edSecond.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        } else {
            edHour.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            edMinute.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            edSecond.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        }
        if(textColor != 0) {
            tvHour.setTextColor(textColor);
            tvMinute.setTextColor(textColor);
            edHour.setTextColor(textColor);
            edMinute.setTextColor(textColor);
            edSecond.setTextColor(textColor);
        }
        edHour.setTypeface(null, textBold?Typeface.BOLD:Typeface.NORMAL);
        edMinute.setTypeface(null, textBold?Typeface.BOLD:Typeface.NORMAL);
        edSecond.setTypeface(null, textBold?Typeface.BOLD:Typeface.NORMAL);


        OnFocusChangeListener mFocusChangeListener= (v, hasFocus)->{
            setLabelColor(hasFocus);
        };
        edHour.setOnFocusChangeListener(mFocusChangeListener);
        edMinute.setOnFocusChangeListener(mFocusChangeListener);
        edSecond.setOnFocusChangeListener(mFocusChangeListener);

        LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) edHour.getLayoutParams();
        params.width= textWidth > 0?textWidth:80;
        params.height= textWidth > 0?textWidth:LinearLayout.LayoutParams.MATCH_PARENT;
        edHour.setLayoutParams(params);

        params= (LinearLayout.LayoutParams) edMinute.getLayoutParams();
        params.width= textWidth > 0?textWidth:80;
        params.height= textWidth > 0?textWidth:LinearLayout.LayoutParams.MATCH_PARENT;
        edMinute.setLayoutParams(params);

        params= (LinearLayout.LayoutParams) edSecond.getLayoutParams();
        params.width= textWidth > 0?textWidth:80;
        params.height= textWidth > 0?textWidth:LinearLayout.LayoutParams.MATCH_PARENT;
        edSecond.setLayoutParams(params);

        edHour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!bAllowChanges) {
                    return;
                }
                if(mRestrictedMode) {
                    String str = edHour.getText().toString();
                    int val = Math.abs(Integer.parseInt(TextUtils.isEmpty(str) ? "0" : str));
                    if (str.length() >= 2 || val > 2) {
                        bAllowChanges = false;
                        edHour.setText(String.valueOf(val));
                        bAllowChanges = true;
                        if (showMinutes) {
                            edMinute.requestFocus();
                        } else if (showSeconds) {
                            edSecond.requestFocus();
                        } else {
                            moveToNextFocus();
                        }
                    }
                }
                onChange();
            }
        });
        edHour.setOnKeyListener((v, keyCode, event)->{
            if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
                if(edHour.getText().toString().isEmpty()) {
                    moveToPrevFocus();
                }
            }
            return false;
        });


        edMinute.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!bAllowChanges) {
                    return;
                }

                if(mRestrictedMode) {
                    String str = edMinute.getText().toString();
                    int val = Math.abs(Integer.parseInt(TextUtils.isEmpty(str) ? "0" : str));
                    if (str.length() >= 2 || (val >= 6 && val <= 9)) {
                        bAllowChanges = false;
                        edMinute.setText(String.valueOf(val));
                        bAllowChanges = true;
                        if (showSeconds) {
                            edSecond.requestFocus();
                        } else {
                            moveToNextFocus();
                        }
                    }
                }
                onChange();
            }
        });
        edMinute.setOnKeyListener((v, keyCode, event)->{
            if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
                if(edMinute.getText().toString().isEmpty()) {
                    if(showHours) {
                        edHour.requestFocus();
                    } else {
                        moveToPrevFocus();
                    }
                }
            }
            return false;
        });

        edSecond.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!bAllowChanges) {
                    return;
                }
                if(mRestrictedMode) {
                    String str = edSecond.getText().toString();
                    if (str.contains(".")) {
                        float val = Math.abs(Float.parseFloat(TextUtils.isEmpty(str) || TextUtils.equals(".", str) ? "0" : str));
                        String[] temp = str.split("\\.");
                        if (str.length() >= 2 || (val >= 6 && val <= 9)) {
                            bAllowChanges = false;
                            edSecond.setText(String.valueOf(val));
                            bAllowChanges = true;
                            moveToNextFocus();

                        }
                    }
                }
                onChange();
            }
        });
        edSecond.setOnKeyListener((v, keyCode, event)->{
            if(event.getAction() == KeyEvent.ACTION_DOWN) {
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    if (edSecond.getText().toString().isEmpty()) {
                        if(showMinutes) {
                            edMinute.requestFocus();
                        } else if (showHours) {
                            edHour.requestFocus();
                        } else {
                            moveToPrevFocus();
                        }
                    }
                }
            }
            return false;
        });
        edSecond.setOnEditorActionListener((v, actionId, event)-> {
            if(actionId== EditorInfo.IME_ACTION_NEXT) {
                moveToNextFocus();
                return true;
            }
            return false;
        });

    }
    private void resetVisibility() {
        if(!showHours) {
            edHour.setVisibility(GONE);
            tvHour.setVisibility(GONE);
        }
        if(!showMinutes) {
            edMinute.setVisibility(GONE);
            tvMinute.setVisibility(GONE);
        }
        if(!showSeconds) {
            edSecond.setVisibility(GONE);
            tvMinute.setVisibility(GONE);
        }
    }
    private void moveToNextFocus() {
        try {
            View view= ((Activity) getContext()).getWindow().getDecorView().findViewById(mNextViewID);
            if(view!=null) {
                view.requestFocus(FOCUS_DOWN);
            } else {
                if(showHours) {
                    edHour.requestFocus();
                } else if(showMinutes) {
                    edMinute.requestFocus();
                } else if(showSeconds) {
                    edSecond.requestFocus();
                }
            }
        } catch (Exception e) {
            //ignore
        }
    }
    private void moveToPrevFocus() {
        try {
            View view= ((Activity) getContext()).getWindow().getDecorView().findViewById(mPrevViewID);
            if(view!=null) {
                view.requestFocus(FOCUS_UP);
            } else {
                if(showSeconds) {
                    edSecond.requestFocus();
                } else if(showMinutes) {
                    edMinute.requestFocus();
                } else if(showHours) {
                    edHour.requestFocus();
                }
            }
        } catch (Exception e) {
            //ignore
        }
    }

    private void onChange() {
        if(mOnChangeListener != null && bAllowChanges) {
            mOnChangeListener.onChange(mRootView);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        mOnChangeListener= onChangeListener;
    }

    @Override
    public void setEnabled(boolean enabled) {
        edHour.setEnabled(enabled);
        edMinute.setEnabled(enabled);
        edSecond.setEnabled(enabled);
        super.setEnabled(enabled);
    }
}
