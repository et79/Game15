package administrator.game15;

/**
 * Created by administrator on 2014/06/26.
 */
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class Piece {

    private Paint paint;
    private Point gridOrg;
    private Point pieceOrg;
    private int oneWidth;
    private int gridWidth;
    public int numIdx;
    public int posIdx;

    private int margin;


    public Piece(Paint argPaint, Point argGridOrg, int argGridWidth, int argPosIdx)
    {
        // 初期化
        paint = argPaint;
        gridOrg = argGridOrg;
        gridWidth = argGridWidth;

        numIdx = argPosIdx;

        margin = (int)( gridWidth/4 * 0.05 );
        oneWidth = gridWidth/4 - margin * 2;

        setPosIdx(argPosIdx);
    }

    public void setPosIdx( int idx )
    {
        posIdx = idx;

        int xPos = idx % 4;
        int yPos = idx / 4;

        pieceOrg = new Point(
                gridOrg.x + gridWidth / 4  * xPos + margin,
                gridOrg.y + gridWidth / 4 * yPos + margin );

    }

    public void drawPiece(Canvas canvas)
    {
        if( numIdx == 15 )
            return;

        canvas.drawRect(
                pieceOrg.x,
                pieceOrg.y,
                pieceOrg.x + oneWidth,
                pieceOrg.y + oneWidth,
                paint );

        paint.setTextSize(oneWidth - margin * 3);
        canvas.drawText(
                String.valueOf(numIdx + 1),
                pieceOrg.x + margin,
                pieceOrg.y + oneWidth - margin * 2,
                paint );
    }
}
