package administrator.game15;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

public class Piece {

    private Paint   paint;      // ペイント
    private Point   pieceOrg;   // コマの座標
    private int     oneWidth;   // 一辺の長さ
    private GameView gameView;  // 親のゲームビュークラス
    public  int     numIdx;     // 表示する番号（から-1した数）
    public  int     posIdx;     // 位置インデックス
    public double   ratio = 0.9;    // コマ一辺とマス一辺のサイズ比

    // コンストラクタ
    public Piece(Paint argPaint, GameView argGameView, int argPosIdx)
    {
        // 初期化
        paint = argPaint;
        gameView = argGameView;
        numIdx = argPosIdx;

        // コマの一辺の長さを算出→マス一辺 * 0.9
        oneWidth = (int)((gameView.gridWidth/4) * ratio);

        // 位置座標設定
        setPosIdx(argPosIdx);
    }

    // 位置Idxから位置座標の算出
    public Point getCenterPos()
    {
        return new Point(getOrgPos().x + oneWidth / 2, getOrgPos().y + oneWidth / 2);
    }

    // 位置Idxから位置座標の算出
    public Point getOrgPos()
    {
        // マス一辺の長さ
        int gridOneWidth = gameView.gridWidth/4;

        // マージン
        int margin = ( gridOneWidth - oneWidth )/2;

        // 横軸の位置→グリッドの原点 + 位置インデックス/4の余り + マージン
        int xPos = gameView.gridOrg.x + gridOneWidth * (posIdx % 4) + margin;
        // 横軸の位置→グリッドの原点 + 位置インデックス/4の商 + マージン
        int yPos = gameView.gridOrg.y + gridOneWidth * (posIdx / 4) + margin;

        return new Point(xPos, yPos);
    }

    // 位置Idxから位置座標の算出／設定
    public void setPosIdx( int idx )
    {
        posIdx = idx;

        pieceOrg = getOrgPos();
    }

    // 移動時用の座標設定
    public void setMovePos(Point centerPos, boolean isVertical)
    {
        // 縦移動
        if( isVertical )
            pieceOrg.y = centerPos.y - oneWidth / 2;
        // 横移動
        else
            pieceOrg.x = centerPos.x - oneWidth / 2;
    }

    public RectF getPieceRect()
    {
        return new RectF( pieceOrg.x, pieceOrg.y, pieceOrg.x + oneWidth, pieceOrg.y + oneWidth );
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
        canvas.drawRoundRect(getPieceRect(), 10, 10, paint);

        // 数字を書く
        paint.setColor(Color.parseColor("#d0d0d0"));
        paint.setTextSize( oneWidth/3 * 2 );

        // 書く位置
        // 表示を見ながら、微妙な調整を入れています…。
        int numPosX = pieceOrg.x +  ( ( numIdx + 1 <  10 ) ? oneWidth/3 : oneWidth/9 );
        int numPosY = pieceOrg.y + oneWidth/4 * 3;

        // 数字描画
        canvas.drawText( String.valueOf(numIdx + 1), numPosX, numPosY, paint );
    }
}
