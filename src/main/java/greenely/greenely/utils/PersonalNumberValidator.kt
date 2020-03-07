package greenely.greenely.utils

import android.util.Log
import greenely.greenely.utils.InvalidPersonalNumberException.*


sealed class InvalidPersonalNumberException(message: String = "") : Exception(message) {
    class InvalidLengthException : InvalidPersonalNumberException()
    class NotAllDigitsException : InvalidPersonalNumberException()
    class InvalidCheckNumberException : InvalidPersonalNumberException()
}

fun String.validatePersonalNumber() {
    if (!this.isCorrectLength()) throw InvalidLengthException()
    if (!this.isAllDigits()) throw NotAllDigitsException()

    val digits = this.toArrayOfInts().drop(2).dropLast(1)
    val productList = digits.createProductList()
    val digitSum = productList.sumOfDigits()
    val checkSum = subtractSingularFromTen(digitSum)

    if (Integer.parseInt(this.last().toString()) != checkSum) throw InvalidCheckNumberException()
}


private fun String.isCorrectLength() = this.length == 12
private fun String.isAllDigits() = this.all { it.isDigit() }
private fun String.toArrayOfInts() = this.toCharArray().map { Integer.parseInt(it.toString()) }

private fun List<Int>.createProductList() =
        this.mapIndexed { index: Int, value: Int ->
            if (index.rem(2) == 0) value * 2
            else value * 1
        }

private fun List<Int>.sumOfDigits() =
        this.joinToString("")
                .map { Integer.parseInt(it.toString()) }
                .fold(0) { acc: Int, x: Int ->
                    acc + x
                }

private fun subtractSingularFromTen(digitSum: Int) =
        (10 - Integer.parseInt(digitSum.toString().last().toString())).let { subtracted ->
            if (subtracted == 10) 0
            else subtracted
        }
