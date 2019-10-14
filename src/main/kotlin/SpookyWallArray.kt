fun Array<SpookyWall>.adjustArrayToBpm(): Array<SpookyWall> {
    return map { it.adjustToBPM(
            baseBPM = data.BPM as Double,
            newBPM = data.currentBPM as Double
        )}
        .toTypedArray()
}