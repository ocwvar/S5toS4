package com.ocwvar.tools

import com.ocwvar.tools.bean.Source
import com.ocwvar.tools.bean.Step
import com.ocwvar.utils.node.Node
import com.ocwvar.utils.node.NodeHelper
import java.io.File

class Convert(baseFolderPath: String) {

    private val baseFolder: File
    private val sourceFolder: File
    private val resultFolder: File

    init {
        this.baseFolder = File(baseFolderPath)
        this.sourceFolder = File("$baseFolderPath\\source\\")
        this.resultFolder = File("$baseFolderPath\\result\\")

        val tool2DX = File("$baseFolderPath\\tools\\2dx\\2dxBuild.exe")
        val toolWav = File("$baseFolderPath\\tools\\wav\\2dxWavConvert.exe")
        val toolFf = File("$baseFolderPath\\tools\\ff\\ffmpeg.exe")
        if (!Step.setEnv(tool2DX, toolWav, toolFf, resultFolder) || !this.sourceFolder.exists() || !this.resultFolder.exists()) {
            throw RuntimeException("运行环境有异常")
        }
    }

    /**
     * 开始转换
     */
    fun start() {
        //获取所有需要转换的数据
        val sources: List<Source>? = loadSources()
        val musicDB: Node? = loadMusicDB()
        sources ?: return
        musicDB ?: return

        //装载XML数据
        loadMusicData2Source(sources, musicDB)

        //开始遍历处理数据
        sources.forEach { doIt(it) }
    }

    /**
     * 开始处理
     *
     * @return  处理是否成功
     */
    private fun doIt(source: Source): Boolean {
        println("[转换] [${source.getName()}] WMA -> MP3:${Step.convertAudio2Mp3(source)}")
        println("[转换] [${source.getName()}] MP3 -> WAV:${Step.convertMp3ToWav(source)}")
        println("[转换] [${source.getName()}] WAV -> 2DX:${Step.convertWavTo2DX(source)}")
        println("[转移] [${source.getName()}] VOX 文件:${Step.moveVox(source)}")
        println("[生成] [${source.getName()}] XML 文件:${Step.writeXml(source)}")
        println("[生成] [${source.getName()}] COVER 文件:${Step.writeCover(source)}")
        println("=============================  ${source.getName()} 处理结束  ====================================")
        return true
    }

    /**
     * 将DB数据导入对应的source中
     */
    private fun loadMusicData2Source(sources: List<Source>, musicDB: Node) {
        val totalData: Int = musicDB.childCount()
        for (pos in 0 until totalData) {
            val p = sources.indexOfFirst { it.getID() == musicDB.indexChildNode(pos).getAttribute("id") }
            if (p < 0) continue
            sources[p].setNode(musicDB.indexChildNode(pos) as Node)
            println("[信息检索] 已找到对应数据：${sources[p]}")
        }
    }

    /**
     * 从数据源目录检索数据
     */
    private fun loadSources(): List<Source>? {
        val folders: Array<File>? = sourceFolder.listFiles { _, name -> !name.endsWith(".xml") }
        if (folders == null || folders.isEmpty()) {
            return null
        }

        val result: ArrayList<Source> = ArrayList(folders.size)
        folders.forEach {
            result.add(Source(it.path))
        }

        return result
    }

    /**
     * 从数据源目录读取歌曲数据库
     */
    private fun loadMusicDB(): Node? {
        try {
            val bytes: ByteArray = File(sourceFolder.path + "\\music_db.xml").readBytes()
            return NodeHelper.xml2Node(NodeHelper.byte2Xml(bytes))
        } catch (e: Exception) {
            return null
        }
    }

}