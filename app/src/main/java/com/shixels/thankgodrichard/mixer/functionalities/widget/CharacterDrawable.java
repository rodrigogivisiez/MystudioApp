package com.shixels.thankgodrichard.mixer.functionalities.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.annotation.Dimension;

/**
 * Created with Android Studio.
 * ""
 * Date: 7/16/16
 * Time: 6:59 AM
 * Desc: CharacterDrawable
 */
public class CharacterDrawable extends Drawable {

    private String mCharacter;
    @ColorInt
    private int mCharacterTextColor;
    private boolean mBackgroundRoundAsCircle;
    @ColorInt
    private int mBackgroundColor;
    @Dimension
    private float mBackgroundRadius;
    /**
     * The M character padding.
     */
    @Dimension
    float mCharacterPadding;

    /**
     * The M paint.
     */
    Paint mPaint = new Paint();
    /**
     * The M clip path.
     */
    Path mClipPath = new Path();
    /**
     * The M background rect.
     */
    RectF mBackgroundRect = new RectF();

    /**
     * The M width.
     */
    int mWidth, /**
     * The M height.
     */
    mHeight;

    private CharacterDrawable() {
        // Avoid direct instantiate
    }

    @Override
    public void draw(Canvas canvas) {
        mWidth = getBounds().right - getBounds().left;
        mHeight = getBounds().bottom - getBounds().top;

        mPaint.setAntiAlias(true);

        // Draw background
        mPaint.setColor(mBackgroundColor);
        mBackgroundRect.set(0, 0, mWidth, mHeight);

        if (mBackgroundRoundAsCircle) {
            canvas.drawOval(mBackgroundRect, mPaint);
            mClipPath.addOval(mBackgroundRect, Path.Direction.CW);
        } else {
            canvas.drawRoundRect(mBackgroundRect, mBackgroundRadius, mBackgroundRadius, mPaint);
            mClipPath.addRoundRect(mBackgroundRect, mBackgroundRadius, mBackgroundRadius, Path.Direction.CW);
        }
        canvas.clipPath(mClipPath);

        // Draw text in the center of the canvas
        mPaint.setColor(mCharacterTextColor);
        mPaint.setTextSize(mHeight - mCharacterPadding * 2);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTypeface(Typeface.DEFAULT);

        if (mCharacter != null) {
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float textBaseY = mHeight - fontMetrics.bottom / 2 - mCharacterPadding;
            float fontWidth = mPaint.measureText(mCharacter);
            float textBaseX = (mWidth - fontWidth) / 2;
            canvas.drawText(mCharacter, textBaseX, textBaseY, mPaint);
        }
        // Clip the circle path
        // http://stackoverflow.com/a/22829656/2290191
        // canvas.drawPath(mClipPath, mPaint);
    }

    @Override
    public void setAlpha(int i) {
        // TODO
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // TODO
    }

    @Override
    public int getOpacity() {
        // TODO
        return PixelFormat.UNKNOWN;
    }

    // Getters & Setters

    /**
     * Gets character.
     *
     * @return the character
     */
    public String getCharacter() {
        return mCharacter;
    }

    /**
     * Sets character.
     *
     * @param character the character
     */
    public void setCharacter(String character) {
        this.mCharacter = character;
    }

    /**
     * Gets character text color.
     *
     * @return the character text color
     */
    public int getCharacterTextColor() {
        return mCharacterTextColor;
    }

    /**
     * Sets character text color.
     *
     * @param characterTextColor the character text color
     */
    public void setCharacterTextColor(int characterTextColor) {
        this.mCharacterTextColor = characterTextColor;
    }

    /**
     * Is background round as circle boolean.
     *
     * @return the boolean
     */
    public boolean isBackgroundRoundAsCircle() {
        return mBackgroundRoundAsCircle;
    }

    /**
     * Sets background round as circle.
     *
     * @param backgroundRoundAsCircle the background round as circle
     */
    public void setBackgroundRoundAsCircle(boolean backgroundRoundAsCircle) {
        this.mBackgroundRoundAsCircle = backgroundRoundAsCircle;
    }

