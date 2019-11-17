import kotlin.math.*

fun surroundingCurve(): Array<SpookyWall> {
    val n = getSpookyNotes()
    val m = n
        .groupBy { Pair(it.z,it._type) }
        .filter { it.value.size == 2 }
    return m.map { entry ->
        val l = entry.value
        l.sortFromDegree()
        val points = l.map { it.toPoint3d() }
        val z = points.first().z
        val p0 = points.first().mirrorPointToThis(points.last())
        val p3 = points.last().mirrorPointToThis(points.first()).copy(z = z+0.3)

        val midX = points.first().x + points.last().x-points.first().x /2
        val midY = points.first().y + points.last().y-points.first().y /2
        val middlePoint = Point3d(midX,midY,z)
        val orthVektor = Point3d(-(p3.y-p0.y), (p3.x-p0.x),0.0)
        val p1 = middlePoint + orthVektor.mul(0.2)
        p1.z += +0.1
        val p2 = middlePoint + orthVektor.mul(0.2)
        p2.z += 0.2
        buildBezier(p0,p1,p2,p3,8)
    }.flatten().toTypedArray()
}
fun findCenter(l: List<Point3d>): Point3d {
    return Point3d(l[0].x+(l[1].x-l[0].x),
        l[0].y+(l[1].y-l[0].y),
        l[0].z+(l[1].z-l[0].z)
        )
}
fun List<SpookyNotes>.sortFromDegree() {
    when(first().degree) {
        in 0.25*PI .. 0.75*PI -> sortedByDescending { it.x }
        in 0.75*PI .. 1.25*PI -> sortedBy{ it.y }
        in 1.25*PI .. 1.75*PI -> sortedBy { it.x }
        else -> sortedByDescending { it.y }
    }
}

