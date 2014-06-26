package administrator.game15;

/**
 * ゲームのメインコンテンツビューです
 */

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Collections;

public class GameView extends SurfaceView {
    private Paint paint = new Paint();

    private Point gridOrg = new Point(50,50);   // 枠の原点
    private int gridWidth = 680;                // 枠の幅
    private Rect gridRect = new Rect(
            gridOrg.x,
            gridOrg.y,
            gridOrg.x + gridWidth,
            gridOrg.y + gridWidth );
    private int emptyPosIdx = -1;

    private ArrayList<Piece> pieces = new ArrayList<Piece>(16);

    public GameView(Context context){
        super(context);
        setBackgroundColor(Color.WHITE);

        for( int i = 0; i < 16; i++ )
        {
            Piece piece = new Piece(paint, gridOrg, gridWidth, i);
            pieces.add(i, piece);
        }
        emptyPosIdx = 15;
    }

    private void shufflePieces(){
        ArrayList<Integer> idxArr = new ArrayList<Integer>(16);
        for( int i = 0; i < 16; i++ )
            idxArr.add(i, i);

        Collections.shuffle(idxArr);

        for( int i = 0; i < 16; i++ ) {
            pieces.get(i).setPosIdx(idxArr.get(i));
            if( pieces.get(i).numIdx == 15 )
                emptyPosIdx = idxArr.get(i);
        }

        Collections.sort(pieces, new PieceComparator());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction();
        if(( action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP)
        {
            if( gridRect.contains((int)event.getX(), (int)event.getY()) )
            {
                int xPos = ( (int)event.getX() - gridOrg.x ) / ( gridWidth / 4 );
                int yPos = ( (int)event.getY() - gridOrg.y ) / ( gridWidth / 4 );

                int clickPosIdx = xPos + yPos * 4;

                boolean fMoveOk = false;
                if( clickPosIdx - 4 == emptyPosIdx || clickPosIdx + 4 == emptyPosIdx )
                    fMoveOk = true;

                if( !fMoveOk && clickPosIdx % 4 != 0 ) {
                    // 左隣をチェック
                    if( clickPosIdx - 1 == emptyPosIdx )
                        fMoveOk = true;
                }

                if( !fMoveOk && clickPosIdx % 4 != 3 ) {
                    // 右隣をチェック
                    if( clickPosIdx + 1 == emptyPosIdx )
                        fMoveOk = true;
                }

                if( fMoveOk )
                {
                    Piece movePiece = pieces.get(clickPosIdx);
                    movePiece.setPosIdx(emptyPosIdx);

                    Piece emptyPiece = pieces.get(emptyPosIdx);
                    emptyPiece.setPosIdx(clickPosIdx);
                    emptyPosIdx = clickPosIdx;

                    Collections.sort(pieces, new PieceComparator());
                }
            }
            else
            {
                shufflePieces();
            }
            invalidate();
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas){

        // グリッド表示
        paint.setAntiAlias(true);           // 線が滑らかになる
        paint.setStyle(Paint.Style.STROKE); // 塗りつぶしなし
        paint.setStrokeWidth(4);            // 線幅

        // 外枠
        canvas.drawRect( gridRect, paint);

//        // 格子
//        int oneWidth = gridWidth/4;
//        for( int i = 0; i < 4; i++ )
//        {
//            // 横
//            canvas.drawLine(
//                    gridOrg.x,
//                    gridOrg.y + oneWidth * i,
//                    gridOrg.x + gridWidth,
//                    gridOrg.y + oneWidth * i,
//                    paint);
//
//            // 縦
//            canvas.drawLine(
//                    gridOrg.x + oneWidth * i,
//                    gridOrg.y,
//                    gridOrg.x + oneWidth * i,
//                    gridOrg.y + gridWidth,
//                    paint);
//        }

        // コマ描画
        boolean fOk = true;
        for( int i = 0; i < pieces.size(); i++ )
        {
            Piece piece = pieces.get(i);
            if( piece.numIdx != i )
                fOk = false;

            piece.drawPiece(canvas);
        }

        // タイトル
        String mess = "15 Game";
        if( fOk ) mess += " Done!!";
        paint.setTextSize(48);
        canvas.drawText(
                mess,
                gridOrg.x,
                gridOrg.y + gridWidth + 100,
                paint );
    }
}
