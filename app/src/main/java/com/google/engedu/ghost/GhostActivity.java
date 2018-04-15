/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private static final String TAG = "GhostActivity";
    private Random random = new Random();
    private StringBuilder wordFragment = new StringBuilder();
    private TextView text, label;
    private Button challenge, restart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        try {
            dictionary = new SimpleDictionary(assetManager.open("words.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        text = (TextView) findViewById(R.id.ghostText);
        label = (TextView) findViewById(R.id.gameStatus);
        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        wordFragment.setLength(0);
        userTurn = random.nextBoolean();
        text.setText(wordFragment);

        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        // Do computer turn stuff then make it the user's turn again
        String longerWord;
        String word = wordFragment.toString();
        if (word.length() >= 4 && dictionary.isWord(word)) {
            label.setText("Computer Wins!");
            wordFragment.setLength(0);
            userTurn = false;
        } else if ((longerWord = dictionary.getGoodWordStartingWith(word)) == null ) {
            label.setText("Computer Wins!");
            wordFragment.setLength(0);
            userTurn = false;
        } else {
            wordFragment.append(longerWord.charAt(word.length()));
            text.setText(wordFragment);
            userTurn = true;
            label.setText(USER_TURN);
        }
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        char ch = (char)event.getUnicodeChar();
        if (Character.isLetter(ch) && userTurn) {
            wordFragment.append(ch);
            text.setText(wordFragment.toString());
            Log.e(TAG, wordFragment.toString());
            computerTurn();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void onChallenge(View view) {
        String longerWord;
        String word = wordFragment.toString();
        Log.e(TAG, word);
        if (word.length() >= 4 && dictionary.isWord(word)) {
            label.setText("User Wins!");
        } else if ((longerWord = dictionary.getAnyWordStartingWith(word)) == null) {
            label.setText("User Wins!");
        } else {
            label.setText("Computer Wins!");
            text.setText(longerWord);
        }
        wordFragment.setLength(0);
        userTurn = false;
    }
}
