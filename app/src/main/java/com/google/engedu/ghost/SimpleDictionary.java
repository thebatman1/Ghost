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

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private static final String TAG = "SimpleDictionary";
    private ArrayList<String> words;
    private Random random = new Random();
    private ArrayList<String> oddLengthArrayList, evenLengthArrayList;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        String word = null;
        if (prefix.isEmpty()) {
            word = words.get(random.nextInt(words.size()));
        } else {
            int index = binarySearch(prefix);
            if (index != -1)
                word = words.get(index);
        }
        return word;
    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        String selected = null;
        int index = binarySearch(prefix);
        Log.e(TAG, "" + index);
        if (index >= 0) {
            Log.e(TAG, words.get(index));
            evenLengthArrayList = new ArrayList<>();
            oddLengthArrayList = new ArrayList<>();
            int i = index - 1, j = index + 1;
            String word;
            while (i >= 0 && (word = words.get(i)).startsWith(prefix)) {
                if (word.length() % 2 == 0) {
                    evenLengthArrayList.add(word);
                } else {
                    oddLengthArrayList.add(word);
                }
                i--;
            }
            while (j < words.size() && (word = words.get(j)).startsWith(prefix)) {
                if (word.length() % 2 == 0) {
                    evenLengthArrayList.add(word);
                } else {
                    oddLengthArrayList.add(word);
                }
                j++;
            }
            if (prefix.length() % 2 == 0) {
                if (!evenLengthArrayList.isEmpty()) {
                    selected = evenLengthArrayList.get(random.nextInt(evenLengthArrayList.size()));
                } else {
                    selected = oddLengthArrayList.get(random.nextInt(oddLengthArrayList.size()));
                }
            }
            else {
                if (!oddLengthArrayList.isEmpty()) {
                    selected = oddLengthArrayList.get(random.nextInt(oddLengthArrayList.size()));
                } else {
                    selected = evenLengthArrayList.get(random.nextInt(evenLengthArrayList.size()));
                }
            }
        }
        return selected;
    }

    private int binarySearch(String prefix) {
        int high = words.size() - 1;
        int low = 0;
        while (low <= high) {
            int mid = (high + low)/2;
            String s = words.get(mid);
            int c = s.startsWith(prefix) ? 0 : prefix.compareTo(s);
            if (c == 0) {
                return mid;
            } else if (c > 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }
}
