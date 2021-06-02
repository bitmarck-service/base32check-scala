package de.bitmarck.bms.base32

import java.nio.{ByteBuffer, CharBuffer}

import de.bitmarck.bms.base32.Alphabets.Base32Alphabet

object Base32 {
  /**
   * Selects at most 8 bits from a byte array as a right aligned byte
   */
  private def bitsAtOffset(bytes: IndexedSeq[Byte], bitIndex: Long, length: Int): Int = {
    val i = (bitIndex / 8).toInt
    if (i >= bytes.length) 0
    else {
      val off = (bitIndex - (i * 8)).toInt
      val mask = ((1 << length) - 1) << (8 - length)
      val half = (bytes(i) << off) & mask
      val full =
        if (off + length <= 8 || i + 1 >= bytes.length) half
        else half | ((bytes(i + 1) & ((mask << (8 - off)) & 0xFF)) >>> (8 - off))
      full >>> (8 - length)
    }
  }

  /**
   * Converts the contents of this vector to a base 32 string using the specified alphabet.
   *
   * @group conversions
   */
  def encode(bytes: IndexedSeq[Byte], alphabet: Base32Alphabet = Alphabets.Base32RFC4648): String = {
    val bitsPerChar = 5
    val bytesPerGroup = 5
    val charsPerGroup = bytesPerGroup * 8 / bitsPerChar

    val bldr = CharBuffer.allocate((bytes.length + bytesPerGroup - 1) / bytesPerGroup * charsPerGroup)

    {
      var bidx: Long = 0
      while ((bidx / 8) < bytes.length) {
        val char = alphabet.toChar(bitsAtOffset(bytes, bidx, bitsPerChar))
        bldr.append(char)
        bidx += bitsPerChar
      }
    }

    if (alphabet.pad != 0.toChar) {
      val padLen = (((bytes.length + bitsPerChar - 1) / bitsPerChar * bitsPerChar) - bytes.length) * 8 / bitsPerChar
      var i = 0
      while (i < padLen) {
        bldr.append(alphabet.pad)
        i += 1
      }
    }

    bldr.flip.toString
  }

  /**
   * Constructs a `ByteVector` from a base 32 string or returns an error message if the string is not valid base 32.
   * An empty input string results in an empty ByteVector.
   * The string may contain whitespace characters and hyphens which are ignored.
   *
   * @group base
   */
  def decodeDescriptive(str: String,
                        alphabet: Base32Alphabet = Alphabets.Base32RFC4648): Either[String, Array[Byte]] = {
    val bitsPerChar = 5
    val bytesPerGroup = 5
    val charsPerGroup = bytesPerGroup * 8 / bitsPerChar

    val Pad = alphabet.pad
    var idx, bidx, buffer, padding = 0
    val acc = ByteBuffer.allocate((str.length + charsPerGroup - 1) / charsPerGroup * bytesPerGroup)
    while (idx < str.length) {
      val c = str(idx)

      if (Pad != 0.toChar && c == Pad) {
        padding += 1
      } else if (!alphabet.ignore(c)) {
        if (padding > 0)
          return Left(
            s"Unexpected character '$c' at index $idx after padding character; only '=' and whitespace characters allowed after first padding character"
          )

        val index =
          try {
            alphabet.toIndex(c)
          } catch {
            case _: IllegalArgumentException =>
              return Left(s"Invalid base 32 character '$c' at index $idx")
          }

        buffer |= (index << (8 - bitsPerChar) >>> bidx) & 0xFF
        bidx += bitsPerChar

        if (bidx >= 8) {
          bidx -= 8
          acc.put(buffer.toByte)
          buffer = (index << (8 - bidx)) & 0xFF
        }
      }

      idx += 1
    }

    if (bidx >= bitsPerChar)
      acc.put(buffer.toByte)

    acc.flip()
    val bytes = new Array[Byte](acc.remaining())
    acc.get(bytes)

    val expectedPadding = (((bytes.length + bitsPerChar - 1) / bitsPerChar * bitsPerChar) - bytes.length) * 8 / bitsPerChar
    if (padding != 0 && padding != expectedPadding)
      return Left(
        s"Malformed padding - optionally expected $expectedPadding padding characters such that the quantum is completed"
      )

    Right(bytes)
  }

  /**
   * Constructs a `ByteVector` from a base 32 string or throws an IllegalArgumentException if the string is not valid base 32.
   * Details pertaining to base 32 decoding can be found in the comment for fromBase32Descriptive.
   * The string may contain whitespace characters which are ignored.
   *
   * @throws IllegalArgumentException if the string is not valid base 32
   * @group base
   */
  def decode(str: String,
             alphabet: Base32Alphabet = Alphabets.Base32RFC4648): Array[Byte] =
    decodeDescriptive(str, alphabet)
      .fold(msg => throw new IllegalArgumentException(msg), identity)
}
