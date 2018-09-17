/*
 * Terrarum Sans Bitmap
 *
 * Copyright (c) 2018 Minjae Song (Torvald)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.torvald.terrarumsansbitmap.gdx

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap

/**
 * Created by minjaesong on 2018-09-17.
 */
class PixmapRegionPack(
        pixmap: Pixmap,
        val tileW: Int,
        val tileH: Int,
        val hGap: Int = 0,
        val vGap: Int = 0,
        val hFrame: Int = 0,
        val vFrame: Int = 0,
        val xySwapped: Boolean = false // because Unicode chart does, duh
) {

    //constructor(ref: String, tileW: Int, tileH: Int, hGap: Int = 0, vGap: Int = 0, hFrame: Int = 0, vFrame: Int = 0, xySwapped: Boolean = false) :
    //        this(Pixmap(ref), tileW, tileH, hGap, vGap, hFrame, vFrame, xySwapped)
    constructor(fileHandle: FileHandle, tileW: Int, tileH: Int, hGap: Int = 0, vGap: Int = 0, hFrame: Int = 0, vFrame: Int = 0, xySwapped: Boolean = false) :
            this(Pixmap(fileHandle), tileW, tileH, hGap, vGap, hFrame, vFrame, xySwapped)

    val horizontalCount = (pixmap.width - 2 * hFrame + hGap) / (tileW + hGap)
    val verticalCount = (pixmap.height - 2 * vFrame + vGap) / (tileH + vGap)

    val regions: Array<Pixmap>

    init {
        if (!xySwapped) {
            regions = Array<Pixmap>(horizontalCount * verticalCount, {
                val region = Pixmap(tileW, tileH, Pixmap.Format.RGBA8888)
                val rx = (it % horizontalCount * (tileW + hGap)) + hFrame // pixel, not index
                val ry = (it / horizontalCount * (tileH + vGap)) + vFrame // pixel, not index

                /*region.setColor(Color.WHITE)
                region.drawPixmap(pixmap, 0, 0,
                        rx * (tileW + hGap),
                        ry * (tileH + vGap),
                        tileW,
                        tileH
                )*/

                region.pixels.rewind()

                //println(region.pixels.capacity())

                //val bytesBucket = ByteArray(4 * tileW * tileH)
                //var bytesBucketHead = 0

                for (y in 0 until tileH) {
                    //println("${pixmap.width * pixmap.height * 4} ==? ${pixmap.pixels.capacity()}")

                    val offsetY = ry * (pixmap.width * 4) + (vFrame * pixmap.width * 4)
                    val offsetX = rx * 4 + hFrame * 4

                    //println("($rx, $ry): ${offsetY + offsetX} / ${pixmap.pixels.capacity()}; $offsetY, $offsetX")

                    val bytesBuffer = ByteArray(4 * tileW)

                    pixmap.pixels.position(offsetY + offsetX)
                    pixmap.pixels.get(bytesBuffer, 0, bytesBuffer.size)
                    region.pixels.put(bytesBuffer)
                }

                // todo globalFlipY ?

                /*return*/region
            })
        }
        else {
            regions = Array<Pixmap>(horizontalCount * verticalCount, {
                val region = Pixmap(tileW, tileH, Pixmap.Format.RGBA8888)
                val rx = (it / verticalCount * (tileW + hGap)) + hFrame
                val ry = (it % verticalCount * (tileH + vGap)) + vFrame

                region.drawPixmap(pixmap, 0, 0,
                        rx * (tileW + hGap),
                        ry * (tileH + vGap),
                        tileW, tileH
                )

                // todo globalFlipY ?

                /*return*/region
            })
        }
    }

    fun get(x: Int, y: Int) = regions[y * horizontalCount + x]

    fun dispose() {
        regions.forEach { it.dispose() }
    }

}