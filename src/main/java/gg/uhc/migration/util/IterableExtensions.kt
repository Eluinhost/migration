package gg.uhc.migration.util

inline fun <T> Iterable<T>.tap(f: (T) -> Any): Iterable<T> {
    this.forEach { f(it) }
    return this
}