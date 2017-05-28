package customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by TechnoA on 24.05.2017.
 */


public class TextViewHelveticaLight extends android.support.v7.widget.AppCompatTextView {

    public TextViewHelveticaLight(Context context) {
        super(context);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaLight.ttf");
        this.setTypeface(face);
    }

    public TextViewHelveticaLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaLight.ttf");
        this.setTypeface(face);
    }

    public TextViewHelveticaLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaLight.ttf");
        this.setTypeface(face);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }
}

