package ru.arsmagna.direct;

/**
 * Справочник в N01/L01 является таблицей, определяющей
 * поисковый термин. Каждый ключ переменной длины, который
 * есть в записи, представлен в справочнике одним входом,
 * формат которого описывает следующая структура
 */
public class NodeItem {

    /**
     * Длина ключа.
     */
    public short length;

    /**
     * Смещение ключа от начала записи.
     */
    public short keyOffset;

    /**
     * Младшее слово смещения.
     */
    public int lowOffset;

    /**
     * Старшее слово смещения.
     */
    public int highOffset;

    /**
     * Текстовое значение ключа.
     */
    public String text;
}
