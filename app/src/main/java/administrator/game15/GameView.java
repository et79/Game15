package administrator.game15;

/**
 * ゲームのメインコンテンツビューです
 */

import android.graphics.Rect;
import android.graphics.RectF;
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

    // メンバー宣言

    // paint
    private Paint paint = new Paint();

    // 座標系
    private Point   gridOrg     = new Point(50,180);  // 外枠の原点
    private int     gridWidth   = 680;                // 外枠の幅
    private RectF   gridRect    = new RectF(          // 外枠
            gridOrg.x,
            gridOrg.y,
            gridOrg.x + gridWidth,
            gridOrg.y + gridWidth );

    private int     titleSize   = 100;                 // タイトルのサイズ
    private Point   titlePos    = new Point(            // タイトルの位置
            gridOrg.x,
            gridOrg.y - 50);

    private int     emptyPosIdx = -1;               // 空白セルの位置

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
            Piece piece = new Piece( paint, gridOrg, gridWidth, i );
            pieces.add(i, piece);
        }

        // 最初の空白は、最後のセル
        emptyPosIdx = 15;
    }

    // コマをシャッフル
    private void shufflePieces(){
        // コマアレーをシャッフル
        Collections.shuffle(pieces);

        // シャッフルしたアレーのIdxを、コマの位置にセット→位置のシャッフル完了
        for( int i = 0; i < 16; i++ ) {
            pieces.get(i).setPosIdx(i);
            if( pieces.get(i).numIdx == 15 )
                emptyPosIdx = i;
        }
    }

    // 座標がどの位置に該当するか算出
    private int getPosIdx(Point point){

        int oneWidth = gridWidth/4;

        // 座標を１セルの幅で割った時の商が、x,y 座標のセルの位置に該当
        int xPos = ( point.x - gridOrg.x ) / oneWidth;
        int yPos = ( point.y - gridOrg.y ) / oneWidth;

        return xPos + yPos * 4;
    }

    // コマを動かせるか、チェック
    // 該当位置の上下左右に、空白セルがあれば、true
    private boolean isPieceMoveAble(int posIdx) {
        // 上下をチェック
        if( posIdx - 4 == emptyPosIdx ||
            posIdx + 4 == emptyPosIdx )
            return true;

        // 右をチェック（コマが右端にいる場合は除外）
        if( posIdx % 4 != 3 && posIdx + 1 == emptyPosIdx  )
            return true;

        // 左をチェック（コマが左端にいる場合は除外）
        if( posIdx % 4 != 0 && posIdx - 1 == emptyPosIdx )
            return true;

        return false;
    }

    // 画面タッチイベント
    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction();
        if(( action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP)
        {
            // 枠内を触った場合
            if( gridRect.contains(event.getX(), event.getY()) )
            {
                // 座標から位置Idx取得
                int clickPosIdx = getPosIdx(new Point((int)event.getX(), (int)event.getY()));

                // コマが動かせる場合
                if( isPieceMoveAble(clickPosIdx) )
                {
                    // 動かす対象のコマを、空白セルの場所に移動
                    pieces.get(clickPosIdx).setPosIdx(emptyPosIdx);

                    // 空白セルを、動かしたコマの位置に移動
                    pieces.get(emptyPosIdx).setPosIdx(clickPosIdx);

                    // 空白セルの位置Idxを更新
                    emptyPosIdx = clickPosIdx;

                    // 位置Idxとアレーの並びを合わせる
                    Collections.sort(pieces, new PieceComparator());

                    invalidate();   // 再描画
                }
            }
            // 枠外をタッチ
            else
            {
                // 今はシャッフル実行
                shufflePieces();
                invalidate();   // 再描画
            }
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas){

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

        String mess = "15 Game";
        if( !isInit & fNumAllOk ) mess += " Done!!";
        paint.setTextSize(titleSize);
        canvas.drawText(
                mess,
                titlePos.x,
                titlePos.y,
                paint );

        isInit = false;
    }
}
