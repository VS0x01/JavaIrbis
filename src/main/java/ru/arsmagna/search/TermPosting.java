// This is an open source non-commercial project. Dear PVS-Studio, please check it.
// PVS-Studio Static Code Analyzer for C, C++ and C#: http://www.viva64.com

package ru.arsmagna.search;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.arsmagna.FastNumber;
import ru.arsmagna.Utility;
import ru.arsmagna.infrastructure.ServerResponse;

import java.util.ArrayList;

/**
 * Постинг терма.
 */
@SuppressWarnings("WeakerAccess")
public final class TermPosting implements Cloneable {

    /**
     * MFN записи с искомым термом.
     */
    public int mfn;

    /**
     * Метка поля с искомым термом.
     */
    public int tag;

    /**
     * Повторение поля.
     */
    public int occurrence;

    /**
     * Количество повторений.
     */
    public int count;

    /**
     * Результат форматирования.
     */
    public String text;

    //=========================================================================

    /**
     * Разбор ответа сервера.
     *
     * @param response Ответ сервера.
     * @return Массив постингов.
     */
    @NotNull
    public static TermPosting[] parse (@NotNull ServerResponse response) {
        ArrayList<TermPosting> result = new ArrayList<>();
        while (true) {
            String line = response.readUtf();
            if (Utility.isNullOrEmpty(line)) {
                break;
            }

            String[] parts = line.split("#", 5);
            if (parts.length < 4) {
                break;
            }

            TermPosting item = new TermPosting();
            item.mfn = FastNumber.parseInt32(parts[0]);
            item.tag = FastNumber.parseInt32(parts[1]);
            item.occurrence = FastNumber.parseInt32(parts[2]);
            item.count = FastNumber.parseInt32(parts[3]);
            if (parts.length > 4) {
                item.text = parts[4];
            }
            result.add(item);
        }

        return result.toArray(new TermPosting[0]);
    }

    /**
     * Клонирование.
     *
     * @return Копию.
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public TermPosting clone() {
        TermPosting result = new TermPosting();
        result.mfn = mfn;
        result.tag = tag;
        result.occurrence = occurrence;
        result.count = count;
        result.text = text;

        return result;
    }

    //=========================================================================

    @Override
    @Contract(pure = true)
    public String toString() {
        return "TermPosting{" +
                "mfn=" + mfn +
                ", tag=" + tag +
                ", occurrence=" + occurrence +
                ", count=" + count +
                ", text='" + text + '\'' +
                '}';
    }
}
