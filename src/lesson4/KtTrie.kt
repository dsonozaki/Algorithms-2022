package lesson4

import java.io.File
import java.util.*

/**
 * Префиксное дерево для строк
 */
class KtTrie : AbstractMutableSet<String>(), MutableSet<String> {

    private class Node {
        val children: SortedMap<Char, Node> = sortedMapOf()
        var parent: Node? = null
        var ch: Char? = null
    }

    private val root = Node()

    override var size: Int = 0
        private set

    override fun clear() {
        root.children.clear()
        size = 0
    }

    private fun String.withZero() = this + 0.toChar()

    private fun findNode(element: String): Node? {
        var current = root
        for (char in element) {
            current = current.children[char] ?: return null
        }
        return current
    }

    override fun contains(element: String): Boolean =
        findNode(element.withZero()) != null

    override fun add(element: String): Boolean {
        var current = root
        var modified = false
        for (char in element.withZero()) {
            val child = current.children[char]
            if (child != null) {
                current = child
            } else {
                modified = true
                var newChild = Node()
                newChild.parent = current
                newChild.ch = char
                current.children[char] = newChild
                current = newChild
            }
        }
        if (modified) {
            size++
        }
        return modified
    }

    override fun remove(element: String): Boolean {
        var current = findNode(element) ?: return false
        if (current.children.remove(0.toChar()) != null) {
            size--
            print("removed $element")
            while (current != root && current.children.size == 0) {
                println("удаляется " + current.ch)
                current.parent?.children?.remove(current.ch)
                current = current.parent!!
            }
            return true
        }
        return false
    }

    /**
     * Итератор для префиксного дерева
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Сложная
     */
    override fun iterator(): MutableIterator<String> {
        return TrieIterator();
    }

    inner class TrieIterator : MutableIterator<String> {
        private var stack = Stack<Pair<Node, String>>()
        private var next = Pair<Node?, String>(null, "")
        private var overuse = false
        val writer = File("input/logs.txt").bufferedWriter()

        init {
            initiation(root, "")
        }

        private fun initiation(node: Node, result: String) {
            var n = node
            var r = result
            if (n == root && n.children.isEmpty()) return
            while (n.children.firstKey() != 0.toChar()) {
                val first = n.children.firstKey()
                n = n.children[first]!!
                r += first
            }
            stack.push(Pair(n, r))
            println(r)
        }

        // T=O(1)
        // R=O(1)
        override fun hasNext(): Boolean {
            return (stack.isNotEmpty())
        }

        // T=O(1)
        // R=O(1)
        override fun next(): String {
            if (!hasNext()) throw NoSuchElementException()
            next = stack.pop()
            var string = next.second.reversed()
            var node = next.first!!
            if (node.children.size > 1) {
                var children = node.children
                val iterator = node.children.iterator()
                iterator.next()
                val c = iterator.next().key
                initiation(children[c]!!, string.reversed() + c)
            } else {
                while (string.isNotBlank()) {
                    val chr = string[0]
                    var i = 0
                    var c = -2
                    var new = Pair<Char, Node?>(' ', null)
                    node.parent?.children?.forEach {
                        writer.write(it.key.toString())
                        writer.newLine()
                        if (it.key == chr)
                            c = i
                        if (i == c + 1) {
                            new = it.toPair()
                        }
                        i++
                    }
                    if (new.second != null) {
                        initiation(new.second!!, string.removeRange(0, 1).reversed() + new.first)
                        break
                    }
                    node = node.parent!!
                    string = string.removeRange(0, 1)
                }
            }
            return (next.second)
        }

        // T=O(1)
        // R=O(1)
        override fun remove() {
            if (next.second.isBlank() || overuse) throw IllegalStateException()
            remove(next.second)
            overuse = true
        }

    }
}