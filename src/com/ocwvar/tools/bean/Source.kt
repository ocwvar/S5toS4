package com.ocwvar.tools.bean

import com.ocwvar.utils.node.Node

class Source(folderPath: String) {

    private val path: String = folderPath
    private val folderName: String = folderPath.substring(folderPath.lastIndexOf("\\") + 1)

    private val id: String
    private val name: String
    private var xmlNode: Node? = null

    init {
        val pos: Int = folderName.indexOfFirst { c: Char -> c == '_' }
        this.id = folderName.substring(0, pos)
        this.name = folderName.substring(pos + 1)
    }

    /**
     * @return  目录地址
     */
    fun getPath(): String {
        return this.path
    }

    /**
     * @return  目录名称
     */
    fun getFolderName(): String {
        return this.folderName
    }

    /**
     * @return  歌曲ID
     */
    fun getID(): String {
        return this.id
    }

    /**
     * @return  歌曲名称
     */
    fun getName(): String {
        return this.name
    }

    /**
     * 添加歌曲XML数据
     *
     * @param node 数据
     */
    fun setNode(node: Node) {
        this.xmlNode = node
    }

    /**
     * @return  歌曲XML数据
     */
    fun getNode(): Node? {
        return this.xmlNode
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (other !is Source) {
            return false
        }

        return other.id == this.id
    }

    override fun toString(): String {
        return "Music{ID:$id  Name:$name}"
    }

}