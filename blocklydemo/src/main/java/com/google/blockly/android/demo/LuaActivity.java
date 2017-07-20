/*
 *  Copyright 2017 Google Inc. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.google.blockly.android.demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.android.codegen.LanguageDefinition;
import com.google.blockly.android.demo.Bluetooth.BluetoothActivity;
import com.google.blockly.android.demo.Bluetooth.ConnectThread;
import com.google.blockly.model.DefaultBlocks;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;


/**
 * Demo activity that programmatically adds a view to split the screen between the Blockly workspace
 * and an arbitrary other view or fragment.
 */
public class LuaActivity extends AbstractBlocklyActivity {
    private static final String TAG = "LuaActivity";

    private static final String SAVE_FILENAME = "lua_workspace.xml";
    private static final String AUTOSAVE_FILENAME = "lua_workspace_temp.xml";
    // Add custom blocks to this list.
    private static final List<String> BLOCK_DEFINITIONS = DefaultBlocks.getAllBlockDefinitions();
    private static final List<String> LUA_GENERATORS = Arrays.asList();

    private static final LanguageDefinition LUA_LANGUAGE_DEF
            = new LanguageDefinition("lua/lua_compressed.js", "Blockly.Lua");

    private TextView mGeneratedTextView;
    private Handler mHandler;
    static String LuaLanguage;//保存输出的Lua语言代码

    private String mNoCodeText;

    CodeGenerationRequest.CodeGeneratorCallback mCodeGeneratorCallback =
            new CodeGenerationRequest.CodeGeneratorCallback() {
                @Override
                public void onFinishCodeGeneration(final String generatedCode) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LuaLanguage = generatedCode.toString();
                            Log.d("LuaLanguage", LuaLanguage);
                            mGeneratedTextView.setText(generatedCode);
                            updateTextMinWidth();

                            //当文件已经保存时，生成lua代码

                            if (ConnectThread.mmSocket == null || !ConnectThread.mmSocket.isConnected()) {

                                Toast.makeText(LuaActivity.this, "请在本应用中选择并连接蓝牙", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LuaActivity.this, BluetoothActivity.class);
                                startActivity(intent);

                            } else {
                                OutputStream out = null;
                                try {
                                    out = ConnectThread.mmSocket.getOutputStream();
                                    byte[] b = LuaLanguage.getBytes();
                                    out.write(b);
                                    Toast.makeText(LuaActivity.this, "传输成功", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(LuaActivity.this, "传输失败", Toast.LENGTH_SHORT).show();
                                    AlertDialog.Builder dialog = new AlertDialog.Builder
                                            (LuaActivity.this);
                                    dialog.setTitle("传输失败了");
                                    dialog.setMessage("可能是蓝牙连接断开导致，要不试试重新连接蓝牙");
                                    dialog.setPositiveButton("重连蓝牙", new DialogInterface.
                                            OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(LuaActivity.this, BluetoothActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                    dialog.setNegativeButton("取消", new DialogInterface.
                                            OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    dialog.show();
                                }


                            }
                        }
                    });
                }
            };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {

            onSaveWorkspace();
            return true;
        } else if (id == R.id.action_load) {
            onLoadWorkspace();

            return true;
        } else if (id == R.id.action_clear) {
            onClearWorkspace();
            return true;
        } else if (id == R.id.action_run) {
            if (getController().getWorkspace().hasBlocks()) {
                onRunCode();

            } else {
                Log.i(TAG, "No blocks in workspace. Skipping run request.");
                Toast.makeText(this, "请先保存文件", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == android.R.id.home && mNavigationDrawer != null) {
            setNavDrawerOpened(!isNavDrawerOpen());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
    }

    @Override
    protected View onCreateContentView(int parentId) {
        View root = getLayoutInflater().inflate(R.layout.split_content, null);
        mGeneratedTextView = (TextView) root.findViewById(R.id.generated_code);
        updateTextMinWidth();

        mNoCodeText = mGeneratedTextView.getText().toString(); // Capture initial value.

        return root;
    }

    @Override
    protected int getActionBarMenuResId() {
        return R.menu.split_actionbar;
    }

    @NonNull
    @Override
    protected List<String> getBlockDefinitionsJsonPaths() {
        return BLOCK_DEFINITIONS;
    }

    @NonNull
    @Override
    protected LanguageDefinition getBlockGeneratorLanguage() {
        return LUA_LANGUAGE_DEF;
    }

    @NonNull
    @Override
    protected String getToolboxContentsXmlPath() {
        return DefaultBlocks.TOOLBOX_PATH;
    }

    @NonNull
    @Override
    protected List<String> getGeneratorsJsPaths() {
        return LUA_GENERATORS;
    }

    @NonNull
    @Override
    protected CodeGenerationRequest.CodeGeneratorCallback getCodeGenerationCallback() {
        // Uses the same callback for every generation call.
        return mCodeGeneratorCallback;
    }

    @Override
    public void onClearWorkspace() {
        super.onClearWorkspace();
        mGeneratedTextView.setText(mNoCodeText);
        updateTextMinWidth();
    }

    /**
     * Estimate the pixel size of the longest line of text, and set that to the TextView's minimum
     * width.
     */
    private void updateTextMinWidth() {
        String text = mGeneratedTextView.getText().toString();
        int maxline = 0;
        int start = 0;
        int index = text.indexOf('\n', start);
        while (index > 0) {
            maxline = Math.max(maxline, index - start);
            start = index + 1;
            index = text.indexOf('\n', start);
        }
        int remainder = text.length() - start;
        if (remainder > 0) {
            maxline = Math.max(maxline, remainder);
        }

        float density = getResources().getDisplayMetrics().density;
        mGeneratedTextView.setMinWidth((int) (maxline * 13 * density));
    }

    @Override
    @NonNull
    protected String getWorkspaceSavePath() {
        return SAVE_FILENAME;
    }

    @Override
    @NonNull
    protected String getWorkspaceAutosavePath() {
        return AUTOSAVE_FILENAME;
    }


}
