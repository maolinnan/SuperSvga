package com.opensource.svgaplayer

import com.opensource.svgaplayer.proto.SpriteEntity
import org.json.JSONObject

/**
 * Created by cuiminghui on 2016/10/17.
 */
class SVGAVideoSpriteEntity {

    val imageKey: String?

    val frames: Array<SVGAVideoSpriteFrameEntity?>

    var spriteEntity: SpriteEntity? = null

    constructor(obj: JSONObject) {
        this.imageKey = obj.optString("imageKey")
        val mutableFrames: MutableList<SVGAVideoSpriteFrameEntity> = mutableListOf()
        obj.optJSONArray("frames")?.let {
            for (i in 0 until it.length()) {
                it.optJSONObject(i)?.let {
                    val frameItem = SVGAVideoSpriteFrameEntity(it)
                    if (frameItem.shapes.isNotEmpty()) {
                        frameItem.shapes.first()?.let {
                            if (it.isKeep && mutableFrames.size > 0) {
                                frameItem.shapes = mutableFrames.last().shapes
                            }
                        }
                    }
                    mutableFrames.add(frameItem)
                }
            }
        }
        frames = arrayOfNulls(mutableFrames.size)
        for (index in frames.indices){
            frames[index] = mutableFrames[index]
        }
    }

    constructor(obj: SpriteEntity,isCustomLoaderFlow:Boolean) {
        this.imageKey = obj.imageKey
        if (isCustomLoaderFlow) {
            spriteEntity = obj
            frames = arrayOfNulls(obj.frames.size)
        }else {
            var lastFrame: SVGAVideoSpriteFrameEntity? = null
            var tmp = obj.frames?.map {
                val frameItem = SVGAVideoSpriteFrameEntity(it)
                if (frameItem.shapes.isNotEmpty()) {
                    frameItem.shapes.first()?.let {
                        if (it.isKeep) {
                            lastFrame?.let {
                                frameItem.shapes = it.shapes
                            }
                        }
                    }
                }
                lastFrame = frameItem
                return@map frameItem
            } ?: listOf()
            frames = arrayOfNulls(tmp.size)
            for (index in frames.indices){
                frames[index] = tmp[index]
            }
        }

    }

    /**
     * 初始化指定帧信息
     */
    fun initFrameContent(index:Int){
        if (frames[index] == null) {
            frames[index] = SVGAVideoSpriteFrameEntity(spriteEntity!!.frames[index])
        }
    }

    /**
     * 批量初始化帧
     */
    fun initFramesContent(indexs:Array<Int>){
        for(index in frames.indices){
            if(index in indexs){
                initFrameContent(index)
            }else{
                frames[index] = null//释放帧缓存
            }
        }
    }

    /**
     * 不做清理直接预加载帧
     */
    fun unremoveOldFrameInitFramesContent(indexs:Array<Int>){
        for(index in frames.indices){
            if(index in indexs){
                initFrameContent(index)
            }
        }
    }
}
