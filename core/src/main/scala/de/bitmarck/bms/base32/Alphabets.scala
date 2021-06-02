package de.bitmarck.bms.base32

object Alphabets {

  trait Base32Alphabet {
    /**
     * Converts the specified index to a character.
     *
     * @throws IndexOutOfBoundsException if the specified byte is not supported by this alphabet
     */
    def toChar(index: Int): Char

    /**
     * Converts the specified char to an index.
     *
     * @throws IllegalArgumentException if the specified char is not supported by this alphabet
     */
    def toIndex(c: Char): Int

    /**
     * Indicates whether the specified character should be ignored.
     */
    def ignore(c: Char): Boolean

    /** Padding character. */
    val pad: Char
  }

  private def charIndicesLookupArray(indicesMap: Map[Char, Int]): (Int, Array[Int]) = {
    val indicesMin: Int = indicesMap.keys.min.toInt
    val indices: Array[Int] = Array.tabulate[Int](indicesMap.keys.max - indicesMin + 1) { i =>
      indicesMap.getOrElse((i + indicesMin).toChar, -1)
    }
    (indicesMin, indices)
  }

  /** Base 32 alphabet as defined by [[https://tools.ietf.org/html/rfc4648#section-6 RF4648 section 4]]. Whitespace is ignored. */
  object Base32RFC4648 extends Base32Alphabet {
    private val Chars: Array[Char] = (('A' to 'Z') ++ ('2' to '7')).toArray
    private val (indicesMin, indices) = charIndicesLookupArray(Chars.zipWithIndex.toMap)
    val pad = '='

    def toChar(i: Int): Char = Chars(i)

    def toIndex(c: Char): Int = {
      val lookupIndex = c - indicesMin
      if (lookupIndex >= 0 && lookupIndex < indices.length && indices(lookupIndex) >= 0)
        indices(lookupIndex)
      else throw new IllegalArgumentException
    }

    def ignore(c: Char): Boolean = c.isWhitespace
  }

}
