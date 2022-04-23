/*
 * Copyright 2020, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.dessertclicker

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import com.example.android.dessertclicker.databinding.ActivityMainBinding

//定数
const val TAG = "MainActivity"
const val KEY_REVENUE = "revenue_key"
const val KEY_DESSERT_SOLD = "dessert_sold_key"

class MainActivity : AppCompatActivity() {

    private var revenue = 0
    private var dessertsSold = 0

    // Contains all the views
    private lateinit var binding: ActivityMainBinding

    /** Dessert Data **/

    /**
     * Simple data class that represents a dessert. Includes the resource id integer associated with
     * the image, the price it's sold for, and the startProductionAmount, which determines when
     * the dessert starts to be produced.
     */
    data class Dessert(val imageId: Int, val price: Int, val startProductionAmount: Int)

    // Create a list of all desserts, in order of when they start being produced
    private val allDesserts = listOf(
            Dessert(R.drawable.cupcake, 5, 0),
            Dessert(R.drawable.donut, 10, 5),
            Dessert(R.drawable.eclair, 15, 20),
            Dessert(R.drawable.froyo, 30, 50),
            Dessert(R.drawable.gingerbread, 50, 100),
            Dessert(R.drawable.honeycomb, 100, 200),
            Dessert(R.drawable.icecreamsandwich, 500, 500),
            Dessert(R.drawable.jellybean, 1000, 1000),
            Dessert(R.drawable.kitkat, 2000, 2000),
            Dessert(R.drawable.lollipop, 3000, 4000),
            Dessert(R.drawable.marshmallow, 4000, 8000),
            Dessert(R.drawable.nougat, 5000, 16000),
            Dessert(R.drawable.oreo, 6000, 20000)
    )
    private var currentDessert = allDesserts[0]

    /*
    ライフサイクルのユースケース
    ユースケース 1: アクティビティの開始と終了
    アプリを初めて起動し、カップケーキを数回クリック、その後完全に終了するという、基本的なユースケース
    onCreate()、onStart()、onResume()、onPause()、onStop()、onDestroy()
    戻るボタンを押した後にホーム画面から復帰したとしてもonDestroy()、onCreate()、onStart()、onResume()となる
    ユースケース 2: アクティビティ間の移動
    カップケーキを数回クリック、アプリがバックグラウンドに移行してフォアグラウンドに戻ったときのアクティビティのライフサイクルを確認
    onPause()、onStop()、onRestart()、onStart()、onResume()
    ユースケース 3: アクティビティの一部を非表示にする
    シェアボタンを押す
    onPause()、onResume()
     */

    //アプリ起動時に必ず呼び出す
    //パラメーターにバンドルがあり、これがnullでない(バンドルに値が入っている)場合は、
    //既知のポイントからアクティビティを再作成できる！
    override fun onCreate(savedInstanceState: Bundle?) {
        //onCreate()メソッド内でスーパークラスの実装を呼び出す必要がある
        super.onCreate(savedInstanceState)
        //Logcatで見つけやすくるタグTAG="MainActivity"とログメッセージ"onCreate Called"を出力するLogメソッド
        //LogcatでMainActivityを検索するとmsgが出力されているのがわかる
        Log.d(TAG, "onCreate Called")

        // Use Data Binding to get reference to the views
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.dessertButton.setOnClickListener {
            onDessertClicked()
        }
        //バンドルに値が入っているときに再作成する
        //ビューホルダーにbindingする前に変数を上書きする！！
        if(savedInstanceState != null){
            //getInt()でキーを指定し取得(0は指定するキーが存在しない場合のデフォルト値)
            revenue = savedInstanceState.getInt(KEY_REVENUE,0)
            dessertsSold = savedInstanceState.getInt(KEY_DESSERT_SOLD,0)
            showCurrentDessert()
        }

        // Set the TextViews to the right values
        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // Make sure the correct dessert is showing
        binding.dessertButton.setImageResource(currentDessert.imageId)



    }

    //onCreateの後や、onRestartの後に呼び出される
    //[Code] > [Override Methods]で「onStart」と打ち込めば出てくるので選択
    override fun onStart() {
        super.onStart()
        //状態遷移確認用のログ出力
        Log.d(TAG, "onStart Called")
    }

    //onStartの後や、アクティビティにフォーカスがある(操作可能)なときに呼び出される
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume Called")
    }

    //アプリがバックグラウンドに移行するときや、アクティビティにフォーカスがない(操作不可能)なときに呼び出される
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause Called")
    }

    //onPause()の後に呼び出される
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop Called")
    }

    //アクティビティが完全にシャットダウンされガベージコレクションになる可能性がある
    //ガベージコレクション：使用されなくなったオブジェクトの自動クリーンアップ
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy Called")
    }

    //アプリがバックグラウンドからフォアグラウンドに移行するときに、呼び出される
    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart Called")
    }

    //アクティビティがフォアグラウンドを終了したときに少量の情報をバンドルに保存するための安全柵
    //注：onSaveInstanceStateをオーバーライドするときはパラメーターが1つのやつを使用する
    //BundleはKey-Valueペアのコレクションで、キーは常に文字列
    //バンドルはIntやBooleanなどの単純なデータを追加できる。バンドルはメモリに保存されるため小さくする
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState Called")
        //バンドルにputInt()で収益と販売個数を追加する
        outState.putInt(KEY_REVENUE,revenue)
        outState.putInt(KEY_DESSERT_SOLD,dessertsSold)
    }

    /**
     * Updates the score when the dessert is clicked. Possibly shows a new dessert.
     */
    private fun onDessertClicked() {

        // Update the score
        revenue += currentDessert.price
        dessertsSold++

        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // Show the next dessert
        showCurrentDessert()
    }

    /**
     * Determine which dessert to show.
     */
    private fun showCurrentDessert() {
        var newDessert = allDesserts[0]
        for (dessert in allDesserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                newDessert = dessert
            }
            // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
            // you'll start producing more expensive desserts as determined by startProductionAmount
            // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
            // than the amount sold.
            else break
        }

        // If the new dessert is actually different than the current dessert, update the image
        if (newDessert != currentDessert) {
            currentDessert = newDessert
            binding.dessertButton.setImageResource(newDessert.imageId)
        }
    }

    /**
     * Menu methods
     */
    private fun onShare() {
        val shareIntent = ShareCompat.IntentBuilder.from(this)
                .setText(getString(R.string.share_text, dessertsSold, revenue))
                .setType("text/plain")
                .intent
        try {
            startActivity(shareIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.sharing_not_available),
                    Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.shareMenuButton -> onShare()
        }
        return super.onOptionsItemSelected(item)
    }
}
