package wapuniverse.extensions

import org.reactfx.util.Tuple2

operator fun <A, B> Tuple2<A, B>.component1(): A = this._1

operator fun <A, B> Tuple2<A, B>.component2(): B = this._2
