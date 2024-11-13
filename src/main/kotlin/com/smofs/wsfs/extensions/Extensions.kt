package com.smofs.wsfs.extensions

import java.nio.ByteBuffer
import java.util.UUID

@Suppress("MagicNumber")
fun UUID.asBytes(): ByteArray {
    val b = ByteBuffer.wrap(ByteArray(16))
    b.putLong(mostSignificantBits)
    b.putLong(leastSignificantBits)
    return b.array()
}
