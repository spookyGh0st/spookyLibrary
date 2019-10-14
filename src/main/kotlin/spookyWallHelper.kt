import kotlin.math.PI
import kotlin.math.sin

fun getHeight(i:Int):Double{
    if(i < 500){
        TODO()
    }
    var a = ((i-4001)/1000).toDouble()
    a = a/1000 / (1.0/3.0) * (4.0/3.0)
    return a
}
fun getStartHeight(i:Int):Double{
    if(i<500) return 0.0
    var a = (i % 1000 -1).toDouble()
    a= a/250 * (4.0/3.0)
    return a
}

fun getLineIndex(i:Int):Double =
    if(i>=0)
        (i.toDouble() - 1000)/1000.0 -2.0
    else
        (i.toDouble() +1000)/1000 - 2.0

fun getWidth(i:Int):Double =
    (i.toDouble() -1000) / 1000

fun spookyWalltoWall(a: Array<SpookyWall>):Array<dynamic> =
    a.map { it.toWall() }
        .toTypedArray()

fun wave(amount: Int):ArrayList<Double>{
    val l = arrayListOf<Double>()
    for (i in 0 .. amount)
        l.add( sin(i.toDouble()/amount * PI))
    return l
}

