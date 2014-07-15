package administrator.game15;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.util.List;


public class MainActivity extends ActionBarActivity implements SensorEventListener {

    // ゲームビューのインスタンス
    private GameView gameView;

    // センサーマネージャ
    private SensorManager mSensorManager;

    // シェイク判定用のメンバー変数
    private float beforeX;
    private float beforeY;
    private float beforeZ;
    private long beforeTime = -1;   // 前回の時間

    private float shakeSpeed = 80;  // 振ってると判断するスピード
    private float shakeCount = 0;   // 振ってると判断した回数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ゲームビューのインスタンス作成
        gameView = new GameView(this);

        // センサーマネージャのインスタンス作成
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // ツールバーを非表示
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // ゲームビューを表示
        setContentView(gameView);
    }

    @Override
    protected void onResume(){
        super.onResume();

        // 加速度センサーのオブジェクト取得
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        // センサーを登録
        if(sensors.size() > 0){
            Sensor s = sensors.get(0);
            mSensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
        }

        // 初期化
        beforeX = 0;
        beforeY = 0;
        beforeZ = 0;
        beforeTime = -1;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // センサーの登録を解除
        if(mSensorManager != null)
            mSensorManager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){
        // 未使用
    }

    @Override
    public void onSensorChanged(SensorEvent event){

        switch(event.sensor.getType()){

        // 加速度センサーのイベントをハンドリング
        case Sensor.TYPE_ACCELEROMETER:

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            long nowTime = System.currentTimeMillis();

            // 最初のイベント→値を保持するのみ
            if( beforeTime == -1 ){
                beforeX = x;
                beforeY = y;
                beforeZ = z;

                beforeTime = nowTime;
                break;
            }

            long diffTime = nowTime - beforeTime;
            if( diffTime < 500 )
                break;

            // 前回の値との差から、スピードを算出
            // すみません、どうしてこれでOKなのか、不勉強でまだ理解出来ていません。。。
            float speed = Math.abs(x + y + z - beforeX - beforeY - beforeZ) / diffTime * 10000;

            // スピードがしきい値以上の場合、振ってるとみなす
            if( speed > shakeSpeed ){
                // 振ってると判断した回数が3以上、つまり1.5秒間振り続けると、シャッフルする
                if(++shakeCount > 2){
                    shakeCount = 0;

                    // コマの並びをシャッフルし、再描画
                    gameView.shufflePieces();
                    gameView.invalidate();
                }
            }
            else {
                // 途中でフリが収まった場合は、カウントを初期化
                shakeCount = 0;
            }

            // 前回の値を覚える
            beforeX = x;
            beforeY = y;
            beforeZ = z;

            beforeTime = nowTime;

            break;
        }
    }
}
