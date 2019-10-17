@file:Suppress("unused")

import kotlin.math.pow

fun Array<SpookyWall>.adjustArrayToBpm(): Array<SpookyWall> {
    return map { it.adjustToBPM(
            baseBPM = data.BPM as Double,
            newBPM = data.currentBPM as Double
        )}
        .toTypedArray()
}

fun Array<SpookyWall>.curve():Array<SpookyWall>{
    val points =
        this.map{ it.center() }
            .windowed(4,3,false)
    val amount = cursorPrecision()

    val list = arrayListOf<SpookyWall>()
    for(p in points){
        if(p[3].z<p[0].z){
            throw Exception("You have something wrong with you curve")
        }
        repeat(amount){
            val currentPoint = quadraticBezier(p[0], p[1], p[2], p[3], it.toDouble() / amount)
            val nextPoint = quadraticBezier(p[0], p[1], p[2], p[3], (it + 1.0) / amount)
            val startRow = currentPoint.x
            val startHeight = currentPoint.y
            val startTime = currentPoint.z
            val width = nextPoint.x - currentPoint.x
            val height = nextPoint.y - currentPoint.y
            val duration = nextPoint.z -currentPoint.z
            list.add(SpookyWall(
                startTime = startTime,
                duration = duration,
                startHeight = startHeight,
                height = height,
                startRow = startRow,
                width = width
            ))
        }
    }
    return list.toTypedArray()
}
fun Array<SpookyWall>.defaultCurve():Array<SpookyWall>{
    val points =
        this.map{ it.center() }
            .windowed(4,3,false)
    val amount = cursorPrecision()

    val list = arrayListOf<SpookyWall>()
    for(p in points){
        if(p[3].z<p[0].z){
            throw Exception("You have something wrong with you curve")
        }
        repeat(amount){
            val currentPoint = quadraticBezier(p[0], p[1], p[2], p[3], it.toDouble() / amount)
            val nextPoint = quadraticBezier(p[0], p[1], p[2], p[3], (it + 1.0) / amount)
            val startRow = currentPoint.x
            val startHeight = currentPoint.y
            val startTime = currentPoint.z
            val width = nextPoint.x - currentPoint.x
            val height = nextPoint.y - currentPoint.y
            val duration = nextPoint.z -currentPoint.z
            list.add(SpookyWall(
                startTime = startTime,
                duration = duration,
                startHeight = startHeight,
                height = height,
                startRow = startRow,
                width = width
            ))
        }
    }
    return list.toTypedArray()
}

private fun quadraticBezier(p0: Point3d, p1: Point3d, p2: Point3d, p3: Point3d, t:Double): Point3d {
    val x =(1-t).pow(3)*p0.x +
            (1-t).pow(2)*3*t*p1.x +
            (1-t)*3*t*t*p2.x +
            t*t*t*p3.x
    val y =(1-t).pow(3)*p0.y +
            (1-t).pow(2)*3*t*p1.y +
            (1-t)*3*t*t*p2.y +
            t*t*t*p3.y
    val z =(1-t).pow(3)*p0.z +
            (1-t).pow(2)*3*t*p1.z +
            (1-t)*3*t*t*p2.z +
            t*t*t*p3.z
    return Point3d(x, y, z)
}