    /**
     * Gets background color.
     *
     * @return the background color
     */
    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    /**
     * Sets background color.
     *
     * @param backgroundColor the background color
     */
    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
    }

    /**
     * Gets background radius.
     *
     * @return the background radius
     */
    public float getBackgroundRadius() {
        return mBackgroundRadius;
    }

    /**
     * Sets background radius.
     *
     * @param backgroundRadius the background radius
     */
    public void setBackgroundRadius(float backgroundRadius) {
        this.mBackgroundRadius = backgroundRadius;
    }

    /**
     * Gets character padding.
     *
     * @return the character padding
     */
    public float getCharacterPadding() {
        return mCharacterPadding;
    }

    /**
     * Sets character padding.
     *
     * @param characterPadding the character padding
     */
    public void setCharacterPadding(float characterPadding) {
        this.mCharacterPadding = characterPadding;
    }

    /**
     * The type Builder.
     */
    public static class Builder {

        private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#CCCCCC");
        private static final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#EEEEEE");

        private String character;

        @ColorInt
        private int characterTextColor = DEFAULT_TEXT_COLOR;

        private boolean backgroundRoundAsCircle;
        @ColorInt
        private int backgroundColor = DEFAULT_BACKGROUND_COLOR;
        @Dimension
        private float backgroundRadius;

        @Dimension
        private float mCharacterPadding;

        /**
         * Apply style builder.
         *
         * @param style the style
         * @return the builder
         */
        public Builder applyStyle(int style) {

            return this;
        }

        /**
         * Sets character.
         *
         * @param character the character
         * @return the character
         */
        public Builder setCharacter(char character) {
            this.character = String.valueOf(character);
            return this;
        }

        /**
         * Sets character.
         *
         * @param character the character
         * @return the character
         */
        public Builder setCharacter(String character) {
            this.character = character;
            return this;
        }

        /**
         * Sets character text color.
         *
         * @param textColor the text color
         * @return the character text color
         */
        public Builder setCharacterTextColor(@ColorInt int textColor) {
            this.characterTextColor = textColor;
            return this;
        }

        /**
         * Sets background round as circle.
         *
         * @param roundAsCircle the round as circle
         * @return the background round as circle
         */
        public Builder setBackgroundRoundAsCircle(boolean roundAsCircle) {
            this.backgroundRoundAsCircle = roundAsCircle;
            return this;
        }

        /**
         * Sets background color.
         *
         * @param backgroundColor the background color
         * @return the background color
         */
        public Builder setBackgroundColor(@ColorInt int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        /**
         * Sets background radius.
         *
         * @param backgroundRadius the background radius
         * @return the background radius
         */
        public Builder setBackgroundRadius(@Dimension float backgroundRadius) {
            this.backgroundRadius = backgroundRadius;
            return this;
        }

        /**
         * Sets character padding.
         *
         * @param padding the padding
         * @return the character padding
         */
        public Builder setCharacterPadding(@Dimension float padding) {
            this.mCharacterPadding = padding;
            return this;
        }

        /**
         * Build character drawable.
         *
         * @return the character drawable
         */
        public CharacterDrawable build() {
            CharacterDrawable drawable = new CharacterDrawable();
            drawable.setCharacter(character);
            drawable.setCharacterTextColor(characterTextColor);
            drawable.setBackgroundRoundAsCircle(backgroundRoundAsCircle);
            drawable.setBackgroundColor(backgroundColor);
            drawable.setBackgroundRadius(backgroundRadius);
            drawable.setCharacterPadding(mCharacterPadding);
            return drawable;
        }
    }

    /**
     * Create character drawable.
     *
     * @param context       the context
     * @param character     the character
     * @param roundAsCircle the round as circle
     * @param padding       the padding
     * @return the character drawable
     */
    public static CharacterDrawable create(Context context, char character, boolean roundAsCircle, @DimenRes int padding) {
        return new CharacterDrawable.Builder()
                .setCharacter(character)
                .setBackgroundRoundAsCircle(roundAsCircle)
                .setCharacterPadding(context.getResources().getDimensionPixelSize(padding))
                .build();
    }
}
