package de.bitmarck.bms.base32

import de.bitmarck.bms.base32.Alphabets.Base32Alphabet

// https://espadrine.github.io/blog/posts/a-base32-checksum.html
// https://github.com/espadrine/base32check/blob/master/1.js
// https://github.com/christian-schlichtherle/base32check/blob/master/1.js
trait Base32Check1 {

  import Base32Check1._

  protected def primitive: IntVector

  // = IntVector(primitive^0, primitive^1, ... primitive^(Cardinal - 2))
  private lazy val primitivePowers = {
    val first :+ last = IntVector.iterate(primitive, Cardinal - 1)(_ * primitive)
    last +: first
  }

  final def validate(payload: String,
                     alphabet: Base32Alphabet = Alphabets.Base32RFC4648): Boolean =
    compute(payload, alphabet) == 'A'

  final def compute(payload: String,
                    alphabet: Base32Alphabet = Alphabets.Base32RFC4648): Char = {
    val len = payload.length

    val sum = payload.zipWithIndex.foldLeft(0) {
      case (sum, (char, i)) =>
        val value = alphabet.toIndex(char)
        sum ^ (IntVector(value) * primitivePowers((i + 1) % (Cardinal - 1))).head
    }

    val exp = (Cardinal - 2 - len) % (Cardinal - 1) match {
      case exp if exp < 0 => exp + Cardinal - 1
      case exp => exp
    }

    alphabet.toChar((IntVector(sum) * primitivePowers(exp)).head)
  }
}

object Base32Check1 extends Base32Check1 {
  private val Cardinal = 1 << 5

  override protected def primitive: IntVector = IntVector(
    0x01, //bin"00001",
    0x11, //bin"10001",
    0x08, //bin"01000",
    0x05, //bin"00101",
    0x03, //bin"00011",
  )

  type IntVector = IndexedSeq[Int]
  val IntVector: IndexedSeq.type = IndexedSeq

  implicit class IntVectorOps(val a: IntVector) extends AnyVal {
    def *(b: IntVector): IntVector = {
      a.map { ai =>
        b.zipWithIndex.foldLeft(0) {
          case (sum, (bj, j)) if (ai & (1 << b.length - 1 - j)) != 0 =>
            sum ^ bj

          case (sum, _) => sum
        }
      }
    }
  }

}
