package com.ocwvar.tools.bean

import com.ocwvar.utils.node.Node
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import java.nio.charset.Charset
import javax.imageio.ImageIO

object Step {

    private lateinit var tool2DX: File
    private lateinit var toolWav: File
    private lateinit var toolFf: File
    private lateinit var resultFolder: File

    /**
     * 配置运行环境
     *
     * @param   tool2DX 2DX 转换工具路径
     * @param   toolWav 2dxWav 转换工具路径
     * @param   toolFf  FFMPEG 转换工具
     */
    fun setEnv(tool2DX: File, toolWav: File, toolFf: File, resultFolder: File): Boolean {
        this.tool2DX = tool2DX
        this.toolWav = toolWav
        this.toolFf = toolFf
        this.resultFolder = resultFolder

        return tool2DX.exists() && toolWav.exists() && toolFf.exists()
    }

    /**
     * 将音频转换为mp3格式
     */
    fun convertAudio2Mp3(source: Source): Boolean {
        var fullAudio = File("${source.getPath()}\\${source.getFolderName()}.s3v")
        var previewAudio = File("${source.getPath()}\\${source.getFolderName()}_pre.s3v")
        val resultFullAudio = File("${source.getPath()}\\${source.getFolderName()}.mp3")
        val resultPreAudio = File("${source.getPath()}\\${source.getFolderName()}_pre.mp3")

        //改名
        fullAudio.renameTo(File("${source.getPath()}\\${source.getFolderName()}.wma"))
        fullAudio = File("${source.getPath()}\\${source.getFolderName()}.wma")
        previewAudio.renameTo(File("${source.getPath()}\\${source.getFolderName()}_pre.wma"))
        previewAudio = File("${source.getPath()}\\${source.getFolderName()}_pre.wma")

        //转换
        waitFinish(Runtime.getRuntime().exec("${toolFf.path} -i ${fullAudio.path} -acodec libmp3lame -b:a 128k ${resultFullAudio.path}"))
        waitFinish(Runtime.getRuntime().exec("${toolFf.path} -i ${previewAudio.path} -acodec libmp3lame -b:a 128k ${resultPreAudio.path}"))

        return resultFullAudio.exists() && resultFullAudio.length() > 0 && resultPreAudio.exists() && resultPreAudio.length() > 0
    }

    /**
     * 将mp3转换为wav格式
     */
    fun convertMp3ToWav(source: Source): Boolean {
        val fullAudio = File("${source.getPath()}\\${source.getFolderName()}.mp3")
        val preAudio = File("${source.getPath()}\\${source.getFolderName()}_pre.mp3")
        val resultFullAudio = File("${source.getPath()}\\${source.getFolderName()}.wav")
        val resultPreAudio = File("${source.getPath()}\\${source.getFolderName()}_pre.wav")

        //转换
        waitFinish(Runtime.getRuntime().exec("${toolWav.path} ${fullAudio.path} ${resultFullAudio.path}"))
        waitFinish(Runtime.getRuntime().exec("${toolWav.path} ${preAudio.path} ${resultPreAudio.path} preview"))

        return resultFullAudio.exists() && resultFullAudio.length() > 0 && resultPreAudio.exists() && resultPreAudio.length() > 0
    }

    /**
     * 将wav转换为2dx
     */
    fun convertWavTo2DX(source: Source): Boolean {
        val fullAudio = File("${source.getPath()}\\${source.getFolderName()}.wav")
        val preAudio = File("${source.getPath()}\\${source.getFolderName()}_pre.wav")
        val targetWav = File("${tool2DX.parent}\\0.wav")
        val target2DX = File("${tool2DX.parent}\\result.2dx")

        //需要转移到指定目录才能进行操作
        fullAudio.renameTo(targetWav)   //复制到临时目录，统一使用名为 0.wav
        waitFinish(Runtime.getRuntime().exec("${tool2DX.path} result.2dx", null, tool2DX.parentFile))  //转换出2DX文件到临时目录
        val statue1 = putResult(source.getID(), "sound", "004_${source.getFolderName()}.2dx", target2DX)

        targetWav.delete()
        Thread.sleep(500)

        //第二次操作
        preAudio.renameTo(targetWav)
        waitFinish(Runtime.getRuntime().exec("${tool2DX.path} result.2dx", null, tool2DX.parentFile))
        val statue2 = putResult(source.getID(), "sound\\preview", "004_${source.getID()}_pre.2dx", target2DX)
        targetWav.delete()

        return statue1 && statue2
    }

