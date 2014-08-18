package administrator.game15;

/**
 * ゲームのメインコンテンツビューです
 */

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Collections;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    // paint
    private Paint paint = new Paint();

    // 座標系
    public Point    gridOrg     = new Point(50, 180);  // 外枠の原点
    public int      gridWidth   = 680;                // 外枠の幅
    private RectF   gridRect    = new RectF(          // 外枠
            gridOrg.x,
            gridOrg.y,
            gridOrg.x + gridWidth,
            gridOrg.y + gridWidth );

    private float   scale;

    private int     emptyPosIdx = -1;               // 空白セルの位置
    private int     movePiecePosIdx = -1;           // 移動中コマの位置

    // コマ保持アレー
    private ArrayList<Piece> pieces = new ArrayList<Piece>(16);

    // 最初の描画フラグ
    private boolean isInit      = true;

    // コンストラクタ
    public GameView(Context context){
        super(context);
        setBackgroundColor(Color.parseColor("#d0d0d0"));

        // コマを生成
        for( int i = 0; i < 16; i++ )
        {
            Piece piece = new Piece( paint, this, i );
            pieces.add(i, piece);
        }

        // 最初の空白は、最後のセル
        emptyPosIdx = 15;

        getHolder().addCallback(this);
    }

    // サーフェースのコールバック
    public void surfaceCreated(SurfaceHolder holder)
    {
        // サイズ調整用の比率（縦横それぞれ）を取得
        float scaleX = getWidth() / (float)( gridWidth + gridOrg.x * 2 );
        float scaleY = getHeight() / (float)( gridWidth + gridOrg.y );

        // 比率の小さい方を採用
        scale = scaleX > scaleY ? scaleY : scaleX;
    }
    public void surfaceDestroyed(SurfaceHolder holder)
    {
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
    }

    // コマをシャッフル
    public void shufflePieces(){
        // コマアレーをシャッフル
        Collections.shuffle(pieces);

        // シャッフルしたアレーのIdxを、コマの位置にセット→位置のシャッフル完了
        for( int i = 0; i < 16; i++ ) {
            pieces.get(i).setPosIdx(i);
            if( pieces.get(i).numIdx == 15 )
                emptyPosIdx = i;
        }

        // クリア可能な配列か、チェック
        if( !checkNumLine() )
            shufflePieces();
    }

    // 配列がクリア可能か、チェック
    // http://www.aji.sakura.ne.jp/algorithm/slide_goal.html 参考
    private boolean checkNumLine()
    {
        // チェック用数列の作成
        int[] idxForCheck = {3, 2, 1, 0, 4, 5, 6, 7, 11, 10, 9, 8, 12, 13, 14, 15};
        ArrayList<Integer> checkLine = new ArrayList<Integer>(15);

        for( int i = 0; i < 16; i++ ) {
            if( pieces.get(idxForCheck[i]).numIdx != 15 )
                checkLine.add(pieces.get(idxForCheck[i]).numIdx);
        }

        // checkLineを0~14に並べ変えた時の移動数を算出
        int moveCount = 0;

        for( int i = 0; i < 15; i++ )
        {
            for( int j = i; j < 15; j++ )
            {
                if( checkLine.get(j) == i ) {
                    checkLine.remove(j);
                    checkLine.add(i, i);

                    moveCount += j - i;
                }
            }
        }

        // 移動数が偶数なら、クリア可能
        return ( moveCount % 2 == 0 ) ? true : false;
    }

    // 座標がどの位置に該当するか算出
    private int
    getPosIdx(Point point){

        int oneWidth = gridWidth/4;

        // 座標を１セルの幅で割った時の商が、x,y 座標のセルの位置に該当
        int xPos = ( point.x - gridOrg.x ) / oneWidth;
        int yPos = ( point.y - gridOrg.y ) / oneWidth;

        return xPos + yPos * 4;
    }

    // コマを動かせるか、チェック
    // 該当位置の上下左右に、空白セルがあれば、true
    private boolean isPieceMoveAble(int posIdx, boolean[] isVertical) {
        // 上下をチェック
        if( posIdx - 4 == emptyPosIdx ||
            posIdx + 4 == emptyPosIdx ) {
            isVertical[0] = true;
            return true;
        }

        isVertical[0] = false;

        // 右をチェック（コマが右端にいる場合は除外）
        if( posIdx % 4 != 3 && posIdx + 1 == emptyPosIdx  )
            return true;

        // 左をチェック（コマが左端にいる場合は除外）
        if( posIdx % 4 != 0 && posIdx - 1 == emptyPosIdx )
            return true;

        return false;
    }

    public Rect getUpdateArea(Piece piece)
    {
        int margin = 50;
        return new Rect(
                (int)piece.getPieceRect().left,
                (int)piece.getPieceRect().top,
                (int)piece.getPieceRect().right,
                (int)piece.getPieceRect().bottom );
    }

    // 画面タッチイベント
    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction();
        boolean[] isVertical = new boolean[1];
        isVertical[0] = false;

        if(( action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE)
        {
            // タッチ座標に、プログラム上座標への変換スケールをかける
            float touchedX = event.getX() / scale;
            float touchedY = event.getY() / scale;

            // 枠内を触った場合
            if( gridRect.contains(touchedX, touchedY) ) {

                // 座標から位置Idx取得
                if( movePiecePosIdx == -1 )
                    movePiecePosIdx = getPosIdx(new Point((int) touchedX, (int) touchedY));

                // コマが動かせる場合
                if (isPieceMoveAble(movePiecePosIdx, isVertical)) {

                    Piece movePiece = pieces.get(movePiecePosIdx);
                    Piece emptyPiece = pieces.get(emptyPosIdx);

                    // タッチ座標が移動可能な座標かをチェック
                    if( isVertical[0] )
                    {
                        if(( movePiece.getCenterPos().y < touchedY && emptyPiece.getCenterPos().y < touchedY ) ||
                           ( movePiece.getCenterPos().y > touchedY && emptyPiece.getCenterPos().y > touchedY ))
                            return true;
                    }
                    else
                    {
                        if(( movePiece.getCenterPos().x < touchedX && emptyPiece.getCenterPos().x < touchedX ) ||
                           ( movePiece.getCenterPos().x > touchedX && emptyPiece.getCenterPos().x > touchedX ))
                            return true;
                    }

                    // 動かす対象のコマを、空白セルの場所に移動
                    movePiece.setMovePos(new Point((int) touchedX, (int) touchedY), isVertical[0]);

                    //invalidate(getUpdateArea(movePiece));   // 再描画
                    invalidate();
                }
            }
        }
        if(( action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP)
        {
            if( movePiecePosIdx == -1 )
                return true;

            // コマは移動しているので、移動がキャンセルされる場合を勘案し、
            // 座標を一旦戻しておく
            pieces.get(movePiecePosIdx).setPosIdx(movePiecePosIdx);

            // タッチ座標に、プログラム上座標への変換スケールをかける
            float touchedX = event.getX() / scale;
            float touchedY = event.getY() / scale;

            // 枠内を触った場合
            if( gridRect.contains(touchedX, touchedY) )
            {
                // 座標から位置Idx取得
                int upPosIdx = getPosIdx(new Point((int)touchedX, (int)touchedY));

                if( upPosIdx == emptyPosIdx )
                {
                    // 動かす対象のコマを、空白セルの場所に移動
                    pieces.get(movePiecePosIdx).setPosIdx(emptyPosIdx);

                    // 空白セルを、動かしたコマの位置に移動
                    pieces.get(emptyPosIdx).setPosIdx(movePiecePosIdx);

                    // 空白セルの位置Idxを更新
                    emptyPosIdx = movePiecePosIdx;

                    // 位置Idxとアレーの並びを合わせる
                    Collections.sort(pieces, new PieceComparator());
                }
            }
            // 再描画
            //invalidate(getUpdateArea( pieces.get(movePiecePosIdx)));
            //invalidate(getUpdateArea( pieces.get(emptyPosIdx)));
            invalidate();

            movePiecePosIdx = -1;
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas){

        // キャンバスサイズを、画面サイズに合わせて調整
        canvas.scale(scale, scale);

        // ペイントの基本設定
        paint.setAntiAlias(true);           // 線が滑らかになる
        paint.setStrokeWidth(0);            // 線幅なし
        paint.setStyle(Paint.Style.FILL);   // 塗りつぶしあり

        // 外枠
        paint.setColor(Color.parseColor("#b7b7b7"));
        canvas.drawRoundRect( gridRect, 10, 10, paint);

        // コマ描画
        boolean fNumAllOk = true; // 数字の並び順があっているか？
        for( int i = 0; i < pieces.size(); i++ )
        {
            // 空白セル以外を描画
            if( pieces.get(i).numIdx != 15 )
                pieces.get(i).drawPiece(canvas);

            // 数字と並びがあっているか？
            if( pieces.get(i).numIdx != i )
                fNumAllOk = false;
        }

        // タイトル
        paint.setColor(Color.parseColor("#38949d"));

        int titleSize = 100;

        String mess = "15 Game";
        if( !isInit & fNumAllOk ) mess += " Done!!";
        paint.setTextSize(titleSize);
        canvas.drawText(
                mess,
                gridOrg.x,
                gridOrg.y - titleSize / 2,
                paint );

        isInit = false;
    }
}
