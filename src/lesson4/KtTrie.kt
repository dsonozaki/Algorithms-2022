package lesson4

import lesson3.BinarySearchTree
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
                current.children[char] = newChild
                newChild.parent = current
                newChild.ch = char
                current = newChild
            }
        }
        if (modified) {
            size++
        }
        return modified
    }

    override fun remove(element: String): Boolean {
        val current = findNode(element) ?: return false
        if (current.children.remove(0.toChar()) != null) {
            size--
            print("removed $element")
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
        private var stack = Stack<String>()
        private var result = ""
        private var node = root
        private var next = ""
        private var overuse = false

        init {
            initiation();
            stack.reverse()
        }

        private fun initiation() {
            for (child in node.children) {
                if (child.key == 0.toChar()) {
                    var n = node
                    result += node.ch
                    while (n.parent?.ch != null) {
                        result += n.parent!!.ch
                        n = n.parent!!
                    }
                    stack.push(result.reversed())
                    result = ""
                } else {
                    node = child.value
                    initiation()
                }
            }
        }

        // T=O(N) (наверное, зависит от размера stack)
        // R=O(1)
        override fun hasNext(): Boolean {
            return (stack.isNotEmpty())
        }

        // T=O(1)
        // R=O(1)
        override fun next(): String {
            if (!hasNext()) throw NoSuchElementException()
            next = stack.pop()
            return (next)
        }

        // T=O(1)
        // R=O(1)
        override fun remove() {
            if (next.isBlank() || overuse) throw IllegalStateException()
            remove(next)
            overuse = true
        }

    }
}