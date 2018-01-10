package com.filemark.jcr.service;


public interface AssetManager {
    public void executeAssetOperationNow(final AssetOperation op);
    public void executeAssetOperationBackend(final AssetOperation op);
    public void executeCreateFileOperationBackend(String path, Integer w);
    public void executeCreateFolderIconOperationBackend(String path);
    public void executeCreateFolderIconOperationFrontend(String path);
    public void executeImportFolderOperationBackend(String folderPath,String path,String userName,String override);
}
