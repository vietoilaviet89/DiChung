package Modules;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * Created by 123456789 on 5/2/2017.
 */

public class Converter {
    public static Bitmap stringToBitmap(String s){
        byte[] manghinh = Base64.decode(s, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(manghinh, 0, manghinh.length);
        return bmp;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static String imageToString(ImageView v) {
        BitmapDrawable drawable = (BitmapDrawable) v.getDrawable();
        Bitmap bm = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String imageCode = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return imageCode;
    }

    public static String drawableToString(Drawable drawable){
        BitmapDrawable bmdrawable = (BitmapDrawable) drawable;
        Bitmap bm = bmdrawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String imageCode = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return imageCode;
    }
}
