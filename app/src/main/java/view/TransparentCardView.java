package view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import org.w3c.dom.Attr;

import root.iv.protection.R;

public class TransparentCardView extends View {
    private int cardTopMargin = 0;
    private int cardWidth = 0;
    private int cardHeight = 0;
    private int cardRadiusInner = 0;
    private int cardRadiusOuter = 0;
    private int stroke = 0;
    private int transparentHeight = 0;
    private float centerX = 0;
    private float centerY = 0;
    private int mainWidth = 0;
    private int mainHeight = 0;
    private int cardColor;
    private boolean isDrawn = false;    // Рисовалась ли View
    private OnLayoutListener layoutListener;

    public TransparentCardView(Context context) {
        super(context);
    }

    public TransparentCardView(Context context, AttributeSet attr) {
        super(context, attr);
        init(context, attr);
    }

    public TransparentCardView(Context context, AttributeSet attr, int defStyleAttr) {
        super(context, attr, defStyleAttr);
        init(context, attr);
    }

    public TransparentCardView(Context context, AttributeSet attr, int defStyleAttr, int defStyleRes) {
        super(context, attr, defStyleAttr, defStyleRes);
        init(context, attr);
    }

    private void init(Context context, AttributeSet set) {
        TypedArray array = context.obtainStyledAttributes(set, R.styleable.TransparentCardView);
        cardTopMargin = array.getInt(R.styleable.TransparentCardView_cardTopMargin, cardTopMargin);
        cardWidth = array.getInt(R.styleable.TransparentCardView_cardWidth, cardWidth);
        cardHeight = array.getInt(R.styleable.TransparentCardView_cardHeight, cardHeight);
        cardRadiusInner = array.getInt(R.styleable.TransparentCardView_cardRadiusInner, cardRadiusInner);
        cardRadiusOuter = array.getInt(R.styleable.TransparentCardView_cardRadiusOuter, cardRadiusOuter);
        cardColor = array.getInt(R.styleable.TransparentCardView_cardColor, cardColor);
        array.recycle();
    }

    private void defaultAttributes() {
        mainWidth = getWidth();
        mainHeight = getHeight();
        cardTopMargin = mainHeight / 10;
        cardWidth = mainWidth * 4 / 5;
        cardHeight = mainHeight / 2;
        cardRadiusInner = cardWidth/ 6;
        cardRadiusOuter = cardRadiusInner + (cardRadiusInner/10);
        stroke = cardRadiusInner / 3;
        transparentHeight = cardRadiusOuter;
        centerX = cardWidth/2f;
        centerY = transparentHeight + (cardRadiusOuter / 6f);
        cardColor = R.color.colorPrimary;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isDrawn)
            defaultAttributes();
        isDrawn = true;

        Bitmap bitmap = bitmapDraw();
        canvas.drawBitmap(bitmap, getWidth()/2 - cardWidth/2, cardTopMargin, null);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        defaultAttributes();


        if (layoutListener != null && !isDrawn)
            layoutListener.onLayout();

        isDrawn = true;
    }

    /**
     * Создание CardView с прозрачной окружностью в центре и динамической высотой
     */
    private Bitmap bitmapDraw() {
        Bitmap bitmap = Bitmap.createBitmap(cardWidth, cardHeight, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.TRANSPARENT);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(getResources().getColor(cardColor));

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        canvas.drawCircle(centerX, centerY, cardRadiusInner, paint);

        RectF outerRectangle = new RectF(0,0, cardWidth, transparentHeight);
        canvas.drawRect(outerRectangle, paint);

        paint.setColor(getResources().getColor(cardColor));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stroke);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawCircle(centerX, centerY, cardRadiusOuter, paint);

        return bitmap;
    }

    public interface OnLayoutListener {
        void onLayout();
    }

    public int getCardTopMargin() {
        return cardTopMargin;
    }

    public void setCardTopMargin(int cardTopMargin) {
        this.cardTopMargin = cardTopMargin;
    }

    public int getCardWidth() {
        return cardWidth;
    }

    public void setCardWidth(int cardWidth) {
        this.cardWidth = cardWidth;
    }

    public int getCardHeight() {
        return cardHeight;
    }

    public void setCardHeight(int cardHeight) {
        this.cardHeight = cardHeight;
        invalidate();
    }

    public int getCardRadiusInner() {
        return cardRadiusInner;
    }

    public void setCardRadiusInner(int cardRadiusInner) {
        this.cardRadiusInner = cardRadiusInner;
    }

    public int getCardRadiusOuter() {
        return cardRadiusOuter;
    }

    public void setCardRadiusOuter(int cardRadiusOuter) {
        this.cardRadiusOuter = cardRadiusOuter;
    }

    public int getStroke() {
        return stroke;
    }

    public void setStroke(int stroke) {
        this.stroke = stroke;
    }

    public int getTransparentHeight() {
        return transparentHeight;
    }

    public void setTransparentHeight(int transparentHeight) {
        this.transparentHeight = transparentHeight;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public int getMainWidth() {
        return mainWidth;
    }

    public void setMainWidth(int mainWidth) {
        this.mainWidth = mainWidth;
    }

    public int getMainHeight() {
        return mainHeight;
    }

    public void setMainHeight(int mainHeight) {
        this.mainHeight = mainHeight;
    }

    public int getCardColor() {
        return cardColor;
    }

    public void setCardColor(int cardColor) {
        this.cardColor = cardColor;
        invalidate();
    }

    public boolean isDrawn() {
        return isDrawn;
    }

    public void setDrawn(boolean drawn) {
        isDrawn = drawn;
    }

    public OnLayoutListener getLayoutListener() {
        return layoutListener;
    }

    public void setLayoutListener(OnLayoutListener layoutListener) {
        this.layoutListener = layoutListener;
    }
}
