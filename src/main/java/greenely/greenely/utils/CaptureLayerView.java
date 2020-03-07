package greenely.greenely.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import greenely.greenely.R;

public class CaptureLayerView extends View {

  private Bitmap bitmap;
  private Canvas cnvs;
  private Paint p = new Paint();
  private Paint transparentPaint = new Paint();;
  private Paint semiTransparentPaint = new Paint();;
  private int parentWidth;
  private int parentHeight;
  private int radius = 100;

  public CaptureLayerView(Context context) {
      super(context);
      init();
  }

  public CaptureLayerView(Context context, AttributeSet attrs) {
      super(context, attrs);
      init();
  }

  private void init() {
      transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
      transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

      semiTransparentPaint.setColor(getResources().getColor(R.color.green_3));
      semiTransparentPaint.setAlpha(70);
  }

  @Override
  protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);

      bitmap = Bitmap.createBitmap(parentWidth, parentHeight, Bitmap.Config.ARGB_8888);
      cnvs = new Canvas(bitmap);
      cnvs.drawRect(0, 0, cnvs.getWidth(), cnvs.getHeight(), semiTransparentPaint);
      cnvs.drawCircle(parentWidth / 2, parentHeight / 2, radius, transparentPaint);
      canvas.drawBitmap(bitmap, 0, 0, p);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

      parentWidth = MeasureSpec.getSize(widthMeasureSpec);
      parentHeight = MeasureSpec.getSize(heightMeasureSpec);

      this.setMeasuredDimension(parentWidth, parentHeight);
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }
}