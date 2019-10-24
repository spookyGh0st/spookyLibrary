import kotlin.math.*
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

fun cursorPrecision() =
    (1.0/data.cursorPrecision as Double).toInt()

fun adjust_getNumber(): Double =
    if(data.strobeInterval as Double == 0.5)
        - data.strobeDuration as Double
    else
        data.strobeDuration as Double



fun BPMmultiplier() =
    1/data.currentBPM as Double * data.BPM as Double

fun buildBezier(p0:Point3d,p1:Point3d,p2:Point3d,p3:Point3d, amount: Int): ArrayList<SpookyWall> {
    val list = arrayListOf<SpookyWall>()
    repeat(amount) {
        val currentPoint = quadraticBezier(p0, p1, p2, p3, it.toDouble() / amount)
        val nextPoint = quadraticBezier(p0, p1, p2, p3, (it + 1.0) / amount)
        val startRow = currentPoint.x
        val startHeight = currentPoint.y
        val startTime = currentPoint.z
        val width = nextPoint.x - currentPoint.x
        val height = nextPoint.y - currentPoint.y
        val duration = nextPoint.z - currentPoint.z
        list.add(
            SpookyWall(
                startTime = startTime,
                duration = duration,
                startHeight = startHeight,
                height = height,
                startRow = startRow,
                width = width
            )
        )
    }
    return list
}
fun line(p0: Point3d, p1:Point3d, amount: Int): ArrayList<SpookyWall> {
    return line(p0.x, p0.y, p0.z, p1.x, p1.y, p1.z, amount, null)
}

private fun line(px1:Double, py1:Double, pz1: Double= 0.0, px2: Double, py2: Double, pz2: Double=0.0, defaultAmount: Int? = null, defaultDuration: Double? = null): ArrayList<SpookyWall>{

    //swap values if y2 < y1  - this functions goes from bottom to top
    var x1 = px1
    var x2 = px2
    var y1 = py1
    var y2 = py2
    var z1 = pz1
    var z2 = pz2

    val a= abs(y2-y1)
    val c = sqrt(abs(x2-x1).pow(2) + abs(z2-z1).pow(2))
    val b = sqrt(a.pow(2) + c.pow(2))
    val dgr = asin(a/b)



    val amount = defaultAmount?:((cos(dgr)*sin(dgr)).pow(1.5)*50 +1).toInt()

    val list = arrayListOf<SpookyWall>()

    if(z2<z1){
        x1 = x2.also { x2 = x1 }
        y1 = y2.also { y2 = y1 }
        z1 = z2.also { z2 = z1 }
    }

    //setting the solid values
    val w = (abs(x2-x1)/amount)
    val width = w
    val h = (abs(y2-y1)/amount)
    val height = h
    val d = (abs(z2-z1)/amount)
    val duration = d

    for(i in 0 until amount){
        //setting the dynamic values
        val startHeight =
            if(y2 > y1)
                y1 + i* h
            else
                y1 - (i+1) * h
        val startRow =
            if(x2 > x1)
                x1 + i * w
            else
                x1 - (i+1) * w
        val startTime = z1 + i*d

        //adding the obstacle
        val myD = defaultDuration ?: duration
        list.add(SpookyWall(
            startTime = startTime,
            duration = myD,
            startHeight = startHeight,
            height = height,
            startRow = startRow,
            width = width
        ))
    }
    return list
}
