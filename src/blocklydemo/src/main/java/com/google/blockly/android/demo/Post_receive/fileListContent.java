package com.google.blockly.android.demo.Post_receive;

import java.io.Serializable;

/**
 * Created by admin on 2017/7/25.
 */

public class fileListContent implements Serializable{
    public String fileId;
    public String fileName;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
