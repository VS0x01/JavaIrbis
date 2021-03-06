// This is an open source non-commercial project. Dear PVS-Studio, please check it.
// PVS-Studio Static Code Analyzer for C, C++ and C#: http://www.viva64.com

package ru.arsmagna;

/**
 * Задает путь к файлам Ирбис.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class IrbisPath {
    /**
     * Общесистемный путь
     */
    public static final int SYSTEM = 0;

    /**
     * Путь размещения сведений о базах данных сервера ИРБИС64
     */
    public static final int DATA = 1;

    /**
     * Путь на мастер-файл базы данных
     */
    public static final int MASTER_FILE = 2;

    /**
     * Путь на словарь базы данных
     */
    public static final int INVERTED_FILE = 3;

    /**
     * путь на параметрию базы данных
     */
    public static final int PARAMETER_FILE = 10;

    /**
     * Полный текст
     */
    public static final int FULL_TEXT = 11;

    /**
     * Внутренний ресурс
     */
    public static final int INTERNAL_RESOURCE = 12;
}
