data class Point2d(val x:Double, val y:Double)

data class Line2d(val p1:Point2d,val p2: Point2d)

data class Point3d(val x:Double, val y:Double, val z:Double){

    /**
     * mirrors the given point to this
     */
    fun mirrorPointToThis (p: Point3d): Point3d {
        val x =  this.x + (this.x-p.x)
        val y =  this.y + (this.y-p.y)
        val z =  this.z + (this.z-p.z)
        return Point3d(x,y,z)
    }
    fun buildWall( p: Point3d):SpookyWall {
        val startTime = this.z
        val duration = p.z-this.z
        val startHeight = this.y
        val height = p.y - this.y
        val startRow = this.x
        val width = p.x-this.x
        return SpookyWall(
            startTime = startTime,
            duration = duration,
            startHeight = startHeight,
            height = height,
            startRow = startRow,
            width = width
        )
    }
}

