package com.cdc.plugin.download;

public interface ProgressListener {

    /**
     *
     * @param totalBytes 总共要下载的字节数
     * @param curBytes 当前下载的字节数
     * @param progress 当前的进度
     */
    void onProgressChange(long totalBytes, long curBytes, int progress);
}
