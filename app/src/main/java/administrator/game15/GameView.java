package administrator.game15;

/**
 * ゲームのメインコンテンツビューです
 */

import android.view.SurfaceView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Point;

public class GameView extends SurfaceView {
    private Paint paint = new Paint();

    private Point gridOrg = new Point(50,50);   // 枠の原点
    private int gridWidth = 680;                // 枠の幅

    public GameView(Context context){
        super(context);
        setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onDraw(Canvas canvas){

        // グリッド表示
        paint.setAntiAlias(true);           // 線が滑らかになる
        paint.setStyle(Paint.Style.STROKE); // 塗りつぶしなし
        paint.setStrokeWidth(4);            // 線幅

        // 外枠
        canvas.drawRect(
                gridOrg.x,
                gridOrg.y,
                gridOrg.x + gridWidth,
                gridOrg.y + gridWidth,
                paint);

        // 格子
        int oneWidth = gridWidth/4;
        for( int i = 0; i < 4; i++ )
        {
            // 横
            canvas.drawLine(
                    gridOrg.x,
                    gridOrg.y + oneWidth * i,
                    gridOrg.x + gridWidth,
                    gridOrg.y + oneWidth * i,
                    paint);

            // 縦
            canvas.drawLine(
                    gridOrg.x + oneWidth * i,
                    gridOrg.y,
                    gridOrg.x + oneWidth * i,
                    gridOrg.y + gridWidth,
                    paint);
        }

        // タイトル
        paint.setTextSize(48);
        canvas.drawText(
                "15 Game",
                gridOrg.x,
                gridOrg.y + gridWidth + 100,
                paint );
    }
}
