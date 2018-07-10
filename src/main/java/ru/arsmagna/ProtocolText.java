package ru.arsmagna;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.NotNull;

/**
 * Текстовое представление записи, используемое
 * в протоколе ИРБИС64-сервер.
 */
public final class ProtocolText {
    /**
     * Разделитель текста, используемый ИРБИС64.
     */
    public static final String DELIMITER = "\u001F\u001E";

    //=========================================================================

    /**
     * Encode the subfield.
     *
     * @param builder
     * @param subField
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
    public static void encodeSubField (@NotNull StringBuilder builder, @NotNull SubField subField) {
        builder.append(SubField.DELIMITER);
        builder.append(subField.code);
        builder.append(subField.value);
    }

    /**
     * Encode the field.
     *
     * @param builder
     * @param field
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
    public static void encodeField (@NotNull StringBuilder builder, @NotNull RecordField field) {
        builder.append(field.tag);
        builder.append('#');
        builder.append(field.value);
        for (SubField subField : field.subFields) {
            encodeSubField(builder, subField);
        }
    }

    /**
     * Кодирование записи в коиентское представление.
     *
     * @param record
     * @return Закодированная запись.
     */
    @NotNull
    public static String encodeRecord (@NotNull MarcRecord record) {
        StringBuilder result = new StringBuilder();
        result
                .append(record.mfn)
                .append('#')
                .append(record.status)
                .append(DELIMITER);
        result
                .append(0)
                .append('#')
                .append(record.version)
                .append(DELIMITER);
        for (RecordField field : record.fields) {
            encodeField(result, field);
        }

        return result.toString();
    }
}
