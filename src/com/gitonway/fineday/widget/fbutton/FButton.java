package com.gitonway.fineday.widget.fbutton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.gitonway.fineday.R;

/**
 * Created by hoang8f on 5/5/14.
 */

public class FButton extends Button implements View.OnTouchListener {

    //Custom values
    private boolean isShadowEnabled = true;
    private int mButtonColor;
    private int mShadowColor;
    private int mShadowHeight;
    private int mCornerRadius;
    //Native values
    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingTop;
    private int mPaddingBottom;

    boolean isShadowColorDefined = false;

    public FButton(Context context) {
        super(context);
        init();
        this.setOnTouchListener(this);
    }

    public FButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        parseAttrs(context, attrs);
        this.setOnTouchListener(this);
    }

    public FButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        parseAttrs(context, attrs);
        this.setOnTouchListener(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //Update background color
        refresh();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.setPadding(mPaddingLeft, mPaddingTop + mShadowHeight, mPaddingRight, mPaddingBottom);
                break;
            case MotionEvent.ACTION_MOVE:
                Rect r = new Rect();
                view.getLocalVisibleRect(r);
                if (!r.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    this.setPadding(mPaddingLeft, mPaddingTop + mShadowHeight, mPaddingRight, mPaddingBottom + mShadowHeight);
                }
                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                this.setPadding(mPaddingLeft, mPaddingTop + mShadowHeight, mPaddingRight, mPaddingBottom + mShadowHeight);
                break;
        }
        return false;
    }

    private void init() {
        //Init default values
        isShadowEnabled = true;
        Resources resources = getResources();
        if (resources == null) return;
        mButtonColor = resources.getColor(R.color.fbutton_default_color);
        mShadowColor = resources.getColor(R.color.fbutton_default_shadow_color);
        mShadowHeight = resources.getDimensionPixelSize(R.dimen.fbutton_default_shadow_height);
        mCornerRadius = resources.getDimensionPixelSize(R.dimen.fbutton_default_conner_radius);
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        //Load from custom attributes
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FButton);
        if (typedArray == null) return;
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.FButton_shadowEnabled) {
                isShadowEnabled = typedArray.getBoolean(attr, true); //Default is true
            } else if (attr == R.styleable.FButton_buttonColor) {
                mButtonColor = typedArray.getColor(attr, R.color.fbutton_default_color);
            } else if (attr == R.styleable.FButton_shadowColor) {
                mShadowColor = typedArray.getColor(attr, R.color.fbutton_default_shadow_color);
                isShadowColorDefined = true;
            } else if (attr == R.styleable.FButton_shadowHeight) {
                mShadowHeight = typedArray.getDimensionPixelSize(attr, R.dimen.fbutton_default_shadow_height);
            } else if (attr == R.styleable.FButton_cornerRadius) {
                mCornerRadius = typedArray.getDimensionPixelSize(attr, R.dimen.fbutton_default_conner_radius);
            }
        }
        typedArray.recycle();

        //Get paddingLeft, paddingRight
        int[] attrsArray = new int[]{
                android.R.attr.paddingLeft,  // 0
                android.R.attr.paddingRight, // 1
        };
        TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
        if (ta == null) return;
        mPaddingLeft = ta.getDimensionPixelSize(0, 0);
        mPaddingRight = ta.getDimensionPixelSize(1, 0);
        ta.recycle();

        //Get paddingTop, paddingBottom
        int[] attrsArray2 = new int[]{
                android.R.attr.paddingTop,   // 0
                android.R.attr.paddingBottom,// 1
        };
        TypedArray ta1 = context.obtainStyledAttributes(attrs, attrsArray2);
        if (ta1 == null) return;
        mPaddingTop = ta.getDimensionPixelSize(0, 0);
        mPaddingBottom = ta.getDimensionPixelSize(1, 0);
        ta1.recycle();
    }

    @SuppressLint("NewApi")
	public void refresh() {
        float[] hsv = new float[3];
        Color.colorToHSV(mButtonColor, hsv);
        hsv[2] *= 0.8f; // value component
        //if shadow color was not defined, generate shadow color = 80% brightness
        if (!isShadowColorDefined) {
            mShadowColor = Color.HSVToColor(hsv);
        }

        StateListDrawable stateListDrawable = new StateListDrawable();
        if (isShadowEnabled) {
            //Shadow is enabled
            stateListDrawable.addState(new int[]{-android.R.attr.state_pressed}, createDrawable(mCornerRadius, mButtonColor, mShadowColor));
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, createDrawable(mCornerRadius, Color.TRANSPARENT, mButtonColor));
        } else {
            //Shadow is disabled
            mShadowHeight = 0;
            stateListDrawable.addState(new int[]{-android.R.attr.state_pressed}, createDrawable(mCornerRadius, mButtonColor, Color.TRANSPARENT));
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, createDrawable(mCornerRadius, mShadowColor, Color.TRANSPARENT));
        }

        //Set button background
        if (Build.VERSION.SDK_INT >= 16) {
            this.setBackground(stateListDrawable);
        } else {
            this.setBackgroundDrawable(stateListDrawable);
        }

        //Set padding
        this.setPadding(mPaddingLeft, mPaddingTop + mShadowHeight, mPaddingRight, mPaddingBottom + mShadowHeight);
    }

    private LayerDrawable createDrawable(int radius, int topColor, int bottomColor) {

        float[] outerRadius = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};

        //Top
        RoundRectShape topRoundRect = new RoundRectShape(outerRadius, null, null);
        ShapeDrawable topShapeDrawable = new ShapeDrawable(topRoundRect);
        topShapeDrawable.getPaint().setColor(topColor);
        //Bottom
        RoundRectShape roundRectShape = new RoundRectShape(outerRadius, null, null);
        ShapeDrawable bottomShapeDrawable = new ShapeDrawable(roundRectShape);
        bottomShapeDrawable.getPaint().setColor(bottomColor);
        //Create array
        Drawable[] drawArray = {bottomShapeDrawable, topShapeDrawable};
        LayerDrawable layerDrawable = new LayerDrawable(drawArray);

        //Set shadow height
        if (isShadowEnabled && topColor != Color.TRANSPARENT) {
            //unpressed drawable
            layerDrawable.setLayerInset(0, 0, 0, 0, 0);  /*index, left, top, right, bottom*/
        } else {
            //pressed drawable
            layerDrawable.setLayerInset(0, 0, mShadowHeight, 0, 0);  /*index, left, top, right, bottom*/
        }
        layerDrawable.setLayerInset(1, 0, 0, 0, mShadowHeight);  /*index, left, top, right, bottom*/

        return layerDrawable;
    }

    //Setter
    public void setShadowEnabled(boolean isShadowEnabled) {
        this.isShadowEnabled = isShadowEnabled;
        setShadowHeight(0);
        refresh();
    }

    public void setButtonColor(int buttonColor) {
        this.mButtonColor = buttonColor;
        refresh();
    }

    public void setShadowColor(int shadowColor) {
        this.mShadowColor = shadowColor;
        isShadowColorDefined = true;
        refresh();
    }

    public void setShadowHeight(int shadowHeight) {
        this.mShadowHeight = shadowHeight;
        refresh();
    }

    public void setCornerRadius(int cornerRadius) {
        this.mCornerRadius = cornerRadius;
        refresh();
    }

    public void setFButtonPadding(int left, int top, int right, int bottom) {
        mPaddingLeft = left;
        mPaddingRight = right;
        mPaddingTop = top;
        mPaddingBottom = bottom;
        refresh();
    }

    //Getter
    public boolean isShadowEnabled() {
        return isShadowEnabled;
    }

    public int getButtonColor() {
        return mButtonColor;
    }

    public int getShadowColor() {
        return mShadowColor;
    }

    public int getShadowHeight() {
        return mShadowHeight;
    }

    public int getCornerRadius() {
        return mCornerRadius;
    }
}
