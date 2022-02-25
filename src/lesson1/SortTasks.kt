@file:Suppress("UNUSED_PARAMETER")

package lesson1

import java.io.File
import java.time.LocalTime
import java.time.format.DateTimeFormatter


/**
 * Сортировка времён
 *
 * Простая
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
 * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
 *
 * Пример:
 *
 * 01:15:19 PM
 * 07:26:57 AM
 * 10:00:03 AM
 * 07:56:14 PM
 * 01:15:19 PM
 * 12:40:31 AM
 *
 * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
 * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
 *
 * 12:40:31 AM
 * 07:26:57 AM
 * 10:00:03 AM
 * 01:15:19 PM
 * 01:15:19 PM
 * 07:56:14 PM
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
// T=O(N*log(N)) - стандартная сортировка Kotlin
// R=O(N)
fun sortTimes(inputName: String, outputName: String) {
    val pattern = DateTimeFormatter.ofPattern("hh:mm:ss a")
    val writer = File(outputName).bufferedWriter()
    var result = File(inputName).readLines()
    if (result.size == 1)
        LocalTime.parse(result[0], pattern)
    else {
        result = result.sortedBy { LocalTime.parse(it, pattern) }
    }
    for (line in result)
        writer.appendLine(line)
    writer.close()
}

/**
 * Сортировка адресов
 *
 * Средняя
 *
 * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
 * где они прописаны. Пример:
 *
 * Петров Иван - Железнодорожная 3
 * Сидоров Петр - Садовая 5
 * Иванов Алексей - Железнодорожная 7
 * Сидорова Мария - Садовая 5
 * Иванов Михаил - Железнодорожная 7
 *
 * Людей в городе может быть до миллиона.
 *
 * Вывести записи в выходной файл outputName,
 * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
 * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
 *
 * Железнодорожная 3 - Петров Иван
 * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
 * Садовая 5 - Сидоров Петр, Сидорова Мария
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
// T=O(С*N*log(N)) - стандартная сортировка Kotlin + операции над отсортированным массивом
// R=O(N)
fun sortAddresses(inputName: String, outputName: String) {
    val writer = File(outputName).bufferedWriter()
    val spliter = " - "
    var list = File(inputName).readLines()
    val regex = "[А-Яа-яё-]+ [А-Яа-яё-]+ - [А-Яа-яё-]+ \\d+".toRegex()
    if (list.size == 1) {
        if (!list[0].matches(regex))
            throw Exception("wrong input")
        else {
            val newline = list[0].split(spliter)
            val result = newline[1] + spliter + newline[0]
            writer.appendLine(result)
            writer.close()
            return
        }
    } else {
        list = list.sortedBy {
            if (!it.matches(regex)) {
                println(it)
                throw Exception("wrong input")
            }
            val c = it.split(spliter)
            val k = c[1].split(" ")
            k[0] + " " + k[1].length + k[1] + spliter + c[0] //добавляем перед номером дома длину номера, чтобы более длинные числа при сортировке оказывались после коротких вне зависимости от их первой цифры
        }
    }
    var temp = ""
    for (i in 0..list.lastIndex) {
        val newline = list[i].split(spliter)
        if (i != list.lastIndex) {
            val next = list[i + 1].split(spliter)
            temp = if (newline[1] == next[1]) {
                "$temp${newline[0]}, "
            } else {
                val result = newline[1] + spliter + temp + newline[0]
                writer.appendLine(result)
                ""
            }
        } else {
            val result = newline[1] + spliter + temp + newline[0]
            writer.appendLine(result)
        }
    }
    writer.close()
}

/**
 * Сортировка температур
 *
 * Средняя
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
 * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
 * Например:
 *
 * 24.7
 * -12.6
 * 121.3
 * -98.4
 * 99.5
 * -12.6
 * 11.0
 *
 * Количество строк в файле может достигать ста миллионов.
 * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
 * Повторяющиеся строки сохранить. Например:
 *
 * -98.4
 * -12.6
 * -12.6
 * 11.0
 * 24.7
 * 99.5
 * 121.3
 */
// T=O(N) - сортировка подсчётом
// R=O(N)
fun sortTemperatures(inputName: String, outputName: String) {
    val size = 7731
    val array = mutableListOf<Int>()
    val writer = File(outputName).bufferedWriter()
    val reader = File(inputName).readLines().forEach { array.add((it.toDouble() * 10 + 2730).toInt()) }
    val result1 = countingSort(array.toIntArray(), size)
    for (i in result1) {
        writer.appendLine(((i - 2730).toDouble() / 10.0).toString())
    }
    writer.close()
}

/**
 * Сортировка последовательности
 *
 * Средняя
 * (Задача взята с сайта acmp.ru)
 *
 * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
 *
 * 1
 * 2
 * 3
 * 2
 * 3
 * 1
 * 2
 *
 * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
 * а если таких чисел несколько, то найти минимальное из них,
 * и после этого переместить все такие числа в конец заданной последовательности.
 * Порядок расположения остальных чисел должен остаться без изменения.
 *
 * 1
 * 3
 * 3
 * 1
 * 2
 * 2
 * 2
 */
fun sortSequence(inputName: String, outputName: String) {
    TODO()
}

/**
 * Соединить два отсортированных массива в один
 *
 * Простая
 *
 * Задан отсортированный массив first и второй массив second,
 * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
 * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
 *
 * first = [4 9 15 20 28]
 * second = [null null null null null 1 3 9 13 18 23]
 *
 * Результат: second = [1 3 4 9 9 13 15 20 23 28]
 */
fun <T : Comparable<T>> mergeArrays(first: Array<T>, second: Array<T?>) {
    TODO()
}

