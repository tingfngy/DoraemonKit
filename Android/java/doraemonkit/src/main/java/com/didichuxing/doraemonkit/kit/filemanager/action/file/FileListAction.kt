package com.didichuxing.doraemonkit.kit.filemanager.action.file

import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.didichuxing.doraemonkit.kit.filemanager.FileManagerUtil
import java.io.File

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/23-15:26
 * 描    述：
 * 修订历史：
 * ================================================
 */
object FileListAction {
    fun fileListRes(dirPath: String): MutableMap<String, Any> {
        //root  path
        val params = mutableMapOf<String, Any>().apply {
            this["code"] = 200
        }
        if (dirPath == "/") {
            val data = mutableMapOf<String, Any>().apply {
                this["dirPath"] = "/"
                this["fileList"] = createRootInfo()
            }
            params["data"] = data
        } else {
            //not root path
            val data = mutableMapOf<String, Any>().apply {
                this["dirPath"] = "${FileManagerUtil.relativeRootPath(dirPath)}/"
                this["fileList"] = traverseDir(dirPath)
            }
            params["data"] = data
        }

        return params
    }


    /**
     * 遍历根文件夹
     */
    private fun createRootInfo(): MutableList<FileInfo> {
        val fileInfos = mutableListOf<FileInfo>()
        val internalAppDataPath = PathUtils.getInternalAppDataPath()
        val externalStoragePath = PathUtils.getExternalStoragePath()
        fileInfos.add(FileInfo("/", FileUtils.getFileName(internalAppDataPath), "folder", "", "" + FileUtils.getFileLastModified(internalAppDataPath), true))
        fileInfos.add(FileInfo("/", "external", "folder", "", "" + FileUtils.getFileLastModified(externalStoragePath), true))
        return fileInfos
    }

    /**
     * 遍历文件夹
     */
    private fun traverseDir(dirPath: String): MutableList<FileInfo> {
        val fileInfos = mutableListOf<FileInfo>()
        val dir = File(dirPath)
        if (FileUtils.isDir(dir)) {
            dir.listFiles()?.forEach { file ->
                val fileInfo = FileInfo("${FileManagerUtil.relativeRootPath(dirPath)}/", file.name, if (FileUtils.isDir(file)) {
                    "folder"
                } else if (dir.absolutePath.contains("/databases")) {
                    "db"
                } else {
                    FileUtils.getFileExtension(file)
                }, "", "" + FileUtils.getFileLastModified(file), false)
                fileInfos.add(fileInfo)
            }

        }

        return fileInfos
    }


    data class FileInfo(
            val dirPath: String,
            val fileName: String,
            val fileType: String,
            val fileUri: String,
            val modifyTime: String,
            val isRootPath: Boolean
    )
}


