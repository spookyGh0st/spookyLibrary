@file:Suppress("unused")

import kotlin.math.abs
import kotlin.random.Random

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
            _time = calculateStartTime()
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

    private fun calculateStartTime():Double{
        return if(duration>0)
            this.startTime
        else
            this.startTime+duration
    }

    private fun calculateDuration(): Double{
        return abs(duration).coerceAtLeast(0.0001)
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
        var i = startRow +2
        if(width<0)
            i+=width
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


    internal fun adjustToBPM(baseBPM: Double, newBPM: Double): SpookyWall {
        val tempStartTime = startTime* (baseBPM / newBPM)
        val tempDuration = if(duration > 0)
            duration * (baseBPM / newBPM)
        else
            duration
        return this.copy(startTime = tempStartTime, duration = tempDuration)
    }



    // stuff

    fun center() = Point3d(
        x=startRow+width/2,
        y=startHeight+height/2,
        z=startTime+duration/2
    )

    fun fuckUp() =
        SpookyWall(
            startTime = ra(startTime),
            duration = ra(duration),
            startHeight = ra(startHeight),
            height = ra(height),
            startRow = ra(startRow),
            width = ra(width)
        )

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
        val a = cursorPrecision()
        val total = (a*duration).toInt()
        val tempArr = arrayListOf<SpookyWall>()
        repeat(total){
            val curr = it.toDouble()
            tempArr.add(this.copy(
                startTime = curr/a,
                duration = 1.0/a
            ))
        }
        val adjustedArr = tempArr.toTypedArray().adjustArrayToBpm()
        adjustedArr.forEach { it.startTime+=startTime }
        return adjustedArr
    }
    fun curveInWall(a:Int=1): Array<SpookyWall>{
        //todo move the amount out of here and directly use the dynamic add to DOCU
        val l = splitToBeat()
        val w = wave((duration*a).toInt())
        for ((index, wall) in l.withIndex()){
            wall.startHeight += height * w[index]
            wall.height=(w[index+1]-w[index])*height
        }
        return l
    }

    fun randomCurveInWall(amountPerBeat:Int = 8): Array<SpookyWall> {
        val w = this.splitToBeat().map { it }
        val finalWalls = arrayListOf<SpookyWall>()
        var last: Point3d? = null
        var lastContr: Point3d? = null

        for ( wall in w){

            val p0 = (last?:wall.randomTimedPoint(0))
            val p1: Point3d
            p1 = if(lastContr == null)
                wall.randomTimedPoint(1)
            else
                p0.mirrorPointToThis(lastContr)
            val p2 = wall.randomTimedPoint(2)
            lastContr = p2

            val p3 = wall.randomTimedPoint(3)
            last = p3

            finalWalls.addAll(buildBezier(p0,p1,p2,p3,amountPerBeat))
        }
        return finalWalls.toTypedArray()
    }

    fun randomWalls(amount:Int = cursorPrecision()): Array<SpookyWall> {
        val l = arrayListOf<SpookyWall>()
        repeat(amount){
            val p1 = this.randomPoint()
            val p2 = this.randomPoint()
            l.add(p1.buildWall(p2))
        }
        return l.toTypedArray()
    }

    private fun randomTimedPoint(t: Int, max:Int=3): Point3d {
        return Point3d(
            x = this.startRow+Random.nextDouble(this.width),
            y = this.startHeight+Random.nextDouble(this.height),
            z = this.startTime+ t/max.toDouble()*duration
        )
    }
    private fun randomPoint(): Point3d {
        return Point3d(
            x = this.startRow+Random.nextDouble(this.width),
            y = this.startHeight+Random.nextDouble(this.height),
            z = this.startTime+ Random.nextDouble(this.duration)
        )
    }

    fun curse(): Array<SpookyWall>{
        val tempArr = arrayListOf<SpookyWall>()
        var lines = curseWand(duration,height)
        for (l in lines){
            tempArr.add(this.copy(
                startTime = startTime+ l.p1.x,
                duration = l.p2.x-l.p1.x,
                startHeight = startHeight+l.p1.y,
                height = l.p2.y - l.p1.y,
                width = 0.0
            ))
        }
        lines = curseWand(duration,height)
        for (l in lines){
            tempArr.add(this.copy(
                startTime = startTime+ l.p1.x,
                duration = l.p2.x-l.p1.x,
                startHeight = startHeight+l.p1.y,
                height = l.p2.y - l.p1.y,
                width = 0.0,
                startRow = startRow+width
            ))
        }
        return tempArr.toTypedArray()
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

fun createSpookyWall(startTime:Double,duration: Double,startHeight:Double,height: Double,startRow: Double,width: Double) = SpookyWall(startTime, duration, startHeight, height, startRow, width)

fun main(){
    val a = SpookyWall(0.0,2.0,1.0,1.0,0.0,2.0)
    println(a.randomWalls(4))
}

