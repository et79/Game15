package administrator.game15;

/**
 * Created by administrator on 2014/06/26.
 */
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

public class Piece {

    private Paint   paint;
    private Point   gridOrg;
    private Point   pieceOrg;
    private int     oneWidth;
    private int     gridWidth;
    public  int     numIdx;
    public  int     posIdx;

    private int     margin;

    // コンストラクタ
    public Piece(Paint argPaint, Point argGridOrg, int argGridWidth, int argPosIdx)
    {
        // 初期化
        paint = argPaint;
        gridOrg = argGridOrg;
        gridWidth = argGridWidth;

        numIdx = argPosIdx;

        int gridOneWidth = gridWidth / 4;

        margin = (int)( gridOneWidth * 0.05 );
        oneWidth = gridOneWidth - margin * 2;

        setPosIdx(argPosIdx);
    }

    // 位置Idxから位置座標の算出／設定
    public void setPosIdx( int idx )
    {
        posIdx = idx;

        int xPos = idx % 4;
        int yPos = idx / 4;

        int gridOneWidth = gridWidth / 4;

        pieceOrg = new Point(
                gridOrg.x + gridOneWidth * xPos + margin,
                gridOrg.y + gridOneWidth * yPos + margin );

    }

    // コマを描画
    public void drawPiece(Canvas canvas)
    {
        // 色設定／正解の位置にいる場合は、色を替える
        if( posIdx == numIdx )
            paint.setColor(Color.parseColor("#38949d"));
        else
            paint.setColor(Color.parseColor("#48b5c0"));

        // 描画
        RectF rect = new RectF(pieceOrg.x, pieceOrg.y, pieceOrg.x + oneWidth, pieceOrg.y + oneWidth );
        canvas.drawRoundRect(rect, 10, 10, paint);

        // 数字を書く
        paint.setColor(Color.parseColor("#d0d0d0"));
        paint.setTextSize( oneWidth / 3 * 2 );

        // 書く位置
        // 表示を見ながら、微妙な調整を入れています…。
        int numPosX = pieceOrg.x +  ( ( numIdx + 1 <  10 ) ? oneWidth/3 : oneWidth/9 );
        int numPosY = pieceOrg.y + oneWidth / 4 * 3;

        // 数字描画
        canvas.drawText( String.valueOf(numIdx + 1), numPosX, numPosY, paint );
    }
}
