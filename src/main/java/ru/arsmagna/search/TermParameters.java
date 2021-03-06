// This is an open source non-commercial project. Dear PVS-Studio, please check it.
// PVS-Studio Static Code Analyzer for C, C++ and C#: http://www.viva64.com

package ru.arsmagna.search;

import org.jetbrains.annotations.NotNull;

/**
 * Параметры для команды ReadTermsCommand.
 */
public final class TermParameters implements Cloneable {

    /**
     * Имя базы данных.
     */
    public String database;

    /**
     * Число считываемых термов.
     */
    public int numberOfTerms;

    /**
     * Возвращать в обратном порядке.
     */
    public boolean reverseOrder;

    /**
     * Начальный терм.
     */
    public String startTerm;

    /**
     * Формат.
     */
    public String format;

    //=========================================================================

    /**
     * Клонирование.
     *
     * @return Копию.
     */
    @NotNull
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public TermParameters clone() {
        TermParameters result = new TermParameters();
        result.database = database;
        result.numberOfTerms = numberOfTerms;
        result.reverseOrder = reverseOrder;
        result.startTerm = startTerm;
        result.format = format;

        return result;
    }

    //=========================================================================
}
