package administrator.game15;

/**
 * Created by administrator on 2014/06/25.
 */

import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class GameView extends View {
    private Paint paint = new Paint();

    public GameView(Context context){
        super(context);
    }

    @Override
    public void onDraw(Canvas canvas){
        paint.setTextSize(48);
        canvas.drawText("15 Game", 0, 100, paint );
    }
}