    /**
     * 转移VOX文件
     */
    fun moveVox(source: Source): Boolean {
        val voxFiles: Array<File>? = File(source.getPath()).listFiles { _, name -> name.endsWith(".vox") }
        voxFiles ?: return false

        voxFiles.forEach {
            if (!putResult(source.getID(), "others\\vox", "004_${it.name}", it)) {
                return false
            }
        }

        return true
    }

    /**
     * 写入歌曲信息xml文件
     */
    fun writeXml(source: Source): Boolean {
        source.getNode() ?: return false
        source.getNode()?.let {
            it.indexChildNode("info").let { info ->
                info.indexChildNode("version").contentValue = "4"
                if (info.indexChildNode("inf_ver").contentValue == "5") {
                    info.indexChildNode("inf_ver").contentValue = "4"
                }
            }
        }

        val mdb = Node("mdb")
        mdb.addChildNode(source.getNode())

        return putResult(source.getID(), "others", "music_db.merged.xml", mdb.toXmlText().toByteArray(Charset.forName("CP932")))
    }

    /**
     * 写入封面图像
     */
    @JvmStatic
    fun writeCover(source: Source): Boolean {
        val image: Image = ImageIO.read(File("${source.getPath()}\\jk_${source.getID()}_1.png")).getScaledInstance(202, 202, Image.SCALE_FAST)
        val newImage = BufferedImage(202, 202, BufferedImage.TYPE_INT_RGB)
        newImage.graphics.drawImage(image, 0, 0, 202, 202, null)

        try {
            ImageIO.write(newImage, "png", putEmptyResult(source.getID(), "graphics\\s_jacket00_ifs", "jk_004_${source.getID()}_1.png"))
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun copyCover(source: Source): Boolean {
        val image = File("${source.getPath()}\\jk_${source.getID()}_1.png")
        return putResult(source.getID(), "graphics\\s_jacket00_ifs", "jk_004_${source.getID()}_1.png", image)
    }

    /**
     * 存放结果数据
     *
     * @param   id      歌曲ID
     * @param   path    内部路径
     * @param   name    文件名称
     * @param   source  数据源
     */
    private fun putResult(id: String, path: String, name: String, source: File): Boolean {
        try {
            var file = File("${resultFolder.path}\\$id\\${path}\\")
            if (!file.exists()) file.mkdirs()
            file = File("${resultFolder.path}\\$id\\${path}\\$name")

            return source.renameTo(file)
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * 存放结果数据
     *
     * @param   id      歌曲ID
     * @param   path    内部路径
     * @param   name    文件名称
     * @param   source  数据源
     */
    private fun putResult(id: String, path: String, name: String, source: ByteArray): Boolean {
        try {
            var file = File("${resultFolder.path}\\$id\\${path}\\")
            if (!file.exists()) file.mkdirs()
            file = File("${resultFolder.path}\\$id\\${path}\\$name")
            file.createNewFile()

            file.writeBytes(source)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * 创建一个空的文件
     *
     * @param   id      歌曲ID
     * @param   path    内部路径
     * @param   name    文件名称
     */
    private fun putEmptyResult(id: String, path: String, name: String): File? {
        try {
            var file = File("${resultFolder.path}\\$id\\${path}\\")
            if (!file.exists()) file.mkdirs()
            file = File("${resultFolder.path}\\$id\\${path}\\$name")
            file.createNewFile()

            return file
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * 等待进程执行结束
     */
    private fun waitFinish(process: Process) {
        while (process.isAlive) {
            Thread.sleep(100)
        }
    }

}