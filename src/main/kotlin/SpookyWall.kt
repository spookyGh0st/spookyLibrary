@file:Suppress("unused")

import kotlin.math.abs
import kotlin.random.Random

@Suppress("MemberVisibilityCanBePrivate")
data class SpookyWall(
    var startTime: Double,
    var duration: Double,
    var startHeight: Double,
    var height: Double,
    var startRow: Double,
    var width: Double
) {

    /**Changes the MyObstacle Type to an Object Type */
    fun toWall(): dynamic {
        return jsObject {
            _time = startTime
            _duration = calculateDuration()
            _type = type()
            _lineIndex = calculateLineIndex()
            _width = calculateWidth()
        }
    }

    inline fun jsObject(init: dynamic.() -> Unit): dynamic {
        val o = js("{}")
        init(o)
        return o
    }

    private fun calculateDuration(): Double{
        val tempDuration = if (duration <0.0001 && duration >-0.0001) 0.0001 else duration
        return tempDuration.coerceAtLeast(-3.0)
    }

    /**returns th Object value of the width*/
    //TODO until negative width is allowed, this is needed. Once negative width is allowed, do this like lineIndex
    private fun calculateWidth():Int{
        //makes sure its not 0 width
        width = if(width > -0.01 && width < 0.01) 0.01 else width

        //calculate the width
        return if( width >= 0.0)
            (width * 1000 +1000).toInt()
        else{
            startRow += width
            (abs(width)*1000+1000).toInt()
        }
    }


    /**Return the Object value of the startRow*/
    private fun calculateLineIndex():Int {
        val i = startRow +2
        return if( i >= 0.0)
            (i* 1000 +1000).toInt()
        else
            (i*1000-1000).toInt()
    }


    /**returns the type given heigt and startheight */
    private fun type():Int {

        val wallH= if(height >-0.01 && height <0.01) 0.01 else abs(height)

        val startH = if(height >0) startHeight else startHeight + height

        var tWallH:Int = (((1.0/3.0)*(wallH/(4.0/3.0)))*1000).toInt()
        tWallH = when {
            tWallH>4000 -> 4000
            tWallH<0 -> 0
            else -> tWallH
        }

        var tStartH:Int =  (250*(startH/(4.0/3.0))).toInt()
        tStartH = when {
            tStartH>999 -> 999
            tStartH<0 -> 0
            else -> tStartH
        }
        return  (tWallH * 1000 + tStartH+4001)
    }


    fun adjustToBPM(baseBPM:Double,newBPM:Double,offset:Double): SpookyWall {
        var tempStartTime = startTime* (baseBPM / newBPM)
        tempStartTime += offset
        val tempDuration = if(duration > 0)
            duration * (baseBPM / newBPM)
        else
            duration
        return this.copy(startTime = tempStartTime, duration = tempDuration)
    }
    fun fuckUp() =
        SpookyWall(ra(startRow), ra(duration), ra(width), ra(height), ra(startHeight), ra(startTime))

    private fun ra(i:Double) = i+ Random.nextDouble(-0.2 ,0.2)
    fun fast() = this.copy(duration= -2.0)
    fun hyper() = this.copy(duration = -3.0)

    /**returns the mirrored obstacle */
    fun mirror(d:Boolean): List<SpookyWall> {
        val a =  mutableListOf(this.copy(startRow = -startRow, width = -width))
        if (d) a.add(this.copy())
        return a
    }
    fun verticalMirror(sh:Double = 2.0,d: Boolean): MutableList<SpookyWall> {
        val a = mutableListOf<SpookyWall>()
        a.add(this.copy(startHeight = 2 * sh - startHeight, height = -height))
        if (d) a.add(this.copy())
        return a
    }
    fun pointMirror(d:Boolean): MutableList<SpookyWall> {
        val a = mutableListOf<SpookyWall>()
        a.addAll(this.mirror(false).flatMap { it.verticalMirror(d =false) })
        if (d) a.add(this.copy())
        return a
    }

    /** splits the wall to the given amount per beat */
    fun splitToBeat(): Array<SpookyWall>{
        //todo make the global var a requirement
        val a = 1/((js("JSON.stringify(spookyData.cursorPrecision)") as String).toDoubleOrNull()?:1.0)
        val total = (a*duration).toInt()
        val tempArr = arrayListOf<SpookyWall>()
        repeat(total){
            val curr = it.toDouble()
            tempArr.add(this.copy(
                startTime = startTime + curr/a,
                duration = 1.0/a
            ))
        }
        return tempArr.toTypedArray()
    }
    fun curveInWall(): Array<SpookyWall>{
        //todo move the amount out of here and directly use the dynamic
        val a = 1/((js("JSON.stringify(spookyData.cursorPrecision)") as String).toDoubleOrNull()?:1.0)
        val l = splitToBeat()
        val w = wave((duration*a).toInt())
        for ((index, wall) in l.withIndex()){
            wall.startHeight += height * w[index]
            wall.height=(w[index+1]-w[index])*height
        }
        return l
    }

    fun outline(): Array<SpookyWall> {
        return  arrayOf(
            //first 4
            this.copy(duration = 0.0,height = 0.0),
            this.copy(startHeight=startHeight+height,duration = 0.0,height = 0.0),
            this.copy(duration= 0.0,width = 0.0),
            this.copy(startRow= startRow+width, duration= 0.0,width = 0.0),
            // the long boys
            this.copy(width=0.0,height = 0.0),
            this.copy(startRow=startRow+width,width=0.0,height = 0.0),
            this.copy(startHeight = startHeight+height,width=0.0,height = 0.0),
            this.copy(startRow=startRow+width,startHeight = startHeight+height,width=0.0,height = 0.0),
            // last 4
            this.copy(startTime=startTime+duration,duration = 0.0,height = 0.0),
            this.copy(startTime=startTime+duration,startHeight=startHeight+height,duration = 0.0,height = 0.0),
            this.copy(startTime=startTime+duration,duration= 0.0,width = 0.0),
            this.copy(startTime=startTime+duration,startRow= startRow+width, duration= 0.0,width = 0.0)
        )
    }
    fun randomNoise(): Array<SpookyWall> {
        val amount = (width*height*duration)
        val arrayList = arrayListOf<SpookyWall>()
        repeat(amount.toInt()){
            arrayList.add(SpookyWall(
                startTime=startTime+Random.nextDouble(duration),
                duration = 0.0,
                startHeight = startHeight+ Random.nextDouble(height),
                height = 0.0,
                startRow = startRow + Random.nextDouble(width),
                width = 0.0
            ))
        }
        return arrayList.toTypedArray()
    }


    fun scale(s:Double) = this.copy(
        duration= if(duration>0) duration*s else duration,
        startTime = startTime*s)
    fun verticalScale(s:Double) = this.copy(
        startRow= startRow*s,
        height = height*s,
        startHeight= startHeight*s
    )
    fun extendX(a:Double) = this.copy(
        width = a-startRow
    )
    fun extendY(a:Double) = this.copy(
        height = a-startHeight
    )
    fun extendZ(a: Double) = this.copy(
        duration = a - startTime
    )
    fun time(a:Double) = this.copy(
        startTime = startTime+a
    )
    fun repeat(a: Int, o: Double = 1.0): MutableList<SpookyWall> {
        val list = mutableListOf<SpookyWall>()
        for (i in 0 until a){
            list.add(this.copy(startTime= this.startTime + i*o))
        }
        return list
    }
    fun split(a: Int): MutableList<SpookyWall> {
        val list = mutableListOf<SpookyWall>()
        for (i in 0 until a){
            if (this.height>this.width)
                list.add(this.copy(startHeight = startHeight+height* i / a,height = 1.0/a))
            else
                list.add(this.copy(startRow = startRow + width * i / a,width = 1.0/a))
        }
        return list
    }


    ////////////////////
    constructor(
        _time: Double,
        _duration: Double,
        _lineIndex: Int,
        _type: Int,
        _width: Int
    ): this(
        _time,
        _duration,
        getStartHeight(_type),
        getHeight(_type),
        getLineIndex(_lineIndex),
        getWidth(_width)
    )
    constructor(w:dynamic): this(
        _time = w._time,
        _duration = w._duration,
        _lineIndex = w._lineIndex,
        _type = w._type,
        _width = w._width
    )
}

