package administrator.game15;

/**
 * ゲームのメインコンテンツビューです
 */

import android.content.res.Resources;
import android.graphics.PointF;
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

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private MainActivity ma;

    private SurfaceHolder holder;
    private Thread thread;

    // paint
    private Paint paint = new Paint();

    private Resources rsc = getResources();

    // 座標系
    public PointF gridOrgPoint;//     = new Point(50, 50);  // 外枠の原点
    public float      gridWidth   = 680f;                // 外枠の幅
    private RectF   gridRect;                           // 外枠

    private float   scale;

    private int     emptyPosIdx = -1;               // 空白セルの位置
    private int     movePiecePosIdx = -1;           // 移動中コマの位置

    private float touchedX = -1f;
    private float touchedY = -1f;

    // コマ保持アレー
    private ArrayList<Piece> pieces = new ArrayList<Piece>(16);

    // コンストラクタ
    public GameView(Context context){
        super(context);

        ma = (MainActivity)getContext();

        holder = getHolder();
        holder.addCallback(this);

        // 最初の空白は、最後のセル
        emptyPosIdx = 15;
    }

    // サーフェースのコールバック
    public void surfaceCreated(SurfaceHolder holder)
    {
        thread = new Thread(this);
        thread.start();

        // 向きをチェック
        boolean isVertical = getWidth() < getHeight() ? true : false;

        float margin;

        // 縦向きの場合
        if( isVertical ) {
            margin = (float)(getWidth() * 0.05);
            scale = getWidth() / ( gridWidth + margin * 2 );

            gridOrgPoint = new PointF( margin, margin );
        }
        // 横向きの場合
        else {
            margin = (float)(getHeight() * 0.05);
            scale = getHeight() / (float)( gridWidth + margin * 2 );

            gridOrgPoint = new PointF( (getWidth() - gridWidth * scale) / 2, margin );
        }

        // 外枠
        gridRect = new RectF(
                gridOrgPoint.x,
                gridOrgPoint.y,
                gridOrgPoint.x + gridWidth,
                gridOrgPoint.y + gridWidth );

        // コマを生成
        for( int i = 0; i < 16; i++ )
        {
            Piece piece = new Piece( paint, this, i );
            pieces.add(i, piece);
        }
    }
    public void surfaceDestroyed(SurfaceHolder holder){
        thread = null;
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

    // 配列がゲームクリア可能か、チェック
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

        float oneWidth = gridWidth/4;

        // 座標を１セルの幅で割った時の商が、x,y 座標のセルの位置に該当
        int xPos = (int)(( point.x - gridOrgPoint.x ) / oneWidth);
        int yPos = (int)(( point.y - gridOrgPoint.y ) / oneWidth);

        return xPos + yPos * 4;
    }

    private float
    getPointsDistance(PointF point1, PointF point2)
    {
        return (float)Math.sqrt((int)(point1.x - point2.x)^2 + (int)(point1.y - point2.y)^2);
    }

    // 座標がどの位置に該当するか算出
    private int
    getNearPosIdx(PointF touchedPoint, int posIdx1, int posIdx2){

        Piece piece1 = pieces.get(posIdx1);
        Piece piece2 = pieces.get(posIdx2);

        float distance1 = getPointsDistance( piece1.getCenterPos(), touchedPoint);
        float distance2 = getPointsDistance( piece2.getCenterPos(), touchedPoint);

        // バイアスをかけてる。んだけど、、難しいなぁ。
        return distance1 < distance2 / 3 ? posIdx1 : posIdx2;
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
        return new Rect(
                (int)piece.getPieceRect().left,
                (int)piece.getPieceRect().top,
                (int)piece.getPieceRect().right,
                (int)piece.getPieceRect().bottom );
    }

    private boolean isNumAllOk(){
        boolean fNumAllOk = true;
        for( int i = 0; i < pieces.size(); i++ )
        {
            // 数字と並びがあっているか？
            if( pieces.get(i).numIdx != i )
                return false;
        }
        return true;
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
            touchedX = event.getX() / scale;
            touchedY = event.getY() / scale;

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

                    invalidate(getUpdateArea(movePiece));   // 再描画
                }
                else {
                    movePiecePosIdx = -1;
                }
            }
        }
        if(( action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP)
        {
            if( movePiecePosIdx == -1 )
                return true;

            boolean isMove = false;

            int upPosIdx = getNearPosIdx(new PointF(touchedX, touchedY), movePiecePosIdx, emptyPosIdx);
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

                isMove = true;
            }

            if( !isMove )
            {
                // コマを戻す
                pieces.get(movePiecePosIdx).setPosIdx(movePiecePosIdx);
            }

            // 再描画
            invalidate(getUpdateArea( pieces.get(movePiecePosIdx)));
            invalidate(getUpdateArea( pieces.get(emptyPosIdx)));

            // ゲームクリアしているか？
            if( isMove && isNumAllOk() ){
                ma.clearedGame();
            }

            movePiecePosIdx = -1;
        }
        return true;
    }

    public void run() {

        // ペイントの基本設定
        paint.setAntiAlias(true);           // 線が滑らかになる
        paint.setStrokeWidth(0);            // 線幅なし
        paint.setStyle(Paint.Style.FILL);   // 塗りつぶしあり

        while(thread != null){
            Canvas canvas = holder.lockCanvas();
            if(canvas == null)
                break;

            canvas.drawColor(Color.WHITE);

            // キャンバスサイズを、画面サイズに合わせて調整
            canvas.scale(scale, scale);

            // コマ描画
            for( int i = 0; i < pieces.size(); i++ )
            {
                // 空白セル以外を描画
                if( pieces.get(i).numIdx != 15 )
                    pieces.get(i).drawPiece(canvas);
            }

            holder.unlockCanvasAndPost(canvas);
        }
    }
}
