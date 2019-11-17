import kotlin.math.PI

class SpookyNotes (_time:Double, val  _type: Int, val  _lineLayer: Int, val  _lineIndex: Int, val  _cutDirection: Int){
    val degree = calcDegree()
    val x = calcX()
    val y = calcY()
    val z = _time

    private fun calcDegree(): Double = when(_cutDirection){
        in 1000..1360 -> (_cutDirection - 1000) / 360 * (2* PI)
        1 -> 0.0
        2 -> PI/2
        0 -> PI
        3 -> 1.5*PI
        else -> 0.0
    }
    private  fun calcX(): Double = when(_lineIndex ){
        in -999..999 -> _lineIndex.toDouble()-1.5
        else-> _lineIndex / 1000.0 - 2.5
    }
    private  fun calcY(): Double = when(_lineLayer ){
        in -999..999 -> _lineLayer.toDouble() +1-0.175
        else-> (_lineLayer.toDouble() - 175 ) / 1000
    }
    fun toSpookyWallDot(): SpookyWall {
        return SpookyWall(z,0.0,y,0.0,x,0.0)
    }
    fun toSpookyWallBox(): SpookyWall {
        return SpookyWall(z-0.15,0.3,y-0.4,0.8,x-0.4,0.8)
    }
    fun toPoint3d() = Point3d(x,y,z)
    fun toPoint2d() = Point2d(x,y)

}

fun getSpookyNotes() : Array<SpookyNotes> =
    notes.map {
        SpookyNotes(
        _time = it["_time"] as Double,
        _type =  it["_type"] as Int,
        _lineLayer =  it["_lineLayer"] as Int,
        _lineIndex =  it["_lineIndex"] as Int,
        _cutDirection =  it["_cutDirection"] as Int
    ) }.toTypedArray()

