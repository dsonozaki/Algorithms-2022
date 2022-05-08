package lesson5

import java.util.NoSuchElementException
import java.util.Stack

/**
 * Множество(таблица) с открытой адресацией на 2^bits элементов без возможности роста.
 */
class KtOpenAddressingSet<T : Any>(private val bits: Int) : AbstractMutableSet<T>() {
    init {
        require(bits in 2..31)
    }

    private val capacity = 1 shl bits

    private val storage = Array<Any?>(capacity) { null }

    override var size: Int = 0

    /**
     * Индекс в таблице, начиная с которого следует искать данный элемент
     */
    private fun T.startingIndex(): Int {
        return hashCode() and (0x7FFFFFFF shr (31 - bits))
    }

    private val Removed = object {
    }

    /**
     * Проверка, входит ли данный элемент в таблицу
     */
    override fun contains(element: T): Boolean {
        var index = element.startingIndex()
        var current = storage[index]
        while (current != null) {
            if (current == element) {
                return true
            }
            index = (index + 1) % capacity
            current = storage[index]
        }
        return false
    }

    /**
     * Добавление элемента в таблицу.
     *
     * Не делает ничего и возвращает false, если такой же элемент уже есть в таблице.
     * В противном случае вставляет элемент в таблицу и возвращает true.
     *
     * Бросает исключение (IllegalStateException) в случае переполнения таблицы.
     * Обычно Set не предполагает ограничения на размер и подобных контрактов,
     * но в данном случае это было введено для упрощения кода.
     */
    override fun add(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var index = startingIndex
        var current = storage[index]
        while (current != null && current != Removed) {
            if (current == element) {
                return false
            }
            index = (index + 1) % capacity
            check(index != startingIndex) { "Table is full" }
            current = storage[index]
        }
        storage[index] = element
        size++
        return true
    }

    /**
     * Удаление элемента из таблицы
     *
     * Если элемент есть в таблице, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     *
     * Средняя
     */
    // T=O(n) - худший случай
    // R=O(1)
    override fun remove(element: T): Boolean {
        var index = element.startingIndex()
        var current = storage[index]
        while (current != null) {
            if (current == element) {
                storage[index] = Removed
                size--
                return true
            }
            index = (index + 1) % capacity
            current = storage[index]
        }
        return false
    }

    /**
     * Создание итератора для обхода таблицы
     *
     * Не забываем, что итератор должен поддерживать функции next(), hasNext(),
     * и опционально функцию remove()
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Средняя (сложная, если поддержан и remove тоже)
     */
    override fun iterator(): MutableIterator<T> {
        return AddressingSetIterator()
    }

    inner class AddressingSetIterator<T1> : MutableIterator<T1> {
        private var stack = Stack<Pair<T1, Int>>()
        private var next = Pair<T1?, Int>(null, -1)
        private var overuse = false

        init {
            initiation(0)
            stack.reverse()
        }

        private fun initiation(start: Int) {
            var i = start
            while (i <= storage.lastIndex) {
                if (storage[i] != null && storage[i] != Removed) {
                    stack.push(Pair(storage[i] as T1, i))
                    break
                }
                i++
            }
        }

        // T=O(1)
        // R=O(1)
        override fun hasNext(): Boolean {
            return (stack.isNotEmpty())
        }

        // T=O(n) - худший случай
        // R=O(1)
        override fun next(): T1 {
            if (!hasNext()) throw NoSuchElementException()
            next = stack.pop()!!
            initiation(next.second + 1)
            return (next.first as T1)
        }

        //T=O(n) - худший случай (совпадает с функцией remove вне итератора)
        //R=O(1)
        override fun remove() {
            if (next.first == null || overuse) throw IllegalStateException()
            remove(next.first as T)
            overuse = true
        }

    }
}
