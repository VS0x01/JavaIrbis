// This is an open source non-commercial project. Dear PVS-Studio, please check it.
// PVS-Studio Static Code Analyzer for C, C++ and C#: http://www.viva64.com

package ru.arsmagna.search;

import org.jetbrains.annotations.NotNull;

/**
 * Параметры для команды SearchCommand.
 */
@SuppressWarnings("WeakerAccess")
public final class SearchParameters implements Cloneable {
    /**
     * Имя базы данных.
     */
    public String database;

    /**
     * Номер первой записи.
     */
    public int firstRecord;

    /**
     * Формат.
     */
    public String formatSpecification;

    /**
     * Максимальный MFN.
     */
    public int maxMfn;

    /**
     * Минимальный MFN.
     */
    public int minMfn;

    /**
     * Число записей.
     */
    public int numberOfRecords;

    /**
     * Выражение для поиска по словарю.
     */
    public String searchExpression;

    /**
     * Выражение для последовательного поиска.
     */
    public String sequentialSpecification;

    /**
     * Выражение для локальной фильтрации.
     */
    public String filterSpecification;

    /**
     * Признак кодировки UTF8.
     */
    public boolean uftFormat;

    /**
     * Признак вложенного вызова.
     */
    public boolean nestedCall;

    //=========================================================================

    /**
     * Конструктор по умолчанию.
     */
    public SearchParameters() {
        firstRecord = 1;
    }

    //=========================================================================

    /**
     * Клонирование.
     *
     * @return Копию.
     */
    @NotNull
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public SearchParameters clone() {
        SearchParameters result = new SearchParameters();
        result.database = database;
        result.firstRecord = firstRecord;
        result.formatSpecification = formatSpecification;
        result.maxMfn = maxMfn;
        result.minMfn = minMfn;
        result.numberOfRecords = numberOfRecords;
        result.searchExpression = searchExpression;
        result.sequentialSpecification = sequentialSpecification;
        result.filterSpecification = filterSpecification;
        result.uftFormat = uftFormat;

        return result;
    }

    //=========================================================================
}
