import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

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


fun curseWand(PointX:Double, PointY:Double) : ArrayList<Line2d>{
    val multip = 3.0
    val list = arrayListOf<Line2d>()
    repeat((PointX*multip).toInt()){
        var x = Random.nextDouble(PointX)
        var y = 0.0
        for(i in 1.. (multip*PointY).toInt()){
            val tx = Random.nextDouble(x-1/multip,x+1/multip).coerceIn(0.0,PointX)
            val ty = Random.nextDouble(y,i/multip)
            list.add(Line2d(Point2d(x,y),Point2d(x,ty)))
            if(tx>x)
            list.add(Line2d(Point2d(x,ty),Point2d(tx,ty)))
            else
                list.add(Line2d(Point2d(tx,ty),Point2d(x,ty)))
            x = tx
            y = ty
        }
    }
    return list
}

